package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoopProcessor extends AbstractProcessor {


    private static Logger LOG = LoggerFactory.getLogger(NoopProcessor.class);

    @Override
    public void process() {
    }
}
