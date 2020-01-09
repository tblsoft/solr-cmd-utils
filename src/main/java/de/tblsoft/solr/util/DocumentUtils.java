package de.tblsoft.solr.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DocumentUtils {


	public static void writeToFile(File file, Document document) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		file.getParentFile().mkdirs();
		objectMapper.writeValue(IOUtils.getOutputStream(file), document);
	}

	public static Document readFromFile(File file) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper.readValue(IOUtils.getInputStream(file), Document.class);
	}

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
