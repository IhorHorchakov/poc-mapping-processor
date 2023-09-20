package org.writer.impl;

import org.processor.rule.DataSource;
import org.reader.values.CompositeValue;
import org.reader.values.ListValue;
import org.reader.values.SimpleValue;
import org.reader.values.StringValue;
import org.writer.RecordWriter;
import org.writer.fields.RecordControlField;
import org.writer.fields.RecordDataField;

import java.util.Collections;
import java.util.List;

public abstract class AbstractRecordWriter implements RecordWriter {

    @Override
    public void write(SimpleValue simpleValue) {
        DataSource dataSource = simpleValue.getDataSource();
        String tag = dataSource.getTag();
        if (SimpleValue.SubType.STRING.equals(simpleValue.getSubType())) {
            StringValue stringValue = (StringValue) simpleValue;
            if (dataSource.isSubFieldDataSource() || dataSource.isIndicatorDataSource()) {
                RecordDataField recordDataField = buildDataFieldForStringValues(tag, Collections.singletonList(stringValue));
                writeDataField(recordDataField);
            } else {
                RecordControlField recordControlField = new RecordControlField(tag, stringValue.getValue());
                writeControlField(recordControlField);
            }
        } else if (SimpleValue.SubType.LIST_OF_STRING.equals(simpleValue.getSubType())) {
            ListValue listValue = (ListValue) simpleValue;
            if (dataSource.isSubFieldDataSource() || dataSource.isIndicatorDataSource()) {
                RecordDataField recordDataField = buildDataFieldForListOfStrings(listValue);
                writeDataField(recordDataField);
            } else {
                for (String value : listValue.getValue()) {
                    RecordControlField recordControlField = new RecordControlField(tag, value);
                    writeControlField(recordControlField);
                }
            }
        }
    }

    @Override
    public void write(CompositeValue compositeValue) {
        String tag = compositeValue.getValue().get(0).get(0).getDataSource().getTag();
        for (List<StringValue> entry : compositeValue.getValue()) {
            RecordDataField recordDataField = buildDataFieldForStringValues(tag, entry);
            writeDataField(recordDataField);
        }
    }

    protected abstract void writeControlField(RecordControlField recordControlField);

    protected abstract void writeDataField(RecordDataField recordDataField);

    private RecordDataField buildDataFieldForListOfStrings(ListValue listValue) {
        DataSource dataSource = listValue.getDataSource();
        String tag = listValue.getDataSource().getTag();
        RecordDataField field = new RecordDataField(tag);
        for (String stringValue : listValue.getValue()) {
            if (listValue.getDataSource().isSubFieldDataSource()) {
                char subFieldCode = dataSource.getSubField().charAt(0);
                String subFieldData = stringValue;
                field.addSubField(subFieldCode, subFieldData);
            } else if (dataSource.isIndicatorDataSource()) {
                char indicator = stringValue.charAt(0);
                if ("1".equals(dataSource.getIndicator())) {
                    field.setIndicator1(indicator);
                } else if ("2".equals(dataSource.getIndicator())) {
                    field.setIndicator2(indicator);
                }
            }
        }
        return field;
    }

    private RecordDataField buildDataFieldForStringValues(String tag, List<StringValue> entry) {
        RecordDataField field = new RecordDataField(tag);
        for (StringValue stringValue : entry) {
            DataSource dataSource = stringValue.getDataSource();
            if (dataSource.isSubFieldDataSource()) {
                char subFieldCode = dataSource.getSubField().charAt(0);
                String subFieldData = stringValue.getValue();
                if (subFieldData != null) {
                    field.addSubField(subFieldCode, subFieldData);
                }
            } else if (dataSource.isIndicatorDataSource()) {
                char indicator = stringValue.getValue().charAt(0);
                if ("1".equals(dataSource.getIndicator())) {
                    field.setIndicator1(indicator);
                } else if ("2".equals(dataSource.getIndicator())) {
                    field.setIndicator2(indicator);
                }
            }
        }
        return field;
    }

}
