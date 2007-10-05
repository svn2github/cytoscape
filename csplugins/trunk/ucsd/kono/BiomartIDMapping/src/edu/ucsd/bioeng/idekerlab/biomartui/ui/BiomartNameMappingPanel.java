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
package edu.ucsd.bioeng.idekerlab.biomartui.ui;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.data.webservice.AttributeImportQuery;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.swing.AttributeImportPanel;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Attribute;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Dataset;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.Filter;
import edu.ucsd.bioeng.idekerlab.biomartclient.utils.XMLQueryBuilder;
import giny.model.Node;


/**
 *
 */
public class BiomartNameMappingPanel extends AttributeImportPanel implements PropertyChangeListener {

	private static final String ATTR_BASE_URL = "http://www.biomart.org/biomart/martservice?virtualschema=default&type=attributes&dataset=";
	private static final String FILTER_BASE_URL = "http://www.biomart.org/biomart/martservice?virtualschema=default&type=filters&dataset=";
	private static final Icon LOGO = new ImageIcon(BiomartNameMappingPanel.class.getResource("/images/logo_biomart2.png"));
	private Map<String, String> datasourceMap;
	private Map<String, Map<String, String>> attributeMap;
	private Map<String, List<String>> attributeListOrder;
	private Map<String, Map<String, String>> filterMap;
	private enum SourceType {
		DATABASE,
		ATTRIBUTE,
		FILTER;
	}


	public BiomartNameMappingPanel() throws IOException {
		this(LOGO, "Biomart ID Mapping",  "Available attributes");
	}
	/**
	 * Creates a new BiomartNameMappingPanel object.
	 *
	 * @param logo  DOCUMENT ME!
	 * @param title  DOCUMENT ME!
	 *
	 * @throws IOException  DOCUMENT ME!
	 */
	public BiomartNameMappingPanel(Icon logo, String title, String attrPanelLabel) throws IOException {
		super(logo, title, attrPanelLabel);
		initDataSources();
	}

	private void initDataSources() throws IOException {
		datasourceMap = new HashMap<String, String>();
		attributeMap = new HashMap<String, Map<String, String>>();
		attributeListOrder = new HashMap<String, List<String>>();
		filterMap = new HashMap<String, Map<String, String>>();
		
		loadDBList();
		loadFilter();
	}

	private void loadDBList() throws IOException {
		InputStreamReader inFile;

		inFile = new InputStreamReader(this.getClass().getResource("/resource/databaselist.txt")
		                                   .openStream());

		BufferedReader inBuffer = new BufferedReader(inFile);

		String line;
		String trimed;

		while ((line = inBuffer.readLine()) != null) {
			trimed = line.trim();
			System.out.println("DB-------------> " + trimed);

			String[] dbparts = trimed.split("\\t");

			URL url = new URL(dbparts[1]);
			URLConnection uc = url.openConnection();
			InputStream is = uc.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String s;

			List<String> datasources = new ArrayList<String>();

			while ((s = reader.readLine()) != null) {
				String[] parts = s.split("\\t");

				if ((parts.length > 4) && parts[3].equals("1")) {
					System.out.println("============DataSource: " + s);
					//					if (parts[1].endsWith("_gene_ensembl")) {
					datasources.add(dbparts[0] + " - " + parts[2]);

					datasourceMap.put(dbparts[0] + " - " + parts[2], parts[1]);

					//					}
				}
			}

			reader.close();
			Collections.sort(datasources);

			for (String ds : datasources) {
				this.databaseComboBox.addItem(ds);
			}
		}

		inBuffer.close();
	}

	private void loadFilter() throws IOException {
		attributeTypeComboBox.removeAllItems();

		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		fetchData(new URL(FILTER_BASE_URL + selectedDBName), SourceType.FILTER);
	}

