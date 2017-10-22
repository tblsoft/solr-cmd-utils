package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 21.10.17.
 */
public class TokenizerFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(TokenizerFilter.class);
    }

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(0);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }


    @Test
    public void testPassthrough() {
        // for a tokenizer we don't pass through documents
    }

    @Test
    public void testTokenizerFilter() {
        configure();
        createField("text", "today is a nice day");
        createField("text2", "because the sun is shining");
        runTest();
        assertNumberOfDocuments(10);
        assertFiled("token", "today");

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
