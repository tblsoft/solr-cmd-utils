package de.tblsoft.solr.pipeline.filter;

import com.beust.jcommander.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serialize documents in elasticdump format
 */
public class ElasticdumpFileWriter extends AbstractFilter {
	BufferedWriter bw = null;
	FileWriter fw = null;
	private Gson gson;

	private String index;
	private String type;

	private String filepath;
	private String format; // bulk, elasticdump

	private boolean delete;

	private String idField;

	@Override
	public void init() {

		filepath = getProperty("filepath", null);
		verify(filepath, "For the JsonWriter a filepath must be defined.");

		delete = getPropertyAsBoolean("delete", Boolean.TRUE);

		index = getProperty("index", null);
		type = getProperty("type", null);
		format = getProperty("format", "elasticdump");
		idField = getProperty("idField", null);

		gson = new GsonBuilder().create();

		String absoluteFilepath = IOUtils.getAbsoluteFile(getBaseDir(), filepath);
		File file = new File(absoluteFilepath);
		try {
			if (file.exists()) {
				if (delete) {
					file.delete();
				}
			}

			if (!file.exists()) {
				file.createNewFile();
			}

			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		super.init();
	}

	static Object transformDatatype(String value) {
		if (NumberUtils.isNumber(value)) {
			try {
				Long intValue = Long.valueOf(value);
				return intValue;
			} catch (NumberFormatException e) {
				return value;
			}
		}
		return value;
	}

	@Override
	public void document(Document document) {
		Map<String, Object> jsonDocument = mapToJson(document);
		if(!jsonDocument.isEmpty()) {
			String id;
			if (Strings.isStringEmpty(idField)) {
				id = UUID.randomUUID().toString();
			} else {
				id = document.getFieldValue(idField);
			}

			String serializedDoc = null;
			if("bulk".equals(format)) {
				serializedDoc = serializeElasticBulk(id, jsonDocument);
			}
			else {
				serializedDoc = serializeElasticdump(id, jsonDocument);
			}

			try {
				bw.write(serializedDoc);
				bw.newLine();
			} catch (IOException e) {
				throw new RuntimeException("Error while writing data to file!", e);
			}
		}

		super.document(document);
	}

	public String serializeElasticBulk(String id, Map<String, Object> doc) {
		String bulkMethod = "{ \"index\" : { \"_index\" : \"" + index
				+ "\", \"_type\" : \"" + type + "\", \"_id\" : \"" + id
				+ "\" } }";
		String json = gson.toJson(doc);
		StringBuilder bulkLine = new StringBuilder();
		bulkLine.append(bulkMethod).append(" \n");
		bulkLine.append(json);
		return bulkLine.toString();
	}

	public String serializeElasticdump(String id, Map<String, Object> doc) {
		String source = gson.toJson(doc);
		String serializedDoc = "{\"_index\":\""+index+"\",\"_type\":\""+type+"\",\"_source\":"+source+"}";
		return serializedDoc;
	}

	static Map createExpandedValue(String flatName, Object value) {
		Map<String, Object> last = new HashMap<String, Object>();
		Map<String, Object> result = last;
		String[] parts = flatName.split("\\.");
		for(int i = 1; i < parts.length; i++) {
			String part = parts[i];
			if(i == parts.length-1) {
				last.put(part, value);
			}
			else {
				Map<String, Object> lastMap = new HashMap<String, Object>();
				last.put(part, lastMap);
				last = lastMap;
			}
		}
		return result;
	}

	static Map<String, Object> mapToJson(Document document) {
		Map<String, Object> jsonDocument = new HashMap<String, Object>();
		for (Field field : document.getFields()) {
			List<String> values = field.getValues();
			if (values == null || values.isEmpty()) {
				continue;
			}

			boolean fieldIsFlat= field.getName().contains(".");
			String fieldName = field.getName();
			Object fieldValue = field.getValues();

			if (values.size() == 1) {
				fieldValue = transformDatatype(field.getValue());
			}

			if(fieldIsFlat) {
				fieldValue = createExpandedValue(fieldName, fieldValue);
				fieldName = StringUtils.substringBefore(fieldName, ".");

				if(jsonDocument.containsKey(fieldName)) {
					Map existingValue = (Map) jsonDocument.get(fieldName);
					Map newValue = deepMerge(existingValue, (Map) fieldValue);
					jsonDocument.put(fieldName, newValue);
				} else {
					jsonDocument.put(fieldName, fieldValue);
				}
			} else {
				jsonDocument.put(fieldName, fieldValue);
			}
		}

		return jsonDocument;

	}

	public String createBulkMethod(String method, String index, String type,
			String id) {
		String bulkMethod = "{ \"" + method + "\" : { \"_index\" : \"" + index
				+ "\", \"_type\" : \"" + type + "\", \"_id\" : \"" + id
				+ "\" } }";
		return bulkMethod;
	}

	@Override
	public void end() {
		try {
			if(bw != null) {
				bw.close();
			}
			if(fw != null) {
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		super.end();
	}

	private static Map deepMerge(Map original, Map newMap) {
		for (Object key : newMap.keySet()) {
			if (newMap.get(key) instanceof Map && original.get(key) instanceof Map) {
				Map originalChild = (Map) original.get(key);
				Map newChild = (Map) newMap.get(key);
				original.put(key, deepMerge(originalChild, newChild));
			} else if (newMap.get(key) instanceof List && original.get(key) instanceof List) {
				List originalChild = (List) original.get(key);
				List newChild = (List) newMap.get(key);
				for (Object each : newChild) {
					if (!originalChild.contains(each)) {
						originalChild.add(each);
					}
				}
			} else {
				original.put(key, newMap.get(key));
			}
		}
		return original;
	}
}
