package de.tblsoft.solr.logic;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.io.Files;
import de.tblsoft.solr.parser.SolrXmlParser;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

/**
 * Unit test for simple App.
 */
public class SpecialCharacterExtractor extends SolrXmlParser {

    private Map<String, String> dictionary = new HashMap<String, String>();

    private Set<String> currentChars = new HashSet<String>();

    private String currentArticleCode;

    private String outputFileName;

    private String idField = "articleCode_exact";

    private String allowedChars = "[a-zA-Z0-9-]+";

    private int maxItems = 50;

    public void extractSpecialCharacters() throws Exception {
        parse();
        OutputStream out = IOUtils.getOutputStream(outputFileName);
        OutputStreamStringBuilder dict = new OutputStreamStringBuilder(out);
        for(Entry<String, String> entry: dictionary.entrySet()) {
            dict.append(entry.getKey());
            dict.append(" : ");
            if(entry.getValue().length() > maxItems) {
                dict.append(entry.getValue().substring(0, maxItems));
            } else {
                dict.append(entry.getValue());
            }
            dict.append("\n");
        }

        out.close();
        //Files.write(dict, new File("special-char-dict.txt"),Charset.forName("UTF-8"));

    }


    @Override
    public void field(String name, String value) {
        if(idField.equals(name)) {
            currentArticleCode = value;
        }

        value = value.replaceAll(allowedChars, "");

        for (int i = 0; i < value.length(); i++) {
            String valueChar = String.valueOf(value.charAt(i));
            currentChars.add(valueChar);

        }
    }

    @Override
    public void endDocument() {
        for(String value: currentChars) {
            String articleCodes = dictionary.get(value);
            if(articleCodes==null) {
                articleCodes = currentArticleCode;
            } else {
                articleCodes = articleCodes + "," + currentArticleCode;
            }
            dictionary.put(value, articleCodes);
        }
        currentChars = new HashSet<String>();
        this.currentArticleCode = null;
        super.endDocument();
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void setAllowedChars(String allowedChars) {
        this.allowedChars = allowedChars;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }
}