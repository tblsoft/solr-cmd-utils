package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import net.minidev.json.JSONValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tblsoft 12.05.18.
 */
public class SplashFilter extends AbstractFilter {

    private String splashPayloadFilename;
    private String splashPayload;
    private String splashUrl;
    private String jsSourceFilename;
    private String jsSource;

    private boolean shouldMatch = true;

    @Override
    public void init() {
        splashPayloadFilename = getProperty("splashPayloadFilename", null);
        splashUrl = getProperty("splashUrl", null);
        jsSourceFilename = getProperty("jsSourceFilename", null);

        try {
            String absoluteSplashPayloadFilename = IOUtils.getAbsoluteFile(getBaseDir(), splashPayloadFilename);
            splashPayload = FileUtils.readFileToString(new File(absoluteSplashPayloadFilename));

            String absoluteJsSourceFilename = IOUtils.getAbsoluteFile(getBaseDir(), jsSourceFilename);
            String jsSourceRaw = FileUtils.readFileToString(new File(absoluteJsSourceFilename));
            jsSource = JSONValue.escape(jsSourceRaw);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.init();
    }


    @Override
    public void document(Document document) {
        Map<String, String > documentMap = new HashMap<>();
        for (Field field : document.getFields()) {
            documentMap.put(field.getName(), field.getValue());
        }

        StrSubstitutor sub = new StrSubstitutor(documentMap);
        String replacedJsSource = sub.replace(jsSource);

        documentMap.put("jsSource", replacedJsSource);

        String replacedSplashPayload = sub.replace(splashPayload);

        String response = HTTPHelper.post(splashUrl, replacedSplashPayload, "application/json");
        document.setField("splash_payload", response);
        super.document(document);
    }

}
