package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 28.04.16.
 */
public class PipelineExecuterTest extends AbstractPipelineTest {


    @Test(expected = IllegalArgumentException.class)
    public void getNotExistingFilterTest() {
        runPipeline("examples/unittest/csv-reader-pipeline.yaml");
        pipelineExecuter.getFilterById("filter-not-exists");
    }

    @Test(expected = RuntimeException.class)
    public void notExistingFilterTest() {
        runPipeline("examples/unittest/filter-not-exists-pipeline.yaml");
    }

    @Test(expected = Exception.class)
    public void readPipelineFromYamlFileFileNotExists() {
        runPipeline("file-not-exists.yaml");
    }
}
