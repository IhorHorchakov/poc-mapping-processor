package org.reader;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import io.vertx.core.json.JsonObject;
import net.minidev.json.JSONArray;
import org.processor.rule.DataSource;
import org.processor.rule.Rule;
import org.reader.values.CompositeValue;
import org.reader.values.MissingValue;
import org.reader.values.RuleValue;
import org.reader.values.SimpleValue;
import org.reader.values.StringValue;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class JPathSyntaxEntityReader extends AbstractEntityReader {
    private final DocumentContext documentContext;

    public JPathSyntaxEntityReader(JsonObject entity) {
        this.documentContext = JsonPath.parse(
                entity.encode(),
                Configuration.defaultConfiguration()
                        .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL)
                        .addOptions(Option.SUPPRESS_EXCEPTIONS)
        );
    }

    @Override
    protected RuleValue readCompositeValue(Rule rule) {
        List<SimpleEntry<DataSource, JSONArray>> matrix = new ArrayList<>();
        for (DataSource dataSource : rule.getDataSources()) {
            if (dataSource.getFrom() == null) {
                matrix.add(new SimpleEntry<>(dataSource, new JSONArray()));
            } else {
                Object objectValue = this.documentContext.read(dataSource.getFrom());
                if (objectValue instanceof String) {
                    String stringValue = (String) objectValue;
                    matrix.add(new SimpleEntry<>(dataSource, new JSONArray().appendElement(stringValue)));
                } else if (objectValue instanceof JSONArray) {
                    JSONArray value = (JSONArray) objectValue;
                    matrix.add(new SimpleEntry<>(dataSource, value));
                }
            }
        }
        int matrixLength = matrix.size();
        int matrixWidth = matrix.get(0).getValue().size();
        if (matrixWidth == 0) {
            return MissingValue.getInstance();
        } else {
            CompositeValue compositeValue = new CompositeValue();
            for (int widthIndex = 0; widthIndex < matrixWidth; widthIndex++) {
                List<StringValue> entry = new ArrayList<>();
                for (int lengthIndex = 0; lengthIndex < matrixLength; lengthIndex++) {
                    SimpleEntry<DataSource, JSONArray> field = matrix.get(lengthIndex);
                    JSONArray jsonArray = field.getValue();
                    if (jsonArray.isEmpty()) {
                        entry.add(SimpleValue.ofNullable(field.getKey()));
                    } else {
                        entry.add(SimpleValue.of((String) jsonArray.get(widthIndex), field.getKey()));
                    }
                }
                compositeValue.addEntry(entry);
            }
            return compositeValue;
        }
    }

    @Override
    protected RuleValue readSimpleValue(DataSource dataSource) {
        String path = dataSource.getFrom();
        Object readValue = documentContext.read(path);
        if (readValue instanceof String) {
            String string = (String) readValue;
            return SimpleValue.of(string, dataSource);
        } else if (readValue instanceof JSONArray) {
            JSONArray array = (JSONArray) readValue;
            if (array.isEmpty()) {
                return MissingValue.getInstance();
            }
            if (array.get(0) instanceof String) {
                List<String> listOfStrings = new ArrayList<>();
                array.forEach(arrayItem -> listOfStrings.add(arrayItem.toString()));
                return SimpleValue.of(listOfStrings, dataSource);
            } else if (array.get(0) instanceof Map) {
                throw new IllegalArgumentException(format("Reading a list of complex fields is not supported, data source: %s", dataSource));
            }
        } else if (readValue instanceof Map) {
            throw new IllegalArgumentException(format("Reading a complex field is not supported, data source: %s", dataSource));
        }
        return MissingValue.getInstance();
    }
}
