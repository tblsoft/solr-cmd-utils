package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.AtomicLongMap;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.Map;

/**
 * Created by tblsoft 31.03.16.
 *
 * Count the field names.
 */
public class SolrFieldCounter extends AbstractFilter {



    private AtomicLongMap<String> solrFields = AtomicLongMap.create();


    @Override
    public void init() {

    }

    @Override
    public void document(Document document) {
        for(Field field: document.getFields()) {
            solrFields.incrementAndGet(field.getName());
        }
        super.document(document);
    }

    @Override
    public void end() {
        Document document = new Document();
        Map<String,Long> fieldMap = solrFields.asMap();
        for(Map.Entry<String, Long> field: fieldMap.entrySet()) {
            document.addField(field.getKey(), String.valueOf(field.getValue()));
        }
        super.document(document);
        super.end();
    }
}
