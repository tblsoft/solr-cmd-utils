package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.bean.Filter;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by tblsoft on 29.04.16.
 */
public class BeanTest {

    @Test
    public void testFilterBean() {
        testBean(new Filter());
    }

    @Test
    public void testFieldBean() {
        testBean(new Field("foo", "bar"));
    }

    @Test
    public void testFieldBeanWithNoValue() {
        String value = null;
        Field f = new Field("foo", value);
        f.setValues(new ArrayList<String>());
        Assert.assertNull(f.getValue());
    }


    @Test
    public void testDocumentBean() {
        testBean(DocumentBuilder.document().create());
    }


    @Test
    public void testDeleteFieldInDocument() {
        Document document = DocumentBuilder.document().field("foo","bar").field("john", "doe").create();
        document.deleteField("foo");
        Assert.assertNull(document.getField("foo"));
        Assert.assertEquals("doe", document.getFieldValue("john"));
        Assert.assertEquals(1, document.getFields().size());

    }

    void testBean(Object bean) {
        Assert.assertFalse(bean.toString().contains("@"));
    }
}
