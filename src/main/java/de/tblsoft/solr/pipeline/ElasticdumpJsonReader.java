package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Read Elasticdump JSON export file
 */
public class ElasticdumpJsonReader extends AbstractReader {
    private String filepath;
    private String format; // elasticdump (default) or bulk
    private String jsonPathRoot;

    public void read() {
        filepath = getProperty("filepath", null);
        if(Strings.isNullOrEmpty(filepath)) {
            throw new RuntimeException("For the ElasticdumpJsonReader a filepath property must be defined!");
        }
        format = getProperty("format", "elasticdump");
        jsonPathRoot = "elasticdump".equals(format) ? "$['_source']" : "$";

        try {
            readFileAsLines(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void readFileAsLines(String filepath) throws IOException {
        BufferedReader br = null;
        FileReader fr = null;
        try {
            File jsonFile = IOUtils.getAbsoluteFileAsFile(getBaseDir(), filepath);
            fr = new FileReader(jsonFile);
            br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                Document doc = parseJsonLineAsDoc(line, jsonPathRoot);
                if("bulk".equals(format)) {
                    String lineSource = br.readLine();
                    Document docSource = parseJsonLineAsDoc(lineSource, jsonPathRoot);
                    docSource.setField("_index", doc.getFieldValue("_index"));
                    docSource.setField("_type", doc.getFieldValue("_type"));
                    docSource.setField("_id", doc.getFieldValue("_id"));
                    doc = docSource;
                }
                executer.document(doc);
            }
        } catch (FileNotFoundException e) {
            throw new IOException(e);
        } catch (IOException e) {
            throw new IOException(e);
        } finally {
            try {
                if(br != null) {
                    br.close();
                }
                if(fr != null) {
                    br.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    protected static Document parseJsonLineAsDoc(String jsonLine, String jsonSourcePath) {
        DocumentContext context = JsonPath.parse(jsonLine);

        Document doc = new Document();
        doc.setField("_index", (String) readJsonValueOrNull(context, "$['_index']"));
        doc.setField("_type", (String) readJsonValueOrNull(context, "$['_type']"));
        doc.setField("_id", (String) readJsonValueOrNull(context, "$['_id']"));

        Map<String, Object> source = readJsonValueOrNull(context, jsonSourcePath);
        Map<String, Object> flatSource = flatSourceToDocument(source);
        if(flatSource != null) {
            for (Map.Entry<String, Object> entry : flatSource.entrySet()) {
                doc.addField(entry.getKey(), entry.getValue());
            }
        }


        return doc;
    }

    protected static <T> T readJsonValueOrNull(DocumentContext context, String jsonPath) {
        T result = null;
        try {
            result = context.read(jsonPath);
        }
        catch (RuntimeException ignored) {
        }

        return result;
    }

    protected static Map<String, Object> flatSourceToDocument(Map<String, Object> source) {
        Map<String, Object> document = new HashMap<>();

        if(source != null) {
            document = new HashMap<>();
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                Map<String, Object> fields = flatValueToFields(entry.getKey(), entry.getValue());
                document.putAll(fields);
            }
        }

        return document;
    }

    protected static Map<String, Object> flatValueToFields(String field, Object value) {
        Map<String, Object> fields = new HashMap<>();

        if(value instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                String key = field+"."+entry.getKey();
                Map<String, Object> map = flatValueToFields(key, entry.getValue());
                fields.putAll(map);
            }
        } else {
            fields.put(field, value);
        }

        return fields;
    }
}
