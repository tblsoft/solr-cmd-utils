package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SpyFilter extends AbstractFilter {

    List<String> fields;

    @Override
    public void init() {
        fields = getPropertyAsList("fields", new ArrayList<String>());
        super.init();
    }

    @Override
    public void field(String name, String value) {
        if(fields.isEmpty() || fields.contains(name)) {
            System.out.println(name + ": " + value);
        }
        super.field(name,value);
    }
}
