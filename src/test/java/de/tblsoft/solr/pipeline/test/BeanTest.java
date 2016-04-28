package de.tblsoft.solr.pipeline.test;

import de.tblsoft.solr.pipeline.bean.Filter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tblsoft on 29.04.16.
 */
public class BeanTest {

    @Test
    public void testFilterBean() {
        testBean(new Filter());
    }

    void testBean(Object bean) {
        Assert.assertFalse(bean.toString().contains("@"));
    }
}
