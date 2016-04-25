package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by tblsoft on 03.04.16.
 * 
 * Count the tokens for a specific field.
 */
public class TokenCounterFilter extends AbstractFilter {
	
	private String fieldName;
	private String tokenCountFieldName;

    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the TokenCounterFilter a fieldName property must be defined.");

        tokenCountFieldName = getProperty("tokenCountFieldName", "tokenCount");
        super.init();
    }


    @Override
    public void document(Document document) {
        List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
        int tokenCount = 0;
        for(String value: values) {
            StringTokenizer tokenizer = new StringTokenizer(value);
            tokenCount = tokenCount + tokenizer.countTokens();
        }
        document.setField(tokenCountFieldName, String.valueOf(tokenCount));


        super.document(document);
    }

}
