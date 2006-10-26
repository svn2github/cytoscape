package cytoscape.data.readers;

/**
 * Define table type (delimiter type).
 * 
 * @author kono
 *
 */
public enum TextFileDelimiters {
	TAB("\\t"), COMMA(","), SEMICOLON(";"), SPACE(" "); 
	
	private String delimiter;
	
	private TextFileDelimiters(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String toString() {
		return delimiter;
	}
}
