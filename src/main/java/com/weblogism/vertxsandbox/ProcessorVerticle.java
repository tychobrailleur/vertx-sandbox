package com.weblogism.vertxsandbox;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class ProcessorVerticle extends Verticle {

    public void start() {
        final Logger log = container.logger();
        log.info("Processor Verticle started. " + this);
        log.info("Current thread: " + Thread.currentThread());

        EventBus eb = vertx.eventBus();
        eb.registerHandler("hello.world", new Handler<Message>() {
            public void handle(Message message) {
                log.info(" ** Handler Current thread: " + Thread.currentThread());
                System.out.println("I received a message "
                                   + message.body()
                                   + " - I am "
                                   + this + " - thread: " +
                                   Thread.currentThread());
            }
        });
    }
}
