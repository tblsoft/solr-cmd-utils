package de.tblsoft.solr.crawl.attr;

import de.tblsoft.solr.crawl.JSoupAnalyzer;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TableTdTdAttributeExtractor implements AttributeExtractor {

    private JSoupAnalyzer jSoupAnalyzer;

    public TableTdTdAttributeExtractor(JSoupAnalyzer jSoupAnalyzer) {
        this.jSoupAnalyzer = jSoupAnalyzer;
    }

    @Override
    public List<Attribute> extractAttributes(Attributes attributes) {
        List<Attribute> attributeNames = new ArrayList<>();
        if(attributes == null || attributes.getSelector() == null) {
            return null;
        }
        for(String selector : attributes.getSelector()) {
            for (Element element : jSoupAnalyzer.getJsoupDocument().select(selector).select("tr")) {
                Elements td = element.select("td");
                if(td.size() != 2) {
                    continue;
                }
                String name = element.select("td").get(0).text();
                String value = element.select("td").get(1).text();

                Attribute attribute = new Attribute();
                attribute.setName(name);
                attribute.setValue(value);
                attributeNames.add(attribute);
            }
        }
        return attributeNames;
    }
}
