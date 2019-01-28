package de.tblsoft.solr.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tblsoft
 */
public class OutputStreamStringBuilder {


    private OutputStream out;

    public OutputStreamStringBuilder(OutputStream out) {
        this.out = out;
    }

    public OutputStreamStringBuilder append(String value) {
        try {
            out.write(value.getBytes("UTF-8"));
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
