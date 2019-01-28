package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;
import de.tblsoft.solr.util.DateUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FunctionFactory {


    private static Map<String, Function<String, String>> functionMap = new HashMap<>();
    static {
        functionMap.put("lower", t -> t.toLowerCase());
        functionMap.put("upper", t -> t.toUpperCase());
        functionMap.put("trim", t -> t.trim());
        functionMap.put("md5", t -> DigestUtils.md5Hex(t));
        functionMap.put("mapGermanChars", t -> SimpleMapping.mapGermanChars(t));
        functionMap.put("mapFranceChars", t -> SimpleMapping.mapFranceChars(t));
        functionMap.put("urldecode", t -> UrlUtil.encode(t));
        functionMap.put("removeWhitespace", t -> t.replaceAll(" ", ""));
        functionMap.put("toSolrDate", t -> DateUtils.toSolrDate(t));
        functionMap.put("removeSpecialChars", t -> t.replaceAll("[^a-zA-Z0-9']+", " "));
        functionMap.put("leftpad", t -> StringUtils.leftPad(t, 10, "-" ));
    }

    public static Function<String, String> get(String name) {
        Function<String, String> ret = functionMap.get(name);
        if(ret == null) {
            throw new IllegalArgumentException("The function name " + name + " is not registered.");
        }
        return ret;
    }

}
