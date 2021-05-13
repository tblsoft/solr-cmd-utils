package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Read a html from a file.
 * It's for testing purpose.
 */
public class HtmlFileReader extends AbstractReader {

    public void read() {

        String fieldName = getProperty("fieldName", "http_payload");
        String filename = getProperty("filename", null);
        String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);
        String charset = getProperty("charset", StandardCharsets.UTF_8.name());

        try {

            String html = IOUtils.getString(absoluteFilename);

            Document document = new Document();
            document.setField(fieldName, html);

            executer.document(document);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
