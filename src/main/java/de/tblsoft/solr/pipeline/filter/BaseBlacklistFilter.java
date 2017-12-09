package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 */
public abstract class BaseBlacklistFilter extends AbstractFilter {
    Map<String, HashSet<String>> topicValues;

    String fieldTopic;
    String fieldValue;

    @Override
    public void init() {
        topicValues = new HashMap<String, HashSet<String>>();
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
                String topic = record.get(0);
                String value = record.get(1);

                if(!topicValues.containsKey(topic)) {
                    topicValues.put(topic, new HashSet<String>());
                }
                topicValues.get(topic).add(value);
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

        fieldTopic = getProperty("fieldTopic", null);
        fieldValue = getProperty("fieldValue", null);

        super.init();
    }

}
