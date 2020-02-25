package de.tblsoft.solr.pipeline.nlp.squad;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.pipeline.AbstractReader;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.nlp.squad.dto.Data;
import de.tblsoft.solr.pipeline.nlp.squad.dto.Squad;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by tblsoft on 25.02.20.
 *
 * Squad reader
 * https://rajpurkar.github.io/SQuAD-explorer/
 *
 * its not streamable - if you need it streamable, feel free to implement it
 *
 * it just operates on raw fields
 */
public class SquadReader extends AbstractReader {

    public void read() {

        try {
            String filename = getProperty("filename", null);
            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(),filename);
            InputStream in = IOUtils.getInputStream(absoluteFilename);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            Squad squad = objectMapper.readValue(in, Squad.class);
            in.close();

            for(Data data: squad.getData()) {
               Document document = new Document();
               document.setRawField("data", data, "json");
               executer.document(document);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
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
