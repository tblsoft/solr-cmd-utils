package de.tblsoft.solr.util;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.HashMap;
import java.util.Map;

public class DocumentUtils {

	public static String normalizeFieldKey(String key) {
		key = key.replaceAll(" ", "_");
		key = key.replaceAll("[^a-zA-Z0-9_-]+","");
		return key;
	}

	public static Map<String, Field> mapFields(Document document) {
		Map<String, Field> fields = new HashMap<String, Field>();
		for (Field field : document.getFields()) {
			fields.put(field.getName(), field);
		}

		return fields;
	}
}
