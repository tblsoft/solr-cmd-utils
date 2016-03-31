import de.tblsoft.solr.log.parser.RequestCounter;
import de.tblsoft.solr.log.parser.SolrRequestCounter;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.junit.Ignore;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.Enumeration;

/**
 * Created by tblsoft
 */
public class Test {

    @org.junit.Test
    @Ignore
    public void test() throws Exception {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        root.setUserObject("root");

        DefaultMutableTreeNode child = new DefaultMutableTreeNode("child");
        child.setUserObject("child");


        root.add(child);
        Enumeration<DefaultMutableTreeNode> en = root.depthFirstEnumeration();
        while (en.hasMoreElements()) {

            // Unfortunately the enumeration isn't genericised so we need to downcast
            // when calling nextElement():
            DefaultMutableTreeNode node = en.nextElement();
            System.out.println(node.getUserObject());
        }
        DefaultTreeModel model = new DefaultTreeModel(root);

    }

    void ja(String key, String value) {
        //System.out.println("string");
    }
    void ja(String key, Integer value) {
        //System.out.println("integer");
    }

    @org.junit.Test
    @Ignore
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
