package de.tblsoft.solr.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Read from Quasiris Netbase
 */
public class NetbaseReader extends AbstractReader {

	private Gson gson;

	private String url;
	private String scroll;

	public void read() {
		GsonBuilder builder = new GsonBuilder();
		gson = builder.create();
		try {
			url = getProperty("url", null);
			List<String> netbaseIds = getPropertyAsList("netbaseIds",new ArrayList<String>());
			for (String netbaseId : netbaseIds) {
				String netBaseUrl = url + netbaseId + "+limit+100000";
				String response = HTTPHelper.get(netBaseUrl);

				JsonElement jsonResponse = gson.fromJson(response,
						JsonElement.class);

				Iterator<JsonElement> hitsIterator =
						jsonResponse.getAsJsonObject().get("results").getAsJsonArray().get(0).getAsJsonObject().get("statements").getAsJsonArray()
						.iterator();

				while (hitsIterator.hasNext()) {
					Document document = new Document();
					for (Entry<String, JsonElement> entry : hitsIterator.next()
							.getAsJsonObject()
							.entrySet()) {
						if (entry.getValue().isJsonArray()) {

						} else if (entry.getValue().isJsonPrimitive()) {
							document.addField(entry.getKey(), entry.getValue().getAsString());
						}
					}
					executer.document(document);

				}
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
