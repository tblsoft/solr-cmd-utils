package de.tblsoft.solr.pipeline.processor;

import com.quasiris.qsf.commons.ai.ModelRepositoryManager;
import de.tblsoft.solr.pipeline.AbstractProcessor;

public class QSFDataRepositoryUploadProcessor extends AbstractProcessor {

    @Override
    public void process() {
        String groupId = getProperty("groupId", null);
        String artifactId = getProperty("artifactId", null);
        String version = getProperty("version", null);
        String modelBasePath = getProperty("modelBasePath", null);
        String modelBaseUrl = getProperty("modelBaseUrl", null);
        String uploadBaseUrl = getProperty("uploadBaseUrl", null);
        String sourceDir = getProperty("sourceDir", null);

        try {
            ModelRepositoryManager modelRepositoryManager = ModelRepositoryManager.Builder.create().
                    groupId(groupId).
                    artifactId(artifactId).
                    version(version).
                    modelBasePath(modelBasePath).
                    modelBaseUrl(modelBaseUrl).
                    uploadBaseUrl(uploadBaseUrl).
                    build();

            modelRepositoryManager.saveAndUpload(sourceDir);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
