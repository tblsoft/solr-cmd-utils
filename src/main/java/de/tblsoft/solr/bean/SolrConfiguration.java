package de.tblsoft.solr.bean;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Created by tblsoft
 *
 * A bean class for the solr.xml file
 */
@XmlRootElement(name="solr")
public class SolrConfiguration {


    private Boolean persistent;

    private Cores cores;


    @XmlElement
    public Cores getCores() {
        return cores;
    }

    public void setCores(Cores cores) {
        this.cores = cores;
    }

    @XmlAttribute
    public Boolean getPersistent() {
        return persistent;
    }

    public void setPersistent(Boolean persistent) {
        this.persistent = persistent;
    }

    @Override
    public String toString() {
        return "Solr{" +
                "persistent=" + persistent +
                ", cores=" + cores +
                '}';
    }
}
