package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class DictionaryNormalizationFilter extends AbstractFilter {

    private Set<String> tokens = new HashSet<String>();

    @Override
    public void init() {
        super.init();
    }


    @Override
    public void document(Document document) {
        String phrase = document.getFieldValue("token");
        phrase = normalizePhrase(phrase);
        StringTokenizer tokenizer = new StringTokenizer(phrase);
        while(tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if(token.contains(".")) {
                continue;
            }
            if(StringUtils.isEmpty(token)) {
                continue;
            }
            if(token.length() < 2) {
                continue;
            }
            // contains digit?
            if(token.matches(".*\\d+.*")) {
                continue;
            }
            if(token.startsWith("-")) {
                continue;
            }
            if(token.endsWith("-")) {
                continue;
            }

            tokens.add(token);
        }


    }

    String normalizePhrase(String token) {
        token = token.replaceAll("[()\",'*+%°/\\[\\]?!:]", "");
        token = token.toLowerCase().trim();
        return token;
    }

    @Override
    public void end() {
        List<String> sortedTokens = new ArrayList<String>(tokens);
        Collections.sort(sortedTokens);
        for(String token: sortedTokens) {
            Document document = new Document();
            document.setField("token", token);
            super.document(document);
        }
        super.end();
    }
}

