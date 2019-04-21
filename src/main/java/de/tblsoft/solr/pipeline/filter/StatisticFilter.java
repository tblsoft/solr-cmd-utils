package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.schema.SolrSchemaManager;
import de.tblsoft.solr.util.DatatypeUtils;
import de.tblsoft.solr.util.PairBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by tblsoft 18.05.17.
 */
public class StatisticFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(StatisticFilter.class);


    private long documentCount = 0;
    private Map<String,FieldStatistic> fieldStatisticMap = new HashMap<String, FieldStatistic>();

    private List<Pair<String, String>> dynamicFields = new ArrayList<Pair<String, String>>();


    @Override
    public void init() {
        super.init();
        initDynamicFields();

    }


    private void initDynamicFields() {
        List<String> dynamicFieldList = getPropertyAsList("dynamicFields", new ArrayList<String>());
        for (String value: dynamicFieldList) {
            dynamicFields.add(PairBuilder.createPair(value, "=>"));
        }
    }

    @Override
    public void document(Document document) {
        for(Field field : document.getFields()) {
            for(Pair<String, String> dynamicField: dynamicFields) {
                if(field.getName().matches(dynamicField.getLeft())) {
                    field.setName(dynamicField.getRight());
                }
            }

            FieldStatistic fieldStatistic = fieldStatisticMap.get(field.getName());
            if(fieldStatistic == null) {
                fieldStatistic = new FieldStatistic(field.getName());
                fieldStatisticMap.put(field.getName(), fieldStatistic);
            }
            fieldStatistic.maxValueCount(field.getValues().size());
            for(String value: field.getValues()) {
                fieldStatistic.processValue(value);
            }
        }

        documentCount++;
        super.document(document);

    }

    @Override
    public void end() {
        LOG.info("number of docs: " + documentCount);
        LOG.info("number of fields: " + fieldStatisticMap.size());
        LOG.info("");
        LOG.info("");


        System.out.printf("%40s %5s %5s %10s %100s", "field name", "mv", "count", "type", "example values");
        LOG.info("");
        SolrSchemaManager solrSchemaManager = new SolrSchemaManager("http://localhost:8983/solr/bar");
        try {

            //solrSchemaManager.deleteAllFields();

            for(Map.Entry<String,FieldStatistic> fieldStatisticEntry: fieldStatisticMap.entrySet()) {
                FieldStatistic fieldStatistic = fieldStatisticEntry.getValue();
                StringBuilder exampleValueBuilder = new StringBuilder();
                for(String value : fieldStatistic.getExampleValues()) {
                    exampleValueBuilder.append(StringUtils.abbreviate(value, 20)).append(", ");
                }

                //solrSchemaManager.addField(fieldStatistic.getFieldName(), fieldStatistic.getDataType(), fieldStatistic.isMultiValue());


                System.out.printf("%40s %5s %5s %10s %100s",
                        fieldStatistic.getFieldName(),
                        fieldStatistic.isMultiValue(),
                        fieldStatistic.getMaxValueCount(),
                        fieldStatistic.getDataType(),
                        exampleValueBuilder.toString()
                );
                LOG.info("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.end();
    }

    class FieldStatistic {
        private String fieldName;
        private Set<String> exampleValues = new HashSet<String>();
        private String min;
        private String max;
        private String minLength;
        private String maxLength;
        private int maxValueCount = 0;
        private String dataType;


        public FieldStatistic(String fieldName) {
            this.fieldName = fieldName;
        }

        public void processValue(String value) {
            min(value);
            max(value);
            dataType(value);
            exampleValue(value);
        }

        public void exampleValue(String value) {
            this.exampleValues.add(value);
            if(this.exampleValues.size() > 5) {
                String valueToRemove = this.exampleValues.iterator().next();
                this.exampleValues.remove(valueToRemove);

            }

        }



        public void dataType(String value) {
            if(StringUtils.isEmpty(value)) {
                return;
            }
            String currentDatatype = DatatypeUtils.estimateDatatype(value);
            if(this.dataType == null) {
                this.dataType = currentDatatype;
                return;
            }
            this.dataType = DatatypeUtils.getBestDatatype(currentDatatype, this.dataType);

        }



        public void min(String min) {
            if(min == null) {
                return;
            }
            if(this.min == null) {
                this.min = min;
                return;
            }
            if(this.min.compareTo(min) < 1) {
                this.min = min;
            }
         }

        public void max(String max) {
            if(max == null) {
                return;
            }
            if(this.max == null) {
                this.max = max;
                return;
            }
            if(this.max.compareTo(max) > 1) {
                this.max = max;
            }
        }

        public void maxValueCount(int valueCount) {
           if(valueCount > this.maxValueCount) {
               this.maxValueCount = valueCount;
           }
        }


        public boolean isMultiValue() {
            return this.maxValueCount > 1;
        }
        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Set<String> getExampleValues() {
            return exampleValues;
        }

        public void setExampleValues(Set<String> exampleValues) {
            this.exampleValues = exampleValues;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMinLength() {
            return minLength;
        }

        public void setMinLength(String minLength) {
            this.minLength = minLength;
        }

        public String getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(String maxLength) {
            this.maxLength = maxLength;
        }

        public int getMaxValueCount() {
            return maxValueCount;
        }

        public void setMaxValueCount(int maxValueCount) {
            this.maxValueCount = maxValueCount;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }
}
