package clusterMaker.algorithms.MCL;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupImpl;
import cytoscape.groups.CyGroupManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;


public class MCLgui extends JPanel implements ActionListener, MouseListener {
	
	
	private RunComparisonthread rct;
	
	private JPanel resultsPanel;
	
	private Vector<String> validEdgeAttributes = new Vector<String>();
	private JComboBox validEdgeAttributesComboBox = new JComboBox();
	
	private JButton runMCLbutton;
	
	private JRadioButton simRadioButton = new JRadioButton("Similarity");
	private JRadioButton distRadioButton = new JRadioButton("Distance");
	
	private JSlider inflationSlider = new JSlider(10,50,20);
	
	private JSlider iterationsSlider = new JSlider(5,20,8);
	
	private JSlider probabilityThresholdSlider = new JSlider(-30,0,-15);
	
	private JTextField nodeClusterAttributeName = new JTextField("MCL_cluster");
	
	private Vector<String> validNodeAttributes = new Vector<String>();
	private JComboBox validNodeAttributesComparisonGoldStandardComboBox = new JComboBox();
	
	private JSlider minThresholdForComparison = new JSlider(10,50,20);
	private JSlider maxThresholdForComparison = new JSlider(10,50,20);
	private JSpinner stepSizeForComparison;
	private JButton runComparisonButton;
	
	
	public MCLgui() {
		
		makePanels();
		
	}
	
