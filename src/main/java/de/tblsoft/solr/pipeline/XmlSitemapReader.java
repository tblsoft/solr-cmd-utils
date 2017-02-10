package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.sitemap.bean.Sitemap;
import de.tblsoft.solr.sitemap.bean.Sitemapindex;
import de.tblsoft.solr.sitemap.bean.Url;
import de.tblsoft.solr.sitemap.bean.UrlSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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

    @Override
    public void read() {

        sitemapBlacklits = getPropertyAsList("sitemapBlacklits", new ArrayList<String>());

        List<String> urls = getPropertyAsList("urls", null);

        try {
            for(String url: urls) {
                List<String> sitemapList = readSitemapUrlsFromRobotsTxt(url);
                for (String sitemap : sitemapList) {
                    if (sitemapBlacklits.contains(sitemap)) {
                        continue;
                    }
                    String sitemapOrSitemapIndexList = HTTPHelper.get(sitemap);
                    InputStream is = org.apache.commons.io.IOUtils.toInputStream(sitemapOrSitemapIndexList, "UTF-8");
                    UrlSet urlset = parseSitemap(is);
                    is.close();

                    // it is not a sitemap, it is a sitemap index
                    if (urlset == null) {
                        processSitemapIndex(sitemapOrSitemapIndexList, sitemap);
                    } else {
                        addDocument(urlset, null, sitemap);
                    }
                }
            }



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void addDocument(UrlSet urlSet, String sitemapIndexUrl, String sitemapUrl) {
        if(urlSet == null) {
            System.out.println("no url set is null: " + sitemapIndexUrl + " sitemap: " + sitemapUrl);
            return;
        }
        if(urlSet.getUrl() == null) {
            System.out.println("no urls found for: " + sitemapIndexUrl + " sitemap: " + sitemapUrl);
            return;
        }
        for(Url url: urlSet.getUrl()) {
            Document document = new Document();
            document.addField("sitemapIndexUrl", sitemapIndexUrl);
            document.addField("sitemapUrl", sitemapUrl);
            document.addField("loc", url.getLoc());
            //document.addField("timestamp", DateUtils.date2String(new Date()));
            executer.document(document);
        }

    }

    List<String> readSitemapUrlsFromRobotsTxt(String domain) {
        String robots = HTTPHelper.get(domain);

        List<String> sitemapList = new ArrayList<String>();
        String sitemapPattern = "Sitemap: (.*)";
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

    void processSitemapIndex(String sitemapIndexContent, String sitemapIndexUrl) throws IOException {

        InputStream is = org.apache.commons.io.IOUtils.toInputStream(sitemapIndexContent, "UTF-8");
        Sitemapindex sitemapindex = parseSitemapIndex(is);
        is.close();

        for (Sitemap sitemapFromIndex : sitemapindex.getSitemap()) {
            String loc = sitemapFromIndex.getLoc();
            String sitemapContent = HTTPHelper.get(loc);
            is = org.apache.commons.io.IOUtils.toInputStream(sitemapContent, "UTF-8");
            UrlSet urlSet = parseSitemap(is);

            addDocument(urlSet, loc, sitemapIndexUrl);
        }


    }

    UrlSet parseSitemap(InputStream is) {

        try {
            JAXBContext jc = JAXBContext.newInstance(UrlSet.class);
            UrlSet sitemap =
                    (UrlSet) jc.createUnmarshaller().unmarshal(is);
            return sitemap;
        } catch (JAXBException e) {
            return null;
        }
    }

    Sitemapindex parseSitemapIndex(InputStream is) {
        try {
            JAXBContext jc = JAXBContext.newInstance(Sitemapindex.class);
            Sitemapindex sitemapIndex =
                    (Sitemapindex) jc.createUnmarshaller().unmarshal(is);
            return sitemapIndex;
        } catch (JAXBException e) {
            return null;
        }
    }

}
