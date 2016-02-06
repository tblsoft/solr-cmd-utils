package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tblsoft on 23.01.16.
 */
public class FileLineWriter extends AbstractWriter {

    private String outputFileName;

    private OutputStreamStringBuilder outputStreamStringBuilder;

    private OutputStream outputStream;

    @Override
    public void init(){

        outputFileName = getProperty("outputFileName", null);
        verify(outputFileName, "For the FileLineWriter a output filname must be defined.");

        try {
            outputStream = IOUtils.getOutputStream(outputFileName);
            outputStreamStringBuilder = new OutputStreamStringBuilder(outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    @Override
    public void field(String name, String value) {
        outputStreamStringBuilder.append(value);
        outputStreamStringBuilder.append("\n");

    }

    @Override
    public void endDocument() {

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
