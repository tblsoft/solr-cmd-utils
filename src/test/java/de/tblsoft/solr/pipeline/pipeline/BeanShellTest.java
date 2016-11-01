package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tblsoft on 28.04.16.
 */
public class BeanShellTest extends AbstractPipelineTest {

    @Test
    @Ignore
    public void testBeanShell() {
        runPipeline("examples/beanshell/beanshell-pipeline.yaml");
        print();

        assertFiled("column1", "foo");
        assertFiled("column2", "bar");

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
