package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.parser.Parser;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class RichJavaScriptFilter extends AbstractFilter {
    private String filename;

    private String script;

    private ScriptEngine engine;

    private CompiledScript compiledScript;

    Parser htmlParser;
    Parser xmlParser;

    @Override
    public void init() {
        String internalFilename = getProperty("filename", null);
        script = getProperty("script", null);

        ScriptEngineManager mgr = new ScriptEngineManager();
        engine = mgr.getEngineByName("JavaScript");

        htmlParser = Parser.htmlParser();
        xmlParser = Parser.xmlParser();
        try {
            if(internalFilename != null) {
                filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);
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
        List<Document> docs = new ArrayList<>();
        docs.add(document);
        try {
            engine.put("htmlParser", htmlParser);
            engine.put("xmlParser", xmlParser);
            engine.put("docs", docs);
            engine.put("documentBuilder", new DocumentBuilder());
            compiledScript.eval();

            for(Document d : docs) {
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
