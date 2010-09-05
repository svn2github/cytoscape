package org.cytoscape.io.internal.read.datatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.cytoscape.io.internal.read.AbstractDataTableReader;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.cytoscape.model.GraphObject.*;

public class TextDataTableReader extends AbstractDataTableReader {

	@Tunable(description="Column delimiter character")
	public String delimiter = "\t";

	@Tunable(description="Table name")
	public String tableName = "";

	private String[] columnNames;
	private static final Logger logger = LoggerFactory.getLogger(TextDataTableReader.class);

	public TextDataTableReader(InputStream inputStream, CyDataTableFactory tableFactory) {
		super(inputStream, tableFactory);
	}
	
	public void run(TaskMonitor tm) throws IOException {
		try {
	
		
			String line;
			final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		
			line = br.readLine();
			final CyDataTable table = createTable(line);
		
			while ((line = br.readLine()) != null) 
				processLine(table, line);
	
			cyDataTables = new CyDataTable[] { table };

		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}

		tm.setProgress(1.0);
	}
	
	private CyDataTable createTable(String line) throws IOException {
		if (line == null)
			throw new IllegalStateException("Column names cannot be null");
		
		columnNames = line.split(delimiter);

		checkForDuplicates( columnNames );
		final CyDataTable table = tableFactory.createTable(tableName, columnNames[0], 
		                                                   String.class, true);
		
		for ( String col : columnNames ) 
			table.createColumn(col, String.class, false);

		return table;
	}

	private void checkForDuplicates(String[] names) throws IOException {
		Map<String,Integer> map = new HashMap<String,Integer>();

		for ( String n : names ) {
			if ( !map.containsKey(n) )
				map.put(n,0);
			map.put(n, map.get(n) + 1 );
		}
	
		if (map.keySet().size() != names.length) {
			String dupes = " ";
			for ( Map.Entry<String,Integer> entry : map.entrySet() )
				if ( entry.getValue() > 1 )
					dupes += entry.getKey() + " ";
		
			throw new IOException("Illegal duplicate column headers found: " + dupes);
		}
	}

	private void processLine(CyDataTable table, String line) {
		String[] buffer = line.split(delimiter);

		if ( buffer.length != columnNames.length ) {
			logger.warn("Skipping line: '" + line + "' due to incorrect length");
			return;
		}
		
		CyRow row = table.getRow(buffer[0]);

		for(int i = 0; i<buffer.length; i++) 
			row.set(columnNames[i], buffer[i]);
	}
}
