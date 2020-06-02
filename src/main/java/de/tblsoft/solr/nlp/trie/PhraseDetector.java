package de.tblsoft.solr.nlp.trie;

import de.tblsoft.solr.nlp.Phrase;

import java.util.ArrayList;
import java.util.List;

public class PhraseDetector extends Trie {



    public List<Phrase> detectPhrases(String text) {
        List<Phrase> phraseList = new ArrayList<>();
        if(text == null) {
            return phraseList;
        }
        String textWindow = text;
        int globalPosition = 0;
        while( textWindow.length() > 0) {
            Phrase phraseEnd = getPhraseEndPosition(textWindow);
            if(phraseEnd != null && phraseEnd.getEndPosition() != null) {

                String phraseText = textWindow.substring(0, phraseEnd.getEndPosition()+1);
                Phrase phrase = new Phrase();
                phrase.setPhraseText(phraseText);
                phrase.setStartPosition(globalPosition);
                phrase.setEndPosition(globalPosition + phraseEnd.getEndPosition() + 1);
                phrase.setLength(phraseEnd.getEndPosition() + 1 );
                phrase.setNormalizedText(phraseEnd.getNormalizedText());
                phraseList.add(phrase);


                textWindow = textWindow.substring(phraseEnd.getEndPosition()+1);
                globalPosition = globalPosition + phraseEnd.getEndPosition() +1;

            }

            int nextTokenizerPosition = findNextTokenizerPosition(textWindow);
            globalPosition = globalPosition + nextTokenizerPosition;
            if(nextTokenizerPosition == -1) {
                break;
            }
            textWindow = textWindow.substring(nextTokenizerPosition);


        }

        return phraseList;

    }
}
