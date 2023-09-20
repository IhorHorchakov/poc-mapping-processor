package org;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.IOUtils;
import org.processor.RuleProcessor;
import org.reader.EntityReader;
import org.reader.JPathSyntaxEntityReader;
import org.writer.RecordWriter;
import org.writer.impl.JsonRecordWriter;
import org.writer.impl.MarcRecordWriter;
import org.writer.impl.XmlRecordWriter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.FileReader;
import java.io.IOException;

@RunWith(JUnit4.class)
public class ProcessorTest {
    private static JsonObject entity;
    private static JsonArray rules;

    @BeforeClass
    public static void setup() throws IOException {
        entity = new JsonObject(IOUtils.toString(new FileReader("src/test/resources/given_inventory_instance.json")));
        rules = new JsonArray(IOUtils.toString(new FileReader("src/test/resources/rules/rules.json")));
    }

    @Test
    public void shouldMapEntityTo_MarcRecord() throws IOException {
        // given
        RuleProcessor ruleProcessor = new RuleProcessor(rules);
        EntityReader reader = new JPathSyntaxEntityReader(entity);
        RecordWriter writer = new MarcRecordWriter();
        // when
        String actualMarcRecord = ruleProcessor.process(reader, writer);
        // then
        String expectedMarcRecord = IOUtils.toString(new FileReader("src/test/resources/records/expected_marc_record.mrc"));
        Assert.assertEquals(expectedMarcRecord, actualMarcRecord);
    }

    @Test
    public void shouldMapEntityTo_JsonRecord() throws IOException {
        // given
        RuleProcessor ruleProcessor = new RuleProcessor(rules);
        EntityReader reader = new JPathSyntaxEntityReader(entity);
        RecordWriter writer = new JsonRecordWriter();
        // when
        String actualJsonRecord = ruleProcessor.process(reader, writer);
        // then
        String expectedJsonRecord = IOUtils.toString(new FileReader("src/test/resources/records/expected_json_record.json"));
        Assert.assertEquals(expectedJsonRecord, actualJsonRecord);
    }

    @Test
    public void shouldMapEntityTo_XmlRecord() throws IOException {
        // given
        RuleProcessor ruleProcessor = new RuleProcessor(rules);
        EntityReader reader = new JPathSyntaxEntityReader(entity);
        RecordWriter writer = new XmlRecordWriter();
        // when
        String actualXmlRecord = ruleProcessor.process(reader, writer);
        // then
        String expectedXmlRecord = IOUtils.toString(new FileReader("src/test/resources/records/expected_xml_record.xml"));
        Assert.assertEquals(expectedXmlRecord, actualXmlRecord);
    }
}