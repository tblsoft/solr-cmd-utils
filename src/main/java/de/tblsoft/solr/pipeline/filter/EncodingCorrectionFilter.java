package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

import java.util.List;

/**
 * Created by tblsoft on 03.04.16.
 * 
 * Count the tokens for a specific field.
 */
public class EncodingCorrectionFilter extends AbstractFilter {
	
	private List<String> fieldName;
	
    @Override
    public void init() {
        fieldName = getPropertyAsList("fieldName", null);
        verify(fieldName, "For the EncodingCorrectionFilter a fieldName list property must be defined.");
        super.init();
    }

}
