package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Gender Filter generates separate male/female tokens from a shorthand token like Chirurg/-in -> [Chirurg, Chirurgin]
 * Where the first token is the male and the second the female token.
 * The female token is always created from the male and the female token.
 * Tested with German language other languages can work.
 */
public class SimpleGenderFilter extends AbstractFilter {
    private String fieldName;

    private List<String> replaceableWords; // Words with should be replaced in the male token with the female token.
    private List<String> trimEndings; // specify endings with should be trimmed in first male token
    private String separator; // male/ female token separator

    @Override
    public void init() {
        setFieldName(getProperty("fieldName", null));
        verify(getFieldName(), "For the SimpleGenderFilter a fieldName property must be defined.");

        setReplaceableWords(getPropertyAsList("replaceableWords", new ArrayList<String>()));
        setTrimEndings(getPropertyAsList("trimEndings", Arrays.asList("e")));
        setSeparator(getProperty("separator", "/-"));

        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> values = document.getFieldValues(getFieldName(), new ArrayList<String>());

        if(values != null) {
            List<String> filteredValues = new ArrayList<String>();
            for (String value : values) {
                List<String> filteredGenderValues = filterGender(value);
                filteredValues.addAll(filteredGenderValues);
            }
            document.setField(getFieldName(), filteredValues);
        }

        super.document(document);
    }

    protected List<String> filterGender(String value) {
        List<String> values = null;
        if(value != null) {
            values = new ArrayList<String>();
            if (value.contains("/-")) {
                String[] tokens = value.split("/-");
                if(tokens.length == 2) {
                    String maleToken = tokens[0];
                    String femaleToken = tokens[1];

                    values.add(maleToken);

                    String femaleWord = buildFemaleWord(maleToken, femaleToken);
                    values.add(femaleWord);
                }
            } else {
                values.add(value);
            }
        }

        return values;
    }

    protected String buildFemaleWord(String maleWord, String femaleToken) {
        String result = null;

        boolean wordReplaced = false;
        for (String replaceableWord : getReplaceableWords()) {
            if(StringUtils.containsIgnoreCase(maleWord, replaceableWord)) {
                result = maleWord.replaceAll("(?i)"+replaceableWord, femaleToken);
                wordReplaced = true;
                break;
            }
        }

        if(!wordReplaced) {
            for (String ending : getTrimEndings()) {
                if(maleWord.endsWith(ending)) {
                    maleWord = maleWord.substring(0, maleWord.length()-ending.length());
                }
            }

            result = maleWord + femaleToken;
        }

        return result;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getReplaceableWords() {
        return replaceableWords;
    }

    public void setReplaceableWords(List<String> replaceableWords) {
        this.replaceableWords = replaceableWords;
    }

    public List<String> getTrimEndings() {
        return trimEndings;
    }

    public void setTrimEndings(List<String> trimEndings) {
        this.trimEndings = trimEndings;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }
}
