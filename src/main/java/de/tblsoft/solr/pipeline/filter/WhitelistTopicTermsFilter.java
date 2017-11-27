package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Add or override words by topic values defined in an external whitelist
 */
public class WhitelistTopicTermsFilter extends AbstractFilter {
    Map<String, HashMap<String, Document>> topicValues;
    Map<String, HashMap<String, Boolean>> topicsOverriden;

    String fieldTopic;
    String fieldValue;
    boolean override;
    String arrayDelimiter;

    @Override
    public void init() {
        fieldTopic = getProperty("fieldTopic", null);
        fieldValue = getProperty("fieldValue", null);
        override = getPropertyAsBoolean("override", true);
        arrayDelimiter = getProperty("arrayDelimiter", ";");

        topicValues = new HashMap<String, HashMap<String, Document>>();
        topicsOverriden = new HashMap<String, HashMap<String, Boolean>>();
        InputStream in = null;
        try {
            String filename = getProperty("filename", null);
            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

            in = IOUtils.getInputStream(absoluteFilename);
            java.io.Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8.name());

            CSVFormat format = CSVFormat.RFC4180
                                        .withHeader()
                                        .withDelimiter(',');

            CSVParser parser = format.parse(reader);
            Iterator<CSVRecord> csvIterator = parser.iterator();
            while(csvIterator.hasNext()) {
                CSVRecord record = csvIterator.next();
                Map<String, Integer> header = parser.getHeaderMap();

                Document document = new Document();
                for(Map.Entry<String,Integer> entry : header.entrySet()) {
                    String key = entry.getKey();
                    try {
                        String[] values = record.get(key).split(arrayDelimiter);
                        document.addField(key, Arrays.asList(values));
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                String topic = record.get(header.get(fieldTopic));
                String value = record.get(header.get(fieldValue));
                if(!topicValues.containsKey(topic)) {
                    topicValues.put(topic, new HashMap<String, Document>());
                    topicsOverriden.put(topic, new HashMap<String, Boolean>());
                }

                topicValues.get(topic).put(value, document);
                topicsOverriden.get(topic).put(value, false);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        super.init();
    }

    @Override
    public void document(Document document) {
        Field topic = document.getField(fieldTopic);
        Field value = document.getField(fieldValue);

        boolean hasOverriden = false;
        if(override) {
            if(topic != null && value != null) {
                if(topicValues.containsKey(topic.getValue()) &&
                    topicValues.get(topic.getValue()).containsKey(value.getValue())) {
                    Document docOverride = topicValues.get(topic.getValue()).get(value.getValue());
                    for (Field field : docOverride.getFields()) {
                        document.setField(field.getName(), field.getValues());
                    }
                    super.document(document);
                    hasOverriden = true;
                    topicsOverriden.get(topic.getValue()).put(value.getValue(), true);
                }
            }
        }

        if(!hasOverriden) {
            super.document(document);
        }
    }

    @Override
    public void end() {
        // append non overridden values to end
        for (Map.Entry<String, HashMap<String, Document>> entry : topicValues.entrySet()) {
            String topic = entry.getKey();
            for (Map.Entry<String, Document> valueEntry : entry.getValue().entrySet()) {
                String value = valueEntry.getKey();
                Boolean overridden = topicsOverriden.get(topic).get(value);
                if(!overridden) {
                    super.document(valueEntry.getValue());
                }
            }
        }

        super.end();
    }
}
