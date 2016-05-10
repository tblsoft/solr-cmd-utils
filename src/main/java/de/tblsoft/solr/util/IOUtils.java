package de.tblsoft.solr.util;

import com.google.common.io.Files;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by tblsoft
 */
public class IOUtils {

    public static String getAbsoluteFile(String directory, String fileName) {
        if(fileName.startsWith("/")) {
            return fileName;
        } else {
            return directory + "/" + fileName;
        }
    }


    public static String getDirectoryForFile(String file) {
        File f = new File(file);
        File currentPath = new File(f.getParent());

        return currentPath.getName();
    }

    public static List<String> getFiles(String path) {
        List<String> fileList = new ArrayList<String>();

        File root = new File(path);
        if(root.isFile()) {
            fileList.add(path);
            return fileList;
        }

        if(root.isDirectory()) {
            for (File file : Files.fileTreeTraverser().preOrderTraversal(root)) {
                if (file.isFile()) {
                    fileList.add(file.getAbsolutePath());
                }
            }
            return fileList;
        }

        throw new RuntimeException("The file or path does not exists: " + path);
    }


    public static InputStream getInputStream(String inputFileName) throws IOException {
        InputStream fileStream = new FileInputStream(inputFileName);
        if (inputFileName.endsWith(".gz")) {
            return new GZIPInputStream(fileStream);
        }
        if(inputFileName.endsWith(".bz2")) {
            return new BZip2CompressorInputStream(fileStream);
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
