package de.tblsoft.solr.util;

import de.tblsoft.solr.elastic.AliasManager;
import org.junit.Test;

import java.util.List;

/**
 * Created by tbl on 15.07.18.
 */
public class JsonUtilTest {
    @Test
    public void parse() throws Exception {

        String url = "http://localhost:9200/_cat/indices/osm*?format=json";

        /*
        List<String> f = JsonUtil.parse(url,"$.[*].index");
        System.out.println(f);
*/
        List<String> indexList = AliasManager.getIndexesByPrefix(url, "osm");
        AliasManager.switchAlias(url, "test-alias", indexList, "osm-thueringen");

    }

}