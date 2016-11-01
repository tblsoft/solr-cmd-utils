package de;

import com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl;
import de.tblsoft.solr.MyContentHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by oelbaer on 11.05.16.
 */
public class JaxbTest {

    @Test
    public void testXSL() throws Exception {
        XMLReader myReader = XMLReaderFactory.createXMLReader();
        ContentHandler mySerializer = new MyContentHandler();
        String[] transformationFiles = {"test.stx", "test.xsl"};
        boolean firstFile = true;
        TransformerHandler lastHandler = null;
        for(String file: transformationFiles) {
            TransformerHandler currentHandler = getTransformerHandler(file);
            if(firstFile) {
                firstFile = false;
                myReader.setContentHandler(currentHandler);
                lastHandler = currentHandler;
                continue;
            }
            lastHandler.setResult(new SAXResult(currentHandler));
            lastHandler = currentHandler;

        }
        if(lastHandler == null) {
            myReader.setContentHandler(mySerializer);
        } else {
            lastHandler.setResult(new SAXResult(mySerializer));
        }

        myReader.parse("test.xml");

    }

    TransformerHandler getTransformerHandler(String fileName) throws TransformerConfigurationException {
        if(fileName.endsWith(".stx")) {
            SAXTransformerFactory stxFactory = new net.sf.joost.trax.TransformerFactoryImpl();
            return stxFactory.newTransformerHandler(new StreamSource("test.stx"));

        }
        if(fileName.endsWith(".xsl")) {
            SAXTransformerFactory xslFactory = (SAXTransformerFactory) TransformerFactory.newInstance();;
            return xslFactory.newTransformerHandler(new StreamSource("test.xsl"));
        }
        throw new IllegalArgumentException("Only the filetypes xsl and stx are supported.");
    }

    @Test
    @Ignore
    public void testStx() throws Exception{
        TransformerFactory tFactory = new TransformerFactoryImpl();
        SAXTransformerFactory saxTFactory = (SAXTransformerFactory) tFactory;

        // of course the transformation source must be different
        TransformerHandler tHandler1 =
                saxTFactory.newTransformerHandler(new StreamSource("trans.stx"));
        XMLReader myReader = XMLReaderFactory.createXMLReader();
        myReader.setContentHandler(tHandler1);


        ContentHandler mySerializer = null;
        tHandler1.setResult(new SAXResult(mySerializer));
    }
}
