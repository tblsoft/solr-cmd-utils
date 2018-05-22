package de.tblsoft.solr.pipeline.filter;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleGenderFilterTest {
    @Test
    public void filterGender() throws Exception {
        // given
        SimpleGenderFilter simpleGenderFilter = new SimpleGenderFilter();
        simpleGenderFilter.setReplaceableWords(Arrays.asList("arzt"));
        simpleGenderFilter.setTrimEndings(Arrays.asList("e"));

        // then
        assertNull(simpleGenderFilter.filterGender(null));
        assertArrayEquals(new String[]{""}, simpleGenderFilter.filterGender("").toArray());
        assertEquals(Arrays.asList("Chirurg", "Chirurgin"), simpleGenderFilter.filterGender("Chirurg/-in"));
        assertEquals(Arrays.asList("Laborarzt", "Laborärztin"), simpleGenderFilter.filterGender("Laborarzt/-ärztin"));
        assertEquals(Arrays.asList("Kardiologe", "Kardiologin"), simpleGenderFilter.filterGender("Kardiologe/-in"));
        assertEquals(Arrays.asList("Arzt", "Ärztin"), simpleGenderFilter.filterGender("Arzt/-Ärztin"));
    }
}
