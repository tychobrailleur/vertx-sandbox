package com.weblogism.processit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rabbitmq.QueueOptions;
import io.vertx.rabbitmq.RabbitMQClient;
import io.vertx.rabbitmq.RabbitMQConsumer;
import io.vertx.rabbitmq.RabbitMQOptions;

import java.util.Base64;


public class MainVerticle extends AbstractVerticle {

    private Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        log.info("Starting...");

        JsonObject config = config();
        final JsonObject rabbitConfig = config.getJsonObject("rabbitmq");

        RabbitMQOptions mqConfig = new RabbitMQOptions();

        mqConfig.setUser("guest")
            .setPassword("guest")
            .setHost(rabbitConfig.getString("hostname"))
            .setPort(rabbitConfig.getInteger("port"))
            .setConnectionTimeout(6000)
            .setRequestedHeartbeat(60)
            .setHandshakeTimeout(6000)
            .setRequestedChannelMax(5)
            .setNetworkRecoveryInterval(500)
            .setAutomaticRecoveryEnabled(true);

        final RabbitMQClient client = RabbitMQClient.create(vertx, mqConfig);
        final JsonObject queueConfig = new JsonObject();

        client.start(voidAsyncResult -> {
            if (voidAsyncResult.succeeded()) {
                client.queueDeclare(rabbitConfig.getString("queue"),
                    true, // durable
                    false, // exclusive
                    false, // autoDelete
                    queueConfig,
                    jsonObjectAsyncResult -> {
                        if (jsonObjectAsyncResult.succeeded()) {
                            processIncomingMessages(client, rabbitConfig);
                        } else {
                            log.error("Could not declare queue.", jsonObjectAsyncResult.cause());
                        }
                    });
            } else {
                log.error("Error starting rabbitmq client.", voidAsyncResult.cause());
            }

            startFuture.complete();
        });
    }

    private void processIncomingMessages(RabbitMQClient client, final JsonObject rabbitConfig) {
        final EventBus eventBus = vertx.eventBus();
        QueueOptions queueOptions = new QueueOptions();
        client.basicConsumer(rabbitConfig.getString("queue"),
            queueOptions,
            rabbitMQConsumerAsyncResult -> {
                if (rabbitMQConsumerAsyncResult.succeeded()) {
                    RabbitMQConsumer mqConsumer = rabbitMQConsumerAsyncResult.result();
                    mqConsumer.handler(rabbitMQMessage -> {
                        if (log.isDebugEnabled()) {
                            log.debug("Received rabbit message: " + rabbitMQMessage.body().toString());
                        }
                        eventBus.publish("processit.incoming",
                            new String(Base64.getDecoder().decode(rabbitMQMessage.body().toString())));
                    });
                } else {
                    log.error("Error creating RabbitMQ consumer.", rabbitMQConsumerAsyncResult.cause());
                }
            });
    }
}
