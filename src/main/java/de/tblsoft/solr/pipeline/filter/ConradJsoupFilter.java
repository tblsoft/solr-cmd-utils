package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ConradJsoupFilter extends AbstractFilter {

    protected org.jsoup.nodes.Document jsoupDocument;

    protected String htmlField;

    protected String html;


	@Override
	public void init() {
        htmlField = getProperty("htmlField", "html");
		super.init();

	}



	@Override
	public void document(Document document) {
        html = document.getFieldValue(htmlField);
        jsoupDocument = Jsoup.parse(html);



        Elements technicaldetails = jsoupDocument.select("section[id=technicaldetail] > div > dl");
        for (int i = 0; i < technicaldetails.size(); i++) {
            Element detail = technicaldetails.get(i);
            String name = detail.select("dt > div > a").text();
            if(StringUtils.isEmpty(name)) {
                name = detail.select("dt").text();
            }
            String value = detail.select("dd").text();
            String key = normalizeKey(name);
            document.setField("attributekey_"+ key,value);
            document.setField("attributename_"+ key,name);
        }


        Elements breadcrumb = jsoupDocument.select("ul[class=ccpBreadcrumb] > li");
        for (int i = 1; i < breadcrumb.size(); i++) {
            Element categoryElement = breadcrumb.get(i);
            String category = categoryElement.text();

            document.addField("category",category);
        }

        Elements brandHtml = jsoupDocument.select("[itemprop=brand]");
        String brand = brandHtml.text();
        if(StringUtils.isEmpty(brand)) {
            brand = brandHtml.select("img").attr("alt");
        }

        document.setField("brand", brand);
        super.document(document);
	}

	String normalizeKey(String key) {
	    key = key.replaceAll(" ", "_");
	    key = key.replaceAll("[^a-zA-Z0-9_-]+","");
        return key;
    }





}
