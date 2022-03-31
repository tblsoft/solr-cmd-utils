package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.elastic.AliasManager;
import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.ElasticBulkResponse;
import de.tblsoft.solr.pipeline.bean.ElasticResponse;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class ElasticWriter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(ElasticWriter.class);

    private Gson gson;
    private String jsonDatePattern;

    private String type;

    protected String location;

    private String elasticMappingLocation;

    private boolean delete;

    private String idField;
    private Boolean hashId = false;

    protected List<Document> buffer = new ArrayList<Document>();

    private int bufferSize = 10000;

    private long currentBufferContentSize = 0;

    private long maxBufferContentSize = 3000000L;

    private boolean detectNumberValues = true;

    private boolean failOnError = true;

    private String indexUrl;
    private Integer housekeepingCount;
    private String housekeppingStrategy;

    private Boolean housekeepingEnabled = false;
    private String alias;

    private String bulkMethodFieldName;

    private boolean includeTypeName;

    @Override
    public void init() {

        bulkMethodFieldName = getProperty("bulkMethodFieldName", null);
        alias = getProperty("alias", null);
        housekeepingEnabled = getPropertyAsBoolean("housekeepingEnabled", housekeepingEnabled);
        housekeepingCount = getPropertyAsInt("housekeepingCount", 5);
        housekeppingStrategy = getProperty("housekeppingStrategy", "linear");

        bufferSize = getPropertyAsInt("bufferSize", 10000);
        location = getProperty("location", null);
        verify(location, "For the JsonWriter a location must be defined.");

        failOnError = getPropertyAsBoolean("failOnError", Boolean.TRUE);
        delete = getPropertyAsBoolean("delete", Boolean.TRUE);
        detectNumberValues = getPropertyAsBoolean("detectNumberValues", Boolean.TRUE);
        elasticMappingLocation = getProperty("elasticMappingLocation", null);

        idField = getProperty("idField", null);
        hashId = getPropertyAsBoolean("hashId", false);
        includeTypeName = getPropertyAsBoolean("includeTypeName", false);
        jsonDatePattern = getProperty("jsonDatePattern", "yyyy-MM-dd'T'HH:mm:ssZ");

        GsonBuilder builder = new GsonBuilder().setDateFormat(jsonDatePattern);
        gson = builder.create();

        try {
            if(housekeepingEnabled) {
                indexUrl = AliasManager.getElasticUrlWithDatePattern(location);
            } else {
                indexUrl = ElasticHelper.getIndexUrl(location);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        if (delete && !"elasticupdate".equals(type)) {
            String response = HTTPHelper.delete(indexUrl);
            ElasticResponse elasticResponse = gson.fromJson(response, ElasticResponse.class);
            if(!Boolean.TRUE.equals(elasticResponse.getAcknowledged())) {
                LOG.error("Could not delete index: {} error: {} ",
                        indexUrl, response);
            }
            LOG.debug("delete elastic index for url {} and response {}", indexUrl, response);
        }
        if (elasticMappingLocation != null) {
            try {
                String absoluteElasticMappingLocation = IOUtils.getAbsoluteFile(
                        getBaseDir(), elasticMappingLocation);
                String mappingJson = IOUtils.getString(absoluteElasticMappingLocation);
                String mappingUrl = ElasticHelper.getIndexUrl(indexUrl);
                int statusCode = HTTPHelper.getStatusCode(mappingUrl);
                LOG.debug("status code {}", statusCode);

                if(includeTypeName) {
                    mappingUrl = mappingUrl + "?include_type_name=true";
                }
                LOG.debug("mapping url: {} mappingJson: {}", mappingUrl, mappingJson );
                if(statusCode == 404) {
                    String response = HTTPHelper.put(mappingUrl, mappingJson, "application/json");
                    ElasticResponse elasticResponse = gson.fromJson(response, ElasticResponse.class);
                    if(!Boolean.TRUE.equals(elasticResponse.getAcknowledged())) {
                        LOG.error("Could not create mapping for url: {} mappingJson: {} error: {} ",
                                mappingUrl, mappingJson, response);
                        throw new RuntimeException("Could not create mapping");
                    }

                    LOG.debug("mapping response {}", response);
                }

                if(housekeepingEnabled) {
                    // check if alias exists, if not create it
                    String aliasFromUrl = ElasticHelper.getIndexFromUrl(location);
                    if(!AliasManager.exists(indexUrl, aliasFromUrl)) {
                        String index = ElasticHelper.getIndexFromUrl(indexUrl);
                        LOG.info("Create the alias: {} for index: {}", aliasFromUrl, index);
                        AliasManager.switchAlias(indexUrl, aliasFromUrl, new ArrayList<>(), index);

                    }
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        super.init();
    }

    Object transformRawValue(Field field) {
        String dataType = field.getDatatype();
        if(dataType.equals("json")) {
            return gson.toJsonTree(field.getRawValue());
        }

        return null;
    }


    static Object transformDatatype(Field field, boolean detectNumberValues) {

        String value = field.getValue();
        String dataType = field.getDatatype();
        if(dataType == null) {
            dataType = "string";
        }

        if(dataType.equals("list.string")) {
            return field.getValues();
        }
        if(dataType.equals("json")) {
            Gson gson = new Gson();
            return gson.toJson(field.getRawValue());
        }

        if(!detectNumberValues) {
            return value;

        }
        if(field.getDatatype() != null && "string".equals(field.getDatatype())) {
            return  value;
        }

        if(!detectNumberValues) {
            return value;
        }
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

    protected void procesBuffer() {
        if(buffer.size() == 0) {
            return;
        }
        StringBuilder bulkRequest = new StringBuilder();
        try {

            for (Document document : buffer) {
                Map<String, Object> jsonDocument = mapToJson(document, detectNumberValues);
                if (jsonDocument.isEmpty()) {
                    continue;
                }
                String id;
                if (Strings.isNullOrEmpty(idField)) {
                    id = UUID.randomUUID().toString();
                } else if(hashId) {
                    id= DigestUtils.md5Hex(document.getFieldValue(idField));
                } else {
                    id = document.getFieldValue(idField);
                }
                String index = ElasticHelper.getIndexFromUrl(indexUrl);
                String type = ElasticHelper.getTypeFromUrl(location);

                if(isDeleteBulkMethod(document)) {
                    String bulkMethod = createBulkMethod("delete", index, type, id);
                    bulkRequest.append(bulkMethod).append(" \n");
                } else {
                    String bulkMethod = createBulkMethod("index", index, type, id);
                    String json = gson.toJson(jsonDocument);
                    bulkRequest.append(bulkMethod).append(" \n");
                    bulkRequest.append(json).append(" \n");
                }


            }

            String bulkUrl = ElasticHelper.getBulkUrl(indexUrl);
            LOG.debug("bulk url: {} bulkRequest: {}", bulkUrl, bulkRequest);
            String response = HTTPHelper.post(bulkUrl, bulkRequest.toString(), "application/json");
            ElasticBulkResponse elasticBulkResponse = gson.fromJson(response, ElasticBulkResponse.class);

            if(Boolean.TRUE.equals(elasticBulkResponse.getErrors())) {
                LOG.error("There was an error processing the bulk request {} with message: {}", bulkRequest.toString(), response );
                throw new Exception("There was an error processing the bulk request");
            }
        } catch (Exception e) {
            LOG.info("There was an error processing the bulk request: " + e.getMessage());
            LOG.info(bulkRequest.toString());

            if(failOnError) {
                throw new RuntimeException(e);
            } else {
                LOG.info("Continue processing ... ");
            }
        }

    }

    private boolean isDeleteBulkMethod(Document document) {
        if(Strings.isNullOrEmpty(bulkMethodFieldName)) {
            return false;
        }
        String bulkMethod = document.getFieldValue(bulkMethodFieldName);
        if("delete".equals(bulkMethod)) {
            return true;
        }
        return false;
    }

    @Override
    public void document(Document document) {
        long documentSize = document.getSize();

        if (buffer.size() >= bufferSize || currentBufferContentSize + documentSize > maxBufferContentSize ) {
            procesBuffer();
            LOG.debug("bufferContentSize: " + currentBufferContentSize + " bufferSize: " + buffer.size());
            buffer = new ArrayList<>();
            currentBufferContentSize = 0;
        }
        buffer.add(document);
        currentBufferContentSize = currentBufferContentSize + documentSize;
        super.document(document);
    }

    public String mapToJsonString(List<Document> documentList, boolean detectNumberValues) {
        List<Map<String, Object>> documentMap = new ArrayList<Map<String, Object>>();
        for (Document document : documentList) {
            documentMap.add(mapToJson(document, detectNumberValues));
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(documentMap);
        return json;

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

    private Map<String, Object> mapToJson(Document document, boolean detectNumberValues) {
        Map<String, Object> jsonDocument = new HashMap<String, Object>();
        for (Field field : document.getFields()) {
            if (!field.hasValues()) {
                continue;
            }


            boolean fieldIsFlat= field.getName().contains(".");
            String fieldName = field.getName();
            Object fieldValue = field.getValues();
            Object fieldRawValue = field.getRawValue();
            List<Document> subDocuments = field.getDocuments();


            if(subDocuments != null) {
                List<Map<String, Object>> subFieldValue = new ArrayList<>();
                for(Document subDocument : subDocuments) {
                    subFieldValue.add(mapToJson(subDocument, detectNumberValues));
                }
                fieldValue = subFieldValue;
            } else if(fieldRawValue != null) {
                fieldValue = transformRawValue(field);
            } else if (field.getValues().size() == 1) {
                fieldValue = transformDatatype(field, detectNumberValues);
            } else if (fieldIsFlat) {
                fieldValue = createExpandedValue(fieldName, fieldValue);
                fieldName = StringUtils.substringBefore(fieldName, ".");
            }

            jsonDocument.put(fieldName, fieldValue);
        }

        return jsonDocument;

    }

    public static Object getFielddddValueByDataType(Field field) {
        if(field.getDatatype() == null) {
            return null;
        }
        if("multipoint".equals(field.getDatatype())) {

            Map<String, Object> data = new HashMap<>();
            data.put("type", "multipoint");


            List<List<Double>> coordinates = new ArrayList<>();
            for(String value : field.getValues()) {
                String[] splitted = value.split(",");
                if(splitted.length == 2) {
                    coordinates.add(
                            Arrays.asList(
                                    Double.valueOf(splitted[1]),
                                    Double.valueOf(splitted[0])
                            )
                    );
                }

            }
            data.put("coordinates", coordinates );
            return data;
        }

        return null;
    }


    public String createBulkMethod(String method, String index, String type,
                                   String id) {
        String bulkMethod = "{ \"" + method + "\" : { \"_index\" : \"" + index;
        if(includeTypeName) {
            bulkMethod += "\", \"_type\" : \"" + type;
        }
        bulkMethod += "\", \"_id\" : \"" + id + "\"} }";
        return bulkMethod;
    }

    public void housekeeping() {


        String prefix = AliasManager.getIndexPrefixByUrl(indexUrl);
        if(Strings.isNullOrEmpty(prefix)) {
            throw new RuntimeException("Could not extract prefix from url: " + indexUrl);
        }
        List<String> indexes = AliasManager.getIndexesByPrefix(indexUrl,prefix);
        Collections.sort(indexes);


        try {
            String alias = ElasticHelper.getIndexFromUrl(location);
            AliasManager.switchAlias(location, alias, indexes, indexes.get(indexes.size()-1));

        } catch (Exception e) {
            LOG.error("There was an error switching the alias: " + e.getMessage(), e);
        }
        int indexesToDeleteCount = indexes.size() - housekeepingCount;
        if(indexesToDeleteCount < 0) {
            indexesToDeleteCount = 0;
        }
        List<String> indexToDeleteList = indexes.subList(0,indexesToDeleteCount);
        for(String indexToDelete: indexToDeleteList) {

            try {
                String deleteUrl = ElasticHelper.getIndexUrl(location, indexToDelete);
                HTTPHelper.delete(deleteUrl);
            } catch (Exception e) {
                LOG.error("There was an error deleting the index: " + indexToDelete, e);
            }
        }
    }

    @Override
    public void end() {
        procesBuffer();
        if(housekeepingEnabled) {
            housekeeping();
        }

        if(alias != null) {
            try {
                String index = ElasticHelper.getIndexFromUrl(indexUrl);
                AliasManager.switchAlias(indexUrl, alias, new ArrayList<>(), index);
            } catch (Exception e) {
                LOG.info("Error switching alias, because: " + e.getMessage());
            }
        }

        super.end();
    }

    public String getIndexUrl() {
        return indexUrl;
    }

    public void setIncludeTypeName(boolean includeTypeName) {
        this.includeTypeName = includeTypeName;
    }
}
