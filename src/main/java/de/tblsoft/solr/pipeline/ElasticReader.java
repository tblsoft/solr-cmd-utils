package de.tblsoft.solr.pipeline;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;

/**
 * Created by tblsoft on 14.05.16.
 */
public class ElasticReader extends AbstractReader {

	private Gson gson;

	private String url;

	// TODO implement paging
	@Override
	public void read() {
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
		String response = "";
		String pagedUrl = "";
		String scrollId = "";
		boolean hasHits = false;
		try {
			
			
			
			url = getProperty("url", null);
			String scrollBaseUrl = ElasticHelper.getScrollUrl(url);
			pagedUrl = url + "&scroll=1m";

			do {
				 
		
				response = HTTPHelper.get(pagedUrl);
	
				JsonElement jsonResponse = gson.fromJson(response,
						JsonElement.class);
				scrollId = jsonResponse.getAsJsonObject()
						.get("_scroll_id").getAsString();
				
				Iterator<JsonElement> hitsIterator = jsonResponse.getAsJsonObject()
						.get("hits").getAsJsonObject().get("hits").getAsJsonArray()
						.iterator();
				hasHits = false;
				while (hitsIterator.hasNext()) {
					hasHits=true;
					Document document = new Document();
					for (Entry<String, JsonElement> entry : hitsIterator.next()
							.getAsJsonObject().get("_source").getAsJsonObject()
							.entrySet()) {
						if (entry.getValue().isJsonArray()) {
	
						} else if (entry.getValue().isJsonPrimitive()) {
							document.addField(entry.getKey(), entry.getValue().getAsString());
						}
					}
					executer.document(document);
				}
				
				pagedUrl = scrollBaseUrl + "?scroll=1m&scroll_id=" + scrollId;
			
			} while(hasHits);
			executer.end();

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
