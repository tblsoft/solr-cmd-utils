package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.filter.TestingFilter;

/**
 * Created by tblsoft on 28.04.16.
 */
public class AbstractPipelineTest extends AbstractBaseTest {

    protected PipelineExecuter pipelineExecuter;

    public void runPipeline(String pipeline) {
        pipelineExecuter = new PipelineExecuter(pipeline);
        pipelineExecuter.execute();
        testingFilter = (TestingFilter) pipelineExecuter.getFilterById("testingFilter");
        outputDocumentList = testingFilter.getDocumentList();
    }
}
