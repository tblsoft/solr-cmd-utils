package de.tblsoft.solr.sitemap.bean;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by tblsoft on 15.12.16.
 */
public class Url {

    private String loc;


    @XmlElement(name="loc", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }
}
