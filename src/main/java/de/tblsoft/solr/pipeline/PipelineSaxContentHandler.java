package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Created by tblsoft on 11.05.16.
 */
public class PipelineSaxContentHandler implements ContentHandler {

    private PipelineExecuter executer;

    private Document currentDocument = new Document();

    private StringBuilder currentValue = new StringBuilder();

    private String currentName;


    public PipelineSaxContentHandler(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void setDocumentLocator(Locator locator) {

    }

    @Override
    public void startDocument() throws SAXException {

    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        currentName = null;
        if("document".equals(localName)) {
            currentDocument = new Document();
        }
        if("field".equals(localName)) {
            currentName = atts.getValue("name");
        }
        currentValue = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {


        if("document".equals(localName)) {
            executer.document(currentDocument);
        }

        if("field".equals(localName)) {
            currentDocument.addField(currentName,currentValue.toString());
        }



    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String (ch, start, length);
        currentValue.append(value);

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {

    }

    @Override
    public void skippedEntity(String name) throws SAXException {

    }
}
