package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Permute document fields with values from previous documents specified in offset.
 * Useful for test or anonymization purposes.
 */
public class OffsetPermutationFilter extends AbstractFilter {
    private long offset;
    private List<String> fields;
    List<Document> previousDocs;

    @Override
    public void init() {
        offset = getPropertyAsInt("offset", 1);
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the OffsetPermutationFilter fields must be defined!");

        previousDocs = new ArrayList<>();

        super.init();
    }

    @Override
    public void document(Document doc) {
        if(previousDocs.size() >= offset && previousDocs.size() > 0) {
            Document previousDoc = previousDocs.remove(0);
            Document permutedDoc = permuteDocument(doc, previousDoc, fields);

            super.document(permutedDoc);
        }

        previousDocs.add(doc);
    }

    protected static Document permuteDocument(Document currentDoc, Document previousDoc, List<String> fields) {
        Document result = new Document(currentDoc);
        if(previousDoc != null) {
            for (String field : fields) {
                result.setField(field, previousDoc.getFieldValues(field));
            }
        }

        return result;
    }
}
