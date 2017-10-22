package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 22.10.17.
 */
public class DuplicateRemovalFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("fieldName", "duplicateField");
        setClazz(DuplicateRemovalFilter.class);
    }




    @Test
    public void testDuplicateRemovalFilter() {
        configure();
        Document document1 = DocumentBuilder.document().field("duplicateField", "foo").field("field2","one").create();
        Document document2 = DocumentBuilder.document().field("duplicateField", "foo").field("field2","two").create();
        Document document3 = DocumentBuilder.document().field("duplicateField", "bar").field("field2","three").create();
        document(document1, document2, document3);

        assertNumberOfDocuments(2);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
