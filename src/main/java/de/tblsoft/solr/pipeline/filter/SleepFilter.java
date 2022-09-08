package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;


/**
 * Sleep
 */
public class SleepFilter extends AbstractFilter {
    private long duration; // in ms

    public SleepFilter() {
    }

    public SleepFilter(long duration) {
        this.duration = duration;
    }

    @Override
    public void init() {
        duration = getPropertyAsInt("duration", 1000);

        super.init();
    }

    @Override
    public void document(Document document) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        super.document(document);
    }

    @Override
    public Document map(Document document) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return document;
    }
}
