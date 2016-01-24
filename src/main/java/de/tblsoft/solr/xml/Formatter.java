package de.tblsoft.solr.xml;

import com.sun.org.apache.xml.internal.serializer.OutputPropertiesFactory;
import de.tblsoft.solr.util.IOUtils;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by tblsoft
 */
public class Formatter {


    public static void format(String inputFilename, String outputFilename) throws Exception {
        InputStream is = IOUtils.getInputStream(inputFilename);
        OutputStream out = IOUtils.getOutputStream(outputFilename);
        Source xmlInput = new StreamSource(is);
        StreamResult xmlOutput = new StreamResult(out);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        //transformerFactory.setAttribute("indent-number", 2);


        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputPropertiesFactory.S_KEY_INDENT_AMOUNT, "2");
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(xmlInput, xmlOutput);

        out.close();
    }
}
