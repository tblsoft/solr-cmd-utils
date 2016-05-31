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
        assertFiled("raw", "foo bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(4);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test(expected = RuntimeException.class)
    public void testGrokReaderException() {
        runPipeline("examples/unittest/grok/grok-pipeline-with-exception.yaml");
    }


}
