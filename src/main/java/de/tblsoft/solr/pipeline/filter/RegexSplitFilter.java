package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

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

        notMatchedDestFieldList = getPropertyAsList("notMatchedDestFieldList", new ArrayList<String>());

        super.init();
    }


    @Override
    public void document(Document document) {
        String value = document.getFieldValue(sourceField, "");
        Matcher m = regex.matcher(value);
        if (m.matches()) {
            for (int i = 0; i < m.groupCount(); i++) {
                String extractedValue = m.group(i+1);
                String fieldName = destFieldList.get(i);
                document.addField(fieldName,extractedValue);
            }
        } else {
            for(String fieldName : notMatchedDestFieldList) {
                document.addField(fieldName,value);
            }
        }

        super.document(document);
    }
}
