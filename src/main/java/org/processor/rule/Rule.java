package org.processor.rule;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static org.processor.Constants.DATA_SOURCE;
import static org.processor.Constants.TAG;


public class Rule {
    private List<DataSource> dataSources = new ArrayList<>();

    public Rule(JsonObject rule) {
        String tag = rule.getString(TAG);
        JsonArray mapping = rule.getJsonArray(DATA_SOURCE);
        if (mapping.isEmpty()) {
            throw new IllegalArgumentException(String.format("Given rule does not have dataSource, rule : %s", rule));
        } else {
            mapping.forEach(item -> this.dataSources.add(new DataSource(tag, (JsonObject) item)));
        }
    }

    public List<DataSource> getDataSources() {
        return dataSources;
    }
}
