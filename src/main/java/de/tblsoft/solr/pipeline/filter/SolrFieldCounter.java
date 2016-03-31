package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.AtomicLongMap;
import de.tblsoft.solr.pipeline.AbstractFilter;

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
    public void field(String name, String value) {
        solrFields.incrementAndGet(name);
    }

    @Override
    public void end() {
        Map<String,Long> fieldMap = solrFields.asMap();
        for(Map.Entry<String, Long> field: fieldMap.entrySet()) {
            super.field(field.getKey(),String.valueOf(field.getValue()));
        }
        super.end();
    }
}
