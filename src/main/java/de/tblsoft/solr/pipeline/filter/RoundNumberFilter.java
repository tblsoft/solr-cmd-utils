package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;

public class RoundNumberFilter extends AbstractFilter {
    String field;

    @Override
    public void init() {
        field = getProperty("field", null);
        verify(field, "A field must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        if(document != null) {
            List<String> fieldValues = document.getFieldValues(field);
            if (fieldValues != null) {
                for(int i = 0; i < fieldValues.size(); i++) {
                    String value = fieldValues.get(i);

                    try {
                        Double numericValue = Double.parseDouble(value);
                        Long roundValue = Math.round(numericValue);
                        value = roundValue.toString();
                    }
                    catch(Exception ignored) {
                    }

                    fieldValues.set(i, value);
                }
            }
        }

        super.document(document);
    }
}