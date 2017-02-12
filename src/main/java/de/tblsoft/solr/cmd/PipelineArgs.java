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


    @DynamicParameter(names = "-V", description = "Dynamic variables for the pipeline. Overrides the default variables in the pipeline file.")
    private Map<String,String> variables = new HashMap<String, String>();

    @Parameter(names = { "-pipeline", "-p"}, description = "The filename for the pipeline.", required = true)
    private String pipeline = null;

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }
}