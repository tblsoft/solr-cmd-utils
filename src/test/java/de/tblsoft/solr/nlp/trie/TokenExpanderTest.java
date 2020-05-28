package de.tblsoft.solr.nlp.trie;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TokenExpanderTest {


    @Test
    public void testExpandEmpty() {
        List<String> actual = TokenExpander.expand("");
        assertEquals(0, actual.size());
    }


    @Test
    public void testExpandNull() {
        String text = null;
        List<String> actual = TokenExpander.expand(text);
        assertEquals(0, actual.size());
    }


    @Test
    public void testExpandSingleWord() {
        List<String> actual = TokenExpander.expand("test");
        assertEquals(1, actual.size());
    }

    @Test
    public void testExpandTwoWords() {
        List<String> actual = TokenExpander.expand("test das");
        assertEquals(3, actual.size());

        List<String> expected = new ArrayList<>();
        expected.add("test");
        expected.add("das");
        expected.add("testdas");

        assertThat("List equality without order",
                actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    public void testExpand() {
        List<String> actual = TokenExpander.expand("apple iphone xs 2. generation");

        List<String> expected = new ArrayList<>();
        expected.add("apple");
        expected.add("iphone");
        expected.add("xs");
        expected.add("appleiphone");
        expected.add("iphonexs");
        expected.add("appleiphonexs");
        expected.add("appleiphonexs2.");
        expected.add("appleiphonexs2.generation");
        expected.add("iphonexs2.");
        expected.add("iphonexs2.generation");
        expected.add("2.generation");
        expected.add("generation");
        expected.add("2.");
        expected.add("xs2.");
        expected.add("xs2.generation");


        assertThat("List equality without order",
                actual, containsInAnyOrder(expected.toArray()));


    }
}
