package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tblsoft on 17.05.16.
 */
@Ignore
public class ExternalFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(ExternalFilter.class);
        putProperty("url","https://gk6xr5fxbe.execute-api.eu-central-1.amazonaws.com/default/solr-cmd-utils-filter-test");
    }
    
    @Test
    public void test() {
        configure();
        document(
                DocumentBuilder.document().field("alice","bob").create()
        );

        assertNumberOfDocuments(1);
        assertNumberOfFields(2);
        Field tbl =outputDocumentList.get(0).getField("alice");
        assertFiledList("tbl", "bo", "alice");

    }


    @Test
    public void testEmptyDocument() {
        // disable because it is not useful
    }


    @Test
    public void testPassthrough() {
        // disable because it is not useful
    }
}
