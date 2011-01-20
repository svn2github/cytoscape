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
package org.cytoscape.io.webservice.biomart.ui;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.cytoscape.io.webservice.biomart.BiomartQuery;
import org.cytoscape.io.webservice.biomart.rest.Attribute;
import org.cytoscape.io.webservice.biomart.rest.BiomartRestClient;
import org.cytoscape.io.webservice.biomart.rest.Dataset;
import org.cytoscape.io.webservice.biomart.rest.Filter;
import org.cytoscape.io.webservice.biomart.rest.XMLQueryBuilder;
import org.cytoscape.io.webservice.biomart.task.BioMartTaskFactory;
import org.cytoscape.io.webservice.biomart.task.ImportAttributeListTask;
import org.cytoscape.io.webservice.biomart.task.ImportAttributeListTaskFactory;
import org.cytoscape.io.webservice.biomart.task.ImportFilterTask;
import org.cytoscape.io.webservice.biomart.task.ImportFilterTaskFactory;
import org.cytoscape.io.webservice.biomart.task.LoadRepositoryResult;
import org.cytoscape.io.webservice.biomart.task.LoadRepositoryTask;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.ValuedTask;
import org.cytoscape.work.ValuedTaskExecutor;
import org.cytoscape.work.swing.GUITaskManager;

/**
 *
 */
public class BiomartAttrMappingPanel extends AttributeImportPanel {

	private static final long serialVersionUID = 3574198525811249639L;

	private static final Icon LOGO = new ImageIcon(
			BiomartAttrMappingPanel.class
					.getResource("/images/logo_biomart2.png"));

	private Map<String, String> datasourceMap;

	private Map<String, Map<String, String[]>> attributeMap;
	private Map<String, List<String>> attributeListOrder;
	private Map<String, Map<String, String>> attrNameMap;
	private Map<String, Map<String, String>> filterMap;

	public enum SourceType {
		DATABASE, ATTRIBUTE, FILTER;
	}

	private boolean cancelFlag = false;
	private boolean initialized = false;

	// These databases are not compatible with this UI.
	private static final List<String> databaseFilter = new ArrayList<String>();

	private final TaskManager taskManager;
	private final CyApplicationManager appManager;
	private final CyTableManager tblManager;

	private final Window parent;
	
	private final BiomartRestClient restClient;

	/**
	 * Creates a new BiomartNameMappingPanel object.
	 * 
	 * @param logo
	 *            DOCUMENT ME!
	 * @param title
	 *            DOCUMENT ME!
	 * @throws Exception
	 */
	public BiomartAttrMappingPanel(final BiomartRestClient client,
			final TaskManager taskManager,
			final CyApplicationManager appManager,
			final CyTableManager tblManager, final Window parent) {
		super(LOGO, "Biomart", "Import Settings");

		this.restClient = client;
		this.taskManager = taskManager;
		this.appManager = appManager;
		this.tblManager = tblManager;
		this.parent = parent;

		// Access the MartService and get the available services.
		initDataSources();
	}

	/**
	 * Access data sources and build GUI.
	 * 
	 * @throws Exception
	 */
	private void initDataSources() {
		attributeMap = new HashMap<String, Map<String, String[]>>();
		attributeListOrder = new HashMap<String, List<String>>();
		filterMap = new HashMap<String, Map<String, String>>();
		attrNameMap = new HashMap<String, Map<String, String>>();

		// Import list of repositories.
		loadMartServiceList();

		// Load available filters for current source.
		//loadFilter();
	}

