package de.tblsoft.solr.pipeline.filter;

import com.google.gson.Gson;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import sun.org.mozilla.javascript.internal.NativeArray;
import sun.org.mozilla.javascript.internal.NativeObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft 30.03.18.
 */
public class JavaScriptFilter extends AbstractFilter {


    private String filename;

    private String script;

    private ScriptEngine engine;

    private Gson gson = new Gson();

    @Override
    public void init() {
        String internalFilename = getProperty("filename", null);
        verify(internalFilename, "For the JavaScriptFilter a filename property must be defined.");
        filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);


        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");

        try {
            script = FileUtils.readFileToString(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }

    @Override
    public void document(Document document) {
        String input = gson.toJson(document);
        String function = "input = " + input + "; " + script;
        Object result = null;
        try {
            result = engine.eval(function);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }

        List<Document> documents = mapResult(result);
        for(Document doc : documents) {
            super.document(doc);
        }

    }


    List<Document> mapResult(Object object) {
        List<Document> documents = new ArrayList<Document>();
        if(object instanceof NativeArray) {
            NativeArray array = (NativeArray) object;
            for (int i = 0; i < array.getLength(); i++) {
                NativeObject nativeObject = (NativeObject) array.get(i);
                documents.add(mapDocument(nativeObject));

            }
        } else {
            documents.add(mapDocument((NativeObject) object));
        }

        return documents;
    }

    Document mapDocument(NativeObject object) {
        Document document = new Document();
        NativeArray fields = (NativeArray) object.get("fields");
        for (int i = 0; i < fields.getLength(); i++) {
            NativeObject field = (NativeObject) fields.get(i);

            String name = (String) field.get("name");
            NativeArray values = (NativeArray) field.get("values");
            for (int j = 0; j < values.getLength(); j++) {
                String value = (String) values.get(j);
                document.addField(name, value);

            }
        }

        return document;
    }



}
