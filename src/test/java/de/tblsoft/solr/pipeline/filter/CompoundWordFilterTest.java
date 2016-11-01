package de.tblsoft.solr.pipeline.filter;

import org.junit.Ignore;
import org.junit.Test;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;

/**
 * Created by tblsoft on 17.05.16.
 */
public class CompoundWordFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(CompoundWordFilter.class);
    }
    
    @Test
    @Ignore
    public void testGartentor() {
        configure();
        document(
                DocumentBuilder.document().field("noun","gartentor").create(),
                DocumentBuilder.document().field("noun","garten").create(),
                DocumentBuilder.document().field("noun","tor").create()
        );
        Field d =outputDocumentList.get(0).getField("tokenized");
        assertFiledList("tokenized", "garten", "tor");

    }
    
    
    @Test
    @Ignore
    public void testLeitungschutzschalter() {
        configure();
        document(
                DocumentBuilder.document().field("noun","leitungsschutzschalter").create(),
                DocumentBuilder.document().field("noun","leitung").create(),
                DocumentBuilder.document().field("noun","schutz").create(),
                DocumentBuilder.document().field("noun","schalter").create()
        );
        Field d =outputDocumentList.get(0).getField("tokenized");
        assertFiledList("tokenized", "maurer", "hammer");

    }

    @Test
    @Ignore
    public void testTurmdeckelschnecke() {
        configure();
        document(
                DocumentBuilder.document().field("noun","turmdeckelschnecke").create(),
                DocumentBuilder.document().field("noun","turm").create(),
                DocumentBuilder.document().field("noun","schnecke").create(),
                DocumentBuilder.document().field("noun","blumen").create(),
                DocumentBuilder.document().field("noun","blume").create()
        );
        Field d =outputDocumentList.get(0).getField("tokenized");
        assertFiledList("tokenized", "maurer", "hammer");

    }
    @Test
    @Ignore
    public void testBlumenzwiebel() {
    	configure();
    	document(
    			DocumentBuilder.document().field("noun","blumenzwiebel").create(),
    			DocumentBuilder.document().field("noun","lumen").create(),
    			DocumentBuilder.document().field("noun","zwiebel").create(),
    			DocumentBuilder.document().field("noun","deckel").create()
    			);
        Document d =outputDocumentList.get(0);
    	assertFiledList("tokenized", "maurer", "hammer");
    	
    }
    
    @Test
    @Ignore
    public void testCompoundWordFilter() {
        configure();
        document(
                DocumentBuilder.document().field("noun","Maurer").create(),
                DocumentBuilder.document().field("noun","Hammer").create(),
                DocumentBuilder.document().field("noun","Maurerhammer").create()
        );

        assertFiled("noun", "maurerhammer");
        assertFiledList("compound", "maurer", "hammer");

    }

    @Test
    @Ignore
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(0);
    }


    @Test
    @Ignore
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertNumberOfDocuments(0);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
