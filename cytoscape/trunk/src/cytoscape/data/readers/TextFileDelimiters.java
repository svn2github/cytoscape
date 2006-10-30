package cytoscape.data.readers;

/**
 * Define text file delimiters as enum.
 * 
 * @since Cytoscape 2.4
 * @version 0.9
 * @author Keiichiro Ono
 *
 */
public enum TextFileDelimiters {
	TAB("\\t"), COMMA(","), SEMICOLON(";"), SPACE(" "), PIPE("\\|"), COLON(":"), SLASH("/"), BACKSLASH("\\"); 
	
	private String delimiter;
	
	private TextFileDelimiters(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String toString() {
		return delimiter;
	}
}
