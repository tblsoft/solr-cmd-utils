package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;

/**
 * Remove field
 */
public class RemoveFieldFilter extends AbstractFilter {
    List<String> fields;

    @Override
    public void init() {
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the RemoveFieldFilter a fields attribute must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        for (String field : fields) {
            document.deleteField(field);
        }

        super.document(document);
    }
}
