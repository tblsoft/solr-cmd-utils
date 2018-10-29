package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;
import java.util.Random;

/**
 * Select value of static predefined values
 */
public class RandomStaticValueFilter extends AbstractFilter {
    private String field;
    private List<String> values;

    @Override
    public void init() {
        field = getProperty("field", null);
        verify(field, "For the RandomStaticValueFilter field must be defined!");

        values = getPropertyAsList("values", null);
        verify(values, "For the RandomStaticValueFilter values must be defined!");

        super.init();
    }

    @Override
    public void document(Document doc) {
        int i = new Random().nextInt(values.size());
        String value = values.get(i);
        doc.setField(field, value);

        super.document(doc);
    }
}
