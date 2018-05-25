package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Remove empty values from array
 */
public class EmptyArrayValuesFilter extends AbstractFilter {
    private String field;

    @Override
    public void init() {
        this.field = getProperty("field", null);
        verify(this.field, "For the RegexSplitFilter a field property must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> fieldValues = document.getFieldValues(field);
        if (fieldValues != null && fieldValues.size() > 0) {
            List<String> values = new ArrayList<>();
            for (String fieldValue : fieldValues) {
                if(StringUtils.isNotEmpty(fieldValue)) {
                    values.add(fieldValue);
                }
            }
            document.setField(field, values);
        }

        super.document(document);
    }
}
