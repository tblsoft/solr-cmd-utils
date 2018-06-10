package de.tblsoft.solr.util;

import java.util.Map;

/**
 * Created by tbl on 10.06.18.
 */
public class MapUtils {
    public static <K, V> V getOrDefault(Map<K,V> map, K key, V defaultValue) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }
}
