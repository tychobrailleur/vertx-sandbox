#!/bin/sh

while ! nc -z rabbitmq 5672; do sleep 3; done
java -jar vertx-sandbox.jar