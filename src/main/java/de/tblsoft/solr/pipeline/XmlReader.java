package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.util.IOUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tblsoft on 11.02.16.
 */
public class XmlReader extends AbstractReader {

    @Override
    public void read() {

        try {
            String filename = getProperty("filename", null);


            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);
            List<String> fileList = IOUtils.getFiles(absoluteFilename);

            XMLReader myReader = XMLReaderFactory.createXMLReader();
            ContentHandler mySerializer = new PipelineSaxContentHandler(executer);
            boolean firstFile = true;
            TransformerHandler lastHandler = null;
            List<String> transformationFiles = getPropertyAsList("transformations", new ArrayList<>());
            for (String file : transformationFiles) {
                TransformerHandler currentHandler = getTransformerHandler(file);
                if (firstFile) {
                    firstFile = false;
                    myReader.setContentHandler(currentHandler);
                    lastHandler = currentHandler;
                    continue;
                }
                lastHandler.setResult(new SAXResult(currentHandler));
                lastHandler = currentHandler;

            }
            if (lastHandler == null) {
                myReader.setContentHandler(mySerializer);
            } else {
                lastHandler.setResult(new SAXResult(mySerializer));
            }

            for (String sourceFile : fileList) {
                InputStream in = IOUtils.getInputStream(sourceFile);
                myReader.parse(new InputSource(in));
                in.close();

            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    TransformerHandler getTransformerHandler(String fileName) throws TransformerConfigurationException {
        fileName = IOUtils.getAbsoluteFile(getBaseDir(),fileName);
        if(fileName.endsWith(".stx")) {
            SAXTransformerFactory stxFactory = new net.sf.joost.trax.TransformerFactoryImpl();
            return stxFactory.newTransformerHandler(new StreamSource(fileName));

        }
        if(fileName.endsWith(".xsl")) {
            SAXTransformerFactory xslFactory = (SAXTransformerFactory) TransformerFactory.newInstance();;
            return xslFactory.newTransformerHandler(new StreamSource(fileName));
        }
        throw new IllegalArgumentException("Only the filetypes xsl and stx are supported.");
    }

}
