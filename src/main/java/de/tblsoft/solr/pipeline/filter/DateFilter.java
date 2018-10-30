package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tblsoft 17.03.16.
 */
public class DateFilter extends AbstractFilter {

    private SimpleDateFormat inputDateFormat;
    private SimpleDateFormat outputDateFormat;

    private String dateField;


    @Override
    public void init() {
        String inputDateFormatString = getProperty("inputDateFormat", null);
        verify(inputDateFormatString, "For the DateFilter a inputDateFormat must be defined.");

        inputDateFormat = new SimpleDateFormat(inputDateFormatString, Locale.ENGLISH);

        String outputDateFormatString = getProperty("outputDateFormat", null);
        verify(outputDateFormatString, "For the DateFilter a outputDateFormat must be defined.");

        outputDateFormat = new SimpleDateFormat(outputDateFormatString, Locale.ENGLISH);

        this.dateField = getProperty("dateField", null);
        verify(this.dateField, "For the DateFilter a dateField must be defined.");

        super.init();
    }

    @Override
    public void document(Document document) {
        String value = document.getFieldValue(this.dateField);

        if(!Strings.isNullOrEmpty(value)) {
            try {
                Date inputDate = inputDateFormat.parse(value);
                String newValue = outputDateFormat.format(inputDate);
                document.setField(this.dateField, newValue);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        super.document(document);
    }
}
