package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.parser.SolrXmlParser;
import de.tblsoft.solr.pipeline.bean.Reader;

/**
 * Created by tblsoft on 23.01.16.
 */
public class StandardReader extends SolrXmlParser implements ReaderIF {

    private PipelineExecuter executer;

    private Reader reader;

    private String baseDir;

    @Override
    public void field(String name, String value) {
        executer.field(name,value);

    }


    @Override
    public void endDocument() {
        executer.endDocument();
    }

    @Override
    public void read() {
        try {
            String filename = (String) reader.getProperty().get("filename");
            setInputFileName(filename);
            parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setSource(String source) {
        setInputFileName(source);
    }

    @Override
    public void setPipelineExecuter(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void end() {

    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
