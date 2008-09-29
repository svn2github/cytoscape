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
package org.cytoscape.ws.client.picr;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.CyWebServiceException.WSErrorCode;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientImplWithGUI;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.data.webservice.ui.WebServiceClientGUI;

import cytoscape.layout.Tunable;

import cytoscape.util.ModulePropertiesImpl;

import ebi.picr.AccessionMapperInterface;
import ebi.picr.AccessionMapperService;
import ebi.picr.AccessionMapperService_Impl;
import ebi.picr.CrossReference;
import ebi.picr.UPEntry;

import giny.model.Node;

import org.cytoscape.ws.client.picr.ui.PICRPanel;

import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.naming.ConfigurationException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;


/**
 *
 */
public class PICRClient extends WebServiceClientImplWithGUI<AccessionMapperInterface, JPanel> {
	// Client name
	private static final String DISPLAY_NAME = "PICR Name Mapping Service Client";

	// Client ID
	private static final String CLIENT_ID = "picr";

	// Actual biomart client.
	private static WebServiceClient<AccessionMapperInterface> client;
	private ConcurrentHashMap<String, Map<String, List<String>>> idMap = new ConcurrentHashMap<String, Map<String, List<String>>>();
	private static final Icon LOGO = new ImageIcon(PICRPanel.class.getResource("/images/ims-logo-small.jpg"));

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 * @throws Exception
	 */
	public static WebServiceClient<AccessionMapperInterface> getClient() throws Exception {
		if (client == null) {
			client = new PICRClient();
		}

		return client;
	}

