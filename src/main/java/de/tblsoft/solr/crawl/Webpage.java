package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.crawl.attr.Attributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Webpage {


    private long parseTime;

    private String rawHtml;
    private String title;
    private String metaDescription;
    private String canonical;
    private String image;
    private String baseUrl;

    private List<String> h1;
    private List<String> h2;
    private List<String> h3;
    private List<String> h4;
    private List<String> h5;
    private List<String> h6;

    private Collection<String> links;
    private Collection<String> domains;
    private Collection<String> images;

    private Collection<JsonNode> jsonLd;

    private Meta meta;

    private List<Custom> custom;

    private Attributes attributes;

    private Breadcrumb breadcrumb;


    public String getRawHtml() {
        return rawHtml;
    }

    public void setRawHtml(String rawHtml) {
        this.rawHtml = rawHtml;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getCanonical() {
        return canonical;
    }

    public void setCanonical(String canonical) {
        this.canonical = canonical;
    }

    public List<String> getH1() {
        return h1;
    }

    public void setH1(List<String> h1) {
        this.h1 = h1;
    }

    public List<String> getH2() {
        return h2;
    }

    public void setH2(List<String> h2) {
        this.h2 = h2;
    }

    public List<String> getH3() {
        return h3;
    }

    public void setH3(List<String> h3) {
        this.h3 = h3;
    }

    public List<String> getH4() {
        return h4;
    }

    public void setH4(List<String> h4) {
        this.h4 = h4;
    }

    public List<String> getH5() {
        return h5;
    }

    public void setH5(List<String> h5) {
        this.h5 = h5;
    }

    public List<String> getH6() {
        return h6;
    }

    public void setH6(List<String> h6) {
        this.h6 = h6;
    }

    public Collection<String> getLinks() {
        return links;
    }

    public void setLinks(Collection<String> links) {
        this.links = links;
    }

    public Collection<String> getImages() {
        return images;
    }

    public void setImages(Collection<String> images) {
        this.images = images;
    }

    public Collection<JsonNode> getJsonLd() {
        return jsonLd;
    }

    public void setJsonLd(Collection<String> jsonLd) {
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> tempList = new ArrayList<>();
        for(String json : jsonLd) {

            try {
                JsonNode actualObj = mapper.readTree(json);
                tempList.add(actualObj);
            } catch (IOException e) {
                // ignore errors
            }

        }
        this.jsonLd = tempList;
    }

    public Meta getMeta() {
        if(this.meta == null) {
            this.meta = new Meta();
        }
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Custom> getCustom() {
        return custom;
    }

    public void setCustom(List<Custom> custom) {
        this.custom = custom;
    }

    public long getParseTime() {
        return parseTime;
    }

    public void setParseTime(long parseTime) {
        this.parseTime = parseTime;
    }

    public Collection<String> getDomains() {
        return domains;
    }

    public void setDomains(Collection<String> domains) {
        this.domains = domains;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public boolean hasAttributes() {
        return attributes != null;
    }

    public Breadcrumb getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(Breadcrumb breadcrumb) {
        this.breadcrumb = breadcrumb;
    }
}
