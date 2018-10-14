package de.tblsoft.solr.util;

import java.time.Instant;

public class InstantUtils {


    private static Instant now = null;


    public static Instant now() {
        if(now == null) {
            return Instant.now();
        }
        return now;
    }

    public static void setNow(Instant now) {
        InstantUtils.now = now;
    }

}
