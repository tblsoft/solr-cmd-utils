package de.tblsoft.solr.log.parser;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.AtomicLongMap;

import java.text.SimpleDateFormat;
import java.util.*;

public class SolrRequestCounter extends SolrQueryLogParser {

    private AtomicLongMap<String> count = AtomicLongMap.create();

    private static Map<String, String> aggregationOptions =  new HashMap<String, String>();
    static {
        aggregationOptions.put("second", "yyyy-MM-dd-kk:mm:ss");
        aggregationOptions.put("minute", "yyyy-MM-dd-kk:mm");
        aggregationOptions.put("hour", "yyyy-MM-dd-kk");
        aggregationOptions.put("day", "yyyy-MM-dd");
        aggregationOptions.put("month", "yyyy-MM");
        aggregationOptions.put("year", "yyyy");
    }

    private String aggregationOption = "second";

    private long maxOutput = 150;



    public SolrRequestCounter(String file) {
        super(file);
    }

    @Override
    protected void logRow(SolrLogRow solrLogRow) {


        SimpleDateFormat sdf = new SimpleDateFormat(
               aggregationOptions.get(aggregationOption));
        String aggregatedDate = sdf.format(solrLogRow.getTimestamp());

        count.incrementAndGet(aggregatedDate);

    }

    @Override
    protected void logRowError(SolrLogRow solrLogRow, Exception e) {
        System.out.println("error");
    }

    public void print() {
        SortedSet<String> keys = new TreeSet<String>(count.asMap().keySet());

        long maxValue = Collections.max(count.asMap().values());
        System.out.println("max: " + maxValue);

        for (String key : keys) {
            long value = count.get(key);
            long scaledValue = scale(value,maxOutput, maxValue);
            System.out.println(key + " : " + value + print(scaledValue));

        }
    }

    private String print(long n) {
        int i = 0;
        StringBuilder buffer = new StringBuilder(" ");
        while (i++ < n) {
            //http://www.key-shortcut.com/zeichentabellen/utf-8-unicode-tabelle-2/
            //buffer.append('\u2588');
            //'\u2B1B'
            char icon = '\u2B1B';
            buffer.append(icon);
        }
        return buffer.toString();
    }

    private long scale(long value, long scale, long max) {
        long f = (value * scale) / max;
        return f;
    }

    public void setAggregationOption(String aggregationOption) {
        if(!Strings.isNullOrEmpty(aggregationOption)) {
            this.aggregationOption = aggregationOption;
        }
    }
}
