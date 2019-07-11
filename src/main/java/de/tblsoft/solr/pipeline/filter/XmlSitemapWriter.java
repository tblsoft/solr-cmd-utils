package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.sitemap.bean.Sitemap;
import de.tblsoft.solr.sitemap.bean.Sitemapindex;
import de.tblsoft.solr.util.DateUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private String sitemapIndexFilename;
    private String baseUrl;

    private List<String> sitemapFiles = new ArrayList<String>();

    @Override
    public void init() {

        try {
            filename = getProperty("filename", null);
            sitemapIndexFilename = getProperty("sitemapIndexFilename", "sitemap-index.xml");
            baseUrl = getProperty("baseUrl", null);
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
            sitemapFiles.add(finalFilename);
            File file = new File(finalFilename);

            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            OutputStream os = new FileOutputStream(file);
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
        createSitemapIndex();

        super.end();

    }

    private void createSitemapIndex() {
        try {
            Sitemapindex sitemapindex = new Sitemapindex();

            for (String sitemapFile : sitemapFiles) {
                Sitemap sitemap = new Sitemap();
                File file = new File(sitemapFile);
                sitemap.setLoc(baseUrl + file.getName());
                sitemapindex.getSitemap().add(sitemap);
            }


            File file = new File(sitemapIndexFilename);
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(Sitemapindex.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(sitemapindex, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
