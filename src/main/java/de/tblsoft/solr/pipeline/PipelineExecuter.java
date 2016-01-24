package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.bean.Pipeline;
import de.tblsoft.solr.pipeline.filter.RegexSplitFilter;
import de.tblsoft.solr.pipeline.filter.SolrFeeder;
import de.tblsoft.solr.pipeline.filter.SpyFilter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 23.01.16.
 */
public class PipelineExecuter {


    private Pipeline pipeline;

    private List<FilterIF> filterList;

    private ReaderIF reader;

    private String yamlFileName;

    private static Map<String, Class> classRegestriy = new HashMap<String, Class>();
    static {
        classRegestriy.put("solrcmdutils.StandardReader", StandardReader.class);
        classRegestriy.put("solrcmdutils.SpyFilter", SpyFilter.class);
        classRegestriy.put("solrcmdutils.RegexSplitFilter", RegexSplitFilter.class);
        classRegestriy.put("solrcmdutils.SolrFeeder", SolrFeeder.class);

    }

    public PipelineExecuter(String yamlFileName) {
        this.yamlFileName = yamlFileName;
    }

    public void init() {
        try {
            pipeline = readPipelineFromYamlFile(yamlFileName);
            reader = (ReaderIF) getInstance(pipeline.getReader().getClazz());
            reader.setPipelineExecuter(this);
            reader.setReader(pipeline.getReader());

            filterList = new ArrayList<FilterIF>();

            FilterIF lastFilter = null;
            FilterIF filterInstance = null;
            for(Filter filter :pipeline.getFilter()) {
                if(filter.getDisabled() != null && filter.getDisabled()) {
                    continue;
                }
                filterInstance = createFilterInstance(filter);
                if(lastFilter == null) {
                    lastFilter = filterInstance;
                    continue;
                }
                lastFilter.setNextFilter(filterInstance);
                filterList.add(lastFilter);
            }
            filterList.add(filterInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object getInstance(String clazz) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazzClass = classRegestriy.get(clazz);
        if(clazzClass != null) {
            return Class.forName(clazzClass.getName()).newInstance();
        }
        return Class.forName(clazz).newInstance();
    }

    private FilterIF createFilterInstance(Filter filter) throws Exception {
        String filterClazz = filter.getClazz();
        FilterIF filterInstance = (FilterIF) getInstance(filterClazz);
        filterInstance.setFilterConfig(filter);
        return filterInstance;
    }

    public void execute() {
        init();
        filterList.get(0).init();
        reader.read();
        end();
    }


    public void field(String name, String value) {
        filterList.get(0).field(name, value);
    }


    public void endDocument() {
        filterList.get(0).endDocument();

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

}
