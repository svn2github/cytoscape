package org.cytoscape.io.webservice.biomart.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cytoscape.io.webservice.biomart.BiomartQuery;
import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ImportTableTask extends AbstractTask {

	private final BiomartRestClient client;
	private final BiomartQuery query;
	
	private final CyTableFactory tableFactory;
	private final CyNetworkManager manager;

	private Set<CyTable> tables;

	public ImportTableTask(final BiomartRestClient client,
			final BiomartQuery query, final CyTableFactory tableFactory,
			final CyNetworkManager manager) {
		
		this.client = client;
		this.query = query;
		this.tableFactory = tableFactory;
		this.manager = manager;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		if (query == null)
			throw new NullPointerException("Query is null");

		final BufferedReader result = client.sendQuery(query.getQueryString());

		if (result.ready() == false)
			throw new IOException("Could not get result.");

		tables = mapping(result, query.getKeyColumnName());
	}

	private Set<CyTable> mapping(BufferedReader reader, String key)
			throws IOException {

		final Set<CyTable> newTables = new HashSet<CyTable>();

		final CyTable globalTable = tableFactory.createTable(query.getTableName(), key,
				String.class, true);
		
		newTables.add(globalTable);

		// Read result from reader
		String line = reader.readLine();
		System.out.println("Table Header: " + line);
		final String[] columnNames = line.split("\\t");

		if (columnNames[0].contains("Query ERROR"))
			throw new IOException("Biomart service returns Querry ERROR.  ");

		// For status report
		int recordCount = 0;
		List<String> report = new ArrayList<String>();

		// final int rowCount = result.size();
		final int colSize = columnNames.length;
		int keyIdx = 0;

		// Search column index of the key
		for (int i = 0; i < colSize; i++) {
			globalTable.createColumn(columnNames[i], String.class);

			if (columnNames[i].equals(key))
				keyIdx = i;
		}
	

		String[] row;
		String val;

		List<List<Object>> listOfValList;
		List<String> ids = null;

		List<Object> testList;
		String keyVal = null;
		int rowLength = 0;

		int hitCount = 0;

		long start = System.currentTimeMillis();
		while ((line = reader.readLine()) != null) {

			row = line.split("\\t");

			// Ignore invalid length entry.
			if ((row.length <= keyIdx) || (row.length == 0))
				continue;

			recordCount++;
			keyVal = row[keyIdx];
//
//			System.out.println("Key ====>" + keyVal + "<==");

			rowLength = row.length;

			for (int j = 0; j < rowLength; j++) {
				val = row[j];

				if ((val != null) && (val.length() != 0) && (j != keyIdx)) {
					listOfValList = new ArrayList<List<Object>>();

					final CyRow cyRow = globalTable.getRow(keyVal);
					cyRow.set(columnNames[j], val);
//					if (keyAttrName.equals("ID")) {
//						testList = attr
//								.getListAttribute(keyVal, columnNames[j]);
//
//						if (testList != null)
//							listOfValList.add(testList);
//					} else {
//						ids = getIdFromAttrValue(attrDataType, keyAttrName,
//								keyVal, nodeIdList, attr);
//
//						if (ids.size() == 0)
//							continue;
//
//						for (String id : ids)
//							listOfValList.add(attr.getListAttribute(id,
//									columnNames[j]));
//					}
//
//					if (listOfValList.size() == 0) {
//						List<Object> valList = new ArrayList<Object>();
//						listOfValList.add(valList);
//					}
//
//					int index = 0;
//					for (List<Object> valList : listOfValList) {
//						if (valList == null)
//							valList = new ArrayList<Object>();
//
//						if (valList.contains(row[j]) == false)
//							valList.add(row[j]);
//
//						if (keyAttrName.equals("ID")) {
//							attr.setListAttribute(keyVal, columnNames[j],
//									valList);
//							attr.setAttribute(keyVal, columnNames[j] + "-TOP",
//									valList.get(0).toString());
//						} else {
//							attr.setListAttribute(ids.get(index),
//									columnNames[j], valList);
//							attr.setAttribute(ids.get(index), columnNames[j]
//									+ "-TOP", valList.get(0).toString());
//
//						}
//						hitCount++;
//						index++;
//					}
				}
				System.out.println();
			}
		}

		reader.close();
		reader = null;
		return newTables;
	}

	public Set<CyTable> getCyTables() {
		return tables;
	}
}
