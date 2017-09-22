package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class FuzzyPermutationFilter extends AbstractFilter {



    private String input;

    private int distance;
    private String output;


    @Override
    public void init() {


        input = getProperty("input", "token");
        output = getProperty("output", "permutations");
        distance = getPropertyAsInt("distance", 2);


        super.init();
    }


    @Override
    public void document(Document document) {
        if(document.getFieldValues(input) == null) {
            super.document(document);
            return;
        }
        Set<String> permutations = new HashSet<String>();
        for(String value : document.getFieldValues(input)) {
            permutations.addAll(permutate(value, distance));
        }
        document.addField(output, new ArrayList<String>(permutations));
        super.document(document);
    }

    private Set<String> permutate(String word, int iterations) {
        Set<String> ret = new HashSet<String>();
        ret.add(word);
        if(iterations <= 0) {
            return ret;
        }
        Set<String> permutations = permutate(word);
        int newIteration = --iterations;
        for(String permutation: permutations) {
            ret.addAll(permutate(permutation, newIteration));
        }

        ret.addAll(permutations);

        return ret;
    }

    private Set<String> permutate(String word) {
        Set<String> ret = new HashSet<String>();
        for (int i = 0; i < word.length(); i++) {
            StringBuilder builder = new StringBuilder(word);
            ret.add(builder.deleteCharAt(i).toString());
        }

        return ret;

    }


}

