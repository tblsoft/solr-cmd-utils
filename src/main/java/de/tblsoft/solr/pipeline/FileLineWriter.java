package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tblsoft on 23.01.16.
 */
public class FileLineWriter extends AbstractFilter {

    private String filename;

    private OutputStreamStringBuilder outputStreamStringBuilder;

    private OutputStream outputStream;

    @Override
    public void init(){

        String relativeFilename = getProperty("filename", null);
        filename = IOUtils.getAbsoluteFile(getBaseDir(), relativeFilename);

        verify(filename, "For the FileLineWriter a filname must be defined.");

        try {
            outputStream = IOUtils.getOutputStream(filename);
            outputStreamStringBuilder = new OutputStreamStringBuilder(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }


    @Override
    public void document(Document document) {
        for(Field field: document.getFields()) {
            field(field.getName(),field.getValue());
        }
        super.document(document);
    }

    public void field(String name, String value) {
        outputStreamStringBuilder.append(value);
        outputStreamStringBuilder.append("\n");

    }

    @Override
    public void end() {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
