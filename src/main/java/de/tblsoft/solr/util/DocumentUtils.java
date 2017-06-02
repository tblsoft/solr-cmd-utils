package de.tblsoft.solr.util;

public class DocumentUtils {

	public static String normalizeFieldKey(String key) {
		key = key.replaceAll(" ", "_");
		key = key.replaceAll("[^a-zA-Z0-9_-]+","");
		return key;
	}

}
