package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by tbl on 12.08.17.
 */
public class CheckDuplicateFilter extends AbstractFilter {

    private Set<String> duplicates;

    private File absoluteDuplicatesStoreFile;
    private String duplicateFieldName;

    private int counter = 0;

    private int checkpoint = 1000;

    @Override
    public void init() {
        String duplicatesStoreFile = getProperty("duplicatesStoreFile", "duplicatesStore.txt");
        duplicateFieldName = getProperty("duplicateFieldName", null);
        checkpoint = getPropertyAsInt("checkpoint", 1000);
        absoluteDuplicatesStoreFile =  IOUtils.getAbsoluteFileAsFile(getBaseDir(),duplicatesStoreFile );
        if(!absoluteDuplicatesStoreFile.exists()) {
            duplicates = new HashSet<String>();
        } else {
            try {
                for(String line :FileUtils.readLines(absoluteDuplicatesStoreFile)) {
                    duplicates.add(line);
                }
            } catch (IOException e) {
                duplicates = new HashSet<String>();
            }
        }
        super.init();
    }

    @Override
    public void document(Document document) {
        counter++;
        if((counter % checkpoint) == 0) {
            saveDuplicatesToFile();
        }
        String duplicateFieldValue = document.getFieldValue(duplicateFieldName);
        if(duplicateFieldValue == null) {
            super.document(document);
        }
        if(duplicates.contains(duplicateFieldValue)) {
            // do nothing
        } else {
            duplicates.add(duplicateFieldValue);
        }
    }

    @Override
    public void end() {
        saveDuplicatesToFile();

        super.end();
    }

    void saveDuplicatesToFile() {
        try {
            FileUtils.writeLines(absoluteDuplicatesStoreFile, duplicates);
        } catch (IOException e) {
            //fail silent
        }
    }
}
