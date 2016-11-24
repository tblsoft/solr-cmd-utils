package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 28.04.16.
 */
public class CsvReaderTest extends AbstractPipelineTest {

    @Test
    public void testCsvReader() {
        runPipeline("examples/unittest/csv-reader-pipeline.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testCsvReaderMaxRows() {
        runPipeline("examples/unittest/csv-reader-pipeline-max-rows.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(1);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testCsvReaderAddMeta() {
        runPipeline("examples/unittest/csv-reader-pipeline-add-meta.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");
        assertFiled("rowNumber", "1");
        assertFiledExists("fileName");

        assertNumberOfDocuments(2);
        assertNumberOfFields(4);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testCsvReaderWithBz2() {
        runPipeline("examples/unittest/csv-reader-bz2-pipeline.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testCsvReaderWithGzip() {
        runPipeline("examples/unittest/csv-reader-gzip-pipeline.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testCsvReaderWithoutHeader() {
        runPipeline("examples/unittest/csv-reader-without-header-pipeline.yaml");
        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);
    }

    @Test(expected = RuntimeException.class)
    public void testFileNotExists() {
        runPipeline("examples/unittest/csv-file-not-exists-pipeline.yaml");
    }
}
