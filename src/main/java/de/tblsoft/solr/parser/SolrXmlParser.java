package de.tblsoft.solr.parser;


import de.tblsoft.solr.util.IOUtils;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;
import java.util.List;

/**
 * Solr XML Parser
 */
public class SolrXmlParser {

    private String inputFileName;

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }


    public void parse() throws Exception {


        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        XMLEventReader xmlEventReader = xmlInputFactory
                .createXMLEventReader(IOUtils.getInputStream(getInputFileName()));

        boolean isDoc = false;
        boolean isFacet = false;
        boolean isArr = false;
        int lstCounter = 0;
        String currentValue = "";
        String currentName = "";
        List<String> possibleStartElements = Arrays.asList("date,arr,str,bool,double,float,long,int".split(","));

        while (xmlEventReader.hasNext()) {
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getAttributeByName(new QName("name")) != null) {
                    currentName = startElement.getAttributeByName(
                            new QName("name")).getValue();

                }
                if (startElement.getName().getLocalPart().equals("doc")) {
                    isDoc = true;
                }
                if (startElement.getName().getLocalPart().equals("lst")) {
                    lstCounter ++;
                    if(isFacet)
                    if("facet_fields".equals(currentName)) {
                        isFacet = true;
                    }

                }
            } else if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("doc")) {
                    isDoc = false;
                    endDocument();
                } else if (endElement.getName().getLocalPart().equals("arr")) {
                    currentName = "";
                } else if (isDoc && possibleStartElements.contains(endElement.getName().getLocalPart())) {
                    if (currentName != null && !"".equals(currentName)) {
                        field(currentName, currentValue);
                    }
                    currentValue = "";
                } else if (endElement.getName().getLocalPart().equals("lst")) {
                    lstCounter--;
                } else {
                }
            } else if (xmlEvent.isCharacters()) {
                Characters characters = xmlEvent.asCharacters();
                currentValue = characters.getData();
            }
        }

    }

    public void field(String name, String value) {

    }

    public void endDocument() {

    }

}