	private void makePanels() {
		
		fillValidEdgeAttributesComboBox();
		validEdgeAttributesComboBox.addMouseListener(this);
		
		fillValidNodeAttributesComboBox(validNodeAttributesComparisonGoldStandardComboBox);
		validNodeAttributesComparisonGoldStandardComboBox.addMouseListener(this);
		
		
		JPanel attributesPanel = new JPanel();
		attributesPanel.setLayout(new SpringLayout());
		
		attributesPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Attributes"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		attributesPanel.add(new JLabel("Edge weight attribute:"));
		attributesPanel.add(validEdgeAttributesComboBox);
		
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
		
		attributesPanel.add(new JLabel("Node attribute MCL cluster: "));
		attributesPanel.add(nodeClusterAttributeName);
		
		SpringUtilities.makeCompactGrid(attributesPanel, 3, 2, 5, 5, 5, 5);
		
		
		Hashtable<Integer,JLabel> labelTable = new Hashtable<Integer,JLabel>();
		labelTable.put( new Integer( 10 ), new JLabel("1") );
		labelTable.put( new Integer( 20 ), new JLabel("2") );
		labelTable.put( new Integer( 30 ), new JLabel("3") );
		labelTable.put( new Integer( 40 ), new JLabel("4") );
		labelTable.put( new Integer( 50 ), new JLabel("5") );
		
		this.inflationSlider.setLabelTable(labelTable);
		this.inflationSlider.setMajorTickSpacing(10);
		this.inflationSlider.setMinorTickSpacing(1);
		this.inflationSlider.setPaintTicks(true);
		this.inflationSlider.setPaintLabels(true);
//		this.inflationSlider.setMaximumSize(new Dimension(200, 50));
		this.inflationSlider.setSnapToTicks(true);
		
		
		
		
		this.iterationsSlider.setMajorTickSpacing(5);
		this.iterationsSlider.setMinorTickSpacing(1);
		this.iterationsSlider.setPaintTicks(true);
		this.iterationsSlider.setPaintLabels(true);
//		this.iterationsSlider.setMaximumSize(new Dimension(200, 50));
		this.iterationsSlider.setSnapToTicks(true);
		
		
		
		
		this.probabilityThresholdSlider.setMajorTickSpacing(5);
		this.probabilityThresholdSlider.setMinorTickSpacing(1);
		this.probabilityThresholdSlider.setPaintTicks(true);
		this.probabilityThresholdSlider.setPaintLabels(true);
//		this.probabilityThresholdSlider.setMaximumSize(new Dimension(200, 50));
		this.probabilityThresholdSlider.setSnapToTicks(true);
		
		
		
		JPanel dummy2 = new JPanel();
		dummy2.setLayout(new SpringLayout());
		
		dummy2.add(new JLabel("Inflation parameter: "));
		dummy2.add(this.inflationSlider);
		dummy2.add(new JLabel("Iterations: "));
		dummy2.add(this.iterationsSlider);
		dummy2.add(new JLabel("Probability threshold exp. (1e-X): "));
		dummy2.add(this.probabilityThresholdSlider);
		
		SpringUtilities.makeCompactGrid(dummy2, 3, 2, 5, 5, 5, 5);
		
		
		
		
		JPanel mclAttributesPanel = new JPanel();
		mclAttributesPanel.setLayout(new BoxLayout(mclAttributesPanel,BoxLayout.Y_AXIS));
		
		mclAttributesPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("MCL attributes"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		mclAttributesPanel.add(dummy2);
		
		runMCLbutton = new JButton("Run MCL");
		
		
		runMCLbutton.setActionCommand("runMCL");
		
		runMCLbutton.addActionListener(this);
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		
		buttonPanel.add(runMCLbutton);
		
		
		
		JPanel dummy4 = new JPanel();
		dummy4.setLayout(new BoxLayout(dummy4,BoxLayout.Y_AXIS));
		
		JPanel comparisonPanel = new JPanel();
		comparisonPanel.setLayout(new SpringLayout());
	
		
		dummy4.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Determine optimal threshold"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		comparisonPanel.add(new JLabel("Gold standard attribute:"));
		comparisonPanel.add(validNodeAttributesComparisonGoldStandardComboBox);
		
		comparisonPanel.add(new JLabel("Minimal inflation: "));
		
		
		
		this.minThresholdForComparison.setLabelTable(labelTable);
		this.minThresholdForComparison.setMajorTickSpacing(10);
		this.minThresholdForComparison.setMinorTickSpacing(1);
		this.minThresholdForComparison.setPaintTicks(true);
		this.minThresholdForComparison.setPaintLabels(true);
//		this.inflationSlider.setMaximumSize(new Dimension(200, 50));
		this.minThresholdForComparison.setSnapToTicks(true);
		
		comparisonPanel.add(this.minThresholdForComparison);
		
		
		comparisonPanel.add(new JLabel("Maximal inflation: "));
		this.maxThresholdForComparison.setLabelTable(labelTable);
		this.maxThresholdForComparison.setMajorTickSpacing(10);
		this.maxThresholdForComparison.setMinorTickSpacing(1);
		this.maxThresholdForComparison.setPaintTicks(true);
		this.maxThresholdForComparison.setPaintLabels(true);
//		this.inflationSlider.setMaximumSize(new Dimension(200, 50));
		this.maxThresholdForComparison.setSnapToTicks(true);
		
		comparisonPanel.add(this.maxThresholdForComparison);
		
		comparisonPanel.add(new JLabel("Stepsize: "));
		
		Double value = new Double(0.1);
		Double min = new Double(0.1);
		Double max = new Double(1);
		Double step = new Double(0.1);
		SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, step); 
		stepSizeForComparison = new JSpinner(model);
		
		comparisonPanel.add(this.stepSizeForComparison);
		
		SpringUtilities.makeCompactGrid(comparisonPanel, 4, 2, 5, 5, 5, 5);	
		
		JPanel dummy3 = new JPanel();
		
		runComparisonButton = new JButton("Run Comparison");
		
		runComparisonButton.setActionCommand("runComparison");
		
		runComparisonButton.addActionListener(this);
		dummy4.add(comparisonPanel);
		dummy3.add(runComparisonButton);
		dummy4.add(dummy3);
		
		
		
		
				
		// ---------------- INFO
		
		JPanel infoPanel = new JPanel();
		JButton helpButton = new JButton("About/help");
		helpButton.setActionCommand("info");
		helpButton.addActionListener(this);
		
		infoPanel.add(helpButton);
		
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		this.add(attributesPanel);
		this.add(mclAttributesPanel);
		this.add(new JLabel("   "));
		this.add(buttonPanel);
		this.add(new JLabel("   "));
		this.add(dummy4);
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
	
	
	
	public void actionPerformed(ActionEvent e) {
		
		String c = e.getActionCommand();
		
		
		if (c.equalsIgnoreCase("runMCL")) {
			
			if (this.distRadioButton.isSelected()) {
				this.isDistanceFunction = true;
			} else {
				this.isDistanceFunction = false;
			}
			
			RunMCLthread t = new RunMCLthread();
			t.start();
			
		}  else if (c.equalsIgnoreCase("reloadAttributes")) {
			
			fillValidEdgeAttributesComboBox();
			
		}  else if (c.equalsIgnoreCase("info")) {
			
			new CytoscapeMCLinfoFrame();
			
		} else if (c.equalsIgnoreCase("runComparison")) {
			
			if (this.distRadioButton.isSelected()) {
				this.isDistanceFunction = true;
			} else {
				this.isDistanceFunction = false;
			}
			
			this.runComparisonButton.setActionCommand("abortComparison");
			this.runComparisonButton.setText("Stop comparison");
			
			rct = new RunComparisonthread();
			rct.start();
			
			
		} else if(c.equalsIgnoreCase("abortComparison")){
			
			rct.stop();
			this.runComparisonButton.setActionCommand("runComparison");
			this.runComparisonButton.setText("Run Comparison");
			
		}
		
	}
	
	private class RunComparisonthread extends Thread {
		
		public void run() {
			
			if (validNodeAttributesComparisonGoldStandardComboBox.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select a node attribute as cluster ID for the gold standard!");
				runComparisonButton.setActionCommand("runComparison");
				runComparisonButton.setText("Run Comparison");
				return;
			}
			if (validEdgeAttributesComboBox.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge attribute! ");
				runComparisonButton.setActionCommand("runComparison");
				runComparisonButton.setText("Run Comparison");
				return;
			}
			
			runThresholdDetermination();
			runComparisonButton.setActionCommand("runComparison");
			runComparisonButton.setText("Run Comparison");
			
		}
		
	}
	
	private void runMCL(){
		
		buildNodesAndEdgesList();
		
		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		
		int edgeAttrIndex = validEdgeAttributesComboBox.getSelectedIndex();
		if (edgeAttrIndex == -1 ) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge attribute! ");
			return;
		}
		String attrName = validEdgeAttributes.get(edgeAttrIndex);
		byte type = cyEdgeAttributes.getType(attrName);
		
		for (Iterator<CyEdge> i = edges.iterator(); i.hasNext();) {
			
			CyEdge e = i.next();
			
			String sourceID = e.getSource().getIdentifier();
			String targetID = e.getTarget().getIdentifier();
			
			// THIS IS A STRANGE BUG IN CYTOSCAPE; it always give u 2 additional nodes :-(
			if (!sourceID.equalsIgnoreCase("Source") && !sourceID.equalsIgnoreCase("Target") && !targetID.equalsIgnoreCase("Source") && !targetID.equalsIgnoreCase("Target")) {
				
				double sim = 0;
				if (type == CyAttributes.TYPE_INTEGER) {
					try {
						sim = (double) cyEdgeAttributes.getIntegerAttribute(e.getIdentifier(), attrName);
					} catch(NullPointerException e1) {
						continue;
					}
					
				} else {
					try {
						sim = cyEdgeAttributes.getDoubleAttribute(e.getIdentifier(), attrName);
					} catch(NullPointerException e1) {
						continue;
					}
					
				}
				
				similaritiesForGivenEdges.put((sourceID + "#" + targetID), sim);
				
				if (sim < this.minSim) {
					this.minSim = sim;
				}
				if (sim > this.maxSim) {
					this.maxSim = sim;
				}
				
			}
		}
		
		for (Iterator<String> iterator = similaritiesForGivenEdges.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			
			double sd = similaritiesForGivenEdges.get(key);
			double s;
			if (!this.isDistanceFunction) { // is sim function
				s = this.getNormalizedValue(this.minSim, this.maxSim, sd);
			} else {
				s = 100-this.getNormalizedValue(this.minSim, this.maxSim, sd);
			}
			
			normalizedSimilaritiesForGivenEdges.put(key, s);
			
		}
		
		double inflationParameter = ((double)this.inflationSlider.getValue())/10;
		int number_iterations = this.iterationsSlider.getValue();
		double clusteringThresh = Math.pow(10, this.probabilityThresholdSlider.getValue());
		
		RunMCL rmcl = new RunMCL(this.nodeClusterAttributeName.getText(), this.nodes, this.normalizedSimilaritiesForGivenEdges,inflationParameter, number_iterations,  clusteringThresh);
		rmcl.run();
		
	}

	
	
