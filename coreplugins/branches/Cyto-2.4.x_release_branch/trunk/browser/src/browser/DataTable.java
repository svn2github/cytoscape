package browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.util.swing.ColumnResizer;
import cytoscape.view.CytoscapeDesktop;
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
public class DataTable implements PropertyChangeListener {

	public static final String ID = "ID";
	public static final Color NON_EDITIBLE_COLOR = new Color(235, 235, 235);

	public static final String NETWORK_METADATA = "Network Metadata";

	// Panel to be added to JDialog for attribute modification
	private AttrSelectModPanel modPanel;
	// protected SelectPanel selectionPanel; selectPanel is now part of modPanel

	// Panels to be added on the CytoPanels

	private DataTableModel tableModel;
	private JSortTable attributeTable;

	// Small toolbar panel on the top of browser
	private AttributeBrowserPanel attributeBrowserPanel;

	private JPanel mainPanel;

	// Index number for the panels
	int attributePanelIndex;
	int modPanelIndex;
	int tableIndex;

	int browserIndex;

	// Each Attribute Browser operates on one CytoscapeData object, and on
	// either Nodes or Edges.
	private CyAttributes data;

	// Object types
	public static final int NODES = 0;
	public static final int EDGES = 1;
	public static final int NETWORK = 2;

	private String type = null;

	public int tableObjectType;

	public DataTable(final CyAttributes data, final int tableObjectType) {

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(this);

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
		attributeBrowserPanel = new AttributeBrowserPanel(data,
				new AttributeModel(data), new LabelModel(data), tableObjectType);
		attributeBrowserPanel.setTableModel(tableModel);

		// the attribute table display: CytoPanel 2, horizontal SOUTH panel.
		mainPanel = new JPanel(); // Container for table and toolbar.
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new java.awt.Dimension(400, 180));
		
		
		
		mainPanel.setBorder(null);
//		if (tableObjectType == NETWORK && Cytoscape.getCurrentNetwork() != null) {
//			if (Cytoscape.getCurrentNetwork().getTitle().equals("0")) {
//				mainPanel
//						.setBorder(javax.swing.BorderFactory
//								.createTitledBorder(
//										null,
//										type + " Attribute Browser",
//										javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
//										javax.swing.border.TitledBorder.DEFAULT_POSITION,
//										null, null));
//			} else {
//				mainPanel
//						.setBorder(javax.swing.BorderFactory
//								.createTitledBorder(
//										null,
//										type
//												+ " Attribute Browser ( "
//												+ Cytoscape.getCurrentNetwork()
//														.getTitle() + " )",
//										javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
//										javax.swing.border.TitledBorder.DEFAULT_POSITION,
//										null, null));
//			}
//		} else {
//			mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
//					null, type + " Attribute Browser",
//					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
//					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
//					null));
//		}
		attributeTable = new JSortTable(tableModel, tableObjectType);
		attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		

		// If this is a network attribute browser, do not allow to swap
		// column.
		if (this.tableObjectType == DataTable.NETWORK) {
			attributeTable.getTableHeader().setReorderingAllowed(false);
		}

		JScrollPane mainTable = new JScrollPane(attributeTable);
		mainPanel.setName(type + "AttributeBrowser");
		mainPanel.add(mainTable, java.awt.BorderLayout.CENTER);
		mainPanel.add(attributeBrowserPanel, java.awt.BorderLayout.NORTH);

		// modPanel will be added to JDialog for attribute modification
		modPanel = new AttrSelectModPanel(this, data, tableModel,
				tableObjectType);
		attributeBrowserPanel.setAttrModPane(modPanel, type);

		// Add main browser panel to CytoPanel 2 (SOUTH)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
				type + " Attribute Browser", mainPanel);

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
				CytoPanelState.DOCK);

	}

	protected JSortTable getattributeTable() {
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
		List attributeNames = CyAttributesUtils.getVisibleAttributeNames(data);
		DataTableModel model = new DataTableModel();
		List graph_objects = getSelectedGraphObjects();
		if (tableObjectType == NETWORK) {
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

	protected DataTableModel getDataTableModel() {
		return tableModel;
	}

	/**
	 * Catch pce here.<br>
	 * Used mainly for the network panel.<br>
	 * 
	 */
	public void propertyChange(PropertyChangeEvent e) {
		
//		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS) {
//			mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
//					null, type + " Attribute Browser ( "
//							+ Cytoscape.getCurrentNetwork().getTitle() + " )",
//					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
//					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
//					null));
//		}

	}

}
