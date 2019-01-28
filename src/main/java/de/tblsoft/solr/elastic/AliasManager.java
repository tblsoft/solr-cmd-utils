package de.tblsoft.solr.elastic;

import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.util.DateUtils;
import de.tblsoft.solr.util.InstantUtils;
import de.tblsoft.solr.util.JsonUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by tbl on 15.07.18.
 */
public class AliasManager {


    private static String datePattern = "yyyy-MM-dd-HH.mm.ss";
    private static String separator = "_";


    public static String getElasticUrlWithDatePattern(String elasticUrl) throws URISyntaxException {
        String type = ElasticHelper.getTypeFromUrl(elasticUrl);
        String indexUrl = ElasticHelper.getIndexUrl(elasticUrl);

        return indexUrl + separator + DateUtils.date2String(Date.from(InstantUtils.now()), datePattern) + "/" + type;
    }

    public static List<String> getIndexesByPrefix(String elasticUrl, String prefix) {
        try {
            String url = ElasticHelper.getCatlUrl(elasticUrl) + "/" + prefix + "*?format=json";
            return JsonUtil.parse(url,"$.[*].index");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getIndexPrefixByUrl(String elasticUrl) {
        try {
            String index = ElasticHelper.getIndexFromUrl(elasticUrl);
            int lastIndexOfSeperator = index.lastIndexOf(separator);
            if(lastIndexOfSeperator <0) {
                return null;
            }
            return index.substring(0, lastIndexOfSeperator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void switchAlias(String elasticUrl, String alias, List<String> oldIndexList, String newIndex ) {

        try {

            StringBuilder request = new StringBuilder();
            request.append("{\n" +
                    "    \"actions\" : [\n");

            for(String oldIndex: oldIndexList) {
                request.append("        { \"remove\" : { \"index\" : \"" + oldIndex + "\", \"alias\" : \"" + alias + "\" } },\n");
            }
            request.append("        { \"add\" : { \"index\" : \""+ newIndex + "\", \"alias\" : \"" + alias + "\" } }\n" +
                    "    ]\n" +
                    "}");

            String url = ElasticHelper.getAliaslUrl(elasticUrl);
            HTTPHelper.post(url, request.toString(), "application/json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
