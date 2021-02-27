package de.tblsoft.solr.pipeline;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Read an array of documents from a json file
 */
public class DocumentReader extends AbstractReader {

    ObjectMapper objectMapper = new ObjectMapper();

    public void read() {
        try {
            String url = getProperty("url", null);
            String filename = getProperty("filename", null);


            objectMapper
                    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES.FAIL_ON_UNKNOWN_PROPERTIES, false);

            if(!Strings.isNullOrEmpty(url)) {
                DocumentContext context = loadJsonContextFromUrl(url);
                execute(context, url);
            } else if(!Strings.isNullOrEmpty(filename)) {
                File jsonFile = IOUtils.getAbsoluteFileAsFile(getBaseDir(), filename);
                DocumentContext context = loadJsonContextFromFile(jsonFile);
                execute(context, filename);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void execute(DocumentContext context, String source) {
        List<Object> jsonHits = context.read("$");
        for(Object obj: jsonHits){
            Document document = objectMapper.convertValue(obj, Document.class);
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