	private CyAttributes cyNodeAttributes;
	private CyAttributes cyEdgeAttributes;

	private List<CyNode> nodes;
	private List<CyEdge> edges;
	
	private Hashtable<String, Double> similaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	private Hashtable<String, Double> normalizedSimilaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	
	private double maxSim = Double.NEGATIVE_INFINITY;
	private double minSim = Double.POSITIVE_INFINITY;
	private boolean isDistanceFunction = false;
;
	
	private double getNormalizedValue(double min, double max, double value) {
		double span = max-min;
		return ((value-min)/span)*100;
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
	
	private class RunMCLthread extends Thread {
		
		public void run() {
			runMCL();
		}
		
	}
	
	private void runThresholdDetermination(){
		
		int dummy = this.inflationSlider.getValue();
		double minThreshold = ((double) this.minThresholdForComparison.getValue())/10;
	
		double maxThreshold = ((double) this.maxThresholdForComparison.getValue())/10;
		double stepsize = (Double) this.stepSizeForComparison.getValue();
		double threshold = minThreshold;
		
		System.out.println("minThreshold = " + minThreshold + " maxThreshold = " + maxThreshold + " stepsize = " + stepsize);
		this.inflationSlider.setValue((int)Math.rint(minThreshold*10));
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Threshold");
		columnNames.add("Recall");
		columnNames.add("Precision");
		columnNames.add("F-measure I");
		columnNames.add("F-measure II");
		
		ComparisonResultsTable crt = new ComparisonResultsTable(columnNames);
		
		while(threshold<=maxThreshold){
			this.inflationSlider.setValue((int)Math.rint(threshold*10));
			
			System.out.println(threshold + ", " + ((double) this.inflationSlider.getValue()));
			
			runMCL();
			ComparisonResults cr = runComparison();
			if (cr == null) {
				return;
			}
			Vector<String> v = cr.toVector(Double.toString(threshold));
			crt.addRow(v);
			
			
			threshold = threshold + stepsize;
			
		}
		
		crt.initializeTable();
		
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
        CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);
		
		if (this.resultsPanel != null) {
				
			cytoPanel.remove(this.resultsPanel);
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
            	cytoPanel.setState(CytoPanelState.HIDE);
            }
            
		}
		
