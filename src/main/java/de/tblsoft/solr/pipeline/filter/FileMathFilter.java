package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Run Math operations from file. This is useful for boosting
 */
public class FileMathFilter extends AbstractFilter {
    public class AggregationId {
        List<String> fields = new ArrayList<String>();

        public AggregationId(List<String> fields) {
            this.fields = fields;
        }

        public String buildId(Document document) {
            String id = "";

            for (String field : fields) {
                String fieldValue = document.getFieldValue(field);
                if(StringUtils.isEmpty(fieldValue)) {
                    break;
                }
                else {
                    id+="."+fieldValue;
                }
            }
            if(StringUtils.isEmpty(id)) {
                id = null;
            }

            return id;
        }
    }

    String filepath;
    AggregationId idField;
    String field1;
    String field2;
    boolean field1FromFile;
    String operator;
    String fieldResult;

    Map<String, Document> fileData;

    @Override
    public void init() {
        filepath = getProperty("filepath", null);
        verify(filepath, "A filepath must be defined!");
        String absolutePath = IOUtils.getAbsoluteFile(getBaseDir(), filepath);

        String aggId = getProperty("idField", null);
        verify(aggId, "A idField must be defined!");
        String[] fields = aggId.split(",");
        idField = new AggregationId(Arrays.asList(fields));

        fileData = new HashMap<>();
        List<Map<String, String>> csvData = loadCsvAsMap(absolutePath);
        for (Map<String, String> entry : csvData) {
            Document doc = new Document();
            for (Map.Entry<String, String> entrySet : entry.entrySet()) {
                doc.setField(entrySet.getKey(), entrySet.getValue());
            }
            String docId = idField.buildId(doc);
            fileData.put(docId, doc);
        }

        field1 = getProperty("field1", null);
        verify(field1, "A field1 must be defined!");

        field2 = getProperty("field2", null);
        verify(field2, "A field2 must be defined!");

        fieldResult = getProperty("fieldResult", null);
        verify(fieldResult, "A fieldResult must be defined!");

        field1FromFile = getPropertyAsBoolean("field1FromFile", true);
        operator = getProperty("operator", "*");

        super.init();
    }

    public static List<Map<String, String>> loadCsvAsMap(String absolutePath) {
        List<Map<String, String>> result = new ArrayList<>();
        InputStream in = null;
        try {
            in = IOUtils.getInputStream(absolutePath);
            java.io.Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8.name());

            CSVFormat format = CSVFormat.RFC4180
                    .withHeader()
                    .withDelimiter(',');

            CSVParser parser = format.parse(reader);
            Iterator<CSVRecord> csvIterator = parser.iterator();
            while(csvIterator.hasNext()) {
                CSVRecord record = csvIterator.next();
                result.add(record.toMap());
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

        return result;
    }

    @Override
    public void document(Document document) {
        String docId = idField.buildId(document);
        if(docId != null && fileData.containsKey(docId)) {
            Document docFile = fileData.get(docId);
            Float result = calculate(docFile, document);
            document.setField(fieldResult, result.toString());
        }

        super.document(document);
    }

    public Float calculate(Document docFile, Document doc) {
        Float result = null;

        Float val1;
        Float val2;
        if (field1FromFile) {
            val1 = Float.parseFloat(docFile.getFieldValue(field1));
            val2 = Float.parseFloat(doc.getFieldValue(field2));
        } else {
            val1 = Float.parseFloat(doc.getFieldValue(field1));
            val2 = Float.parseFloat(docFile.getFieldValue(field2));
        }

        if ("+".equals(operator)) {
            result = val1 + val2;
        } else if ("-".equals(operator)) {
            result = val1 - val2;
        } else if ("*".equals(operator)) {
            result = val1 * val2;
        } else if ("/".equals(operator)) {
            result = val1 / val2;
        }

        return result;
    }
}
