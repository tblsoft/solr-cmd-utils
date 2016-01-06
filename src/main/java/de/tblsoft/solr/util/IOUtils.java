package de.tblsoft.solr.util;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by tblsoft
 */
public class IOUtils {


    public static InputStream getInputStream(String inputFileName) throws IOException {
        InputStream fileStream = new FileInputStream(inputFileName);
        if (inputFileName.endsWith(".gz")) {
            return new GZIPInputStream(fileStream);
        }
        return fileStream;
    }


    public static OutputStream getOutputStream(String outputFileName) throws IOException {
        if("stdout".equals(outputFileName)) {
            return System.out;
        } else if("stderr".equals(outputFileName)) {
            return System.err;
        } else if (outputFileName.endsWith(".gz")) {
            OutputStream out = new FileOutputStream(outputFileName);
            return new GZIPOutputStream(out);
        } else {
            return new FileOutputStream(outputFileName);
        }
    }

    public static void appendToOutputStream(OutputStream out, String value ) throws IOException {
        out.write(value.getBytes("UTF-8"));

    }
}
