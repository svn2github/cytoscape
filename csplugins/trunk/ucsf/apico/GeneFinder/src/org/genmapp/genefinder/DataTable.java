package browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
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

	// Panels to be added on the CytoPanels
	FinderPanel2 finderPanel;
	DataTableModel tableModel;
	
	
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
	public int graphObjectType;

	public DataTable(CyAttributes data, int graphObjectType) {

		// set up CytoscapeData Object and GraphObject Type
		this.data = data;
		this.graphObjectType = graphObjectType;

		// Make display title
		String type = "Node";
		if (graphObjectType != NODES)
			type = "Edge";

		// Create table model.
		tableModel = (DataTableModel) makeModel(data);
		tableModel.setGraphObjectType(graphObjectType);


		//
		// Advanced Window: CytoPanel 2
		//
		JTabbedPane advancedPanel = new JTabbedPane();
		advancedPanel.setPreferredSize(new Dimension(200,100));
		
		finderPanel = new FinderPanel2(tableModel, graphObjectType);
		advancedPanel.add("Gene Finder", finderPanel);

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add(
		// type + "Attributes", attributePanel);

		// Add advanced panel to the CytoPanel 3 (EAST)
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).add(
				type + " Attribute Editor", advancedPanel);

		
		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).add(
		// type + " Attribute Browser", mainPanel);
		//		


		// Add an export command in "Data" menu
		
		
		
		
		
		
		// attributePanelIndex = Cytoscape.getDesktop().getCytoPanel(
		// SwingConstants.WEST).indexOfComponent(attributePanel);

		//
		// Get indexes for the panels.

		// browserIndex = Cytoscape.getDesktop().getCytoPanel(
		// SwingConstants.SOUTH).indexOfComponent(mainPanel);

		// Setup listeners
		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		// .addCytoPanelListener(
		// new Listener(attributePanelIndex, modPanelIndex, -1,
		// tableIndex));

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		// .addCytoPanelListener(
		// new Listener(attributePanelIndex, -1, tableIndex,
		// modPanelIndex));
		//		

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
		// .addCytoPanelListener(
		// new Listener(attributePanelIndex, -1, tableIndex,
		// browserIndex));
		//		
		//		

		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(
		// CytoPanelState.DOCK);
		// Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
		// CytoPanelState.DOCK);

	}

	// 
	class Listener implements CytoPanelListener {

		int WEST;
		int SOUTH;
		int EAST;
		int myIndex;

		Listener(int w, int s, int n, int e, int my) {

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
		return graphObjectType;
	}

	public CyAttributes getData() {
		return data;
	}

	//
	// Make sort model by using given CyAttributes
	//
	protected SortTableModel makeModel(CyAttributes data) {

		List attributes = Arrays.asList(data.getAttributeNames());
		List graph_objects = getFlaggedGraphObjects();

		DataTableModel model = new DataTableModel();
		model.setTableData(data, graph_objects, attributes);
		return model;
	}

	private List getFlaggedGraphObjects() {
		if (graphObjectType == NODES)
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getFlaggedNodes());
		else
			return new ArrayList(Cytoscape.getCurrentNetwork()
					.getFlaggedEdges());
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("JSortTable Test");
		frame.getContentPane().setLayout(new GridLayout());
		frame.getContentPane().add(new JSortTableTest());
		frame.pack();
		frame.show();
	}
}