	/**
	 * Creates a new Biomart Client object.
	 *
	 * @throws ServiceException
	 * @throws ConfigurationException
	 */
	public PICRClient() throws Exception {
		super(CLIENT_ID, DISPLAY_NAME, new ClientType[] { ClientType.ATTRIBUTE }, null, null, null);

		final AccessionMapperService mapper = new AccessionMapperService_Impl();
		clientStub = mapper.getAccessionMapperPort();

		// Set properties
		props = new ModulePropertiesImpl(clientID, "wsc");

		props.add(new Tunable("import_all", "Import all available entries", Tunable.BOOLEAN,
		                      new Boolean(false)));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public JPanel getGUI() {
		if (gui == null) {
			try {
				gui = new PICRPanel();
			} catch (CyWebServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return gui;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param size DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Icon getIcon(WebServiceClientGUI.IconSize size) {
		return LOGO;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 * @throws CyWebServiceException
	 */
	@Override
	public void executeService(CyWebServiceEvent e) throws CyWebServiceException {
		// TODO Auto-generated method stub
		if (e.getSource().equals(CLIENT_ID)) {
			if (e.getEventType().equals(WSEventType.IMPORT_ATTRIBUTE)) {
				importAttributes((AttributeImportQuery) e.getParameter());
			}
		}
	}

	private static Set<String> loadKeySet(final Map mapAttrs) {
		final Set<String> mappedKeys = new TreeSet<String>();

		final Iterator keyIter = mapAttrs.values().iterator();

		Object o = null;

		while (keyIter.hasNext()) {
			o = keyIter.next();

			if (o instanceof List) {
				List list = (List) o;

				for (int i = 0; i < list.size(); i++) {
					Object vo = list.get(i);

					if (!mappedKeys.contains(vo))
						mappedKeys.add(vo.toString());
				}
			} else {
				if (!mappedKeys.contains(o))
					mappedKeys.add(o.toString());
			}
		}

		return mappedKeys;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param parameter DOCUMENT ME!
	 * @throws CyWebServiceException
	 */
	public void importAttributes(AttributeImportQuery parameter) throws CyWebServiceException {
		Object[] selectedAttrs = (Object[]) parameter.getParameter();
		final String[] dbNames = new String[selectedAttrs.length];

		int len = dbNames.length;
		for(int i=0; i<len; i++) {
			dbNames[i] = selectedAttrs[i].toString();
			System.out.println("Selected DB: " + dbNames[i]);
		}
		
		final String keyAttr = parameter.getKeyCyAttrName();
		System.out.println("Selected: " + keyAttr);

		long startTime = System.currentTimeMillis();
//		ExecutorService e = Executors.newSingleThreadExecutor();

		final List<Node> nodes = Cytoscape.getRootGraph().nodesList();

		
		ImportIDMapTask task = null;
		
		if (keyAttr.equals("ID")) {
			for (Node node : nodes) {
				System.out.println("Thread start for: " + node.getIdentifier());
//				e.submit(new ImportIDMapTask(node.getIdentifier(), null, dbNames, null, false));
				
				task = new ImportIDMapTask(node.getIdentifier(), null, dbNames, null, false);
				task.run();
				
			}
		} else {
			// Use Attributes for mapping
			final CyAttributes attrs = Cytoscape.getNodeAttributes();
			final Map mapAttrs = CyAttributesUtils.getAttribute(keyAttr, attrs);

			if ((mapAttrs == null) || (mapAttrs.size() == 0))
				return;

			for (String key : loadKeySet(mapAttrs)) {
//				e.submit(new ImportIDMapTask(key, null, dbNames, null, false));
				
				task = new ImportIDMapTask(key, null, dbNames, null, false);
				task.run();
			}
		}

//		try {
//			e.shutdown();
//			e.awaitTermination(600, TimeUnit.SECONDS);

			long endTime = System.currentTimeMillis();
			double msec = (endTime - startTime) / 1000.0;
			System.out.println("Data import finished in " + msec + " sec.");

			CyAttributes nodeAttr = Cytoscape.getNodeAttributes();

			Map<String, List<String>> attrVal;

			for (String key : idMap.keySet()) {
				attrVal = idMap.get(key);

				for (String dbName : attrVal.keySet()) {
					if (keyAttr.equals("ID")) {
						nodeAttr.setListAttribute(key, dbName, attrVal.get(dbName));
					} else {
						List<String> ids = CyAttributesUtils.getIDListFromAttributeValue(CyAttributesUtils.AttributeType.NODE,
						                                                                 keyAttr,
						                                                                 key);

						for (String id : ids) {
							nodeAttr.setListAttribute(id, dbName, attrVal.get(dbName));
						}
					}
				}
			}

			Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			System.out.println("=======================TIMEOUT 2=================================");
//			e1.printStackTrace();
//		}
	}

	class ImportIDMapTask  {
		private String accession;
		String version;
		String[] dbList;
		String taxonID;
		boolean active;

		public ImportIDMapTask(String accession, String version, String[] dbList,
		                       String taxonID, boolean active) {
			this.accession = accession;
			this.version = version;
			this.dbList = dbList;
			this.taxonID = taxonID;
			this.active = active;
		}

		private void extractEntry(CrossReference[] crossReferences, Map<String, List<String>> oneMap) {

			String dbName = null;
			String id = null;

			for (CrossReference cr: crossReferences) {
				dbName = cr.getDatabaseName();
				id = cr.getAccession();
				

				if ((dbName != null) && (id != null)) {
					List<String> dbEntries = oneMap.get(dbName);

					if (dbEntries == null) {
						dbEntries = new ArrayList<String>();
						dbEntries.add(id);
					} else {
						dbEntries.add(id);
					}

					// Remove redundant entry
					Set<String> set = new TreeSet(dbEntries);

					oneMap.put(dbName, new ArrayList<String>(set));

					break;
				}
			}
		}

		public void run() {
			UPEntry[] res = null;
			System.out.println("ID = " + accession);
			
			final AccessionMapperService mapper = new AccessionMapperService_Impl();
			AccessionMapperInterface clientStub2 = null;
			try {
				clientStub2 = mapper.getAccessionMapperPort();
			} catch (ServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			try {
				res = clientStub2.getUPIForAccession(accession, version, dbList, null, false);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (res == null)
				return;
			
			final Map<String, List<String>> oneMap = new HashMap<String, List<String>>();

			List<String> upiList = new ArrayList<String>();

			for (UPEntry entry: res) {
				upiList.add(entry.getUPI());
						
				extractEntry(entry.getIdenticalCrossReferences(), oneMap);
				extractEntry(entry.getLogicalCrossReferences(), oneMap);
				

				oneMap.put("UNIPARC", upiList);
				idMap.put(accession, oneMap);
			}

			System.out.println("Got data for " + accession);
		}
	}
}
