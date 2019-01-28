package de.tblsoft.solr.pipeline.processor;

import com.google.common.io.CharSink;
import com.google.common.io.Files;
import de.tblsoft.solr.pipeline.AbstractProcessor;
import de.tblsoft.solr.util.IOUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ListDiffProcessor extends AbstractProcessor {

    private String charset = StandardCharsets.UTF_8.name();

    private String absoluteOutputFilename;

    private List<String> inputFiles;
    private List<Function> functions = new ArrayList<>();


    /**
     * disjunction
     * intersection
     */
    private String mode = "intersection";


    @Override
    public void process() {

        try {

            init();



            functions.add(new DistinctFunction());

            List<String> outputList = null;

            for(String inputFile: inputFiles) {
                String absoluteInputFile = IOUtils.getAbsoluteFile(getBaseDir(), inputFile);
                List<String> lines = Files.readLines(new File(absoluteInputFile), Charset.forName(charset));
                Stream<String> stream = lines.stream();

                for(Function function : functions) {
                    if(function instanceof DistinctFunction) {
                        stream = stream.distinct();
                    } else {
                        stream = stream.map(function);
                    }

                }


                if(outputList == null) {
                    outputList = lines;
                } else {
                    if("intersection".equals(mode)) {
                        outputList.retainAll(lines);
                    } else if("disjunction".equals(mode)) {
                        outputList.removeAll(lines);
                    }


                }

            }
            CharSink sink = Files.asCharSink(new File(absoluteOutputFilename), Charset.forName(charset));
            sink.writeLines(outputList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void init() {
        charset = getProperty("charset", charset);
        mode = getProperty("mode", mode);
        inputFiles = getPropertyAsList("inputFiles", new ArrayList<>());
        String outputFilename = getProperty("outputFilename", null);
        absoluteOutputFilename = IOUtils.getAbsoluteFile(getBaseDir(), outputFilename);

    }

    public String getCharset() {
        return charset;
    }

    public String getAbsoluteOutputFilename() {
        return absoluteOutputFilename;
    }
}
