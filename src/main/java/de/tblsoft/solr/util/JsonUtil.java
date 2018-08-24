package de.tblsoft.solr.util;

import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by tbl on 15.07.18.
 */
public class JsonUtil {

    public static <T> T parse( String location, String jsonPath) throws IOException {
        InputStream json = IOUtils.getInputStream(location);
        return JsonPath.parse(json).read(jsonPath);
    }

}
