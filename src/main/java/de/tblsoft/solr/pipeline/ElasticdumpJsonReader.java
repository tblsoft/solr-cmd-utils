package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.tblsoft.solr.pipeline.bean.Document;

import java.io.*;
import java.util.ArrayList;
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
            List<Document> docs = parseJsonFile(filepath);
            for (Document doc : docs) {
                executer.document(doc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected static List<Document> parseJsonFile(String filepath) throws IOException {
        List<Document> docs = new ArrayList<Document>();

        List<String> jsonLines = readFileAsLines(filepath);
        for (String jsonLine : jsonLines) {
            Document doc = parseJsonLineAsDoc(jsonLine);
            docs.add(doc);
        }

        return docs;
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
        if(source != null) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
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
}
