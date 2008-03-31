package clusterExplorerPlugin;

import giny.model.Node;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;


public class ClusterExplorerGui extends JPanel implements ActionListener, MouseListener {
	
	private Vector<String> validEdgeAttributes = new Vector<String>();
	private JComboBox validEdgeAttributesComboBox = new JComboBox();
	
	private Vector<String> validNodeAttributes = new Vector<String>();
	private JComboBox validNodeAttributesComboBox = new JComboBox();
	private JComboBox validNodeAttributesComparisonGoldStandardComboBox = new JComboBox();
	private JComboBox validNodeAttributesComparisonClusteringComboBox = new JComboBox();
	
	private JComboBox methodsComboBox = new JComboBox();
	
	private JPanel resultsPanel;
	private JTextField simMissingEdgesTextField = new JTextField("0");
	
	private JButton similarityHistogramButton = new JButton("Plot intra/inter cluster edge weight histogram");
	private JSlider similarityHistogramNumClassesSlider = new JSlider(5,50,10);
	
	private JButton clusterSizeHistogramButton = new JButton("Plot cluster size histogram");
	private JSlider clusterSizeHistogramNumClassesSlider = new JSlider(5,50,10);
	
	private JButton startButton;
	
	private Clusters clusters;
	
	private List<CyNode> nodes;
	private List<CyEdge> edges;
	
	private JRadioButton simRadioButton = new JRadioButton("Similarity");
	private JRadioButton distRadioButton = new JRadioButton("Distance");
	
	private JCheckBox minusLog = new JCheckBox("Negative: Use -LOG(x) instead of LOG(x)?");
	private JTextField logBasisTextField = new JTextField("10");
	private JTextField logEdgeAttributeNameTextField = new JTextField("LOG_edge_weight");
	
	public ClusterExplorerGui() {
		
		makePanels();
		
	}
	
