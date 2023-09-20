package org.processor.translations;

import io.vertx.core.json.JsonObject;

@FunctionalInterface
public interface TranslationFunction {
    String apply(String value, JsonObject parameters, Settings settings);
}
