package de.tblsoft.solr.pipeline.bean;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.Comparator;

/**
 * Created by tblsoft on 22.09.17.
 */
public class DocumentComparator implements Comparator<Document> {


    private String fieldName;
    public DocumentComparator(String fieldName) {
        this.fieldName = fieldName;

    }

    public int compare(Document first, Document second) {
        if(first == null && second == null) {
            return 0;
        }
        if(first == null) {
            return -1;
        }
        if(second == null) {
            return 1;
        }



        Field firstField = first.getField(fieldName);
        Field secondField = second.getField(fieldName);

        if(firstField == null && secondField == null) {
            return 0;
        }

        if(firstField == null) {
            return -1;
        }
        if(secondField == null) {
            return 1;
        }

        String firstFieldValue = firstField.getValue();
        String secondFieldValue = secondField.getValue();



        if(firstFieldValue == null && secondFieldValue == null) {
            return 0;
        }

        if(firstFieldValue == null) {
            return -1;
        }
        if(secondFieldValue == null) {
            return 1;
        }
        return firstFieldValue.compareTo(secondFieldValue);
    }
}
