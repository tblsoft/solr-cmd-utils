package de.tblsoft.solr.pipeline.bean;

import java.util.Comparator;

public class FieldComperator implements Comparator<Field> {

	@Override
	public int compare(Field o1, Field o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
