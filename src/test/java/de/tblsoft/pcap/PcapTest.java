package de.tblsoft.pcap;

import com.google.common.io.ByteStreams;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * http://www.kroosec.com/2012/10/a-look-at-pcap-file-format.html
 * Created by oelbaer on 01.05.16.
 */
public class PcapTest {
    private static Logger LOG = LoggerFactory.getLogger(PcapTest.class);


    @org.junit.Test
    @Ignore
    public void test() throws Exception {
        String file = "/tmp/artifacts/output.pcap";
        InputStream input = new BufferedInputStream(new FileInputStream(file));
        byte[] bytes = ByteStreams.toByteArray(input);

        byte[] magicNumber = new byte[4];
        System.arraycopy(bytes,0,magicNumber,0,4);
        magicNumber(magicNumber);


        byte[] version = new byte[4];
        System.arraycopy(bytes,4,version,0,4);
        magicNumber(version);

    }

    void version(byte[] version) {
        printHex(version);

    }


    void magicNumber(byte[] magicNumber) {
        printHex(magicNumber);

    }

    void printHex(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String p = String.format("%02X ", b[i]);
            LOG.info(p + " ");

        }

    }


}
