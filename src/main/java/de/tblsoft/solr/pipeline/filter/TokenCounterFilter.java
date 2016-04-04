package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

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
    public void field(String name, String value) {
        super.field(name,value);
        if(!name.matches(fieldName)) {
            return;
        }
        StringTokenizer tokenizer = new StringTokenizer(value);
        int tokenCount = tokenizer.countTokens();
        super.field(tokenCountFieldName, String.valueOf(tokenCount));
    }
}
