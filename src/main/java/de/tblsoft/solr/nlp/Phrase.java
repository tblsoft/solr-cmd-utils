package de.tblsoft.solr.nlp;

import java.util.List;

public class Phrase {

    private List<String> posTags;

    private List<String> tokens;

    private Integer startPosition;

    private Integer endPosition;

    private Integer length;

    private String phraseText;

    private String normalizedText;




    /**
     * Getter for property 'posTags'.
     *
     * @return Value for property 'posTags'.
     */
    public List<String> getPosTags() {
        return posTags;
    }

    /**
     * Setter for property 'posTags'.
     *
     * @param posTags Value to set for property 'posTags'.
     */
    public void setPosTags(List<String> posTags) {
        this.posTags = posTags;
    }

    /**
     * Getter for property 'tokens'.
     *
     * @return Value for property 'tokens'.
     */
    public List<String> getTokens() {
        return tokens;
    }

    /**
     * Setter for property 'tokens'.
     *
     * @param tokens Value to set for property 'tokens'.
     */
    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    /**
     * Getter for property 'startPosition'.
     *
     * @return Value for property 'startPosition'.
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * Setter for property 'startPosition'.
     *
     * @param startPosition Value to set for property 'startPosition'.
     */
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * Getter for property 'endPosition'.
     *
     * @return Value for property 'endPosition'.
     */
    public Integer getEndPosition() {
        return endPosition;
    }

    /**
     * Setter for property 'endPosition'.
     *
     * @param endPosition Value to set for property 'endPosition'.
     */
    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    /**
     * Getter for property 'length'.
     *
     * @return Value for property 'length'.
     */
    public Integer getLength() {
        return length;
    }

    /**
     * Setter for property 'length'.
     *
     * @param length Value to set for property 'length'.
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * Getter for property 'phraseText'.
     *
     * @return Value for property 'phraseText'.
     */
    public String getPhraseText() {
        return phraseText;
    }

    /**
     * Setter for property 'phraseText'.
     *
     * @param phraseText Value to set for property 'phraseText'.
     */
    public void setPhraseText(String phraseText) {
        this.phraseText = phraseText;
    }

    /**
     * Getter for property 'normalizedText'.
     *
     * @return Value for property 'normalizedText'.
     */
    public String getNormalizedText() {
        return normalizedText;
    }

    /**
     * Setter for property 'normalizedText'.
     *
     * @param normalizedText Value to set for property 'normalizedText'.
     */
    public void setNormalizedText(String normalizedText) {
        this.normalizedText = normalizedText;
    }
}
