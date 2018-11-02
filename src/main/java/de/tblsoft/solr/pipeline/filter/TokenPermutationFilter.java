package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Permutate document fields with randomly selected token values from documents in pool.re
 * Useful for test or anonymization purposes.
 */
public class TokenPermutationFilter extends AbstractFilter {
    private List<String> fields;
    private int minTokens;
    private int maxTokens;
    private int poolSize;

    List<Document> previousDocs;

    @Override
    public void init() {
        poolSize = getPropertyAsInt("poolSize", 100);
        minTokens = getPropertyAsInt("minTokens", 1);
        maxTokens = getPropertyAsInt("maxTokens", 10);
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the PermutationFilter fields must be defined!");

        previousDocs = new ArrayList<>();

        super.init();
    }

    @Override
    public void document(Document doc) {
        Document permutedDoc = permuteDocument(doc, previousDocs, fields, minTokens, maxTokens);

        super.document(permutedDoc);

        previousDocs.add(doc);
        if(previousDocs.size() > poolSize) {
            previousDocs.remove(0);
        }
    }

    protected static Document permuteDocument(Document currentDoc, List<Document> previousDocs, List<String> fields, int minTokens, int maxTokens) {
        Document result = new Document(currentDoc);
        if(previousDocs.size() > 0) {
            for (String field : fields) {
                int tokenSize = new Random().nextInt(maxTokens - minTokens)+minTokens;

                String permutedValue = permuteFieldValue(field, previousDocs, tokenSize);
                result.setField(field, permutedValue);
            }
        }

        return result;
    }

    protected static String permuteFieldValue(String field, List<Document> previousDocs, int tokenSize) {
        List<String> tokens = new ArrayList<>();

        while(tokens.size() < tokenSize) {
            int randDoc = new Random().nextInt(previousDocs.size());
            Document previousDoc = previousDocs.get(randDoc);
            String fieldValue = previousDoc.getFieldValue(field);
            String randomToken = getRandomToken(fieldValue);
            if(randomToken != null) {
                tokens.add(randomToken);
            }
        }

        String result = StringUtils.join(tokens, " ");
        return result;
    }

    protected static String getRandomToken(String text) {
        String token = null;

        if(StringUtils.isNotEmpty(text)) {
            String[] tokens = text.split("\\s+");
            int i = new Random().nextInt(tokens.length);
            token = tokens[i];
        }

        return token;
    }
}
