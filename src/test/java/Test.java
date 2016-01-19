import de.tblsoft.solr.log.parser.RequestCounter;
import de.tblsoft.solr.log.parser.SolrRequestCounter;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.junit.Ignore;

import java.io.File;

/**
 * Created by tblsoft
 */
public class Test {

    @org.junit.Test
    public void parseHAProxy() throws Exception {

        RequestCounter logParser = new RequestCounter("haproxy.log");
        logParser.setDateRegex(".*\\[(.*)\\].*");
        logParser.setDatePattern("dd/MMM/yyyy:kk:mm:ss.SSS");
        logParser.parse();
        logParser.print();
    }

    @org.junit.Test
    @Ignore
    public void readSolrLog() throws Exception {
        String file = "/tmp/artifacts/solr.2016-01-14.log";
        SolrRequestCounter parser = new SolrRequestCounter(file);
        parser.parse();
        parser.print();
    }



    @org.junit.Test
    @Ignore
    public void tailer() throws Exception {
        TailerListener listener = new MyTailerListener();
        Tailer tailer = Tailer.create(new File("/tmp/artifacts/solr.log"), listener, 1000,true);
        tailer.run();
    }


}
