package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by tblsoft on 21.10.17.
 * 
 * Tokenize all fields.
 */
public class ShingleFilter extends AbstractFilter {

	private String delim;

	private int minShingles = 2;
	private int maxShingles = 2;

    @Override
    public void init() {
        delim = getProperty("delim", " \t\n\r\f,.");
        minShingles = getPropertyAsInt("minShingles", 2);
        maxShingles = getPropertyAsInt("maxShingles", 2);
        super.init();
    }


    @Override
    public void document(Document document) {

        List<String> shingles = new ArrayList<>();
        for(Field field : document.getFields()) {
            for(String value : field.getValues()) {
                StringTokenizer tokenizer = new StringTokenizer(value, delim);
                List<String> tokens = new ArrayList<>();
                while(tokenizer.hasMoreElements()) {
                    String token = tokenizer.nextToken();
                    tokens.add(token);
                }

                String[] tokenArray = tokens.toArray(new String[0]);
                shingles.addAll(shingle(tokenArray, minShingles, maxShingles));
            }
        }


        for(String shingle : shingles) {
            Document d = new Document();
            d.setField("shingle", shingle);
            super.document(d);
        }
    }


    private List<String>  shingle(String[] tokens, int minShingles, int maxShingles) {
        List<String> shingles = new ArrayList<>();
        for (int i = minShingles; i <= maxShingles; i++) {
            shingles.addAll(shingle(tokens, i));
        }
        return shingles;
    }


    private List<String> shingle(String[] tokens, int windowSize) {
        List<String> shingles = new ArrayList<>();
        for (int i = 0; i < tokens.length ; i++) {
            int start = i;
            int end = i + windowSize;
            if(end > tokens.length) {
                break;
            }

            StringBuilder shingle = new StringBuilder();
            for (int j = start; j < end; j++) {
                shingle.append(tokens[j]);
                shingle.append(" ");
            }

            shingles.add(shingle.toString());
        }
        return shingles;
    }
    
}
