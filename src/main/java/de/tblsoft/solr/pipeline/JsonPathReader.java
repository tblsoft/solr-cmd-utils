package de.tblsoft.solr.pipeline;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Read Json Files from File or Url
 */
public class JsonPathReader extends AbstractReader {

    public void read() {
        SimpleMapping simpleMapping = new SimpleMapping(getPropertyAsList("mapping", new ArrayList<String>()));
        Map<String, List<String>> mapping = simpleMapping.getMapping();
        try {
            String rootPath = getProperty("rootPath", "$");

            DocumentContext context = loadJsonContext();
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
                executer.document(document);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected DocumentContext loadJsonContext() throws IOException {
        DocumentContext context = null;

        String url = getProperty("url", null);
        String filename = getProperty("filename", null);

        if(StringUtils.isNotEmpty(url)) {
            //@todo jsonBody has encoding bug
            String jsonBody = HTTPHelper.get(url);
            context = JsonPath.parse(jsonBody);
        }
        else if(StringUtils.isNotEmpty(filename)) {
            File jsonFile = IOUtils.getAbsoluteFileAsFile(getBaseDir(), filename);
            context = JsonPath.parse(jsonFile);
        }

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
