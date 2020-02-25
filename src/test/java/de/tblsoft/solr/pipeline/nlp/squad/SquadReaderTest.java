package de.tblsoft.solr.pipeline.nlp.squad;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 25.02.20.
 */
public class SquadReaderTest extends AbstractPipelineTest {

    @Test
    public void testCsvReader() {
        runPipeline("examples/squad/squad-reader-unittest-pipeline.yaml");

        assertFiledExists("data");

        assertNumberOfDocuments(2);
        assertNumberOfFields(1);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test(expected = RuntimeException.class)
    public void testFileNotExists() {
        runPipeline("examples/unittest/csv-file-not-exists-pipeline.yaml");
    }
}
