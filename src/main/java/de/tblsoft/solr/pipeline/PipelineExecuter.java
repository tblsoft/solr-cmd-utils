package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
import de.tblsoft.solr.compare.SolrCompareFilter;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.bean.Pipeline;
import de.tblsoft.solr.pipeline.bean.Processor;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.filter.*;
import de.tblsoft.solr.pipeline.nlp.squad.SquadReader;
import de.tblsoft.solr.pipeline.nlp.squad.SquadWriter;
import de.tblsoft.solr.pipeline.processor.DownloadResourcesProcessor;
import de.tblsoft.solr.pipeline.processor.Json2SingleDocumentsProcessor;
import de.tblsoft.solr.pipeline.processor.NoopProcessor;
import de.tblsoft.solr.pipeline.processor.OpenNLPPosTaggerTrainProcessor;
import de.tblsoft.solr.pipeline.processor.QSFDataRepositoryUploadProcessor;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by tblsoft on 23.01.16.
 */
public class PipelineExecuter {

    private static Logger LOG = LoggerFactory.getLogger(PipelineExecuter.class);

    private Pipeline pipeline;

    private String webHookStart;

    private String webHookEnd;

    private String webHookError;

    private List<ProcessorIF> preProcessorList;

    private List<FilterIF> filterList;

    private List<ProcessorIF> postProcessorList;

    private ReaderIF reader;

    private String yamlFileName;
    
    private Map<String, String> pipelineVariables = new HashMap<>();

    private String processId;

    private Long expectedDocumentCount = -1L;

