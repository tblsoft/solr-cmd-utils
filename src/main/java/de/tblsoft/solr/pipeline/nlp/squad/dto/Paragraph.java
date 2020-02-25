package de.tblsoft.solr.pipeline.nlp.squad.dto;

import java.util.List;

public class Paragraph {
    private String context;
    private List<Qas> qas;

    /**
     * Getter for property 'qas'.
     *
     * @return Value for property 'qas'.
     */
    public List<Qas> getQas() {
        return qas;
    }

    /**
     * Setter for property 'qas'.
     *
     * @param qas Value to set for property 'qas'.
     */
    public void setQas(List<Qas> qas) {
        this.qas = qas;
    }

    /**
     * Getter for property 'context'.
     *
     * @return Value for property 'context'.
     */
    public String getContext() {
        return context;
    }

    /**
     * Setter for property 'context'.
     *
     * @param context Value to set for property 'context'.
     */
    public void setContext(String context) {
        this.context = context;
    }
}
