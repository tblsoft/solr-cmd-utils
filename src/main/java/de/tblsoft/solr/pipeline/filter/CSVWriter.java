package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Joiner;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 21.02.16.
 */
public class CSVWriter extends AbstractFilter {

    private CSVPrinter printer;

    private String multiValueSeperator;

    private boolean firstDocument = true;

    private String delimiter;

    private String[] headers;

    private String absoluteFilename;
    
    private boolean withHeaders = true;

    private boolean append = true;

    @Override
    public void init() {

        try {
            String filename = getProperty("filename", null);
            absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

            delimiter = getProperty("delimiter", ",");
            headers = getPropertyAsArray("headers", null);

            multiValueSeperator = getProperty("multiValueSeperator", ";");
            withHeaders = getPropertyAsBoolean("withHeaders", true);
            append = getPropertyAsBoolean("append", false);





        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        super.init();
    }

    String[] getFieldNames(Document document) {
        List<String> headersFromDocument = new ArrayList<String>();
        for(Field field : document.getFields()) {
            headersFromDocument.add(field.getName());
        }
        return headersFromDocument.toArray(new String[headersFromDocument.size()]);
    }

    @Override
    public void document(Document document) {
        if(firstDocument) {
            try {
                if(headers == null) {
                    headers = getFieldNames(document);
                }
                CSVFormat format = CSVFormat.RFC4180;
                if(withHeaders) {
                	format = format.withDelimiter(delimiter.charAt(0)).withHeader(headers);
                } else {
                	format = format.withDelimiter(delimiter.charAt(0));
                }

                if(!append) {
                    File file = new File(absoluteFilename);
                    file.delete();
                }

                OpenOption[] openOptions = append ?
                                            new OpenOption[] {StandardOpenOption.APPEND, StandardOpenOption.CREATE} :
                                            new OpenOption[] {StandardOpenOption.CREATE};
                Writer writer = Files.newBufferedWriter(Paths.get(absoluteFilename), openOptions);
                
            	printer = new CSVPrinter(writer, format);
                firstDocument = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            List<List<String>> csvRows = new ArrayList<List<String>>();
            List<String> csvList = new ArrayList<String>();
            for (String headerField : headers) {
                Field field = document.getField(headerField);
                String value = null;
                if(field != null && field.getValues() != null) {
                    value = Joiner.on(multiValueSeperator).skipNulls().join(field.getValues());
                }
                csvList.add(value);
            }
            csvRows.add(csvList);
            printer.printRecords(csvRows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.document(document);
    }



    @Override
    public void end() {
        try {
            if(printer != null) {
                printer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.end();

    }

}
