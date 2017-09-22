package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tblsoft 17.03.16.
 */
public class LookupFilter extends AbstractFilter {


    private Map<String, Document> lookup = new HashMap<String, Document>();

    private String keyField;

    @Override
    public void init() {
        keyField = getProperty("keyField", "key");
        super.init();
    }

    @Override
    public void document(Document document) {
        String key = document.getFieldValue(keyField);
        lookup.put(key,document);

        super.document(document);
    }

    public Map<String, Document> getLookup() {
        return lookup;
    }

}
