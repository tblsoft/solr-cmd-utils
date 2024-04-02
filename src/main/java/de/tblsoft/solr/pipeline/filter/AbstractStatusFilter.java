package de.tblsoft.solr.pipeline.filter;

import com.quasiris.qsc.dto.StatusUpdateDTO;
import com.quasiris.qsc.exception.CancelPipelineException;
import com.quasiris.qsc.factory.ObjectMapperBuilder;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tblsoft on 26.11.16.
 * 
 * abstract class to implement multiple status filter.
 */
public abstract class AbstractStatusFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(AbstractStatusFilter.class);

    protected int documentCounter = 0;
	protected long lapStart = 0;
	
	protected int lapCount = 0;
	
	protected long start = 0;

	protected String webHook;

	protected List<String> fieldsToPrint;
	
    @Override
    public void init() {
        LOG.info("start processing");
    	lapStart = System.currentTimeMillis();
    	start = System.currentTimeMillis();
    	webHook = getProperty("webHook", null);
    	fieldsToPrint = getPropertyAsList("fieldsToPrint", null);

        if(webHook != null) {
            HTTPHelper.webHook(webHook,
                    "status", "progress.start",
                    "documentCounter", String.valueOf(documentCounter),
                    "expectedDocumentCount", String.valueOf(pipelineExecuter.getExpectedDocumentCount()),
                    "processId", getPipelineExecuter().getProcessId());
        }

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

            printStatus(duration, lapDuration, document);
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

    void printStatus(long duration, long lapDuration, Document document) {
        StringBuilder printFields = new StringBuilder();
        if (fieldsToPrint != null) {
            for (String fieldToPrint : fieldsToPrint) {
                String fieldValue = document.getFieldValue(fieldToPrint);
                if (fieldValue != null) {
                    printFields.append(" ");
                    printFields.append(fieldToPrint);
                    printFields.append(":");
                    printFields.append(fieldValue);
                }
            }
        }

        LOG.info("processed all " + documentCounter + " in " + getFormattedDuration(duration) + ". - processed the last " + lapCount + " documents in " + getFormattedDuration(lapDuration) + "." + printFields);

        if (webHook != null) {
            String response = HTTPHelper.webHookWithResponse(webHook, "status", "progress", "documentCounter", String.valueOf(documentCounter), "expectedDocumentCount", String.valueOf(pipelineExecuter.getExpectedDocumentCount()), "processId", getPipelineExecuter().getProcessId());
            if (response != null) {
                StatusUpdateDTO statusUpdateDTO = null;
                try {
                    statusUpdateDTO = ObjectMapperBuilder.defaultMapper().readValue(response, StatusUpdateDTO.class);
                } catch (Exception ignore) {
                }
                if (statusUpdateDTO != null && Boolean.TRUE.equals(statusUpdateDTO.getCancelPipelineInitiated())) {
                    throw new CancelPipelineException("Job has been canceled");
                }
            }
        }
    }
    
    void printEnd() {
    	long duration = System.currentTimeMillis() - start;
        LOG.info("End. processed all " + documentCounter + " in " + getFormattedDuration(duration) + ".");

		if(webHook != null) {
            HTTPHelper.webHook(webHook,
                    "status", "progress.end",
                    "documentCounter", String.valueOf(documentCounter),
                    "expectedDocumentCount", String.valueOf(pipelineExecuter.getExpectedDocumentCount()),
                    "processId", getPipelineExecuter().getProcessId());
		}
    }
}
