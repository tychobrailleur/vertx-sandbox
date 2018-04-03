package com.weblogism.processit

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory

class ProcessMessageVerticle extends AbstractVerticle {
    def log = LoggerFactory.getLogger(this.class)

    @Override
    void start() {
        JsonObject config = config()
        def rabbitConfig = config.getJsonObject("rabbitmq")

        def eb = vertx.eventBus()
        eb.consumer(rabbitConfig.getString('processor.address'), { message ->
                def body = message.body()
                log.info("Received message on my.message")
                log.info("Message: ${body['body']}")
            })
    }
}
