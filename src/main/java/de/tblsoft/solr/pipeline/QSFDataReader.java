package de.tblsoft.solr.pipeline;

import com.quasiris.qsf.commons.repo.ModelRepositoryManager;
import com.quasiris.qsf.commons.repo.config.ModelRepositoryConfig;
import com.quasiris.qsf.commons.repo.config.ModelRepositoryConfigBuilder;
import de.tblsoft.solr.pipeline.bean.Pipeline;
import de.tblsoft.solr.pipeline.bean.Reader;

import java.util.Map;

/**
 * Created by tblsoft on 27.03.16.
 */
public class QSFDataReader extends AbstractReader {

    @Override
    public void read() {
        String groupId = getProperty("groupId", null);
        String artifactId = getProperty("artifactId", null);
        String version = getProperty("version", null);
        String modelBasePath = getProperty("modelBasePath", null);
        String modelBaseUrl = getProperty("modelBaseUrl", null);
        String pipeline = getProperty("pipeline", "pipeline.yml");
        String filenameProperty = getProperty("filenameProperty", "filename");

        ModelRepositoryConfig modelRepositoryConfig = ModelRepositoryConfigBuilder.create().
                modelBasePath(modelBasePath).
                modelBaseUrl(modelBaseUrl).
                build();
        try {
            ModelRepositoryManager modelRepositoryManager = ModelRepositoryManager.Builder.create().
                    groupId(groupId).
                    artifactId(artifactId).
                    version(version).
                    config(modelRepositoryConfig).
                    build();


            modelRepositoryManager.install();

            String absolutePipeline = modelRepositoryManager.getAbsoluteModelFile(pipeline);

            Pipeline pipelineInstance = PipelineExecuter.readPipelineFromYamlFile(absolutePipeline);
            Reader reader = pipelineInstance.getReader();

            String filename = (String)reader.getProperty().get(filenameProperty);
            filename = modelRepositoryManager.getAbsoluteModelFile() + filename;
            Map<String, Object> properties = (Map<String, Object>) reader.getProperty();
            properties.put(filenameProperty, filename );


            ReaderIF readerInstance = PipelineExecuter.createReaderInstance(
                    reader, "", pipelineInstance.getVariables(), executer);
            readerInstance.read();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
