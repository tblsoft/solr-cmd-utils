package de.tblsoft.solr.pipeline.processor;

import java.util.function.Function;

public class DistinctFunction implements Function<String, String> {

    @Override
    public String apply(String s) {
        return s.toLowerCase();
    }

    @Override
    public <V> Function<V, String> compose(Function<? super V, ? extends String> before) {


        return null;
    }

    @Override
    public <V> Function<String, V> andThen(Function<? super String, ? extends V> after) {
        return null;
    }
}
