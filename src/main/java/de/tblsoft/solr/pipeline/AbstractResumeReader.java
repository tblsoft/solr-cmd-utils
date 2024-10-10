package de.tblsoft.solr.pipeline;

import com.quasiris.qsf.commons.util.JsonUtil;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.resume.ResumeStatusDTO;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 10.10.24.
 */
public abstract class AbstractResumeReader extends AbstractReader {

    private static Logger LOG = LoggerFactory.getLogger(AbstractResumeReader.class);

    private Long resumeBatchSize = 100L;

    private Integer batchCount = 0;

    private Boolean resumable = false;

    private List<Document> documents = new ArrayList<>();

    private Boolean resumeDeleteDir = true;

    public void initResume() {
        resumeBatchSize = getPropertyAsInteger("resumeBatchSize", resumeBatchSize);
        resumeDeleteDir = getPropertyAsBoolean("resumeDeleteDir", resumeDeleteDir);
        resumable = getPropertyAsBoolean("resumable", resumable);
        IOUtils.createDirectoryIfNotExists(executer.getWorkDir() + "/resume");
    }


    @Override
    public void document(Document document) {
        if(resumable) {
            documents.add(document);
            if(documents.size() >= resumeBatchSize) {
                writeBatch();
            }
        } else {
            super.document(document);
        }
    }

    private String getBatchFile(Integer batch) {
        return executer.getWorkDir() + "/resume/" + batch + ".jsonl";
    }

    private void writeBatch() {
        if(documents.isEmpty()) {
            return;
        }
        String file = getBatchFile(batchCount);
        serializeToJsonl(documents, file);

        executer.getResumeStatus().setLastBatch(batchCount);
        executer.saveResumeStatus();
        batchCount++;
        documents = new ArrayList<>();
    }

    @Override
    public void end() {
        if(resumable) {
            writeBatch();
            ResumeStatusDTO status = executer.getResumeStatus();
            status.setCompleted(Boolean.TRUE);
            if(status.getLastBatch() == null) {
                status.setLastBatch(0);
            }
            String file = getBatchFile(status.getLastBatch());
            while (IOUtils.fileExists(file)) {
                List<Document> docs = readJsonlFile(file);
                for (Document document : docs) {
                    super.document(document);
                }
                status.setLastBatch(status.getLastBatch() + 1);
                file = getBatchFile(status.getLastBatch());
                executer.saveResumeStatus();
            }

            if (resumeDeleteDir) {
                try {
                    FileUtils.deleteDirectory(new File(executer.getWorkDir()));
                } catch (IOException e) {
                    // ignore error
                }
            }
        }

        super.end();
    }



    public static List<Document> readJsonlFile(String filePath) {
        List<Document> documents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Document document = JsonUtil.defaultMapper().readValue(line, Document.class);
                documents.add(document);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return documents;
    }

    public static void serializeToJsonl(List<Document> documents, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Document document : documents) {
                String json = JsonUtil.defaultMapper().writeValueAsString(document);
                writer.write(json);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean getResumable() {
        return resumable;
    }
}
