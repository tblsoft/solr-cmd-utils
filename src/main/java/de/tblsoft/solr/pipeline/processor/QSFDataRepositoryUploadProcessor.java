package de.tblsoft.solr.pipeline.processor;

import com.quasiris.qsf.commons.repo.ModelRepositoryManager;
import com.quasiris.qsf.commons.repo.config.ModelRepositoryConfig;
import com.quasiris.qsf.commons.repo.config.ModelRepositoryConfigBuilder;
import de.tblsoft.solr.pipeline.AbstractProcessor;

public class QSFDataRepositoryUploadProcessor extends AbstractProcessor {

    @Override
    public void process() {
        String groupId = getProperty("groupId", null);
        String artifactId = getProperty("artifactId", null);
        String locale = getProperty("locale", null);
        String version = getProperty("version", null);
        String modelBasePath = getProperty("modelBasePath", null);
        String modelBaseUrl = getProperty("modelBaseUrl", null);
        String uploadBaseUrl = getProperty("uploadBaseUrl", null);
        String sourceDir = getProperty("sourceDir", null);

        try {
             ModelRepositoryConfig modelRepositoryConfig = ModelRepositoryConfigBuilder.create().
                     modelBasePath(modelBasePath).
                     uploadBaseUrl(uploadBaseUrl).
                     modelBaseUrl(modelBaseUrl).
                     build();


            ModelRepositoryManager modelRepositoryManager = ModelRepositoryManager.Builder.create().
                    groupId(groupId).
                    artifactId(artifactId).
                    version(version).
                    locale(locale).
                    config(modelRepositoryConfig).
                    build();

            modelRepositoryManager.saveAndUpload(sourceDir);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
