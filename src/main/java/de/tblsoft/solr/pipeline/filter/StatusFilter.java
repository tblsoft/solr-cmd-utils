package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

/**
 * Created by tblsoft on 02.04.16.
 * 
 * print a status after a defined number of documents. The default is 1000.
 */
public class StatusFilter extends AbstractFilter {

    private int documentCounter;
	private int statusCount = 0;
	
	private long lapStart = 0;
	
	private long start = 0;
	
    @Override
    public void init() {
    	statusCount = getPropertyAsInt("statusCount", 1000);
    	lapStart = System.currentTimeMillis();
    	start = System.currentTimeMillis();
        super.init();
    }

    @Override
    public void document(Document document) {
        documentCounter++;
        if(documentCounter % statusCount == 0) {
            long duration = System.currentTimeMillis() - start;
            long lapDuration = System.currentTimeMillis() - lapStart;

            System.out.println("processed all " + documentCounter + " in " + getFormattedDuration(duration) + ". - processed the last " + statusCount + " documents in " + getFormattedDuration(lapDuration) + ".");
            lapStart = System.currentTimeMillis();
        }
        super.document(document);
    }
    
    @Override
    public void end() {
    	long duration = System.currentTimeMillis() - start;
    	System.out.println("End. processed all " + documentCounter + " in " + getFormattedDuration(duration) + ".");
    	super.end();
    };
    
    String getFormattedDuration(long duration) {
    	if(duration < 300000 ) {
    		long durationInSeconds = duration / 1000;    		
    		return durationInSeconds + " seconds";
    	}
    	long durationInMinute = duration / 1000 / 60;
    	return durationInMinute + " minutes";
    }
}
