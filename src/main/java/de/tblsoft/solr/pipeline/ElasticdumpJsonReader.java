package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.tblsoft.solr.pipeline.bean.Document;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Read Elasticdump JSON export file
 */
public class ElasticdumpJsonReader extends AbstractReader {
    private String filepath;

    public void read() {
        filepath = getProperty("filepath", null);
        if(Strings.isNullOrEmpty(filepath)) {
            throw new RuntimeException("For the ElasticdumpJsonReader a filepath property must be defined!");
        }

        try {
            parseJsonFile(filepath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void parseJsonFile(String filepath) throws IOException {
        List<String> jsonLines = readFileAsLines(filepath);
        for (String jsonLine : jsonLines) {
            Document doc = parseJsonLineAsDoc(jsonLine);
            executer.document(doc);
        }
    }

    protected static List<String> readFileAsLines(String filepath) throws IOException {
        List<String> jsonLines = new ArrayList<String>();

        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filepath);
            br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                jsonLines.add(line);
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

        return jsonLines;
    }

    protected static Document parseJsonLineAsDoc(String jsonLine) {
        DocumentContext context = JsonPath.parse(jsonLine);

        Document doc = new Document();
        doc.setField("_index", (String) readJsonValueOrNull(context, "$['_index']"));
        doc.setField("_type", (String) readJsonValueOrNull(context, "$['_type']"));
        doc.setField("_id", (String) readJsonValueOrNull(context, "$['_id']"));

        Map<String, Object> source = readJsonValueOrNull(context, "$['_source']");
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
