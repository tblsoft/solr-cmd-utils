package de.tblsoft.solr.util;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;

/**
 * Created by tblsoft
 */
public class XPathUtils {


    public static String getValueByXpath(String xpathExpression, String fileName) {
        try {


            DocumentBuilderFactory builderFactory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(IOUtils.getInputStream(fileName));

            XPath xPath =  XPathFactory.newInstance().newXPath();

            return xPath.compile(xpathExpression).evaluate(document);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
