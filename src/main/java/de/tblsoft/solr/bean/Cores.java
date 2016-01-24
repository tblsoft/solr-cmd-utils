package de.tblsoft.solr.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tblsoft
 *
 * A bean class for the solr.xml file
 */
public class Cores {

    private String defaultCoreName;

    private String adminPath;


    private List<Core> core;

    @XmlAttribute
    public String getAdminPath() {
        return adminPath;
    }

    public void setAdminPath(String adminPath) {
        this.adminPath = adminPath;
    }

    @XmlElement
    public List<Core> getCore() {
        return core;
    }

    public void setCore(List<Core> core) {
        this.core = core;
    }

    @XmlAttribute
    public String getDefaultCoreName() {
        return defaultCoreName;
    }

    public void setDefaultCoreName(String defaultCoreName) {
        this.defaultCoreName = defaultCoreName;
    }

    @Override
    public String toString() {
        return "Cores{" +
                "adminPath='" + adminPath + '\'' +
                ", core=" + core +
                '}';
    }

    public void addCore(String coreName) {
        for(Core core: getCore()) {
            if(core.getName().equals(coreName)) {
                return;
            }
        }
        getCore().add(Core.createCore(coreName));
    }

    public void deleteCore(String coreName) {
        Iterator<Core> it = this.core.iterator();

        while(it.hasNext()) {
            Core core = it.next();
            if(core.getName().equals(coreName)) {
                this.core.remove(core);
            }
        }
    }

    public void renameCore(String oldCoreName, String newCoreName) {

        for(Core core: getCore()) {
            if(!core.getName().equals(oldCoreName)) {
                core.setName(newCoreName);
                core.setInstanceDir(newCoreName);
            }
        }
    }
}
