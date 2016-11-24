package de.tblsoft.solr.pipeline.filter;


/**
 * Created by tblsoft on 02.04.16.
 * 
 * print a status after a defined number of documents. The default is 1000.
 */
public class StatusFilter extends AbstractStatusFilter {

	private int statusCount = 0;
	
	
    @Override
    public void init() {
    	statusCount = getPropertyAsInt("statusCount", 1000);
        super.init();
    }

    @Override
    boolean printCurrentStatus() {
    	if(documentCounter % statusCount == 0) {
    		return true;
    	}
    	return false;
    }
    

}
