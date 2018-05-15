package de.tblsoft.solr.pipeline;


import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.jsfr.json.JsonPathListener;
import org.jsfr.json.JsonSurfer;
import org.jsfr.json.JsonSurferGson;
import org.jsfr.json.ParsingContext;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Read Json Files from File or Url
 */
public class JsonReader extends AbstractReader {


    private ScriptEngine engine;

    private String script;

    private String filename;

    private Context cx;

    private String javaScriptFilename;

    private String  rootPath;

    public void read() {

        try {

            ScriptEngineManager mgr = new ScriptEngineManager();
            engine = mgr.getEngineByName("JavaScript");


            String internalFilename = getProperty("filename", null);
            filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);

            String internalJavaScriptFilename = getProperty("javaScriptFilename", null);
            javaScriptFilename = IOUtils.getAbsoluteFile(getBaseDir(),internalJavaScriptFilename);

            rootPath = getProperty("rootPath", "$");

            script = FileUtils.readFileToString(new File(javaScriptFilename));
            cx = Context.enter();

            JsonSurfer surfer = JsonSurferGson.INSTANCE;

            java.io.Reader sample = new FileReader(filename);

            surfer.configBuilder()
                    .bind(rootPath, new JsonPathListener() {
                        public void onValue(Object value, ParsingContext context) {
                            Scriptable scope = cx.initStandardObjects();
                            List<Document> output = new ArrayList<Document>();
                            ScriptableObject.putProperty(scope, "documentBuilder", Context.javaToJS(new DocumentBuilder(), scope));
                            ScriptableObject.putProperty(scope, "output", Context.javaToJS(output, scope));

                            String exec = "var input = " + value.toString() + ";" + script;
                            cx.evaluateString(scope, exec, filename, 1, null);

                            for(Document out: output) {
                                executer.document(out);
                            }
                        }
                    })
                    .buildAndSurf(sample);

            sample.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void setPipelineExecuter(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void end() {
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }
}