	private void makePanels() {
		
		// ---------------- ATTRIBUTES
		
		JPanel attributesPanel = new JPanel();
		
		attributesPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Attributes"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		fillValidEdgeAttributesComboBox();
		
		attributesPanel.setLayout(new SpringLayout());
		
		attributesPanel.add(new JLabel("Edge weight attribute:"));
		attributesPanel.add(validEdgeAttributesComboBox);
    	validEdgeAttributesComboBox.addMouseListener(this);
		
		fillValidNodeAttributesComboBox(validNodeAttributesComboBox);
		attributesPanel.add(new JLabel("Cluster ID attribute:"));
		attributesPanel.add(validNodeAttributesComboBox);
		validNodeAttributesComboBox.addMouseListener(this);
		
		attributesPanel.add(new JLabel("Weight of missing edges:"));
		attributesPanel.add(simMissingEdgesTextField);
		
		this.simRadioButton.setSelected(true);
		
		ButtonGroup group = new ButtonGroup();
		group.add(this.simRadioButton);
		group.add(this.distRadioButton);
		
		JPanel dummy7_1 = new JPanel();
		dummy7_1.setLayout(new BoxLayout(dummy7_1,BoxLayout.Y_AXIS));
		
		dummy7_1.add(this.simRadioButton);
		dummy7_1.add(this.distRadioButton);
		
		attributesPanel.add(new JLabel("Edge weights correspond to"));
		attributesPanel.add(dummy7_1);
		
		
		SpringUtilities.makeCompactGrid(attributesPanel, 4, 2, 5, 5, 5, 5);
		
		
		// ---------------- START/METHOD
		
		JPanel methodStartPanel = new JPanel();
		methodStartPanel.setLayout(new BoxLayout(methodStartPanel,BoxLayout.Y_AXIS));
		methodStartPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Start/Methods"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		fillValidMethodsComboBox();
		JPanel dummy3 = new JPanel();
		dummy3.setLayout(new BoxLayout(dummy3,BoxLayout.X_AXIS));
		dummy3.add(new JLabel("Method:"));
		dummy3.add(methodsComboBox);
		
		startButton = new JButton("Apply method to graph");
		startButton.setActionCommand("start");
		startButton.addActionListener(this);
		
		JPanel dummy3_1 = new JPanel();
		dummy3_1.add(startButton);
		
		methodStartPanel.add(dummy3);
		methodStartPanel.add(dummy3_1);
		
		// ---------------- HISTOGRAMS
		
		JPanel histogramsPanel = new JPanel();
		histogramsPanel.setLayout(new BoxLayout(histogramsPanel,BoxLayout.Y_AXIS));
		
		histogramsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Plot histograms"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		this.similarityHistogramNumClassesSlider.setMajorTickSpacing(5);
		this.similarityHistogramNumClassesSlider.setMinorTickSpacing(1);
		this.similarityHistogramNumClassesSlider.setPaintTicks(true);
		this.similarityHistogramNumClassesSlider.setPaintLabels(true);
		this.similarityHistogramNumClassesSlider.setMaximumSize(new Dimension(300, 50));
		
		JPanel dummy5 = new JPanel();
		dummy5.add(this.similarityHistogramButton);
		
		JPanel dummy5_1 = new JPanel();
		dummy5_1.setLayout(new BoxLayout(dummy5_1,BoxLayout.X_AXIS));
		JLabel sliderLabel = new JLabel("Buckets:");
		dummy5_1.add(sliderLabel);
		dummy5_1.add(this.similarityHistogramNumClassesSlider);
		
		this.clusterSizeHistogramNumClassesSlider.setMajorTickSpacing(5);
		this.clusterSizeHistogramNumClassesSlider.setMinorTickSpacing(1);
		this.clusterSizeHistogramNumClassesSlider.setPaintTicks(true);
		this.clusterSizeHistogramNumClassesSlider.setPaintLabels(true);
		this.clusterSizeHistogramNumClassesSlider.setMaximumSize(new Dimension(300, 50));
		
		JPanel dummy6 = new JPanel();
		dummy6.add(this.clusterSizeHistogramButton);
		
		JPanel dummy6_1 = new JPanel();
		dummy6_1.setLayout(new BoxLayout(dummy6_1,BoxLayout.X_AXIS));
		JLabel sliderLabel2 = new JLabel("Buckets:");
		dummy6_1.add(sliderLabel2);
		dummy6_1.add(this.clusterSizeHistogramNumClassesSlider);
		
		similarityHistogramButton.setActionCommand("plotEdgeWeightHistogram");
		similarityHistogramButton.addActionListener(this);
		
		clusterSizeHistogramButton.setActionCommand("plotClusterSizeHistogram");
		clusterSizeHistogramButton.addActionListener(this);
		
		histogramsPanel.add(dummy5_1);
		histogramsPanel.add(dummy5);
		
		histogramsPanel.add(dummy6_1);
		histogramsPanel.add(dummy6);
		
		// --------------- Comparions of two clusterings
		
		JPanel comparisonAttributesPanel = new JPanel();
		comparisonAttributesPanel.setLayout(new SpringLayout());
		
		comparisonAttributesPanel.add(new JLabel("Gold standard cluster ID attribute:"));
		comparisonAttributesPanel.add(this.validNodeAttributesComparisonGoldStandardComboBox);
		validNodeAttributesComparisonGoldStandardComboBox.addMouseListener(this);
		
		comparisonAttributesPanel.add(new JLabel("Cluster ID attribute:"));
		comparisonAttributesPanel.add(this.validNodeAttributesComparisonClusteringComboBox);
		validNodeAttributesComparisonClusteringComboBox.addMouseListener(this);
		
		SpringUtilities.makeCompactGrid(comparisonAttributesPanel, 2, 2, 5, 5, 5, 5);
		
		JPanel comparisonPanel = new JPanel();
		comparisonPanel.setLayout(new BoxLayout(comparisonPanel,BoxLayout.Y_AXIS));
		comparisonPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Clustering comparison"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		JButton comparisonButton = new JButton("Start comparison");
		comparisonButton.addActionListener(this);
		comparisonButton.setActionCommand("comparison");
		
		JPanel comparisonButtonPanel = new JPanel();
		comparisonButtonPanel.add(comparisonButton);
				
		comparisonPanel.add(comparisonAttributesPanel);
		comparisonPanel.add(comparisonButtonPanel);
		
		// --------------- LOG transfer edge weights
		
		JPanel logAttributesPanel = new JPanel();
		logAttributesPanel.setLayout(new SpringLayout());
		
		logAttributesPanel.add(new JLabel("LOG basis:"));
		logAttributesPanel.add(this.logBasisTextField);
		
		logAttributesPanel.add(new JLabel("LOG-transformed edge attribute:"));
		logAttributesPanel.add(this.logEdgeAttributeNameTextField);
		
		SpringUtilities.makeCompactGrid(logAttributesPanel, 2, 2, 5, 5, 5, 5);
		
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel,BoxLayout.Y_AXIS));
		logPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("LOG-transform edge weights"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		JButton logTransformButton = new JButton("Start LOG transformation");
		logTransformButton.addActionListener(this);
		logTransformButton.setActionCommand("log");
		
		JPanel logDummyPanel = new JPanel();
		logDummyPanel.add(logTransformButton);
		
		this.minusLog.setSelected(true);
		
		JPanel logDummyPanel2 = new JPanel();
		logDummyPanel2.add(this.minusLog);
		
		logPanel.add(logAttributesPanel);
		logPanel.add(logDummyPanel2);
		logPanel.add(logDummyPanel);
		
		// ---------------- INFO
		
		JPanel infoPanel = new JPanel();
		JButton helpButton = new JButton("About/help");
		helpButton.setActionCommand("info");
		helpButton.addActionListener(this);
		
		infoPanel.add(helpButton);
		
		// ---------------- MAIN
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		this.add(attributesPanel);
		this.add(methodStartPanel);
		this.add(histogramsPanel);
		this.add(comparisonPanel);
		this.add(logPanel);
		this.add(new JLabel("   "));
		this.add(infoPanel);
		
		
	}
	
	
	private void fillComboBox(JComboBox cb, Vector<String> elements) {
		
		String sel = (String) cb.getSelectedItem();
		
		cb.removeAllItems();
		for (int i = 0; i < elements.size(); i++) {
			cb.addItem(elements.get(i));
		}
		
		cb.setSelectedItem(sel);
		
	}
	
	private void fillValidMethodsComboBox() {
		
		Vector<String> methods = new Vector<String>();
		methods.add("Cluster: central element");
		methods.add("Cluster: sim. to other clusters");
		methods.add("Cluster: sim. to other elements");
		methods.add("Element: sim. to other clusters");
		methods.add("Element: sim. to other elements");
		
		fillComboBox(this.methodsComboBox, methods);
		
	}
	
	private void fillValidNodeAttributesComboBox(JComboBox box) {
		
		Vector<String> validNodeAttributesForComboBox = new Vector<String>();
		this.validNodeAttributes.removeAllElements();
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
    	String[] nodeAttributeNames = nodeAttributes.getAttributeNames();
    	
    	for(int i=0; i < nodeAttributeNames.length; i++) {
    		
    		String name = nodeAttributeNames[i];
    		byte type = nodeAttributes.getType(name);
    		
    		String typeStr = null;
    		if (type == CyAttributes.TYPE_INTEGER) {
    			typeStr = "INTEGER";
    		} else if (type == CyAttributes.TYPE_FLOATING) {
    			typeStr = "FLOAT";
    		} else if (type == CyAttributes.TYPE_STRING) {
    			typeStr = "STRING";
    		} else if (type == CyAttributes.TYPE_BOOLEAN) {
    			typeStr = "BOOLEAN";
    		}
    		
    		if (typeStr != null) {
    			this.validNodeAttributes.add(name);
    			validNodeAttributesForComboBox.add(name + ":: " + typeStr);
    			
    		}
    		
    	}
    	
    	fillComboBox(box, validNodeAttributesForComboBox);
	}
	
	private void fillValidEdgeAttributesComboBox() {
		
		Vector<String> validEdgeAttributesForComboBox = new Vector<String>();
		this.validEdgeAttributes.removeAllElements();
		
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
    	String[] edgeAttributeNames = edgeAttributes.getAttributeNames();
    	
    	for(int i=0; i < edgeAttributeNames.length; i++) {
    		
    		String name = edgeAttributeNames[i];
    		byte type = edgeAttributes.getType(name);
    		
    		String typeStr = null;
    		if (type == CyAttributes.TYPE_INTEGER) {
    			typeStr = "INTEGER";
    		} else if (type == CyAttributes.TYPE_FLOATING) {
    			typeStr = "FLOAT";
    		}
    		
    		if (typeStr != null) {
    			this.validEdgeAttributes.add(name);
    			validEdgeAttributesForComboBox.add(name + " :: " + typeStr);
    		}
    		
    	}
    	
    	fillComboBox(validEdgeAttributesComboBox, validEdgeAttributesForComboBox);
	}
	
	private boolean initClusters() {
		
		int edgeAttrIndex = validEdgeAttributesComboBox.getSelectedIndex();
		int nodeAttrIndex = validNodeAttributesComboBox.getSelectedIndex();
		
		if (edgeAttrIndex != -1 && nodeAttrIndex != -1) {
			
			String attrNameSim = validEdgeAttributes.get(edgeAttrIndex);
			String attrNameCluster = validNodeAttributes.get(nodeAttrIndex);
			
			float initValue = Float.parseFloat(this.simMissingEdgesTextField.getText().trim());
			
			this.clusters = initClusterExplorer(attrNameSim, attrNameCluster, initValue);
			
		} else {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge attribute (similarity) and a node attribute (cluster ID)!");
			
			return false;
		}
		
		return true;
	}
	
	private boolean areAllNodesInOneCluster(Set<CyNode> selectedNodes, Clusters cs) {
		
		int i = 0;
		String cID = "";
		for (Iterator<CyNode> iterator = selectedNodes.iterator(); iterator.hasNext();) {
			CyNode selectedNode = iterator.next();
			Cluster c = cs.getClusterOfElement(selectedNode.getIdentifier());
			if (i==0) {
				cID = c.getID();
				i++;
			}
			if (cID != c.getID()) {
				return false;
			}
		}
		
		return true;
	}
	
	private void runClusterExplorer() {
		
		int edgeAttrIndex = validEdgeAttributesComboBox.getSelectedIndex();
		int nodeAttrIndex = validNodeAttributesComboBox.getSelectedIndex();
		int methodIndex = methodsComboBox.getSelectedIndex();
		
		if (methodIndex == -1) {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select a method!");
			
		} else if (edgeAttrIndex != -1 && nodeAttrIndex != -1) {
			
			Set<CyNode> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
			
			if (selectedNodes.size() == 0) {
				
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select at least one node as starting point.");
				
			} else if (((methodIndex == 3) || (methodIndex == 4)) && (selectedNodes.size() != 1)) {
				
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select exactly one node as starting point.");
				
			} else if (!areAllNodesInOneCluster(selectedNodes, clusters)) {
				
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select nodes from one (!) cluster as starting point.");
				
			} else {
				
				Iterator<CyNode> it = selectedNodes.iterator();
				CyNode selectedNode = it.next();
				String selectedNodeID = selectedNode.getIdentifier();
				
				CytoscapeDesktop desktop = Cytoscape.getDesktop();
		        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
				
				if (this.resultsPanel != null) {
					
					cytoPanel.remove(this.resultsPanel);
		            if (cytoPanel.getCytoPanelComponentCount() == 0) {
		            	cytoPanel.setState(CytoPanelState.HIDE);
		            }
		            
				}
				
				boolean order = true;
				if (!this.simRadioButton.isSelected()) {
					order = false;
				}
				
				String title = "";
				if (methodIndex == 0) {
					findCentrals(clusters, selectedNodeID, order);
					title = "CE: Centrals for cluster " + clusters.getClusterOfElement(selectedNodeID).getID();
				} else if (methodIndex == 1) {
					findClusterSimToOtherClusters(clusters, selectedNodeID, order);
					title = "CE: sim. of cluster " + clusters.getClusterOfElement(selectedNodeID).getID() + " to others clusters";
				} else if (methodIndex == 2) {
					findClusterSimToOtherElements(clusters, selectedNodeID, order);
					title = "CE: sim. of cluster " + clusters.getClusterOfElement(selectedNodeID).getID() + " to other elements";
				} else if (methodIndex == 3) {
					findElementSimToOtherClusters(clusters, selectedNodeID, order);
					title = "CE: sim. of element " + selectedNodeID + " to other clusters";
				} else if (methodIndex == 4) {
					findElementSimToOtherElements(clusters, selectedNodeID, order);
					title = "CE: sim. of element " + selectedNodeID + " to other elements";
				}
				
				cytoPanel.add(title, this.resultsPanel);
	            cytoPanel.setState(CytoPanelState.DOCK);
				
	            this.resultsPanel.updateUI();
				
			}
			
		} else {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge attribute (similarity) and a node attribute (cluster ID)!");
			
		}
		
	}
	
	
	
	private void findCentrals(Clusters cs, String selectedNodeID, boolean order) {
		
		Cluster c = cs.getClusterOfElement(selectedNodeID);
		Vector<ClusterElementSimilarity> v = c.getOrderedCentralElementsList(order);
		
		this.resultsPanel = new ClusterExplorerGuiResultsElements(cs, v, true);
		
	}
	
	private void findClusterSimToOtherClusters(Clusters cs, String selectedNodeID, boolean order) {
		Cluster c = cs.getClusterOfElement(selectedNodeID);
		Vector<ClusterClusterSimilarity> v = cs.getOrderedClusterSimilarityListForCluster(c, order);
		
		this.resultsPanel = new ClusterExplorerGuiResultsClusters(cs, v, false);
		
	}
	
	private void findClusterSimToOtherElements(Clusters cs, String selectedNodeID, boolean order) {
		
		Cluster c = cs.getClusterOfElement(selectedNodeID);
		Vector<ClusterElementSimilarity> v = cs.getOrderedForeignElementSimilarityListForCluster(c, order);
		
		this.resultsPanel = new ClusterExplorerGuiResultsElements(cs, v, true);
		
	}
	
	private void findElementSimToOtherClusters(Clusters cs, String selectedNodeID, boolean order) {
		
		Vector<ClusterElementSimilarity> v = cs.getOrderedForeignClusterSimilarityListForElement(selectedNodeID, order);
		
		this.resultsPanel = new ClusterExplorerGuiResultsClusters(cs, v, true);
		
	}
	
	private void findElementSimToOtherElements(Clusters cs, String selectedNodeID, boolean order) {
		
		Vector<ElementElementSimilarity> v = cs.getOrderedElementSimilarityListForElement(selectedNodeID, order);
		
		this.resultsPanel = new ClusterExplorerGuiResultsElements(cs, v, false);
		
	}

	private Clusters initClusterExplorer(String attrNameSim, String attrNameCluster, float initValue) {
		
		Console.startNewConsoleWindow(0,3,"Reading mapping...");
		Mapping m = createMapping();
		Console.setBarValue(1);
		
		Console.setBarText("Constructing similarity matrix...");
		Sim s = createSim(m, initValue, attrNameSim);
		Console.setBarValue(2);
		
		Console.setBarText("Reading clusters...");
		Clusters cs = createClusters(m, s, attrNameCluster);
		Console.setBarValue(3);
		
		Console.closeWindow();
		
		return cs;
	}
	
	private void plotClusterSizeHistogram() {
		int nrClasses = this.clusterSizeHistogramNumClassesSlider.getValue();
		
		Vector<Float> sizes = this.clusters.getClusterSizes();
		
		Vector<Vector<Float>> data = new Vector<Vector<Float>>();
		data.add(sizes);
		
		String[] labels = {""};
		
		new HistogramPlotWindow(data, nrClasses, "Cluster size distribution", "Cluster size", "Frequency", labels);
		
	}
	
	private void plotEdgeWeightHistogram() {
		int nrClasses = this.similarityHistogramNumClassesSlider.getValue();
		
		Vector<Float> intra = this.clusters.getIntraClusterSimilarities();
		Vector<Float> inter = this.clusters.getInterClusterSimilarities();
		
		Vector<Vector<Float>> data = new Vector<Vector<Float>>();
		data.add(intra);
		data.add(inter);
		
		String[] labels = {"Intra cluster", "Inter cluster"};
		
		new HistogramPlotWindow(data, nrClasses, "Edge weight distribution", "Edge weight", "Frequency", labels);
		
	}
	
	
	private Mapping createMapping() {
		
		buildNodesAndEdgesList();
		
		Mapping m = new Mapping();
		
		int num = 0;
		for (Iterator<CyNode> i = nodes.iterator(); i.hasNext();) {
			
			CyNode node = i.next();
			String nodeID = node.getIdentifier();
			
			// THIS IS A STRANGE BUG IN CYTOSCAPE; it always give u 2 additional nodes :-(
			if (!nodeID.equalsIgnoreCase("Source") && !nodeID.equalsIgnoreCase("Target")) {
				m.add(nodeID, num);
				num++;
			}
		}
		
		return m;
	}
	
	private Sim createSim(Mapping m, float initValue, String attrNameSim) {
		buildNodesAndEdgesList();
		
		
		Sim sim;
		if (initValue != 0) {
			sim = new Sim(m, initValue);
		} else {
			sim = new Sim(m);
		}
		
		
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		
		byte type = edgeAttributes.getType(attrNameSim);
		
		for (Iterator<CyEdge> i = this.edges.iterator(); i.hasNext();) {
			
			CyEdge edge = i.next();
			String edgeID = edge.getIdentifier();
			
			if (edgeAttributes.hasAttribute(edgeID, attrNameSim)) {
				
				Node sourceNode = edge.getSource();
				Node targetNode = edge.getTarget();
				
				String sourceNodeID = sourceNode.getIdentifier();
				String targetNodeID = targetNode.getIdentifier();
				
				if (type == CyAttributes.TYPE_INTEGER) {
					try {
						edgeAttributes.getIntegerAttribute(edge.getIdentifier(), attrNameSim);
					} catch(NullPointerException e1) {
						continue;
					}
					
				} else {
					try {
						edgeAttributes.getDoubleAttribute(edge.getIdentifier(), attrNameSim);
					} catch(NullPointerException e1) {
						continue;
					}
					
				}
				
				float value = getAttributeFloat(edgeAttributes, edgeID, attrNameSim);
				
				sim.set(sourceNodeID, targetNodeID, value);
				
			}
			
		}
		
		return sim;
	}
	
	private Clusters createClusters(Mapping m, Sim sim, String attrNameCluster) {
		
		buildNodesAndEdgesList();
		
		Clusters cs = new Clusters(m, sim);
		
		Hashtable<String, Cluster> csHash = new Hashtable<String, Cluster>();
		
		
		
		CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
		
			
		int singletons = 0;
		for (Iterator<CyNode> i = this.nodes.iterator(); i.hasNext();) {
			
			CyNode node = i.next();
			String nodeID = node.getIdentifier();
			
			// THIS IS A STRANGE BUG IN CYTOSCAPE; it always give u 2 additional nodes :-(
			if (!nodeID.equalsIgnoreCase("Source") && !nodeID.equalsIgnoreCase("Target")) {
				
				String clusterID;
				if (!nodeAttributes.hasAttribute(nodeID, attrNameCluster)) {
					clusterID = "SingletonClusterID#" + singletons;
					singletons++;
				} else {
					clusterID = getAttributeString(nodeAttributes, nodeID, attrNameCluster);
				}
				
				if (csHash.containsKey(clusterID)) {
					Cluster c = csHash.get(clusterID);
					c.add(m.getNumber(nodeID));
				} else {
					Cluster c = new Cluster(m, sim, clusterID);
					c.add(m.getNumber(nodeID));
					csHash.put(clusterID, c);
				}
				
			}
			
		}
		
		Enumeration<Cluster> e = csHash.elements();
		while(e.hasMoreElements()) {
			cs.addCluster(e.nextElement());
		}
		
		return cs;
	}
	
	private void runComparison() {
		
		buildNodesAndEdgesList();
		CyAttributes cyNodeAttributes = Cytoscape.getNodeAttributes();
		
		int nodeGoldStandardAttrIndex = this.validNodeAttributesComparisonGoldStandardComboBox.getSelectedIndex();
		int nodeClusteringAttrIndex = this.validNodeAttributesComparisonClusteringComboBox.getSelectedIndex();
		
		if (nodeGoldStandardAttrIndex != -1 && nodeClusteringAttrIndex != -1) {
			
			String attrNameGoldStandard = validNodeAttributes.get(nodeGoldStandardAttrIndex);
			String attrNameCluster = validNodeAttributes.get(nodeClusteringAttrIndex);
			
			
			Hashtable<String,Hashtable<String, CyNode>> goldStandard = new Hashtable<String,Hashtable<String, CyNode>>();
			Hashtable<String,Hashtable<String, CyNode>> compareCluster = new Hashtable<String,Hashtable<String, CyNode>>();
			
			Console.startNewConsoleWindow(0, 5, "Searching clusters...");
			
			for (int i = 0; i < nodes.size(); i++) {
				CyNode n = nodes.get(i);
				String id = n.getIdentifier();
				String goldStandardClusterID = getAttributeString(cyNodeAttributes, id, attrNameGoldStandard);			
				String compareClusterID = getAttributeString(cyNodeAttributes, id, attrNameCluster);
				if(goldStandard.containsKey(goldStandardClusterID)){
					Hashtable<String,CyNode> dummy = goldStandard.get(goldStandardClusterID);
					dummy.put(id, n);
					goldStandard.put(goldStandardClusterID, dummy);
				}else{
					Hashtable<String, CyNode> dummy = new Hashtable<String, CyNode>();
					dummy.put(id, n);
					goldStandard.put(goldStandardClusterID, dummy);
				}
				
				if(compareCluster.containsKey(compareClusterID)){
					Hashtable<String,CyNode> dummy = compareCluster.get(compareClusterID);
					dummy.put(id, n);
					compareCluster.put(compareClusterID, dummy);
				}else{
					Hashtable<String, CyNode> dummy = new Hashtable<String, CyNode>();
					dummy.put(id, n);
					compareCluster.put(compareClusterID, dummy);
				}
				
				
			}
			
			
			// recall = tp/tp+fn
			
			Console.setBarValue(1);
			Console.setBarText("Calculating recall...");
			double recall = calculateRecall(goldStandard,compareCluster);
			
			//precision = tp/tp+fp
			Console.setBarValue(2);
			Console.setBarText("Calculating precision...");
			double precicision = calculatePrecision(goldStandard,compareCluster);
			
			Console.setBarValue(3);
			Console.setBarText("Calculating F-Measure...");
			double fMeasure = calculateFmeasure(goldStandard,compareCluster);
			
			Console.setBarValue(4);
			Console.setBarText("Calculating F-Measure II...");
			double fMeasureUsingSensitivitySpecifity = (2*recall*precicision)/(recall+precicision);
			
			Console.closeWindow();
			
			CytoscapeDesktop desktop = Cytoscape.getDesktop();
	        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
			
	        if (this.resultsPanel != null) {
				
				cytoPanel.remove(this.resultsPanel);
	            if (cytoPanel.getCytoPanelComponentCount() == 0) {
	            	cytoPanel.setState(CytoPanelState.HIDE);
	            }
	            
			}
			
	        this.resultsPanel = new ComparisonResults(recall, precicision, fMeasureUsingSensitivitySpecifity, fMeasure);
	        
			cytoPanel.add("Comparison results", this.resultsPanel);
            cytoPanel.setState(CytoPanelState.DOCK);
			
            this.resultsPanel.updateUI();
			
			
		} else {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select two node attributes (cluster IDs)!");
			
			return;
		}
		
	}
	
	private class ComparisonResults extends JPanel implements ActionListener{
		
		double recall, precision, fm1, fm2;
		public ComparisonResults(double recall, double precision, double fm1, double fm2) {
			this.recall = recall;
			this.precision = precision;
			this.fm1 = fm1;
			this.fm2 = fm2;
			
			Vector<String> columnNames = new Vector<String>();
			columnNames.add("Parameter");
			columnNames.add("Value");
			
			Vector<Vector<String>> data = new Vector<Vector<String>>();
			
			Vector<String> v1 = new Vector<String>();
			v1.add("Recall");
			v1.add("" + this.recall);
			data.add(v1);
			
			Vector<String> v2 = new Vector<String>();
			v2.add("Precision");
			v2.add("" + this.precision);
			data.add(v2);
			
			Vector<String> v3 = new Vector<String>();
			v3.add("F-Measure");
			v3.add("" + this.fm1);
			data.add(v3);
			
			Vector<String> v4 = new Vector<String>();
			v4.add("F-Measure II");
			v4.add("" + this.fm2);
			data.add(v4);
			
			JTable t = new JTable(data, columnNames);
			
			
			JButton clearButton = new JButton("Destroy results");
			clearButton.setActionCommand("clear");
			clearButton.addActionListener(this);
			
			JPanel clearDummyPanel = new JPanel();
			clearDummyPanel.add(clearButton);
			
			JScrollPane scrollpane = new JScrollPane(t);
			
			this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
			
			this.add(scrollpane);
			this.add(clearDummyPanel);
			
		}
		
		public void actionPerformed(ActionEvent e) {
			
			String c = e.getActionCommand();
			
			if (c.equalsIgnoreCase("clear")) {
				
				CytoscapeDesktop desktop = Cytoscape.getDesktop();
	            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
	            
	            cytoPanel.remove(this);
	            if (cytoPanel.getCytoPanelComponentCount() == 0) {
	            	cytoPanel.setState(CytoPanelState.HIDE);
	            }
				
			}
			
		}
		
	}
	
	private double calculatePrecision(Hashtable<String, Hashtable<String, CyNode>> goldStandard2, Hashtable<String, Hashtable<String, CyNode>> compareCluster) {
		double precision = 0;
		double truePositives = 0;
		double falseNegatives = 0;
		double falsePositives = 0;
		double numberOfNodes = 0;
		
		for (Iterator iter = goldStandard2.keySet().iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			Hashtable<String , CyNode> goldStandard = goldStandard2.get(element);
			numberOfNodes+=goldStandard.size();
			double maxTruePositivesForCurrentCluster = 0;
			double currentFalseNegatives = 0;
			double currentFalsePositives = 0;
			for (Iterator iterator = compareCluster.keySet().iterator(); iterator.hasNext();) {
				String element2 = (String) iterator.next();
				Hashtable<String , CyNode> compare = compareCluster.get(element2);
				double truePositivesForThisCluster = 0;
				//true positives
				for (Iterator iter2 = compare.keySet().iterator(); iter2.hasNext();) {
					String key = (String) iter2.next();
					if(goldStandard.containsKey(key)) truePositivesForThisCluster++;
				}
				if(truePositivesForThisCluster>maxTruePositivesForCurrentCluster) {
					maxTruePositivesForCurrentCluster = truePositivesForThisCluster;
					currentFalsePositives = compare.size()-truePositivesForThisCluster;
					currentFalseNegatives = goldStandard.size()-truePositivesForThisCluster;
				}
			}
			truePositives += maxTruePositivesForCurrentCluster;
			falsePositives += currentFalsePositives;
			falseNegatives += currentFalseNegatives;
		}			
		precision = truePositives/(truePositives+falsePositives);
		return precision;
	}

	private double calculateRecall(Hashtable<String, Hashtable<String, CyNode>> goldStandard2, Hashtable<String, Hashtable<String, CyNode>> compareCluster) {
		double sensitivity = 0;
		double truePositives = 0;
		double numberOfNodes = 0;
		
		for (Iterator iter = goldStandard2.keySet().iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			Hashtable<String , CyNode> goldStandard = goldStandard2.get(element);
			numberOfNodes+=goldStandard.size();
			double maxTruePositivesForCurrentCluster = 0;
			for (Iterator iterator = compareCluster.keySet().iterator(); iterator.hasNext();) {
				String element2 = (String) iterator.next();
				Hashtable<String , CyNode> compare = compareCluster.get(element2);
				double truePositivesForThisCluster = 0;
				for (Iterator iter2 = compare.keySet().iterator(); iter2.hasNext();) {
					String key = (String) iter2.next();
					if(goldStandard.containsKey(key)) truePositivesForThisCluster++;
				}
				if(truePositivesForThisCluster>maxTruePositivesForCurrentCluster) maxTruePositivesForCurrentCluster = truePositivesForThisCluster;
			}
			truePositives += maxTruePositivesForCurrentCluster;
		}
		
		sensitivity = truePositives/numberOfNodes;
		
		return sensitivity;
	}

	private double calculateFmeasure(Hashtable<String, Hashtable<String, CyNode>> goldStandard, Hashtable<String, Hashtable<String, CyNode>> compare) {
		
		double fmeasure = 0;
		double numberOfNodes = 0;
		
		for (Iterator iter = goldStandard.keySet().iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			Hashtable<String, CyNode> goldStandardCluster = goldStandard.get(element);
			numberOfNodes+=goldStandardCluster.size();
			double nh = goldStandardCluster.size();
			double max = 0;
			for (Iterator iterator = compare.keySet().iterator(); iterator.hasNext();) {
				String element2 = (String) iterator.next();
				Hashtable<String, CyNode> compareCluster = compare.get(element2);
				double nl = compareCluster.size();
				double common = 0;
				for (Iterator i2 = compareCluster.keySet().iterator(); i2.hasNext();) {
					String key = (String) i2.next();
					if(goldStandardCluster.containsKey(key)) common++;
				}
				double dummy = (2*common)/(nl+nh);
				if(dummy>max) max = dummy;
			}
			fmeasure+=(max*nh);
		}
		fmeasure/=numberOfNodes;
		
		return fmeasure;
	}
	
	private void runLogTransformation() {
		
		int edgeAttrIndex = validEdgeAttributesComboBox.getSelectedIndex();
		
		if (edgeAttrIndex != -1 ) {
			
			String attrNameSim = validEdgeAttributes.get(edgeAttrIndex);
			String logAttrName = this.logEdgeAttributeNameTextField.getText();
			boolean minusLog = this.minusLog.isSelected();
			
			buildNodesAndEdgesList();
			
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			byte type = edgeAttributes.getType(attrNameSim);
			
			int count = 0;
			for (Iterator<CyEdge> i = this.edges.iterator(); i.hasNext();) {
				
				CyEdge edge = i.next();
				String edgeID = edge.getIdentifier();
				
				if (edgeAttributes.hasAttribute(edgeID, attrNameSim)) {
					
					if (type == CyAttributes.TYPE_INTEGER) {
						try {
							edgeAttributes.getIntegerAttribute(edge.getIdentifier(), attrNameSim);
						} catch(NullPointerException e1) {
							count++;
							continue;
						}
						
					} else {
						try {
							edgeAttributes.getDoubleAttribute(edge.getIdentifier(), attrNameSim);
						} catch(NullPointerException e1) {
							count++;
							continue;
						}
						
					}
					
					float value = getAttributeFloat(edgeAttributes, edgeID, attrNameSim);
					
					double logValue = Float.MIN_VALUE;
					
					double basis = Double.parseDouble(this.logBasisTextField.getText());
					if (value > 0) {
						
						logValue = Math.log10(value) / Math.log10(basis);
						
					} else if (value < 0) {
						
						JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - Negative edge weight found. LOG(x) undefined for negative values of x. ABORTED!");
						return;
						
					}
					
					if (minusLog) {
						logValue = (-1) * logValue;
					}
					
					edgeAttributes.setAttribute(edge.getIdentifier(), logAttrName, logValue);
					
				}
				
				count++;
			}
			
		} else {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge weight attribute!");
			
			return;
		}
		
		
		
	}
	
	
	
	public static String getAttributeString(CyAttributes attributes, String id, String attributeName) {
		
		byte type = attributes.getType(attributeName);
		String value = "";
		
		if (type == CyAttributes.TYPE_INTEGER) {
			int b = attributes.getIntegerAttribute(id, attributeName);
			value = "" + b;
		} else if (type == CyAttributes.TYPE_BOOLEAN) {
			boolean b = attributes.getBooleanAttribute(id, attributeName);
			value = "" + b;
		} else if (type == CyAttributes.TYPE_FLOATING) {
			double b = attributes.getDoubleAttribute(id, attributeName);
			value = "" +  b;
		} else if (type == CyAttributes.TYPE_STRING) {
			String b = attributes.getStringAttribute(id, attributeName);
			value = "" +  b;
		}
		
		return value;
	}
	
	public static float getAttributeFloat(CyAttributes attributes, String id, String attributeName) {
		
		byte type = attributes.getType(attributeName);
		float value = 0;
		
		if (type == CyAttributes.TYPE_INTEGER) {
			int b = attributes.getIntegerAttribute(id, attributeName);
			value = (float) b;
		} else if (type == CyAttributes.TYPE_BOOLEAN) {
			boolean b = attributes.getBooleanAttribute(id, attributeName);
			if (b) {
				value = 1;
			} else {
				value = -1;
			}
		} else if (type == CyAttributes.TYPE_FLOATING) {
			double b = attributes.getDoubleAttribute(id, attributeName);
			value = (float) b;
		}
		
		return value;
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		
		String c = e.getActionCommand();
		
		if (c.equalsIgnoreCase("start")) {
			
			RunThread r = new RunThread();
			r.start();
			
		} else if (c.equalsIgnoreCase("plotClusterSizeHistogram")) {
			
			ClusterSizeHistogramThread i = new ClusterSizeHistogramThread();
			i.start();
			
		} else if (c.equalsIgnoreCase("plotEdgeWeightHistogram")) {
			
			EdgeWeightHistogramThread i = new EdgeWeightHistogramThread();
			i.start();
			
		} else if (c.equalsIgnoreCase("info")) {
			
			new ClusterExplorerInfoFrame();
			
		} else if (c.equalsIgnoreCase("comparison")) {
			
			runComparison();
			
		} else if (c.equalsIgnoreCase("log")) {
			
			runLogTransformation();
			
		}
		
		
	}
	
	private class InitThread extends Thread {
		
		public InitThread(){
		}
		
		public void run(){
			initClusters();
		}		
	}
	
	private class RunThread extends Thread {
		
		public RunThread(){
		}
		
		public void run(){
			boolean suc = initClusters();
			if (suc) runClusterExplorer();
		}
	}
	
	private class ClusterSizeHistogramThread extends Thread {
		
		public ClusterSizeHistogramThread(){
		}
		
		public void run(){
			boolean suc = initClusters();
			if (suc) plotClusterSizeHistogram();
		}		
	}
	
	private class EdgeWeightHistogramThread extends Thread {
		
		public EdgeWeightHistogramThread(){
		}
		
		public void run(){
			boolean suc = initClusters();
			if (suc) plotEdgeWeightHistogram();
		}
	}
	
	private void buildNodesAndEdgesList() { // remove nodes/edges from foreign network
		CyNetwork net = Cytoscape.getCurrentNetwork();
		
		List<CyEdge> edges = Cytoscape.getCyEdgesList();
		List<CyNode> nodes = Cytoscape.getCyNodesList();
		
		this.nodes = new Vector<CyNode>();
		this.edges = new Vector<CyEdge>();
		
		for (int i = 0; i < edges.size(); i++) {
			if (net.containsEdge(edges.get(i))) {
				this.edges.add(edges.get(i));
			}
		}
		for (int i = 0; i < nodes.size(); i++) {
			if (net.containsNode(nodes.get(i))) {
				this.nodes.add(nodes.get(i));
			}
		}
		
	}

	public void mouseClicked(MouseEvent arg0) {
	}

	public void mouseEntered(MouseEvent e) {
		
		JComboBox box = (JComboBox) e.getSource();
		
		if (box == this.validEdgeAttributesComboBox) {
			
			fillValidEdgeAttributesComboBox();
			
		} else {
			
			fillValidNodeAttributesComboBox(box);
			
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
	
	
	
	
	
	
	
	
	
}


























