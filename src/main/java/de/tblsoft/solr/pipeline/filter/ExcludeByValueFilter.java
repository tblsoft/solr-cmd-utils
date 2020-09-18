package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.commons.lang3.StringUtils;

/**
 * Exclude documents by value
 */
public class ExcludeByValueFilter extends AbstractFilter {
    String field;
    String value;

    @Override
    public void init() {
        field = getProperty("field", null);
        value = getProperty("value", null);

        super.init();
    }

    @Override
    public void document(Document document) {
        Field topic = document.getField(field);
        if(topic == null || !StringUtils.equalsIgnoreCase(topic.getValue(), value)) {
            super.document(document);
        }
    }
}
