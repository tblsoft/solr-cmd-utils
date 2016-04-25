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

    @Override
    public void init() {

        try {
            String filename = getProperty("filename", null);
            String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);

            String delimiter = getProperty("delimiter", ",");
            String[] headers = getPropertyAsArray("headers", null);

            multiValueSeperator = getProperty("multiValueSeperator", ";");



            CSVFormat format = CSVFormat.RFC4180;
            if(headers == null) {
                format = format.withHeader();
            } else {
                format = format.withHeader(headers);
            }

            PrintWriter out = new PrintWriter(absoluteFilename);
            printer = format.withDelimiter(delimiter.charAt(0)).print(out);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        super.init();
    }

    @Override
    public void document(Document document) {
        try {
            List<String> csvList = new ArrayList<String>();
            for(Field field: document.getFields()) {
                String value = Joiner.on(multiValueSeperator).join(field.getValues());
                csvList.add(value);
            }
            printer.printRecords(csvList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void end() {
        try {
            printer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
