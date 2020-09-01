package de.tblsoft.solr.crawl.attr;

import de.tblsoft.solr.crawl.JSoupAnalyzer;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The attributes are organized as a list of paragraphs.
 * The key and value are splitted by colon.
 *
 * Example:
 * <p><strong>Decksohle:</strong><br>Leder</p>
 * <p><strong>Form:</strong><br>Sandale/Pantolette</p>
 * <p><strong>Futter:</strong><br>Leder</p>
 * <p><strong>Farbe:</strong><br>Grau</p>
 */
public class ParagraphAttributeExtractor implements AttributeExtractor {

    private JSoupAnalyzer jSoupAnalyzer;

    public ParagraphAttributeExtractor(JSoupAnalyzer jSoupAnalyzer) {
        this.jSoupAnalyzer = jSoupAnalyzer;
    }

    @Override
    public List<Attribute> extractAttributes(Attributes attributes) {
        List<Attribute> attributeNames = new ArrayList<>();
        for(String selector : attributes.getSelector()) {
            for (Element element : jSoupAnalyzer.getJsoupDocument().select(selector)) {
                String text = element.text();
                String[] splitted = text.split(Pattern.quote(":"));

                Attribute attribute = new Attribute();
                attribute.setName(splitted[0].trim());
                attribute.setValue(splitted[1].trim());
                attributeNames.add(attribute);
            }
        }
        return attributeNames;
    }
}
