package de.tblsoft.solr.pipeline.filter;

import java.util.List;

import de.tblsoft.solr.pipeline.AbstractFilter;

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


    @Override
    public void field(String name, String value) {
        if(!fieldName.contains(name)) {
        	super.field(name,value);
        	return;
        }
        byte[] bytes = value.getBytes();
        String hexString = Integer.toHexString(bytes[1]);
        System.out.println(hexString);
    
    }
}
