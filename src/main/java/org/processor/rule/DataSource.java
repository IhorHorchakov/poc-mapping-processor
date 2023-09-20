package org.processor.rule;

import io.vertx.core.json.JsonObject;

import static org.processor.Constants.FROM;
import static org.processor.Constants.INDICATOR;
import static org.processor.Constants.SUB_FIELD;
import static org.processor.Constants.TRANSLATION;

public class DataSource {

    private String tag;
    private String subField;
    private String indicator;
    private String from;
    private Translation translation;

    public DataSource(String tag, JsonObject dataSource) {
        this.tag = tag;
        if (dataSource.containsKey(SUB_FIELD)) {
            this.subField = dataSource.getString(SUB_FIELD);
        } else if (dataSource.containsKey(INDICATOR)) {
            this.indicator = dataSource.getString(INDICATOR);
        }
        this.from = dataSource.getString(FROM);
        if (dataSource.containsKey(TRANSLATION)) {
            this.translation = new Translation(dataSource.getJsonObject(TRANSLATION));
        }
    }

    public String getTag() {
        return this.tag;
    }

    public String getSubField() {
        return subField;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getFrom() {
        return from;
    }

    public Translation getTranslation() {
        return translation;
    }

    public boolean isSubFieldDataSource() {
        return this.subField != null;
    }

    public boolean isIndicatorDataSource() {
        return this.indicator != null;
    }
}
