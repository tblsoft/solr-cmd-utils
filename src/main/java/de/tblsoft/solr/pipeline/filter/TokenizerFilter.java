package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.StringTokenizer;

/**
 * Created by tblsoft on 21.10.17.
 * 
 * Tokenize all fields.
 */
public class TokenizerFilter extends AbstractFilter {

	private String delim;

    @Override
    public void init() {
        delim = getProperty("delim", " \t\n\r\f");
        super.init();
    }


    @Override
    public void document(Document document) {
        for(Field field : document.getFields()) {
            for(String value : field.getValues()) {
                StringTokenizer tokenizer = new StringTokenizer(value, delim);
                while(tokenizer.hasMoreElements()) {
                    String token = tokenizer.nextToken();
                    Document newDocument = new Document();
                    newDocument.addField("token", token);
                    super.document(newDocument);
                }
            }
        }
    }
    
}
