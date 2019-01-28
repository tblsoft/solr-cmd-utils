package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueMappingFilter extends AbstractFilter {

	private Map<String, String> mapping = new HashMap<>();
    private String fieldName;
    private Boolean passUndefinedValues = Boolean.TRUE;

	@Override
	public void init() {

        fieldName = getProperty("fieldName", null);
		passUndefinedValues = getPropertyAsBoolean("passUndefinedValues", Boolean.TRUE);
        verify("fieldName", "The field fieldName must be configured.");
		mapping = getPropertyAsMapping("mapping");
		super.init();
	}

	@Override
	public void document(Document document) {
		List<String> values = document.getFieldValues(fieldName);
		if(values == null) {
			super.document(document);
			return;
		}
		List<String> mappedValues = new ArrayList<>();
		for (String value : values) {
			String mappedValue = mapping.get(value);
			if(mappedValue == null && passUndefinedValues) {
				mappedValues.add(value);
			} else {
				mappedValues.add(mappedValue);
			}
		}
		document.setField(fieldName, mappedValues);
		super.document(document);
	}

	


}
