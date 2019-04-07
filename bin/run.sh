#!/bin/sh

while ! nc -z rabbitmq 5672; do sleep 1; done
java \
   -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory \
   -jar vertx-sandbox.jar
