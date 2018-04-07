package com.weblogism.processit

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.json.JsonArray
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.jdbc.JDBCClient


class ProcessMessageVerticle extends AbstractVerticle {
    def log = LoggerFactory.getLogger(this.class)

    @Override
    void start() {
        JsonObject config = config()
        def rabbitConfig = config.getJsonObject("rabbitmq")
        def client = JDBCClient.createShared(vertx, config)

        def eb = vertx.eventBus()
        String busAddress = rabbitConfig.getString('processor.address')
        eb.consumer(busAddress, { message ->
                def body = message.body()
                def decoded = new String(body['body']?.decodeBase64())
                log.info("Received message on ${busAddress}")
                log.debug("Message: ${decoded}")

                client.getConnection({ res ->
                        if (res.succeeded()) {
                            def connection = res.result()

                            String query = "INSERT INTO events (type, data, date_created) values ('payment', ?, ?)"
                            JsonArray params = new JsonArray().add(decoded).add(new Date().toInstant())

                            connection.updateWithParams(query, params, { res2 ->
                                    if (res2.succeeded()) {
                                        def rs = res2.result()
                                    }
                                })
                        }
                    })
            })
    }
}
