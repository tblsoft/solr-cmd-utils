package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class AbstractSubFieldFilter extends AbstractFilter {

	private String subField;

	@Override
	public void init() {

		subField = getProperty("subField", null);
		super.init();
	}

	public Document traverse(int i, String[] fieldNames, Document document) {
		List<Document> documents = document.getSubField(fieldNames[i]);
		if(documents == null) {
			return document;
		}
		List<Document> processedDocumentList = new ArrayList<>();
		for(Document d : documents) {
			if(i >= fieldNames.length - 1) {
				Document processed = processDocument(d);
				processedDocumentList.add(processed);
			} else {
				Document processed = traverse(i+1, fieldNames, d);
				processedDocumentList.add(processed);
			}

		}

		document.setSubField(fieldNames[i], processedDocumentList);
		return document;
	}


	@Override
	public void document(Document document) {
		Document processeddDocument = null;
		if(subField != null) {
			String[] splitted = subField.split(Pattern.quote("."));
			processeddDocument = traverse(0, splitted, document);
		} else {
			processeddDocument = processDocument(document);
		}
		if(processeddDocument != null) {
			super.document(processeddDocument);
		}

	}

	public abstract  Document processDocument(Document document);

}
