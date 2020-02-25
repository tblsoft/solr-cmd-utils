package de.tblsoft.solr.pipeline.nlp.squad.dto;

import java.util.List;

public class Data {
    private List<Paragraph> paragraphs;
    private String title;

    /**
     * Getter for property 'paragraphs'.
     *
     * @return Value for property 'paragraphs'.
     */
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    /**
     * Setter for property 'paragraphs'.
     *
     * @param paragraphs Value to set for property 'paragraphs'.
     */
    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    /**
     * Getter for property 'title'.
     *
     * @return Value for property 'title'.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter for property 'title'.
     *
     * @param title Value to set for property 'title'.
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
