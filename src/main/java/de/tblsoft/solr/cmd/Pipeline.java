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

        try {
            pipeline.jc = new JCommander(pipelineArgs, args);
        } catch (ParameterException e) {
            LOG.error(e.getMessage());
        }

        PipelineExecuter pipelineExecuter = new PipelineExecuter(pipelineArgs.getPipeline());
        pipelineExecuter.setPipelineVariables(pipelineArgs.getParameters());
        pipelineExecuter.execute();




    }


    void printHelp(String help) {
        JCommander.getConsole().println(help);

    }


}
