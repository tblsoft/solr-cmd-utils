package de.tblsoft.solr.pipeline.filter;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JsonWriter extends AbstractFilter {


    private Gson gson;

    private String type;

    private String location;
    
    private String elasticMappingLocation;
    
    private boolean delete;
    
    private String idField;

    private String absoluteFilename;





    @Override
    public void init() {
        type = getProperty("type", "file");
        Boolean pretty = getPropertyAsBoolean("pretty", false);
        location = getProperty("location", null);
        if("file".equals(type)) {
            absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), location);
        }
        verify(location, "For the JsonWriter a location must be defined.");

        delete = getPropertyAsBoolean("delete", Boolean.TRUE);
        elasticMappingLocation = getProperty("elasticMappingLocation", null);

        idField = getProperty("idField", null);

        GsonBuilder builder = new GsonBuilder();
        if(pretty) {
            builder = builder.setPrettyPrinting();
        }
        gson = builder.create();

        if(delete && "file".equals(type)) {
            FileUtils.deleteQuietly(new File(absoluteFilename));

        } else

        if("elastic".equals(type)) {
            if(delete && !"elasticupdate".equals(type)) {
                try {
                    String indexUrl = ElasticHelper.getIndexUrl(location);
                    HTTPHelper.delete(indexUrl);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        	if(elasticMappingLocation != null) {
        		String mappingJson;
				try {
					String indexUrl = ElasticHelper.getIndexUrl(location);
                    File elasticMappingFile = new File(IOUtils.getAbsoluteFile(getBaseDir(),elasticMappingLocation));

					mappingJson = FileUtils.readFileToString(elasticMappingFile);
            		HTTPHelper.put(indexUrl, mappingJson);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

        	}
        } 


        super.init();
    }


    Object transformDatatype(List<String> values) {
        List<Long> longList = new ArrayList<Long>();
        for(String value: values) {
            Object transformedValue = transformDatatype(value);
            if(transformedValue instanceof Long) {
                longList.add((Long) transformedValue);
            } else {
                return values;
            }
        }
        return longList;
    }

    static Object transformDatatype(String value) {
        if(NumberUtils.isNumber(value)) {
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

            String json = gson.toJson(jsonDocument);



            if("elastic".equals(type)) {
            	String elasticLocation = location;
            	if(idField != null) {
            		String id = document.getFieldValue(idField);
            		elasticLocation = ElasticHelper.getIndexUrlWithId(location, id);
            	}
            	
                HTTPHelper.post(elasticLocation, json);
            } else if ("elasticupdate".equals(type)) {
            	try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if(idField == null) {
            		throw new RuntimeException("For a document update a id field is required.");
            	}
            	String id = document.getFieldValue(idField);
        		String elasticUpdateLocation = ElasticHelper.getUpdateUrl(location, id);
        		
        		String updateJson = "{ \"doc\" : " + json + "}";
        		
        		String response = HTTPHelper.post(elasticUpdateLocation, updateJson);
        		JsonElement jsonResponse = gson.fromJson(response, JsonElement.class);
        		if(jsonResponse.getAsJsonObject().get("status") != null ) {
        			int status = jsonResponse.getAsJsonObject().get("status").getAsInt();
	        		if(status == 404) {
	        			String elasticLocation = ElasticHelper.getIndexUrlWithId(location, id);
	        			HTTPHelper.post(elasticLocation, json);
	        		}
	        	}
        		
        		
            }
            else if ("file".equals(type)) {
                try {
                    FileUtils.writeStringToFile(new File(absoluteFilename), json, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if ("stdout".equals(type)) {
                System.out.println(json);
            }



        }

        super.document(document);
    }


    public static String mapToJsonString(List<Document> documentList) {
        List<Map<String,Object>> documentMap = new ArrayList<Map<String, Object>>();
        for(Document document: documentList) {
            documentMap.add(mapToJson(document));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(documentMap);
        return json;

    }

    static Map<String, Object> mapToJson(Document document) {
        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        for(Field field: document.getFields()) {
            List<String> values = field.getValues();
            if(values == null || values.isEmpty()) {
                continue;
            }
            if(values.size() == 1){
                jsonDocument.put(field.getName(), transformDatatype(field.getValue()));
            } else {
                jsonDocument.put(field.getName(), field.getValues());
            }

        }

        return jsonDocument;

    }



    @Override
    public void end() {
        super.end();
    }



}

