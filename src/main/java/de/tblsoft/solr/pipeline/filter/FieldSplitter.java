package de.tblsoft.solr.pipeline.filter;


import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class FieldSplitter extends AbstractFilter {

    private String sourceField;

    private String destField;

    private String splitChar;

    private boolean trimValues;

    @Override
    public void init() {
        sourceField=getProperty("sourceField", null);
        verify(sourceField, "For the FieldSplitter a sourceField property must be defined.");

        destField=getProperty("destField", null);
        splitChar=getProperty("splitChar", ",");
        trimValues=getPropertyAsBoolean("trimValues", false);
        if(Strings.isNullOrEmpty(destField)) {
            destField = sourceField;
        }

        super.init();
    }

    @Override
    public void document(Document document) {

        processValues(document);
        super.document(document);
    }

    void processValues(Document document) {
        List<String> values = document.getFieldValues(sourceField);
        if(values == null) {
            return;
        }
        List<String> splittedList = new ArrayList<String>();
        for (String value : values) {
            String[] splittedValues = value.split(Pattern.quote(splitChar));
            for(String splittedValue : splittedValues) {
                if(trimValues) {
                    splittedValue = splittedValue.trim();
                }
                splittedList.add(splittedValue);
            }
        }
        document.setField(destField, splittedList);
    }
}

