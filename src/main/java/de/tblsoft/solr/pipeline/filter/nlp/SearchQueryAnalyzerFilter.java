package de.tblsoft.solr.pipeline.filter.nlp;


import com.quasiris.qsf.commons.text.TextUtils;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DatatypeUtils;

/**
 * Analyze search query an append meta information
 *
 */
public class SearchQueryAnalyzerFilter extends AbstractFilter {



    private String fieldName;


    @Override
    public void init() {
        fieldName = getProperty("fieldName", "keyword");
        verify(fieldName, "For the AddStaticValueFilter a fieldName must be defined.");
        super.init();
    }


    @Override
    public void document(Document document) {

        String value = document.getFieldValue(fieldName);
        analyzeSearchQuery(value, document);
        super.document(document);
    }

    private void analyzeSearchQuery(String searchQuery, Document document) {
        if(searchQuery == null) {
            return;
        }
        document.setField("charCount" , searchQuery.length());

        int tokenCount = searchQuery.split(" ").length;
        document.setField("tokenCount" , tokenCount);

        String countAsString = document.getFieldValue("count");
        if(countAsString != null) {
            int count = Integer.valueOf(countAsString);
            int normalizedCount = tokenCount * count;
            document.setField("normalizedCount" , normalizedCount);
        }



        tagToken(searchQuery, "query", document);
        for(String token : searchQuery.split(" ")) {
            tagToken(token, "token", document);
        }




    }

    private void tagToken(String token, String prefix, Document document) {

        if(TextUtils.containsNumber(token)) {
            document.addField("tag", prefix + "containsNumber");
        }

        if(DatatypeUtils.isBoolean(token)) {
            document.addField("tag", prefix + "boolean");
        }

        if(DatatypeUtils.isNumber(token)) {
            document.addField("tag", prefix + "number");
            document.addField("tagdetail", prefix + "number" + token.length());
        }
        if(DatatypeUtils.isInteger(token)) {
            document.addField("tag", prefix + "integer");
            document.addField("tagdetail", prefix + "integer" + token.length());
        }
        if(DatatypeUtils.isLong(token)) {
            document.addField("tag", prefix + "long");
            document.addField("tagdetail", prefix + "long" + token.length());
        }

        if(isWord(token)) {
            document.addField("tag", prefix + "word");
        }

    }

    private boolean isWord(String value) {
        for(char c : value.toCharArray()) {
            if(!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }



}

