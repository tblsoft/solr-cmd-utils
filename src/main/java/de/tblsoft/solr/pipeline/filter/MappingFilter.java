package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.bean.FieldComperator;

import de.tblsoft.solr.util.DateUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class MappingFilter extends AbstractFilter {

	private Map<String, List<String>> mapping = new HashMap<String, List<String>>();
	private Map<String, List<String>> mappingFunctions = new HashMap<String, List<String>>();
	private Map<String, String> joins = new HashMap<String, String>();

    private boolean sortFieldsByName = false;
    private boolean addEmptyFieldIfNotExists = false;


	@Override
	public void init() {

        sortFieldsByName = getPropertyAsBoolean("sortFieldsByName", false);
        addEmptyFieldIfNotExists = getPropertyAsBoolean("addEmptyFieldIfNotExists", false);

		List<String> tempMapping = getPropertyAsList("mapping",
				new ArrayList<String>());
		for (String v : tempMapping) {
			if (v.startsWith("join:")) {
				v = v.replace("join:", "");
				String[] s = v.split("=",2);
				joins.put(s[0], s[1]);
			} else {
				String[] s = v.split("->");

				String[] f = s[1].split(Pattern.quote("|"));

				List<String> mappingList = mapping.get(s[0]);
				if (mappingList == null) {
					mappingList = new ArrayList<String>();
				}
				mappingList.add(f[0]);
				mapping.put(s[0], mappingList);
				List<String> functions = new ArrayList<String>();
				for (int i = 1; i < f.length; i++) {
					functions.add(f[i]);
				}
				mappingFunctions.put(f[0], functions);
			}
		}
		super.init();
	}

	public String executeFunction(String function, String value) {

        if("md5".equals(function)) {
            return DigestUtils.md5Hex(value);
        } else if ("lowercase".equals(function)) {
            return StringUtils.lowerCase(value);
        } else if ("trim".equals(function)) {
            return StringUtils.trim(value);
        } else if ("toSolrDate".equals(function)) {
			return DateUtils.toSolrDate(value);
		} else if ("uniq".equals(function)) {
			return value;
		}

		throw new IllegalArgumentException("The function: " + function
				+ " is not implemented.");
	}


	public void executeFieldFunction(String function, Field field) {

		if("md5".equals(function)) {
			return;
		} else if ("lowercase".equals(function)) {
			return;
		} else if ("trim".equals(function)) {
			return;
		} else if ("toSolrDate".equals(function)) {
			return;
		} else if ("uniq".equals(function)) {
			Set<String> uniqValues= new HashSet<String>();
			uniqValues.addAll(field.getValues());
			field.setValues(new ArrayList(uniqValues));
			return;
		}
		throw new IllegalArgumentException("The function: " + function
				+ " is not implemented.");
	}

	@Override
	public void document(Document document) {
		Document mappedDocument = new Document();
		for (Field f : document.getFields()) {
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
							newValue = executeFunction(function, newValue);
						}
						newValues.add(newValue);
					}

					mappedDocument.addField(mappedName, newValues);
					Field mappedField = mappedDocument.getField(mappedName);
					for (String function : mappedFunctions) {
						executeFieldFunction(function, mappedField);
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
			mappedDocument.addField(entry.getKey(), value);
		}

		if (mappedDocument.getFields().size() != 0) {

			addMissingFields(mappedDocument);
            sortFieldsByName(mappedDocument);
			super.document(mappedDocument);
		}
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
