package org.cytoscape.io.internal.read.datatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cytoscape.io.internal.read.AbstractDataTableReader;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.session.CyNetworkManager;

import static org.cytoscape.model.GraphObject.*;

public class TextDataTableReader extends AbstractDataTableReader {
	
	private static final String DEF_DELIMITER = "\t";
	
	private String delimiter;
	
	private String[] buffer;
	private CyRow rowBuffer;
	
	private String[] columnNames;
	
	private String primaryKey = "name";
	
	private CyNetworkManager manager;

	public TextDataTableReader(CyNetworkManager manager, String objType) {
		super();
		this.objectType = objType;
		this.manager = manager;
		delimiter = DEF_DELIMITER;
	}
	
	public Map<Class<?>, Object> read() throws IOException {
		network = manager.getCurrentNetwork();
		
		if( network == null)
			throw new IllegalStateException("Could not find current network.");
		
		final Map<String, CyDataTable> tables = network.getCyDataTables(objectType);
		final CyDataTable table = tables.get(CyNetwork.DEFAULT_ATTRS);
		
		if( table == null)
			throw new IllegalStateException("Could not find target CyDataTable for " + objectType);
		
		String line;
		final BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		
		
		// Extract attribute names
		line = br.readLine();
		createColumns(table, line);
		
		while ((line = br.readLine()) != null)
			processLine(network, line);
		
		for(CyNode node: network.getNodeList()) {
			System.out.println("Attr for " + node.getSUID() + " =========> " + node.attrs().toString());
		}

		readObjects.put(CyDataTable.class, table);
		

		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		
		return readObjects;
	}
	
	private void createColumns(CyDataTable table, String line) {
		if(line == null)
			throw new IllegalStateException("Column names cannot be null");
		
		buffer = line.split(delimiter);
		
		if(buffer.length < 2)
			throw new IllegalStateException("At least two columns should be in the table");
		
		final int columnLen = buffer.length;
		List<String> existingColumns = table.getUniqueColumns();
		columnNames = new String[columnLen - 1];
		
		for(int i=1; i<columnLen; i++) {
			columnNames[i-1] = buffer[i];
			if(existingColumns.contains(buffer[i]) == false)
				table.createColumn(buffer[i], String.class, false);
		}
	}

	private void processLine(CyNetwork network, String line) {
		buffer = line.split(delimiter);
		
		// find target Graph Object
		List<? extends GraphObject> graphObjects = null;
		
		if(objectType.equals(NODE)) {
			graphObjects = network.getNodeList();
		} else if(objectType.equals(EDGE)) {
			graphObjects = network.getEdgeList();
		} else {
			processRow(network);
			return;
		}
		
		for(GraphObject obj: graphObjects)
			processRow(obj);
	}
	
	private void processRow(GraphObject obj) {
		rowBuffer = obj.attrs();
		if(rowBuffer.get(primaryKey, String.class).equals(buffer[0])) {
			for(int i = 1; i<buffer.length; i++) {
				rowBuffer.set(columnNames[i-1], buffer[i]);
				
			}
		}
	}

}
