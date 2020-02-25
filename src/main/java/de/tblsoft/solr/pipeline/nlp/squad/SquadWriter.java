package de.tblsoft.solr.pipeline.nlp.squad;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import de.tblsoft.solr.pipeline.nlp.squad.dto.Data;
import de.tblsoft.solr.pipeline.nlp.squad.dto.Squad;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by tblsoft on 25.02.20.
 *
 * Squad writer
 * https://rajpurkar.github.io/SQuAD-explorer/
 *
 * Write the squad data to a json file.
 */
public class SquadWriter extends AbstractSquadFilter {

    private String absoluteFilename;
    
    private Squad squad;

    @Override
    public void init() {

        try {
            String filename = getProperty("filename", null);
            String squadVersion = getProperty("squadVersion", "v2.0");
            absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);
            squad = new Squad();
            squad.setData(new ArrayList<>());
            squad.setVersion(squadVersion);

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        super.init();
    }



    @Override
    public Data data(Data data) {
        squad.getData().add(data);
        return data;
    }


    @Override
    public void end() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(absoluteFilename), squad);
        } catch (IOException e) {
            throw new RuntimeException("Could not serialize sqad file: " + absoluteFilename, e);
        }

    }

}
