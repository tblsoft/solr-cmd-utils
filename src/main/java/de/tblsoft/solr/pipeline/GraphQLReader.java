package de.tblsoft.solr.pipeline;

import com.google.gson.*;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.quasiris.qsc.writer.QscDataPushWriter;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reader for getting Json Entries from a GraphQL endpoint and parse specified attributes of them into documents
 * @author Benjamin E. Müller
 * @author bmr@quasiris.de
 * @since 3.21
 */
public class GraphQLReader extends AbstractReader {

    /**
     * Logging instance to log batch errors
     */
    private static final Logger LOG = LoggerFactory.getLogger(QscDataPushWriter.class);

    /**
     * Gson instance to convert JSON API Response String
     */
    private final static Gson gson = new Gson();

    /**
     * configuration of property name Strings
     */
    public final static String PROPERTY_BATCH_SIZE = "batchSize";
    public final static String PROPERTY_URL = "url";
    public final static String PROPERTY_HEADER = "header";
    public final static String PROPERTY_QUERY_TEXT_FILEPATH = "queryTextFilepath";
    public final static String PROPERTY_QUERY_TOTAL_TEXT_FILEPATH = "queryTotalTextFilepath";
    public final static String PROPERTY_QUERY_TEXTS_BATCH_SIZE_LABEL = "queryTextsBatchSizeLabel";
    public final static String PROPERTY_QUERY_TEXTS_CURRENT_BATCH_LABEL = "queryTextsCurrentBatchLabel";
    public final static String PROPERTY_TOTAL_BATCHES_JSON_PATH = "totalBatchesJsonPath";
    public final static String PROPERTY_ENTRIES_JSON_PATH = "entriesJsonPath";
    public final static String PROPERTY_ERRORS_JSON_PATH = "errorsJsonPath";
    public final static String PROPERTY_JSON_MAPPING = "jsonMapping";
    public final static String PROPERTY_JSON_MAPPING_CONFIG = "jsonMappingConfig";

    /**
     * configuration of property default Strings
      */
    public final static Long PROPERTY_DEFAULT_BATCH_SIZE = 100L;
    public final static String PROPERTY_DEFAULT_QUERY_TEXTS_BATCH_SIZE_LABEL = "batchSize";
    public final static String PROPERTY_DEFAULT_QUERY_TEXTS_CURRENT_BATCH_LABEL = "currentBatch";
    public final static String PROPERTY_DEFAULT_ERRORS_JSON_PATH = "$.errors";

    /**
     * local variables with default values for each property that can be set by a pipeline configuration yaml
     *   default of "mapping" is later set by "jsonMapping" and "jsonMappingConfig" properties
     */
    int batchSize = Math.toIntExact(PROPERTY_DEFAULT_BATCH_SIZE);
    String url = null;
    List<String> header = new ArrayList<>();
    String queryTextFilepath = null;
    String queryTotalTextFilepath = null;
    String queryTextsBatchSizeLabel = PROPERTY_DEFAULT_QUERY_TEXTS_BATCH_SIZE_LABEL;
    String queryTextsCurrentBatchLabel = PROPERTY_DEFAULT_QUERY_TEXTS_CURRENT_BATCH_LABEL;
    String totalBatchesJsonPath = null;
    String entriesJsonPath = null;
    String errorsJsonPath = null;
    Map<String, List<String>> mapping = new HashMap<>();

