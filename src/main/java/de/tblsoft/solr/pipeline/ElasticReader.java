package de.tblsoft.solr.pipeline;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

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
		String response = "foo";
		try {
			url = getProperty("url", null);
			response = HTTPHelper.get(url);

			JsonElement jsonResponse = gson.fromJson(response,
					JsonElement.class);
			Iterator<JsonElement> hitsIterator = jsonResponse.getAsJsonObject()
					.get("hits").getAsJsonObject().get("hits").getAsJsonArray()
					.iterator();

			while (hitsIterator.hasNext()) {
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
