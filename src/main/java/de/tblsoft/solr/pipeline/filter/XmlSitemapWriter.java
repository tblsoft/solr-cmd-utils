package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 24.03.18.
 */
public class XmlSitemapWriter extends AbstractFilter {

    private XMLStreamWriter writer;

    private Integer maxEntriesPerFile;

    private long documentCounter = 0;

    private long sitemapCounter = 0;

    private String filename;

    @Override
    public void init() {

        try {
            filename = getProperty("filename", null);
            maxEntriesPerFile = getPropertyAsInt("maxEntriesPerFile", 50000);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }


        super.init();
    }

    @Override
    public void document(Document document) {
        if(documentCounter % maxEntriesPerFile == 0) {
            finishSitemapFile();
            createNewSitemapFile();
            sitemapCounter++;

        }
        try {
            writer.writeStartElement("url");
            writeField("loc", document, null);
            writeField("lastmod", document, DateUtils.date2String(new Date()));
            writeField("changefreq", document, "daily");
            writeField("priority", document, "0.7");

            writer.writeEndElement();
            documentCounter++;

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        super.document(document);
    }

    private void finishSitemapFile() {
        if(writer == null) {
            return;
        }
        try {
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }

    private void createNewSitemapFile() {
        try {
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            String finalFilename = filename.replaceAll(Pattern.quote("${sitemapCounter}"), String.valueOf(sitemapCounter));
            OutputStream os = new FileOutputStream(new File(finalFilename));
            writer = output.createXMLStreamWriter(os);
            writer.writeStartDocument();
            writer.writeStartElement("urlset");
            writer.writeDefaultNamespace("http://www.sitemaps.org/schemas/sitemap/0.9");

        } catch (Exception e) {
            throw new RuntimeException(e);

        }




    }

    private void writeField(String name, Document document, String defaultValue) throws XMLStreamException {
        String value = document.getFieldValue(name);
        if(value == null) {
            value = defaultValue;
        }
        writer.writeStartElement(name);
        writer.writeCharacters(value);
        writer.writeEndElement();
    }

    @Override
    public void end() {
        finishSitemapFile();


        super.end();

    }

}
