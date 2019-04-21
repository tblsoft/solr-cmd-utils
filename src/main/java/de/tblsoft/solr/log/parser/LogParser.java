package de.tblsoft.solr.log.parser;

import de.tblsoft.solr.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static Logger LOG = LoggerFactory.getLogger(LogParser.class);


    public LogParser(String file) {
        this.file = file;
    }

    private String datePattern = "";

    private String dateRegex = "";

    private String locale = "US";


    private String file;

    public void parse() throws Exception {

        InputStream in = IOUtils.getInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));


        String line;
        int currentCount = 0;





        Pattern pattern = Pattern
                .compile(dateRegex);


        while ((line = br.readLine()) != null) {
            currentCount++;
            Matcher m = pattern.matcher(line);
            if (m.matches()) {
                try {
                    currentCount++;
                    LOG.info(m.group(1));
                    Date date = parseDate( m.group(1));
                    line(date, line);
                } catch (Exception e) {
                    error(e);
                }
            }

        }
        br.close();
    }

    protected void line(Date date, String line) {

    }

    protected void error(Exception e) {

    }


    protected Date parseDate(String line) {
        SimpleDateFormat sdf = new SimpleDateFormat(
                datePattern , Locale.forLanguageTag(locale));

        Locale.forLanguageTag(locale);
        Date d;
        try {
            d = sdf.parse(line);
            return d;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void setDatePattern(String datePattern) {
        this.datePattern = datePattern;
    }

    public void setDateRegex(String dateRegex) {
        this.dateRegex = dateRegex;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
