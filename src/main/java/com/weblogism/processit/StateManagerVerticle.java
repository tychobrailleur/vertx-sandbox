package com.weblogism.processit;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class StateManagerVerticle extends AbstractVerticle {

    private final Logger log = LoggerFactory.getLogger(StateManagerVerticle.class);

    private class State {
        long balance;
    }

    // Only one thread accesses state.
    private final Map<String, State> state = new HashMap<>();

    public void start(Future<Void> future) {
        final EventBus eventBus = vertx.eventBus();
        eventBus.<String>consumer("processit.incoming", event -> {
            final JsonObject jsonEvent = new JsonObject(event.body());
            String merchantId = jsonEvent.getString("merchant_id");

            State merchantState = state.getOrDefault(merchantId, new State());
            merchantState.balance += jsonEvent.getInteger("amount");
            log.info("Merchant state: " + merchantId + " balance: " + merchantState.balance);
            state.put(merchantId, merchantState);
        });

        eventBus.<JsonObject>consumer("processit.state", event -> {
            final String merchantId = event.body().getString("merchant_id");
            final String correlationId = event.body().getString("correlation_id");

            State s = state.getOrDefault(merchantId, new State());

            JsonObject response = new JsonObject();
            response.put("merchant_id", merchantId);
            response.put("correlation_id", correlationId);
            response.put("balance", s.balance);

            event.reply(response);

        });

    }
}
