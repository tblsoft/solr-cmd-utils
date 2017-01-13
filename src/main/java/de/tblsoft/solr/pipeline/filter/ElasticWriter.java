package de.tblsoft.solr.pipeline.filter;

import com.beust.jcommander.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ElasticWriter extends AbstractFilter {

	private Gson gson;

	private String type;

	private String location;

	private String elasticMappingLocation;

	private boolean delete;

	private String idField;

	private List<Document> buffer = new ArrayList<Document>();

    private int bufferSize;

	@Override
	public void init() {

        bufferSize = getPropertyAsInt("bufferSize", 10000);
		location = getProperty("location", null);
		verify(location, "For the JsonWriter a location must be defined.");

		delete = getPropertyAsBoolean("delete", Boolean.TRUE);
		elasticMappingLocation = getProperty("elasticMappingLocation", null);

		idField = getProperty("idField", null);

		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();

		if (delete && !"elasticupdate".equals(type)) {
			try {
				String indexUrl = ElasticHelper.getIndexUrl(location);
				HTTPHelper.delete(indexUrl);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}
		if (elasticMappingLocation != null) {
			String mappingJson;
			try {
				String indexUrl = ElasticHelper.getIndexUrl(location);
				File elasticMappingFile = new File(IOUtils.getAbsoluteFile(
						getBaseDir(), elasticMappingLocation));

				mappingJson = FileUtils.readFileToString(elasticMappingFile);
				HTTPHelper.put(indexUrl, mappingJson);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}

		}

		super.init();
	}

	Object transformDatatype(List<String> values) {
		List<Long> longList = new ArrayList<Long>();
		for (String value : values) {
			Object transformedValue = transformDatatype(value);
			if (transformedValue instanceof Long) {
				longList.add((Long) transformedValue);
			} else {
				return values;
			}
		}
		return longList;
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

	void procesBuffer() {
		try {
			StringBuilder bulkRequest = new StringBuilder();
			for (Document document : buffer) {
				Map<String, Object> jsonDocument = mapToJson(document);
				if (jsonDocument.isEmpty()) {
					continue;
				}
				String id;
				if (Strings.isStringEmpty(idField)) {
					id = UUID.randomUUID().toString();
				} else {
					id = document.getFieldValue(idField);
				}
				String index = ElasticHelper.getIndexFromUrl(location);
				String type = ElasticHelper.getTypeFromUrl(location);
				String bulkMethod = createBulkMethod("index", index, type, id);
				String json = gson.toJson(jsonDocument);
				bulkRequest.append(bulkMethod).append(" \n");
				bulkRequest.append(json).append(" \n");
			}

			String bulkUrl = ElasticHelper.getBulkUrl(location);
			HTTPHelper.post(bulkUrl, bulkRequest.toString());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void document(Document document) {
		if (buffer.size() <= bufferSize) {
			buffer.add(document);
		} else {
			procesBuffer();
			buffer = new ArrayList<Document>();
		}

		super.document(document);
	}

	public static String mapToJsonString(List<Document> documentList) {
		List<Map<String, Object>> documentMap = new ArrayList<Map<String, Object>>();
		for (Document document : documentList) {
			documentMap.add(mapToJson(document));
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(documentMap);
		return json;

	}

	static Map<String, Object> mapToJson(Document document) {
		Map<String, Object> jsonDocument = new HashMap<String, Object>();
		for (Field field : document.getFields()) {
			List<String> values = field.getValues();
			if (values == null || values.isEmpty()) {
				continue;
			}
			if (values.size() == 1) {
				jsonDocument.put(field.getName(),
						transformDatatype(field.getValue()));
			} else {
				jsonDocument.put(field.getName(), field.getValues());
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
		procesBuffer();
		super.end();
	}

}
