package de.tblsoft.solr.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by oelbaer on 26.05.17.
 */
public class DatatypeUtilsTest {
    @Test
    public void isInteger() {
        Assert.assertTrue(DatatypeUtils.isInteger("1"));
        Assert.assertTrue(DatatypeUtils.isInteger("1.0"));
        Assert.assertTrue(DatatypeUtils.isInteger("1.0000"));
        Assert.assertFalse(DatatypeUtils.isInteger("1.1"));
        Assert.assertFalse(DatatypeUtils.isInteger("foo"));
        Assert.assertFalse(DatatypeUtils.isInteger(null));
        Assert.assertFalse(DatatypeUtils.isInteger(""));
        Assert.assertFalse(DatatypeUtils.isInteger("1473155779000"));
    }

    @Test
    public void isLong() {
        Assert.assertTrue(DatatypeUtils.isLong("1"));
        Assert.assertTrue(DatatypeUtils.isLong("1.0"));
        Assert.assertTrue(DatatypeUtils.isLong("1.0000"));
        Assert.assertFalse(DatatypeUtils.isLong("1.1"));
        Assert.assertFalse(DatatypeUtils.isLong("foo"));
        Assert.assertFalse(DatatypeUtils.isLong(null));
        Assert.assertFalse(DatatypeUtils.isLong(""));
        Assert.assertTrue(DatatypeUtils.isLong("1473155779000"));
    }

    @Test
    public void isBoolean() {
        Assert.assertTrue(DatatypeUtils.isBoolean("true"));
        Assert.assertTrue(DatatypeUtils.isBoolean("True"));
        Assert.assertTrue(DatatypeUtils.isBoolean("TRUE"));
        Assert.assertTrue(DatatypeUtils.isBoolean("false"));
        Assert.assertTrue(DatatypeUtils.isBoolean("False"));
        Assert.assertTrue(DatatypeUtils.isBoolean("FALSE"));
        Assert.assertFalse(DatatypeUtils.isBoolean(null));
        Assert.assertFalse(DatatypeUtils.isBoolean(""));
        Assert.assertFalse(DatatypeUtils.isBoolean("foo"));
        Assert.assertFalse(DatatypeUtils.isBoolean("1"));
        Assert.assertFalse(DatatypeUtils.isBoolean("0"));
        Assert.assertFalse(DatatypeUtils.isBoolean("2"));
    }

}