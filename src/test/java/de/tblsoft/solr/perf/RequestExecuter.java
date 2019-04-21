package de.tblsoft.solr.perf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by oelbaer on 18.06.16.
 */
public class RequestExecuter implements Runnable{

    private static Logger LOG = LoggerFactory.getLogger(RequestExecuter.class);

    private Request request;

    public RequestExecuter(Request request) {
        this.request = request;
    }

    @Override
    public void run() {
        long sleep = request.getExecutionTime() - System.currentTimeMillis();
        if(sleep > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            LOG.info("mmmmmm");
        }
        Date d = new Date(request.getExecutionTime());
        LOG.info(request.getOut() + d);



    }
}
