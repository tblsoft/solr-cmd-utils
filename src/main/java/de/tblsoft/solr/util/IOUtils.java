package de.tblsoft.solr.util;

import com.google.common.io.Files;

import de.tblsoft.solr.http.HTTPHelper;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by tblsoft
 */
public class IOUtils {

    public static String getAbsoluteFile(String directory, String fileName) {
        if(fileName.toLowerCase().startsWith("http")) {
            return fileName;
        } else if(fileName.toLowerCase().startsWith("classpath:")) {
    		return fileName;
        } else if(fileName.toLowerCase().startsWith("c:\\")) {
    		return fileName;
    	} else if(fileName.startsWith("/")) {
            return fileName;
    	} else if(fileName.startsWith("./")) {
            return fileName;
        } else {
            return directory + "/" + fileName;
        }
    }

    public static File getAbsoluteFileAsFile(String directory, String fileName) {
        return new File(getAbsoluteFile(directory, fileName));
    }

    public static String getDirectoryForFile(String file) {
        File f = new File(file);
        File currentPath = new File(f.getParent());

        return currentPath.getName();
    }

    public static List<String> getFiles(String path) {
        List<String> fileList = new ArrayList<String>();
        
        File root = new File(path);
        
		if(path.contains("*")) {
			IOFileFilter fileFilter = new WildcardFileFilter(root.getName());
			IOFileFilter dirFilter = new WildcardFileFilter("*");
			Collection<File> files = FileUtils.listFiles(root.getParentFile(), fileFilter, dirFilter);
			for(File file:files) {
				fileList.add(file.getAbsolutePath());
			}
			return fileList;
		}
        
        if(root.isFile()) {
            fileList.add(path);
            return fileList;
        }

        if(root.isDirectory()) {
            for (File file : Files.fileTraverser().breadthFirst(root)) {
                if (file.isFile()) {
                    fileList.add(file.getAbsolutePath());
                }
            }
            return fileList;
        }

        throw new RuntimeException("The file or path does not exists: " + path);
    }

    public static InputStream getInputStream(File inputFile) throws IOException {
        return getInputStream(inputFile.getAbsolutePath());
    }


    public static InputStream getInputStream(String inputFileName) throws IOException {
        InputStream inputStream;
        if(inputFileName.startsWith("http")) {
            inputStream =  HTTPHelper.getAsInputStream(inputFileName);
        } else if (inputFileName.startsWith("classpath:")) {
            String classpath = inputFileName.replaceFirst("classpath:", "");
            return IOUtils.class.getResourceAsStream(classpath);
        } else {
            inputStream = new FileInputStream(inputFileName);
        }

        if (inputFileName.endsWith(".gz")) {
            return new GZIPInputStream(inputStream);
        }
        if(inputFileName.endsWith(".bz2")) {
            return new BZip2CompressorInputStream(inputStream);
        }
        return inputStream;
    }

    public static String getString(String location) throws IOException {
        if(location.startsWith("http")) {
            return HTTPHelper.get(location);
        }
        return FileUtils.readFileToString(new File(location));
    }


    public static OutputStream getOutputStream(File outputFile) throws IOException {
        return getOutputStream(outputFile.getAbsolutePath());

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

    public static List<String> file2List(String fileName) throws IOException {
       return file2List(fileName, StandardCharsets.UTF_8);
    }

    public static List<String> file2List(String fileName, Charset charSet) throws IOException {
        List<String> result;
        Stream<String> lines = java.nio.file.Files.lines(Paths.get(fileName), charSet);
        result = lines.collect(Collectors.toList());
        return result;
    }

    public static void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (java.nio.file.Files.notExists(path)) {
            try {
                java.nio.file.Files.createDirectories(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean fileExists(String filePath) {
        Path path = Paths.get(filePath);
        return java.nio.file.Files.exists(path);
    }

    public static boolean fileExists(File file) {
        return file.exists();
    }
}
