package com.weblogism.processit

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.rabbitmq.RabbitMQClient

class MainVerticle extends AbstractVerticle {

    def log = LoggerFactory.getLogger(this.class)

    @Override
    void start(Future<Void> future) {
        log.info('Starting...')

        JsonObject config = config()
        def rabbitConfig = config.getJsonObject("rabbitmq")

        def mqConfig = [:]

        mqConfig.user = "guest"
        mqConfig.password = "guest"
        mqConfig.host = rabbitConfig.getString("hostname")
        mqConfig.port = rabbitConfig.getInteger("port")
        mqConfig.connectionTimeout = 6000
        mqConfig.requestedHeartbeat = 60
        mqConfig.handshakeTimeout = 6000
        mqConfig.requestedChannelMax = 5
        mqConfig.networkRecoveryInterval = 500
        mqConfig.automaticRecoveryEnabled = true

        def client = RabbitMQClient.create(vertx, mqConfig)
        client.start({ v ->
                client.basicConsume(rabbitConfig.getString("queue"),
                                    rabbitConfig.getString("processor.address"),
                                    { consumeResult ->
                        if (consumeResult.succeeded()) {
                            log.debug("RabbitMQ consumer created!")
                        } else {
                            consumeResult.cause().printStackTrace()
                        }
                    })

            })
    }
}
