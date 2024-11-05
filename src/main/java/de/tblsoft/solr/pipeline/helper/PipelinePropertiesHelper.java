package de.tblsoft.solr.pipeline.helper;

import de.tblsoft.solr.util.DateUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.*;

public class PipelinePropertiesHelper {

    public static String getProperty(Map<String, ?> property,
                                     Map<String, String> variables,
                                     String name,
                                     String defaultValue) {
        if(property == null) {
            return defaultValue;
        }
        String value = (String) property.get(name);
        if(value != null) {
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            value = strSubstitutor.replace(value);
            return value;
        }
        return defaultValue;
    }

    public static Boolean getPropertyAsBoolean(Map<String, ?> property,
                                        Map<String, String> variables,
                                        String name,
                                        Boolean defaultValue) {
        String value = getProperty(property, variables, name,null);
        if(value == null) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    public static List<String> getPropertyAsList(Map<String, ?> property, Map<String, String> variables, String name, List<String> defaultValue) {
        if(property == null) {
            return defaultValue;
        }
        List<String> values = (List<String>) property.get(name);
        if(values != null) {
            List<String> substitutedValues = new ArrayList<>();
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            for(String value : values) {
                value = strSubstitutor.replace(value);
                substitutedValues.add(value);
            }

            return substitutedValues;
        }
        return defaultValue;
    }

    public static Map<String, String> getPropertyAsMapping(Map<String, ?> property,
                                                           Map<String, String> variables,
                                                           String name,
                                                           Map<String, String> defaultValue,
                                                           String splitter) {
        if(property == null) {
            return defaultValue;
        }
        Map<String, String> mapping = new HashMap<>();
        List<String> rawValues = getPropertyAsList(property, variables,name, new ArrayList<>());
        for (String rawValue : rawValues) {
            String[] splittedValue = rawValue.split(splitter);
            if(splittedValue.length < 2) {
                throw new RuntimeException("The mapping is not correct configured: " + rawValue);
            }
            mapping.put(splittedValue[0], splittedValue[1]);
        }
        return mapping;
    }

    public static int getPropertyAsInt(Map<String, ?> property,
                                Map<String, String> variables,
                                String name,
                                int defaultValue) {
        String value = getProperty(property, variables, name, null);
        if(value != null) {
            return Integer.valueOf(value).intValue();
        }
        return defaultValue;
    }

    public static float getPropertyAsFloat(Map<String, ?> property,
                                    Map<String, String> variables,
                                    String name,
                                    float defaultValue) {
        String value = getProperty(property, variables, name,null);
        if(value != null) {
            return Float.valueOf(value);
        }
        return defaultValue;
    }

    public static Date getPropertyAsDate(Map<String, ?> property,
                                         Map<String, String> variables,
                                         String name,
                                         Date defaultValue) {
        String value = getProperty(property, variables, name,null);
        if(value != null) {
            return DateUtils.getDate(value);
        }
        return defaultValue;
    }

    public static String[] getPropertyAsArray(Map<String, ?> property,
                                              Map<String, String> variables,
                                              String name,
                                              String[] defaultValue) {
        List<String> list = getPropertyAsList(property, variables, name, null);
        if(list == null) {
            return defaultValue;
        }
        return list.toArray(new String[list.size()]);
    }

    public static Long getPropertyAsLong(Map<String, ?> property,
                                  Map<String, String> variables,
                                  String name,
                                  Long defaultValue) {
        String value = getProperty(property, variables, name, null);
        if(value == null) {
            return defaultValue;
        }

        return Long.valueOf(value);

    }
}
