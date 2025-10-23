package de.tblsoft.solr.elastic;

import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.util.DateUtils;
import de.tblsoft.solr.util.InstantUtils;
import de.tblsoft.solr.util.JsonUtil;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by tbl on 15.07.18.
 */
public class AliasManager {


    private static String datePattern = "yyyy-MM-dd-HH.mm.ss";
    private static String separator = "_";


    public static boolean exists(String elasticUrl, String alias) throws URISyntaxException {

        String aliasUrl = ElasticHelper.getAliasUrl(elasticUrl) + "/" + alias;
        int statusCode = HTTPHelper.getStatusCode(aliasUrl);
        return statusCode == 200;
    }

    public static String getElasticUrlWithDatePattern(String elasticUrl) throws URISyntaxException {
        String type = ElasticHelper.getTypeFromUrl(elasticUrl);
        String indexUrl = ElasticHelper.getIndexUrl(elasticUrl);

        return indexUrl + separator + DateUtils.date2String(Date.from(InstantUtils.now()), datePattern) + "/" + type;
    }

    public static List<String> getIndexesByPrefix(String elasticUrl, String prefix) {
        try {
            String url = ElasticHelper.getCatlUrl(elasticUrl) + "/" + prefix + separator + "*?format=json";
            List<String> allIndexes = JsonUtil.parse(url,"$.[*].index");

            List<String> filtered = allIndexes.stream()
                    .filter(i -> matchesPrefixBeforeLastUnderscore(prefix, i))
                    .collect(Collectors.toList());

            return filtered;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean matchesPrefixBeforeLastUnderscore(String prefix, String index) {
        int last = index.lastIndexOf('_');
        if (last < 0) return false; // must have at least one _

        // split by last _
        String left = index.substring(0, last);
        String right = index.substring(last + 1); // not used, but could check if needed

        return left.equals(prefix);
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

            String url = ElasticHelper.getAliasesUrl(elasticUrl);
            HTTPHelper.post(url, request.toString(), "application/json");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeIndex(String elasticUrl, String... indices) {
        for (String index : indices) {
            try {
                String indexCloseUrl = ElasticHelper.getIndexCloseUrl(elasticUrl, index);
                HTTPHelper.post(indexCloseUrl, null, "application/json");
            } catch (URISyntaxException e) {
                throw new RuntimeException("Could not close index due malformed uri!", e);
            }
        }
    }
}
