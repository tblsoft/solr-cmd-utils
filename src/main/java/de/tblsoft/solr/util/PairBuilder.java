package de.tblsoft.solr.util;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Pattern;

/**
 * Created by tbl on 29.01.18.
 */
public class PairBuilder {

    public static Pair<String, String> createPair(String value, String seperator) {
        String[] valueSplitted = value.split(Pattern.quote(seperator));
        if(valueSplitted.length == 2) {
            return new ImmutablePair<String, String>(valueSplitted[0], valueSplitted[1]);
        }
        return null;
    }
}
