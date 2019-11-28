package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DownloadResourcesProcessor extends AbstractProcessor {

    private List<String> urlMappingList;

    @Override
    public void process() {

        try {
            init();
            for(String urlMapping: urlMappingList ) {
                String[] splitted = urlMapping.split("->");
                if(splitted.length != 2) {
                    throw new RuntimeException("Incorrect url mapping: " + urlMapping);
                }
                String url = splitted[0];
                String filename = splitted[1];
                InputStream inputStream = IOUtils.getInputStream(url);

                File targetFile = new File(filename);
                OutputStream outStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void init() {
        urlMappingList = getPropertyAsList("urlMappingList", new ArrayList<>());
    }


}