	public void loadMartServiceList() {

		final ValuedTask<LoadRepositoryResult> firstTask = new LoadRepositoryTask(
				restClient);
		ValuedTaskExecutor<LoadRepositoryResult> ex = new ValuedTaskExecutor<LoadRepositoryResult>(
				firstTask);
		final BioMartTaskFactory tf = new BioMartTaskFactory(ex);
		System.out.println("Current thread: " + Thread.currentThread());
		((GUITaskManager) taskManager).setParent(parent);
		taskManager.execute(tf);

		LoadRepositoryResult result;
		try {
			result = ex.get();

			this.datasourceMap = result.getDatasourceMap();
			final List<String> dsList = result.getSortedDataSourceList();
			// System.out.println("GOT datasource list from task: " + dsList);
			for (String ds : dsList)
				this.databaseComboBox.addItem(ds);

		} catch (CancellationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadFilter() {
		attributeTypeComboBox.removeAllItems();

		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		fetchData(selectedDBName, SourceType.FILTER);
	}

	protected void resetButtonActionPerformed(ActionEvent e) {
		updateAttributeList();
	}

	protected void databaseComboBoxActionPerformed(ActionEvent evt) {
		updateAttributeList();
		loadFilter();
	}

	private void updateAttributeList() {
		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		List<String> order = attributeListOrder.get(selectedDBName);
		model = new DefaultListModel();
		attrList.setModel(model);

		if (order != null) {
			// List<String> sortedList = new
			// ArrayList<String>(singleAttrMap.keySet());
			// Collections.sort(sortedList);
			for (String dispAttrName : order)
				model.addElement(dispAttrName);
		} else {
			fetchData(selectedDBName, SourceType.ATTRIBUTE);
		}
	}

	private void fetchData(final String datasourceName, SourceType type) {
		Map<String, String> returnValMap = new HashMap<String, String>();
		final List<String> order = new ArrayList<String>();
		final String selectedDB = databaseComboBox.getSelectedItem().toString();
		final String selectedDBName = datasourceMap.get(selectedDB);

		if (type.equals(SourceType.ATTRIBUTE)) {

			System.out.println("Calling attribute update task.");

			final ImportAttributeListTaskFactory tf = new ImportAttributeListTaskFactory(
					datasourceName, restClient);
			final ImportAttributeListTask firstTask = (ImportAttributeListTask) tf
					.getTaskIterator().next();
			taskManager.executeAndWait(tf);
			System.out.println("Calling attribute update task done.");

			Map<String, String[]> attributeVals = firstTask
					.getAttributeValues();
			Map<String, String> names = new HashMap<String, String>();
			attributeMap.put(selectedDBName, attributeVals);
			model.removeAllElements();

			String[] entry;
			String dispNameWithCategory;

			for (String attr : attributeVals.keySet()) {
				entry = attributeVals.get(attr);

				if ((entry != null) && (entry[0] != null)) {
					if ((entry.length > 2) && (entry[2] != null))
						dispNameWithCategory = entry[2] + ": \t" + entry[0]
								+ "\t  (" + attr + ")";
					else
						dispNameWithCategory = " \t" + entry[0] + "\t  ("
								+ attr + ")";

					names.put(dispNameWithCategory, attr);
					order.add(dispNameWithCategory);
				}
			}

			System.out.println("!!!!!!!!! attribute update task 1.");

			this.attrNameMap.put(selectedDBName, names);
			Collections.sort(order);

			for (String attrName : order) {
				model.addElement(attrName);
			}

			System.out.println("!!!!!!!!! attribute update task 2.");
			attributeListOrder.put(selectedDBName, order);
			// attrList.repaint();

			System.out.println("!!!!!!!!! attribute update task done.");

		} else if (type.equals(SourceType.FILTER)) {

			final ImportFilterTaskFactory tf = new ImportFilterTaskFactory(
					datasourceName, restClient);
			final ImportFilterTask firstTask = (ImportFilterTask) tf
					.getTaskIterator().next();
			tf.setTask(firstTask);
			taskManager.executeAndWait(tf);
			tf.setTask(null);

			returnValMap = firstTask.getFilters();
			filterMap.put(selectedDBName, returnValMap);

			List<String> filterNames = new ArrayList<String>(
					returnValMap.keySet());
			Collections.sort(filterNames);

			for (String filter : filterNames)
				attributeTypeComboBox.addItem(filter);
		}
	}

	protected void importButtonActionPerformed(ActionEvent evt) {
		importAttributes();
	}

	private String getIDFilterString(final String keyAttrName) {

		// TODO fix tunables
		// final Tunable tunable =
		// WebServiceClientManager.getClient("biomart").getProps().get("selected_only");
		// tunable.updateValue();
		// final Object value = tunable.getValue();

		// if (value != null && Boolean.parseBoolean(value.toString())) {
		// // Selected nodes only
		// nodes = new
		// ArrayList<Node>(Cytoscape.getCurrentNetwork().getSelectedNodes());
		// } else {
		// // Send all nodes in current network
		// nodes = Cytoscape.getCurrentNetwork().nodesList();
		// }

		final CyNetwork curNetwork = appManager.getCurrentNetwork();
		final List<CyNode> nodes = curNetwork.getNodeList();

		final StringBuilder builder = new StringBuilder();

		// // If attribute name is ID, then use node id as the key.
		// if (keyAttrName.equals("ID")) {
		// for (Node n : nodes) {
		// builder.append(n.getIdentifier());
		// builder.append(",");
		// }
		// } else {
		// Use Attributes for mapping
		final CyTable defTable = tblManager.getTableMap(CyNode.class,
				curNetwork).get(CyNetwork.DEFAULT_ATTRS);
		final Class<?> attrDataType = defTable.getColumnTypeMap().get(
				keyAttrName);
		for (CyNode node : nodes) {
			final CyRow row = defTable.getRow(node.getSUID());

			Object value = row.get(keyAttrName, attrDataType);
			if (value instanceof List) {
				List<?> values = (List<?>) value;
				for (Object val : values) {
					builder.append(val.toString());
					builder.append(",");
				}
			} else {
				builder.append(value.toString());
				builder.append(",");
			}
		}

		// if ((mapAttrs == null) || (mapAttrs.size() == 0))
		// return null;
		//
		// // List acceptedClasses =
		// Arrays.asList(mapping.getAcceptedDataClasses());
		// // Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);
		// //
		// // if ((mapAttrClass == null) ||
		// !(acceptedClasses.contains(mapAttrClass)))
		// // return null;
		// for (String key : loadKeySet(mapAttrs)) {
		// builder.append(key);
		// builder.append(",");
		// }
		// }

		String filterStr = builder.toString();
		filterStr = filterStr.substring(0, filterStr.length() - 1);

		return filterStr;
	}

	// private static Set<String> loadKeySet(final Map mapAttrs) {
	// final Set<String> mappedKeys = new TreeSet<String>();
	//
	// final Iterator keyIter = mapAttrs.values().iterator();
	//
	// Object o = null;
	//
	// while (keyIter.hasNext()) {
	// o = keyIter.next();
	//
	// if (o instanceof List) {
	// List list = (List) o;
	//
	// for (int i = 0; i < list.size(); i++) {
	// Object vo = list.get(i);
	//
	// if (!mappedKeys.contains(vo))
	// mappedKeys.add(vo.toString());
	// }
	// } else {
	// if (!mappedKeys.contains(o))
	// mappedKeys.add(o.toString());
	// }
	// }
	//
	// return mappedKeys;
	// }

	private BiomartQuery importAttributesFromService(Dataset dataset,
			Attribute[] attrs, Filter[] filters, String keyInHeader,
			String keyAttrName) {

		final String query = XMLQueryBuilder.getQueryString(dataset, attrs,
				filters);
		
		return new BiomartQuery(query, keyAttrName);
		

		// AttributeImportQuery qObj = new AttributeImportQuery(query2, key,
		// keyAttrName);

	}

	@Override
	protected void importAttributes() {
		

	}
	
	public BiomartQuery getTableImportQuery() {
		final String datasource = datasourceMap.get(databaseComboBox
				.getSelectedItem());
		final Map<String, String> attrMap = this.attrNameMap.get(datasource);
		final Map<String, String> fMap = filterMap.get(datasource);

		final String keyAttrName = attributeComboBox.getSelectedItem()
				.toString();

		System.out.println("Target attr name found: " + keyAttrName);

		Dataset dataset;
		Attribute[] attrs;
		Filter[] filters;

		// Name of the data source
		dataset = new Dataset(datasource);
		// System.out.println("Target Dataset = " + dataset.getName());

		final Object[] selectedAttr = attrList.getSelectedValues();
		attrs = new Attribute[selectedAttr.length + 1];

		// This is the mapping key
		String filterName = fMap.get(attributeTypeComboBox.getSelectedItem());
		String dbName = this.databaseComboBox.getSelectedItem().toString();
		// System.out.println("Filter Name = " + filterName);

		// Database-specific modification.
		// This is not the best way, but cannot provide universal solution.

		// FIXME
		// if (dbName.contains("REACTOME")) {
		// attrs[0] = new Attribute(stub.toAttributeName("REACTOME",
		// filterName));
		// } else
		if (dbName.contains("VARIATION")) {
			// String newName = filterName.replace("_id", "_stable_id");
			// newName = newName.replace("_ensembl", "");
			attrs[0] = new Attribute(filterName + "_stable_id");
		} else {
			attrs[0] = new Attribute(filterName);
		}

		for (int i = 1; i <= selectedAttr.length; i++) {
			attrs[i] = new Attribute(attrMap.get(selectedAttr[i - 1]));
		}

		// For name mapping, just use ID list filter for query.
		filters = new Filter[1];

		filters[0] = new Filter(filterName, getIDFilterString(keyAttrName));

		String keyInHeader = null;

		for (String key : attrMap.keySet()) {
			if (attrMap.get(key).equals(filterName)) {
				keyInHeader = key.split("\\t")[1];
				// System.out.println("Key Attr = " + keyInHeader);
			}
		}

		// Create query
		return importAttributesFromService(dataset, attrs, filters, keyInHeader,
				keyAttrName);
	}

}
