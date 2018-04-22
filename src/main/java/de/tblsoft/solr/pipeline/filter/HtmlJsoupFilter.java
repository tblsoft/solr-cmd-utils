package de.tblsoft.solr.pipeline.filter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.repackaged.com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Pattern;

public class HtmlJsoupFilter extends AbstractFilter {

    protected org.jsoup.nodes.Document jsoupDocument;
    protected String html;

    protected Document document;

    protected String metaPrefix = "__meta_";

    protected String htmlField;

    private boolean deleteHtmlField;


	@Override
	public void init() {

        htmlField = getProperty("htmlField", "html");
        deleteHtmlField = getPropertyAsBoolean("deleteHtmlField", false);

        metaPrefix = getProperty("metaPrefix", "__meta_");

		super.init();
	}



	@Override
	public void document(Document document) {
        this.document = document;
        html = document.getFieldValue(htmlField);
        jsoupDocument = Jsoup.parse(html);

        document.addField("canonical", getCanonical());
        mapFirstElement("title", "title");
        mapMeta("description", "description");
        mapAllElements("h1", "h1");
        mapAllElements("h2", "h2");
        mapAllElements("h3", "h3");
        mapAllElements("h4", "h4");

        document.setField("links", getAbsoluteLinks());
        document.setField("jsonld", getJsonLd());
        mapItempropArticleBody();
        extractAllMeta();
        if(deleteHtmlField) {
            document.deleteField(htmlField);
        }
		super.document(document);
	}


    public Collection<String> getAbsoluteLinks() {
	    Set<String> absoluteUrls = new HashSet<String>();
        Elements link = jsoupDocument.select("a");
        for (int i = 0; i < link.size() ; i++) {
            String absUrl = link.get(i).absUrl("href");
            absoluteUrls.add(absUrl);
        }
        return absoluteUrls;
    }

    public Collection<String> getJsonLd() {
	    List jsonLdList = new ArrayList();
        Elements jsonLdScripts = jsoupDocument.select("script[type=application/ld+json]");
        for (int i = 0; i < jsonLdScripts.size() ; i++) {
            try {
                String jsonLd = jsonLdScripts.get(i).data();
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
                JsonNode json = objectMapper.readTree(jsonLd);
                jsonLdList.add(objectMapper.writeValueAsString(json));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return jsonLdList;
    }


    public String getCanonical() {
        Elements meta = jsoupDocument.select("link[rel=canonical]");
        if (meta.size() > 0) {
            return meta.get(0).attr("href");
        }
        return "";
    }

    public void extractAllMeta() {

        Elements meta = jsoupDocument.select("meta");
        Iterator<Element> metaIt = meta.iterator();
        while (metaIt.hasNext()) {
            Element element = metaIt.next();
            String name = element.attr("name");
            String property = element.attr("property");
            String itemprop = element.attr("itemprop");

            String content = element.attr("content");

            if(StringUtils.isNotEmpty(name)) {
                document.addField("metanames", name);
                document.addField("__meta_" + name, content);
            }

            if(StringUtils.isNotEmpty(property)) {
                document.addField("propertynames", name);
                property = property.replaceAll(Pattern.quote(":"), "_");
                document.addField("__property_" + property, content);
            }


            if(StringUtils.isNotEmpty(itemprop)) {
                document.addField("itempropnames", name);
                document.addField("__itemprop_" + itemprop, content);
            }


        }
    }

    public void mapFirstElementAttr(Elements element, String attr, String fieldName) {
        if (element.size() > 0) {
            document.addField(fieldName, element.attr(attr));
        }
    }

    public void mapAllElements(String selector, String fieldName) {
        Elements elements = jsoupDocument.select(selector);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);


            StringBuilder value = new StringBuilder();
            for(Element subElements : element.getAllElements()) {
                for (TextNode textNode : subElements.textNodes()) {
                    final String text = textNode.text();
                    value.append(text);
                    value.append(" ");
                }
            }
            document.addField(fieldName, value.toString().trim());
        }
    }

    public String getFirstElement(String selector) {
        Elements elements = jsoupDocument.select(selector);
        if (elements.size() > 0) {
            String value = elements.get(0).text();
            return value;
        }
        return null;
    }

    public void mapFirstElement(String selector, String fieldName) {
        String value = getFirstElement(selector);
        if(!Strings.isNullOrEmpty(value)) {
            document.addField(fieldName, value);

        }

    }

    public void mapMeta(String metaName, String fieldName) {
        Elements meta = jsoupDocument.select("meta[name=" + metaName + "]");
        mapFirstElementAttr(meta, "content", fieldName);
    }

    public void mapItempropArticleBody() {
        Elements articleBody = jsoupDocument.select("[itemprop=articleBody]");
        String text = articleBody.text();

        document.addField("articleBody", text);
    }



}
