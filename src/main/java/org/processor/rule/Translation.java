package org.processor.rule;

import io.vertx.core.json.JsonObject;

import static org.processor.Constants.FUNCTION;
import static org.processor.Constants.PARAMETERS;

public class Translation {
    private String function;
    private JsonObject parameters;

    public Translation(JsonObject translation) {
        this.function = translation.getString(FUNCTION);
        if (translation.containsKey(PARAMETERS)) {
            this.parameters = translation.getJsonObject(PARAMETERS);
        }
    }

    public String getFunction() {
        return function;
    }

    public JsonObject getParameters() {
        return parameters;
    }
}
