package de.tblsoft.solr.pipeline.filter;


import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MappingFilter extends AbstractFilter {



    private Map<String,String> mapping = new HashMap<String, String>();
    private Map<String,String> joins = new HashMap<String, String>();




    @Override
    public void init() {
        List<String> tempMapping = getPropertyAsList("mapping", new ArrayList<String>());
        for(String v: tempMapping) {
            if(v.startsWith("join:")) {
                v = v.replace("join:", "");
                String[] s = v.split("=");
                joins.put(s[0], s[1]);
            } else {
                String[] s = v.split("->");
                mapping.put(s[0], s[1]);
            }
        }
        super.init();
    }


    @Override
    public void document(Document document) {
        Document mappedDocument = new Document();
        for(Field f : document.getFields()) {
            String mappedName = mapping.get(f.getName());
            if(!Strings.isNullOrEmpty(mappedName)) {
                mappedDocument.setField(mappedName, f.getValues());
            }
        }

        for(Map.Entry<String, String> entry: joins.entrySet()) {
            Map<String, String> documentMap = new HashMap<String, String>();
            for(Field field: document.getFields()) {
                documentMap.put(field.getName(),field.getValue());
            }
            StrSubstitutor sub = new StrSubstitutor(documentMap);
            String value = sub.replace(entry.getValue());
            mappedDocument.addField(entry.getKey(), value);
        }

        if(mappedDocument.getFields().size() != 0) {
            super.document(mappedDocument);
        }

    }

}

