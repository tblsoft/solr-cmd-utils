package de.tblsoft.solr.pipeline;


import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Reader;

import java.util.UUID;


/**
 * Created by tblsoft on 07.04.18.
 * Generate random documents.
 */
public class DocumentGeneratorReader extends AbstractReader {


    private long count = 1;

    private long fieldCount = 1;

    public void read() {
        count = getPropertyAsInteger("count", 1L);
        fieldCount = getPropertyAsInteger("fieldCount", 1L);

        for (int i = 0; i < count; i++) {
            Document document = DocumentBuilder.document().
                    field("count", String.valueOf(i)).
                    create();

            for (int j = 0; j < fieldCount; j++) {
                document.setField("field" + j, UUID.randomUUID().toString());

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
