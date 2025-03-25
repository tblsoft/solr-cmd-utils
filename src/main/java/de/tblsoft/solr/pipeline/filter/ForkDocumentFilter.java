package de.tblsoft.solr.pipeline.filter;

import com.quasiris.qsf.commons.util.YamlFactory;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.bean.FiltersPipeline;
import de.tblsoft.solr.util.IOUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Fork a document and run in separate filter pipeline
 */
public class ForkDocumentFilter extends AbstractFilter {
    List<FilterIF> filterList = new ArrayList<FilterIF>();

    @Override
    public void init() {
        initPipelineFilters();

        super.init();
    }

    @Override
    public void document(Document document) {
        filterList.get(0).document(document);

        super.document(document);
    }

    @Override
    public void end() {
        filterList.get(0).end();

        super.end();
    }

    protected void initPipelineFilters() {
        String includeFilters = getProperty("include", null);
        String absoluteFile = IOUtils.getAbsoluteFile(getBaseDir(), includeFilters);
        FiltersPipeline filtersPipeline = readFiltersFromYamlFile(absoluteFile);

        // init
        FilterIF lastFilter = null;
        FilterIF filterInstance = null;

        try {
            for(int i = 0; i < filtersPipeline.getFilter().size(); i++) {
                Filter filter = filtersPipeline.getFilter().get(i);

                if(filter.getDisabled() != null && filter.getDisabled()) {
                    continue;
                }
                filterInstance = PipelineExecuter.createFilterInstance(filter);

                filterInstance.setBaseDir(getBaseDir());
                filterInstance.setVariables(variables);
                if(lastFilter == null) {
                    lastFilter = filterInstance;
                    continue;
                }
                lastFilter.setNextFilter(filterInstance);
                filterList.add(lastFilter);
                lastFilter = filterInstance;
            }
            filterInstance.setNextFilter(new LastFilter());
            filterList.add(filterInstance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        filterList.get(0).init();
    }

    protected FiltersPipeline readFiltersFromYamlFile(String fileName) {
        try {
            InputStream input = new FileInputStream(new File(fileName));
            Yaml yaml = new Yaml(new Constructor(FiltersPipeline.class, YamlFactory.createAllowAllLoadOptions()));
            FiltersPipeline filters = (FiltersPipeline) yaml.load(input);

            input.close();
            return filters;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
