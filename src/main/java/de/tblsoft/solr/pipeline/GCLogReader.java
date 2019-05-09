package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 07.02.16.
 */
public class GCLogReader extends AbstractReader {

    private static Logger LOG = LoggerFactory.getLogger(GCLogReader.class);




    private Grok grok;

    private Boolean keepRaw;

    private String filename;

    private String currentFileName;

    @Override
    public void read() {
        try {
            String conffilename = getProperty("filename", null);
            filename = IOUtils.getAbsoluteFile(getBaseDir(),conffilename);

            keepRaw = getPropertyAsBoolean("keepRaw", false);

            String grokPatternPath = "src/main/grok/patterns/gc";

            grok = Grok.create(grokPatternPath);
            grok.addPatternFromFile("src/main/grok/patterns/patterns");
            //grok.compile("Total time for which application threads were stopped: %{BASE16FLOAT:foo1} seconds");
            grok.compile("^%{TIMESTAMP_ISO8601:date}");

            List<String> fileList = IOUtils.getFiles(filename);

            for (String file : fileList) {
                currentFileName = file;

                InputStream in = IOUtils.getInputStream(file);
                InputStreamReader isr = new InputStreamReader(in);
                BufferedReader br = new BufferedReader(isr);

                String line;

                while ((line = br.readLine()) != null) {

                    processLine(line);
                }
                br.close();
            }
            executer.end();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    void processLine(String line) {

        //executer.field("raw", line);


        LOG.info(line);
        Match gm = grok.match(line);

        gm.captures();
        Map<String, Object> m = gm.toMap();
        Document document = new Document();
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            Object value = entry.getValue();
            document.addField(entry.getKey(),String.valueOf(value));
        }

        if (!m.isEmpty()) {
            document.addField("filename", currentFileName);
            if (keepRaw) {
                document.addField("raw", line);
            }
            executer.document(document);
        }

    }


    @Override
    public void setPipelineExecuter(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void end() {

    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }
}
