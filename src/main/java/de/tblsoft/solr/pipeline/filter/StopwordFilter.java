package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Remove stopword values
 */
public class StopwordFilter extends AbstractFilter {
    private List<String> fields;
    private String replacement;

    private Pattern regex;
    private List<String> stopwords;

    @Override
    public void init() {
        fields = getPropertyAsList("fields", null);
        verify(fields, "For the RegexSplitFilter a fields property must be defined!");
        replacement = getProperty("replacement", " ");

        String filepath = getProperty("filepath", null);
        verify(filepath, "For the StopwordFilter a filepath property must be defined!");

        try {
            stopwords = Files.readAllLines(new File(filepath).toPath(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Could not read stopword file!", e);
        }

        // compile regex
        StringBuilder regexBuilder = new StringBuilder("\\b(");
        for(int i = 0; i < stopwords.size(); i++) {
            String stopword = stopwords.get(i);
            regexBuilder.append(stopword);
            if(i+1 < stopwords.size()) {
                regexBuilder.append("|");
            }
        }
        regexBuilder.append(")\\b");
        regex = Pattern.compile(regexBuilder.toString());

        super.init();
    }

    @Override
    public void document(Document document) {
        for (String field : fields) {
            List<String> fieldValues = document.getFieldValues(field);
            if (fieldValues != null) {
                for(int i = 0; i < fieldValues.size(); i++) {
                    String value = fieldValues.get(i);
                    value = value.replaceAll(regex.pattern(), replacement);
                    fieldValues.set(i, value);
                }
            }
        }

        super.document(document);
    }
}
