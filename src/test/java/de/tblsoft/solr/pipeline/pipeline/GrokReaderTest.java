package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 31.05.16.
 */
public class GrokReaderTest extends AbstractPipelineTest {

    @Test
    public void testGrokReader() {
        runPipeline("examples/unittest/grok/grok-pipeline.yaml");
        assertFiled("foo", "foo");
        assertFiled("bar", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(3);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
