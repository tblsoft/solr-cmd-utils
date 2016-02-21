package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 21.02.16.
 */
public class CSVWriter extends AbstractWriter {

    private CSVPrinter printer;

    private List<String> currentList = new ArrayList<String>();

    @Override
    public void init() {

        try {
            String filename = getProperty("filename", null);
            String delimiter = getProperty("delimiter", ",");
            String[] headers = getPropertyAsArray("headers", null);



            CSVFormat format = CSVFormat.RFC4180;
            if(headers == null) {
                format = format.withHeader();
            } else {
                format = format.withHeader(headers);
            }

            PrintWriter out = new PrintWriter(filename);
            printer = format.withDelimiter(delimiter.charAt(0)).print(out);
        } catch (Exception e) {

        }
        super.init();
    }

    @Override
    public void field(String name, String value) {
        currentList.add(value);
    }

    @Override
    public void endDocument() {

        try {
            List<List<String>> myList = new ArrayList<List<String>>();
            myList.add(currentList);

            printer.printRecords(myList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentList = new ArrayList<String>();

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
