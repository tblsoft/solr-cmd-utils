package de.tblsoft.solr.pipeline.filter;

import java.util.Date;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;

/**
 * Created by tblsoft 2.06.16.
 */
public class IgnoreDocumentFilter extends AbstractFilter {

    private int count;
    
    private int offset;
    
    private Date fromDate;
    
    private Date toDate;
    
    private String dateField;

    @Override
    public void init() {
    	fromDate = getPropertyAsDate("fromDate", null);
    	toDate = getPropertyAsDate("toDate", null);
    	dateField = getProperty("dateField", "date");
    	
        offset = getPropertyAsInt("offset", 0);
        super.init();
    }

    @Override
    public void document(Document document) {
    	count++;
    	
    	if(fromDate != null) {
    		String dateString = document.getFieldValue(dateField);
    		Date date = DateUtils.getDate(dateString);
    		if(date.compareTo(fromDate) > 0 &&  date.compareTo(toDate) < 0 ) {
    			super.document(document);
    			
    		}
    	} else if(count > offset) {
    		super.document(document);
    	}
    }
}
