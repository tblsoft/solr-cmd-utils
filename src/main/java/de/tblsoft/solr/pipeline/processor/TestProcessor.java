package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;

public class TestProcessor extends AbstractProcessor {
    @Override
    public void process() {
        System.out.println("Hello World.");
    }
}
