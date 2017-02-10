package de.tblsoft.solr.pipeline.pipeline;

import de.tblsoft.solr.pipeline.PipelineExecuter;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tblsoft on 28.04.16.
 */
public class XmlSitemapReaderTest {

    @Test
    @Ignore
    public void testXmlSitemapReader() {
        //PipelineExecuter pipelineExecuter = new PipelineExecuter("examples/xmlsitemap/open-thesaurus-pipeline.yaml");
        //PipelineExecuter pipelineExecuter = new PipelineExecuter("examples/xmlsitemap/extract-teams-pipeline.yaml");
        //PipelineExecuter pipelineExecuter = new PipelineExecuter("examples/xmlsitemap/extract-persons-pipeline.yaml");
        //PipelineExecuter pipelineExecuter = new PipelineExecuter("examples/xmlsitemap/copy-elastic-index-pipeline.yaml");
        PipelineExecuter pipelineExecuter = new PipelineExecuter("examples/xmlsitemap/xmlsitemap-pipeline.yaml");
        pipelineExecuter.execute();
    }
}
