package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 23.01.16.
 */
public class RegexSplitFilter extends AbstractFilter {

    private Pattern regex;
    private String sourceField;

    private List<String> destFieldList;

    private List<String> notMatchedDestFieldList;


    @Override
    public void init() {
        String regexString = getProperty("regex", null);
        verify(regexString, "For the RegexSplitFilter a regex property must be defined.");

        regex = Pattern.compile(regexString);

        this.sourceField = getProperty("sourceField", null);
        verify(this.sourceField, "For the RegexSplitFilter a sourceField property must be defined.");

        destFieldList = getPropertyAsList("destFieldList", null);
        verify(this.destFieldList, "For the RegexSplitFilter a destFieldList property must be defined as list.");

        notMatchedDestFieldList = getPropertyAsList("destFieldList", new ArrayList<String>());

        super.init();
    }

    @Override
    public void field(String name, String value) {
        if(!name.matches(sourceField)) {
            super.field(name,value);
            return;
        }
        Matcher m = regex.matcher(value);
        if (m.matches()) {
            for (int i = 0; i < m.groupCount(); i++) {
                String extractedValue = m.group(i+1);
                String fieldName = destFieldList.get(i);
                super.field(fieldName, extractedValue);
            }
            super.field(name,value);
        } else {
            super.field(name,value);
            for(String fieldName : notMatchedDestFieldList) {
                super.field(fieldName,value);
            }
        }
    }
}
