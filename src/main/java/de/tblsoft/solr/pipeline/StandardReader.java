package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.parser.SolrXmlParser;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tblsoft on 23.01.16.
 */
public class StandardReader extends SolrXmlParser implements ReaderIF {

    private static Logger LOG = LoggerFactory.getLogger(StandardReader.class);


    private PipelineExecuter executer;

    private Reader reader;

    private String baseDir;

    private Document document = new Document();
    
    private File tempFile;
    
    protected Map<String,String> variables = new HashMap<String, String>();

    @Override
    public void field(String name, String value) {
        document.addField(name, value);

    }


    @Override
    public void endDocument() {
        executer.document(document);
        document = new Document();
    }

    @Override
    public void read() {
        try {
            
        	String filename = (String) reader.getProperty().get("filename");
        	String url = getProperty("url", null);
        	if(!Strings.isNullOrEmpty(url)) {
        		tempFile = File.createTempFile("solr-reader-temp-file", ".xml");
        		LOG.info("Store temporary file to " + tempFile.getAbsolutePath());
        		HTTPHelper.get2File(url, tempFile);
        		filename = tempFile.getAbsolutePath();
        	}
            
        	
            
            String absoluteFilename = IOUtils.getAbsoluteFile(baseDir, filename);
            setInputFileName(absoluteFilename);
            parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setSource(String source) {
        setInputFileName(source);
    }

    @Override
    public void setPipelineExecuter(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void end() {
    	try {
    		if(tempFile != null) {
    			Files.deleteIfExists(tempFile.toPath());
    		}
		} catch (IOException e) {
			throw new RuntimeException(e);		}

    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    @Override
    public void setVariables(Map<String, String> variables) {
        if(variables == null) {
            return;
        }
        for(Map.Entry<String,String> entry: variables.entrySet()) {
            this.variables.put("variables." + entry.getKey(), entry.getValue());
        }
    }
    
    public String getProperty(String name, String defaultValue) {
        String value = (String) reader.getProperty().get(name);
        if(value != null) {
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            value = strSubstitutor.replace(value);
            return value;
        }
        return defaultValue;
    }
}
