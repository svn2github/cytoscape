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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

import browser.ui.AttributeBrowserToolBar;
import browser.ui.CyAttributeBrowserTable;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;


/**
 * 
 * DataTable class constructs all Panels for the browser.<br>
 * One DataTable contains the table of values and the toolbar above the table
 * within a jpanel. This panel is then added to CytoPanel2. Tabbed browsing
 * between attribute tables is handled by CytoPanel2.
 * 
 * @author kono
 * 
 * Combine CytoPanel_2 and CytoPanel_3 Peng-Liang wang 9/12/2006 Move advanced
 * panel to AttrMod Dialog Peng-Liang wang 9/28/2006
 * 
 */
public class AttributeBrowser implements PropertyChangeListener {
	/**
	 * 
	 */
	public static final String ID = "ID";
	public static final String NETWORK_METADATA = "Network Metadata";

	public static final Color DEFAULT_EDGE_COLOR = Color.RED;
	public static final Color DEFAULT_NODE_COLOR = Color.YELLOW;
	
	// Each Attribute Browser operates on one CytoscapeData object, and on
	// either Nodes or Edges.
	private final CyAttributes attrData;
	
	// Type of attribute
	private final DataObjectType panelType;
	
	// Main panel for put everything
	private JPanel mainPanel;
	
	// Table object and its model. 
	private DataTableModel tableModel;
	private CyAttributeBrowserTable attributeTable;

	// Toolbar panel on the top of browser
	private AttributeBrowserToolBar attributeBrowserToolBar;

	// Index number for the panels
	int attributePanelIndex;
	int modPanelIndex;
	int tableIndex;
	int browserIndex;

	/**
	 * Browser object is one per attribute type.
	 * Users should access it through this.
	 * 
	 * @param type
	 */
	public static AttributeBrowser getBrowser(final DataObjectType type) {
		return new AttributeBrowser(type);
	}
	
	protected void addMenuItem(Component newItem) {
		attributeTable.getContextMenu().add(newItem);
	}
	
	/**
	 * Creates a new DataTable object.
	 * 
	 * @param attrData
	 *            DOCUMENT ME!
	 * @param tableObjectType
	 *            DOCUMENT ME!
	 */
	private AttributeBrowser(final DataObjectType panelType) {
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);


		// set up CytoscapeData Object and GraphObject Type
		this.attrData = panelType.getAssociatedAttribute();
		this.panelType = panelType;
		

		// Create table model.
		tableModel = (DataTableModel) makeModel(attrData);
		tableModel.setObjectType(panelType);

		// List of attributes and labels: CytoPanel 1

		// Toolbar for selecting attributes and create new attribute.
		attributeBrowserToolBar = new AttributeBrowserToolBar(tableModel,
				new AttributeModel(attrData), panelType);
		//attributeBrowserToolBar.setTableModel(tableModel);

		// the attribute table display: CytoPanel 2, horizontal SOUTH panel.
		mainPanel = new JPanel(); // Container for table and toolbar.
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(400, 200));

		mainPanel.setBorder(null);

		attributeTable = new CyAttributeBrowserTable(tableModel, panelType);
//		attributeTable.getColumnModel().addColumnModelListener(this);
		attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// If this is a network attribute browser, do not allow to swap
		// column.
		if (panelType == DataObjectType.NETWORK) {
			attributeTable.getTableHeader().setReorderingAllowed(false);
		}

		JScrollPane mainTable = new JScrollPane(attributeTable);
		mainPanel.setName(panelType.getDislayName() + "AttributeBrowser");
		mainPanel.add(mainTable, java.awt.BorderLayout.CENTER);
		mainPanel.add(attributeBrowserToolBar, java.awt.BorderLayout.NORTH);

		
		//attributeBrowserToolBar.setAttrModPane(modDialog, panelType.getDislayName());

		// Add main browser panel to CytoPanel 2 (SOUTH)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
				panelType.getDislayName() + " Attribute Browser", mainPanel);

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
				CytoPanelState.DOCK);
	}

	public CyAttributeBrowserTable getattributeTable() {
		return attributeTable;
	}


	static class Listener implements CytoPanelListener {
		int WEST;
		int SOUTH;
		int EAST;
		int myIndex;

		Listener(int w, int s, int e, int my) {
			WEST = w;
			SOUTH = s;
			EAST = e;
			myIndex = my;
		}

		public void onComponentAdded(int count) {
		}

		public void onComponentRemoved(int count) {
		}

		public void onComponentSelected(int componentIndex) {
			if (componentIndex == myIndex) {
				if (WEST != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST)
							.setSelectedIndex(WEST);
				}

				if (SOUTH != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
							.setSelectedIndex(SOUTH);
				}

				if (EAST != -1) {
					Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
							.setSelectedIndex(EAST);
				}
			}
		}

		public void onStateChange(CytoPanelState newState) {
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public DataObjectType getGraphObjectType() {
		return panelType;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CyAttributes getData() {
		return attrData;
	}

	//
	// Make sort model by using given CyAttributes
	//
	protected SortTableModel makeModel(CyAttributes data) {
		final List attributeNames = CyAttributesUtils.getVisibleAttributeNames(data);
		DataTableModel model = new DataTableModel();
		List graph_objects = getSelectedGraphObjects();

		if (panelType == DataObjectType.NETWORK) {
			model.setTableData(data, null, attributeNames, panelType);
		} else {
			model.setTableData(data, graph_objects, attributeNames,
					panelType);
		}

		return model;
	}

	private List getSelectedGraphObjects() {
		if (panelType.equals(DataObjectType.NODES)) {
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getSelectedNodes());
		} else {
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getSelectedEdges());
		}
	}

	public DataTableModel getDataTableModel() {
		return tableModel;
	}

	/**
	 * Catch pce here.<br>
	 * Used mainly for the network panel.<br>
	 * 
	 */
	public void propertyChange(PropertyChangeEvent e) {
		// not yet implemented.
	}

	
	public void restoreColumnModel(TableColumnModel newModel) {
		attributeTable.setColumnModel(newModel);
	}
	
	public TableColumnModel getColumnModel() {
		return attributeTable.getColumnModel();
	}
	
}
