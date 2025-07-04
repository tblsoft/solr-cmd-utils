package de.tblsoft.solr.sitemap.bean;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 15.12.16.
 */
@XmlRootElement(name="sitemapindex", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
public class Sitemapindex {


    private List<Sitemap> sitemap = new ArrayList<Sitemap>();

    @XmlElement(name = "sitemap", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    public List<Sitemap> getSitemap() {
        return sitemap;
    }

    public void setSitemap(List<Sitemap> sitemap) {
        this.sitemap = sitemap;
    }
}
