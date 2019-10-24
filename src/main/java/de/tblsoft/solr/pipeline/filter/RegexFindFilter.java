package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Set Regex matches as array to given field
 */
public class RegexFindFilter extends AbstractFilter {
    private Pattern regex;
    private String sourceField;
    private String destField;

    @Override
    public void init() {
        String regexString = getProperty("regex", null);
        verify(regexString, "For the RegexFindFilter a regex property must be defined.");

        regex = Pattern.compile(regexString);

        sourceField = getProperty("sourceField", null);
        verify(sourceField, "For the RegexFindFilter a sourceField property must be defined.");

        destField = getProperty("destField", sourceField);

        super.init();
    }


    @Override
    public void document(Document document) {
        String value = document.getFieldValue(sourceField);
        if(value != null) {
            if (document.getFieldValues(destField) != null) {
                document.getFieldValues(destField).clear();
            }

            Matcher m = regex.matcher(value);
            while (m.find()) {
                String extractedValue = m.group();
                document.addField(destField, extractedValue);
            }
        }

        super.document(document);
    }
}
