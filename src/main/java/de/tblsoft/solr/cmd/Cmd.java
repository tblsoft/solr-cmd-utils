package de.tblsoft.solr.cmd;

import com.beust.jcommander.JCommander;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import de.tblsoft.solr.http.Solr;
import de.tblsoft.solr.logic.NounExtractor;
import de.tblsoft.solr.logic.SolrFeeder;
import de.tblsoft.solr.logic.SpecialCharacterExtractor;
import de.tblsoft.solr.logic.UsedFieldCounter;
import de.tblsoft.solr.util.XPathUtils;
import de.tblsoft.solr.xml.Formatter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by tblsoft
 */
public class Cmd {

    private JCommander jc;


    public static void main(String[] args) throws Exception {
        SolrArgs solrArgs = new SolrArgs();
        Cmd cmd = new Cmd();
        cmd.jc = new JCommander(solrArgs, args);


        String command = solrArgs.getCommand();

        if ("feedFileToSolr".equals(command)) {
            cmd.feedFileToSolr(solrArgs);
        } else if ("indexFileToSolr".equals(command)) {
            cmd.indexFileToSolr(solrArgs);
        } else if ("solrDump".equals(command)) {
            cmd.solrDump(solrArgs);
        } else if ("retrieveFromSolr".equals(command)) {
            cmd.retrieveFromSolr(solrArgs);
        } else if ("deleteByQuery".equals(command)) {
            cmd.deleteByQuery(solrArgs);
        } else if ("deleteAll".equals(command)) {
            cmd.deleteAll(solrArgs);
        } else if ("xmllint".equals(command)) {
            cmd.xmllint(solrArgs);
        } else if ("extractNouns".equals(command)) {
            cmd.extractNouns(solrArgs);
        } else if ("countUsedFields".equals(command)) {
            cmd.countUsedFields(solrArgs);
        } else if ("extractSpecialCharacters".equals(command)) {
            cmd.extractSpecialCharacters(solrArgs);
        } else if ("numFound".equals(command)) {
            cmd.numFound(solrArgs);
        }else if ("xPath".equals(command)) {
            cmd.xPath(solrArgs);
        } else {
            JCommander.getConsole().println("--comand=feedFileToSolr");
            JCommander.getConsole().println("--comand=indexFileToSolr");
            JCommander.getConsole().println("--comand=solrDump");
            JCommander.getConsole().println("--comand=retrieveFromSolr");
            JCommander.getConsole().println("--comand=deleteByQuery");
            JCommander.getConsole().println("--comand=deleteAll");
            JCommander.getConsole().println("--comand=xmllint");
            JCommander.getConsole().println("--comand=numFound");
            JCommander.getConsole().println("--comand=xPath");
            JCommander.getConsole().println("--comand=extractNouns");
            JCommander.getConsole().println("--comand=countUsedFields");
            JCommander.getConsole().println("--comand=extractSpecialCharacters");
        }

    }


    public void countUsedFields(SolrArgs solrArgs) throws Exception {
        String inputFileName = solrArgs.getInput();

        String outputFileName = solrArgs.getOutput();
        if (Strings.isNullOrEmpty(outputFileName)) {
            outputFileName = "stdout";
        }
        verifiy(inputFileName, "--input");

        UsedFieldCounter counter = new UsedFieldCounter();
        counter.setInputFileName(inputFileName);
        counter.setOutputFileName(outputFileName);
        counter.countFields();

    }

    public void solrDump(SolrArgs solrArgs) throws Exception {
        String help = "--comand=solrDump --input=[value] --output=[value] --ignore-fields=[regex1],[regex2],...";
        if(solrArgs.isHelp()) {
            printHelp(help);
            return;
        }

        String input = solrArgs.getInput();
        String output = solrArgs.getOutput();

        verifiy(input, "--input");
        verifiy(output, "--output");


        SolrFeeder feeder = new SolrFeeder(output);
        String ignoreFields = solrArgs.getIgnoreFields();
        List<String> ignoreFieldsList = Lists.newArrayList(Splitter.on(",").split(ignoreFields));
        feeder.setIgnoreFields(ignoreFieldsList);

        Solr solr = new Solr(false);

        if (input.startsWith("http") && !output.startsWith("http")) {
            solr.retrieveFromSolr(input, output);
        } else if (input.startsWith("http") && output.startsWith("http")) {
            String tempFile = File.createTempFile("solr_dump_", ".xml.gz").getAbsolutePath();
            solr.retrieveFromSolr(input, tempFile);
            feeder.setInputFileName(tempFile);
            feeder.doFeed();
            Files.delete(Paths.get(tempFile));

        } else if (!input.startsWith("http") && output.startsWith("http")) {
            feeder.setInputFileName(input);
            feeder.doFeed();
        }
    }

    public void numFound(SolrArgs solrArgs) throws Exception {
        String help = "--comand=numFound --input=[value]";
        if(solrArgs.isHelp()) {
            printHelp(help);
            return;
        }

        String input = solrArgs.getInput();

        verifiy(input, "--input");
        String xPath = "/response/result/@numFound";


        Solr solr = new Solr(false);

        if (input.startsWith("http")) {
            String tempFile = File.createTempFile("solr_dump_", ".xml.gz").getAbsolutePath();
            solr.retrieveFromSolr(input, tempFile);
            String value = XPathUtils.getValueByXpath(xPath,tempFile);
            System.out.println(value);
            Files.delete(Paths.get(tempFile));
        } else {
            String value = XPathUtils.getValueByXpath(xPath,input);
            System.out.println(value);

        }
    }


