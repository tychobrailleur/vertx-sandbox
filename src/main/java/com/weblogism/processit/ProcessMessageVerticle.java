package com.weblogism.processit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.asyncsql.AsyncSQLClient;
import io.vertx.ext.asyncsql.PostgreSQLClient;
import io.vertx.ext.sql.SQLConnection;

import java.util.Base64;
import java.util.Date;

public class ProcessMessageVerticle extends AbstractVerticle {

    private Logger log = LoggerFactory.getLogger(ProcessMessageVerticle.class);

    @Override
    public void start(Future<Void> startFuture) {
        log.info("Deploying ProcessMessage verticle...");
        JsonObject config = config();
        final EventBus eventBus = getVertx().eventBus();
        AsyncSQLClient client = PostgreSQLClient.createShared(getVertx(), config);

        eventBus.consumer("processit.incoming", message -> {
            log.debug("Received Message on 'processing.incoming': " + message.body());
            Object body = message.body();
            String decoded = new String(Base64.getDecoder().decode((String) body));
            log.debug("Received message: " + decoded);

            saveEvent(client, decoded);
        });

        startFuture.complete();
    }

    private void saveEvent(AsyncSQLClient client, String event) {

        client.getConnection(connectionResult -> {
            if (connectionResult.succeeded()) {
                SQLConnection connection = connectionResult.result();
                String query = "INSERT INTO events (type, data, date_created) values ('payment', ?, ?)";
                JsonArray params = new JsonArray()
                    .add(event)
                    .add(new Date().toInstant());

                connection.updateWithParams(query, params, updateResultAsyncResult -> {
                    if (updateResultAsyncResult.succeeded()) {
                        log.info("Successfully inserted");
                        Object rs = updateResultAsyncResult.result();
                    } else {
                        log.error("Error inserting event.", updateResultAsyncResult.cause());
                    }
                    connection.close();
                });
            }
        });
    }
}
