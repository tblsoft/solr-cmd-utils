package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Joiner;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SpyFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(SpyFilter.class);

    List<String> fields;

    @Override
    public void init() {
        fields = getPropertyAsList("fields", new ArrayList<String>());
        super.init();
    }


    @Override
    public void document(Document document) {
        for(String fieldName: fields) {
            List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
            String value = Joiner.on("; ").join(values);
            LOG.info(fieldName + ": " + value);
        }

        super.document(document);
    }
}
