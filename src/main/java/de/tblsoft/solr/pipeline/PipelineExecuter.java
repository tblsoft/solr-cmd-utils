package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.compare.SolrCompareFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.bean.Pipeline;
import de.tblsoft.solr.pipeline.bean.Processor;
import de.tblsoft.solr.pipeline.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created by tblsoft on 23.01.16.
 */
public class PipelineExecuter {

    private static Logger LOG = LoggerFactory.getLogger(PipelineExecuter.class);



    private Pipeline pipeline;

    private List<ProcessorIF> preProcessorList;

    private List<FilterIF> filterList;

    private List<ProcessorIF> postProcessorList;

    private ReaderIF reader;

    private String yamlFileName;
    
    private Map<String, String> pipelineVariables = new HashMap<String, String>();

    private static Map<String, Class> classRegestriy = new HashMap<String, Class>();
    static {
        classRegestriy.put("solrcmdutils.StandardReader", StandardReader.class);
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
        classRegestriy.put("solrcmdutils.EntityExtractionFilter", EntityExtractionFilter.class);
        classRegestriy.put("solrcmdutils.ValidationFilter", ValidationFilter.class);
        classRegestriy.put("solrcmdutils.OpenThesaurusReader", OpenThesaurusReader.class);
        classRegestriy.put("solrcmdutils.DictionaryNormalizationFilter", DictionaryNormalizationFilter.class);
        classRegestriy.put("solrcmdutils.ThreadDumpReader", ThreadDumpReader.class);
        classRegestriy.put("solrcmdutils.DateFilter", DateFilter.class);
        classRegestriy.put("solrcmdutils.UrlSplitter", UrlSplitter.class);
        classRegestriy.put("solrcmdutils.GrepFilter", GrepFilter.class);
        classRegestriy.put("solrcmdutils.KeyValueSplitterFilter", KeyValueSplitterFilter.class);
        classRegestriy.put("solrcmdutils.FieldJoiner", FieldJoiner.class);
        classRegestriy.put("solrcmdutils.JsonWriter", JsonWriter.class);
        classRegestriy.put("solrcmdutils.ElasticWriter", ElasticWriter.class);
        classRegestriy.put("solrcmdutils.CSVReader", CSVReader.class);
        classRegestriy.put("solrcmdutils.SolrQueryLogReader", SolrQueryLogReader.class);
        classRegestriy.put("solrcmdutils.SpyFilter", SpyFilter.class);
        classRegestriy.put("solrcmdutils.StatusFilter", StatusFilter.class);
        classRegestriy.put("solrcmdutils.StatusTimeFilter", StatusTimeFilter.class);
        classRegestriy.put("solrcmdutils.StatisticFilter", StatisticFilter.class);
        classRegestriy.put("solrcmdutils.MappingFilter", MappingFilter.class);
        classRegestriy.put("solrcmdutils.EncodingCorrectionFilter", EncodingCorrectionFilter.class);
        classRegestriy.put("solrcmdutils.RegexSplitFilter", RegexSplitFilter.class);
        classRegestriy.put("solrcmdutils.FieldSplitter", FieldSplitter.class);
        classRegestriy.put("solrcmdutils.IgnoreDocumentFilter", IgnoreDocumentFilter.class);
        classRegestriy.put("solrcmdutils.BeanShellFilter", BeanShellFilter.class);
        classRegestriy.put("solrcmdutils.LookupFilter", LookupFilter.class);
        classRegestriy.put("solrcmdutils.TokenCounterFilter", TokenCounterFilter.class);
        classRegestriy.put("solrcmdutils.TokenizerFilter", TokenizerFilter.class);
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
        classRegestriy.put("solrcmdutils.ElasticdumpFileWriter", ElasticdumpFileWriter.class);
        classRegestriy.put("solrcmdutils.DocumentGeneratorReader", DocumentGeneratorReader.class);
        classRegestriy.put("solrcmdutils.JavaScriptFilter", JavaScriptFilter.class);
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
    }

    public PipelineExecuter(String yamlFileName) {
        this.yamlFileName = yamlFileName;
    }

    private String getBaseDirFromYamlFile() {
        File f = new File(yamlFileName).getAbsoluteFile();
        return f.getParentFile().getAbsoluteFile().toString();

    }

    public void init() {
        LOG.debug("Read the pipeline configuration from the yaml file: {}", yamlFileName);
        try {
            pipeline = readPipelineFromYamlFile(yamlFileName);

            LOG.debug("Default variables in the pipeline {}", pipeline.getVariables());
            LOG.debug("Configured variables in the pipeline {}", pipelineVariables);
            pipeline.getVariables().putAll(pipelineVariables);
            LOG.debug("Effective variables in the pipeline {}", pipeline.getVariables());

            createReaderInstance();

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

    private void createReaderInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if(pipeline.getReader() == null) {
            return;
        }

        reader = (ReaderIF) getInstance(pipeline.getReader().getClazz());
        reader.setPipelineExecuter(this);
        reader.setReader(pipeline.getReader());
        reader.setBaseDir(getBaseDirFromYamlFile());
        reader.setVariables(pipeline.getVariables());
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

    public void execute() {
        LOG.debug("Start the initialization.");
        init();


        LOG.debug("Process the pre processors.");
        for(ProcessorIF processorIF: preProcessorList) {
            processorIF.process();
        }

        if(reader != null) {
            LOG.debug("Start the initialization for all filters.");
            if(filterList.size() > 0 ) {
                filterList.get(0).init();
            }
            LOG.debug("Read the input from the configured reader.");
            reader.read();
            LOG.debug("Finalize the pipeline.");
            end();
        }


        LOG.debug("Process the pst processors.");
        for(ProcessorIF processorIF: postProcessorList) {
            processorIF.process();
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

    Pipeline readPipelineFromYamlFile(String fileName) {
        try {
            InputStream input = new FileInputStream(new File(
                    fileName));
            Yaml yaml = new Yaml(new Constructor(Pipeline.class));
            Pipeline pipeline = (Pipeline) yaml.load(input);

            input.close();
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
    
    
}
