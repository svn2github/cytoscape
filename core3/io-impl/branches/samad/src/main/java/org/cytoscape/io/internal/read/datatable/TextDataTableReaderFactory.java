package org.cytoscape.io.internal.read.datatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.GraphObject;
import org.cytoscape.session.CyNetworkManager;

import static org.cytoscape.model.GraphObject.*;

import org.cytoscape.io.read.CyNetworkReaderFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.CyFileFilter;

public class TextDataTableReaderFactory implements CyNetworkReaderFactory {
	
	private static final String DEF_DELIMITER = "\t";
	
	private String delimiter;
	
	private String[] buffer;
	private CyRow rowBuffer;
	
	private String[] columnNames;
	
	private String primaryKey = "name";
	
	private final CyFileFilter fileFilter;
	private final String objectType;

	public TextDataTableReaderFactory(String objType, CyFileFilter fileFilter) {
		super();
		this.objectType = objType;
		this.fileFilter = fileFilter;
		delimiter = DEF_DELIMITER;
	}

	public CyFileFilter getCyFileFilter()
	{
		return fileFilter;
	}


	public Task getReader(InputStream input, CyNetwork network, CyDataTable dataTable)
	{
		return new TextDataTableReader(input, network, dataTable);
	}

	class TextDataTableReader implements Task
	{
		final InputStream input;
		final CyNetwork network;
		final CyDataTable dataTable;
		boolean cancel = false;

		public TextDataTableReader(InputStream input, CyNetwork network, CyDataTable dataTable)
		{
			this.input = input;
			this.network = network;
			this.dataTable = dataTable;
		}

		public void run(TaskMonitor monitor) throws IOException {
			monitor.setTitle("Reading text data table");
			List<String> lines = new ArrayList<String>();
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					input));
			
			
			monitor.setStatusMessage("Reading column names");
			// Extract attribute names
			createColumns(br.readLine());
			
			monitor.setStatusMessage("Reading contents");
			while (true)
			{
				String line = br.readLine();
				if (line == null)
					break;
				if (cancel)
					return;
				lines.add(line);
			}

			monitor.setStatusMessage("Parsing contents");
			for (int i = 0; (i < lines.size()) && (!cancel); i++)
			{
				monitor.setProgress(i / ((double) lines.size()));
				String line = lines.get(i);
				processLine(line);
			}
			
			for(CyNode node: network.getNodeList()) {
				System.out.println("Attr for " + node.getSUID() + " =========> " + node.attrs().toString());
			}
		}
		
		private void createColumns(String line) {
			if(line == null)
				throw new IllegalStateException("Column names cannot be null");
			
			buffer = line.split(delimiter);
			
			if(buffer.length < 2)
				throw new IllegalStateException("At least two columns should be in the table");
			
			final int columnLen = buffer.length;
			List<String> existingColumns = dataTable.getUniqueColumns();
			columnNames = new String[columnLen - 1];
			
			for(int i=1; i<columnLen; i++) {
				columnNames[i-1] = buffer[i];
				if(existingColumns.contains(buffer[i]) == false)
					dataTable.createColumn(buffer[i], String.class, false);
			}
		}

		private void processLine(String line) {
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

		public void cancel()
		{
			cancel = true;
		}
	}
}
