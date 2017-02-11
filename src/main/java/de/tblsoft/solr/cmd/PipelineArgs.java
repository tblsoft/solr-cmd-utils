package de.tblsoft.solr.cmd;


import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;

import java.util.HashMap;
import java.util.Map;


/**
 * http://jcommander.org/
 */
//@Parameters(separators = "=")
public class PipelineArgs {


    @DynamicParameter(names = "-P", description = "Dynamic parameters.")
    private Map<String,String> parameters = new HashMap<String, String>();

    @Parameter(names = { "-pipeline", "-p"}, description = "The file for the pipeline.", required = true)
    private String pipeline = null;

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }
}