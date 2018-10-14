package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LineProcessor extends AbstractProcessor {

    private Long currentLine = 0L;

    private Long skipLines = 1L;

    private String charset = StandardCharsets.UTF_8.name();

    private String absoluteInputFilename;

    private String absoluteOutputFilename;


    @Override
    public void process() {

        try {

            init();

            InputStream in = IOUtils.getInputStream(absoluteInputFilename);
            Reader reader = new InputStreamReader(in, charset);


            OutputStream out = IOUtils.getOutputStream(absoluteOutputFilename);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset));

            BufferedReader br = new BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                currentLine++;

                String processedLine = processLine(line);
                if(processedLine != null) {
                    writer.write(line);
                    writer.newLine();
                }

            }
            reader.close();
            writer.close();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void init() {
        charset = getProperty("charset", charset);
        skipLines = getPropertyAsInteger("skipLines", skipLines);
        String inputFilename = getProperty("inputFilename", null);
        absoluteInputFilename = IOUtils.getAbsoluteFile(getBaseDir(), inputFilename);

        String outputFilename = getProperty("outputFilename", null);
        absoluteOutputFilename = IOUtils.getAbsoluteFile(getBaseDir(), outputFilename);

    }

    public String processLine(String line) {
        return line;
    }

    public Long getCurrentLine() {
        return currentLine;
    }

    public Long getSkipLines() {
        return skipLines;
    }

    public String getCharset() {
        return charset;
    }

    public String getAbsoluteInputFilename() {
        return absoluteInputFilename;
    }

    public String getAbsoluteOutputFilename() {
        return absoluteOutputFilename;
    }
}
