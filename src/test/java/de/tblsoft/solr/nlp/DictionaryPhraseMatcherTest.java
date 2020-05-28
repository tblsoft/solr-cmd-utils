package de.tblsoft.solr.nlp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class DictionaryPhraseMatcherTest {

    @Test
    public void test() {
        DictionaryPhraseMatcher dictionaryPhraseMatcher = new DictionaryPhraseMatcher();
        List<Phrase> phrases = new ArrayList<>();
        phrases.add(createPhrase("magenta","TARIF", "mobil", "TARIF"));
        phrases.add(createPhrase("magenta","TARIF", "eins", "TARIF"));
        dictionaryPhraseMatcher.getDictionary().put("magenta", phrases);


        Phrase input = createPhrase("test", "NN", "magenta", "NE", "eins", "NE", "tarif", "NE", "test", "NE");
        input = createPhrase("magenta", "NE");


        List<String> posTags = dictionaryPhraseMatcher.match(input.getTokens(), input.getPosTags());
        assertThat(posTags, is(input.getPosTags()));

    }

    private Phrase createPhrase(String... tuples) {
        List<String> tokens = new ArrayList<>();
        List<String> posTags = new ArrayList<>();
        for (int i = 0; i < tuples.length ; i++) {
            tokens.add(tuples[i]);
            posTags.add(tuples[i+1]);
            i++;
        }

        Phrase phrase = new Phrase();
        phrase.setTokens(tokens);
        phrase.setPosTags(posTags);

        return phrase;


    }

}