	private void attributeTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		// TODO add your handling code here:
	}

	protected void resetButtonActionPerformed(ActionEvent e) {
		updateAttributeList();
	}
	
	protected void databaseComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		
		updateAttributeList();

		// Then update filter list
		try {
			loadFilter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private void updateAttributeList() {
		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		Map<String, String> singleAttrMap = attributeMap.get(selectedDBName);
		List<String> order = attributeListOrder.get(selectedDBName);
		model = new DefaultListModel();
		attrList.setModel(model);

		if (order != null) {
//			List<String> sortedList = new ArrayList<String>(singleAttrMap.keySet());
//			Collections.sort(sortedList);
			for(String dispAttrName : order) {
				model.addElement(dispAttrName);
			}
		} else {
			try {
				fetchData(new URL(ATTR_BASE_URL + selectedDBName), SourceType.ATTRIBUTE);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void fetchData(final URL url, SourceType type) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String s;
		String[] parts;

		final Map<String, String> singleMap = new HashMap<String, String>();
		final List<String> order = new ArrayList<String>();
		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		if (type.equals(SourceType.ATTRIBUTE)) {
			while ((s = reader.readLine()) != null) {
				parts = s.split("\\t");

				if (parts.length > 1) {
					System.out.println(parts[1]);

					if (parts[1].endsWith("ID") || parts[1].toUpperCase().startsWith("AFFY") 
							|| parts[1].toUpperCase().contains("SYMBOL") || parts[1].toUpperCase().contains("ACCESSION")) {
						model.addElement(parts[1]);
						singleMap.put(parts[1], parts[0]);
						order.add(parts[1]);
					}
				}
			}

			attributeMap.put(selectedDBName, singleMap);
			attributeListOrder.put(selectedDBName, order);
			attrList.repaint();
		} else if (type.equals(SourceType.FILTER)) {
			while ((s = reader.readLine()) != null) {
				//				System.out.println("Filter---> " + s);
				parts = s.split("\\t");

				if ((parts.length > 1)
				    && (parts[1].endsWith("ID(s)") || parts[1].endsWith("Accession(s)"))
				    && (parts[0].startsWith("with_") == false)) {
					System.out.println(parts[1]);
					this.attributeTypeComboBox.addItem(parts[1]);
					singleMap.put(parts[1], parts[0]);
				}
			}

			filterMap.put(selectedDBName, singleMap);
		}

		reader.close();
	}

	protected void importButtonActionPerformed(ActionEvent evt) {
		final String datasource = datasourceMap.get(databaseComboBox.getSelectedItem());
		final Map<String, String> attrMap = attributeMap.get(datasource);
		final Map<String, String> fMap = filterMap.get(datasource);

		final String keyAttrName = attributeComboBox.getSelectedItem().toString();
		
		System.out.println("##### Target attr name found: " + keyAttrName);

		Dataset dataset;
		Attribute[] attrs;
		Filter[] filters;

		// Name of the datasource
		dataset = new Dataset(datasource);
		System.out.println("======dataset = " + dataset.getName());

		final Object[] selectedAttr = attrList.getSelectedValues();
		attrs = new Attribute[selectedAttr.length + 1];

		// This is the mapping key
		String filterName = fMap.get(attributeTypeComboBox.getSelectedItem());
		attrs[0] = new Attribute(filterName);
		System.out.println("======ATTR Key = " + attrs[0].getName());

		for (int i = 1; i <= selectedAttr.length; i++) {
			attrs[i] = new Attribute(attrMap.get(selectedAttr[i - 1]));
			System.out.println("======ATTR = " + attrs[i].getName());
		}

		for (Attribute at : attrs) {
			System.out.println("Result -----------------> " + at.getName());
		}

		// For name mapping, just use ID list filter for query.
		filters = new Filter[1];
		filters[0] = new Filter(filterName, getIDFilterString());

		String keyInHeader = null;

		for (String key : attrMap.keySet()) {
			if (attrMap.get(key).equals(filterName)) {
				keyInHeader = key;
				System.out.println("Key in header = " + keyInHeader);
			}
		}

		// Create Task
		final ImportAttributeTask task = new ImportAttributeTask(dataset, attrs, filters,
		                                                         keyInHeader, keyAttrName);

		// Configure JTask Dialog Pop-Up Box
		final JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);

		//mart.execute("getDatabaseList", new Class[] {  }, new Object[] {  });
	}

	

	private static String getIDFilterString() {
		final List<Node> nodes = Cytoscape.getRootGraph().nodesList();
		final StringBuilder builder = new StringBuilder();

		for (Node n : nodes) {
			builder.append(n.getIdentifier());
			builder.append(",");
		}

		String filterStr = builder.toString();
		filterStr = filterStr.substring(0, filterStr.length() - 1);

		System.out.println("Filter =====>>> " + filterStr);

		return filterStr;
	}

	@Override
	protected void importAttributes() {
		// Build Query
	}

	private class ImportAttributeTask implements Task {
		private Dataset dataset;
		private Attribute[] attrs;
		private Filter[] filters;
		private String key;
		private String keyAttrName;
		private TaskMonitor taskMonitor;

		public ImportAttributeTask(Dataset dataset, Attribute[] attrs, Filter[] filters,
		                           String key, String keyAttrName) {
			this.dataset = dataset;
			this.attrs = attrs;
			this.filters = filters;
			this.key = key;
			this.keyAttrName = keyAttrName;
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @return  DOCUMENT ME!
		 */
		public String getTitle() {
			// TODO Auto-generated method stub
			return "Loading Attributes from Web Service";
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void halt() {
			// TODO Auto-generated method stub
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void run() {
			taskMonitor.setStatus("Accessing Biomart Web Service.\n\nIt may take a while.\nPlease wait...");
			taskMonitor.setPercentCompleted(-1);

			String query2 = XMLQueryBuilder.getQueryString(dataset, attrs, filters);
			
			AttributeImportQuery qObj = new AttributeImportQuery(query2, key, keyAttrName);

			WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(new CyWebServiceEvent("biomart", WSEventType.IMPORT_ATTRIBUTE, qObj));
			


			taskMonitor.setPercentCompleted(100);
			taskMonitor.setStatus("Attributes successfully loaded.");

		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param arg0 DOCUMENT ME!
		 *
		 * @throws IllegalThreadStateException DOCUMENT ME!
		 */
		public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
			this.taskMonitor = taskMonitor;
		}
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
