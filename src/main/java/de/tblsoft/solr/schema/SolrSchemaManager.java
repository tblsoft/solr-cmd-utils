package de.tblsoft.solr.schema;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpJdkSolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.schema.SchemaResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 27.05.17.
 */
public class SolrSchemaManager {


    private SolrClient solrClient;

    private static Map<String, String> dataTypeMapping = new HashMap<String, String>();
    static {
        dataTypeMapping.put("integer", "int");
    }

    private List<String> fieldBlackList = new ArrayList<String>();

    public SolrSchemaManager(String solrUrl) {
        solrClient = new HttpJdkSolrClient.Builder(solrUrl).build();
        fieldBlackList.add("_text_");
        fieldBlackList.add("_version_");
        fieldBlackList.add("id");
    }


    public void addField(String name, String type, boolean multivalue)  throws IOException, SolrServerException {
        String mappedType = dataTypeMapping.get(type);
        if(mappedType == null) {
            mappedType = type;
        }

        Map<String, Object> fieldAttributes = new HashMap();
        fieldAttributes.put("name", name);
        fieldAttributes.put("type", mappedType);
        fieldAttributes.put("stored", true);
        fieldAttributes.put("indexed", true);
        fieldAttributes.put("multiValued", multivalue);
        fieldAttributes.put("required", false);

        SchemaRequest.AddField schemaRequest = new SchemaRequest.AddField(fieldAttributes);
        SchemaResponse.UpdateResponse response =  schemaRequest.process(solrClient);
    }

    public void addField(String name)  throws IOException, SolrServerException {
        addField(name, "string",false);

    }

    public void deleteField(String name) throws IOException, SolrServerException {
        if(fieldBlackList.contains(name)) {
            return;
        }
        SchemaRequest.DeleteField deleteFieldRequest = new SchemaRequest.DeleteField(name);
        SchemaResponse.UpdateResponse deleteFieldResponse = deleteFieldRequest.process(solrClient);


    }

    public List<String> getAllFields() throws IOException, SolrServerException {
        List<String> allFields = new ArrayList<String>();
        SchemaRequest.Fields listFields = new SchemaRequest.Fields();
        SchemaResponse.FieldsResponse fieldsResponse = listFields.process(solrClient);
        List<Map<String,Object>> solrFields = fieldsResponse.getFields();
        for(Map<String,Object> field : solrFields) {
            allFields.add((String) field.get("name"));
        }
        return allFields;
    }

    public void deleteAllFields() throws IOException, SolrServerException {
        for(String fieldName: getAllFields()) {
            deleteField(fieldName);
        }
    }
}
