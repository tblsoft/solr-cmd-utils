package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;

import java.util.Date;

/**
 * Created by tblsoft 22.05.19.
 */
public class ProcessingTimeFilter extends AbstractFilter {

    private String dateFormat;

    private String processingTimeField;


    @Override
    public void init() {
        dateFormat = getProperty("dateFormat", DateUtils.DATE_FORMAT);
        this.processingTimeField = getProperty("processingTimeField", "processingtime");
        super.init();
    }

    @Override
    public void document(Document document) {
        document.setField(processingTimeField, DateUtils.date2String(new Date(), dateFormat));
        super.document(document);
    }
}
