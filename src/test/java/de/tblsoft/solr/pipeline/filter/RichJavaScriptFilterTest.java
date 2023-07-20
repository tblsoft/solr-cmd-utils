package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tblsoft on 17.05.16.
 */
@Deprecated // javascript engine must be replaced for newer java versions
@Ignore
public class RichJavaScriptFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(RichJavaScriptFilter.class);
        putProperty("filename","./examples/java-script/paragraph-attribute-extractor.js");
    }
    
    @Test
    public void test() {
        configure();
        document(
                DocumentBuilder.document().field("alice","bob").create()
        );

        assertNumberOfDocuments(1);
        assertNumberOfFields(5);
        Field tbl = outputDocumentList.get(0).getField("attr_Form");
        assertFiledList("attr_Form", "Sandale/Pantolette");
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
