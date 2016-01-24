package de.tblsoft.solr.bean;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Created by tblsoft
 *
 * A bean class for the solr.xml file
 */
public class Core {

    private String name;

    private String instanceDir;

    private String config;
    private String schema;
    private String transientAtt;
    private String dataDir;


    private Boolean loadOnStartup;

    public static Core createCore(String name) {
        return createCore(name,name);
    }

    public static Core createCore(String name, String instanceDir) {
        Core core = new Core();
        core.setName(name);
        core.setInstanceDir(instanceDir);
        return core;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getInstanceDir() {
        return instanceDir;
    }

    public void setInstanceDir(String instanceDir) {
        this.instanceDir = instanceDir;
    }

    @XmlAttribute
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @XmlAttribute
    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    @XmlAttribute(name="transient")
    public String getTransientAtt() {
        return transientAtt;
    }

    public void setTransientAtt(String transientAtt) {
        this.transientAtt = transientAtt;
    }

    @XmlAttribute
    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }
    @XmlAttribute
    public Boolean getLoadOnStartup() {
        return loadOnStartup;
    }

    public void setLoadOnStartup(Boolean loadOnStartup) {
        this.loadOnStartup = loadOnStartup;
    }

    @Override
    public String toString() {
        return "Core{" +
                "name='" + name + '\'' +
                ", instanceDir='" + instanceDir + '\'' +
                '}';
    }
}
