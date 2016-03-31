package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
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
    public void field(String name, String value) {
        super.field(name,value);
        if(!name.matches(this.urlField)) {
            return;
        }

        List<NameValuePair> urlParams = URLEncodedUtils.parse(value,
                Charset.forName("UTF-8"));
        for(NameValuePair urlParam: urlParams) {
            super.field(fieldPrefix + urlParam.getName(),urlParam.getValue());
        }


    }
}

