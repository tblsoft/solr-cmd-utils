package de.tblsoft.solr.util;


import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tbl 26.5.21
 * Map a document to a map
 */
public class DocumentMapper {

	public static Map<String, Object> toMap(Document document) {
		Map<String, Object> outputDocument = new HashMap<>();
		for(Field field : document.getFields()) {
			if(field.getValues().size() == 1) {
				outputDocument.put(field.getName(), field.getValue());
			} else {
				outputDocument.put(field.getName(), field.getValues());
			}
		}
		return outputDocument;
	}

}
