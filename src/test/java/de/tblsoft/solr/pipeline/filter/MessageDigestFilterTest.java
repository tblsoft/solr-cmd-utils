package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

import java.util.Arrays;


public class MessageDigestFilterTest extends AbstractFilterTest {
    @Override
    public void configure() {
        putProperty("algorithm","SHA-512");
        putProperty("salt", "xyz");
        addProperty("fields", "id");
        addProperty("fields", "scopes");
        addProperty("fields", "phone");
        setClazz(MessageDigestFilter.class);
    }

    @Test
    public void document() {
        // given
        configure();
        Document doc = new Document();
        doc.addField("id", "customer-101");
        doc.addField("scopes", Arrays.asList("read", "write"));
        doc.addField("name", "John Doe");
        doc.setField("phone", (String) null);

        // when
        document(doc);

        // then
        assertFiled("id",         "515472304181ba9e821812188ff2b55a90a05c26798b78d38866bf75a59b805007888fc604a6ece5cddb2686f284bd765fdfb4e098da70b5ca86b5794587bca2");
        assertFiledList("scopes", "ee021c5aa94c55f1dbbe287200618d386799f21ce4e35af71c9e7474267ebaf5fde5436ea44d689c8abd9dbb24e76da9493f982453cad987d1ca003f9eb9ef34",
                                  "8039e274249e5df52a780f1c3d913cb1769d8edb30707ed14fa453f701c8177fbc4e72c423fda59dbd95b5ccd951b2a73c73307ea4eea72fd0383cb49d1274a6");
        assertFiled("name", "John Doe");
        assertFiled("phone", null);
        assertNumberOfDocuments(1);
        assertNumberOfFields(4);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}