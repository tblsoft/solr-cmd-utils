package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tblsoft on 7.4.20
 *
 * This filter do not support streaming. Don't use it for large input data.
 *
 * The filter consumes all documents, store them in a list. When all documents are consumed, the documents are counted
 * and forward to the next filter.
 */
public class ExpectedDocumentCountFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(ExpectedDocumentCountFilter.class);

    private List<Document> documents = new ArrayList<>();

    @Override
    public void init() {
        super.init();
    }


    @Override
    public void document(Document document) {
        documents.add(document);
    }

    @Override
    public void end() {
        pipelineExecuter.setExpectedDocumentCount((long) documents.size());
        Iterator<Document> iterator = documents.iterator();
        while(iterator.hasNext()) {
            Document doc = iterator.next();
            super.document(doc);
            iterator.remove();
        }
        super.end();
    }
}
