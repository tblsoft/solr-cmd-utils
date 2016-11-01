package de.tblsoft.solr.perf;

import java.util.Date;

/**
 * Created by oelbaer on 18.06.16.
 */
public class RequestExecuter implements Runnable{

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
            System.out.println("mmmmmm");
        }
        Date d = new Date(request.getExecutionTime());
        System.out.println(request.getOut() + d);



    }
}
