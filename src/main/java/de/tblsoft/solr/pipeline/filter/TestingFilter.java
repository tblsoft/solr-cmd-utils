package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oelbaer on 22.04.16.
 */
public class TestingFilter extends AbstractFilter {

    private List<Document> documentList = new ArrayList<Document>();


    @Override
    public void document(Document document) {
        documentList.add(document);
        super.document(document);
    }

    public List<Document> getDocumentList() {
        return documentList;
    }
}
