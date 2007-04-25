package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.io.IOException;
import java.util.List;


/**
 * Interface of all text table readers.<br>
 * 
 * @since Cytoscape 2.4
 * @version 1.0
 * @author kono
 *
 */
public interface TextTableReader {
	
	public enum ObjectType {
		NODE, EDGE, NETWORK;
	}
	public void readTable() throws IOException;
	
	public List getColumnNames();
	
	/**
	 * Report the result of import as a string.
	 * @return Description of
	 */
	public String getReport();

}
