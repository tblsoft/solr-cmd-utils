package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.resume.ResumeStatusDTO;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AbstractResumeReaderTest {

    @Rule
    public TemporaryFolder workDir = new TemporaryFolder();

    @Test
    public void flushesEveryBatchBeforeItIsMarkedAsDone() {
        RecordingExecuter executer = new RecordingExecuter(workDir.getRoot().getAbsolutePath(), new ResumeStatusDTO());
        executer.status.setCompleted(false);
        run(executer, "1", "2", "3");

        Assert.assertEquals(List.of("1", "2", "flush lastBatch=0", "3", "flush lastBatch=1"), executer.events);
        Assert.assertEquals(Integer.valueOf(2), executer.status.getLastBatch());
    }

    @Test
    public void resumeContinuesWithTheFirstUnfinishedBatch() {
        String dir = workDir.getRoot().getAbsolutePath();
        RecordingExecuter first = new RecordingExecuter(dir, new ResumeStatusDTO());
        first.status.setCompleted(false);
        first.failOnDocument = "3";
        Assert.assertThrows(IllegalStateException.class, () -> run(first, "1", "2", "3"));

        RecordingExecuter second = new RecordingExecuter(dir, first.status);
        run(second);

        Assert.assertEquals(List.of("3", "flush lastBatch=1"), second.events);
    }

    private void run(RecordingExecuter executer, String... ids) {
        TestReader reader = new TestReader(ids);
        Reader config = new Reader();
        config.setProperty(new HashMap<>(java.util.Map.of("resumeBatchSize", "2")));
        reader.setReader(config);
        reader.setPipelineExecuter(executer);
        reader.init();
        reader.read();
        reader.end();
    }

    private static class TestReader extends AbstractResumeReader {
        private final String[] ids;

        TestReader(String... ids) {
            this.ids = ids;
        }

        @Override
        public void read() {
            for (String id : ids) {
                Document document = new Document();
                document.setId(id);
                document(document);
            }
        }
    }

    private static class RecordingExecuter extends PipelineExecuter {
        final List<String> events = new ArrayList<>();
        final String dir;
        final ResumeStatusDTO status;
        String failOnDocument;

        RecordingExecuter(String dir, ResumeStatusDTO status) {
            super("unused");
            this.dir = dir;
            this.status = status;
        }

        @Override
        public Boolean getResumeable() {
            return true;
        }

        @Override
        public String getWorkDir() {
            return dir;
        }

        @Override
        public ResumeStatusDTO getResumeStatus() {
            return status;
        }

        @Override
        public void saveResumeStatus() {
        }

        @Override
        public void document(Document document) {
            if (document.getId().equals(failOnDocument)) {
                throw new IllegalStateException("canceled");
            }
            events.add(document.getId());
        }

        @Override
        public void flush() {
            events.add("flush lastBatch=" + status.getLastBatch());
        }
    }
}
