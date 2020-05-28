package de.tblsoft.solr.nlp.trie;

import com.google.common.base.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TokenExpander {

    public static List<String> expand(List<String> tokens) {
        List<String> expanded = new ArrayList<>();

        for (int i = 0; i < tokens.size() ; i++) {
            //expanded.add(tokens.get(i));
            StringBuilder sb = new StringBuilder();
            for (int j = i; j < tokens.size(); j++) {
                sb.append(tokens.get(j));
                String token = sb.toString().trim();
                if(!Strings.isNullOrEmpty(token)) {
                    expanded.add(sb.toString().trim());
                }
            }

        }
        return expanded;
    }


    public static List<String> expand(String text) {
        if(text == null) {
            return new ArrayList<>();
        }
        String[] tokens = text.split(" ");
        return expand(Arrays.asList(tokens));
    }
}