    private static Map<String, Class> classRegestriy = new HashMap<String, Class>();
    static {
        classRegestriy.put("solrcmdutils.DownloadResourcesProcessor", DownloadResourcesProcessor.class);
        classRegestriy.put("solrcmdutils.StandardReader", StandardReader.class);
        classRegestriy.put("solrcmdutils.QSFDataReader", QSFDataReader.class);
        classRegestriy.put("solrcmdutils.RandomReader", RandomReader.class);
        classRegestriy.put("solrcmdutils.GrokReader", GrokReader.class);
        classRegestriy.put("solrcmdutils.GCLogReader", GCLogReader.class);
        classRegestriy.put("solrcmdutils.JsonPathReader", JsonPathReader.class);
        classRegestriy.put("solrcmdutils.JsonReader", JsonReader.class);
        classRegestriy.put("solrcmdutils.ElasticJsonPathReader", ElasticJsonPathReader.class);
        classRegestriy.put("solrcmdutils.ElasticReader", ElasticReader.class);
        classRegestriy.put("solrcmdutils.XmlReader", XmlReader.class);
        classRegestriy.put("solrcmdutils.XmlSitemapReader", XmlSitemapReader.class);
        classRegestriy.put("solrcmdutils.XmlSitemapWriter", XmlSitemapWriter.class);
        classRegestriy.put("solrcmdutils.HttpFilter", HttpFilter.class);
        classRegestriy.put("solrcmdutils.SplashFilter", SplashFilter.class);
        classRegestriy.put("solrcmdutils.HtmlJsoupFilter", HtmlJsoupFilter.class);
        classRegestriy.put("solrcmdutils.HtmlFilter", HtmlFilter.class);
        classRegestriy.put("solrcmdutils.EntityExtractionFilter", EntityExtractionFilter.class);
        classRegestriy.put("solrcmdutils.ValidationFilter", ValidationFilter.class);
        classRegestriy.put("solrcmdutils.OpenThesaurusReader", OpenThesaurusReader.class);
        classRegestriy.put("solrcmdutils.DictionaryNormalizationFilter", DictionaryNormalizationFilter.class);
        classRegestriy.put("solrcmdutils.ThreadDumpReader", ThreadDumpReader.class);
        classRegestriy.put("solrcmdutils.DateFilter", DateFilter.class);
        classRegestriy.put("solrcmdutils.ProcessingTimeFilter", ProcessingTimeFilter.class);
        classRegestriy.put("solrcmdutils.UrlSplitter", UrlSplitter.class);
        classRegestriy.put("solrcmdutils.GrepFilter", GrepFilter.class);
        classRegestriy.put("solrcmdutils.KeyValueSplitterFilter", KeyValueSplitterFilter.class);
        classRegestriy.put("solrcmdutils.FieldJoiner", FieldJoiner.class);
        classRegestriy.put("solrcmdutils.JsonWriter", JsonWriter.class);
        classRegestriy.put("solrcmdutils.ElasticWriter", ElasticWriter.class);
        classRegestriy.put("solrcmdutils.CSVReader", CSVReader.class);
        classRegestriy.put("solrcmdutils.JdbcReader", JdbcReader.class);
        classRegestriy.put("solrcmdutils.SolrQueryLogReader", SolrQueryLogReader.class);
        classRegestriy.put("solrcmdutils.SpyFilter", SpyFilter.class);
        classRegestriy.put("solrcmdutils.StatusFilter", StatusFilter.class);
        classRegestriy.put("solrcmdutils.StatusTimeFilter", StatusTimeFilter.class);
        classRegestriy.put("solrcmdutils.ExpectedDocumentCountFilter", ExpectedDocumentCountFilter.class);
        classRegestriy.put("solrcmdutils.StatisticFilter", StatisticFilter.class);
        classRegestriy.put("solrcmdutils.MappingFilter", MappingFilter.class);
        classRegestriy.put("solrcmdutils.ValueMappingFilter", ValueMappingFilter.class);
        classRegestriy.put("solrcmdutils.EncodingCorrectionFilter", EncodingCorrectionFilter.class);
        classRegestriy.put("solrcmdutils.RegexSplitFilter", RegexSplitFilter.class);
        classRegestriy.put("solrcmdutils.RegexFindFilter", RegexFindFilter.class);
        classRegestriy.put("solrcmdutils.FieldSplitter", FieldSplitter.class);
        classRegestriy.put("solrcmdutils.Multivalue2DocumentFilter", Multivalue2DocumentFilter.class);
        classRegestriy.put("solrcmdutils.IgnoreDocumentFilter", IgnoreDocumentFilter.class);
        classRegestriy.put("solrcmdutils.BeanShellFilter", BeanShellFilter.class);
        classRegestriy.put("solrcmdutils.LookupFilter", LookupFilter.class);
        classRegestriy.put("solrcmdutils.TokenCounterFilter", TokenCounterFilter.class);
        classRegestriy.put("solrcmdutils.TokenizerFilter", TokenizerFilter.class);
        classRegestriy.put("solrcmdutils.LowercaseFilter", LowercaseFilter.class);
        classRegestriy.put("solrcmdutils.ShingleFilter", ShingleFilter.class);
        classRegestriy.put("solrcmdutils.RemoveHtmlFilter", RemoveHtmlFilter.class);
        classRegestriy.put("solrcmdutils.CharCounterFilter", CharCounterFilter.class);
        classRegestriy.put("solrcmdutils.CompoundWordFilter", CompoundWordFilter.class);
        classRegestriy.put("solrcmdutils.LinkCheckerFilter", LinkCheckerFilter.class);
        classRegestriy.put("solrcmdutils.SolrFeeder", SolrFeeder.class);
        classRegestriy.put("solrcmdutils.SolrNumFoundFilter", SolrNumFoundFilter.class);
        classRegestriy.put("solrcmdutils.SolrCompareFilter", SolrCompareFilter.class);
        classRegestriy.put("solrcmdutils.SystemOutWriter", SystemOutWriter.class);
        classRegestriy.put("solrcmdutils.NounExtractorFilter", NounExtractorFilter.class);
        classRegestriy.put("solrcmdutils.FileLineWriter", FileLineWriter.class);
        classRegestriy.put("solrcmdutils.FilelineReader", FilelineReader.class);
        classRegestriy.put("solrcmdutils.CSVWriter", CSVWriter.class);
        classRegestriy.put("solrcmdutils.N3Writer", N3Writer.class);
        classRegestriy.put("solrcmdutils.KafkaWriter", KafkaWriter.class);
        classRegestriy.put("solrcmdutils.TestingFilter", TestingFilter.class);
        classRegestriy.put("solrcmdutils.NoopFilter", NoopFilter.class);
        classRegestriy.put("solrcmdutils.DuplicateRemovalFilter", DuplicateRemovalFilter.class);
        classRegestriy.put("solrcmdutils.ForkDocumentFilter", ForkDocumentFilter.class);
        classRegestriy.put("solrcmdutils.NetbaseReader", NetbaseReader.class);
        classRegestriy.put("solrcmdutils.BlacklistTopicFilter", BlacklistTopicFilter.class);
        classRegestriy.put("solrcmdutils.BlacklistFieldFilter", BlacklistFieldFilter.class);
        classRegestriy.put("solrcmdutils.WhitelistTopicTermsFilter", WhitelistTopicTermsFilter.class);
        classRegestriy.put("solrcmdutils.TopicMergeFilter", TopicMergeFilter.class);
        classRegestriy.put("solrcmdutils.TopicAggregationFilter", TopicAggregationFilter.class);
        classRegestriy.put("solrcmdutils.AggregationCountFilter", AggregationCountFilter.class);
        classRegestriy.put("solrcmdutils.ElasticdumpFileWriter", ElasticdumpFileWriter.class);
        classRegestriy.put("solrcmdutils.DocumentGeneratorReader", DocumentGeneratorReader.class);
        classRegestriy.put("solrcmdutils.JavaScriptFilter", JavaScriptFilter.class);
        classRegestriy.put("solrcmdutils.RichJavaScriptFilter", RichJavaScriptFilter.class);
        classRegestriy.put("solrcmdutils.SimpleGenderFilter", SimpleGenderFilter.class);
        classRegestriy.put("solrcmdutils.RegexReplaceFilter", RegexReplaceFilter.class);
        classRegestriy.put("solrcmdutils.EmptyFieldDocumentFilter", EmptyFieldDocumentFilter.class);
        classRegestriy.put("solrcmdutils.EmptyArrayValuesFilter", EmptyArrayValuesFilter.class);
        classRegestriy.put("solrcmdutils.FileMathFilter", FileMathFilter.class);
        classRegestriy.put("solrcmdutils.RoundNumberFilter", RoundNumberFilter.class);
        classRegestriy.put("solrcmdutils.LibSvmWriter", LibSvmWriter.class);
        classRegestriy.put("solrcmdutils.ElasticdumpJsonReader", ElasticdumpJsonReader.class);
        classRegestriy.put("solrcmdutils.RemoveFieldFilter", RemoveFieldFilter.class);
        classRegestriy.put("solrcmdutils.MessageDigestFilter", MessageDigestFilter.class);
        classRegestriy.put("solrcmdutils.RandomStaticValueFilter", RandomStaticValueFilter.class);
        classRegestriy.put("solrcmdutils.SleepFilter", SleepFilter.class);
        classRegestriy.put("solrcmdutils.OffsetPermutationFilter", OffsetPermutationFilter.class);
        classRegestriy.put("solrcmdutils.TokenPermutationFilter", TokenPermutationFilter.class);
        classRegestriy.put("solrcmdutils.RestFilter", RestFilter.class);
        classRegestriy.put("solrcmdutils.Json2SingleDocumentsProcessor", Json2SingleDocumentsProcessor.class);
        classRegestriy.put("solrcmdutils.StopwordFilter", StopwordFilter.class);
        classRegestriy.put("solrcmdutils.NoopProcessor", NoopProcessor.class);
        classRegestriy.put("solrcmdutils.QSFDataRepositoryUploadProcessor", QSFDataRepositoryUploadProcessor.class);
        classRegestriy.put("solrcmdutils.HtmlTextExtractorFilter", HtmlTextExtractorFilter.class);
        classRegestriy.put("solrcmdutils.SquadWriter", SquadWriter.class);
        classRegestriy.put("solrcmdutils.SquadReader", SquadReader.class);
        classRegestriy.put("solrcmdutils.OpenNlpPosTagFilter", OpenNlpPosTagFilter.class);
        classRegestriy.put("solrcmdutils.OpenNlpPosTagWriteTrainingFilter", OpenNlpPosTagWriteTrainingFilter.class);
        classRegestriy.put("solrcmdutils.OpenNLPPosTaggerTrainProcessor", OpenNLPPosTaggerTrainProcessor.class);
        classRegestriy.put("solrcmdutils.AddStaticValueFilter", AddStaticValueFilter.class);
        classRegestriy.put("solrcmdutils.ExcludeByValueFilter", ExcludeByValueFilter.class);
    }

