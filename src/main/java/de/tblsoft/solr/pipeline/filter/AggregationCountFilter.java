package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.AtomicLongMap;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tbl on 22.09.17.
 */
public class AggregationCountFilter extends AbstractFilter {

    Map<String, AtomicLongMap<String>> aggregation = new HashMap<String, AtomicLongMap<String>>();

    @Override
    public void document(Document document) {
        for(Field field : document.getFields()) {
            AtomicLongMap<String> countMap = aggregation.get(field.getName());
            if(countMap == null){
                countMap = AtomicLongMap.create();
            }
            for(String value : field.getValues()) {
                countMap.incrementAndGet(value);
            }
            aggregation.put(field.getName(), countMap);
        }
    }

    @Override
    public void end() {

        for(Map.Entry<String, AtomicLongMap<String>> entry : aggregation.entrySet()) {
            String fieldName = entry.getKey();
            AtomicLongMap<String> fieldValue =  entry.getValue();
            for(Map.Entry<String,Long> fieldValueEntry : fieldValue.asMap().entrySet()) {
                String value = fieldValueEntry.getKey();
                Long count = fieldValueEntry.getValue();
                Document document = new Document();
                document.setField("value", value);
                document.setField("count", count);
                document.setField("type", fieldName);
                super.document(document);
            }
        }

        super.end();
    }

}
