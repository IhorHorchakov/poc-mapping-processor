package org.processor;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.processor.rule.Rule;
import org.processor.rule.Translation;
import org.processor.translations.Settings;
import org.processor.translations.TranslationFunction;
import org.processor.translations.TranslationsHolder;
import org.reader.EntityReader;
import org.reader.values.CompositeValue;
import org.reader.values.ListValue;
import org.reader.values.RuleValue;
import org.reader.values.SimpleValue;
import org.reader.values.StringValue;
import org.writer.RecordWriter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class RuleProcessor {
    private Settings settings;
    private JsonArray rules;

    public RuleProcessor(JsonArray rules) {
        this.rules = rules;
    }

    public String process(EntityReader reader, RecordWriter writer) {
        Iterator ruleIterator = rules.iterator();
        while (ruleIterator.hasNext()) {
            Rule rule = new Rule(JsonObject.mapFrom(ruleIterator.next()));
            RuleValue ruleValue = reader.read(rule);
            switch (ruleValue.getType()) {
                case SIMPLE:
                    SimpleValue simpleValue = (SimpleValue) ruleValue;
                    translate(simpleValue);
                    writer.write(simpleValue);
                    break;
                case COMPOSITE:
                    CompositeValue compositeValue = (CompositeValue) ruleValue;
                    translate(compositeValue);
                    writer.write(compositeValue);
                    break;
                case MISSING:
            }
        }

        return writer.getResult();
    }

    private void translate(SimpleValue simpleValue) {
        Translation translation = simpleValue.getDataSource().getTranslation();
        if (translation != null) {
            TranslationFunction translationFunction = TranslationsHolder.lookup(translation);
            if (SimpleValue.SubType.STRING.equals(simpleValue.getSubType())) {
                StringValue stringValue = (StringValue) simpleValue;
                String readValue = stringValue.getValue();
                String translatedValue = translationFunction.apply(readValue, translation.getParameters(), settings);
                stringValue.setValue(translatedValue);
            } else if (SimpleValue.SubType.LIST_OF_STRING.equals(simpleValue.getSubType())) {
                ListValue listValue = (ListValue) simpleValue;
                List<String> translatedValues = new ArrayList<>();
                for (String readValue : listValue.getValue()) {
                    String translatedValue = translationFunction.apply(readValue, translation.getParameters(), settings);
                    translatedValues.add(translatedValue);
                }
                listValue.setValue(translatedValues);
            }
        }
    }

    private void translate(CompositeValue compositeValue) {
        List<List<StringValue>> readValues = compositeValue.getValue();
        for (List<StringValue> readEntry : readValues) {
            readEntry.forEach(stringValue -> {
                Translation translation = stringValue.getDataSource().getTranslation();
                if (translation != null) {
                    TranslationFunction translationFunction = TranslationsHolder.lookup(translation);
                    String readValue = stringValue.getValue();
                    String translatedValue = translationFunction.apply(readValue, translation.getParameters(), settings);
                    stringValue.setValue(translatedValue);
                }
            });
        }
    }


}