package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * Extract text from html
 */
public class HtmlTextExtractorFilter extends AbstractFilter {
    private List<String> fields;

    @Override
    public void init() {
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the HtmlTextExtractorFilter a fields property must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        for (String field : fields) {
            List<String> fieldValues = document.getFieldValues(field);
            if (fieldValues != null) {
                for(int i = 0; i < fieldValues.size(); i++) {
                    String value = fieldValues.get(i);

                    org.jsoup.nodes.Document jsoupDoc = Jsoup.parseBodyFragment(value);
                    value = jsoupDoc.body().text();
                    fieldValues.set(i, value);
                }
            }
        }

        super.document(document);
    }
}
