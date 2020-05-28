package de.tblsoft.solr.pipeline.processor;

import de.tblsoft.solr.pipeline.AbstractProcessor;
import opennlp.tools.postag.*;
import opennlp.tools.util.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class OpenNLPPosTaggerTrainProcessor extends AbstractProcessor {

    private  String trainingInputFile;
    private  String modelOutputfile;
    private  String trainingInputFileCharSet;
    private  String languageCode;


    @Override
    public void process() {

        trainingInputFile = getProperty("trainingInputFile", "train.txt");
        modelOutputfile = getProperty("modelOutputfile", "model.bin");
        trainingInputFileCharSet = getProperty("trainingInputFileCharSet", "UTF-8");
        languageCode = getProperty("languageCode", "de");


        train();
    }


    private void train() {

        try {
            InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(getBaseDir() + "/" + trainingInputFile));
            ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, trainingInputFileCharSet);
            ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
            POSModel model = POSTaggerME.train(languageCode, sampleStream, TrainingParameters.defaultParams(), new POSTaggerFactory());

            OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(getBaseDir() + "/" + modelOutputfile));
            model.serialize(modelOut);
            modelOut.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
