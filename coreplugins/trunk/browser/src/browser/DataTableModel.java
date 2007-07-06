
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

package browser;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.attr.MultiHashMapListener;

import cytoscape.visual.GlobalAppearanceCalculator;

import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;


/**
 *
 * Actual data manipulation is implemented here.<br>
 *
 * @author kono
 * @author xmas
 */
public class DataTableModel extends DefaultTableModel implements SortTableModel,
                                                                 MultiHashMapListener {
	// Property for this browser. One for each panel.
	private Properties props;
	private CyAttributes data;
	private List<GraphObject> graphObjects;
	private List attributeNames;

	/**
	 * 
	 */
	public static final String LS = System.getProperty("line.separator");
	private static final Boolean DEFAULT_FLAG = new Boolean(false);
	private int objectType = DataTable.NODES;

	/*
	 * Selected nodes & edges color
	 */
	private Color selectedNodeColor;
	private Color selectedEdgeColor;

	// will be used by internal selection.
	private HashMap internalSelection = null;

	/**
	 * Creates a new DataTableModel object.
	 */
	public DataTableModel() {
		initialize();
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param rows  DOCUMENT ME!
	 * @param cols  DOCUMENT ME!
	 */
	public DataTableModel(int rows, int cols) {
		super(rows, cols);
		initialize();
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public DataTableModel(Object[][] data, Object[] names) {
		super(data, names);
		initialize();
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param names  DOCUMENT ME!
	 * @param rows  DOCUMENT ME!
	 */
	public DataTableModel(Object[] names, int rows) {
		super(names, rows);
		initialize();
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param names  DOCUMENT ME!
	 * @param rows  DOCUMENT ME!
	 */
	public DataTableModel(Vector names, int rows) {
		super(names, rows);
		initialize();
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public DataTableModel(Vector data, Vector names) {
		super(data, names);
		initialize();
	}

	/*
	 * Initialize properties for Browser Plugin.
	 */
	private void initialize() {
		props = new Properties();
		props.setProperty("colorSwitch", "off");
		setSelectedColor(JSortTable.SELECTED_NODE);
		setSelectedColor(JSortTable.SELECTED_EDGE);
	}

	protected void setSelectedColor(final int type) {
		final GlobalAppearanceCalculator gac = Cytoscape.getVisualMappingManager().getVisualStyle()
		                                                .getGlobalAppearanceCalculator();

		switch (type) {
			case JSortTable.SELECTED_NODE:
				selectedNodeColor = gac.getDefaultNodeSelectionColor();

				break;

			case JSortTable.SELECTED_EDGE:
				selectedEdgeColor = gac.getDefaultEdgeSelectionColor();

				break;

			default:
				break;
		}
	}

	protected Color getSelectedColor(final int type) {
		Color newColor;
		final GlobalAppearanceCalculator gac = Cytoscape.getVisualMappingManager().getVisualStyle()
		                                                .getGlobalAppearanceCalculator();

		switch (type) {
			case JSortTable.SELECTED_NODE:
				newColor = gac.getDefaultNodeSelectionColor();

				break;

			case JSortTable.SELECTED_EDGE:
				newColor = gac.getDefaultEdgeSelectionColor();

				break;

			default:
				newColor = null;

				break;
		}

		return newColor;
	}

	protected void setColorSwitch(final boolean flag) {
		if (flag) {
			props.setProperty("colorSwitch", "on");
		} else {
			props.setProperty("colorSwitch", "off");
		}
	}

	protected boolean getColorSwitch() {
		if (props.getProperty("colorSwitch").equals("on")) {
			return true;
		} else {
			return false;
		}
	}

	// Accept CyAttributes and create table
	/**
	 *  DOCUMENT ME!
	 *
	 * @param data DOCUMENT ME!
	 * @param graph_objects DOCUMENT ME!
	 * @param attributeNames DOCUMENT ME!
	 * @param objectType DOCUMENT ME!
	 */
	public void setTableData(CyAttributes data, List<GraphObject> graph_objects, List attributeNames,
	                         int objectType) {
		this.data = data;
		this.graphObjects = graph_objects;
		this.attributeNames = attributeNames;
		this.objectType = objectType;
		data.getMultiHashMap().addDataListener(this);

		if (objectType == DataTable.NETWORK) {
			setNetworkTable();
		} else {
			setTable();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Map getSelectionArray() {
		return internalSelection;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param key DOCUMENT ME!
	 * @param flag DOCUMENT ME!
	 */
	public void setSelectionArray(final String key, final boolean flag) {
		internalSelection.put(key, new Boolean(flag));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void resetSelectionFlags() {
		if (this.objectType != DataTable.NETWORK) {
			final Iterator it = graphObjects.iterator();

			while (it.hasNext()) {
				internalSelection.put(((GraphObject) it.next()).getIdentifier(), DEFAULT_FLAG);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getObjects() {
		return graphObjects;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param graph_objects DOCUMENT ME!
	 * @param attributes DOCUMENT ME!
	 */
	public void setTableData(List graph_objects, List attributes) {
		this.graphObjects = graph_objects;
		this.attributeNames = attributes;

		if (this.objectType != DataTable.NETWORK) {
			setTable();
		} else {
			setNetworkTable();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributes DOCUMENT ME!
	 */
	public void setTableDataAttributes(final List attributes) {
		this.attributeNames = attributes;

		if (this.objectType != DataTable.NETWORK) {
			setTable();
		} else {
			setNetworkTable();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param graph_objects DOCUMENT ME!
	 */
	public void setTableDataObjects(final List graph_objects) {
		this.graphObjects = graph_objects;

		if (this.objectType != DataTable.NETWORK) {
			setTable();
		} else {
			setNetworkTable();
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param ot DOCUMENT ME!
	 */
	public void setObjectType(final int ot) {
		objectType = ot;
	}

	protected void setNetworkTable() {
		if (Cytoscape.getCurrentNetwork() == null) {
			return;
		}

		final int att_length = attributeNames.size();

		// Attribute names will be the row id, and num. of column is always
		Object[][] data_vector = new Object[att_length][2];
		Object[] column_names = new Object[2];

		column_names[0] = "Network Attribute Name";
		column_names[1] = "Value";

		for (int i = 0; i < att_length; i++) {
			final String attributeName = (String) attributeNames.get(i);
			data_vector[i][0] = attributeName;
			data_vector[i][1] = getAttributeValue(data.getType(attributeName),
			                                      Cytoscape.getCurrentNetwork().getIdentifier(),
			                                      attributeName);
		}

		setDataVector(data_vector, column_names);
	}

	protected void setAllNetworkTable() {
		int att_length = attributeNames.size() + 1;
		int networkCount = Cytoscape.getNetworkSet().size();

		Object[][] data_vector = new Object[networkCount][att_length];
		Object[] column_names = new Object[att_length];
		column_names[0] = DataTable.ID;

		internalSelection = new HashMap();

		Iterator it = Cytoscape.getNetworkSet().iterator();
		int k = 0;

		while (it.hasNext()) {
			CyNetwork network = (CyNetwork) it.next();
			String id = network.getIdentifier();

			data_vector[k][0] = id;
			k++;
		}

		// Set actual data
		for (int idx = 0; idx < attributeNames.size(); ++idx) {
			int i = idx + 1;
			column_names[i] = attributeNames.get(idx);

			String attributeName = (String) attributeNames.get(idx);

			byte type = data.getType(attributeName);
			it = Cytoscape.getNetworkSet().iterator();

			int j = 0;

			while (it.hasNext()) {
				CyNetwork network = (CyNetwork) it.next();
				Object value = getAttributeValue(type, network.getIdentifier(), attributeName);

				data_vector[j][i] = value;
				j++;
			}
		}

		setDataVector(data_vector, column_names);
	}

	// Fill the cells in the table
	// *** need to add an argument to copy edge attribute name correctly.
	//
	protected void setTable() {
		internalSelection = new HashMap();

		for(GraphObject obj: graphObjects) {

			internalSelection.put(obj.getIdentifier(), DEFAULT_FLAG);

			if (objectType == DataTable.NODES) {
				final Node targetNode = obj.getRootGraph().getNode(obj.getRootGraphIndex());

				if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
					NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView(targetNode);

					if (nv != null) {
						nv.setSelectedPaint(selectedNodeColor);
					}
				}
			} else if (objectType == DataTable.EDGES) {
				final Edge targetEdge = obj.getRootGraph().getEdge(obj.getRootGraphIndex());

				if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
					final EdgeView edgeView = Cytoscape.getCurrentNetworkView()
					                                   .getEdgeView(targetEdge);

					if (edgeView != null) {
						edgeView.setSelectedPaint(selectedEdgeColor);
					}
				}
			}
		}

		int att_length = attributeNames.size() + 1;
		int go_length = graphObjects.size();

		Object[][] data_vector = new Object[go_length][att_length];
		Object[] column_names = new Object[att_length];

		// Set column names (attribute names)
		// System.out.println("Debug: testsection start.");
		column_names[0] = DataTable.ID;

		for (int j = 0; j < go_length; ++j) {
			GraphObject obj = (GraphObject) graphObjects.get(j);

			data_vector[j][0] = obj.getIdentifier();
		}

		// Set actual data
		for (int i1 = 0; i1 < attributeNames.size(); ++i1) {
			int i = i1 + 1;
			column_names[i] = attributeNames.get(i1);

			String attributeName = (String) attributeNames.get(i1);

			byte type = data.getType(attributeName);

			for (int j = 0; j < go_length; ++j) {
				GraphObject obj = (GraphObject) graphObjects.get(j);

				Object value = getAttributeValue(type, obj.getIdentifier(), attributeName);

				data_vector[j][i] = value;
			}
		}

		setDataVector(data_vector, column_names);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 * @param id DOCUMENT ME!
	 * @param att DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getAttributeValue(byte type, String id, String att) {
		if (type == CyAttributes.TYPE_INTEGER)
			return data.getIntegerAttribute(id, att);
		else if (type == CyAttributes.TYPE_FLOATING)
			return data.getDoubleAttribute(id, att);
		else if (type == CyAttributes.TYPE_BOOLEAN)
			return data.getBooleanAttribute(id, att);
		else if (type == CyAttributes.TYPE_STRING)
			return data.getStringAttribute(id, att);
		else if (type == CyAttributes.TYPE_SIMPLE_LIST)
			return data.getListAttribute(id, att);
		else if (type == CyAttributes.TYPE_SIMPLE_MAP)
			return data.getMapAttribute(id, att);

		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getGraphObjects() {
		return graphObjects;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param colName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class getObjectTypeAt(String colName) {
		byte type = data.getType(colName);

		if (type == CyAttributes.TYPE_INTEGER)
			return Integer.class;
		else if (type == CyAttributes.TYPE_FLOATING)
			return Double.class;
		else if (type == CyAttributes.TYPE_BOOLEAN)
			return Boolean.class;
		else if (type == CyAttributes.TYPE_STRING)
			return String.class;
		else if (type == CyAttributes.TYPE_SIMPLE_LIST)
			return List.class;
		else if (type == CyAttributes.TYPE_SIMPLE_MAP)
			return Map.class;

		return null;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 * @param keyIntoValue DOCUMENT ME!
	 * @param oldAttributeValue DOCUMENT ME!
	 * @param newAttributeValue DOCUMENT ME!
	 */
	public void attributeValueAssigned(java.lang.String objectKey, java.lang.String attributeName,
	                                   java.lang.Object[] keyIntoValue,
	                                   java.lang.Object oldAttributeValue,
	                                   java.lang.Object newAttributeValue) {
		// System.out.println( "attributeValueAssigned" );
		// setTable();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 * @param keyIntoValue DOCUMENT ME!
	 * @param attributeValue DOCUMENT ME!
	 */
	public void attributeValueRemoved(java.lang.String objectKey, java.lang.String attributeName,
	                                  java.lang.Object[] keyIntoValue,
	                                  java.lang.Object attributeValue) {
		// setTable();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 */
	public void allAttributeValuesRemoved(java.lang.String objectKey, java.lang.String attributeName) {
		// setTable();
	}

	/*
	 * The following section is an implementation of
	 * JSortTable
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSortable(int col) {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 * @param ascending DOCUMENT ME!
	 */
	public void sortColumn(int col, boolean ascending) {
		Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param rowIndex DOCUMENT ME!
	 * @param colIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (!data.getUserEditable(getColumnName(colIndex)))
			return false;

		Class objectType = null;
		Object selectedObj = this.getValueAt(rowIndex, colIndex);

		if ((selectedObj == null) && (colIndex != 0)) {
			return true;
		} else if (selectedObj != null) {
			objectType = this.getValueAt(rowIndex, colIndex).getClass();
		}

		if (objectType != null) {
			if (colIndex == 0) {
				return false;
			} else if (objectType == ArrayList.class) {
				return false;
			} else if (objectType == HashMap.class) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Instead of using a listener, just overwrite this method to save time and
	 * write to the temp object
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		DataEditAction edit = null;

		if (this.objectType != DataTable.NETWORK) {
			edit = new DataEditAction(this, (String) getValueAt(rowIndex, 0),
			                          (String) attributeNames.get(columnIndex - 1), null,
			                          getValueAt(rowIndex, columnIndex), aValue, objectType);
		} else {
			edit = new DataEditAction(this, Cytoscape.getCurrentNetwork().getIdentifier(),
			                          (String) this.getValueAt(rowIndex, 0), null,
			                          getValueAt(rowIndex, columnIndex), aValue, objectType);
		}

		cytoscape.util.undo.CyUndo.getUndoableEditSupport().postEdit(edit);
	}
}
