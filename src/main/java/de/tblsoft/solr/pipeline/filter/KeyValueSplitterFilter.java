package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.DocumentUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by tblsoft 18.05.17.
 */
public class KeyValueSplitterFilter extends AbstractFilter {

    private String fieldName;
    private String delimiter;
    private String keyValueDelimiter;
    private String keyPrefix;
    private boolean normalizeKey;


    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        verify(fieldName, "For the KeyValueSplitterFilter a fieldName must be defined.");
        delimiter = getProperty("delimiter", "|");
        verify(delimiter, "For the KeyValueSplitterFilter a delimiter must be defined.");
        keyValueDelimiter = getProperty("keyValueDelimiter", "=");
        verify(keyValueDelimiter, "For the KeyValueSplitterFilter a keyValueDelimiter must be defined.");
        keyPrefix = getProperty("keyPrefix", "");
        normalizeKey = getPropertyAsBoolean("normalizeKey", true);

        super.init();
    }


    @Override
    public void document(Document document) {

        List<String> values = document.getFieldValues(fieldName, new ArrayList<String>());
        for(String value: values) {
            String[] splittedValues = value.split(Pattern.quote(delimiter));
            for(String splittedValue : splittedValues) {
                Field field = processKeyValue(splittedValue);
                if(field != null) {
                    document.addField(field);
                }
            }
        }
        super.document(document);

    }

    Field processKeyValue(String keyValue) {
        if(Strings.isNullOrEmpty(keyValue)) {
            return null;
        }
        String[] splittedValue = keyValue.split(Pattern.quote(keyValueDelimiter));
        if(splittedValue.length != 2) {
            return null;
        }
        String key = keyPrefix + splittedValue[0];

        if(normalizeKey) {
            key = DocumentUtils.normalizeFieldKey(key);
        }
        Field field = new Field(key, splittedValue[1]);
        return field;

    }

}
