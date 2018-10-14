package de.tblsoft.solr.elastic;

import de.tblsoft.solr.util.InstantUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.*;

public class AliasManagerTest {

    @Test
    @Ignore
    public void getElasticUrlWithDatePattern() throws Exception{
        InstantUtils.setNow(Instant.parse("2018-08-25T11:22:33.44Z"));
        String url = AliasManager.getElasticUrlWithDatePattern("http://localhost:9200/tblsoft/my-type");
        Assert.assertEquals("http://localhost:9200/tblsoft_2018-08-25-13.22.33/my-type", url);
    }

    @Test
    public void getIndexPrefixByUrl() throws Exception {
        String url = AliasManager.getElasticUrlWithDatePattern("http://localhost:9200/tblsoft/my-type");
        String prefix = AliasManager.getIndexPrefixByUrl(url);
        Assert.assertEquals("tblsoft", prefix);
    }

}