    /**
     * The following properties can be specified in your pipeline config for the GraphQLReader
     *
     * You have to specify at least:
     * url, queryTextFilepath, totalBatchesJsonPath, entriesJsonPath, mapping
     *
     * batchSize                    The page/batch size that should be used
     * url                          The API endpoint the data is retrieved from
     * header                       The header containing content type and authorization credentials
     * queryTextFilepath            Filepath of the query text, determining the attributes to ask for
     *                                uses "batchSize" for the batch / page size in your query text
     *                                uses "currentBatch" for the batch / page number in your query text
     * queryTotalTextFilepath       Filepath of the query total text, determining the attributes to ask for in order to retrieve the total amount of batches
     *                                Since queryTotalText is just used once before processing begins, you can use this property to minimize the response size
     *                                  instead of retrieving the amount of total batches with the queryText, you can retrieve it by specifying a query just for this
     *                                uses "batchSize" for the batch / page size in your query text
     *                                uses "currentBatch" for the batch / page number in your query text
     * queryTextsBatchSizeLabel     The text that is used in query texts to be replaced with the actual batch size
     * queryTextsCurrentBatchLabel  The text that is used in query texts to be replaced with the actual current batch
     * totalBatchesJsonPath         Json path of the total pages / batches attribute
     *                                path in format described on https://github.com/json-path/JsonPath
     * entriesJsonPath              Json path of entries / documents list attribute
     *                                path in format described on https://github.com/json-path/JsonPath
     * errorsJsonPath               Json path of errors list attribute
     *                                path in format described on https://github.com/json-path/JsonPath
     * jsonMapping                  list of mappings between desired attribute field name and json path of the attribute
     *                                Mapping is separated with "->" e.g. "$.product.availability.stock->stock"
     *                                path in format described on https://github.com/json-path/JsonPath
     *                                  escape when filtering, e.g. "$.prices[?(@.currency == \"EUR\"].value->price"
     * jsonMappingConfig            config property of SimpleMapping Class. Please create a meaningful comment if you understand it
     *                                I have no idea what the config in SimpleMapping is used for, because my pipeline doesn't need it
     *                                but since it's required by the class, I made it configurable, just to be on the safe side
     *
     */
    @Override
    public void read() {

        ////////// CONFIG READING & CHECKING

        // Batch size
        this.batchSize = Math.toIntExact(getPropertyAsInteger(PROPERTY_BATCH_SIZE, PROPERTY_DEFAULT_BATCH_SIZE));

        // GraphQl endpoint url and auth string containing authorization token for an http header
        this.url = getProperty(PROPERTY_URL, null);
        this.header = getPropertyAsList(PROPERTY_HEADER, null);

        // Query text files paths
        this.queryTextFilepath = getProperty(PROPERTY_QUERY_TEXT_FILEPATH, null);
        this.queryTotalTextFilepath = getProperty(PROPERTY_QUERY_TOTAL_TEXT_FILEPATH, queryTextFilepath);
        // if no total text specified, use the query text and get the amount of batches from there

        // Query text labels for which get substituted in the query texts with actual values
        this.queryTextsBatchSizeLabel = getProperty(PROPERTY_QUERY_TEXTS_BATCH_SIZE_LABEL, PROPERTY_DEFAULT_QUERY_TEXTS_BATCH_SIZE_LABEL);
        this.queryTextsCurrentBatchLabel = getProperty(PROPERTY_QUERY_TEXTS_CURRENT_BATCH_LABEL, PROPERTY_DEFAULT_QUERY_TEXTS_CURRENT_BATCH_LABEL);

        // GraphQL endpoint specific json paths for total pages, entries Array and errors in the JSON answer String
        this.totalBatchesJsonPath = getProperty(PROPERTY_TOTAL_BATCHES_JSON_PATH, null);
        this.entriesJsonPath = getProperty(PROPERTY_ENTRIES_JSON_PATH, null);
        this.errorsJsonPath = getProperty(PROPERTY_ERRORS_JSON_PATH, PROPERTY_DEFAULT_ERRORS_JSON_PATH);

        // Read and build the json mapping, which is used to map Json Entry fields to Document fields
        SimpleMapping simpleMapping = new SimpleMapping(
                getPropertyAsList(PROPERTY_JSON_MAPPING, new ArrayList<>()),
                getPropertyAsList(PROPERTY_JSON_MAPPING_CONFIG, new ArrayList<>()));
        this.mapping = simpleMapping.getMapping();

        // Check that none of the required values is "null", if so throw an exception with an error message
        String prefix = " The GraphQLReader Property '";
        String suffix = "' is missing in your pipeline configuration!";
        if (url == null) {
            throw new RuntimeException(prefix + PROPERTY_URL + suffix);
        } if (queryTextFilepath == null) {
            throw new RuntimeException(prefix + PROPERTY_QUERY_TEXT_FILEPATH + suffix);
        } if (totalBatchesJsonPath == null) {
            throw new RuntimeException(prefix + PROPERTY_TOTAL_BATCHES_JSON_PATH + suffix);
        } if (entriesJsonPath == null) {
            throw new RuntimeException(prefix + PROPERTY_ENTRIES_JSON_PATH + suffix);
        } if (mapping == null) {
            throw new RuntimeException(prefix + PROPERTY_JSON_MAPPING + suffix);
        }

        // read and store the query text
        String queryText;
        try {
            queryText = readFileToString(queryTextFilepath);
        } catch (IOException e) {
            throw new RuntimeException("Could not read query text config file: '" + queryTextFilepath + "'", e);
        }

        // read and store the query total text
        String queryTotalText;
        try {
            queryTotalText = readFileToString(queryTotalTextFilepath);
        } catch (IOException e) {
            throw new RuntimeException("Could not read query total text config file: '" + queryTotalTextFilepath + "'", e);
        }


        ////////// DATA COLLECTING & DOCUMENT CREATION

        // get amount of total batches to iterate over
        int maxBatch = getTotalBatches(queryTotalText);

        // go over batch until amount of needed batches are reached
        for (int currentBatch = 1; currentBatch <= maxBatch; currentBatch++) {

            // Status info log
            LOG.info("Processing batch " + currentBatch + " of " + maxBatch);

            try {
                // Collect data of that batch
                JsonArray entries = collectData(currentBatch, queryText);


                // parse documents, try to feed the current batch and add return status (if present) to returnLog
                for (JsonElement entry: entries) {
                    executer.document(parseJsonEntryToDocument(entry, mapping));
                }

                //notice, if the current batch could not be collected/fed if any error happened
            } catch (RuntimeException e) {
                LOG.info("Could not collect / parse batch " + currentBatch + ", skipping it.");
            }
        }

        // Status info log
        LOG.info("Processing finished");

    }

