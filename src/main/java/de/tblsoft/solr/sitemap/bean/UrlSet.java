package de.tblsoft.solr.sitemap.bean;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by tblsoft on 15.12.16.
 */
@XmlRootElement(name="urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class UrlSet {

    private List<Url> url;

    @XmlElement(name="url", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    public List<Url> getUrl() {
        return url;
    }

    public void setUrl(List<Url> url) {
        this.url = url;
    }
}
