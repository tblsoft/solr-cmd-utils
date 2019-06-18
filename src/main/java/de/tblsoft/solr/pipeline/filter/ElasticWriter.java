package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.elastic.AliasManager;
import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
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

    private String type;

    private String location;

    private String elasticMappingLocation;

    private boolean delete;

    private String idField;
    private Boolean hashId = false;

    private List<Document> buffer = new ArrayList<Document>();

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

        GsonBuilder builder = new GsonBuilder();
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
            HTTPHelper.delete(indexUrl);
        }
        if (elasticMappingLocation != null) {
            try {
                String absoluteElasticMappingLocation = IOUtils.getAbsoluteFile(
                        getBaseDir(), elasticMappingLocation);
                String mappingJson = IOUtils.getString(absoluteElasticMappingLocation);
                String mappingUrl = ElasticHelper.getIndexUrl(indexUrl);
                LOG.debug("mapping url: {} mappingJson: {}", mappingUrl, mappingJson );
                if(HTTPHelper.getStatusCode(mappingUrl) == 404) {
                    HTTPHelper.put(mappingUrl, mappingJson, "application/json");
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        super.init();
    }

    static Object transformDatatype(Field field, boolean detectNumberValues) {

        String value = field.getValue();

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

    void procesBuffer() {
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
            HTTPHelper.post(bulkUrl, bulkRequest.toString(), "application/json");
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

    public static String mapToJsonString(List<Document> documentList, boolean detectNumberValues) {
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

    static Map<String, Object> mapToJson(Document document, boolean detectNumberValues) {
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
                fieldValue = transformDatatype(field, detectNumberValues);
            }

            if(fieldIsFlat) {
                fieldValue = createExpandedValue(fieldName, fieldValue);
                fieldName = StringUtils.substringBefore(fieldName, ".");
            }

            jsonDocument.put(fieldName, fieldValue);
        }

        return jsonDocument;

    }


    public String createBulkMethod(String method, String index, String type,
                                   String id) {
        String bulkMethod = "{ \"" + method + "\" : { \"_index\" : \"" + index
                + "\", \"_type\" : \"" + type + "\", \"_id\" : \"" + id + "\"} }";
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

}
