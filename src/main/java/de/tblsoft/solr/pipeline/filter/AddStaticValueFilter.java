package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * add static values to a field of every document
 */
public class AddStaticValueFilter extends AbstractFilter {



    private String fieldName;
    private List<String> values;


    @Override
    public void init() {


        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the AddStaticValueFilter a fieldName must be defined.");

        values = getPropertyAsList("values", new ArrayList<>());





        super.init();
    }


    @Override
    public void document(Document document) {

        Field field = document.getField(fieldName);
        if(field == null) {
            field = new Field(fieldName, new ArrayList<>());
            document.setField(field);
        }
        field.getValues().addAll(values);
        super.document(document);
    }

}

