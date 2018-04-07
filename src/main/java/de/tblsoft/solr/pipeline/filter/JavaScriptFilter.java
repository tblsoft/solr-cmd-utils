package de.tblsoft.solr.pipeline.filter;

import com.google.gson.Gson;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
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

    private Scriptable scope;
    private Context cx;

    @Override
    public void init() {
        String internalFilename = getProperty("filename", null);
        verify(internalFilename, "For the JavaScriptFilter a filename property must be defined.");
        filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);


        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");
        cx = Context.enter();
        scope = cx.initStandardObjects();
        ScriptableObject.putProperty(scope, "documentBuilder", Context.javaToJS(new DocumentBuilder(), scope));


        try {
            script = FileUtils.readFileToString(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.init();

    }

    @Override
    public void document(Document document) {




        List<Document> output = new ArrayList<Document>();
        ScriptableObject.putProperty(scope, "input", Context.javaToJS(document, scope));
        ScriptableObject.putProperty(scope, "output", Context.javaToJS(output, scope));

        cx.evaluateString(scope, script, filename, 1, null);


        for(Document out:output) {
            super.document(out);
        }
    }

    @Override
    public void end() {
        super.end();
    }
}
