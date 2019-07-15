package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import de.tblsoft.solr.crawl.attr.AttributeExtractor;
import de.tblsoft.solr.crawl.attr.AttributeExtractorFactory;
import de.tblsoft.solr.crawl.attr.Attributes;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.net.URI;
import java.util.*;

public class JSoupAnalyzer {

    private Webpage webpage;

    private long startTime;

    private long responseTime;

    private String url;

    private Document jsoupDocument;

    public JSoupAnalyzer(String url, String html) {
        this.url = url;
        this.webpage = new Webpage();
        webpage.setRawHtml(html);
        jsoupDocument = Jsoup.parse(html);
    }

    public void analyze() {
        this.webpage = new Webpage();
        this.webpage.setUrl(url);
        webpage.setBaseUrl(extractBaseUrl());
        webpage.setTitle(getFirstElement("title"));
        webpage.setMetaDescription(getMeta("description"));
        webpage.setCanonical(getCanonical());
        webpage.setH1(getAllElements("h1"));
        webpage.setH2(getAllElements("h2"));
        webpage.setH3(getAllElements("h3"));
        webpage.setH4(getAllElements("h4"));
        webpage.setH5(getAllElements("h5"));
        webpage.setH6(getAllElements("h6"));

        webpage.setLinks(getAbsoluteLinks());
        webpage.setImages(getAbsoluteImages());
        webpage.setDomains(getDomains());
        webpage.setJsonLd(getJsonLd());

        //getItempropArticleBody();
        extractAllMeta();

        webpage.setImage(extractImage());
        webpage.setParseTime(System.currentTimeMillis() - startTime);
    }

    public String extractBaseUrl() {
        try {
            URI uri = new URI(this.url);
            return uri.getScheme() + "://" + uri.getHost();
        } catch (Exception e) {
            // ignore
        }


        return null;
    }

    public void extractAttributes(Attributes attributes) {
        AttributeExtractor attributeExtractor = AttributeExtractorFactory.
                create(attributes.getStrategy(), this);
        attributes.setAttributes(attributeExtractor.extractAttributes(attributes));
        webpage.setAttributes(attributes);

    }

    public void extractBreadcrumb(Breadcrumb breadcrumb) {
        if(breadcrumb == null) {
            return;
        }
        Elements aTags = jsoupDocument.select(breadcrumb.getSelector()).select("a");
        for(Element aTag : aTags) {
            String href = aTag.attr("href");
            String name = aTag.text();
            BreadcrumbEntry entry = new BreadcrumbEntry();
            entry.setId(href);
            entry.setLink(href);
            entry.setName(name);
            breadcrumb.addBreadcrumbEntry(entry);
        }

        webpage.setBreadcrumb(breadcrumb);

    }

    public String extractImage() {
        if(webpage.getMeta() != null &&  webpage.getMeta().getProperty() != null) {
            String ogImage =  webpage.getMeta().getProperty().get("og:image");
            return getAbsoluteUrl(ogImage);
        }

        return null;
    }

    public void extractCustom(List<Custom> customs) {
        if(webpage.getCustom() == null) {
            webpage.setCustom(new ArrayList<>());
        }
        for(Custom custom : customs) {
            if("html".equals(custom.getType())) {
                StringBuilder builder = new StringBuilder();
                jsoupDocument.select(custom.getJsoupSelector()).forEach(builder::append);
                custom.setValue(builder.toString());
            }  if("attr".equals(custom.getType())) {
                String value = jsoupDocument.select(custom.getJsoupSelector()).attr(custom.getAttributeName());
                custom.setValue(value);
            } else {
                String value = jsoupDocument.select(custom.getJsoupSelector()).text();
                custom.setValue(value);
            }
            webpage.getCustom().add(custom);
        }
    }

    public Webpage getWebpage() {
        if(webpage == null) {
            analyze();
        }
        return webpage;
    }

    public Collection<String> getDomains() {
        Set<String> domains = new HashSet<>();
        List<String> urls = new ArrayList<>();
        urls.addAll(this.webpage.getLinks());
        urls.addAll(this.webpage.getImages());


        for(String url : urls) {
            try {
                URI uri = new URI(url);
                String host = uri.getHost();
                if(host != null) {
                    domains.add(host);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return domains;
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

    public Collection<String> getAbsoluteImages() {
        Set<String> absoluteUrls = new HashSet<String>();
        Elements link = jsoupDocument.select("img");
        for (int i = 0; i < link.size() ; i++) {
            String absUrl = link.get(i).absUrl("src");
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
                objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
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

    public String getFirstElementAttr(Elements element, String attr) {
        if (element.size() > 0) {
            return element.attr(attr);
        }
        return null;
    }

    public List<String> getAllElements(String selector) {
        List<String> allElements = new ArrayList<>();
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
            allElements.add(value.toString().trim());
        }
        return allElements;
    }

    public String getFirstElement(String selector) {
        Elements elements = jsoupDocument.select(selector);
        if (elements.size() > 0) {
            String value = elements.get(0).text();
            return value;
        }
        return null;
    }

    public String getMeta(String metaName) {
        Elements meta = jsoupDocument.select("meta[name=" + metaName + "]");
        return getFirstElementAttr(meta, "content");
    }

    public String getItempropArticleBody() {
        Elements articleBody = jsoupDocument.select("[itemprop=articleBody]");
        String text = articleBody.text();
        return text;

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



            if(!Strings.isNullOrEmpty(name)) {
                webpage.getMeta().addName(name, content);
            }

            if(!Strings.isNullOrEmpty(property)) {
                webpage.getMeta().addProperty(property, content);
            }


            if(!Strings.isNullOrEmpty(itemprop)) {
                webpage.getMeta().addProperty(itemprop, content);
            }



        }
    }



    private String getAbsoluteUrl(String url) {
        if(url == null) {
            return null;
        }
        if(url.startsWith("http")) {
            return url;
        }
        if(url.startsWith("/")) {
            return webpage.getBaseUrl() + url;
        }
        return url;
    }

    public void setWebpage(Webpage webpage) {
        this.webpage = webpage;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Document getJsoupDocument() {
        return jsoupDocument;
    }

    public void setJsoupDocument(Document jsoupDocument) {
        this.jsoupDocument = jsoupDocument;
    }

}
