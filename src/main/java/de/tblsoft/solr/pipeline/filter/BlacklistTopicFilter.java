package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

/**
 * Filter words by topic values defined in an external blacklist
 */
public class BlacklistTopicFilter extends BaseBlacklistFilter {

    @Override
    public void document(Document document) {
        boolean blacklist = false;
        Field topic = document.getField(fieldTopic);
        Field value = document.getField(fieldValue);
        if(topic != null && value != null) {
            if(topicValues.containsKey(topic.getValue())) {
                blacklist = topicValues.get(topic.getValue()).contains(value.getValue());
            }
        }

        if(!blacklist) {
            super.document(document);
        }
    }
}
