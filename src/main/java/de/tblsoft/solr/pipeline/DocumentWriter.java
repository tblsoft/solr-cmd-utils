package de.tblsoft.solr.pipeline;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tblsoft on 27.02.21.
 * Write an array of documents to a file
 */
public class DocumentWriter extends AbstractFilter {

    private String filename;

    private OutputStreamStringBuilder outputStreamStringBuilder;

    private OutputStream outputStream;

    private ObjectMapper objectMapper = new ObjectMapper();

    private boolean isFirstDocument = true;


    @Override
    public void init(){

        String relativeFilename = getProperty("filename", null);
        filename = IOUtils.getAbsoluteFile(getBaseDir(), relativeFilename);
        verify(filename, "For the FileLineWriter a filname must be defined.");

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            outputStream = IOUtils.getOutputStream(filename);
            outputStreamStringBuilder = new OutputStreamStringBuilder(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        outputStreamStringBuilder.append("[");

        super.init();
    }


    @Override
    public void document(Document document) {
        try {
            if(isFirstDocument) {
                isFirstDocument = false;
            } else {
                outputStreamStringBuilder.append(",");
            }

            Map<String, Object> outputDocument = new HashMap<>();
            for(Field field : document.getFields()) {
                if(field.getValues().size() == 1) {
                    outputDocument.put(field.getName(), field.getValue());
                } else {
                    outputDocument.put(field.getName(), field.getValues());
                }

            }
            outputStreamStringBuilder.append(objectMapper.writeValueAsString(outputDocument));

            super.document(document);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void end() {
        outputStreamStringBuilder.append("]");
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.end();
    }

}