    public void xPath(SolrArgs solrArgs) throws Exception {
        String help = "--comand=xPath --input=[value] --query=[value]";
        if(solrArgs.isHelp()) {
            printHelp(help);
            return;
        }

        String input = solrArgs.getInput();
        String xPath = solrArgs.getQuery();

        verifiy(input, "--input");
        verifiy(xPath, "--query");



        Solr solr = new Solr(false);

        if (input.startsWith("http")) {
            String tempFile = File.createTempFile("solr_dump_", ".xml.gz").getAbsolutePath();
            solr.retrieveFromSolr(input, tempFile);
            String value = XPathUtils.getValueByXpath(xPath,tempFile);
            System.out.println(value);
            Files.delete(Paths.get(tempFile));
        } else {
            String value = XPathUtils.getValueByXpath(xPath,input);
            System.out.println(value);

        }
    }

    public void extractSpecialCharacters(SolrArgs solrArgs) throws Exception {
        String inputFileName = solrArgs.getInput();

        String outputFileName = solrArgs.getOutput();
        if (Strings.isNullOrEmpty(outputFileName)) {
            outputFileName = "stdout";
        }
        verifiy(inputFileName, "--input");


        String idField = solrArgs.getIdField();
        verifiy(idField, "--id-field");

        SpecialCharacterExtractor extractor = new SpecialCharacterExtractor();
        extractor.setInputFileName(inputFileName);
        extractor.setOutputFileName(outputFileName);
        extractor.setMaxItems(solrArgs.getMaxItems());

        String allowedChars = solrArgs.getAllowedChars();
        if (!Strings.isNullOrEmpty(allowedChars)) {
            extractor.setAllowedChars(allowedChars);
        }

        extractor.extractSpecialCharacters();

    }

    public void extractNouns(SolrArgs solrArgs) throws Exception {
        String inputFileName = solrArgs.getInput();

        String outputFileName = solrArgs.getOutput();
        if (Strings.isNullOrEmpty(outputFileName)) {
            outputFileName = "stdout";
        }
        verifiy(inputFileName, "--input");

        NounExtractor extractor = new NounExtractor();
        extractor.setInputFileName(inputFileName);
        extractor.setOutputFileName(outputFileName);
        extractor.extractNouns();

    }

    public void xmllint(SolrArgs solrArgs) throws Exception {
        String inputFileName = solrArgs.getInput();

        String outputFileName = solrArgs.getOutput();
        if (Strings.isNullOrEmpty(outputFileName)) {
            outputFileName = "stdout";
        }
        verifiy(inputFileName, "--input");

        Formatter.format(inputFileName, outputFileName);

    }

    public void indexFileToSolr(SolrArgs solrArgs) throws Exception {
        String url = solrArgs.getOutput();
        String inputFileName = solrArgs.getInput();

        verifiy(url, "--output");
        verifiy(inputFileName, "--input");


        SolrFeeder feeder = new SolrFeeder(url);
        feeder.setInputFileName(inputFileName);


        //".*Facet_[a-z]{2}_[a-z]{2}_facet,.*Stemmed_[a-z]{2}_[a-z]{2}_stemmed,.*Facet_facet"
        String ignoreFields = solrArgs.getIgnoreFields();
        List<String> ignoreFieldsList = Lists.newArrayList(Splitter.on(",").split(ignoreFields));
        feeder.setIgnoreFields(ignoreFieldsList);
        feeder.doFeed();
    }

    public void feedFileToSolr(SolrArgs solrArgs) throws Exception {
        Solr solr = new Solr(solrArgs.isShowHeaders());
        String output = solrArgs.getOutput();
        String inputFileName = solrArgs.getInput();

        verifiy(output, "--output");
        verifiy(inputFileName, "--input");

        String response = solr.feedFileToSolr(output, inputFileName);
        System.out.println(response);
    }

    public void retrieveFromSolr(SolrArgs solrArgs) throws Exception {
        Solr solr = new Solr(solrArgs.isShowHeaders());
        String input = solrArgs.getInput();
        String outputFileName = solrArgs.getOutput();


        if (Strings.isNullOrEmpty(outputFileName)) {
            outputFileName = "stdout";
        }
        verifiy(input, "--input");
        verifiy(outputFileName, "--output");

        solr.retrieveFromSolr(input, outputFileName);
    }

    public void deleteByQuery(SolrArgs solrArgs) throws Exception {
        Solr solr = new Solr(solrArgs.isShowHeaders());
        String input = solrArgs.getInput();
        String query = solrArgs.getQuery();

        verifiy(input, "--input");
        verifiy(query, "--query");

        String response = solr.deleteByQuery(input, query);
        System.out.println(response);
    }

    public void deleteAll(SolrArgs solrArgs) throws Exception {
        Solr solr = new Solr(solrArgs.isShowHeaders());
        String input = solrArgs.getInput();

        verifiy(input, "--input");

        String response = solr.deleteAll(input);
        System.out.println(response);
    }


    void verifiy(String param, String name) {
        if (Strings.isNullOrEmpty(param)) {
            jc.usage();
            throw new RuntimeException("The " + name + " is not defined.");
        }

    }

    void printHelp(String help) {
        JCommander.getConsole().println(help);

    }


}
