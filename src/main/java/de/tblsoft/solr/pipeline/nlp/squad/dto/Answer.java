package de.tblsoft.solr.pipeline.nlp.squad.dto;

public class Answer {

    private int answer_start;
    private String text;

    /**
     * Getter for property 'answer_start'.
     *
     * @return Value for property 'answer_start'.
     */
    public int getAnswer_start() {
        return answer_start;
    }

    /**
     * Setter for property 'answer_start'.
     *
     * @param answer_start Value to set for property 'answer_start'.
     */
    public void setAnswer_start(int answer_start) {
        this.answer_start = answer_start;
    }

    /**
     * Getter for property 'text'.
     *
     * @return Value for property 'text'.
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for property 'text'.
     *
     * @param text Value to set for property 'text'.
     */
    public void setText(String text) {
        this.text = text;
    }
}
