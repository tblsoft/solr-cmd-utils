package de.tblsoft.solr.pipeline.nlp.squad;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.nlp.squad.dto.Data;

/**
 * Created by tblsoft on 25.02.20.
 *
 * Extend this filter to easily proces squad data.
 */
public abstract class AbstractSquadFilter extends AbstractFilter {



    @Override
    public void document(Document document) {
        Data data = (Data) document.getField("data").getRawValue();
        data = data(data);
        document.setRawField("data", data, "json");
        super.document(document);
    }


    public abstract Data data(Data data);


}
