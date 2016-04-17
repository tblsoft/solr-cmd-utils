package de.tblsoft.solr.pipeline;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import de.tblsoft.solr.util.IOUtils;

public class ThreadDumpReader extends AbstractReader {

	private boolean isTrace = false;

	private StringBuilder traceBuilder = new StringBuilder();
	
	private String firstTraceLine;

	private String filename;

	private String currentFileName;
	
	private String currentDate;
	
	private String currentDescription;
	
	private String runId;
	



	public void read() {
		this.runId = UUID.randomUUID().toString();

		filename = getProperty("filename", null);

		List<String> fileList = IOUtils.getFiles(filename);

		for (String file : fileList) {
			try {
				currentFileName = file;
				InputStream in = IOUtils.getInputStream(file);
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);

				String line;

				while ((line = br.readLine()) != null) {
					if (line.matches("^\tat .*")) {
						trace(line);
					} else if (line
							.matches("^\\d{4}-\\d{2}-\\d{2}.\\d{2}:\\d{2}:\\d{2}$")) {
						date(line);
					} else if (line.matches("^Full thread dump Java HotSpot.*")) {
						description(line);

					} else if (line.matches("^Full thread dump Java HotSpot.*")) {
						description(line);

					} else if (line.matches("^   java.lang.Thread.State.*")) {
						state(line);
					} else if (StringUtils.isEmpty(line)) {
						emptyLine(line);
					} else if (StringUtils.startsWith(line, "\"")) {
						thread(line);
					} else if (StringUtils.startsWith(line,
							"JNI global references:")) {
						jniReferences(line);
					}

				}

			} catch (Exception e) {
				throw new RuntimeException();
			}
		}
	}

	void jniReferences(String jniReferences) {
		System.out.println(jniReferences);

	}

	void date(String date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdfPipeline = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			Date d = sdf.parse(date);
			//2016-04-13 09:43:18
			this.currentDate = sdfPipeline.format(d);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

	void description(String description) {
		this.currentDescription = description;
	}

	void state(String state) {
		state = state.trim().replace("java.lang.Thread.State:", "");
		executer.field("state", state);
	}

	void trace(String trace) {
		if(!isTrace) {
			firstTraceLine = trace;
		}
		isTrace = true;
		traceBuilder.append(trace);
		traceBuilder.append("\n");
	}

	void endTrace() {
		String trace = traceBuilder.toString();
		String traceHash = DigestUtils.md5Hex(trace);
		
		
		executer.field("runId", runId);
		executer.field("description", currentDescription);
		executer.field("date", currentDate);
		executer.field("fileName", currentFileName);
		executer.field("traceHash", traceHash);
		executer.field("traceNameHash", firstTraceLine + "_" + traceHash);
		executer.field("traceName", firstTraceLine);
		executer.field("trace", trace);

		traceBuilder = new StringBuilder();
		executer.endDocument();
	}

	void emptyLine(String date) {
		if (isTrace) {
			isTrace = false;
			endTrace();
		}
	}

	void thread(String thread) {
		executer.field("thread", thread);
	}
	
	

}