		this.resultsPanel = crt;
		
		cytoPanel.add("Comparison results", this.resultsPanel);
        cytoPanel.setState(CytoPanelState.DOCK);
		
        this.resultsPanel.updateUI();
		
		this.inflationSlider.setValue(dummy);
		
	}
	
	
	
	private ComparisonResults runComparison() {
		
		buildNodesAndEdgesList();
		CyAttributes cyNodeAttributes = Cytoscape.getNodeAttributes();
		
		int nodeGoldStandardAttrIndex = this.validNodeAttributesComparisonGoldStandardComboBox.getSelectedIndex();
		String attrNameCluster = this.nodeClusterAttributeName.getText();
		
		if (nodeGoldStandardAttrIndex != -1 ) {
			
			String attrNameGoldStandard = validNodeAttributes.get(nodeGoldStandardAttrIndex);
			
			
			
			Hashtable<String,Hashtable<String, CyNode>> goldStandard = new Hashtable<String,Hashtable<String, CyNode>>();
			Hashtable<String,Hashtable<String, CyNode>> compareCluster = new Hashtable<String,Hashtable<String, CyNode>>();
			
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
			
			double recall = calculateRecall(goldStandard,compareCluster);
			
			
			//precision = tp/tp+fp
			double precision = calculatePrecision(goldStandard,compareCluster);
			
			double fMeasure = calculateFmeasure(goldStandard,compareCluster);
			
			double fMeasureUsingSensitivitySpecifity = (2*recall*precision)/(recall+precision);
			
			ComparisonResults cr = new ComparisonResults(recall,precision,fMeasureUsingSensitivitySpecifity,fMeasure);
			
			return cr;
			
		} else {
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select a node attribute as cluster ID for the gold standard!");
			
			return null;
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
	
	
	private class ComparisonResults{
		
		private double recall,precision,fm1,fm2;
		
		
		public ComparisonResults(double recall, double precision, double fm1, double fm2){
			this.recall = recall;
			this.precision = precision;
			this.fm1 = fm1;
			this.fm2 = fm2;
		}
		
		public Vector<String> toVector(String firstEntry){
			Vector<String> v = new Vector<String>();
			
			v.add(firstEntry);
			v.add(Double.toString(recall));
			v.add(Double.toString(precision));
			v.add(Double.toString(fm1));
			v.add(Double.toString(fm2));
			
			return v;
		}
		
	}
	
	
	private class ComparisonResultsTable extends JPanel implements ActionListener{
		
		Vector<String> columnNames = new Vector<String>();
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		JTable t;
		public ComparisonResultsTable(Vector<String> columnNames) {
			this.columnNames = columnNames;
		}
			
		private void addRow(Vector<String> row){
			data.add(row);
		}
		
		private void initializeTable(){	
						
						
			t = new JTable(data, columnNames);
			// t.setAutoCreateRowSorter(true);
			
			
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
	
	
	
	
	
}











































