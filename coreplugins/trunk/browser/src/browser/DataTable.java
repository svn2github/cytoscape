package browser;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * @author kono
 * 
 * DataTable class constructs all Panels for the browser.
 * 
 * For this version, CytoPanels are used as: 1. Not used. Just default Network
 * Tree Viewer will be shown. 2. Main Attribute Browser. 3. "Advanced Window."
 * Mainly for filtering.
 * 
 */
public class DataTable {

	public static final String ID = "ID";
	public static final Color NON_EDITIBLE_COLOR = new Color(235, 235, 235);

	private static final int LIST_MAX = 3;

	// Panels to be added on the CytoPanels
	ModPanel modPanel;
	SelectPanel selectionPanel;
	DataTableModel tableModel;

	boolean coloring;

	// Small toolbar panel on the top of browser
	AttributeBrowserPanel attributePanel2;

	// Index number for the panels
	int attributePanelIndex;
	int modPanelIndex;
	int tableIndex;

	int browserIndex;

	// Each Attribute Browser operates on one CytoscapeData object, and on
	// either Nodes or Edges.
	CyAttributes data;

	public static int NODES = 0;
	public static int EDGES = 1;

	// Special panel needed for network attributes
	public static int NETWORK = 2;

	private String type = null;

	public int tableObjectType;

	public DataTable(CyAttributes data, int tableObjectType) {
		
		// set up CytoscapeData Object and GraphObject Type
		this.data = data;
		this.tableObjectType = tableObjectType;

		// Make display title
		type = "Node";
		if (tableObjectType == EDGES) {
			type = "Edge";
		} else if (tableObjectType == NETWORK) {
			type = "Network";
		}

		// Create table model.
		tableModel = (DataTableModel) makeModel(data);
		tableModel.setObjectType(tableObjectType);

		// List of attributes and labels: CytoPanel 1

		// Toolbar for selecting attributes and create new attribute.
		attributePanel2 = new AttributeBrowserPanel(data, new AttributeModel(
				data), new LabelModel(data), tableObjectType);
		attributePanel2.setTableModel(tableModel);

		// the attribute table display: CytoPanel 2, horizontal SOUTH panel.
		JPanel mainPanel = new JPanel(); // Container for table and toolbar.
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new java.awt.Dimension(400, 180));
		mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				type + " Attribute Browser",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));

		JSortTable attributeTable = new JSortTable(tableModel, tableObjectType);
		// public String getToolTipText(MouseEvent me) {
		// Point pt = me.getPoint();
		// int row = rowAtPoint(pt);
		// int col = columnAtPoint(pt);
		//
		// if (row < 0) {
		// return null;
		// } else {
		// Object targetObject = getValueAt(row, col);
		//
		// if (targetObject.getClass() == ArrayList.class) {
		//
		// int counter = 0;
		// ArrayList listAttribute = (ArrayList) targetObject;
		// String ttText = "<html>";
		// Iterator it = listAttribute.iterator();
		//
		// while (it.hasNext()) {
		// String text = (it.next()).toString();
		// ttText = ttText + text;
		// counter++;
		// if (counter > LIST_MAX) {
		// ttText = ttText
		// + "<br>"
		// + "<font size=\"3\" color=\"yellow\">Click cell to view full
		// listing...</font>";
		// return ttText + "</html>";
		// } else if (counter < listAttribute.size()) {
		// ttText = ttText + "<br>";
		// }
		// }
		// return ttText + "</html>";
		// }
		// return null;
		// }
		// }
		// };

		

		// If this is a network attribute browser, do not allow to swap
		// column.
		if (this.tableObjectType == DataTable.NETWORK) {
			attributeTable.getTableHeader().setReorderingAllowed(false);
		}

		JScrollPane mainTable = new JScrollPane(attributeTable);
		mainPanel.setName(type + "AttributeBrowser");
		mainPanel.add(mainTable, java.awt.BorderLayout.CENTER);
		mainPanel.add(attributePanel2, java.awt.BorderLayout.NORTH);
		// BrowserPanel mainPanel = new BrowserPanel(new
		// JSortTable(tableModel));

		//
		// Advanced Window: CytoPanel 3
		//
		JTabbedPane advancedPanel = new JTabbedPane();
		advancedPanel.setPreferredSize(new Dimension(200, 100));

		modPanel = new ModPanel(data, tableModel, tableObjectType);
		selectionPanel = new SelectPanel(tableModel, tableObjectType);
		advancedPanel.add("Selection", selectionPanel);
		advancedPanel.add("Modification", modPanel);

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add(
		// type + "Attributes", attributePanel);

		// Add advanced panel to the CytoPanel 3 (EAST)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add(
				type + "Attr Mod/ Object Select", advancedPanel);

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
		// type + " Attribute Browser", mainPanel);
		//		

		// Add main browser panel to CytoPanel 2 (SOUTH)

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
				type + " Attribute Browser", mainPanel);

		// if(this.tableObjectType == this.NETWORK) {
		// String netName = Cytoscape.getCurrentNetwork().getTitle();
		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
		// "Network Attributes for " + netName, mainPanel);
		// } else {
		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
		// type + " Attribute Browser", mainPanel);
		// }

		// Get indexes for the panels.
		modPanelIndex = Cytoscape.getDesktop()
				.getCytoPanel(SwingConstants.EAST).indexOfComponent(
						advancedPanel);

		tableIndex = Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
				.indexOfComponent(mainPanel);

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
				.addCytoPanelListener(
						new Listener(attributePanelIndex, -1, modPanelIndex,
								tableIndex));

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
				.addCytoPanelListener(
						new Listener(attributePanelIndex, tableIndex, -1,
								modPanelIndex));

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
				CytoPanelState.DOCK);

	}

	// 
	class Listener implements CytoPanelListener {

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

	public int getGraphObjectType() {
		return tableObjectType;
	}

	public CyAttributes getData() {
		return data;
	}

	//
	// Make sort model by using given CyAttributes
	//
	protected SortTableModel makeModel(CyAttributes data) {
		List attributeNames = Arrays.asList(data.getAttributeNames());
		DataTableModel model = new DataTableModel();
		List graph_objects = getSelectedGraphObjects();
		if (tableObjectType == this.NETWORK) {
			model.setTableData(data, null, attributeNames, tableObjectType);
		} else {
			model.setTableData(data, graph_objects, attributeNames,
					tableObjectType);
		}

		return model;
	}

	private List getSelectedGraphObjects() {
		if (tableObjectType == NODES) {
			// return new ArrayList(Cytoscape.getCurrentNetwork()
			// .getFlaggedNodes());
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getSelectedNodes());
		} else {
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getSelectedEdges());
		}
	}

}

