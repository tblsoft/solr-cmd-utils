package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 11.02.16.
 */
public class CSVReader extends AbstractReader {

    private static Logger LOG = LoggerFactory.getLogger(CSVReader.class);

    private boolean addMeta = false;
    private boolean failOnError = false;
    private String charset = StandardCharsets.UTF_8.name();


    @Override
    public void read() {


        addMeta = getPropertyAsBoolean("addMeta", false);
        failOnError = getPropertyAsBoolean("failOnError", false);
        charset = getProperty("charset", StandardCharsets.UTF_8.name());
        try {
        	

            List<String> filenames = getPropertyAsList("filenames", new ArrayList<>());
            if(filenames.isEmpty()) {
                String singleFile = getProperty("filename", null);
                filenames.add(singleFile);
            }

            for(String filename : filenames) {
                try {
                    processFile(filename);
                } catch (Exception e) {
                    if(failOnError) {
                        throw e;
                    } else {
                        LOG.error("Could not process file " + filename + " because: " + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processFile(String filename) throws IOException {
        String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);


        Long maxRows = getPropertyAsInteger("maxRows", Long.MAX_VALUE);
        String delimiter = getProperty("delimiter", ",");
        String quote = getProperty("quote", null);
        String arrayDelimiter = getProperty("arrayDelimiter", null);
        String[] headers = getPropertyAsArray("headers", null);
        InputStream in = IOUtils.getInputStream(absoluteFilename);
        java.io.Reader reader = new InputStreamReader(in, charset);

        CSVFormat format = CSVFormat.RFC4180;
        if (headers == null) {
            format = format.withHeader();
        } else {
            format = format.withHeader(headers);
        }

        format = format.withDelimiter(delimiter.charAt(0));
        if ("null".equals(quote)) {
            format = format.withQuote(null);
        } else if (quote != null) {
            format = format.withQuote(quote.charAt(0));
        }


        CSVParser parser = format.parse(reader);
        Iterator<CSVRecord> csvIterator = parser.iterator();
        long rowNumber = 0;
        while (csvIterator.hasNext()) {
            if (rowNumber >= maxRows) {
                break;
            }
            rowNumber++;
            CSVRecord record = csvIterator.next();
            Map<String, Integer> header = parser.getHeaderMap();
            Document document = new Document();
            for (Map.Entry<String, Integer> entry : header.entrySet()) {
                String key = entry.getKey();
                try {
                    String value = record.get(key);
                    if (StringUtils.isEmpty(arrayDelimiter)) {
                        document.addField(key, value);
                    } else {
                        List<String> valueList = new ArrayList<String>();
                        String[] values = value.split(arrayDelimiter);
                        if (values.length > 0) {
                            for (String val : values) {
                                if (StringUtils.isNotEmpty(val)) {
                                    valueList.add(val);
                                }
                            }
                        }
                        document.setField(key, valueList);
                    }
                } catch (IllegalArgumentException e) {

                }
            }
            if (addMeta) {
                document.addField("rowNumber", String.valueOf(rowNumber));
                document.addField("fileName", absoluteFilename);
            }
            executer.document(document);
        }
        in.close();
    }

}
