package de.tblsoft.solr.pipeline.filter;

import com.quasiris.qsf.commons.ai.ModelRepositoryManager;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 25.4.20.
 *
 * - input: arbitrary document
 * - output: document for each sentence
 * - every document contains the values:
 *   - sentence
 *   - tokens
 *   - posTags
 *
 * - get evey value of a document
 * - do a sentence detection
 * - for every sentence do a tokenizing
 * - for every sentence do a pos tagging
 *
 */
public class OpenNlpPosTagFilter extends AbstractFilter {


    private String sentenceDetectorModel;
    private String tokenizerModel;
    private String posTaggerModel;

    private String modelBaseUrl;
    private String modelBasePath;

    private SentenceDetectorME sentenceDetector;
    private POSTaggerME posTagger;
    private Tokenizer tokenizer;

    @Override
    public void init() {
        sentenceDetectorModel = getProperty("sentenceDetectorModel", "org.apache.opennlp|de-sent|1.5|de-sent.bin");
        tokenizerModel = getProperty("tokenizerModel", "org.apache.opennlp|de-token|1.5|de-token.bin");
        posTaggerModel = getProperty("posTaggerModel", "org.apache.opennlp|de-pos-maxent|1.5|de-pos-maxent.bin");
        modelBaseUrl = getProperty("modelBaseUrl", null);
        modelBasePath = getProperty("modelBasePath", null);

        try {
            this.sentenceDetector = initSentenceDetector();
            this.posTagger = initPosTagger();
            this.tokenizer = initTokenizer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.init();
    }


    private InputStream getModelByShortId(String shortId) throws FileNotFoundException {
        ModelRepositoryManager loader = ModelRepositoryManager.Builder.create().
                groupId("").artifactId("").version("").
                shortId(shortId).
                modelBasePath(modelBasePath).
                modelBaseUrl(modelBaseUrl).
                build();

        String fileName = shortId.split(Pattern.quote("|")) [3];
        return loader.load(fileName);
    }

    private Tokenizer initTokenizer() throws IOException {
        InputStream modelStream = getModelByShortId(tokenizerModel);
        TokenizerModel model = new TokenizerModel(modelStream);
        Tokenizer tokenizer = new TokenizerME(model);
        modelStream.close();
        return tokenizer;
    }

    private POSTaggerME initPosTagger() throws IOException {
        InputStream modelStream = getModelByShortId(posTaggerModel);
        POSModel model = new POSModel(modelStream);
        POSTaggerME posTagger = new POSTaggerME(model);
        modelStream.close();
        return posTagger;
    }


    private SentenceDetectorME initSentenceDetector() throws IOException {
        InputStream modelStream = getModelByShortId(sentenceDetectorModel);
        SentenceModel model = new SentenceModel(modelStream);
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
        modelStream.close();
        return sentenceDetector;
    }


    @Override
    public void document(Document document) {
        for(Field field : document.getFields()) {
            for(String text : field.getValues()) {
                String[] sentences = sentenceDetector.sentDetect(text);
                for(String sentence : sentences) {
                    Document newDocument = new Document();
                    newDocument.setField("sentence", sentence);
                    String[] tokens = tokenizer.tokenize(sentence);
                    String[] posTags = posTagger.tag(tokens);
                    newDocument.setField("tokens", Arrays.asList(tokens));
                    newDocument.setField("posTags", Arrays.asList(posTags));
                    super.document(newDocument);
                }
            }
        }

    }
    
}
