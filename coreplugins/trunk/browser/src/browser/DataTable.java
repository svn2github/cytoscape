package browser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 * DataTable class constructs all Panels for the browser.<br>
 * 
 * @author kono
 * 
 */
public class DataTable implements PropertyChangeListener {

	public static final String ID = "ID";
	public static final Color NON_EDITIBLE_COLOR = new Color(235, 235, 235);
	
	public static final String NETWORK_METADATA = "Network Metadata";
	
	// Panels to be added on the CytoPanels
	ModPanel modPanel;
	protected SelectPanel selectionPanel;
	private DataTableModel tableModel;
	private JSortTable attributeTable;
	
	boolean coloring;

	// Small toolbar panel on the top of browser
	AttributeBrowserPanel attributeBrowserPanel;
	
	JPanel mainPanel;

	// Index number for the panels
	int attributePanelIndex;
	int modPanelIndex;
	int tableIndex;

	int browserIndex;

	// Each Attribute Browser operates on one CytoscapeData object, and on
	// either Nodes or Edges.
	CyAttributes data;

	// Object types
	public static final int NODES = 0;
	public static final int EDGES = 1;
	public static final int NETWORK = 2;

	private String type = null;

	public int tableObjectType;

	public DataTable(CyAttributes data, int tableObjectType) {
		
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
		attributeBrowserPanel = new AttributeBrowserPanel(data, new AttributeModel(
				data), new LabelModel(data), tableObjectType);
		attributeBrowserPanel.setTableModel(tableModel);

		// the attribute table display: CytoPanel 2, horizontal SOUTH panel.
		mainPanel = new JPanel(); // Container for table and toolbar.
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setPreferredSize(new java.awt.Dimension(400, 180));
		if(tableObjectType == NETWORK && Cytoscape.getCurrentNetwork() != null) {
			if(Cytoscape.getCurrentNetwork().getTitle().equals("0")) {
				mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
						type + " Attribute Browser",
						javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
						javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			} else {
				mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					type + " Attribute Browser ( " + Cytoscape.getCurrentNetwork().getTitle() + " )",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			}
		} else {
			mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
				type + " Attribute Browser",
				javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
				javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		}
		attributeTable = new JSortTable(tableModel, tableObjectType);

		// If this is a network attribute browser, do not allow to swap
		// column.
		if (this.tableObjectType == DataTable.NETWORK) {
			attributeTable.getTableHeader().setReorderingAllowed(false);
		}

		JScrollPane mainTable = new JScrollPane(attributeTable);
		mainPanel.setName(type + "AttributeBrowser");
		mainPanel.add(mainTable, java.awt.BorderLayout.CENTER);
		mainPanel.add(attributeBrowserPanel, java.awt.BorderLayout.NORTH);
		// BrowserPanel mainPanel = new BmakeModelrowserPanel(new
		// JSortTable(tableModel));

		//
		// Advanced Window: CytoPanel 3
		//
		JTabbedPane advancedPanel = new JTabbedPane();
		advancedPanel.setPreferredSize(new Dimension(200, 100));

		modPanel = new ModPanel(data, tableModel, tableObjectType);
		selectionPanel = new SelectPanel(this, tableObjectType);
		selectionPanel.setPreferredSize(new Dimension(500, 100));
		advancedPanel.add("Selection", selectionPanel);
		advancedPanel.add("Modification", modPanel);

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add(
		// type + "Attributes", attributePanel);

		// Add advanced panel to the CytoPanel 3 (EAST)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add(
				type + "Attr Mod/ Object Select", advancedPanel);	

		// Add main browser panel to CytoPanel 2 (SOUTH)

		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
				type + " Attribute Browser", mainPanel);

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
		List attributeNames = Arrays.asList(data.getAttributeNames());
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
		//System.out.println("================Signal DT =  " + e.getPropertyName());
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS) {
			mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
					type + " Attribute Browser ( " + Cytoscape.getCurrentNetwork().getTitle() + " )",
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
		}
		
	}

}

