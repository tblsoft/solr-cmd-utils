package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
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

    private Grok grok;

    @Override
    public void read() {
        try {
            String filename = reader.getProperty().get("filename");

            // %{COMBINEDAPACHELOG}
            String grokPattern = reader.getProperty().get("grokPattern");

            // patterns/patterns
            String grokPatternPath = reader.getProperty().get("grokPatternPath");


            // https://www.elastic.co/guide/en/logstash/current/plugins-codecs-multiline.html
            String multilinePattern = reader.getProperty().get("multilinePattern");
            String multilineNegate = reader.getProperty().get("multilineNegate");
            String multilineWhat = reader.getProperty().get("multilineWhat");

            boolean isMultiline = false;
            if(!Strings.isNullOrEmpty(multilinePattern)) {
                isMultiline = true;
            }




            grok = Grok.create(grokPatternPath);
            grok.compile(grokPattern);



            InputStream in = IOUtils.getInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String line;
            StringBuilder multilineBuilder =  new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(isMultiline) {
                    if(line.matches(multilinePattern)) {
                        processLine(multilineBuilder.toString());
                        multilineBuilder = new StringBuilder(line);
                        continue;
                    } else {
                        multilineBuilder.append(" ").append(line);
                        continue;
                    }

                } else {
                    processLine(line);
                }



            }
            br.close();
            executer.end();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    void processLine(String line) {
        System.out.println(line);
        Match gm = grok.match(line);
        gm.captures();
        Map<String, Object> m = gm.toMap();
        for(Map.Entry<String,Object> entry: m.entrySet()) {
            Object value = entry.getValue();
            executer.field(entry.getKey(), String.valueOf(value));
        }
        executer.endDocument();

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
