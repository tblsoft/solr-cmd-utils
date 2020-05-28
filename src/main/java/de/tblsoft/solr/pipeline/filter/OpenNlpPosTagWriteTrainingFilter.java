package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tblsoft on 25.4.20.
 *
 */
public class OpenNlpPosTagWriteTrainingFilter extends AbstractFilter {


    private OutputStream fos;

    @Override
    public void init() {
        String fileName = getProperty("fileName", "train.txt");
        try {
            fos = IOUtils.getOutputStream(getBaseDir() + "/" + fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.init();
    }

    @Override
    public void document(Document document) {
        try {
            Field tokens = document.getField("tokens");
            Field posTags = document.getField("posTags");

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tokens.getValues().size(); i++) {
                String token = tokens.getValues().get(i);
                String postag = posTags.getValues().get(i);
                sb.append(token);
                sb.append("_");
                sb.append(postag);
                sb.append(" ");
            }
            String training = sb.toString().trim() + "\n";
            IOUtils.appendToOutputStream(fos, training);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void end() {
        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.end();
    }
}
