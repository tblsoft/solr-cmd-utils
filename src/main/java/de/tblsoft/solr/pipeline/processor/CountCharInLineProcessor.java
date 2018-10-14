package de.tblsoft.solr.pipeline.processor;

import org.apache.commons.lang3.StringUtils;

public class CountCharInLineProcessor extends LineProcessor {

    private String charToCount;

    private Long charCount;

    @Override
    public void init() {
        super.init();
        charToCount = getProperty("charToCount", null);
        charCount = getPropertyAsInteger("charCount", null);

    }

    @Override
    public String processLine(String line) {
        if(getCurrentLine() <= getSkipLines()) {
            return line;
        }
        int currentCount = StringUtils.countMatches(line, charToCount);
        if (currentCount == charCount) {
            return line;
        }
        return null;
    }
}
