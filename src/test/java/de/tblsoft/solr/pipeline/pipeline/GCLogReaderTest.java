package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 07.06.16.
 */
public class GCLogReaderTest extends AbstractPipelineTest {

    @Test
    public void testGrokReader() {
        runPipeline("examples/unittest/gc/gc-pipeline.yaml");
        assertFiled("foo", "foo");
        assertFiled("bar", "bar");
        assertFiled("raw", "foo bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(4);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }


}