    /**
     * Parses a JsonElement into a document by mapping Json attributes at specified paths to specified document fields
     * @param entry the jsonElement that serves as root Element for the mapping, containing the attributes
     * @param mapping a mapping that maps a jsonPath to a list of field names
     * @return returns a document containing all mapped attributes with their field names
     */
    private Document parseJsonEntryToDocument(JsonElement entry, Map<String, List<String>> mapping) {

        // create a new document for the entry
        Document document = new Document();

        // go over each jsonPath and get all document fields that are mapped to the path as a list
        //   this is needed, since you can map multiple document fields to the same path
        for(String jsonPath: mapping.keySet()) {
            List<String> documentFieldsList = mapping.get(jsonPath);

            // get the json attribute behind the jsonPath, set the attribute for all document fields of the list
            try {
                Object jsonAttribute = JsonPath.read(entry.toString(), jsonPath); // toString() important, else would be Gson!
                for (String documentField: documentFieldsList) {
                    document.setField(documentField, jsonAttribute);
                }
            } catch (PathNotFoundException e) {
                // no value behind the specified jsonPath, thus skip the jsonPAth
            }
        }

        // return the document, containing all values behind the mapping keys
        return document;
    }

    /**
     * Queries and returns the total amount of Pages/Batches for a specific page/batch size
     * @param queryTotalText The total query text, for finding out the total amount of needed batches
     *                       uses "batchSize" for the batch / page size in your query text
     * @return returns the total amount of batches
     * @throws RuntimeException Gets thrown when the amount of total batches could not be retrieved from the graphQL endpoint
     */
    public int getTotalBatches(String queryTotalText) throws RuntimeException {

        // creates query text that gets amount of pages for a specific page size by replacing batchSize with its value
        String queryText = buildQuery(queryTotalText, 1);

        // send query, get Json answer
        JsonElement answerJson;
        try {
            answerJson = graphQLRequest(queryText);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while collecting the amount of total batches from the endpoint " + url + "\n" +
                    "  The following query could not be executed: " + queryText, e);
        }

