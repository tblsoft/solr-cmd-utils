package de.tblsoft.solr.pipeline.filter;

import java.util.ArrayList;
import java.util.List;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

/**
 * Created by tblsoft on 03.04.16.
 * 
 * Count the tokens for a specific field.
 */
public class CharCounterFilter extends AbstractFilter {
	

    
    private String fieldName;
	private String charCountFieldName;

    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the TokenCounterFilter a fieldName property must be defined.");

        charCountFieldName = getProperty("charCountFieldName", "charCount");
        super.init();
    }


    @Override
    public void document(Document document) {
        List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
        int charCount = 0;
        for(String value: values) {
            charCount = charCount + value.length();
        }
        document.setField(charCountFieldName, String.valueOf(charCount));
        super.document(document);
    }


}
