package de.tblsoft.solr.pipeline.nlp.squad.dto;

import java.util.List;

public class Qas {
    private List<Answer> answers;
    private List<Answer> plausible_answers;
    private String id;
    private Boolean is_impossible;
    private String question;


    /**
     * Getter for property 'answers'.
     *
     * @return Value for property 'answers'.
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    /**
     * Setter for property 'answers'.
     *
     * @param answers Value to set for property 'answers'.
     */
    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    /**
     * Getter for property 'plausible_answers'.
     *
     * @return Value for property 'plausible_answers'.
     */
    public List<Answer> getPlausible_answers() {
        return plausible_answers;
    }

    /**
     * Setter for property 'plausible_answers'.
     *
     * @param plausible_answers Value to set for property 'plausible_answers'.
     */
    public void setPlausible_answers(List<Answer> plausible_answers) {
        this.plausible_answers = plausible_answers;
    }

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Getter for property 'question'.
     *
     * @return Value for property 'question'.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Setter for property 'question'.
     *
     * @param question Value to set for property 'question'.
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Getter for property 'is_impossible'.
     *
     * @return Value for property 'is_impossible'.
     */
    public Boolean getIs_impossible() {
        return is_impossible;
    }

    /**
     * Setter for property 'is_impossible'.
     *
     * @param is_impossible Value to set for property 'is_impossible'.
     */
    public void setIs_impossible(Boolean is_impossible) {
        this.is_impossible = is_impossible;
    }
}
