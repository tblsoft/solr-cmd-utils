package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Replace field values with regex
 */
public class RegexReplaceFilter extends AbstractFilter {
    private Pattern regex;
    private String replacement;
    private List<String> fields;

    @Override
    public void init() {
        String regexString = getProperty("regex", null);
        verify(regexString, "For the RegexSplitFilter a regex property must be defined!");

        regex = Pattern.compile(regexString);

        this.replacement = getProperty("replacement", "");

        this.fields = getPropertyAsList("fields", null);
        verify(this.fields, "For the RegexSplitFilter a fields property must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        for (String field : fields) {
            List<String> fieldValues = document.getFieldValues(field);
            if (fieldValues != null) {
                for(int i = 0; i < fieldValues.size(); i++) {
                    String value = fieldValues.get(i);
                    value = value.replaceAll(regex.pattern(), replacement);
                    fieldValues.set(i, value);
                }
            }
        }

        super.document(document);
    }
}
