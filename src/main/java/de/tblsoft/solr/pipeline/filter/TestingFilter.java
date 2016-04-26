package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 22.04.16.
 */
public class TestingFilter extends AbstractFilter {

    private List<Document> documentList = new ArrayList<Document>();

    private boolean initWasDelegated = false;
    private boolean endWasDelegated = false;

    @Override
    public void init() {
        initWasDelegated = true;
        super.init();
    }

    @Override
    public void document(Document document) {
        documentList.add(document);
        super.document(document);
    }

    @Override
    public void end() {
        endWasDelegated = true;
        super.end();
    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public boolean isInitWasDelegated() {
        return initWasDelegated;
    }

    public boolean isEndWasDelegated() {
        return endWasDelegated;
    }
}
