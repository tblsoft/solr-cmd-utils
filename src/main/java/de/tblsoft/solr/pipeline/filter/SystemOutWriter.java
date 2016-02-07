package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractWriter;

/**
 * Created by tblsoft on 23.01.16.
 */
public class SystemOutWriter extends AbstractWriter {


    private int fieldCounter =0;

    private int documentCounter =0;

    @Override
    public void field(String name, String value) {
        fieldCounter++;
        System.out.print("name: " + name);
        System.out.println(" -- value: " + value);
    }

    @Override
    public void endDocument() {
        documentCounter++;
        //System.out.println("endDocument");
    }

    @Override
    public void end() {
        System.out.println("end");
        System.out.println("fields: " + fieldCounter);
        System.out.println("documents: " + documentCounter);
    }

}
