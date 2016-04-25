package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tblsoft on 11.02.16.
 */
public class CSVReader extends AbstractReader {

    @Override
    public void read() {
        try {
        	
        	String charset = getProperty("charset", StandardCharsets.UTF_8.name());
            String filename = getProperty("filename", null);
            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(),filename);

            String delimiter = getProperty("delimiter", ",");
            String[] headers = getPropertyAsArray("headers", null);
            InputStream in = IOUtils.getInputStream(absoluteFilename);
            java.io.Reader reader = new InputStreamReader(in,charset);

            CSVFormat format = CSVFormat.RFC4180;
            if(headers == null) {
                format = format.withHeader();
            } else {
                format = format.withHeader(headers);
            }

            format=format.withDelimiter(delimiter.charAt(0));

            CSVParser parser = format.parse(reader);
            Iterator<CSVRecord> csvIterator = parser.iterator();

            while(csvIterator.hasNext()) {
                CSVRecord record = csvIterator.next();
                Map<String, Integer> header = parser.getHeaderMap();
                Document document = new Document();
                for(Map.Entry<String,Integer> entry : header.entrySet()) {
                    String key = entry.getKey();
                    String value = record.get(key);
                    document.addField(key, value);
                }
                executer.document(document);
            }
            //executer.end();
            in.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
