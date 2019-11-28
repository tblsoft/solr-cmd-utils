package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.bean.FieldComperator;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.*;
import java.util.Map.Entry;

public class MappingFilter extends AbstractSubFieldFilter {

	private List<String> prefixMapping = new ArrayList<>();
	private Map<String, List<String>> mapping = new HashMap<String, List<String>>();
	private Map<String, List<String>> mappingFunctions = new HashMap<String, List<String>>();
	private Map<String, String> joins = new HashMap<String, String>();
	private Map<String, String> datatypes = new HashMap<String, String>();


    private boolean sortFieldsByName = false;
    private boolean addEmptyFieldIfNotExists = false;

    private boolean appendFields = false;

    private SimpleMapping simpleMapping;

	@Override
	public void init() {

        sortFieldsByName = getPropertyAsBoolean("sortFieldsByName", false);
        addEmptyFieldIfNotExists = getPropertyAsBoolean("addEmptyFieldIfNotExists", false);
		appendFields = getPropertyAsBoolean("appendFields", false);
		simpleMapping = new SimpleMapping(getPropertyAsList("mapping", new ArrayList<String>()), getPropertyAsList("config", new ArrayList<String>()));
		mapping = simpleMapping.getMapping();
		prefixMapping = getPropertyAsList("prefixMapping", new ArrayList<>());
		mappingFunctions = simpleMapping.getMappingFunctions();
		joins = simpleMapping.getJoins();
		datatypes = initDatatypes(getPropertyAsList("datatypes", new ArrayList<String>()));

		super.init();
	}

	Map<String, String> initDatatypes(List<String> datatypeConfig) {
		Map<String, String> datatypes = new HashMap<String, String>();
		for (String datatypeConfigEntry : datatypeConfig) {
			String[] s = datatypeConfigEntry.split("->");
			if(s.length == 2) {
				datatypes.put(s[0], s[1]);
			}
		}
		return datatypes;

	}



	public List<Document> processDocument(Document document) {
		Document mappedDocument;
		if(!appendFields) {
			mappedDocument = new Document();
		} else {
			mappedDocument = new Document();
			mappedDocument.getFields().addAll(document.getFields());
		}

		for (Field f : document.getFields()) {
			for(String prefix: prefixMapping) {
				if(f.getName().startsWith(prefix)) {
					mappedDocument.addField(f);
				}
			}

			List<String> mappedNameList = mapping.get(f.getName());
			if(mappedNameList == null) {
				continue;
			}

			for (String mappedName : mappedNameList) {
				List<String> mappedFunctions = mappingFunctions.get(mappedName);
				if (!Strings.isNullOrEmpty(mappedName)) {
					List<String> newValues = new ArrayList<String>();
					for (String value : f.getValues()) {
						String newValue = value;
						for (String function : mappedFunctions) {
							newValue = simpleMapping.executeFunction(function, newValue);
						}
						newValues.add(newValue);
					}

					mappedDocument.addField(mappedName, newValues);
					Field mappedField = mappedDocument.getField(mappedName);
					mappedField.setDatatype(datatypes.get(mappedField.getName()));
					for (String function : mappedFunctions) {
						simpleMapping.executeFieldFunction(function, mappedField);
					}
				}
			}
		}

		for (Map.Entry<String, String> entry : joins.entrySet()) {
			Map<String, String> documentMap = new HashMap<String, String>();
			for (Field field : document.getFields()) {
				documentMap.put(field.getName(), field.getValue());
			}
			StrSubstitutor sub = new StrSubstitutor(documentMap);
			String value = sub.replace(entry.getValue());
			mappedDocument.setField(entry.getKey(), value);
		}

		if (mappedDocument.getFields().size() != 0) {

			addMissingFields(mappedDocument);
			sortFieldsByName(mappedDocument);
			return Arrays.asList(mappedDocument);
		}
		return null;
	}

    void sortFieldsByName(Document mappedDocument) {
        if(!sortFieldsByName) {
            return;
        }
        Collections.sort(mappedDocument.getFields(), new FieldComperator());

    }
	
	void addMissingFields(Document mappedDocument) {
        if(!addEmptyFieldIfNotExists) {
            return;
        }
		Set<String> mappedFieldNames = new HashSet<String>();
		for(Field field : mappedDocument.getFields()) {
			mappedFieldNames.add(field.getName());
		}
		for(Entry<String, List<String>> entry :mapping.entrySet()) {
            for(String mappedName: entry.getValue()) {
			    if(!mappedFieldNames.contains(mappedName)) {
				    mappedDocument.addField(mappedName, "");
			    }
            }
		}
	}

}
