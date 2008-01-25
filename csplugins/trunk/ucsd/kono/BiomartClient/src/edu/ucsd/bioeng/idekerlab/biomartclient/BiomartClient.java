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

import java.util.ArrayList;
import java.util.List;

import javax.naming.ConfigurationException;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImpl;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.layout.Tunable;
import cytoscape.util.ModulePropertiesImpl;

/**
 * Biomart Web Service Client.
 * 
 * @author kono
 * @version 0.6
 * @since Cytoscape 2.6
 * 
 */
public class BiomartClient extends WebServiceClientImpl {

	// Client name
	private static final String DISPLAY_NAME = "Biomart Web Service Client";
	
	// Client ID
	private static final String CLIENT_ID = "biomart";
	
	// Actual biomart client.
	private static WebServiceClient client;
	
	// Biomart base URL
	private static final String BASE_URL = "http://www.biomart.org/biomart/martservice";

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 * @throws Exception 
	 */
	public static WebServiceClient getClient() throws Exception {
		if(client == null) {
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
		stub = new BiomartStub(BASE_URL);
		
		// Set properties
		props = new ModulePropertiesImpl(clientID, "wsc");

//		props.add(new Tunable("max_interactions", "Maximum number of records", Tunable.INTEGER,
//		                      new Integer(1000)));

		//props.add(new Tunable("search_depth", "Search depth", Tunable.INTEGER, new Integer(0)));
		props.add(new Tunable("import_all", "Import all available entries",
		                      Tunable.BOOLEAN, new Boolean(false)));
		
		props.add(new Tunable("show_all_filter", "Show all available filters",
                Tunable.BOOLEAN, new Boolean(false)));
		props.add(new Tunable("base_url", "Biomart Base URL",
                Tunable.STRING, BASE_URL ));
	}

	/**
	 * Execute service based on events.
	 */
	@Override
	public void executeService(CyWebServiceEvent e) throws Exception {
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAttributes((AttributeImportQuery) e.getParameter());
			}
		}
	}	
	
	
	/**
	 * Based on the query given, execute the data fetching.
	 * 
	 * @param query
	 * @throws Exception 
	 */
	private void importAttributes(AttributeImportQuery query) throws Exception {
		
			List<String[]> result = ((BiomartStub)stub).sendQuery(query.getParameter().toString());
			
			if(((List<String[]>) result).size() == 1) {
				String[] res = ((List<String[]>) result).get(0);
				if(res[0].contains("Query ERROR")) {
					Exception e = new Exception(res[0]);
					
					throw e;
				}
			}
	
			mapping((List<String[]>) result, query.getKeyCyAttrName(), query.getKeyNameInWebService());
			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
	}
	
	private void mapping(List<String[]> result, String key, String keyAttrName) {
		final String[] columnNames = result.get(0);
		final int rowCount = result.size();
		final int colSize = columnNames.length;
		int keyPosition = 0;

		
		
		for (int i = 0; i < colSize; i++) {
			if (columnNames[i].equals(key)) {
				keyPosition = i;
			}
		}

		String[] entry;
		String val;

		List<List<Object>> listOfValList;
		List<String> ids = null;

		List<Object> testList;
		String keyVal = null;

		for (int i = 1; i < rowCount; i++) {
			entry = result.get(i);

			if (entry.length <= keyPosition) {
				continue;
			}

			keyVal = entry[keyPosition];

			for (int j = 0; j < entry.length; j++) {
				val = entry[j];

				if ((val != null) && (val.length() != 0) && (j != keyPosition)) {
					listOfValList = new ArrayList<List<Object>>();

					if (keyAttrName.equals("ID")) {

						testList = Cytoscape.getNodeAttributes()
                        .getListAttribute(keyVal, columnNames[j]);
						if(testList != null) {
							listOfValList.add(testList);
						}
					} else {
						ids = CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE,
						                                                                 keyAttrName,
						                                                                 keyVal);

						for (String id : ids) {
							listOfValList.add(Cytoscape.getNodeAttributes()
							                           .getListAttribute(id, columnNames[j]));
						}
					}

					if (listOfValList.size() == 0) {
						List<Object> valList = new ArrayList<Object>();
						listOfValList.add(valList);
					}

					int index = 0;
					
					for (List<Object> valList : listOfValList) {
						if (valList == null) {
							valList = new ArrayList<Object>();
						}

						if (valList.contains(entry[j]) == false) {
							valList.add(entry[j]);
						}

						if (keyAttrName.equals("ID")) {
							Cytoscape.getNodeAttributes()
						         .setListAttribute(keyVal, columnNames[j], valList);
							Cytoscape.getNodeAttributes()
					         .setAttribute(keyVal, columnNames[j]+"-TOP", valList.get(0).toString());
						} else {
							Cytoscape.getNodeAttributes()
					         .setListAttribute(ids.get(index), columnNames[j], valList);
							Cytoscape.getNodeAttributes()
					         .setAttribute(ids.get(index), columnNames[j]+"-TOP", valList.get(0).toString());
						}
						index++;
					}
				}
			}
		}
	}
	
	
	
	
}
