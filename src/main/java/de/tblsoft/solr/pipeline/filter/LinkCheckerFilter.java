package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

/**
 * Created by tblsoft 17.05.16.
 */
public class LinkCheckerFilter extends AbstractFilter {

    private String urlFieldName;
    private String httpCodeFieldName;
    private String statusFieldName;


    @Override
    public void init() {
        urlFieldName = getProperty("urlFieldName", "url");
        httpCodeFieldName = getProperty("httpCodeFieldName", "httpCode");
        statusFieldName = getProperty("statusFieldName", "status");
        super.init();
    }

    @Override
    public void document(Document document) {
        String httpCode;
        String status;
        String url = document.getFieldValue(urlFieldName);
        if(Strings.isNullOrEmpty(url)) {
            httpCode = "0";
            status = "NOURL";
        } else {
            int httpStatusCode = HTTPHelper.getStatusCode(url);
            httpCode = String.valueOf(httpStatusCode);
            status = "ERROR";
            if(httpStatusCode == 200) {
                status = "OK";
            }
        }



        document.addField(httpCodeFieldName, httpCode);
        document.addField(statusFieldName, status);

        super.document(document);
    }
}
