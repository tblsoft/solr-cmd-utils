package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.filter.LastFilter;
import de.tblsoft.solr.pipeline.filter.TestingFilter;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 26.04.16.
 */
public abstract class AbstractFilterTest extends AbstractBaseTest {



    private Filter filterConfig = new Filter();

    private Document inputDocument = new Document();


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
        FilterIF filter = initFilter();
        filter.document(document);
        filter.end();
        outputDocumentList = testingFilter.getDocumentList();
        return outputDocumentList;
    }

    protected List<Document> document(Document... document) {
        FilterIF filter = initFilter();
        for(Document d: document) {
            filter.document(d);
        }
        filter.end();
        outputDocumentList = testingFilter.getDocumentList();
        return outputDocumentList;
    }

    FilterIF initFilter() {
        FilterIF filter = PipelineExecuter.createFilterInstance(filterConfig);
        this.testingFilter = new TestingFilter();
        testingFilter.setNextFilter(new LastFilter());
        filter.setNextFilter(testingFilter);
        filter.init();
        return filter;

    }
}
