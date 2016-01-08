package de.tblsoft.solr.cmd;


import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;


/**
 * http://jcommander.org/
 */
//@Parameters(separators = "=")
public class SolrArgs {


    @Parameter
    private List<String> parameters = new ArrayList<String>();

    @Parameter(names = { "-command", "-cmd"}, description = "The command. Example: -cmd=deleteByQuery")
    private String command = null;


    @Parameter(names = { "-query", "-q"}, description = "The query. Example: -q=*:*")
    private String query = null;

    @Parameter(names = { "-input","-in"}, description = "The location of the input. Example: -input=/data/my-file.xml")
    private String input = null;

    @Parameter(names = { "-url"}, description = "The url. Example: -url=http://localhost:8983/solr/my-core/select?q=*")
    private String url = null;

    @Parameter(names = { "-output","-out"}, description = "The location of the output. Example: -output=/data/my-file.xml")
    private String output = null;

    @Parameter(names = { "-ignore-fields"}, description = "Comma seperated list of regex ignored fields.: --ignore-fields=foo,bar")
    private String ignoreFields = "";

    @Parameter(names = { "-allowd-chars"}, description = "Regular expression of allowed Characters.")
    private String allowedChars = null;

    @Parameter(names = { "-id-field"}, description = "The name of the id field.")
    private String idField = null;

    @Parameter(names = { "-max-items"}, description = "Maximum number of items.")
    private int maxItems = 50;

    @Parameter(names = "-i", description = "Show http headers")
    private boolean showHeaders = false;

    @Parameter(names = { "--help", "-h", "-?"}, description = "Print the help.")
    private boolean help = false;

    @Parameter(names = { "-v"}, description = "Enable debug mode.")
    private boolean debug = false;

    @Parameter(names = { "-encoding"}, description = "The encoding. Example: -encoding=UTF-8")
    private String enocding = null;


    public String getInput() {
        return input;
    }

    public String getCommand() {
        return command;
    }

    public String getQuery() {
        return query;
    }

    public boolean isShowHeaders() {
        return showHeaders;
    }

    public String getOutput() {
        return output;
    }

    public String getIgnoreFields() {
        return ignoreFields;
    }

    public int getMaxItems() {
        return maxItems;
    }

    public String getIdField() {
        return idField;
    }

    public String getAllowedChars() {
        return allowedChars;
    }

    public boolean isHelp() {
        return help;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getEnocding() {
        return enocding;
    }


    public String getUrl() {
        return url;
    }
}