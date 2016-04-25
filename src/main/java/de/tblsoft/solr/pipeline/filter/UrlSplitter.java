package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;


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

        List<NameValuePair> urlParams = URLEncodedUtils.parse(value,
                Charset.forName("UTF-8"));

        for(NameValuePair urlParam: urlParams) {
            document.addField(fieldPrefix + urlParam.getName(),urlParam.getValue());
        }
        super.document(document);
    }

}

