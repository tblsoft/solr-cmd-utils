package de.tblsoft.solr.pipeline.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractReader;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 14.05.16.
 */
public class ElasticFacetReader extends AbstractReader {

	private String url;
	private String query;
	private String path;

	@Override
	public void read() {

		try {
			
			
			
			url = getProperty("url", null);
			query = getProperty("query", null);
			path = getProperty("path", null);

			String response;
			if(url.startsWith("http")) {
				response = HTTPHelper.post(url, query, "application/json");
			} else {
				String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), url);
				response = IOUtils.getString(absoluteFilename);
			}


			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(response);
			JsonNode coordinatesNode = node.at(path);

			List<Bucket> buckets = mapper.convertValue(coordinatesNode, new TypeReference<List<Bucket>>(){});
			for(Bucket bucket : buckets) {
				Document document = new Document();
				document.setField("value", bucket.getKey());
				document.setField("count", bucket.getDoc_count());
				executer.document(document);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
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
