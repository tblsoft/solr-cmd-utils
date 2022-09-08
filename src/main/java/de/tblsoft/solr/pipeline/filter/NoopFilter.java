package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;


/**
 * Created by tblsoft 28.04.16.
 */
public class NoopFilter extends AbstractFilter {
    public Document map(Document document) {
        return document;
    }
}
