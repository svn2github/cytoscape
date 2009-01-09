package cytoscape.data.readers;

/**
 * Define table type (delimiter type).
 * 
 * @author kono
 *
 */
public enum TableFileType {
	TAB("\t"), CSV(",");
	
	private String delimiter;
	
	private TableFileType(String delimiter) {
		this.delimiter = delimiter;
	}
	
	public String toString() {
		return delimiter;
	}
}
