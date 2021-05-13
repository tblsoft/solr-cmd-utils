package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 13.05.21.
 */
public class DocumentJoinerTest extends AbstractPipelineTest {

    @Test
    public void testCsvReader() {
        runPipeline("examples/unittest/document-joiner/document-joiner-pipeline.yaml");
        assertFiled("id", "1");
        assertFiled("title", "Best TV");
        assertFiled("price", "5.99");

        assertNumberOfDocuments(3);
        assertNumberOfFields(5);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
