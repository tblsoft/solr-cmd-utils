package de.tblsoft.solr.logic;


import com.google.common.util.concurrent.AtomicLongMap;
import de.tblsoft.solr.parser.SolrXmlParser;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.OutputStream;
import java.util.Map.Entry;

/**
 */
public class UsedFieldCounter extends SolrXmlParser {

    private AtomicLongMap<String> counter = AtomicLongMap.create();

    private String outputFileName;

    public void countFields() throws Exception {
        parse();

        OutputStream out = IOUtils.getOutputStream(outputFileName);
        OutputStreamStringBuilder dict = new OutputStreamStringBuilder(out);
        for (Entry<String, Long> entry : counter.asMap().entrySet()) {
            dict.append(entry.getKey() + ";" + entry.getValue());
            dict.append("\n");
        }
        out.close();
        //Files.write(dict, new File("used-fields-counter.csv"), Charset.forName("UTF-8"));

    }

    @Override
    public void field(String name, String value) {
        counter.incrementAndGet(name);
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
