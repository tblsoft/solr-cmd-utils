package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Normalize synonym to main word
 */
public class SynonymNormalizationFilter extends AbstractFilter {
    Map<String, String> synonymLookup; // synonym : main word
    Set<String> mainWordLookup; // main word
    String fieldSynonym;
    String arrayDelimiter;
    Boolean mustExist; // remove documents where main word or synonym not exist

    @Override
    public void init() {
        fieldSynonym = getProperty("fieldSynonym", null);
        arrayDelimiter = getProperty("arrayDelimiter", ";");
        mustExist = getPropertyAsBoolean("mustExist", false);

        synonymLookup = new HashMap<String, String>();
        mainWordLookup = new HashSet<String>();
        InputStream in = null;
        try {
            String filename = getProperty("filename", null);
            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

            in = IOUtils.getInputStream(absoluteFilename);
            java.io.Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8.name());

            CSVFormat format = CSVFormat.RFC4180
                    .withDelimiter(',');

            CSVParser parser = format.parse(reader);
            Iterator<CSVRecord> csvIterator = parser.iterator();
            while(csvIterator.hasNext()) {
                CSVRecord record = csvIterator.next();
                try {
                    String mainWord = record.get(0);
                    String[] synonyms = record.get(1).split(arrayDelimiter);
                    if(synonyms != null && synonyms.length > 0) {
                        for (String synonym : synonyms) {
                            if(StringUtils.isNotEmpty(synonym)) {
                                synonymLookup.put(synonym, mainWord);
                            }
                        }
                    }
                    mainWordLookup.add(mainWord);
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }

        super.init();
    }

    @Override
    public void document(Document document) {
        boolean exist = false;
        List<String> words = document.getFieldValues(fieldSynonym);
        if(words != null && words.size() > 0) {
            for(int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                if(synonymLookup.containsKey(word)) {
                    word = synonymLookup.get(word);
                    words.set(i, word);
                    exist = true;
                }
                else if(mainWordLookup.contains(word)) {
                    exist = true;
                }
                else if(mustExist) {
                    System.out.println("SynonymNormalizationFilter: Omit non existing word -> "+word);
                }
            }
        }
        else {
            exist = true;
        }

        if(!mustExist || exist) {
            super.document(document);
        }
    }
}
