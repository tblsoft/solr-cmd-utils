package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.log.parser.SolrLogRow;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SolrQueryLogReader extends AbstractReader {

	private Pattern pattern = Pattern
			.compile(".* \\[(.*)\\] webapp=(.*) path=(.*) params=\\{(.*)\\} hits=(.*) status=(.*) QTime=(.*)");

	private String filename;

	private String currentFileName;
	
	private String currentDirName;

	@Override
	public void read() {
		filename = getProperty("filename", null);

		String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(),
				filename);

		List<String> fileList = IOUtils.getFiles(absoluteFilename);

		for (String file : fileList) {
			currentFileName = file;
			currentDirName = IOUtils.getDirectoryForFile(file);

			try {
				InputStream in = IOUtils.getInputStream(file);
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);

				String line;
				int currentCount = 0;
				Date currentDate = new Date();

				while ((line = br.readLine()) != null) {
					currentCount++;
					Matcher m = pattern.matcher(line);
					if (m.matches()) {
						try {
							currentCount++;
							SolrLogRow solrLogRow = parseRow(line, m,
									currentDate);
							logRow(solrLogRow);

						} catch (Exception e) {
							SolrLogRow item = new SolrLogRow();
							item.setRaw(line);
							logRowError(item, e);
						}
					} else {
						currentDate = parseDate(line);
					}

				}
				br.close();
			} catch (Exception e) {

			}
		}
		executer.end();

	}

	protected void logRow(SolrLogRow solrLogRow) {
		Document document = new Document();
		document.addField("coreName", solrLogRow.getCoreName());
		document.addField("hits", String.valueOf(solrLogRow.getHits()));
		document.addField("fileName", solrLogRow.getLogFile());
		document.addField("dirName", currentDirName);
		document.addField("queryTime", String.valueOf(solrLogRow.getqTime()));
		document.addField("date", date2String(solrLogRow.getTimestamp()));
		document.addField("url", solrLogRow.getUrl());
		document.addField("raw", solrLogRow.getRaw());

		executer.document(document);
	}

	protected void logRowError(SolrLogRow solrLogRow, Exception e) {

	}

	SolrLogRow parseRow(String line, Matcher m, Date currentDate) {
		SolrLogRow item = new SolrLogRow();
		item.setRaw(line);

		String coreName = m.group(1);
		String webapp = m.group(2);
		String handler = m.group(3);
		String params = m.group(4);
		params = params.replaceAll("\\ ", " ");
		params = params.replaceAll("\\\"", " ");
		int hits = Integer.parseInt(m.group(5));
		int status = Integer.parseInt(m.group(6));
		int qTime = Integer.parseInt(m.group(7).trim());

		List<NameValuePair> urlParams = URLEncodedUtils.parse(params,
				Charset.forName("UTF-8"));

		StringBuilder url = new StringBuilder("http://localhost/?");
		for (NameValuePair pair : urlParams) {
			url.append(pair.getName());
			url.append("=");
			url.append(pair.getValue());
			url.append("&");
		}
		item.setUrl(url.toString());
		item.setCoreName(coreName);
		item.setWebapp(webapp);
		item.setHandler(handler);
		item.setLogFile(currentFileName);
		item.setTimestamp(currentDate);
		item.setHits(hits);
		item.setStatus(status);
		item.setqTime(qTime);
		return item;
	}

	String date2String(Date date) {

		SimpleDateFormat sdfPipeline = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ssZ");
		return sdfPipeline.format(date);

	}

	protected Date parseDate(String line) {
		// String line =
		// "Nov 13, 2014 12:05:39 AM org.apache.solr.core.SolrCore execute";
		SimpleDateFormat sdf = new SimpleDateFormat(
				"MMM dd, yyyy hh:mm:ss a 'org.apache.solr.core.SolrCore execute'",
				Locale.US);
		Date d;
		try {
			d = sdf.parse(line);
			return d;
		} catch (ParseException e) {
			return new Date();
		}

	}

}
