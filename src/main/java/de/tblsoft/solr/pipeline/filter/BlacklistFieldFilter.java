package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Filter words by topic values defined in an external blacklist
 */
public class BlacklistFieldFilter extends BaseBlacklistFilter {


    private List<String> fields = null;

    private String topic;

    @Override
    public void init() {
        super.init();
        topic = getProperty("topic", null);
        fields = getPropertyAsList("fields", null);

    }

    @Override
    public void document(Document document) {
        Document newDocument = new Document();
        for(Field field : document.getFields()) {
            if(fields == null || fields.contains(field.getName())) {

                List<String> newValues = new ArrayList<String>();
                for (String value : field.getValues()) {
                    if (!topicValues.get(topic).contains(value)) {
                        newValues.add(value);
                    }
                }
                newDocument.setField(field.getName(), newValues);
            } else {
                newDocument.setField(field.getName(), field.getValues());
            }
        }
        super.document(newDocument);
    }
}
