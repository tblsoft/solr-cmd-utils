package de.tblsoft.solr.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tblsoft
 */
public class Pipeline {

    private static Logger LOG = LoggerFactory.getLogger(Pipeline.class);

    private JCommander jc;


    public static void main(String[] args) throws Exception {
        PipelineArgs pipelineArgs = new PipelineArgs();
        Pipeline pipeline = new Pipeline();
        pipeline.jc = new JCommander(pipelineArgs);
        pipeline.jc.setProgramName("solr-pipeline");

        try {
            pipeline.jc.parse(args);
        } catch (ParameterException e) {
            StringBuilder usage = new StringBuilder();
            pipeline.jc.usage(usage);
            LOG.error("{}\n{}",e.getMessage(), usage);
            return;
        }

        PipelineExecuter pipelineExecuter = new PipelineExecuter(pipelineArgs.getPipeline());
        pipelineExecuter.setPipelineVariables(pipelineArgs.getVariables());
        pipelineExecuter.execute();




    }


}
