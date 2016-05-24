package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.bean.DocumentDiff;
import de.tblsoft.solr.pipeline.bean.FieldDiff;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tblsoft on 24.05.16.
 */
public class DocumentDifferTest {

    @Test
    public void testEqualDocuments() {
        Document document1 = DocumentBuilder.document().field("foo","bar").create();
        Document document2 = DocumentBuilder.document().field("foo","bar").create();
        DocumentDiff diff = DocumentDiffer.compare(document1, document2);
        Assert.assertEquals(0, diff.getFieldDiffs().size());
    }

    @Test
    public void testDiffDocuments() {
        Document document1 = DocumentBuilder.document().field("foo","bar").field("change","old").create();
        Document document2 = DocumentBuilder.document().field("foo", "bar").field("change","new").create();
        DocumentDiff diff = DocumentDiffer.compare(document1, document2);
        Assert.assertEquals(1, diff.getFieldDiffs().size());
        Assert.assertEquals(FieldDiff.DiffType.DIFF, diff.getFieldDiffs().get(0).getDiffType());
    }

    @Test
    public void testDeleteField() {
        Document document1 = DocumentBuilder.document().field("foo","bar").field("change","old").create();
        Document document2 = DocumentBuilder.document().field("foo", "bar").create();
        DocumentDiff diff = DocumentDiffer.compare(document1, document2);
        Assert.assertEquals(1, diff.getFieldDiffs().size());
        Assert.assertEquals(FieldDiff.DiffType.DELETE, diff.getFieldDiffs().get(0).getDiffType());
    }

    @Test
    public void testCreateField() {
        Document document1 = DocumentBuilder.document().field("foo","bar").create();
        Document document2 = DocumentBuilder.document().field("foo", "bar").field("change","new").create();
        DocumentDiff diff = DocumentDiffer.compare(document1, document2);
        Assert.assertEquals(1, diff.getFieldDiffs().size());
        Assert.assertEquals(FieldDiff.DiffType.CREATE, diff.getFieldDiffs().get(0).getDiffType());
    }
}
