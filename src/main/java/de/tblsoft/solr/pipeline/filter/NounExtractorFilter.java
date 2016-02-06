package de.tblsoft.solr.pipeline.filter;


import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;

import java.util.*;


public class NounExtractorFilter extends AbstractFilter {

    private Set<String> dictionary = new HashSet<String>();

    private List<String> fieldWhiteList;

    private List<String> fieldBlackList;

    @Override
    public void end() {

        List<String> dictionaryList = new ArrayList<String>(dictionary);
        Collections.sort(dictionaryList);
        for (String value : dictionaryList) {
            super.field("noun", value);
        }
        super.end();
    }

    @Override
    public void init() {
        fieldWhiteList = getPropertyAsList("fieldWhiteList", null);
        fieldBlackList = getPropertyAsList("fieldBlackList", null);

        super.init();
    }



    @Override
    public void field(String name, String value) {
        if(isFieldIncluded(name)) {
            dictionary.addAll(tokenize(value));
        }
    }

    boolean isFieldIncluded(String name) {
        if(fieldBlackList != null){
            for(String item :fieldBlackList){
                if(name.matches(item)) {
                    return false;
                }
            }
        }

        if(fieldWhiteList != null) {
            for(String item :fieldWhiteList){
                if(name.matches(item)) {
                    return true;
                }
            }
            return false;
        }

        return true;

    }

    boolean isFirstCharUpperCase(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return false;
        }

        if (value.length() < 3) {
            return false;
        }
        char firstChar = value.charAt(0);
        char secondChar = value.charAt(2);
        return Character.isUpperCase(firstChar) && Character.isLowerCase(secondChar);
    }

    boolean containsOnlyGermanCharacters(String value) {
        return value.matches("[A-Za-zÖÄÜöäüß]*");
    }


    Set<String> tokenize(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value);
        Set<String> values = new HashSet<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();

            if (isFirstCharUpperCase(token) && containsOnlyGermanCharacters(token)) {
                values.add(token.toLowerCase());
            }
        }

        return values;

    }
}

