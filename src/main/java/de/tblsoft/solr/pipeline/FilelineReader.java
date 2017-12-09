package de.tblsoft.solr.pipeline;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by tblsoft on 19.11.17.
 */
public class FilelineReader extends AbstractReader {

    public void read() {
    	String absoluteFilename;
    	boolean addMeta = false;

        String charset = getProperty("charset", StandardCharsets.UTF_8.name());
        String filename = getProperty("filename", null);
        absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

        addMeta = getPropertyAsBoolean("addMeta", false);
        Long maxRows = getPropertyAsInteger("maxRows", Long.MAX_VALUE);
        String delimiter = getProperty("delimiter", ",");
        String commentPrefix = getProperty("commentPrefix", "#");
        String fieldName = getProperty("fieldName", "line");

        try {
            BufferedReader br = Files.newReader(new File(absoluteFilename), Charset.forName(charset));
            String line;
            long countLines = 0;
            while ((line = br.readLine()) != null) {
                countLines++;
                if(countLines > maxRows) {
                    break;
                }
                if (line.startsWith(commentPrefix)) {
                    continue;
                }
                if(Strings.isNullOrEmpty(line)) {
                    continue;
                }
                Document document = new Document();
                for (String item : Splitter.on(delimiter).trimResults().split(line)) {
                    document.addField(fieldName, item);
                }
                executer.document(document);
            }
            br.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
