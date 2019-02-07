package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

/**
 * Created by tblsoft on 26.11.16.
 * 
 * abstract class to implement multiple status filter.
 */
public abstract class AbstractStatusFilter extends AbstractFilter {

    protected int documentCounter = 0;
	protected long lapStart = 0;
	
	protected int lapCount = 0;
	
	protected long start = 0;
	
    @Override
    public void init() {
    	lapStart = System.currentTimeMillis();
    	start = System.currentTimeMillis();
        super.init();
    }
    
    @Override
    public void document(Document document) {
    	documentCounter++;
    	lapCount++;
        if(printCurrentStatus()) {
            long now = System.currentTimeMillis();
            long duration = now - start;
            long lapDuration = now - lapStart;

            printStatus(duration, lapDuration);
            lapStart = now;
            lapCount = 0;
        }
        super.document(document);
    }
    
    abstract boolean printCurrentStatus();
    
    @Override
    public void end() {
    	printEnd();
    	super.end();
    };
    
 
    protected String getFormattedDuration(long duration) {
    	if(duration < 300000 ) {
    		long durationInSeconds = duration / 1000;    		
    		return durationInSeconds + " seconds";
    	} else if (duration < 180*60*1000) {
            long durationInMinute = duration / 1000 / 60;
            return durationInMinute + " minutes";
        } else {
            long durationInMinute = duration / 1000 / 60;
            long durationInHours = durationInMinute / 60;
            return durationInHours + " hours " + durationInMinute + " minutes";
        }
    }
    
    void printStatus(long duration, long lapDuration) {
    	System.out.println("processed all " + documentCounter + " in " + getFormattedDuration(duration) + ". - processed the last " + lapCount + " documents in " + getFormattedDuration(lapDuration) + ".");
        
    }
    
    void printEnd() {
    	long duration = System.currentTimeMillis() - start;
    	System.out.println("End. processed all " + documentCounter + " in " + getFormattedDuration(duration) + ".");
		System.out.println(new Date());
    }
}
