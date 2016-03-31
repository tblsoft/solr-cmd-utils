package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tblsoft 18.03.16.
 */
public class GrepFilter extends AbstractFilter {

    private String fieldName;
    private Pattern pattern;

    private List<Map.Entry<String,String>> fieldList = new ArrayList<Map.Entry<String, String>>();

    private boolean valid = true;
    private boolean shouldMatch = true;


    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the GrepFilter a fieldName must be defined.");

        String patternString = getProperty("pattern", null);
        verify(patternString, "For the GrepFilter a pattern must be defined.");
        pattern = Pattern.compile(patternString);

        shouldMatch = getPropertyAsBoolean("shouldMatch", true);



        super.init();
    }

    @Override
    public void field(String name, String value) {
        Map.Entry<String,String> entry = new AbstractMap.SimpleEntry<String,String>(name, value);
        fieldList.add(entry);
        if(!name.matches(this.fieldName)) {

            return;
        }
        Matcher m = pattern.matcher(value);

        if(shouldMatch) {

            if (m.matches()) {
                valid = true;
            } else {
                valid = false;
            }
        } else {
            if (!m.matches()) {
                valid = true;
            } else {
                valid = false;
            }
        }
    }

    @Override
    public void endDocument() {
        if(valid) {
            for(Map.Entry<String,String> entry : fieldList) {
                super.field(entry.getKey(),entry.getValue());
            }
            super.endDocument();
        }
        fieldList = new ArrayList<Map.Entry<String, String>>();


    }
}
