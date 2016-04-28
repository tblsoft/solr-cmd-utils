package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.filter.JsonWriter;
import de.tblsoft.solr.pipeline.filter.LastFilter;
import de.tblsoft.solr.pipeline.filter.TestingFilter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 26.04.16.
 */
public abstract class AbstractFilterTest {



    private Filter filterConfig = new Filter();

    protected List<Document> outputDocumentList = new ArrayList<Document>();

    private Document inputDocument = new Document();

    private TestingFilter testingFilter = null;


    @Before
    public void before() {
        outputDocumentList.clear();
        inputDocument = DocumentBuilder.document().create();
        filterConfig = new Filter();
        testingFilter = null;
    }

    public void runTest() {
        document(inputDocument);
    }

    public void createField(String name, String value) {
        this.inputDocument.addField(name,value);
    }
    public abstract void configure();

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(0);
    }


    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertFiled("foo","bar");
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    public void putProperty(String key, String value) {
        filterConfig.putProperty(key, value);
    }

    public void addProperty(String key, String value) {
        List<String> propertyList = (List<String>) filterConfig.getProperty().get(key);
        if(propertyList == null) {
            propertyList = new ArrayList<String>();
        }
        propertyList.add(value);
        filterConfig.putPropertyList(key, propertyList);
    }

    public void setClazz(Class clazz) {
        filterConfig.setClazz(clazz.getName());
    }

    protected List<Document> document(Document document) {
        FilterIF filter = PipelineExecuter.createFilterInstance(filterConfig);
        TestingFilter testingFilter = new TestingFilter();
        testingFilter.setNextFilter(new LastFilter());
        filter.setNextFilter(testingFilter);
        filter.init();
        filter.document(document);
        filter.end();
        this.testingFilter = testingFilter;
        outputDocumentList = testingFilter.getDocumentList();
        return outputDocumentList;
    }

    public void print(List<Document> documentList) {
        String json = JsonWriter.mapToJsonString(documentList);
        System.out.println(json);
    }

    public void print() {
        print(outputDocumentList);
    }

    public void assertFiled(String name, String expected) {
        Document document = outputDocumentList.get(0);
        String actual = document.getFieldValue(name);
        Assert.assertEquals(expected, actual);

        CoreMatchers.hasItems("foo","bar");
    }


    public void assertFiledList(String name, String... expected) {
        Document document = outputDocumentList.get(0);
        List<String> actual = document.getFieldValues(name);
        Assert.assertThat(actual, CoreMatchers.hasItems(expected));
    }

    public void assertNumberOfDocuments(int expected) {
        int actual = outputDocumentList.size();
        Assert.assertEquals(expected, actual);
    }

    public void assertNumberOfFields(int expected) {
        int actual = outputDocumentList.get(0).getFields().size();
        Assert.assertEquals(expected, actual);
    }

    public void assertInitWasDelegated() {
        Assert.assertTrue(this.testingFilter.isInitWasDelegated());
    }

    public void assertEndWasDelegated() {
        Assert.assertTrue(this.testingFilter.isEndWasDelegated());
    }
}
