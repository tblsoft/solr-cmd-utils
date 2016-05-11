package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Joiner;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;
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

    @Override
    public void init() {

        try {
            String filename = getProperty("filename", null);
            absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

            delimiter = getProperty("delimiter", ",");
            headers = getPropertyAsArray("headers", null);

            multiValueSeperator = getProperty("multiValueSeperator", ";");






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
                String[] headersFromDocument = getFieldNames(document);
                PrintWriter out = new PrintWriter(absoluteFilename);
                CSVFormat format = CSVFormat.RFC4180;
                printer = format.withDelimiter(delimiter.charAt(0)).withHeader(headersFromDocument).print(out);
                firstDocument = false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            List<List<String>> csvRows = new ArrayList<List<String>>();
            List<String> csvList = new ArrayList<String>();
            for(Field field: document.getFields()) {
                String value = Joiner.on(multiValueSeperator).join(field.getValues());
                csvList.add(value);
            }
            csvRows.add(csvList);
            printer.printRecords(csvRows);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    }

}
