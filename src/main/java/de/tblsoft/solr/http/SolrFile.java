package de.tblsoft.solr.http;

import de.tblsoft.solr.bean.SolrConfiguration;
import org.apache.commons.io.FileUtils;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by tblsoft
 */
public class SolrFile {

    public void createIndexWithCoreProperties(String solrHome, String coreName, String templateDir) throws Exception {
        copyIndex(solrHome,coreName,templateDir);

        FileInputStream fi=new FileInputStream(templateDir + "/core.properties");
        Properties p = new Properties();
        p.load(fi);
        fi.close();

        p.setProperty("name",coreName);

        FileOutputStream fr=new FileOutputStream(solrHome + "/" + coreName  + "/core.properties");
        p.store(fr,"Properties");
        fr.close();
    }



    public void createIndexWithSolrXml(String solrHome, String coreName, String templateDir) throws Exception {
        String solrXml = solrHome + "/solr.xml";
        copyIndex(solrHome  ,coreName,templateDir);
        SolrConfiguration solrConfiguration = getSolrConfiguration(solrXml);
        solrConfiguration.getCores().addCore(coreName);
        writeSolrConfiguration(solrXml,solrConfiguration);
    }


    void copyIndex(String indexDir, String coreName, String templateDir) throws IOException {
        Files.createDirectory( Paths.get(indexDir + "/" + coreName));
        FileUtils.copyDirectory(new File(templateDir + "/conf"), new File(indexDir + "/" + coreName + "/conf"));
    }

    public SolrConfiguration getSolrConfiguration(String file) {
        try {
            File solrfile = new File(file);
            JAXBContext jaxbContext = JAXBContext.newInstance(SolrConfiguration.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SolrConfiguration solr = (SolrConfiguration) jaxbUnmarshaller.unmarshal(solrfile);
            return solr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeSolrConfiguration(String file, SolrConfiguration solr) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SolrConfiguration.class);

            File solrOut = new File(file);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(solr, solrOut);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