        // if the total pages path is not present in the answer return 0 and if available, an error description
        JsonElement totalBatchesJson = getContent(answerJson, totalBatchesJsonPath);
        if (totalBatchesJson.equals(new JsonPrimitive("null"))) {
            JsonElement errorMsg = getContent(answerJson, errorsJsonPath);
            if (!errorMsg.equals(new JsonPrimitive("null"))) {
                throw new RuntimeException("Amount of total batches could not be retrieved from graphQL endpoint\n" +
                        "  The following error message was received from the endpoint: " + errorMsg.getAsJsonArray().toString());
            } else {
                throw new RuntimeException("Amount of total batches could not be retrieved from graphQL endpoint\n" +
                        "  No error message was received from the endpoint");
            }

            // if the total pages path is present return amount of total pages as int
        } else {
            return totalBatchesJson.getAsInt();
        }
    }

    /**
     * collects Json data entries from a GraphQL endpoint and returns them as JsonArray
     * @param currentBatch The current page/batch that should be requested
     * @param queryText The query text, determining the attributes to ask for
     *                  uses "batchSize" for the batch / page size in your query text
     *                  uses "currentBatch" for the batch / page number in your query text
     * @return returns a JsonArray of GraphQL Entries or Errors if no Entries retrieved
     * @throws RuntimeException Gets thrown when an IOException occurs on collecting from the endpoint
     *                            or when the entries list could not be retrieved from the graphQL Endpoint
     */
    public JsonArray collectData(int currentBatch, String queryText) throws RuntimeException {

        // build request and send query
        String query = buildQuery(queryText, currentBatch);
        JsonElement answerJson;
        try {
            answerJson = graphQLRequest(query);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while collecting data from the endpoint " + url + "\n" +
                    "  The following query could not be executed: " + queryText, e);
        }

        // if entries is not present in the answer, return an empty JSON array and if available, an error description,
        JsonElement entries = getContent(answerJson, entriesJsonPath);
        if (entries.equals(new JsonPrimitive("null"))) {
            JsonElement errorMsg = getContent(answerJson, errorsJsonPath);
            if (!errorMsg.equals(new JsonPrimitive("null"))) {
                throw new RuntimeException("List of entries could not be retrieved from graphQL endpoint\n" +
                        "  The following error message was received from the endpoint: " + errorMsg.getAsJsonArray().toString());
            } else {
                throw new RuntimeException("List of entries could not be retrieved from graphQL endpoint\n" +
                        "  No error message was received from the endpoint");
            }

            // if entries is present in the answer, extract all entries from answerJson and return them as JsonArray
        } else {
            return entries.getAsJsonArray();
        }
    }

    /**
     * Builds an actual query text by replacing batchSize and currentBatch in the configured query text
     * @param queryText the query config, determining the query structure with attributes to ask for
     * @param currentBatch the page number that should be asked for
     * @return returns a query text that can be used with a GraphQL API endpoint
     */
    private String buildQuery(String queryText, int currentBatch) {

        // return the query text by replacing batchSize label and currentBatch label with their values
        return queryText.replace(queryTextsBatchSizeLabel, String.valueOf(batchSize))
                .replace(queryTextsCurrentBatchLabel, String.valueOf(currentBatch));
    }

    /**
     * Sends a query to a GraphQL API endpoint, returns the answer as Json Element
     * @param queryText The query text, determining the attributes to ask for
     *                  uses "batchSize" for the batch / page size in your query text
     *                  uses "currentBatch" for the batch / page number in your query text
     * @return The result of the query as JsonElement
     * @throws IOException Gets thrown when an Error occurs on collecting from the endpoint
     */
    private JsonElement graphQLRequest(String queryText) throws IOException {

        // API response String is empty in the beginning such that on IOExceptions nothing happens
        String responseString;

        // read the responseString from the graphQL API
        responseString = HTTPHelper.post(url, queryText, header);

        // return Json Answer
        return gson.fromJson(responseString, JsonElement.class);
    }

    /**
     * Traverses the given path on the root element and returns the JsonElement behind this path
     * @param jsonElement The json root element that should be traversed to get the content
     * @param path The path as dot separated String to the element that should be retrieved
     *             e.g. "product.name" to get the name sub-attribute of the product sub-attribute of the root element
     *             e.g. "prices[0].value" to get the first price of a price list with different currencies
     *             see https://github.com/json-path/JsonPath for more detailed path String description
     * @return returns the element at the given path, or null if no element is at this path
     */
    public JsonElement getContent(JsonElement jsonElement, String path) {

        // get content of the JsonObject behind the path String as String
        //   if it is null or cannot be found, return Primitive with "null" String
        String content;
        try {
            content = JsonPath.read(jsonElement.toString(), path).toString();
        } catch (PathNotFoundException e) {
            return new JsonPrimitive("null");
        }

        // If the content String looks like a JSON Object/Array parse it as such, if not, return it as String Primitive
        if (content.contains("{") || content.contains("[")) {
            JsonParser parser = new JsonParser(); // With newer Gson version, instantiation not needed anymore
            return parser.parse(content); // With newer Gson version, use instead: JsonParser.parseString(content);
        } else {
            return new JsonPrimitive(content);
        }
    }

    /**
     * Reads a file into one String
     * @param path path of the file to read
     * @return returns the content of the file as a String
     */
    public String readFileToString(String path) throws IOException {

        // Java 8 way of reading a file into a long String
        return new String(Files.readAllBytes(Paths.get(path)));
    }
}
