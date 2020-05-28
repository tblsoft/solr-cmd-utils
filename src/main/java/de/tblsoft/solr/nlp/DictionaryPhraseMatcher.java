package de.tblsoft.solr.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryPhraseMatcher {

    private Map<String, List<Phrase>> dictionary = new HashMap<>();


    public void addPhrase(String phrase) {
        Phrase phraseParsed = createPhrase(phrase);
        String firstToken = phraseParsed.getTokens().get(0);
        addPhrase(firstToken, phrase);
    }

    public void addPhrase(String firstToken, Phrase phrase) {
        List<Phrase> phrases = dictionary.get(firstToken);
        if(phrases == null) {
            phrases = new ArrayList<>();
        }
        phrases.add(phrase);
        dictionary.put(firstToken, phrases);
    }

    public void addPhrase(String firstToken, String phrase) {
        Phrase phraseParsed = createPhrase(phrase);
        addPhrase(firstToken, phraseParsed);
    }

    public List<String> match(List<String> inputTokens, List<String> posTags) {
        List<String> newPosTags = new ArrayList<>();
        for (int i = 0; i < inputTokens.size(); i++) {
            String normalizedToken = normalize(inputTokens.get(i));
            List<Phrase> phrases = dictionary.get(normalizedToken);
            if(phrases != null) {
                boolean phraseMatched = false;
                for(Phrase phrase : phrases) {
                   if(matchPhrase(phrase, inputTokens, i)) {
                       i = i + phrase.getTokens().size() - 1;
                       newPosTags.addAll(phrase.getPosTags());
                       phraseMatched = true;
                       break;
                   }
                }
                if(!phraseMatched) {
                    newPosTags.add(posTags.get(i));
                }
            } else {
                newPosTags.add(posTags.get(i));
            }
        }
        return newPosTags;

    }

    private boolean matchPhrase(Phrase phrase, List<String> inputTokens, int start) {
        List<String> phraseTokens = phrase.getTokens();
        if(start + phraseTokens.size() > inputTokens.size()) {
            return false;
        }


        for (int i = 0; i < phraseTokens.size() ; i++) {
            String normalizedToken = normalize(inputTokens.get(start + i));
            String normalizedPhraseToken = normalize(phraseTokens.get(i));
            if(!normalizedToken.equals(normalizedPhraseToken)) {
                return false;
            }

        }
        return true;
    }

    public static List<Phrase> createPhrases(String... values) {
        List<Phrase> phrases = new ArrayList<>();
        for(String value: values) {
            phrases.add(createPhrase(value));
        }
        return phrases;

    }

    public static Phrase createPhrase(String value) {
        List<String> tokenList = new ArrayList<>();
        List<String> posTagList = new ArrayList<>();

        String[] tokens = value.split(" ");
        for (String token : tokens) {
            String[] pos = token.split("_");
            if(pos.length != 2) {
                throw new IllegalArgumentException("invalid pos tag format for input: " + value);
            }
            tokenList.add(pos[0]);
            posTagList.add(pos[1]);
        }
        Phrase phrase = new Phrase();
        phrase.setTokens(tokenList);
        phrase.setPosTags(posTagList);

        return phrase;

    }


    private String normalize(String value) {
        if(value == null) {
            return value;
        }
        return value.toLowerCase();
    }

    /**
     * Getter for property 'dictionary'.
     *
     * @return Value for property 'dictionary'.
     */
    public Map<String, List<Phrase>> getDictionary() {
        return dictionary;
    }

    /**
     * Setter for property 'dictionary'.
     *
     * @param dictionary Value to set for property 'dictionary'.
     */
    public void setDictionary(Map<String, List<Phrase>> dictionary) {
        this.dictionary = dictionary;
    }
}
