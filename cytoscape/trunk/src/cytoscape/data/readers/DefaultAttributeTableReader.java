package cytoscape.data.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cytoscape.util.URLUtil;

/**
 * Basic text table reader for attributes.<br>
 * 
 * <p>
 * based on the given parameters, map the text table to CyAttributes.
 * </p>
 * 
 * @author kono
 * 
 */
public class DefaultAttributeTableReader implements TextTableReader {

	/**
	 * Lines begin with this charactor will be considered as comment lines.
	 */
	private static final String COMMENT_CHAR = "!";
	private static final int DEF_KEY_COLUMN = 0;

	private final URL source;
	private AttributeMappingParameters mapping;
	private final AttributeLineParser parser;
	
	/*
	 * Reader will read entries from this line.
	 */
	private final int startLineNumber;

	/**
	 * Constructor.<br>
	 * 
	 * @param source
	 * @param objectType
	 * @param delimiters
	 * @throws Exception
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters) throws Exception {
		this(source, objectType, delimiters, null,
				DEF_KEY_COLUMN, null, null, null, null, null, 0);
	}

	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters, final int key,
			final String[] columnNames) throws Exception {
		this(source, objectType, delimiters, null,
				DEF_KEY_COLUMN, null, null, columnNames, null, null, 0);
	}

	/**
	 * Constructor with full options.<br>
	 * 
	 * @param source
	 *            Source file URL (can be remote or local)
	 * @param objectType
	 * @param delimiter
	 * @param listDelimiter
	 * @param key
	 * @param aliases
	 * @param columnNames
	 * @param toBeImported
	 * @throws Exception 
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters, final String listDelimiter,
			final int keyIndex, final String mappingAttribute,
			final List<Integer> aliasIndexList, final String[] attributeNames,
			final byte[] attributeTypes, final boolean[] importFlag, final int startLineNumber) throws Exception {
	
		this.source = source;
		this.startLineNumber = startLineNumber;
		this.mapping = new AttributeMappingParameters(objectType,
				delimiters, listDelimiter,
				keyIndex, mappingAttribute,
				aliasIndexList, attributeNames,
				attributeTypes, importFlag); 
		this.parser = new AttributeLineParser(mapping);
	}
	
	public DefaultAttributeTableReader(final URL source, AttributeMappingParameters mapping, final int startLineNumber) {
		this.source = source;
		this.mapping = mapping;
		this.startLineNumber = startLineNumber;
		this.parser = new AttributeLineParser(mapping);
	}

	
	public List getColumnNames() {
		List<String> colNamesList = new ArrayList<String>();
		for (String name : mapping.getAttributeNames()) {
			colNamesList.add(name);
		}
		return colNamesList;
	}

	/**
	 * Read table from the data source.
	 */
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
			if (lineCount > startLineNumber && line.startsWith(COMMENT_CHAR) == false && line.trim().length() > 0) {
				String[] parts = line.split(mapping.getDelimiterRegEx());
				parser.parseEntry(parts);
			}
			lineCount++;
		}
		is.close();
		bufRd.close();
	}

}
