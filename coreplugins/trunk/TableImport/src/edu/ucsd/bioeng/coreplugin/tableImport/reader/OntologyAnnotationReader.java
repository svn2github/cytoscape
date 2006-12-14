package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import cytoscape.util.URLUtil;

public class OntologyAnnotationReader implements TextTableReader {

	private final AttributeAndOntologyMappingParameters mapping;
	private final URL source;
	private final String commentChar;
	private final int startLineNumber;
	private final OntologyAndAnnotationLineParser parser;

	public OntologyAnnotationReader(URL source,
			AttributeAndOntologyMappingParameters mapping,
			final String commentChar, final int startLineNumber) {
		this.source = source;
		this.mapping = mapping;
		this.commentChar = commentChar;
		this.startLineNumber = startLineNumber;

		parser = new OntologyAndAnnotationLineParser(mapping);
	}

	public List getColumnNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public void readTable() throws IOException {
		InputStream is = URLUtil.getInputStream(source);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				is));
		String line;
		int lineCount = 0;

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if (commentChar != null && line.startsWith(commentChar)) {
				// Do nothing
			} else if (lineCount > startLineNumber && line.trim().length() > 0) {
				String[] parts = line.split(mapping.getDelimiterRegEx());
				parser.parseEntry(parts);
			}
			lineCount++;
		}
		is.close();
		bufRd.close();

	}

	public String getReport() {
		// TODO Auto-generated method stub
		final StringBuffer sb = new StringBuffer();

		return sb.toString();
	}

}
