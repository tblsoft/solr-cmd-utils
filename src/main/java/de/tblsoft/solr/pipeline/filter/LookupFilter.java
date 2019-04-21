package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by tblsoft 17.03.16.
 */
public class LookupFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(LookupFilter.class);

    private Map<String, Document> lookup = new HashMap<String, Document>();

    private String keyField;

    private List<String> keyNormalizationFunctions;

    @Override
    public void init() {
        keyField = getProperty("keyField", "key");
        String keyNormalization = getProperty("keyNormalization", "");
        keyNormalizationFunctions = Arrays.asList(keyNormalization.split(Pattern.quote("|")));
        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> keyList = document.getFieldValues(keyField);
        for(String key: keyList) {
            String normalizedKey = normalizeKey(key);

            LOG.info("############ " + normalizedKey);
            lookup.put(normalizedKey,document);
        }


        super.document(document);
    }

    public String normalizeKey(String key) {
        String normalizedKey = key;
        SimpleMapping simpleMapping = new SimpleMapping();
        for(String function: keyNormalizationFunctions) {
            normalizedKey = simpleMapping.executeFunction(function, normalizedKey);
        }
        return normalizedKey;
    }

    public Map<String, Document> getLookup() {
        return lookup;
    }

}
