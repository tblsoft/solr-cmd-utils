package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tblsoft 19.08.18.
 */
public class RegexExtractFilter extends AbstractFilter {

    private String fieldName;
    private Pattern pattern;

    private boolean shouldMatch = true;

    private Map<String, String> mapping;

    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the GrepFilter a fieldName must be defined.");

        String patternString = getProperty("pattern", null);
        verify(patternString, "For the GrepFilter a pattern must be defined.");
        pattern = Pattern.compile(patternString);

        shouldMatch = getPropertyAsBoolean("shouldMatch", true);


        List<String> mappingConfiguration = getPropertyAsList("mapping", new ArrayList<String>());
        mapping = new HashMap<>();
        readConfig(mappingConfiguration);

        super.init();
    }


    @Override
    public void document(Document document) {

        List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
        for(String value: values) {
            value = value.replaceAll("\\r\\n|\\r|\\n", " ");
            Matcher m = pattern.matcher(value);
            if(shouldMatch && !m.matches()) {
                //document.deleteField(fieldName);
            } else if (!shouldMatch && m.matches()) {
                //document.deleteField(fieldName);
            } else {
                super.document(document);
            }
        }

    }


    private void readConfig(List<String> mappingConfiguration) {
        for (String v : mappingConfiguration) {
            String[] s = v.split("->");
            mapping.put(s[0], s[1]);
        }
    }

}
