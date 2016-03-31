package de.tblsoft.solr.log.parser;

import de.tblsoft.solr.util.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrQueryLogParser {


    public SolrQueryLogParser(String file) {
        this.file = file;
    }

    private Pattern pattern = Pattern
            .compile(".* \\[(.*)\\] webapp=(.*) path=(.*) params=\\{(.*)\\} hits=(.*) status=(.*) QTime=(.*)");


    private String file;

    public void parse() throws Exception {

        InputStream in = IOUtils.getInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));


        String line;
        int currentCount = 0;

        Date currentDate = new Date();

        while ((line = br.readLine()) != null) {
            currentCount++;
            Matcher m = pattern.matcher(line);
            if (m.matches()) {
                try {
                    currentCount++;
                    SolrLogRow solrLogRow = parseRow(line,m, currentDate);
                    logRow(solrLogRow);


                } catch (Exception e) {
                    SolrLogRow item = new SolrLogRow();
                    item.setRaw(line);
                    logRowError(item,e);
                }
            } else {
                currentDate = parseDate(line);
            }

        }
        br.close();
    }

    protected void logRow(SolrLogRow solrLogRow) {

    }

    protected void logRowError(SolrLogRow solrLogRow, Exception e) {

    }


    SolrLogRow parseRow(String line, Matcher m, Date currentDate) {
        SolrLogRow item = new SolrLogRow();
        item.setRaw(line);

        String coreName = m.group(1);
        String webapp = m.group(2);
        String handler = m.group(3);
        String params = m.group(4);
        params = params.replaceAll("\\ ", " ");
        params = params.replaceAll("\\\"", " ");
        int hits = Integer.parseInt(m.group(5));
        int status = Integer.parseInt(m.group(6));
        int qTime = Integer.parseInt(m.group(7).trim());


        List<NameValuePair> urlParams = URLEncodedUtils.parse(params,
                Charset.forName("UTF-8"));
        item.setCoreName(coreName);
        item.setWebapp(webapp);
        item.setHandler(handler);
        item.setLogFile(file);
        item.setTimestamp(currentDate);
        item.setHits(hits);
        item.setStatus(status);
        item.setqTime(qTime);
        return item;
    }

    protected Date parseDate(String line) {
        // String line =
        // "Nov 13, 2014 12:05:39 AM org.apache.solr.core.SolrCore execute";
        SimpleDateFormat sdf = new SimpleDateFormat(
                "MMM dd, yyyy hh:mm:ss a 'org.apache.solr.core.SolrCore execute'", Locale.US);
        Date d;
        try {
            d = sdf.parse(line);
            return d;
        } catch (ParseException e) {
            return new Date();
        }

    }


}
