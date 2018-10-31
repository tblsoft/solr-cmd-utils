package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class RestFilterTest extends AbstractFilterTest {
    @Override
    public void configure() {
        putProperty("url", "http://localhost/v1/products/{id}");
        putProperty("method", "POST");
        addProperty("headers", "Content-Type: application/json");
        addProperty("payload", "title");
        addProperty("payload", "price");
        addProperty("payload", "timestamp");
        putProperty("timeout", "2000");
        putProperty("threads", "1");
        putProperty("responsePrefix", "_rest_");
        setClazz(RestFilter.class);
    }

    @Test
    public void document() throws Exception {

    }

    @Test
    public void filterMatch() throws Exception {

    }

    @Test
    public void buildRequestEmpty() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        RestFilter restFilter = initFilter(properties);

        // when
        RestWorker.RestRequest restRequest = restFilter.buildRequest(document1);

        // then
        assertEquals("http://localhost/v1/products/product-samsung-s8-gr%C3%BCn%21", restRequest.url);
        assertEquals("GET", restRequest.method);
        assertEquals(Arrays.asList(), restRequest.headers);
        assertNull(restRequest.payload);
    }

    @Test
    public void buildRequest() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        properties.put("method", "POST");
        properties.put("headers", Arrays.asList("Content-Type: application/json", "User-Agent: My-Rest-Client"));
        properties.put("payload", Arrays.asList("title", "category"));
        RestFilter restFilter = initFilter(properties);

        // when
        RestWorker.RestRequest restRequest = restFilter.buildRequest(document1);

        // then
        assertEquals("http://localhost/v1/products/product-samsung-s8-gr%C3%BCn%21", restRequest.url);
        assertEquals("POST", restRequest.method);
        assertEquals(Arrays.asList("Content-Type: application/json", "User-Agent: My-Rest-Client"), restRequest.headers);
        assertEquals("{\"title\":\"Samsung s8\",\"category\":\"smartphone\"}", restRequest.payload);
    }

    @Test
    public void buildUrlWithParams() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        RestFilter restFilter = initFilter(properties);

        // when
        String url = restFilter.buildUrlWithParams(document1);

        // then
        assertEquals("http://localhost/v1/products/product-samsung-s8-gr%C3%BCn%21", url);
    }

    @Test
    public void getUrlFromConfig() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        RestFilter restFilter = initFilter(properties);

        // when
        String url = restFilter.getUrl(document1);

        // then
        assertEquals("http://localhost/v1/products/{id}", url);
    }

    @Test
    public void getUrlFromDocument() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "my_url");
        RestFilter restFilter = initFilter(properties);

        // when
        String url = restFilter.getUrl(document1);

        // then
        assertEquals("http://example.com/{id}", url);
    }

    @Test
    public void buildPayload() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        properties.put("payload", Arrays.asList("title", "category", "price", "my_url"));
        RestFilter restFilter = initFilter(properties);

        // when
        String jsonPayload = restFilter.buildPayload(document1);

        // then
        assertEquals("{\"price\":\"499.95\",\"my_url\":\"http://example.com/{id}\",\"title\":\"Samsung s8\",\"category\":\"smartphone\"}", jsonPayload);
    }

    @Test
    public void buildPayloadEmpty() throws Exception {
        // given
        Document document1 = createDocMock();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("url", "http://localhost/v1/products/{id}");
        RestFilter restFilter = initFilter(properties);

        // when
        String jsonPayload = restFilter.buildPayload(document1);

        // then
        assertEquals(null, jsonPayload);
    }

    protected Document createDocMock() {
        return DocumentBuilder
                .document()
                .field("id", "product-samsung-s8-grün!")
                .field("title", "Samsung s8")
                .field("category", "smartphone")
                .field("price", "499.95")
                .field("timestamp", new Date().toString())
                .field("my_url", "http://example.com/{id}")
                .create();
    }

    protected RestFilter initFilter(HashMap<String, Object> properties) {
        Filter filterConfig = new Filter();
        filterConfig.setClazz(RestFilter.class.getName());

        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            if(entry.getValue() == null) {
                filterConfig.putProperty(entry.getKey(), null);
            } else {
                if(entry.getValue() instanceof String) {
                    filterConfig.putProperty(entry.getKey(), entry.getValue().toString());
                } else if (entry.getValue() instanceof List) {
                    filterConfig.putPropertyList(entry.getKey(), (List<String>) entry.getValue());
                }
            }
        }


        RestFilter filter = (RestFilter) PipelineExecuter.createFilterInstance(filterConfig);
        this.testingFilter = new TestingFilter();
        testingFilter.setNextFilter(new LastFilter());
        filter.setNextFilter(testingFilter);
        filter.init();
        return filter;
    }
}