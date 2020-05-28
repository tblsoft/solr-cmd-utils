package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.nlp.DictionaryPhraseMatcher;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;

import java.util.List;

/**
 * Created by tblsoft on 25.4.20.
 *
 */
public class PosTagDictReplaceFilter extends AbstractFilter {

    private DictionaryPhraseMatcher dictionaryPhraseMatcher;

    @Override
    public void init() {
        List<String> filenames = getPropertyAsList("filenames", null);
        dictionaryPhraseMatcher = new DictionaryPhraseMatcher();


        try {
            for (String filename : filenames) {
                String posTag = filename.replaceAll(".*_([A-Z]+).*", "$1");

                List<String> lines = IOUtils.file2List(getBaseDir() + "/" + filename);
                for (String line : lines) {
                    if(posTag == null) {
                        dictionaryPhraseMatcher.addPhrase(line);
                    } else {
                        StringBuilder sb = new StringBuilder();
                        String[] splitted = line.split(" ");
                        String firstToken = null;
                        for (String splittedEntry : splitted) {
                            String normalized = normalize(splittedEntry);
                            if (firstToken == null) {
                                firstToken = normalized;
                            }
                            sb.append(normalized);
                            sb.append("_");
                            sb.append(posTag);
                            sb.append(" ");
                        }
                        dictionaryPhraseMatcher.addPhrase(firstToken, sb.toString().trim());
                    }
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        /*

        dictionaryPhraseMatcher.addPhrase("apple", "apple_BRAND");
        dictionaryPhraseMatcher.addPhrase("iphone", "iphone_MODEL");
        dictionaryPhraseMatcher.addPhrase("samsung", "samsung_BRAND");
        dictionaryPhraseMatcher.addPhrase("galaxy", "galaxy_MODEL");
        dictionaryPhraseMatcher.addPhrase("magentaeins", "magentaeins_TARIF");
        dictionaryPhraseMatcher.addPhrase("magentamobil", "magentamobil_TARIF");
        dictionaryPhraseMatcher.addPhrase("magentatv", "magentatv_TARIF");
        dictionaryPhraseMatcher.addPhrase("magentazuhause", "magentazuhause_TARIF");
        dictionaryPhraseMatcher.addPhrase("meinmagenta", "meinmagenta_TARIF");
        dictionaryPhraseMatcher.addPhrase("magentasport", "magentasport_TARIF");
        dictionaryPhraseMatcher.addPhrase("magenta", "magenta_TARIF eins_TARIF");
        dictionaryPhraseMatcher.addPhrase("magenta", "magenta_TARIF mobil_TARIF");
        dictionaryPhraseMatcher.addPhrase("magenta", "magenta_TARIF tv_TARIF");
        dictionaryPhraseMatcher.addPhrase("magenta", "magenta_TARIF zuhause_TARIF");
        dictionaryPhraseMatcher.addPhrase("magenta", "magenta_TARIF sport_TARIF");
        dictionaryPhraseMatcher.addPhrase("farbe", "farbe_ATTR");
        dictionaryPhraseMatcher.addPhrase("rot", "rot_ATTRV");
        dictionaryPhraseMatcher.addPhrase("grün", "grün_ATTRV");
        dictionaryPhraseMatcher.addPhrase("blau", "blau_ATTRV");
        dictionaryPhraseMatcher.addPhrase("sar", "sar_ATTR wert_ATTR");
        dictionaryPhraseMatcher.addPhrase("speicher", "speicher_ATTR");
        dictionaryPhraseMatcher.addPhrase("größe", "größe_ATTR");
         */

        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> tokens = document.getFieldValues("tokens");
        List<String> posTags = document.getFieldValues("posTags");
        List<String> newPosTags  = dictionaryPhraseMatcher.match(tokens, posTags);
        document.setField("posTags", newPosTags);
        super.document(document);
    }

    private String normalize(String value) {
        if(value == null) {
            return value;
        }
        return value.toLowerCase();
    }

    
}