    public PipelineExecuter(String yamlFileName) {
        this.yamlFileName = yamlFileName;
    }

    private String getBaseDirFromYamlFile() {
        if(yamlFileName.startsWith("http")) {
            try {
                URI uri = new URI(yamlFileName);
                return uri.resolve(".").toString();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        File f = new File(yamlFileName).getAbsoluteFile();
        return f.getParentFile().getAbsoluteFile().toString();

    }

    public void init() {
        LOG.debug("Read the pipeline configuration from the yaml file: {}", yamlFileName);
        try {
            pipeline = readPipelineFromYamlFile(yamlFileName);
            processId = pipeline.getProcessId();
            if(Strings.isNullOrEmpty(processId)) {
                processId = UUID.randomUUID().toString();
            }

            webHookStart = pipeline.getWebHookStart();
            webHookEnd = pipeline.getWebHookEnd();
            webHookError = pipeline.getWebHookError();

            HTTPHelper.webHook(webHookStart,
                    "status", "start",
                    "processId", processId);

            LOG.debug("processId {}", processId);

            LOG.debug("Default variables in the pipeline {}", pipeline.getVariables());
            LOG.debug("Configured variables in the pipeline {}", pipelineVariables);

            File settingsPropertiesFile = new File(FileUtils.getUserDirectory().getAbsolutePath() + "/.solr-cmd-utils/settings.properties");
            if(settingsPropertiesFile.exists()) {
                Properties prop = new Properties();
                prop.load(new FileInputStream(settingsPropertiesFile));
                Map<String, String> settings = new HashMap<>();
                prop.forEach((key, value) -> settings.put(key.toString(), value.toString()));
                pipeline.getVariables().putAll(settings);
            }

            pipeline.getVariables().putAll(pipelineVariables);
            LOG.debug("Effective variables in the pipeline {}", pipeline.getVariables());

            reader = createReaderInstance(pipeline.getReader(), getBaseDirFromYamlFile(), pipeline.getVariables(), this);

            preProcessorList = createProcessorInstanceList(pipeline.getPreProcessor());
            postProcessorList = createProcessorInstanceList(pipeline.getPostProcessor());

            filterList = createFilterInstanceList();

            createFilterInstanceList();
        } catch (Exception e) {
        	e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private  List<FilterIF> createFilterInstanceList() {
        List<FilterIF> filterInstanceList = new ArrayList<>();

        if(pipeline.getFilter() == null) {
            return filterInstanceList;
        }
        FilterIF lastFilter = null;
        FilterIF filterInstance = null;
        for(Filter filter : pipeline.getFilter()) {
            if(filter.getDisabled() != null && filter.getDisabled()) {
                continue;
            }
            filterInstance = createFilterInstance(filter);
            filterInstance.setBaseDir(getBaseDirFromYamlFile());
            filterInstance.setVariables(pipeline.getVariables());
            filterInstance.setPipelineExecuter(this);
            if(lastFilter == null) {
                lastFilter = filterInstance;
                continue;
            }
            lastFilter.setNextFilter(filterInstance);
            filterInstanceList.add(lastFilter);
            lastFilter = filterInstance;
        }
        filterInstance.setNextFilter(new LastFilter());
        filterInstanceList.add(filterInstance);

        return filterInstanceList;
    }

    public static ReaderIF createReaderInstance(Reader reader, String baseDir, Map<String, String> variables, PipelineExecuter pipelineExecuter) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(reader == null) {
            return null;
        }

        ReaderIF readerInstance = (ReaderIF) getInstance(reader.getClazz());
        readerInstance.setPipelineExecuter(pipelineExecuter);
        readerInstance.setReader(reader);
        readerInstance.setBaseDir(baseDir);
        readerInstance.setVariables(variables);
        return readerInstance;
    }

    private List<ProcessorIF> createProcessorInstanceList(List<Processor> processorList) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<ProcessorIF> processorInstanceList = new ArrayList<>();
        if(processorList == null) {
            return processorInstanceList;
        }

        for(Processor processor : processorList) {
            if(processor.getDisabled()) {
                continue;
            }
            ProcessorIF processorInstance = (ProcessorIF) getInstance(processor.getClazz());
            processorInstance.setProcessor(processor);
            processorInstance.setVariables(pipeline.getVariables());
            processorInstance.setBaseDir(getBaseDirFromYamlFile());
            processorInstanceList.add(processorInstance);

        }
        return processorInstanceList;
    }

    public static Object getInstance(String clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazzClass = classRegestriy.get(clazz);
        if(clazzClass != null) {
            return Class.forName(clazzClass.getName()).newInstance();
        }
        return Class.forName(clazz).newInstance();
    }

    public static FilterIF createFilterInstance(Filter filter) {
        try {
            String filterClazz = filter.getClazz();
            FilterIF filterInstance = (FilterIF) getInstance(filterClazz);
            filterInstance.setFilterConfig(filter);
            return filterInstance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static void execute(String yamlFileName) {
        PipelineExecuter pipelineExecuter = new PipelineExecuter(yamlFileName);
        pipelineExecuter.execute();
    }

    public void execute() {
        try {
            LOG.debug("Start the initialization.");
            init();


            LOG.debug("Process the pre processors.");
            for (ProcessorIF processorIF : preProcessorList) {
                processorIF.process();
            }

            if (reader != null) {
                LOG.debug("Start the initialization for all filters.");
                if (filterList.size() > 0) {
                    filterList.get(0).init();
                }
                LOG.debug("Read the input from the configured reader.");
                reader.read();
                LOG.debug("Finalize the pipeline.");
                end();
            }


            LOG.debug("Process the post processors.");
            for (ProcessorIF processorIF : postProcessorList) {
                processorIF.process();
            }


            HTTPHelper.webHook(webHookEnd,
                    "status", "end",
                    "processId", processId);
        } catch (Exception e) {
            onWebhookError(e);
            throw e;
        }
    }

    private void onWebhookError(Exception exception) {
        if(!Strings.isNullOrEmpty(webHookError)) {
            try {
                String exceptionString = ExceptionUtils.getStackTrace(exception);
                HTTPHelper.post(webHookError, exceptionString);
            } catch (Exception e) {
                LOG.error("Could not call the error webHookError {} because {}", webHookError, e.getMessage(), e );
                // fail silent
            }
        }

    }

    //public void field(String name, String value) {
    //    filterList.get(0).field(name, value);
    //}

    public void document(Document document) {
        filterList.get(0).document(document);
    }


    public void end() {
        reader.end();
        filterList.get(0).end();
    }

    public static Pipeline readPipelineFromYamlFile(String fileName) {
        try {
            Yaml yaml = new Yaml(new Constructor(Pipeline.class));
            String pipelineString = IOUtils.getString(fileName);
            LOG.info("pipeline:\n" + pipelineString);
            Pipeline pipeline = yaml.load(pipelineString);
            return pipeline;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FilterIF> getFilterList() {
        return filterList;
    }

    public FilterIF getFilterById(String filterId) {
        for(FilterIF filter : getFilterList()) {
            if(filterId.equals(filter.getId())) {
                return filter;
            }
        }
        throw new IllegalArgumentException("The filter with the id: " + filterId + " does not exists.");
    }

    public ReaderIF getReader() {
        return this.reader;
    }

	public Map<String, String> getPipelineVariables() {
		return pipelineVariables;
	}

	public void setPipelineVariables(Map<String, String> pipelineVariables) {
		this.pipelineVariables = pipelineVariables;
	}
	
	public void addPipelineVariable(String name, String value) {
		this.pipelineVariables.put(name, value);
	}

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Getter for property 'expectedDocumentCount'.
     *
     * @return Value for property 'expectedDocumentCount'.
     */
    public Long getExpectedDocumentCount() {
        return expectedDocumentCount;
    }

    /**
     * Setter for property 'expectedDocumentCount'.
     *
     * @param expectedDocumentCount Value to set for property 'expectedDocumentCount'.
     */
    public void setExpectedDocumentCount(Long expectedDocumentCount) {
        this.expectedDocumentCount = expectedDocumentCount;
    }
}
