package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Serialize doc to libsvm format.
 * Auto encode categorical fields with ordinal encoding technique.
 */
public class LibSvmWriter extends AbstractFilter {
    private String featmapInFilepath; // use existing feature map
    private String featmapOutFilepath; // output feature map with is required for prediction
    private String svmFilepath; // output path
    private String svmFieldLabel;
    private List<String> svmFields;

    private OutputStream svmOutputStream;
    private OutputStreamStringBuilder svmOutputStreamStringBuilder;

    Map<String, Integer> featmap;

    @Override
    public void init(){
        featmap = new LinkedHashMap<>();

        String relativeFeatmapInFilepath = getProperty("featmapInFilepath", null);
        if(relativeFeatmapInFilepath != null) {
            featmapInFilepath = IOUtils.getAbsoluteFile(getBaseDir(), relativeFeatmapInFilepath);
            try {
                featmap = loadFeatmap(featmapInFilepath);
            } catch (IOException e) {
                throw new RuntimeException("Could not load featmap!", e);
            }
        }

        String relativeFeatmapOutFilepath = getProperty("featmapOutFilepath", null);
        featmapOutFilepath = IOUtils.getAbsoluteFile(getBaseDir(), relativeFeatmapOutFilepath);
        verify(featmapOutFilepath, "A featmapOutFilepath must be defined!");

        svmFilepath = getProperty("svmFilepath", null);
        verify(svmFilepath, "svmFilepath must be defined!");

        svmFieldLabel = getProperty("svmFieldLabel", null);
        verify(svmFieldLabel, "svmFieldLabel must be defined!");

        svmFields = getPropertyAsList("svmFields", null);
        verify(svmFields, "svmFields must be defined!");

        try {
            svmOutputStream = IOUtils.getOutputStream(svmFilepath);
            svmOutputStreamStringBuilder = new OutputStreamStringBuilder(svmOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        super.init();
    }

    @Override
    public void document(Document document) {
        if(document != null) {
            String svmLine = serializeDocToSvm(document);
            svmOutputStreamStringBuilder.append(svmLine);
            svmOutputStreamStringBuilder.append("\n");
        }

        super.document(document);
    }

    @Override
    public void end() {
        try {
            svmOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("Error when closing svmOutputStream!", e);
        }

        try {
            saveFeatmap(featmapOutFilepath, featmap);
        } catch (IOException e) {
            throw new RuntimeException("Could not save featmap!", e);
        }

        super.end();
    }

    public String serializeDocToSvm(Document doc) {
        StringBuilder svmLineBuilder = new StringBuilder();

        String label = doc.getFieldValue(svmFieldLabel);
        svmLineBuilder.append(label);
        svmLineBuilder.append(" ");

        Integer featureIndex = 1;
        for (String svmField : svmFields) {
            Field featureField = doc.getField(svmField);
            String svmFeatureValue = getSvmFeatureValue(featureField, featureIndex);
            if(svmFeatureValue != null) {
                svmLineBuilder.append(svmFeatureValue);
                svmLineBuilder.append(" ");
            }

            featureIndex++;
        }

        return svmLineBuilder.toString().trim();
    }

    protected Integer updateFeatureMap(String feature) {
        Integer index = null;
        if(!featmap.containsKey(feature)) {
            index = featmap.size()+1;
            featmap.put(feature, index);
        }
        else {
            index = featmap.get(feature);
        }

        return index;
    }

    protected String getSvmFeatureValue(Field featureField, Integer featureIndex) {
        String featureValue = null;

        if(featureField.getValue() != null) {
            if(!StringUtils.isNumeric(featureField.getValue())) {
                String featureName = getFeatmapFeatureName(featureField.getName(), featureField.getValue());
                Integer encodedOrdinalValue = updateFeatureMap(featureName);
                featureValue = serializeSvmFeatureValue(featureIndex, encodedOrdinalValue);
            }
            else {
                featureValue = serializeSvmFeatureValue(featureIndex, featureField.getValue());
            }
        }

        return featureValue;
    }

    public static String serializeSvmFeatureValue(Integer index, Object value) {
        return index+":"+value;
//        return value+":1";
    }

    public static String getFeatmapFeatureName(String field, Object value) {
        String featureName = null;

        if(value != null && value instanceof String) {
            featureName = field+"."+value;
        }

        return featureName;
    }

    public static Map<String, Integer> loadFeatmap(String filepath) throws IOException {
        Map<String, Integer> featmap = new LinkedHashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            for(String line; (line = br.readLine()) != null; ) {
                String[] parts = line.split("\\s");
                if("i".equals(parts[2]) || "q".equals(parts[2])) {
                    int index = Integer.parseInt(parts[0]);
                    String feature = parts[1];
                    featmap.put(feature, index);
                }
            }
        }

        return featmap;
    }

    public static void saveFeatmap(String filepath, Map<String, Integer> featmap) throws IOException {
        try (Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8"))) {
            for (Map.Entry<String, Integer> entry : featmap.entrySet()) {
                String line = entry.getValue()+"\t"+entry.getKey()+"\ti\n";
                out.append(line);
            }
        }
    }
}
