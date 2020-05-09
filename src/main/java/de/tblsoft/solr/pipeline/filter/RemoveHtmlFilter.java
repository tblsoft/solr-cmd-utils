package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 8.4.20.
 * 
 * Remove html in all fields.
 */
public class RemoveHtmlFilter extends AbstractFilter {


    @Override
    public void init() {
        super.init();
    }


    @Override
    public void document(Document document) {
        for(Field field : document.getFields()) {
            List<String> newValues = new ArrayList<>();
            for(String value : field.getValues()) {
                newValues.add(SimpleMapping.removeHtml(value));
            }
            field.setValues(newValues);
        }
         super.document(document);
    }
    
}
