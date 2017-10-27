package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Joiner;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.util.IOUtils;

import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SystemOutWriter extends AbstractFilter {

    private String prefix;

    private int fieldCounter =0;

    private int documentCounter =0;

    @Override
    public void init() {
        try {
            prefix = getProperty("prefix", "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.init();
    }

    @Override
    public void document(Document document) {
        documentCounter++;
        List<Field> values = document.getFields();
        if(values != null) {
            for(Field f :values) {
                fieldCounter++;
                System.out.print(prefix+"name: " + f.getName());

                String out = Joiner.on(", ").join(f.getValues());
                System.out.println(prefix+" -- value: " + out );
            }
        }

        super.document(document);
    }

    @Override
    public void end() {
        System.out.println("end");
        System.out.println("fields: " + fieldCounter);
        System.out.println("documents: " + documentCounter);
        super.end();
    }

}
