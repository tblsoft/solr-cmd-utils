package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 13.05.21.
 */
public class DocumentReaderTest extends AbstractPipelineTest {

    @Test
    public void testReadDocument() {
        runPipeline("examples/unittest/document-reader/document-reader-pipeline.yaml");
        assertFiled("id", "1");
        assertFiled("brand", "Best Brand");
        assertFiled("title", "Best TV");
        assertFiledList("keywords", "best", "super");

        assertNumberOfDocuments(3);
        assertNumberOfFields(4);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
