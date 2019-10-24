package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;


public class RegexFindFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("regex","([A-Za-zÄÖÜäöü][^\\.!?]*[\\.!?])");
        putProperty("sourceField", "text");
        putProperty("destField", "sentences");
        setClazz(RegexFindFilter.class);
    }

    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertFiled("foo","bar");
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(0);
    }

    @Test
    public void testJoinerFilter() {
        configure();
        createField("text", "Microsoft fordert die deutsche Industrie auf, bei der Digitalisierung stärker auf die Tube zu drücken. Damit soll der internationale Wettbewerb bestanden werden. Der Staat soll ebenfalls seine Digitalisierungsbemühungen vorantreiben – und dabei Vorbild sein.");
        runTest();
        assertFiledList("sentences",
                "Microsoft fordert die deutsche Industrie auf, bei der Digitalisierung stärker auf die Tube zu drücken.",
                "Damit soll der internationale Wettbewerb bestanden werden.",
                "Der Staat soll ebenfalls seine Digitalisierungsbemühungen vorantreiben – und dabei Vorbild sein.");
    }
}
