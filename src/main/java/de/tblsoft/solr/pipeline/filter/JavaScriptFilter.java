package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft 30.03.18.
 */
public class JavaScriptFilter extends AbstractFilter {


    private String filename;

    private String script;

    private ScriptEngine engine;

    private CompiledScript compiledScript;

    @Override
    public void init() {
        String internalFilename = getProperty("filename", null);
        verify(internalFilename, "For the JavaScriptFilter a filename property must be defined.");
        filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);
        script = getProperty("script", null);


        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");


        try {
            if(filename != null) {
                script = FileUtils.readFileToString(new File(filename));
            }
            Compilable compEngine = (Compilable) engine;
            compiledScript = compEngine.compile(script);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.init();

    }

    @Override
    public void document(Document document) {
        try {
            engine.put("input", document);
            DocumentBuilder documentBuilder = new DocumentBuilder();
            engine.put("documentBuilder", documentBuilder);

            List<Document> output = new ArrayList<>();
            engine.put("output", output);
            compiledScript.eval();

            for(Document d : output) {
                super.document(d);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void end() {
        super.end();
    }
}
