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
public class JavaScriptFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(JavaScriptFilter.class);
        putProperty("filename","./examples/java-script/transform.js");
    }
    
    @Test
    public void test() {
        configure();
        document(
                DocumentBuilder.document().field("alice","bob").create()
        );

        assertNumberOfDocuments(2);
        assertNumberOfFields(3);
        Field tbl =outputDocumentList.get(0).getField("tbl");
        assertFiledList("tbl", "bar", "alice", "bob");

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
