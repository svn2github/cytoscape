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

import giny.model.Node;

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

/**
 *
 */
public class BiomartClient extends WebServiceClientImpl {
	private static final String DISPLAY_NAME = "Biomart Web Service Client";
	private static final String CLIENT_ID = "biomart";
	
	private static WebServiceClient client = null;
	
	static {
		try {
			client = new BiomartClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
		stub = new BiomartStub();
	}

	@Override
	public void executeService(CyWebServiceEvent e) {
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
	 */
	private void importAttributes(AttributeImportQuery query) {
		
		try {
			List<String[]> result = ((BiomartStub)stub).sendQuery(query.getParameter().toString());
			
			if(((List<String[]>) result).size() == 1) {
				String[] res = ((List<String[]>) result).get(0);
				if(res[0].contains("Query ERROR")) {
					Exception e = new Exception(res[0]);
					
					throw e;
				}
			}
			System.out.println("--------------------Got result: " + result.size());
			mapping((List<String[]>) result, query.getKeyCyAttrName(), query.getKeyNameInWebService());
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	private static void mapping(List<String[]> result, String key, String keyAttrName) {
		final String[] columnNames = result.get(0);
		final int rowCount = result.size();
		final int colSize = columnNames.length;
		int keyPosition = 0;

		
		
		for (int i = 0; i < colSize; i++) {
			System.out.println("Key = " + key +", colname = " + columnNames[i] + ", Key attr name = " + keyAttrName);
			if (columnNames[i].equals(key)) {
				keyPosition = i;
				System.out.println("Key found!!!!!!!!!!!!" + i);
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
						} else {
							Cytoscape.getNodeAttributes()
					         .setListAttribute(ids.get(index), columnNames[j], valList);
						}
						index++;
					}
				}
			}
		}
	}
	
	
	
	
}
