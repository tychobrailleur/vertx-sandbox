package com.weblogism.vertxsandbox;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class ProcessorVerticle extends Verticle {

    public void start() {
        final Logger log = container.logger();
        System.out.println(" ** ProcessorVerticle " + this + " - thread: " +
                Thread.currentThread() +
                " - classloader: " +
                this.getClass().getClassLoader());
        log.info("Processor Verticle started. " + this);
        log.info("Current thread: " + Thread.currentThread());

        EventBus eb = vertx.eventBus();
        eb.registerHandler("hello.world", new Handler<Message>() {
            public void handle(Message message) {
                //System.out.println(formatStackTrace(Thread.currentThread().getStackTrace()));
                log.info(" ** Handler Current thread: " + Thread.currentThread());
                System.out.println("I received a message "
                                   + message.body()
                                   + " - I am "
                                   + this + " - thread: " +
                                   Thread.currentThread() +
                        " - classloader: " +
                    this.getClass().getClassLoader());
            }
        });
    }

    private String formatStackTrace(StackTraceElement[] stackTrace) {
        StringBuilder buffer = new StringBuilder();
        for (StackTraceElement element : stackTrace) {
            buffer.append(element).append("\n");
        }
        return buffer.toString();
    }
}
