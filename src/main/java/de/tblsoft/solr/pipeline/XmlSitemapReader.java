package de.tblsoft.solr.pipeline;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 11.02.16.
 */
public class XmlSitemapReader extends AbstractReader {


    private List<String> sitemapBlacklits;

    public void read() {

        sitemapBlacklits = getPropertyAsList("sitemapBlacklits", new ArrayList<String>());

        List<String> urls = getPropertyAsList("urls", new ArrayList<String>());
        List<String> sitemapIndexUrls = getPropertyAsList("sitemapIndexUrls", new ArrayList<String>());
        List<String> sitemapUrls = getPropertyAsList("sitemapUrls", new ArrayList<String>());

        try {

            for(String sitemapUrl: sitemapUrls) {
                org.w3c.dom.Document doc = getDomByUrl(sitemapUrl);
                parseSitemap(doc, null);
            }

            for(String url: sitemapIndexUrls) {
                if (sitemapBlacklits.contains(url)) {
                    continue;
                }
                org.w3c.dom.Document doc = getDomByUrl(url);
                processSitemapIndex(doc, url);
            }

            for(String url: urls) {
                List<String> sitemapList = readSitemapUrlsFromRobotsTxt(url);
                for (String sitemap : sitemapList) {
                    if (sitemapBlacklits.contains(sitemap)) {
                        continue;
                    }
                    org.w3c.dom.Document doc = getDomByUrl(sitemap);
                    parseSitemap(doc, null);
                    processSitemapIndex(doc, sitemap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private org.w3c.dom.Document getDomByUrl(String url) throws IOException, ParserConfigurationException, SAXException {
        String sitemapContent = HTTPHelper.get(url);
        InputStream is = org.apache.commons.io.IOUtils.toInputStream(sitemapContent, "UTF-8");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        org.w3c.dom.Document doc = dBuilder.parse(is);
        return doc;
    }

    List<String> readSitemapUrlsFromRobotsTxt(String domain) {
        String robots = HTTPHelper.get(domain);

        List<String> sitemapList = new ArrayList<String>();
        String sitemapPattern = "(?i)Sitemap: (.*)";
        Scanner scanner = new Scanner(robots);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (Pattern.matches(sitemapPattern, line)) {
                String sitemapUrl = line.replaceAll(sitemapPattern, "$1");
                sitemapList.add(sitemapUrl);
            }
        }
        scanner.close();
        return sitemapList;
    }

    void processSitemapIndex(org.w3c.dom.Document doc, String sitemapIndexUrl) throws Exception {
        List<String> sitemapUrls = new ArrayList<String>();
        try {
            NodeList locNodes = doc.getElementsByTagName("loc");
            for (int i = 0; i < locNodes.getLength(); i++) {
                String loc = locNodes.item(i).getFirstChild().getNodeValue();
                sitemapUrls.add(loc);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        for (String loc: sitemapUrls) {
            org.w3c.dom.Document sitemapDoc = getDomByUrl(loc);
            parseSitemap(sitemapDoc, sitemapIndexUrl);
        }


    }

    void parseSitemap(org.w3c.dom.Document doc, String sitemapIndexUrl) {

        try {
            NodeList urls = doc.getElementsByTagName("url");
            for (int i = 0; i < urls.getLength(); i++) {
                Node node = urls.item(i);
                parseSitemapUrlNode(node.getChildNodes(), sitemapIndexUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void parseSitemapUrlNode(NodeList url, String sitemapIndexUrl) {
        Document document = new Document();
        if(!Strings.isNullOrEmpty(sitemapIndexUrl)) {
            document.addField("sitemapIndexUrl", sitemapIndexUrl);
        }
        for (int k = 0; k < url.getLength(); k++) {
            Node noder = url.item(k);
            if(1 == noder.getNodeType()) {
                String name = noder.getNodeName();
                String value = noder.getFirstChild().getNodeValue();
                document.addField(name, value);
            }

        }
        executer.document(document);
    }

}
