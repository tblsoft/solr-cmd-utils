package de.tblsoft.solr.pipeline.filter;

import com.google.gson.Gson;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.Charsets;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by tblsoft 26.12.16.
 */
public class EntityExtractionFilter extends AbstractFilter {

    private List<String> fieldList;

    private String entityFieldName;

    List<Map<String,Entity>> dictionaryMap = new ArrayList<Map<String, Entity>>();

    @Override
    public void init() {

        fieldList = getPropertyAsList("fieldList", new ArrayList<String>());
        entityFieldName = getProperty("entityFieldName", "entities");

        readEntityDictionary("teams.csv", Charsets.UTF_8.name(), "team");
        readEntityDictionary("persons.csv", Charsets.UTF_8.name(),"person");

        super.init();

    }


    void readEntityDictionary(String filename, String charset, String type) {
        String absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(),filename);
        CSVFormat format = CSVFormat.RFC4180;
        format = format.withHeader();
        try {
            InputStream in = IOUtils.getInputStream(absoluteFilename);
            java.io.Reader reader = new InputStreamReader(in,charset);

            CSVParser parser = format.parse(reader);
            for(CSVRecord record :parser.getRecords()) {
                String entityName = record.get("name");
                String entityUrl = record.get("url");

                StringTokenizer tokenizer = new StringTokenizer(entityName);
                int tokenCount = 0;
                StringBuilder tokenPhrase = new StringBuilder();
                while(tokenizer.hasMoreTokens()) {

                    String token = tokenizer.nextToken();
                    if(dictionaryMap.size() < tokenCount+1) {
                        dictionaryMap.add(new HashMap<String, Entity>());
                    }
                    if(tokenCount > 0) {
                        tokenPhrase.append(" ");
                    }
                    tokenPhrase.append(token);

                    Map<String,Entity> entityMap = dictionaryMap.get(tokenCount);
                    Entity newEntity = new Entity(entityName, !tokenizer.hasMoreTokens());
                    newEntity.setUrl(entityUrl);
                    newEntity.setType(type);
                    String key = normalize(tokenPhrase.toString());
                    Entity entity = entityMap.get(key);
                    if(entity == null) {
                        entityMap.put(key, newEntity);
                    } else if(newEntity.getName().length() < entity.getName().length()) {
                        entityMap.put(key, newEntity);
                    }

                    tokenCount++;

                }

            }
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    String normalize(String token) {
        token = token.toLowerCase();
        //token = token.replaceAll("[_….;:\"(),'*+%°/\\[\\]?!]", "");
        token = token.replaceAll("[^a-zA-Z0-9äöüÄÖÜß]+", "");
        return token.trim();
    }

    @Override
    public void document(Document document) {
        List<Entity> entities = new ArrayList<Entity>();
        Collection<String> restWords = new HashSet<String>();
        for(String fieldName: fieldList) {
            List<String> fieldValues  =document.getFieldValues(fieldName);
            if(fieldValues == null) {
                continue;
            }
            for(String text : fieldValues) {
                entities.addAll(extractEntites(text));
            }
        }


        Set<Entity> fullMatchEntites = removeDuplicatesAndNotFullMatchEntities(entities);

        if(!fullMatchEntites.isEmpty()) {
            document.setField(entityFieldName + "Object", toJson(fullMatchEntites));
        }
        for(Entity entity: fullMatchEntites) {
            document.addField(entityFieldName, entity.getName());
        }


        super.document(document);



    }

    Set<Entity> removeDuplicatesAndNotFullMatchEntities(List<Entity> entities) {
        Set<Entity> fullMatchEntites = new HashSet<Entity>();
        for(Entity entity: entities) {
            if(entity.fullMatch) {
                fullMatchEntites.add(entity);
            }
        }
        return fullMatchEntites;
    }

    private String toJson(Set<Entity> entites) {
        Gson gson = new Gson();
        return gson.toJson(entites);
    }


    List<Entity> extractEntites(String text) {
        List<Entity> entities = new ArrayList<Entity>();
        StringTokenizer tokenizer = new StringTokenizer(text);
        int tokenCount = 0;
        StringBuilder tokenPhrase = new StringBuilder();
        Entity candidate = null;
        while(tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();



            if(dictionaryMap.size() < tokenCount+1) {
                if(candidate != null) {
                    entities.add(candidate);
                }

                tokenPhrase = new StringBuilder();
                candidate = null;
                tokenCount=0;
                continue;
            }

            if(tokenCount > 0) {
                tokenPhrase.append(" ");
            }
            tokenPhrase.append(token);


            Map<String,Entity> entityMap = dictionaryMap.get(tokenCount);
            Entity newCandidate = entityMap.get(normalize(tokenPhrase.toString()));
            if(newCandidate == null) {
                if(candidate != null) {
                    entities.add(candidate);
                }

                tokenPhrase = new StringBuilder();
                candidate = null;
                tokenCount=0;
                continue;
            }

            candidate = newCandidate;



            tokenCount++;

        }
        return entities;
    }

    class Entity {

        Entity(String name, boolean fullMatch) {
            this.name = name;
            this.fullMatch = fullMatch;
        }

        private String name;
        private boolean fullMatch;

        private String url;

        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            return this.url.hashCode();
        }
    }



}
