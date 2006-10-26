package cytoscape.data.readers;

/**
 * Define table type (delimiter type).
 * 
 * @author kono
 *
 */
public enum TableFileDelimiterType {
	TAB("\\t"), COMMA(","), SEMICOLON(";"), SPACE(" "); 
	
	private String delimiter;
	
	private TableFileDelimiterType(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String toString() {
		return delimiter;
	}
}
