package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class AbstractSubFieldFilter extends AbstractFilter {

	private String subField;

	@Override
	public void init() {

		subField = getProperty("subField", null);
		super.init();
	}

	public List<Document> traverse(int i, String[] fieldNames, Document document) {
		String fieldName = fieldNames[i];
		List<Document> documents = document.getSubField(fieldName);
		if(documents == null) {
			// retrieve subfield from rawvalue field
			if(document.getField(fieldName) != null && document.getField(fieldName).getRawValue() != null) {
				List<Map<String, Object>> rawDocs = (List<Map<String, Object>>) document.getField(fieldName).getRawValue();
				document.deleteField(fieldName);

				for (Map<String, Object> rawDoc : rawDocs) {
					Document subDoc = new Document();
					for (Map.Entry<String, Object> entry : rawDoc.entrySet()) {
						subDoc.setField(entry.getKey(), entry.getValue());
					}
					document.addSubField(fieldName, subDoc);
				}
				documents = document.getSubField(fieldName);
			} else {
				return Arrays.asList(document);
			}
		}
		List<Document> processedDocumentList = new ArrayList<>();
		for(Document d : documents) {
			if(i >= fieldNames.length - 1) {
				List<Document> processed = processDocument(d);
				processedDocumentList.addAll(processed);
			} else {
				List<Document> processed = traverse(i+1, fieldNames, d);
				processedDocumentList.addAll(processed);
			}

		}

		document.setSubField(fieldNames[i], processedDocumentList);
		return Arrays.asList(document);
	}


	@Override
	public void document(Document document) {
		List<Document> processeddDocument = null;
		if(subField != null) {
			String[] splitted = subField.split(Pattern.quote("."));
			processeddDocument = traverse(0, splitted, document);
		} else {
			processeddDocument = processDocument(document);
		}
		if(processeddDocument != null) {
			for(Document d: processeddDocument) {
				super.document(d);
			}
		}

	}

	public abstract List<Document> processDocument(Document document);

	public String getSubField() {
		return subField;
	}

	public void setSubField(String subField) {
		this.subField = subField;
	}
}
