package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tblsoft on 13.04.19.
 */
    public class RandomReader extends AbstractReader {

    @Override
    public void read() {

        try {
            List<String> fields = getPropertyAsList("fields", new ArrayList<>());
            Long docCount = getPropertyAsInteger("docCount", 1L);

            for (int i = 0; i < docCount ; i++) {
                Document document = new Document();
                for(String field : fields) {
                    document.addField(field, UUID.randomUUID().toString());
                }
                executer.document(document);
            }
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
