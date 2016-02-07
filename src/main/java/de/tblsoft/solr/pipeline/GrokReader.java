package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * Created by tblsoft on 07.02.16.
 */
public class GrokReader implements ReaderIF {

    private PipelineExecuter executer;

    private Reader reader;

    @Override
    public void read() {
        try {
            String filename = reader.getProperty().get("filename");

            // %{COMBINEDAPACHELOG}
            String grokPattern = reader.getProperty().get("grokPattern");

            // patterns/patterns
            String grokPatternPath = reader.getProperty().get("grokPatternPath");

            Grok grok = Grok.create(grokPatternPath);
            grok.compile(grokPattern);



            InputStream in = IOUtils.getInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = br.readLine()) != null) {
                Match gm = grok.match(line);
                gm.captures();
                Map<String, Object> m = gm.toMap();
                for(Map.Entry<String,Object> entry: m.entrySet()) {
                    Object value = entry.getValue();
                    executer.field(entry.getKey(), String.valueOf(value));
                }
                executer.endDocument();
            }
            br.close();
            executer.end();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void setSource(String source) {

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
