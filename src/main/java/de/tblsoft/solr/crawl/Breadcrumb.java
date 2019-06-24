package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Breadcrumb {

    private String selector;

    private List<BreadcrumbEntry> breadcrumbs;

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public List<BreadcrumbEntry> getBreadcrumbs() {
        return breadcrumbs;
    }

    public void setBreadcrumbs(List<BreadcrumbEntry> breadcrumbs) {
        this.breadcrumbs = breadcrumbs;
    }

    public void addBreadcrumbEntry(BreadcrumbEntry breadcrumbEntry) {
        if(this.breadcrumbs == null) {
            this.breadcrumbs = new ArrayList<>();
        }
        this.breadcrumbs.add(breadcrumbEntry);
    }
}
