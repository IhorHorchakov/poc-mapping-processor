package org.reader.values;

public interface RuleValue<T> {
    T getValue();

    Type getType();

    enum Type {
        MISSING,
        SIMPLE,
        COMPOSITE
    }
}
