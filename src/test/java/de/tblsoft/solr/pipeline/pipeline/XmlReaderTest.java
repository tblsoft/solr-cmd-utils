package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Test;

/**
 * Created by tblsoft on 28.04.16.
 */
public class XmlReaderTest extends AbstractPipelineTest {

    @Test
    public void testXmlReaderForStxExample() {
        runPipeline("examples/unittest/xml/stx-example-pipeline.yaml");
        assertFiled("title", "title 1");
        assertFiled("description", "description 1");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testXmlReaderForXsl() {
        runPipeline("examples/unittest/xml/xsl-identity-pipeline.yaml");
        assertFiled("title", "title 1");
        assertFiled("description", "description 1");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }


    @Test
    public void testXmlReaderForStx() {
        runPipeline("examples/unittest/xml/stx-identity-pipeline.yaml");
        assertFiled("title", "title 1");
        assertFiled("description", "description 1");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testXmlReaderForStxXsl() {
        runPipeline("examples/unittest/xml/stx-xsl-identity-pipeline.yaml");
        assertFiled("title", "title 1");
        assertFiled("description", "description 1");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }




    @Test(expected = RuntimeException.class)
    public void testFileNotExists() {
        runPipeline("examples/unittest/xml/xml-file-not-exists-pipeline.yaml");
    }
}
