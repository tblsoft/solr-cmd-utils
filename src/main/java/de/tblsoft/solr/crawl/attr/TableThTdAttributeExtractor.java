package de.tblsoft.solr.crawl.attr;

import de.tblsoft.solr.crawl.JSoupAnalyzer;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class TableThTdAttributeExtractor implements AttributeExtractor {

    private JSoupAnalyzer jSoupAnalyzer;

    public TableThTdAttributeExtractor(JSoupAnalyzer jSoupAnalyzer) {
        this.jSoupAnalyzer = jSoupAnalyzer;
    }

    @Override
    public List<Attribute> extractAttributes(Attributes attributes) {
        List<Attribute> attributeNames = new ArrayList<>();
        for(String selector : attributes.getSelector()) {
            for (Element element : jSoupAnalyzer.getJsoupDocument().select(selector).select("tr")) {
                String th = element.select("th").text();
                String td = element.select("td").text();

                Attribute attribute = new Attribute();
                attribute.setName(th);
                attribute.setValue(td);
                attributeNames.add(attribute);
            }
        }
        return attributeNames;
    }
}
