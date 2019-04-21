package de.tblsoft.solr.perf;

import de.tblsoft.solr.log.parser.SolrLogRow;
import de.tblsoft.solr.log.parser.SolrQueryLogParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by tblsoft on 18.06.16.
 */
public class PerformanceTester extends SolrQueryLogParser {

    private static Logger LOG = LoggerFactory.getLogger(PerformanceTester.class);


    private ThreadPoolExecutor executor;

    private boolean first = true;

    private long referenceTime;
    private long startTime;

    public PerformanceTester(String file) {
        super(file);
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
    }

    public static void main(String[] args) throws Exception{
        PerformanceTester tester = new PerformanceTester("/Users/oelbaer/Downloads/logs/solr.2016-02-29.log.1");
        tester.parse();

    }


    @Override
    protected void logRow(SolrLogRow solrLogRow) {
        if(first) {
            startTime = System.currentTimeMillis();
            referenceTime = solrLogRow.getTimestamp().getTime();
            first = false;
        }


            try {
                    long diff = solrLogRow.getTimestamp().getTime() - referenceTime;
                    executor.execute(new RequestExecuter(Request.create().executionTime(startTime + diff). out("foo " + solrLogRow.getTimestamp())));

                while(executor.getActiveCount() >= 20) {
                    Thread.sleep(10);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

    }

}
