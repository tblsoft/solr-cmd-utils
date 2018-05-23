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

    /**
     * emit a new doc for each array value if true,
     * instead of putting all values to list of a single doc field
     */
    private boolean splitArray;

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
        splitArray = getPropertyAsBoolean("splitArray", false);

        super.init();
    }


    @Override
    public void document(Document document) {
        List<String> fieldValues = document.getFieldValues(sourceField);
        if (fieldValues != null && splitArray && fieldValues.size() > 1) {
            for (String value : fieldValues) {
                Document copy = new Document(document);
                splitDocument(copy, value);

                super.document(copy);
            }
        }
        else {
            String value = document.getFieldValue(sourceField, "");
            splitDocument(document, value);

            super.document(document);
        }
    }

    protected void splitDocument(Document document, String value) {
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
    }
}
