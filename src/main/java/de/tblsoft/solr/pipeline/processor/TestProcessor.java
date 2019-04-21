package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProcessor extends AbstractProcessor {


    private static Logger LOG = LoggerFactory.getLogger(TestProcessor.class);

    @Override
    public void process() {
        LOG.info("Hello World.");
    }
}
