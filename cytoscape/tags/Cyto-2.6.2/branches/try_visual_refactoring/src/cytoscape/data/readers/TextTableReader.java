package cytoscape.data.readers;

import java.io.IOException;
import java.util.List;

public interface TextTableReader {
	
	public enum ObjectType {
		NODE, EDGE, NETWORK;
	}
	public void readTable() throws IOException;
	
	public List getColumnNames();
	
}
