package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.filter.JsonWriter;
import de.tblsoft.solr.pipeline.filter.TestingFilter;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 28.04.16.
 */
public abstract class AbstractBaseTest {

    private static Logger LOG = LoggerFactory.getLogger(AbstractBaseTest.class);

    protected List<Document> outputDocumentList = new ArrayList<Document>();
    protected TestingFilter testingFilter;


    public void print(List<Document> documentList) {
        String json = JsonWriter.mapToJsonString(documentList);
        LOG.info(json);
    }

    public void print() {
        print(outputDocumentList);
    }

    public void assertFiled(int position, String name, String expected) {
        if(outputDocumentList.size() <= position) {
            Assert.fail("There is no output document for position: " + position);
        }
        Document document = outputDocumentList.get(position);
        String actual = document.getFieldValue(name);
        Assert.assertEquals(expected, actual);
    }

    public void assertFiled(String name, String expected) {
        assertFiled(0,name,expected);
    }

    public void assertFiledNotExists(String name) {
        if(outputDocumentList.size() == 0) {
            Assert.fail("There is no output document.");
        }
        Document document = outputDocumentList.get(0);
        Field f = document.getField(name);
        Assert.assertNull(f);
    }

    public void assertFiledExists(String name) {
        if(outputDocumentList.size() == 0) {
            Assert.fail("There is no output document.");
        }
        Document document = outputDocumentList.get(0);
        Field f = document.getField(name);
        Assert.assertNotNull(f);
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
