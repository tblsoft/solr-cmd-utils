package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.DocumentUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Aggregate documents by Id and merge topics into separate fields of an aggregated document
 */
public class TopicAggregationFilter extends AbstractFilter {
    public class AggregationId {
        List<String> fields = new ArrayList<String>();

        public AggregationId(List<String> fields) {
            this.fields = fields;
        }

        public String buildId(Document document) {
            String id = "";

            for (String field : fields) {
                String fieldValue = document.getFieldValue(field);
                if(StringUtils.isEmpty(fieldValue)) {
                    break;
                }
                else {
                    id+="."+fieldValue;
                }
            }
            if(StringUtils.isEmpty(id)) {
                id = null;
            }

            return id;
        }
    }

    private List<AggregationId> idFields = new ArrayList<AggregationId>();
    private String fieldTopic;
    private String fieldValue;
    private Map<String, Document> docs = new HashMap<String, Document>(); // id : Document

    @Override
    public void init() {
        List<String> ids = getPropertyAsList("idField", null);
        verify(ids, "For the TopicAggregationFilter a idField property must be defined!");
        for (String field : ids) {
            String[] fields = field.split(",");
            AggregationId aggregationId = new AggregationId(Arrays.asList(fields));
            idFields.add(aggregationId);
        }

        fieldTopic = getProperty("fieldTopic", null);
        fieldValue = getProperty("fieldValue", null);

        super.init();
    }

    @Override
    public void document(Document document) {
        for (AggregationId idField : idFields) {
            String docId = idField.buildId(document);
            if(docId != null) {
                putTopicValueAsField(document);
                if(!docs.containsKey(docId)) {
                    // create
                    docs.put(docId, document);
                }
                else {
                    // merge
                    Document oldDoc = docs.get(docId);

                    Document mergedDoc = mergeDocuments(oldDoc, document);
                    docs.put(docId, mergedDoc);
                }

                break;
            }
        }
    }

    @Override
    public void end() {
        for(Map.Entry<String, Document> entry : docs.entrySet()) {
            Document doc = entry.getValue();

            super.document(doc);
        };

        super.end();
    }

    protected Document mergeDocuments(Document doc1, Document doc2) {
        Map<String, HashSet<String>> mergedFields = new HashMap<String, HashSet<String>>();

        for (Field field : doc1.getFields()) {
            mergedFields.put(field.getName(), new HashSet<String>(field.getValues()));
        }

        for (Field field : doc2.getFields()) {
            if(!mergedFields.containsKey(field.getName())) {
                mergedFields.put(field.getName(), new HashSet<String>());
            }
            mergedFields.get(field.getName()).addAll(field.getValues());
        }

        Document mergedDoc = new Document();
        for (Map.Entry<String, HashSet<String>> entry : mergedFields.entrySet()) {
            Field mergedField = new Field(entry.getKey(), new ArrayList<String>(entry.getValue()));
            mergedDoc.addField(mergedField);
        }

        return mergedDoc;
    }

    protected void putTopicValueAsField(Document document) {
        Map<String, Field> fieldMap = DocumentUtils.mapFields(document);

        if(fieldMap.containsKey(fieldTopic) && fieldMap.containsKey(fieldValue)) {
            Field topicField = fieldMap.get(fieldTopic);
            Field valueField = fieldMap.get(fieldValue);
            document.setField(topicField.getValue(), valueField.getValues());

            document.deleteField(fieldTopic);
            document.deleteField(fieldValue);
        }
    }
}
