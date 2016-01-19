package de.tblsoft.solr.cmd;


import com.beust.jcommander.Parameter;

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

    @Parameter(names = { "-help", "-h", "-?"}, description = "Print the help.")
    private boolean help = false;

    @Parameter(names = { "-v"}, description = "Enable debug mode.")
    private boolean debug = false;

    @Parameter(names = { "-encoding"}, description = "The encoding. Example: -encoding=UTF-8")
    private String enocding = null;

    @Parameter(names = { "-locale"}, description = "The locale. Example: -locale=US")
    private String locale = null;


    @Parameter(names = { "-type", "-t"}, description = "The type. Example: -type=properties")
    private String type = null;


    @Parameter(names = { "-solr-home"}, description = "The home directory of solr. Example: -solr-home=/data/solr/index")
    private String solrHome = null;

    @Parameter(names = { "-core-name"}, description = "The solr core name. Example: -core-name=my-solr-core")
    private String coreName = null;


    @Parameter(names = { "-template"}, description = "The template. Example: -template=my-template")
    private String template = null;

    @Parameter(names = { "-pattern"}, description = "The pattern. Example: -pattern=dd/MMM/yyyy:kk:mm:ss.SSS")
    private String pattern = null;

    @Parameter(names = { "-regex"}, description = "The regex. Example: -regex=.*\\[(.*)\\].*")
    private String regex = null;

    @Parameter(names = { "-period"}, description = "The period. Example: -period=minute")
    private String period = null;



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

    public String getType() {
        return type;
    }

    public String getSolrHome() {
        return solrHome;
    }

    public String getCoreName() {
        return coreName;
    }

    public String getTemplate() {
        return template;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRegex() {
        return regex;
    }

    public String getPeriod() {
        return period;
    }

    public String getLocale() {
        return locale;
    }
}