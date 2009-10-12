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

import org.cytoscape.io.read.CyDataTableReaderFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.CyFileFilter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class TextDataTableReaderFactory implements CyDataTableReaderFactory {
	
	private static final String DEF_DELIMITER = "\t";
	
	private final CyFileFilter fileFilter;

	public TextDataTableReaderFactory(CyFileFilter fileFilter) {
		this.fileFilter = fileFilter;
	}

	public CyFileFilter getCyFileFilter()
	{
		return fileFilter;
	}


	public Task getReader(InputStream input, CyDataTable dataTable)
	{
		return new TextDataTableReader(input, dataTable);
	}

	class TextDataTableReader implements Task
	{
		final InputStream input;
		final CyDataTable dataTable;

		String[] buffer;
		String[] columnNames;
		boolean cancel = false;
		Logger logger = LoggerFactory.getLogger(getClass());
		long lineCount = 1;

		public TextDataTableReader(InputStream input, CyDataTable dataTable)
		{
			this.input = input;
			this.dataTable = dataTable;
		}

		public void run(TaskMonitor monitor) throws IOException {
			monitor.setTitle("Reading text data table");
			final BufferedReader br = new BufferedReader(new InputStreamReader(input));
			
			
			monitor.setStatusMessage("Reading column names");
			// Extract attribute names
			createColumns(br.readLine());
			
			monitor.setStatusMessage("Reading contents");
			while (!cancel)
			{
				String line = br.readLine();
				if (line == null)
					break;
				processLine(line);
			}
		}
		
		private void createColumns(String line) {
			if(line == null)
			{
				logger.warn("Text data table is empty");
				return;
			}
			
			columnNames = line.split(DEF_DELIMITER);
			
			final int columnLen = columnNames.length;
			List<String> existingColumns = dataTable.getUniqueColumns();
			
			for(int i=0; i<columnLen; i++) {
				if(existingColumns.contains(columnNames[i]) == false)
					dataTable.createColumn(columnNames[i], String.class, false);
			}
		}

		private void processLine(String line) {
			String[] buffer = line.split(DEF_DELIMITER);
			lineCount++;
			int min = Math.min(buffer.length, columnNames.length);
			if (buffer.length != columnNames.length)
				logger.warn(String.format("Line %d has %d value(s), while the table has %d column(s); using only the first %d value(s)", lineCount, buffer.length, columnNames.length, min));

			CyRow rowBuffer = dataTable.addRow();
			for(int i = 0; i<min; i++) {
				rowBuffer.set(columnNames[i], buffer[i]);
			}
		}

		public void cancel()
		{
			cancel = true;
		}
	}
}
