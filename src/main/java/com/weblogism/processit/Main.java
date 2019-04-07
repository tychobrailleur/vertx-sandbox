package com.weblogism.processit;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Main {
    public static void main(String[] args) {
        final Logger log = LoggerFactory.getLogger(Main.class);
        final Vertx vertx = Vertx.vertx();

        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setConfig(new JsonObject().put("path", "conf/config.json"));

        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions()
            .setType("sys");

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
            .addStore(fileStore)
            .addStore(sysPropsStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.getConfig(json -> {
            if (json.succeeded()) {
                JsonObject config = json.result();
                log.debug("Starting the app with config: " + json);
                vertx.deployVerticle(MainVerticle.class.getName(), new DeploymentOptions().setConfig(config));
                vertx.deployVerticle(EventStorageVerticle.class.getName(), new DeploymentOptions().setConfig(config));
                vertx.deployVerticle(StateManagerVerticle.class.getName(), new DeploymentOptions().setConfig(config));
            } else {
                log.error("Error retrieving configuration.");
            }
        });
    }

}
