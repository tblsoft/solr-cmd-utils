package de.tblsoft.solr.util;

import de.tblsoft.solr.pipeline.bean.Document;

public class DocumentIdHelper {

    public static String resolveId(Document document, String idField, boolean useExplicitIdField) {
        if ("id".equals(idField) && !useExplicitIdField) {
            return document.getId();
        }
        return document.getFieldValue(idField);
    }

}
