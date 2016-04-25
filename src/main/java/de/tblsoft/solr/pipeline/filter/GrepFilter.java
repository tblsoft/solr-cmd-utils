package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tblsoft 18.03.16.
 */
public class GrepFilter extends AbstractFilter {

    private String fieldName;
    private Pattern pattern;

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
    public void document(Document document) {
        List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
        for(String value: values) {
            Matcher m = pattern.matcher(value);
            if(shouldMatch && !m.matches()) {
                document.deleteField(fieldName);
            } else if (!shouldMatch && m.matches()) {
                document.deleteField(fieldName);
            }
        }
        super.document(document);
    }

}
