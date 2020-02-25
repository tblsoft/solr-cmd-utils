package de.tblsoft.solr.pipeline.nlp.squad;

import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.test.AbstractPipelineTest;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;

/**
 * Created by tblsoft on 25.02.20.
 */
public class SquadWriterTest extends AbstractPipelineTest {

    @Test
    public void writeSquadData() throws Exception {
        PipelineExecuter executer = new PipelineExecuter("examples/squad/squad-identity-pipeline.yaml");
        executer.execute();

        String actual = FileUtils.readFileToString(new File("examples/squad/squad-out.json"));
        String expected = FileUtils.readFileToString(new File("examples/squad/squad-example.json"));

        JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);

        FileUtils.deleteQuietly(new File("examples/squad/squad-out.json"));

    }
}
