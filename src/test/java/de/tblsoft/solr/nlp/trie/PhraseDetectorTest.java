package de.tblsoft.solr.nlp.trie;

import de.tblsoft.solr.nlp.Phrase;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class PhraseDetectorTest {

    @Test
    public void testNull() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases(null);
        assertEquals(0, phraseList.size());
    }

    @Test
    public void testEmpty() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("");
        assertEquals(0, phraseList.size());
    }

    @Test
    public void testNoPhrase() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("quasiris");
        assertEquals(0, phraseList.size());
    }

    @Test
    public void testSimplePhraseAtBeginning() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("programming test");
        assertEquals(1, phraseList.size());
        assertEquals("programming", phraseList.get(0).getNormalizedText());
    }

    @Test
    public void testSimplePhraseAtBeginningQMap() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("q-map test");
        assertEquals(1, phraseList.size());
        assertEquals("qmap", phraseList.get(0).getNormalizedText());
    }



    @Test
    public void testSimplePhraseAtEnd() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("test programming");
        assertEquals(1, phraseList.size());
        assertEquals("programming", phraseList.get(0).getNormalizedText());
    }

    @Test
    public void testSimplePhraseAtEndQMap() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("test q-map");
        assertEquals(1, phraseList.size());
        assertEquals("qmap", phraseList.get(0).getNormalizedText());
    }


    @Test
    public void testSimpleExact() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("programming");
        assertEquals(1, phraseList.size());
        assertEquals("programming", phraseList.get(0).getNormalizedText());
    }

    @Test
    public void testSimpleExactQMap() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("q-map");
        assertEquals(1, phraseList.size());
        assertEquals("qmap", phraseList.get(0).getNormalizedText());
    }

    @Test
    public void testSimplePhrase() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("test programming test");
        assertEquals(1, phraseList.size());
    }

    @Test
    public void testSimplePhraseQMap() {
        PhraseDetector phraseDetector = createPhraseDetector();
        List<Phrase> phraseList = phraseDetector.detectPhrases("test q-map test");
        assertEquals(1, phraseList.size());
    }


    @Test
    public void testComplexPhrases() {
        PhraseDetector phraseDetector = createPhraseDetector();
        String text = "Das corona-virus ist eine Phrase. Auch zusammengeschrieben ist coronavirus ein Phrase. Und sogar mit Leerzeichen ist corona virus eine Phrase. Die q-map ist eine geo Karte.";
        List<Phrase> phraseList =
                phraseDetector.detectPhrases(text);

        assertEquals(4, phraseList.size());
        assertEquals("corona virus", phraseList.get(0).getNormalizedText());
        assertEquals("corona virus", phraseList.get(1).getNormalizedText());
        assertEquals("corona virus", phraseList.get(2).getNormalizedText());
        assertEquals("qmap", phraseList.get(3).getNormalizedText());


        String expected = "Das corona virus ist eine Phrase. Auch zusammengeschrieben ist corona virus ein Phrase. Und sogar mit Leerzeichen ist corona virus eine Phrase. Die qmap ist eine geo Karte.";
        String actual = PhraseReplacer.replacePhrases(text, phraseList);
        assertEquals(expected, actual);
    }


    PhraseDetector createPhraseDetector() {
        PhraseDetector phraseDetector = new PhraseDetector();

        phraseDetector.insert("programming");
        phraseDetector.insert("corona virus");
        phraseDetector.insert("is");
        phraseDetector.insert("a");
        phraseDetector.insert("way");
        phraseDetector.insert("of");
        phraseDetector.insert("life");
        phraseDetector.insert("qmap");

        return phraseDetector;
    }

}