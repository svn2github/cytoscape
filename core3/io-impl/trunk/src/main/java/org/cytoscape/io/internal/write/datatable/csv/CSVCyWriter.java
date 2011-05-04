package org.cytoscape.io.internal.write.datatable.csv;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.work.TaskMonitor;

import au.com.bytecode.opencsv.CSVWriter;

public class CSVCyWriter implements CyWriter {

	private OutputStream outputStream;
	private CyTable table;

	public CSVCyWriter(OutputStream outputStream, CyTable table) {
		this.outputStream = outputStream;
		this.table = table;
	}

	@Override
	public void cancel() {
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		CSVWriter writer = new CSVWriter(new OutputStreamWriter(outputStream), ',', '"', "\r\n");
		try {
			List<CyColumn> columns = new ArrayList<CyColumn>(table.getColumns());
			Collections.sort(columns, new Comparator<CyColumn>() {
				@Override
				public int compare(CyColumn o1, CyColumn o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
			writeHeader(writer, columns);
			writeValues(writer, columns);
		} finally {
			writer.close();
		}
	}
	
	private void writeValues(CSVWriter writer, Collection<CyColumn> columns) {
		for (CyRow row : table.getAllRows()) {
			String[] values = new String[columns.size()];
			int index = 0;
			for (CyColumn column : columns) {
				Class<?> type = column.getType();
				if (type.equals(List.class)) {
					StringBuilder builder = new StringBuilder();
					boolean first = true;
					List<?> list = row.getList(column.getName(), column.getListElementType());
					if (list != null) {
						for (Object value : list) {
							if (!first) {
								builder.append("\r");
							}
							if (value != null) {
								builder.append(value);
							}
							first = false;
						}
						values[index] = builder.toString();
					}
				} else {
					Object value = row.get(column.getName(), type);
					if (value != null) {
						values[index] = value.toString();
					} else {
						values[index] = null;
					}
				}
				index++;
			}
			writer.writeNext(values);
		}
	}

	private void writeHeader(CSVWriter writer, List<CyColumn> columns) {
		String[] values = new String[columns.size()];
		for (int i = 0; i < columns.size(); i++) {
			values[i] = columns.get(i).getName();
		}
		writer.writeNext(values);
	}
}
