package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.IOUtils;
import de.tblsoft.solr.util.OutputStreamStringBuilder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tblsoft on 21.02.16.
 */
public class N3Writer extends AbstractFilter {

	private String absoluteFilename;

	private OutputStream outputStream;

	private OutputStreamStringBuilder outputStreamStringBuilder;


	@Override
	public void init() {
		String relativeFilename = getProperty("filename", null);
		absoluteFilename = IOUtils.getAbsoluteFile(getBaseDir(), relativeFilename);

		verify(absoluteFilename, "For the FileLineWriter a filname must be defined.");

		try {
			outputStream = IOUtils.getOutputStream(absoluteFilename);
			outputStreamStringBuilder = new OutputStreamStringBuilder(outputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		super.init();

	}

	@Override
	public void document(Document document) {
		try {
			String subject = document.getFieldValue("subject");
			String predicate = document.getFieldValue("predicate");
			String object = document.getFieldValue("object");
			subject=subject.replaceAll(" ", "_");
			predicate=predicate.replaceAll(" ", "_");
			object=object.replaceAll("\n", " ");// todo: filter or cut long text?

			outputStreamStringBuilder.append("<").append(subject).append(">").append("\t");
			outputStreamStringBuilder.append("<").append(predicate).append(">").append("\t");
			outputStreamStringBuilder.append("\"").append(object).append("\"");
			outputStreamStringBuilder.append("\n");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		super.document(document);
	}


	@Override
	public void end() {
		try {
			outputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		super.end();
	}

}
