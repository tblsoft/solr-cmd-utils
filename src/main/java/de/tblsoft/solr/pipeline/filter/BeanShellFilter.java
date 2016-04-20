package de.tblsoft.solr.pipeline.filter;

import bsh.EvalError;
import bsh.Interpreter;
import de.tblsoft.solr.pipeline.AbstractFilter;

import java.io.IOException;

/**
 * Created by tblsoft 20.04.16.
 */
public class BeanShellFilter extends AbstractFilter {

    private Interpreter interpreter;

    private String filename;

    @Override
    public void init() {
        filename = getProperty("filename", null);
        verify(filename, "For the BeanShellFilter a filename property must be defined.");
        interpreter = new Interpreter();
        try {
            interpreter.set("instance", this);
        } catch (EvalError evalError) {
            throw new RuntimeException(evalError);
        }
        super.init();

    }

    public void superField(String name, String value) {
        super.field(name,value);

    }

    @Override
    public void field(String name, String value) {


        try {
            interpreter.set("name", name);
            interpreter.set("value", value);
            interpreter.source(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (EvalError evalError) {
            throw new RuntimeException(evalError);
        }
    }

}
