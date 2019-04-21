package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Joiner;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SystemOutWriter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(SystemOutWriter.class);

    private String prefix;

    private int fieldCounter =0;

    private int documentCounter =0;

    @Override
    public void init() {
        try {
            prefix = getProperty("prefix", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.init();
    }

    @Override
    public void document(Document document) {
        documentCounter++;
        List<Field> values = document.getFields();
        if(values != null) {
            for(Field f :values) {
                fieldCounter++;
                LOG.info(prefix+"name: " + f.getName());

                String out = f.getValues() != null ? Joiner.on(", ").skipNulls().join(f.getValues()) : null;
                LOG.info(prefix+" -- value: " + out );
            }
        }

        super.document(document);
    }

    @Override
    public void end() {
        LOG.info("end");
        LOG.info("fields: " + fieldCounter);
        LOG.info("documents: " + documentCounter);
        super.end();
    }

}
