package de.tblsoft.solr.pipeline.filter;


import com.google.common.base.Strings;
import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.NameValuePair;
import java.util.List;
import java.util.regex.Pattern;


public class UrlSplitter extends AbstractFilter {

    private String urlField;
    private String fieldPrefix;

    @Override
    public void init() {

        urlField = getProperty("urlField", "url");
        fieldPrefix = getProperty("fieldPrefix", "");
        super.init();
    }


    @Override
    public void document(Document document) {
        String value = document.getFieldValue(urlField, "");

        if(value.startsWith("http")){
            List<NameValuePair> urlParams = UrlUtil.getUrlParams(value);
            for(NameValuePair urlParam: urlParams) {
                document.addField(fieldPrefix + urlParam.getName(),urlParam.getValue());
            }

            String path = UrlUtil.getPath(value);
            document.addField(fieldPrefix + "_path", path);

            String[] pathSplitted = path.split(Pattern.quote("/"));
            for (int i = 0; i < pathSplitted.length; i++) {
                document.addField(fieldPrefix + "_path_" + i, pathSplitted[i]);
                document.addField(fieldPrefix + "_pathall", pathSplitted[i]);
            }

            document.addField(fieldPrefix + "_pathcount", String.valueOf(pathSplitted.length));


            String host = UrlUtil.getHost(value);
            document.addField(fieldPrefix + "_host",host);


        } else if(value.contains("?")) {
            //we have query parameters and a optional path
            // we have no absolute url

            throw new UnsupportedOperationException("The url is not correct " + value);
        } else if (Strings.isNullOrEmpty(value)) {
            // do nothing

        } else {
            // we only have a path
            throw new UnsupportedOperationException("The url is not correct " + value);
        }

        super.document(document);


    }

}

