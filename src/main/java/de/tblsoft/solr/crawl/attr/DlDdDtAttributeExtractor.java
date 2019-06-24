package de.tblsoft.solr.crawl.attr;

import de.tblsoft.solr.crawl.JSoupAnalyzer;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class DlDdDtAttributeExtractor implements AttributeExtractor {

    private JSoupAnalyzer  jSoupAnalyzer;

    public DlDdDtAttributeExtractor(JSoupAnalyzer jSoupAnalyzer) {
        this.jSoupAnalyzer = jSoupAnalyzer;
    }

    @Override
    public List<Attribute> extractAttributes(Attributes attributes) {
        List<Attribute> attributeNames = new ArrayList<>();
        for(String selector : attributes.getSelector()) {

            for (Element element : jSoupAnalyzer.getJsoupDocument().select(selector).select("dl")) {
                String dt = element.select("dt").text();
                String dd = element.select("dd").text();

                Attribute attribute = new Attribute();
                attribute.setName(dt);
                attribute.setValue(dd);
                attributeNames.add(attribute);
            }

        }
        return attributeNames;
    }
}
