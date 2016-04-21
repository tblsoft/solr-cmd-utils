package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.util.IOUtils;
import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.Match;
import oi.thekraken.grok.api.exception.GrokException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ThreadDumpReader extends AbstractReader {

	private boolean isTrace = false;

    private boolean isLockedOwnableSynchronizers = false;

	private StringBuilder traceBuilder = new StringBuilder();
	
	private String firstTraceLine;

	private String filename;

	private String currentFileName;

    private String currentDirectory;

	private String currentDate;
	
	private String currentDescription;
	
	private String runId;

    private String grokPatternPath = "src/main/grok/patterns/patterns";

    private boolean firstThread = true;

    private int position = 0;
	



	public void read() {
		this.runId = UUID.randomUUID().toString();

		filename = getProperty("filename", null);

		List<String> fileList = IOUtils.getFiles(filename);

		for (String file : fileList) {
			try {
				currentFileName = file;
                currentDirectory = IOUtils.getDirectoryForFile(currentFileName);
                firstThread = true;
                position = 0;
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

					} else if (line.matches("^   Locked ownable synchronizers:.*")) {
                        lockedOwnableSynchronizers(line);

                    } else if (line.matches("^   java.lang.Thread.State.*")) {
						state(line);
					} else if (line.matches("^\t- locked.*")) {
                        locked(line);
                    } else if (line.matches("^\t- waiting on.*")) {
                        waitingOn(line);
                    } else if (line.matches("^\t- parking to wait for.*")) {
                        parkingToWaitingFor(line);
                    } else if (line.matches("^\t-.*")) {
                        problem(line);
                    } else if (StringUtils.isEmpty(line)) {
						emptyLine(line);
					} else if (StringUtils.startsWith(line, "\"")) {
						thread(line);
					} else if (StringUtils.startsWith(line,
							"JNI global references:")) {
						jniReferences(line);
					}

				}
                endDocument();

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

    void lockedOwnableSynchronizers(String value) {
        isLockedOwnableSynchronizers=true;
    }

    void problem(String value) {
        System.out.println("problem: " + isLockedOwnableSynchronizers + " " + value);

    }

	void jniReferences(String jniReferences) {
		//System.out.println(jniReferences);

	}

    void locked(String value) {
        //System.out.println("locked: " + value);

    }

    void waitingOn(String value) {
        //System.out.println("watingOn: " + value);

    }

    void parkingToWaitingFor(String value) {
        //System.out.println("parkingToWaitingFor: " + value);

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
		
		
		executer.field("traceHash", traceHash);
		executer.field("traceNameHash", firstTraceLine + "_" + traceHash);
		executer.field("traceName", firstTraceLine);
		executer.field("trace", trace);


		traceBuilder = new StringBuilder();
	}

	void emptyLine(String date) {
		if (isTrace) {
			isTrace = false;
			endTrace();
		}

        if(isLockedOwnableSynchronizers) {
            isLockedOwnableSynchronizers = false;
            endLockedOwnableSynchronizers();
        }
	}

    void endLockedOwnableSynchronizers() {

    }

	void thread(String thread) {
        if(firstThread) {
            firstThread = false;
        } else {
            endDocument();
        }

        Grok grok = null;
        try {
            grok = Grok.create(grokPatternPath);
            //"RMI TCP Connection(3)-192.168.2.103" daemon prio=5 tid=0x000000011d070000 nid=0xa40f runnable [0x00000001255d4000]
            String grokPattern = "%{QUOTEDSTRING:threadname}( %{WORD:deamon})? prio=%{NUMBER:prio} tid=%{NOTSPACE:tid} nid=%{NOTSPACE:nid}";
            grok.compile(grokPattern);

            Match gm = grok.match(thread);
            gm.captures();
            Map<String, Object> m = gm.toMap();
            for (Map.Entry<String, Object> entry : m.entrySet()) {
                Object value = entry.getValue();
                executer.field(entry.getKey(), String.valueOf(value));
            }
            String nid = (String) m.get("nid");
            nid = nid.replaceFirst("0x", "");
            Integer outputDecimal = Integer.parseInt(nid, 16);
            executer.field("threadIdDecimal", String.valueOf(outputDecimal));

        } catch (GrokException e) {
            throw new RuntimeException(e);
        }


		executer.field("thread", thread);
	}

    void endDocument() {
        executer.field("runId", runId);
        executer.field("description", currentDescription);
        executer.field("date", currentDate);
        executer.field("fileName", currentFileName);
        executer.field("directory", currentDirectory);

        executer.field("position", String.valueOf(position));
        position++;
        executer.endDocument();
    }
	
	

}
