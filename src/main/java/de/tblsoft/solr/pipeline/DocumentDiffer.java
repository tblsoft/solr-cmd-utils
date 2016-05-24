package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentDiff;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.bean.FieldDiff;

import java.util.Collections;
import java.util.List;

/**
 * Created by tblsoft on 24.05.16.
 */
public class DocumentDiffer {

    public static DocumentDiff compare(Document d1, Document d2) {
        DocumentDiff diff = new DocumentDiff();
        for(Field field :d1.getFields()) {
            List<String> values1 = field.getValues();
            List<String> values2 = d2.getFieldValues(field.getName());
            d2.deleteField(field.getName());

            FieldDiff.DiffType changeType = getChangeType(values1, values2);
            FieldDiff fieldDiff = new FieldDiff();
            fieldDiff.setDiffType(changeType);
            if(!changeType.equals(FieldDiff.DiffType.EQUAL)) {
                fieldDiff.setOldValues(values1);
                fieldDiff.setNewValue(values2);
                diff.getFieldDiffs().add(fieldDiff);
            }
        }

        for(Field field: d2.getFields()) {
            FieldDiff fieldDiff = new FieldDiff();
            fieldDiff.setDiffType(FieldDiff.DiffType.CREATE);
            fieldDiff.setNewValue(field.getValues());
            diff.getFieldDiffs().add(fieldDiff);
        }

        return diff;



    }

    public static FieldDiff.DiffType getChangeType(List<String> values1, List<String> values2) {
        if(values1 == null && values2 == null) {
            return FieldDiff.DiffType.EQUAL;
        }
        if(values1== null) {
            return FieldDiff.DiffType.CREATE;
        }
        if(values2== null) {
            return FieldDiff.DiffType.DELETE;
        }
        if(values1.size() != values2.size()) {
            return FieldDiff.DiffType.DIFF;
        }
        Collections.sort(values1);
        Collections.sort(values2);
        if(values1.equals(values2)) {
            return FieldDiff.DiffType.EQUAL;
        }
        return FieldDiff.DiffType.DIFF;
    }




}
