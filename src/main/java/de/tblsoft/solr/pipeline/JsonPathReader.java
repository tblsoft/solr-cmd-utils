package de.tblsoft.solr.pipeline;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Read Json Files from File or Url
 */
public class JsonPathReader extends AbstractReader {

    private Map<String, List<String>> mapping;
    private String rootPath;

    public void read() {
        SimpleMapping simpleMapping = new SimpleMapping(getPropertyAsList("mapping", new ArrayList<String>()), getPropertyAsList("config", new ArrayList<String>()));
        mapping = simpleMapping.getMapping();
        try {
            rootPath = getProperty("rootPath", "$");
            String url = getProperty("url", null);
            String path = getProperty("path", null);
            String filename = getProperty("filename", null);

            if(!Strings.isNullOrEmpty(url)) {
                DocumentContext context = loadJsonContextFromUrl(url);
                execute(context, url);
            } else if(!Strings.isNullOrEmpty(filename)) {
                File jsonFile = IOUtils.getAbsoluteFileAsFile(getBaseDir(), filename);
                DocumentContext context = loadJsonContextFromFile(jsonFile);
                execute(context, filename);
            } else if(!Strings.isNullOrEmpty(path)) {
                String absolutePath = IOUtils.getAbsoluteFile(getBaseDir(), path);
                List<String> absoluteJsonFiles = IOUtils.getFiles(absolutePath);
                for(String absoluteJsonFile : absoluteJsonFiles) {
                    File file = new File(absoluteJsonFile);
                    DocumentContext context = loadJsonContextFromFile(new File(absoluteJsonFile));
                    execute(context, file.getName());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void execute(DocumentContext context, String source) {
        List<Object> jsonHits = context.read(rootPath);
        for(Object obj: jsonHits){
            Document document = new Document();

            for(Map.Entry<String, List<String>> mappingEntry : mapping.entrySet()) {
                try {
                    Object parsedValue = JsonPath.parse(obj).read(mappingEntry.getKey());
                    for(String target: mappingEntry.getValue()) {
                        document.setField(target, parsedValue);
                    }
                } catch (PathNotFoundException e) {
                    //ignore
                }
            }
            document.addField("source", source);
            executer.document(document);
        }
    }

    protected DocumentContext loadJsonContextFromUrl(String url) throws IOException {
        //@todo jsonBody has encoding bug
        String jsonBody = HTTPHelper.get(url);
        DocumentContext context = JsonPath.parse(jsonBody);
        return context;
    }


    protected DocumentContext loadJsonContextFromFile(File jsonFile) throws IOException {
        DocumentContext context = JsonPath.parse(jsonFile);
        return context;
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
