package de.tblsoft.solr.nlp.trie;

import de.tblsoft.solr.nlp.Phrase;

import java.util.List;

public class PhraseReplacer {

    public static String replacePhrases(String text, List<Phrase> phrases) {
        int offsetCorrection = 0;
        for(Phrase phrase : phrases) {
            String prefix = text.substring(0, phrase.getStartPosition() - offsetCorrection);
            String suffix = text.substring(phrase.getEndPosition() - offsetCorrection);
            text = prefix + phrase.getNormalizedText() + suffix;
            int diff = phrase.getLength() - phrase.getNormalizedText().length();

            offsetCorrection = offsetCorrection + diff;
        }
        return text;
    }
}
