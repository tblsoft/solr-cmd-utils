package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;


public class FieldJoiner extends AbstractFilter {



    private String outputField;
    private String output;


    @Override
    public void init() {


        outputField = getProperty("outputField", null);
        verify(outputField, "For the FieldJoiner a outputField must be defined.");


        //The ${animal} jumped over the ${target}.
        output = getProperty("output", null);
        verify(output, "For the FieldJoiner a output must be defined.");




        super.init();
    }


    @Override
    public void document(Document document) {
        Map<String, String> documentMap = new HashMap<String, String>();
        for(Field field: document.getFields()) {
            documentMap.put(field.getName(),field.getValue());
        }


        StrSubstitutor sub = new StrSubstitutor(documentMap);
        String value = sub.replace(output);

        document.addField(outputField, value);



        super.document(document);
    }

}

