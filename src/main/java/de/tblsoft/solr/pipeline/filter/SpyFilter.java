package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SpyFilter extends AbstractFilter {

    @Override
    public void field(String name, String value) {
        //System.out.println("name: " + name);
        if("id".equals(name)) {
            int count = 0;
            for(String splitted :value.split("_")) {
                super.field(name + count ,splitted);
            };
            super.field(name,value);

        }
    }
}
