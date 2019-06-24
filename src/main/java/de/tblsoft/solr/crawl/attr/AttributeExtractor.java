package de.tblsoft.solr.crawl.attr;

import java.util.List;

public interface AttributeExtractor {

    List<Attribute> extractAttributes(Attributes attributes);
}
