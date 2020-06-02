package de.tblsoft.solr.nlp.trie;

import de.tblsoft.solr.nlp.Phrase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PhraseReplacer {


    private static Logger LOG = LoggerFactory.getLogger(PhraseReplacer.class);

    public static String replacePhrases(String text, List<Phrase> phrases) {
        try {
            int offsetCorrection = 0;
            for (Phrase phrase : phrases) {
                String prefix = text.substring(0, phrase.getStartPosition() - offsetCorrection);
                String suffix = text.substring(phrase.getEndPosition() - offsetCorrection);
                text = prefix + phrase.getNormalizedText() + suffix;
                int diff = phrase.getLength() - phrase.getNormalizedText().length();

                offsetCorrection = offsetCorrection + diff;
            }
            return text;
        } catch(Exception e) {
            LOG.error("Could not replace phrase {}", text, e);
            throw e;
        }
    }
}
