package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tblsoft on 02.05.16.
 */
public class SolrQueryLogReaderTest extends AbstractPipelineTest {

    @Test
    @Ignore
    public void testQueryLogReader() {
        runPipeline("examples/unittest/solrlog/solr-query-pipeline.yaml");
        assertFiled("coreName", "my-solr-core");
        assertFiled("queryTime", "344");

        assertNumberOfDocuments(2);
        assertNumberOfFields(6);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

   
}
