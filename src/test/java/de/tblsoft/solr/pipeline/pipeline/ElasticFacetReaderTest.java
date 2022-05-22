package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 22.05.22.
 */
public class ElasticFacetReaderTest extends AbstractPipelineTest {

    @Test
    public void testCsvReader() {
        runPipeline("examples/elastic/elastic-facet-reader-pipeline.yaml");
        assertFiled("value", "wago");
        assertFiled("count", "3161");

        assertNumberOfDocuments(10);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

}
