package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 11.02.16.
 */
public class OpenThesaurusReader extends AbstractReader {

    private String processingOption;

    @Override
    public void read() {

        processingOption = getProperty("processingOption","line2Document");

        String fileName = getProperty("fileName", null);

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("#")) {
                    // ignore
                    continue;

                }
                String[] lineParts = line.split(Pattern.quote(";"));
                if("line2Document".equals(processingOption)) {
                    line2document(lineParts);
                } else if ("token2document".equals(processingOption)) {
                    token2document(lineParts);
                } else {
                    throw new RuntimeException("You have to configure a correct processingOption [line2Document, token2document] . processingOption: " + processingOption);
                }


            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void line2document(String[] lineParts) {
        Document document = new Document();
        document.setField("tokens", Arrays.asList(lineParts));
        executer.document(document);

    }

    void token2document(String[] lineParts) {
        for(String token: lineParts) {
            Document document = new Document();
            document.setField("token", token);
            executer.document(document);
        }
    }




}
