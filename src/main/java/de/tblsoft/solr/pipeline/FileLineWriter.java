package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public class FileLineWriter extends AbstractFilter {

    private String filename;

    private OutputStreamStringBuilder outputStreamStringBuilder;

    private OutputStream outputStream;

    private List<String> fieldNames;
    private String fieldSeperator = ",";
    private String documentSeperator = "\n";


    @Override
    public void init(){

        String relativeFilename = getProperty("filename", null);
        fieldNames = getPropertyAsList("fieldNames", null);
        filename = IOUtils.getAbsoluteFile(getBaseDir(), relativeFilename);

        verify(filename, "For the FileLineWriter a filname must be defined.");

        try {
            outputStream = IOUtils.getOutputStream(filename);
            outputStreamStringBuilder = new OutputStreamStringBuilder(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.init();

    }


    @Override
    public void document(Document document) {
        if(fieldNames == null) {
            for (Field field : document.getFields()) {
                for (String value : field.getValues()) {
                    field(field.getName(), value);
                }
            }
        } else {
            for(String fieldName: fieldNames) {
                Field field = document.getField(fieldName);
                if(field != null) {
                    for (String value : field.getValues()) {
                        field(field.getName(), value);
                    }
                }
            }
        }

        endDocument(document);

        super.document(document);
    }

    public void field(String name, String value) {
        outputStreamStringBuilder.append(value);
        outputStreamStringBuilder.append(fieldSeperator);
    }

    public void endDocument(Document document) {
        outputStreamStringBuilder.append(documentSeperator);
    }

    @Override
    public void end() {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.end();
    }

}
