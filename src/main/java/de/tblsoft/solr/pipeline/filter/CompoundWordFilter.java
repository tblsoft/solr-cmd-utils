package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft 17.05.16.
 */
public class CompoundWordFilter extends AbstractFilter {

    private String nounnFieldName;

    private List<String> nounList = new ArrayList<String>();

    @Override
    public void init() {
        nounnFieldName = getProperty("nounnFieldName", "noun");
        super.init();
    }

    @Override
    public void document(Document document) {
        String noun = document.getFieldValue(nounnFieldName);
        if(!Strings.isNullOrEmpty(noun)) {
            nounList.add(noun.toLowerCase());
        }
    }

    @Override
    public void end() {
        for(String noun: nounList) {
            List<String> compoundList = new ArrayList<String>();
            for(String compound: nounList) {
            	if(noun.contains(compound) && !noun.equals(compound)) {
            		int diff = Math.abs(compound.length() - noun.length());
            		if(diff > 3) {
            			compoundList.add(compound);
            		}
                }
            }
            if(!compoundList.isEmpty()) {
                Document document = new Document();
                document.addField("noun",noun);
                for(String compound: compoundList) {
                    document.addField("compound", compound);
                }
                super.document(document);
            }
        }
        super.end();
    }
}
