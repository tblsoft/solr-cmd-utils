package de.tblsoft.solr.pipeline.filter;


/**
 * Created by tblsoft on 26.11.16
 * 
 * print a status after a defined time interval. The default is 10 seconds.
 */
public class StatusTimeFilter extends AbstractStatusFilter {

	
	int timeInterval = 10;
	
	long lastStatus = 0;
	
    @Override
    public void init() {
    	timeInterval = getPropertyAsInt("timeInterval", 10);
    	lastStatus = System.currentTimeMillis();
        super.init();
    }


    @Override
    boolean printCurrentStatus() {
    	long now = System.currentTimeMillis();
    	if(lastStatus + (timeInterval*1000) < now) {
    		lastStatus = now;
    		return true;
    	}
    	
    	return false;
    }
    

}
