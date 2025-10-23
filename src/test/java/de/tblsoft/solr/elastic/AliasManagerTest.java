package de.tblsoft.solr.elastic;

import de.tblsoft.solr.util.InstantUtils;
import de.tblsoft.solr.util.JsonUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AliasManagerTest {

    @Test
    @Ignore
    public void getElasticUrlWithDatePattern() throws Exception{
        InstantUtils.setNow(Instant.parse("2018-08-25T11:22:33.44Z"));
        String url = AliasManager.getElasticUrlWithDatePattern("http://localhost:9200/tblsoft/my-type");
        Assert.assertEquals("http://localhost:9200/tblsoft_2018-08-25-13.22.33/my-type", url);
    }
    @Test
    @Ignore
    public void getElasticUrlWithDatePatternAndLocale() throws Exception{
        InstantUtils.setNow(Instant.parse("2018-08-25T11:22:33.44Z"));
        String url = AliasManager.getElasticUrlWithDatePattern("http://localhost:9200/tblsoft_en-GB/my-type");
        Assert.assertEquals("http://localhost:9200/tblsoft_en-GB_2018-08-25-13.22.33/my-type", url);
    }


    @Test
    public void getIndexesByPrefix() {

        String prefix = "tblsoft_foo_bar";

        String elasticUrl = "http://localhost:9200/" + prefix + "/my-type";
        List<String> expectedResult =
                List.of(
                        "tblsoft_2018-08-25-14.22.33",
                        "tblsoft_2018-08-26-14.22.33",
                        "tblsoft_2018-08-27-14.22.33",
                        "tblsoft_2018-08-28-14.22.33"
                );

        List<String> allIndexes =
                List.of(
                        "tblsoft_en-GB_2018-08-25-13.22.33",
                        "tblsoft_en-GB_2018-08-26-13.22.33",
                        "tblsoft_en-GB_2018-08-27-13.22.33",
                        "tblsoft_en-GB_2018-08-28-13.22.33",
                        "tblsoft_de-DE_2018-08-25-13.25.33",
                        "tblsoft_de-DE_2018-08-26-13.25.33",
                        "tblsoft_de-DE_2018-08-27-13.25.33",
                        "tblsoft_de-DE_2018-08-28-13.25.33",
                        "tblsoft_foo_bar_2018-08-28-13.25.33",
                        "tblsoft_2018-08-25-14.22.33",
                        "tblsoft_2018-08-26-14.22.33",
                        "tblsoft_2018-08-27-14.22.33",
                        "tblsoft_2018-08-28-14.22.33"
                );

        try (MockedStatic<JsonUtil> jsonMock = mockStatic(JsonUtil.class)) {

            jsonMock.when(() -> JsonUtil.parse("http://localhost:9200/_cat/indices/" + prefix + "_*?format=json", "$.[*].index"))
                    .thenReturn(allIndexes);

            List<String> indexes = AliasManager.getIndexesByPrefix(elasticUrl, prefix);

            assertEquals(expectedResult, indexes);
        }
    }

    @Test
    public void getIndexPrefixByUrl() throws Exception {
        String url = AliasManager.getElasticUrlWithDatePattern("http://localhost:9200/tblsoft/my-type");
        String prefix = AliasManager.getIndexPrefixByUrl(url);
        Assert.assertEquals("tblsoft", prefix);
    }

}