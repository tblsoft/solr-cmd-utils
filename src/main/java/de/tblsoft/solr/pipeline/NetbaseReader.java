package de.tblsoft.solr.pipeline;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Read from Quasiris Netbase
 */
public class NetbaseReader extends AbstractReader {

	public void read() {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		try {
			String url = getProperty("url", null);
			String filename = getProperty("filename", null);
			if(filename != null) {
				String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), filename);
				processFile(absoluteFilename, gson);
			} else {
				processUrl(url, gson);
			}
			List<String> netbaseIds = getPropertyAsList("netbaseIds",new ArrayList<String>());
			for (String netbaseId : netbaseIds) {
				String netBaseUrl = url + netbaseId + "+limit+100000";
				String response = HTTPHelper.get(netBaseUrl);
				processBatch(gson, response);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void processUrl(String url, Gson gson) {
		List<String> netbaseIds = getPropertyAsList("netbaseIds",new ArrayList<String>());
		for (String netbaseId : netbaseIds) {
			String netBaseUrl = url + netbaseId + "+limit+100000";
			String response = HTTPHelper.get(netBaseUrl);
			processBatch(gson, response);
		}
	}

	private void processFile(String filename, Gson gson) throws IOException {
		String response = FileUtils.readFileToString(new File(filename));
		processBatch(gson, response);

	}

	private void processBatch(Gson gson, String response) {
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

	@Override
	public void setPipelineExecuter(PipelineExecuter executer) {
		this.executer = executer;
	}

	@Override
	public void setReader(Reader reader) {
		this.reader = reader;
	}
}
