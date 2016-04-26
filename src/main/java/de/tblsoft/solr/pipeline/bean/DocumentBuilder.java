package de.tblsoft.solr.pipeline.bean;

/**
 * Created by tblsoft on 26.04.16.
 */
public class DocumentBuilder {

    private Document document = new Document();

    public static DocumentBuilder document() {
        return new DocumentBuilder();
    }

    public DocumentBuilder field(String name, String value) {
        document.addField(name,value);
        return this;
    }

    public Document create() {
        return document;
    }

}
