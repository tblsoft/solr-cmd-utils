package de.tblsoft.solr.pipeline.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import de.tblsoft.solr.pipeline.AbstractProcessor;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Convert a json array into single documents.
 */
public class Json2SingleDocumentsProcessor extends AbstractProcessor {
    @Override
    public void process() {
        try {

            List<String> filenames = getPropertyAsList("filenames", null);
            String idSelector = getProperty("idSelector", null);
            String documentSelector = getProperty("documentSelector", "$");
            String outputPrefix = getProperty("outputPrefix", "output/debug_");
            String fileExtension = getProperty("fileExtension", ".json");

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            for (String filename : filenames) {
                File jsonFile = IOUtils.getAbsoluteFileAsFile(getBaseDir(), filename);
                DocumentContext context = JsonPath.parse(jsonFile);
                List docs = context.read(documentSelector, List.class);
                for (Object doc : docs) {
                    String id = null;
                    if(idSelector != null) {
                        id = JsonPath.parse(doc).read(idSelector);
                    } else {
                        id = UUID.randomUUID().toString();
                    }



                    FileUtils.writeStringToFile(
                            new File(outputPrefix  + id + fileExtension),
                            gson.toJson(doc),
                            "UTF-8");
                }
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }

    }
}
