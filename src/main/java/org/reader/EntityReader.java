package org.reader;

import org.processor.rule.Rule;
import org.reader.values.RuleValue;

public interface EntityReader {

    RuleValue read(Rule rule);
}
