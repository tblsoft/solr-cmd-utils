package de.tblsoft.solr.crawl.attr;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attributes {

    private String strategy;
    private List<String> selector;
    private List<Attribute> attributes;

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }

    public List<String> getSelector() {
        return selector;
    }

    public void setSelector(List<String> selector) {
        this.selector = selector;
    }

    public void addSelector(String selector) {
        if(this.selector == null) {
            this.selector = new ArrayList<>();
        }
        this.selector.add(selector);
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }


}
