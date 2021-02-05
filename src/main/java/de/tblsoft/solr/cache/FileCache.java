package de.tblsoft.solr.cache;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DocumentUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

public class FileCache {

    private static Logger LOG = LoggerFactory.getLogger(FileCache.class);


    private String cacheBasePath;

    private String fileExtension;

    private Duration maxAge;


    public FileCache(String cacheBasePath, String fileExtension, Duration maxAge) {
        this.cacheBasePath = cacheBasePath;
        this.fileExtension = fileExtension;
        this.maxAge = maxAge;
    }

    public Document readFromCache(String url)  {
        if(cacheBasePath == null) {
            return null;
        }
        try {
            File target = getTargetFile(url);
            if (!Files.exists(target.toPath())) {
                return null;
            }
            if(maxAge != null) {
                Instant lastModified = Files.getLastModifiedTime(target.toPath()).toInstant();
                Duration fileAge = Duration.between(lastModified, Instant.now());
                if(maxAge.compareTo(fileAge) < 0) {
                    return null;
                }
            }

            return DocumentUtils.readFromFile(target);
        } catch (Exception e) {
            LOG.error("error " + e.getMessage() + " reading from cache for url: " + url, e);
            return null;
        }
    }

    public void writeToCache(String url, Document document) throws Exception {
        if(cacheBasePath == null) {
            return;
        }
        File target = getTargetFile(url);
        DocumentUtils.writeToFile(target, document);
    }

    private File getTargetFile(String url) throws URISyntaxException {
        String hashedUrl = hash(url);
        URI uri = new URI(url);
        File target = new File(cacheBasePath + "/" + uri.getHost() + "/" + hashedUrl + fileExtension);
        return target;
    }


    private String hash(String url) {
        String base64 = Base64.getEncoder().encodeToString(url.getBytes());
        if(base64.length() < 255) {
            return base64;
        }
        return DigestUtils.md5Hex(url.getBytes());
    }
}
