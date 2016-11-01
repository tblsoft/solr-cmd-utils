package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;

import java.util.Map;

/**
 * Created by tblsoft 11.05.16.
 */
public class GrokFilter extends AbstractFilter {

    private String fieldName;
    private Boolean keepRaw;

    private Grok grok;


    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the GrokFilter a fieldName must be defined.");

        keepRaw = getPropertyAsBoolean("keepRaw", false);


        String grokPattern = getProperty("grokPattern", null);
        super.init();
    }

    @Override
    public void document(Document document) {

        super.document(document);
    }


    void processLine(String line) {
        //executer.field("raw", line);
        //System.out.println(line);
        Match gm = grok.match(line);
        gm.captures();
        Map<String, Object> m = gm.toMap();
        Document document = new Document();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Object value = entry.getValue();
            document.addField(entry.getKey(),String.valueOf(value));
        }

        /*
        if (!m.isEmpty()) {
            document.addField("filename", currentFileName);
            if (keepRaw) {
                document.addField("raw", line);
            }
        }
        executer.document(document);
        */
    }
}
