/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo;

import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableColumn;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.cheminfo.similarity.CDKTanimotoScore;
import cytoscape.plugin.cheminfo.structure.MoleculeViewDialog;
import cytoscape.plugin.cheminfo.structure.StructureDepictor;
import cytoscape.plugin.cheminfo.table.ChemTable;
import cytoscape.plugin.cheminfo.table.ChemTableSorter;
import cytoscape.plugin.cheminfo.table.EdgeTable;
import cytoscape.plugin.cheminfo.table.MoleculeCellRenderer;
import cytoscape.plugin.cheminfo.table.NodeTable;
import cytoscape.plugin.cheminfo.table.TextAreaRenderer;
import cytoscape.util.URLUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.NodeContextMenuListener;

/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class ChemInfoPlugin extends CytoscapePlugin implements
		NodeContextMenuListener, PropertyChangeListener, ActionListener {
	
	private HashMap<String, Object[]> attrMap;

	private JMenu menu = null;
	private NodeView nodeView = null;
	private Properties systemProps = null;
	
	public enum AttriType { smiles, inchi };

	public ChemInfoPlugin() {
		try {
			// Set ourselves up to listen for new networks
			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
					.addPropertyChangeListener(
							CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

			((DGraphView) Cytoscape.getCurrentNetworkView())
					.addNodeContextMenuListener(this);
		} catch (ClassCastException ccex) {
			ccex.printStackTrace();
		}

		// Loading properties
		systemProps = new Properties();
		try {
			systemProps.load(this.getClass().getResourceAsStream(
					"cheminfo.props"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.menu = buildMenu();
		
		this.attrMap = new HashMap<String, Object[]>();
		
		Properties cytoProps = CytoscapeInit.getProperties();
		cytoProps.put("nodelinkouturl.Entrez.PubChem(InChI)", "http://www.ncbi.nlm.nih.gov/sites/entrez?term=\"%ID%\"[InChI]&cmd=search&db=pccompound");
	}

	public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu pmenu) {
		this.nodeView = nodeView;
		if (pmenu == null) {
			pmenu = new JPopupMenu();
		}
		pmenu.add(menu);
	}

	/**
	 * Builds the popup menu
	 * 
	 * @return
	 */
	public JMenu buildMenu() {
		JMenu menu = new JMenu(systemProps.getProperty("cheminfo.menu"));
		JMenuItem depict = buildMenuItem("cheminfo.menu.2ddepiction",
				"cheminfo.menu.2ddepiction");
		menu.add(depict);
		JMenu simMenu = new JMenu(systemProps
				.getProperty("cheminfo.menu.similarity"));
		menu.add(simMenu);
		JMenuItem tanimoto = buildMenuItem("cheminfo.menu.similarity.tanimoto",
				"cheminfo.menu.similarity.tanimoto");
		simMenu.add(tanimoto);
		Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(menu);
		return menu;
	}

	/**
	 * Builds a menu item in the popup menu
	 * 
	 * @param label
	 * @param command
	 * @return
	 */
	public JMenuItem buildMenuItem(String label, String command) {
		JMenuItem item = new JMenuItem(systemProps.getProperty(label));
		item.setActionCommand(command);
		item.addActionListener(this);
		return item;
	}

	/**
	 * Detect that a new network view has been created and add our node context
	 * menu listener to nodes within this network
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED) {
			// Add menu to the context dialog
			((CyNetworkView) evt.getNewValue())
					.addNodeContextMenuListener(this);
		}
	}

	/**
	 * Get an attribute from a node
	 * 
	 * @param node
	 * @param attr
	 * @return
	 */
	public static String getAttribute(CyNode node, String attr) {
		CyAttributes attributes = Cytoscape.getNodeAttributes();
		String value = attributes
				.getStringAttribute(node.getIdentifier(), attr);
		if (null == value || "".equals(value)) {
			// Now search for smiles
			String[] names = attributes.getAttributeNames();
			for (String string : names) {
				if (attr.equalsIgnoreCase(string)) {
					value = attributes.getStringAttribute(node.getIdentifier(),
							string);
					break;
				}
			}
		}
		return value;
	}
	
	public static String getSmiles(CyNode node, String attribute, AttriType attrType) {
		String smiles = getAttribute(node, attribute);
		if (attrType != AttriType.smiles) {
			if (attrType == AttriType.inchi) {
				smiles = convertInchiToSmiles(smiles);
			}
		}
		return smiles;
	}

	/**
	 * Use chemspider web service to translate an InChI string to SMILES
	 * 
	 * @param inchi
	 * @return
	 */
	public static String convertInchiToSmiles(String inchi) {
		String url = "http://www.chemspider.com/inchi.asmx/InChIToSMILES?inchi="
				+ inchi.trim();
		String smiles = null;
		try {
			String result = URLUtil.download(new URL(url));
			Pattern pattern = Pattern.compile(".*<[^>]*>([^<]*)</string>");
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				smiles = matcher.group(1);
			}
		} catch (MalformedURLException muex) {
			muex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}

		return smiles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		
		final Object[] attr;
		// First let the user choose the attribute that contains the smiles/inchi code
		if (this.attrMap.containsKey(Cytoscape.getCurrentNetwork().getIdentifier())) {
			attr = attrMap.get(Cytoscape.getCurrentNetwork().getIdentifier());
		} else {
			CyAttributes attributes = Cytoscape.getNodeAttributes();
			attr = showAttributeDialog(attributes.getAttributeNames());
			attrMap.put(Cytoscape.getCurrentNetwork().getIdentifier(), attr);
		}
		
		if (cmd.equals("cheminfo.menu.2ddepiction")) {
			final List nodes = Cytoscape.getCurrentNetworkView()
					.getSelectedNodes();
			if (nodes.size() > 1) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						depictMultipleNodes(nodes, Cytoscape
								.getCurrentNetworkView(), (String)attr[0], (AttriType)attr[1]);
					}
				});
			} else if (nodes.size() == 1) {
				CyNode node = (CyNode) nodeView.getNode();
				depictSingleNode(node, (String)attr[0], (AttriType)attr[1]);
			}
		} else if (cmd.equals("cheminfo.menu.similarity.tanimoto")) {
			final int finalChoice = showSimilarityOptionDialog(0);
			
			if (finalChoice == 0) {
				final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						calculateTanimotoOnSelectedNodes(networkView, (String)attr[0], (AttriType)attr[1]);
					}
				});
			} else if (finalChoice == 1) {
				final CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						calculateTanimotoOnAllNodes(networkView, (String)attr[0], (AttriType)attr[1]);
					}
				});
			}
		}
	}
	
	private void calculateTanimotoOnAllNodes(CyNetworkView origView, String attribute, AttriType attrType) {
		CyNetwork origNet = origView.getNetwork();
		VisualStyle vs = Cytoscape.getVisualMappingManager().getVisualStyle(); 

		int nodeIndices[] = origNet.getNodeIndicesArray();
		int edgeIndices[] = new int[0];	
		
		CyNetwork newNet = Cytoscape.createNetwork(origNet.getNodeIndicesArray(),
                new int[0],
                origNet.getTitle() + " copy", 
				null,
				true);
		
		List nodeList = newNet.nodesList();
		
		List rows = new ArrayList();
		for (int i = 0; i < nodeList.size(); i++) {
			CyNode node1 = (CyNode)nodeList.get(i);
			for (int j = i+1; j < nodeList.size(); j++) {
				List row = new ArrayList();
				CyNode node2 = (CyNode)nodeList.get(j);
				CDKTanimotoScore scorer = new CDKTanimotoScore(node1, node2, attribute, AttriType.smiles);
				CyEdge edge = Cytoscape.getCyEdge(node1, node2, "interaction", "similarity", true, true);
				CyAttributes attrs = Cytoscape.getEdgeAttributes();
				double score = scorer.calculateSimilarity();
				attrs.setAttribute(edge.getIdentifier(), "tanimoto", score); 
				newNet.addEdge(edge);
				
				row.add(edge.getIdentifier());
				row.add(node1.getIdentifier());
				row.add(node2.getIdentifier());
				row.add(score);
				rows.add(row);
			}
		}
		
		CyNetworkView newView = Cytoscape.getNetworkView(newNet.getIdentifier());
		if ( newView != null || newView != Cytoscape.getNullNetworkView() ) {

        	// Use nodes as keys because they are less volatile than views...
	        Iterator ni = origView.getGraphPerspective().nodesIterator();
			while (ni.hasNext()) {
				Node n = (Node) ni.next();

				NodeView onv = origView.getNodeView(n);
				NodeView nnv = newView.getNodeView(n);

				nnv.setXPosition(onv.getXPosition());
				nnv.setYPosition(onv.getYPosition());
			}

			newView.setZoom(origView.getZoom());
			Point2D origCenter = ((DGraphView)origView).getCenter();
			((DGraphView)newView).setCenter(origCenter.getX(), origCenter.getY());

			Cytoscape.getVisualMappingManager().setVisualStyle(vs);
		}
		
		// Select all edges and nodes 
		Iterator it = newView.getEdgeViewsIterator();
		while (it.hasNext()) {
			EdgeView ev = (EdgeView)it.next();
			ev.setSelected(true);
		}
		it = newView.getNodeViewsIterator();
		while (it.hasNext()) {
			NodeView nv = (NodeView)it.next();
			nv.setSelected(true);
		}		
		
		List colNames = new ArrayList();
		colNames.add("Edge ID");
		colNames.add("Node1 ID");
		colNames.add("Node2 ID");
		colNames.add("Tanimoto Coefficient");
		ChemTableSorter sorter = new ChemTableSorter(rows, colNames);
		
		ChemTable table = new EdgeTable(sorter, newView.getIdentifier(), attribute, attrType);

		table.getColumnModel().getColumn(1).setCellRenderer(
				new TextAreaRenderer());
		
		table.getColumnModel().getColumn(2).setCellRenderer(
				new TextAreaRenderer());		

		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionBackground(Color.CYAN);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		//networkView.getNetwork().addSelectEventListener(table);
		sorter.setTableHeader(table.getTableHeader());
		table.showTableDialog("Show Similarity Scores");		
	}

		
		
	private void calculateTanimotoOnSelectedNodes(CyNetworkView nv, String attribute, AttriType attrType) {
		CyNetwork network = nv.getNetwork();
		network.selectAllEdges();
		network.selectAllNodes();
		List edges = network.edgesList();
		List rows = new ArrayList();
		for (Object obj: edges) {
			CyEdge edge = (CyEdge)obj;
			CyNode node1 = (CyNode)edge.getSource();
			CyNode node2 = (CyNode)edge.getTarget();
			List row = new ArrayList();
			row.add(edge.getIdentifier());
			row.add(node1.getIdentifier());
			row.add(node2.getIdentifier());
			CDKTanimotoScore scorer = new CDKTanimotoScore(node1, node2, attribute, attrType);
			CyAttributes attrs = Cytoscape.getEdgeAttributes();
			double score = scorer.calculateSimilarity();
			attrs.setAttribute(edge.getIdentifier(), "tanimoto", score); 
			row.add(score);
			rows.add(row);
		}
		List colNames = new ArrayList();
		colNames.add("Edge ID");
		colNames.add("Node1 ID");
		colNames.add("Node2 ID");
		colNames.add("Tanimoto Coefficient");
		ChemTableSorter sorter = new ChemTableSorter(rows, colNames);
		
		ChemTable table = new EdgeTable(sorter, nv.getIdentifier(), attribute, attrType);

		table.getColumnModel().getColumn(1).setCellRenderer(
				new TextAreaRenderer());
		
		table.getColumnModel().getColumn(2).setCellRenderer(
				new TextAreaRenderer());		

		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionBackground(Color.CYAN);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		//networkView.getNetwork().addSelectEventListener(table);
		sorter.setTableHeader(table.getTableHeader());
		table.showTableDialog("Show Similarity Scores");				
		
	}

	/**
	 * Returns true if a string is null or blank
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		return null == str || "".equals(str.trim());
	}

	/**
	 * Depict 2D structure for multiple nodes
	 * 
	 * @param nodes
	 */
	private void depictMultipleNodes(List nodes, CyNetworkView networkView, String attribute, AttriType attrType) {
		List rows = new ArrayList();
		for (Object node : nodes) {
			NodeView nodeView = (NodeView) node;
			// depictSingleNode((CyNode)nodeView.getNode());
			StructureDepictor depictor = new StructureDepictor(
					(CyNode) nodeView.getNode(), attribute, attrType);
			// Image image = depictor.
			String text = depictor.getMoleculeString();
			String id = nodeView.getNode().getIdentifier();
			List row = new ArrayList();
			row.add(id);
			row.add(text);
			row.add(depictor);
			rows.add(row);
		}

		List colNames = new ArrayList();
		colNames.add("Identifier");
		colNames.add("Smiles/InChI");
		colNames.add("2D Structure");

		ChemTableSorter sorter = new ChemTableSorter(rows, colNames);
		NodeTable table = new NodeTable(sorter, networkView.getIdentifier(), attribute, attrType);
		table.setStructureColumn(2);
		table.setRowHeight(121);

		TableColumn col = table.getColumnModel().getColumn(
				table.getStructureColumn());
		col.setWidth(121);
		col.setPreferredWidth(121);
		col.setResizable(false);
		MoleculeCellRenderer mcr = new MoleculeCellRenderer(new Dimension(120,
				120));
		col.setCellRenderer(mcr);

		table.getColumnModel().getColumn(1).setPreferredWidth(250);
		table.getColumnModel().getColumn(1).setCellRenderer(
				new TextAreaRenderer());

		table.setColumnSelectionAllowed(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionBackground(Color.CYAN);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		networkView.getNetwork().addSelectEventListener(table);
		sorter.setTableHeader(table.getTableHeader());
		table.showTableDialog("Show 2D Structures");
	}

	/**
	 * Depict 2D structure for a single node
	 * 
	 * @param node
	 */
	private void depictSingleNode(CyNode node, String attribute, AttriType attrType) {
		StructureDepictor depictor = new StructureDepictor(node, attribute, attrType);
		if (null == depictor.getMoleculeString()
				|| "".equals(depictor.getMoleculeString())) {
			displayErrorDialog("cheminfo.depictor.noSmilesError");
			return;
		}
		MoleculeViewDialog dialog = new MoleculeViewDialog(Cytoscape
				.getDesktop());
		dialog.setSize(320, 320);
		if (dialog.setDepictor(depictor)) {
			dialog.setLocationRelativeTo(Cytoscape.getDesktop());
			dialog.pack();
			dialog.setVisible(true);
		} else {
			displayErrorDialog("cheminfo.system.error");
		}
	}

	/**
	 * Display an error message
	 * 
	 * @param message
	 */
	public void displayErrorDialog(String messageKey) {
		JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), systemProps
				.getProperty(messageKey), "ChemInfo Plugin Error!",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Display a message
	 * 
	 * @param message
	 */
	public void displayMessageDialog(String messageKey) {
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), systemProps
				.getProperty(messageKey), "ChemInfo Plugin Message",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private Object[] showAttributeDialog(String[] attributes) {
		JComboBox combo = new JComboBox(attributes);
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout());
		pane.add(combo, BorderLayout.CENTER);
		
		final ButtonGroup group = new ButtonGroup();
		JRadioButton radio1 = new JRadioButton("SMILES");
		JRadioButton radio2 = new JRadioButton("InChI");
		group.add(radio1);
		group.add(radio2);
		radio1.setSelected(true);
		JPanel box = new JPanel();
		JLabel label = new JLabel("Type of attribute value:");
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);
		box.add(radio1);
		box.add(radio2);
		pane.add(box, BorderLayout.SOUTH);
		
		JOptionPane editPane = new JOptionPane(pane);
		JDialog dialog = editPane.createDialog(Cytoscape.getDesktop(), "Test");
		dialog.show();
		String value = (String)combo.getSelectedItem();
		return new Object[] { value, radio1.isSelected()? AttriType.smiles : AttriType.inchi };
	}

	private int showSimilarityOptionDialog(int selected) {
		JRadioButton[] radioButtons = new JRadioButton[4];
		final ButtonGroup group = new ButtonGroup();
		for (int i = 0; i < 2; i++) {
			radioButtons[i] = createRadioButton("cheminfo.similarity.nodeset."
					+ i);
			group.add(radioButtons[i]);
		}
		radioButtons[selected].setSelected(true);
		JPanel box = new JPanel();
		JLabel label = new JLabel(systemProps
				.getProperty("cheminfo.similarity.nodeset"));
		box.setLayout(new BoxLayout(box, BoxLayout.PAGE_AXIS));
		box.add(label);

		for (int i = 0; i < 2; i++) {
			box.add(radioButtons[i]);
		}
		box.setBorder(new TitledBorder("Options:"));

		JPanel pane = new JPanel(new BorderLayout());
		pane.add(box, BorderLayout.NORTH);

		JOptionPane editPane = new JOptionPane(pane);
		JDialog dialog = editPane.createDialog(Cytoscape.getDesktop(), "Test");
		dialog.show();
		int selectedOption = -1;
		for (int i = 0; i < 2; i++) {
			if (radioButtons[i].isSelected()) {
				selectedOption = i;
			}
		}
		return selectedOption;
	}

	private JRadioButton createRadioButton(String messageKey) {
		return new JRadioButton(systemProps.getProperty(messageKey));
	}
}
