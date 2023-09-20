package org.writer;

import org.reader.values.CompositeValue;
import org.reader.values.SimpleValue;

public interface RecordWriter {

    void write(SimpleValue value);

    void write(CompositeValue compositeValue);

    String getResult();
}
