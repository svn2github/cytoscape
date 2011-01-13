/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.io.webservice.biomart;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.naming.ConfigurationException;

import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.io.webservice.client.AbstractWebServiceClient;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableFactory;

/**
 * Biomart Web Service Client.
 * 
 */
public class BiomartClient extends AbstractWebServiceClient<BiomartRestClient> {

	// Biomart base URL
	private static final String BASE_URL = "http://www.biomart.org/biomart/martservice";

	private final CyTableFactory tableFactory;
	private final CyNetworkManager manager;


	/**
	 * Creates a new Biomart Client object.
	 * 
	 * @throws ServiceException
	 * @throws ConfigurationException
	 */
	public BiomartClient(final String displayName, final String description, 
			final BiomartRestClient restClient,
			final CyTableFactory tableFactory, 
			final CyNetworkManager manager) {
		super(restClient.getBaseURL(), displayName, description, restClient);

		this.tableFactory = tableFactory;
		this.manager = manager;

		// TODO: set optional parameters (Tunables?)
	}

	/**
	 * Based on the query given, execute the data fetching.
	 * 
	 * @param query
	 * @throws CyWebServiceException
	 * @throws IOException
	 * @throws Exception
	 */
	public Set<CyTable> importAttributes(final BiomartQuery query) throws IOException {

		final BufferedReader result = clientStub.sendQuery(query.getQueryAsString());
		
		if (result.ready() == false)
			throw new IOException("Could not get result.");

		return mapping(result, query.getKeyColumnName());

	}
	
	private Set<CyTable> mapping(BufferedReader reader, String key) throws IOException {
		
		final Set<CyTable> newTables = new HashSet<CyTable>();
		
		final CyTable table = tableFactory.createTable("Test Table name", key, String.class, true);
		
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
			table.createListColumn(columnNames[i], String.class);
			
			if (columnNames[i].equals(key))
				keyIdx = i;
		}

		//byte attrDataType = Cytoscape.getNodeAttributes().getType(keyAttrName);
		final List<String> nodeIdList = new ArrayList<String>();

		// Prepare list of nodes FROM ALL NETWORKS.
		
		final Set<CyNetwork> allNetworks = manager.getNetworkSet();
		
		for(final CyNetwork network: allNetworks) {
			final List<CyNode> nodes = network.getNodeList();
			for(CyNode node: nodes) {
				
			}
		}


//		String[] row;
//		String val;
//
//		List<List<Object>> listOfValList;
//		List<String> ids = null;
//
//		List<Object> testList;
//		String keyVal = null;
//		int rowLength = 0;
//
//		int hitCount = 0;
//
//		long start = System.currentTimeMillis();
//		while ((line = reader.readLine()) != null) {
//			// Cancel the job.
//			if (cancelImport) {
//				cancelImport = false;
//				return null;
//			}
//
//			row = line.split("\\t");
//
//			// Ignore invalid length entry.
//			if ((row.length <= keyIdx) || (row.length == 0))
//				continue;
//
//			recordCount++;
//			keyVal = row[keyIdx];
//
//			// System.out.println("Key ====>" + keyVal + "<==");
//			// for(String s: entry)
//			// System.out.println("ENT ======>" + s + "<===");
//			rowLength = row.length;
//
//			for (int j = 0; j < rowLength; j++) {
//				val = row[j];
//
//				if ((val != null) && (val.length() != 0) && (j != keyIdx)) {
//					listOfValList = new ArrayList<List<Object>>();
//
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
//				}
//			}
//		}
//
//		// System.out.println("Time =====> " +
//		// (System.currentTimeMillis()-start));
//
//		reader.close();
//		reader = null;
//
//		report.add("Number of Records (Rows) from BioMart = " + recordCount
//				+ "\n");
//		report.add("Number of Terms Mapped = " + hitCount + "\n");
//		report.add("The following node attributes are created:" + "\n");
//		for (String s : columnNames) {
//			report.add(s + ", " + s + "-TOP");
//		}
//		report.add("\nAttributes name ends with \'TOP\' contains first entry of the list");
//		return report;
		
		return newTables;
	}

//	private List<String> getIdFromAttrValue(final byte attrDataType,
//			final String attrName, Object attrValue, List<String> nodeIdList,
//			CyAttributes attr) {
//		final List<String> idList = new ArrayList<String>();
//
//		String value;
//
//		List<Object> l = null;
//
//		if (attrDataType == CyAttributes.TYPE_SIMPLE_LIST) {
//			for (String id : nodeIdList) {
//				l = attr.getListAttribute(id, attrName);
//
//				if ((l != null) && (l.size() > 0)) {
//					for (Object obj : l) {
//						if ((obj != null) && obj.equals(attrValue)) {
//							idList.add(id);
//
//							break;
//						}
//					}
//				}
//			}
//		} else if (attrDataType == CyAttributes.TYPE_STRING) {
//			for (String id : nodeIdList) {
//				// Extract attribute value from ID
//				value = attr.getStringAttribute(id, attrName);
//
//				if ((value != null) && value.equals(attrValue))
//					idList.add(id);
//			}
//		}
//
//		return idList;
//	}
	
}
