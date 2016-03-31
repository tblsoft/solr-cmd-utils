package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.Map;


public class FieldJoiner extends AbstractFilter {

    Map<String, String> document = new HashMap<String, String>();

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
    public void field(String name, String value) {
        document.put(name,value);

        super.field(name,value);

    }





    @Override
    public void endDocument() {


        if(document.isEmpty()) {
            super.endDocument();
            return;
        }
        StrSubstitutor sub = new StrSubstitutor(document);
        String value = sub.replace(output);
        super.field(outputField, value);

        document.clear();
        super.endDocument();
    }
}

