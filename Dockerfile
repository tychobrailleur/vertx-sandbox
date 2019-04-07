FROM openjdk:8-jdk-slim

COPY bin/run.sh /run.sh
RUN apt-get update && apt-get install -y netcat && chmod +x /run.sh

ADD target/vertx-sandbox.jar vertx-sandbox.jar

CMD ["/run.sh"]
