package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.jsoup.parser.Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.graalvm.polyglot.*;


public class RichJavaScriptFilter extends AbstractFilter {
    private String filename;

    private String script;

    private Source compiledScript;

    private Context context;

    Parser htmlParser;
    Parser xmlParser;

    @Override
    public void init() {
        String internalFilename = getProperty("filename", null);
        script = getProperty("script", null);

        context = Context.newBuilder("js")
                    .allowAllAccess(true)
                    .build();

        htmlParser = Parser.htmlParser();
        xmlParser = Parser.xmlParser();
        try {
            if(internalFilename != null) {
                filename = IOUtils.getAbsoluteFile(getBaseDir(),internalFilename);
                script = FileUtils.readFileToString(new File(filename));
            }
            compiledScript = Source.newBuilder("js", script, "RichJavaScriptFilter").build();
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
            // Expose the helper function
            Value bindings = context.getBindings("js");
            bindings.putMember("htmlParser", htmlParser);
            bindings.putMember("xmlParser", xmlParser);
            bindings.putMember("docs", docs);
            bindings.putMember("documentBuilder", new DocumentBuilder());
            Value result = context.eval(compiledScript);
            // Print the result
//            System.out.println(result.asString());

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
