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
package edu.ucsd.bioeng.idekerlab.biomartclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.layout.Tunable;
import cytoscape.util.ModulePropertiesImpl;
import giny.model.Node;


/**
 * Biomart Web Service Client.
 *
 * @author kono
 * @version 0.8
 * @since Cytoscape 2.6
 *
 */
public class BiomartClient extends WebServiceClientImpl<BiomartStub> {
	// Client name
	private static final String DISPLAY_NAME = "Biomart Web Service Client";

	// Client ID
	private static final String CLIENT_ID = "biomart";

	// Actual biomart client.
	private static WebServiceClient<BiomartStub> client;

	// Biomart base URL
	private static final String BASE_URL = "http://www.biomart.org/biomart/martservice";
	
	private boolean cancelImport = false;
	
	private String lastStatus;

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 * @throws Exception
	 */
	public static WebServiceClient<BiomartStub> getClient() throws Exception {
		if (client == null) {
			client = new BiomartClient();
		}

		return client;
	}
	
	

	/**
	 * Creates a new Biomart Client object.
	 *
	 * @throws ServiceException
	 * @throws ConfigurationException
	 */
	public BiomartClient() throws Exception {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.ATTRIBUTE });
		clientStub = new BiomartStub(BASE_URL);

		// Set properties
		props = new ModulePropertiesImpl(clientID, "wsc");

		//		props.add(new Tunable("max_interactions", "Maximum number of records", Tunable.INTEGER,
		//		                      new Integer(1000)));

		//props.add(new Tunable("search_depth", "Search depth", Tunable.INTEGER, new Integer(0)));
		props.add(new Tunable("import_all", "Import all available entries", Tunable.BOOLEAN,
		                      new Boolean(false)));

		props.add(new Tunable("show_all_filter", "Show all available filters", Tunable.BOOLEAN,
		                      new Boolean(false)));
		props.add(new Tunable("base_url", "Biomart Base URL", Tunable.STRING, BASE_URL));
	}

	/**
	 * Execute service based on events.
	 * @throws
	 * @throws CyWebServiceException
	 */
	@Override
	public void executeService(CyWebServiceEvent e) throws CyWebServiceException {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAttributes((AttributeImportQuery) e.getParameter());
			} else if(e.getEventType().equals(WSEventType.CANCEL)) {
				//System.out.println("========Cancelling...");
				cancelImport = true;
			}
		} 
	}

	/**
	 * Based on the query given, execute the data fetching.
	 *
	 * @param query
	 * @throws CyWebServiceException
	 * @throws IOException
	 * @throws Exception
	 */
	private void importAttributes(AttributeImportQuery query) throws CyWebServiceException {
		BufferedReader result = null;
		List<String> report;
		try {
			result = clientStub.sendQuery(query.getParameter().toString());
			if (result.ready() == false)
				throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
			report = mapping(result, query.getKeyCyAttrName(), query.getKeyNameInWebService());
		} catch (IOException e) {
			throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		}
		
		lastStatus = reportBuilder(report);
		
		Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	private String reportBuilder(List<String> report) {
		StringBuilder builder = new StringBuilder();
		builder.append("Import Finished:\n\n");
		for(String s: report)
			builder.append(s+"\n");
		
		
		return builder.toString();
	}
	
	public String getLastStatus() {
		return lastStatus;
	}

	
	
	private List<String> mapping(BufferedReader reader, String key, String keyAttrName) throws IOException, CyWebServiceException {
		String line = reader.readLine();
		//System.out.println("Table Header: " + line);
		final String[] columnNames = line.split("\\t");
		
		if (columnNames[0].contains("Query ERROR"))
			throw new CyWebServiceException(CyWebServiceException.WSErrorCode.REMOTE_EXEC_FAILED);
		

		// For status report
		int recordCount = 0;
		List<String> report = new ArrayList<String>();
		
		
		//final int rowCount = result.size();
		final int colSize = columnNames.length;
		int keyIdx = 0;

		// Search column index of the key
		for (int i = 0; i < colSize; i++) {
			if (columnNames[i].equals(key))
				keyIdx = i;
		}

		byte attrDataType = Cytoscape.getNodeAttributes().getType(keyAttrName);
		final List<String> nodeIdList = new ArrayList<String>();

		// Prepare list of node IDs.
		// This is necessary for reverse-mapping.
		if (keyAttrName.equals("ID") == false) {
			final List<Node> nodes = Cytoscape.getRootGraph().nodesList();

			for (Node node : nodes)
				nodeIdList.add(node.getIdentifier());
		}

		final CyAttributes attr = Cytoscape.getNodeAttributes();

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
			// Cancel the job.
			if(cancelImport) {
				cancelImport = false;
				return null;
			}
			
			row = line.split("\\t");

			// Ignore invalid length entry.
			if ((row.length <= keyIdx) || (row.length == 0))
				continue;

			
			recordCount++;
			keyVal = row[keyIdx];

			//			System.out.println("Key ====>" + keyVal + "<==");
			//			for(String s: entry)
			//				System.out.println("ENT ======>" + s + "<===");
			rowLength = row.length;

			for (int j = 0; j < rowLength; j++) {
				val = row[j];

				if ((val != null) && (val.length() != 0) && (j != keyIdx)) {
					listOfValList = new ArrayList<List<Object>>();

					if (keyAttrName.equals("ID")) {
						testList = attr.getListAttribute(keyVal, columnNames[j]);

						if (testList != null)
							listOfValList.add(testList);
					} else {
						ids = getIdFromAttrValue(attrDataType, keyAttrName, keyVal, nodeIdList, attr);

						if (ids.size() == 0)
							continue;

						for (String id : ids)
							listOfValList.add(attr.getListAttribute(id, columnNames[j]));
					}

					if (listOfValList.size() == 0) {
						List<Object> valList = new ArrayList<Object>();
						listOfValList.add(valList);
					}

					int index = 0;
					for (List<Object> valList : listOfValList) {
						if (valList == null)
							valList = new ArrayList<Object>();

						if (valList.contains(row[j]) == false)
							valList.add(row[j]);

						if (keyAttrName.equals("ID")) {
							attr.setListAttribute(keyVal, columnNames[j], valList);
							attr.setAttribute(keyVal, columnNames[j] + "-TOP",
							                  valList.get(0).toString());
						} else {
							attr.setListAttribute(ids.get(index), columnNames[j], valList);
							attr.setAttribute(ids.get(index), columnNames[j] + "-TOP",
							                  valList.get(0).toString());

						}
						hitCount++;
						index++;
					}
				}
			}
		}
		
		//System.out.println("Time =====> " + (System.currentTimeMillis()-start));
		
		reader.close();
		reader = null;
		
		report.add("Number of Records (Rows) from BioMart = " + recordCount + "\n");
		report.add("Number of Terms Mapped = " + hitCount + "\n");
		report.add("The following node attributes are created:" + "\n");
		for(String s: columnNames) {
			report.add(s + ", " + s+"-TOP");
		}
		report.add("\nAttributes name ends with \'TOP\' contains first entry of the list");
		return report;
	}

	private List<String> getIdFromAttrValue(final byte attrDataType, final String attrName,
	                                        Object attrValue, List<String> nodeIdList,
	                                        CyAttributes attr) {
		final List<String> idList = new ArrayList<String>();

		String value;

		List<Object> l = null;

		if (attrDataType == CyAttributes.TYPE_SIMPLE_LIST) {
			for (String id : nodeIdList) {
				l = attr.getListAttribute(id, attrName);

				if ((l != null) && (l.size() > 0)) {
					for (Object obj : l) {
						if ((obj != null) && obj.equals(attrValue)) {
							idList.add(id);

							break;
						}
					}
				}
			}
		} else if (attrDataType == CyAttributes.TYPE_STRING) {
			for (String id : nodeIdList) {
				// Extract attribute value from ID
				value = attr.getStringAttribute(id, attrName);

				if ((value != null) && value.equals(attrValue))
					idList.add(id);
			}
		}

		return idList;
	}
}
