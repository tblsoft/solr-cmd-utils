package de.tblsoft.solr.pipeline.filter;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.DateUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 23.09.17.
 */
public class SimpleMapping {


    private Map<String, List<String>> mapping = new HashMap<String, List<String>>();
    private Map<String, List<String>> mappingFunctions = new HashMap<String, List<String>>();
    private Map<String, String> joins = new HashMap<String, String>();

    private List<String> mappingConfiguration;

    public SimpleMapping(List<String> mappingConfiguration) {
        this.mappingConfiguration = mappingConfiguration;
        readConfig();
    }

    private void readConfig() {


        for (String v : mappingConfiguration) {
            if (v.startsWith("join:")) {
                v = v.replace("join:", "");
                String[] s = v.split("=", 2);
                joins.put(s[0], s[1]);
            } else {
                String[] s = v.split("->");

                String[] f = s[1].split(Pattern.quote("|"));

                List<String> mappingList = mapping.get(s[0]);
                if (mappingList == null) {
                    mappingList = new ArrayList<String>();
                }
                mappingList.add(f[0]);
                mapping.put(s[0], mappingList);
                List<String> functions = new ArrayList<String>();
                for (int i = 1; i < f.length; i++) {
                    functions.add(f[i]);
                }
                mappingFunctions.put(f[0], functions);
            }
        }
    }


    public static String executeFunction(String function, String value) {
        if(Strings.isNullOrEmpty(function)) {
            return value;
        }

        if("md5".equals(function)) {
            return DigestUtils.md5Hex(value);
        } else if ("mapGermanChars".equals(function)) {
            return mapGermanChars(value);
        } else if ("lowercase".equals(function)) {
            return StringUtils.lowerCase(value);
        } else if ("urlencode".equals(function)) {
            return UrlUtil.encode(value);
        } else if ("urldecode".equals(function)) {
            return UrlUtil.decode(value);
        } else if ("trim".equals(function)) {
            return StringUtils.trim(value);
        } else if ("toSolrDate".equals(function)) {
            return DateUtils.toSolrDate(value);
        } else if ("uniq".equals(function)) {
            return value;
        }

        throw new IllegalArgumentException("The function: " + function
                + " is not implemented.");
    }


    public static void executeFieldFunction(String function, Field field) {

        if("md5".equals(function)) {
            return;
        } else if ("mapGermanChars".equals(function)) {
            return;
        }else if ("lowercase".equals(function)) {
            return;
        } else if ("urlencode".equals(function)) {
            return;
        } else if ("urldecode".equals(function)) {
            return;
        } else if ("trim".equals(function)) {
            return;
        } else if ("toSolrDate".equals(function)) {
            return;
        } else if ("uniq".equals(function)) {
            Set<String> uniqValues= new HashSet<String>();
            uniqValues.addAll(field.getValues());
            field.setValues(new ArrayList(uniqValues));
            return;
        }
        throw new IllegalArgumentException("The function: " + function
                + " is not implemented.");
    }


    public Map<String, List<String>> getMapping() {
        return mapping;
    }

    public Map<String, List<String>> getMappingFunctions() {
        return mappingFunctions;
    }

    public Map<String, String> getJoins() {
        return joins;
    }


    static String mapGermanChars(String value) {
        value = value.replaceAll("\u00c4", "Ae");
        value = value.replaceAll("\u00d6", "Oe");
        value = value.replaceAll("\u00dc", "Ue");
        value = value.replaceAll("\u00e4", "ae");
        value = value.replaceAll("\u00f6", "oe");
        value = value.replaceAll("\u00fc", "ue");
        value = value.replaceAll("\u00df", "ss");
        return value;
    }
}
