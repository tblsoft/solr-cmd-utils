package de.tblsoft.solr.pipeline;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 14.05.16.
 */
public class ElasticJsonPathReader extends AbstractReader {

	private String url;
	private String scroll;

	public void read() {
		String response = "";
		String pagedUrl = "";
		String scrollId = "";
		boolean hasHits = false;

		SimpleMapping simpleMapping = new SimpleMapping(getPropertyAsList("mapping", new ArrayList<String>()));
		Map<String, List<String>> mapping = simpleMapping.getMapping();
		try {
			url = getProperty("url", null);
			scroll = getProperty("scroll", "1m");
			String scrollBaseUrl = ElasticHelper.getScrollUrl(url);
			pagedUrl = url + "&scroll=" + scroll;

			do {
				response = HTTPHelper.get(pagedUrl);
				DocumentContext context = JsonPath.parse(response);
				scrollId = context.read("$['_scroll_id']");
				List<Object> elasticHits = context.read("$['hits']['hits'][*]['_source']");
				hasHits = false;
				int count = 0;
				for(Object obj: elasticHits){
					hasHits=true;
					Document document = new Document();

					for(Map.Entry<String, List<String>> mappingEntry : mapping.entrySet()) {
						try {
							Object parsedValue = JsonPath.parse(obj).read(mappingEntry.getKey());
							for(String target: mappingEntry.getValue()) {
								document.setField(target, parsedValue);
							}
						} catch (PathNotFoundException e) {
							//ignore
						}
					}
					executer.document(document);
				}
				
				pagedUrl = scrollBaseUrl + "?scroll=" + scroll + "&scroll_id=" + scrollId;
			
			} while(hasHits);
			//executer.end();

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
