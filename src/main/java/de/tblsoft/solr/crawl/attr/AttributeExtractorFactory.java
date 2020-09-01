package de.tblsoft.solr.crawl.attr;

import de.tblsoft.solr.crawl.JSoupAnalyzer;

public class AttributeExtractorFactory {

    public static AttributeExtractor create(String type, JSoupAnalyzer jSoupAnalyzer) {
        if("tableTdTd".equals(type)) {
            return new TableTdTdAttributeExtractor(jSoupAnalyzer);
        } else if("dldtdd".equals(type)) {
            return new DlDdDtAttributeExtractor(jSoupAnalyzer);
        } else if("tableThTd".equals(type)) {
            return new TableThTdAttributeExtractor(jSoupAnalyzer);
        }
        else if("paragraph".equals(type)) {
            return new ParagraphAttributeExtractor(jSoupAnalyzer);
        }

        return new TableTdTdAttributeExtractor(jSoupAnalyzer);
    }
}
