package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.*;

/**
 * Remove duplicates values per topic. Merge values of fields when documents removed.
 */
public class TopicMergeFilter extends AbstractFilter {
    private String fieldTopic;
    private String fieldValue;
    private boolean fieldValueLowercase;

    private Map<String, Map<String, Document>> topicValues = new HashMap<String, Map<String, Document>>();

    @Override
    public void init() {
        fieldTopic = getProperty("fieldTopic", null);
        fieldValue = getProperty("fieldValue", null);
        fieldValueLowercase = getPropertyAsBoolean("fieldValueLowercase", true);
        verify(fieldValue, "For the TopicUniqueFilter a fieldTopic property must be defined.");
        verify(fieldValue, "For the TopicUniqueFilter a fieldValue property must be defined.");
        super.init();
    }


    @Override
    public void document(Document document) {
        String topic = document.getFieldValue(fieldTopic);
        String value = document.getFieldValue(fieldValue);
        if(fieldValueLowercase) {
            value = value.toLowerCase();
        }

        if(!topicValues.containsKey(topic)) {
            topicValues.put(topic, new HashMap<String, Document>());
        }
        if(!topicValues.get(topic).containsKey(value)) {
            // add
            topicValues.get(topic).put(value, document);
        }
        else {
            // merge
            Document oldDoc = topicValues.get(topic).get(value);

            Document mergedDoc = mergeDocuments(oldDoc, document);
            topicValues.get(topic).put(value, mergedDoc);
        }
    }

    protected Document mergeDocuments(Document doc1, Document doc2) {
        Map<String, HashSet<String>> mergedFields = new HashMap<String, HashSet<String>>();

        for (Field field : doc1.getFields()) {
            if(field.getValues() != null) {
                mergedFields.put(field.getName(), new HashSet<String>(field.getValues()));
            }
        }

        for (Field field : doc2.getFields()) {
            if(mergedFields.containsKey(field.getName())) {
                if(field.getValues() != null) {
                    mergedFields.get(field.getName()).addAll(field.getValues());
                }
            }
        }

        Document mergedDoc = new Document();
        for (Map.Entry<String, HashSet<String>> entry : mergedFields.entrySet()) {
            Field mergedField = new Field(entry.getKey(), new ArrayList<String>(entry.getValue()));
            mergedDoc.addField(mergedField);
        }

        return mergedDoc;
    }

    @Override
    public void end() {
        for (Map<String, Document> topicValueDocs : topicValues.values()) {
            for (Document doc : topicValueDocs.values()) {
                super.document(doc);
            }
        }

        super.end();
    }
}
