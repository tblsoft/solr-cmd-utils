package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft 25.12.16.
 */
public class ValidationFilter extends AbstractFilter {

    private String validationField;

    private List<String> requiredFields = new ArrayList<String>();


    @Override
    public void init() {
        validationField = getProperty("validationField", "validation");
        requiredFields = getPropertyAsList("requiredFields", new ArrayList<String>());


        super.init();

    }

    @Override
    public void document(Document document) {
        checkRequiredFields(document);
        super.document(document);

    }

    void checkRequiredFields(Document document) {
        for(String requiredField: requiredFields) {
            List<String> values = document.getFieldValues(requiredField);
            if(!validateRequiredField(values)) {
                document.addField(validationField, requiredField + "_missingRequired");
            }

        }


    }

    boolean validateRequiredField(List<String> values) {
        if(values == null || values.isEmpty()) {
            return false;
        }
        String value = Iterables.getFirst(values,null);
        if(Strings.isNullOrEmpty(value)) {
            return false;
        }
        return true;
    }


}
