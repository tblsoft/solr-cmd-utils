package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by tblsoft 17.03.16.
 */
public class PostDataFilter extends AbstractFilter {

   Gson gson = new Gson();
    @Override
    public void init() {
        super.init();
    }

    @Override
    public void document(Document document) {
        String id = document.getFieldValue("GRUNDSTUECK_ID");
        String url = "http://localhost:8080/feeding/unity-media/address-suggest/" + id;
        String contentType = "application/json";

        Map<String, String> docMap = new HashMap<>();
        for(Field field : document.getFields()) {
            docMap.put(field.getName(), field.getValue());

        }

        String payload = gson.toJson(docMap);
        HTTPHelper.post(url, payload, contentType);



        super.document(document);
    }
}
