package de.tblsoft.solr.pipeline;

import com.google.gson.GsonBuilder;
import de.tblsoft.solr.elastic.ElasticScrollQuery;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Created by tblsoft on 14.05.16.
 */
public class ElasticReader extends AbstractReader {

	private String url;
	private String scroll;
	private String body;
	private boolean verboseLog;


	@Override
	public void read() {
		GsonBuilder builder = new GsonBuilder();
		String response = "";
		String pagedUrl = "";
		String scrollId = "";
		boolean hasHits = false;
		try {
			
			
			
			url = getProperty("url", null);
			scroll = getProperty("scroll", "1m");
			body = getProperty("body", null);
			verboseLog = getPropertyAsBoolean("verboseLog", false);

			String requestFilename = getProperty("requestFilename", null);
			ElasticScrollQuery elasticScrollQuery = new ElasticScrollQuery(url);
			elasticScrollQuery.setScroll(scroll);

			if(requestFilename != null) {
				String absoluteRequestFilename = IOUtils.getAbsoluteFile(getBaseDir(),requestFilename);
				String request = FileUtils.readFileToString(new File(absoluteRequestFilename));
				elasticScrollQuery.setRequest(request);
			} else if (body != null){
				elasticScrollQuery.setRequest(body);
			}


			List<Document> docs;
			while((docs = scrollQuery(elasticScrollQuery)) != null) {
				for(Document document: docs) {
					executer.document(document);
				}
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private List<Document> scrollQuery(ElasticScrollQuery elasticScrollQuery) throws Exception {
		try {
			return elasticScrollQuery.nextDocuments();
		} catch (IllegalStateException ex) {
			if (verboseLog) {
				throw new IllegalStateException(ex);
			}
			throw new IllegalStateException(StringUtils.left(ex.getMessage(), 1024));
		}
	}

	@Override
	public void setPipelineExecuter(PipelineExecuter executer) {
		this.executer = executer;
	}

	@Override
	public void end() {

	}

	@Override
	public void setReader(Reader reader) {
		this.reader = reader;
	}
}
