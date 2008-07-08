package clusterMaker.algorithms.FORCE;

import giny.model.Node;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
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
import de.layclust.layout.LayoutFactory;
import de.layclust.layout.data.ConnectedComponent;
import de.layclust.layout.data.ICCEdges;
import de.layclust.layout.forcend.FORCEnDLayoutConfig;
import de.layclust.taskmanaging.ClusteringManager;
import de.layclust.taskmanaging.InvalidInputFileException;
import de.layclust.taskmanaging.InvalidTypeException;
import de.layclust.taskmanaging.TaskConfig;


public class CytoscapeFORCEgui extends JPanel implements ActionListener, MouseListener {
	
	private JPanel resultsPanel;
	
	private JTextField thresholdTextField = new JTextField("10");
	private JButton runFORCEbutton;
	
	private JTextField tempDirTextField = new JTextField("");
	private JButton tmpDirButton = new JButton("Assign TMP dir");
	
	private Vector<String> validEdgeAttributes = new Vector<String>();
	private JComboBox validEdgeAttributesComboBox = new JComboBox();
	
	private JRadioButton simRadioButton = new JRadioButton("Similarity");
	private JRadioButton distRadioButton = new JRadioButton("Distance");
	
	private JPanel advanvedOptionsPanel = new JPanel();
	private JPanel advanvedOptionsPanelMemory = new JPanel();
	
	private JSlider dimensionSlider = new JSlider(2,6,3);
	
	private JSlider iterationsSlider = new JSlider(50,250,FORCEnDLayoutConfig.iterations);
	
	private JCheckBox trainingCheckBox = new JCheckBox("Evolutionary paramameter training?", TaskConfig.doLayoutParameterTraining);
	
	private JSlider generationSlider = new JSlider(1,10,TaskConfig.noOfGenerations);
	
	private JTextField attractionTextField = new JTextField("" +FORCEnDLayoutConfig.attractionFactor);
	
	private JTextField repulsionTextField = new JTextField("" +FORCEnDLayoutConfig.repulsionFactor);
	
	private JTextField attributeNameCC = new JTextField("FORCE_connected_component");
	private JTextField attributeNameCluster = new JTextField("FORCE_cluster");
	
	private JCheckBox mergeNodesCheckBox = new JCheckBox("Merge very similar nodes to one?", false);
	private JTextField mergeNodesThresholdTextField = new JTextField("100");
	
	private Vector<String> validNodeAttributes = new Vector<String>();
	private JComboBox validNodeAttributesComparisonGoldStandardComboBox = new JComboBox();
	
	private JTextField minThresholdForComparison = new JTextField(this.thresholdTextField.getText());
	private JTextField maxThresholdForComparison = new JTextField(this.thresholdTextField.getText());
	private JTextField stepSizeForComparison = new JTextField("0.5");
	private JButton runComparisonButton;
	
	private String groupAttributeCC = "__FORCEccGroups";
	private String groupAttribute = "__FORCEGroups";
	
	public CytoscapeFORCEgui() {
		
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
		
		
		
		attributesPanel.add(new JLabel("Threshold: "));
		attributesPanel.add(thresholdTextField);
		
		
		
		String dummy = System.getProperty("java.io.tmpdir");
		dummy = dummy.replaceAll("\\\\", "/");
		
		tempDirTextField.setEditable(false);
		tempDirTextField.setColumns(15);
		tempDirTextField.setText(dummy.substring(0, dummy.length()-1));
		
		tmpDirButton.setActionCommand("setTempDir");
		tmpDirButton.addActionListener(this);
		
		attributesPanel.add(tmpDirButton);
		attributesPanel.add(tempDirTextField);
		
		
		
		SpringUtilities.makeCompactGrid(attributesPanel, 4, 2, 5, 5, 5, 5);		
		
		
		
		
		JButton runCCbutton = new JButton("Assign connected components");
		runFORCEbutton = new JButton("Run FORCE");
		
		runCCbutton.setActionCommand("runCC");
		runFORCEbutton.setActionCommand("runFORCE");
		
		runCCbutton.addActionListener(this);
		runFORCEbutton.addActionListener(this);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel,BoxLayout.X_AXIS));
		
		buttonPanel.add(runCCbutton);
		buttonPanel.add(runFORCEbutton);
		
		
		makeAdvancedOptionsPanel();
		
		// ---- comparison panel
		
		JPanel dummy2 = new JPanel();
		dummy2.setLayout(new BoxLayout(dummy2,BoxLayout.Y_AXIS));
		
		JPanel comparisonPanel = new JPanel();
		comparisonPanel.setLayout(new SpringLayout());
	
		
		dummy2.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Determine optimal threshold"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		comparisonPanel.add(new JLabel("Gold standard attribute:"));
		comparisonPanel.add(validNodeAttributesComparisonGoldStandardComboBox);
		
		comparisonPanel.add(new JLabel("Minimal threshold: "));
		comparisonPanel.add(this.minThresholdForComparison);
		
		comparisonPanel.add(new JLabel("Maximal threshold: "));
		comparisonPanel.add(this.maxThresholdForComparison);
		
		comparisonPanel.add(new JLabel("Stepsize: "));
		comparisonPanel.add(this.stepSizeForComparison);
		
		SpringUtilities.makeCompactGrid(comparisonPanel, 4, 2, 5, 5, 5, 5);	
		
		JPanel dummy3 = new JPanel();
		
		runComparisonButton = new JButton("Run Comparison");
		
		runComparisonButton.setActionCommand("runComparison");
		
		runComparisonButton.addActionListener(this);
		dummy2.add(comparisonPanel);
		dummy3.add(runComparisonButton);
		dummy2.add(dummy3);
			
		
		
		
		
		
		
		// ---------------- INFO
		
		JPanel infoPanel = new JPanel();
		JButton helpButton = new JButton("About/help");
		helpButton.setActionCommand("info");
		helpButton.addActionListener(this);
		
		infoPanel.add(helpButton);
		
		
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		this.add(attributesPanel);
		this.add(new JLabel("   "));
		this.add(buttonPanel);
		this.add(new JLabel("   "));
		this.add(this.advanvedOptionsPanel);
		this.add(new JLabel("   "));
		this.add(dummy2);
		this.add(new JLabel("   "));
		this.add(infoPanel);
		
	}
	
	
	private void makeAdvancedOptionsPanel() {
		
		this.advanvedOptionsPanel.setLayout(new BoxLayout(this.advanvedOptionsPanel,BoxLayout.Y_AXIS));
		
		this.advanvedOptionsPanel.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Advanced attributes"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		hideAdvancedOptions();
		makeAdvancedOptionsPanelMemory();
		
	}
	
	private void showAdvancedOptions() {
		
		this.advanvedOptionsPanel.removeAll();
		
		this.advanvedOptionsPanel.add(this.advanvedOptionsPanelMemory);
		
		this.advanvedOptionsPanel.add(new JLabel("   "));
		
		JButton hideButton = new JButton("Hide advanced options");
		hideButton.setActionCommand("hideAdvancedOptions");
		hideButton.addActionListener(this);
		
		JPanel dummy = new JPanel();
		dummy.add(hideButton);
		
		this.advanvedOptionsPanel.add(dummy);
		this.advanvedOptionsPanel.updateUI();
		this.advanvedOptionsPanel.paintImmediately(this.advanvedOptionsPanel.getBounds());
	}
	
	private void hideAdvancedOptions() {
		
		this.advanvedOptionsPanel.removeAll();
		
		JButton showButton = new JButton("Show advanced options");
		showButton.setActionCommand("showAdvancedOptions");
		showButton.addActionListener(this);
		
		JPanel dummy = new JPanel();
		dummy.add(showButton);
		
		this.advanvedOptionsPanel.add(dummy);
		this.advanvedOptionsPanel.updateUI();
		this.advanvedOptionsPanel.paintImmediately(this.advanvedOptionsPanel.getBounds());
	}
	
	private void makeAdvancedOptionsPanelMemory(){
		
		this.trainingCheckBox.addActionListener(this);
		this.trainingCheckBox.setActionCommand("trainingCheckBox");
		this.generationSlider.setEnabled(false);
		
		this.mergeNodesCheckBox.addActionListener(this);
		this.mergeNodesCheckBox.setActionCommand("mergeNodesCheckBox");
		this.mergeNodesThresholdTextField.setEnabled(false);
		
		this.advanvedOptionsPanelMemory.setLayout(new BoxLayout(this.advanvedOptionsPanelMemory, BoxLayout.Y_AXIS));
		
		// dimension
		
		JPanel dummy1 = new JPanel();
		
		dummy1.setLayout(new BoxLayout(dummy1,BoxLayout.X_AXIS));		

		this.dimensionSlider.setMajorTickSpacing(1);
		this.dimensionSlider.setPaintTicks(true);
		this.dimensionSlider.setPaintLabels(true);
		this.dimensionSlider.setMaximumSize(new Dimension(300, 50));
		this.dimensionSlider.setSnapToTicks(true);
		
		dummy1.add(new JLabel("Dimension: "));
		dummy1.add(this.dimensionSlider);
		
		// iterations
		
		JPanel dummy2 = new JPanel();
		
		dummy2.setLayout(new BoxLayout(dummy2,BoxLayout.X_AXIS));		

		this.iterationsSlider.setMajorTickSpacing(50);
		this.iterationsSlider.setMinorTickSpacing(10);
		this.iterationsSlider.setPaintTicks(true);
		this.iterationsSlider.setPaintLabels(true);
		this.iterationsSlider.setMaximumSize(new Dimension(300, 50));
		this.iterationsSlider.setSnapToTicks(true);
		
		dummy2.add(new JLabel("Iterations: "));
		dummy2.add(this.iterationsSlider);
		
		
		// attraction/repulsion factor
		
		JPanel dummy5 = new JPanel();
				
		dummy5.setLayout(new BoxLayout(dummy5,BoxLayout.X_AXIS));	
		
		dummy5.add(new JLabel("Attraction factor: "));
		dummy5.add(this.attractionTextField);
		
		
		JPanel dummy6 = new JPanel();
		
		dummy6.setLayout(new BoxLayout(dummy6,BoxLayout.X_AXIS));	
		
		dummy6.add(new JLabel("Repulsion factor: "));
		dummy6.add(this.repulsionTextField);
		
		
		// all layout things:
		
		JPanel dummy11 = new JPanel();
		dummy11.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Layouter options"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		dummy11.setLayout(new BoxLayout(dummy11,BoxLayout.Y_AXIS));
		
		dummy11.add(dummy1);
		dummy11.add(dummy2);
		dummy11.add(dummy5);
		dummy11.add(dummy6);
		
		
		// merge nodes
		
		JPanel dummy7 = new JPanel();
		
		dummy7.setLayout(new BoxLayout(dummy7,BoxLayout.X_AXIS));
		dummy7.add(new JLabel("Threshold: "));
		dummy7.add(this.mergeNodesThresholdTextField);
		
		JPanel dummy8 = new JPanel();
		dummy8.add(this.mergeNodesCheckBox);
		
		JPanel dummy9 = new JPanel();
		dummy9.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Merge nodes"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		dummy9.setLayout(new BoxLayout(dummy9,BoxLayout.Y_AXIS));
		
		dummy9.add(dummy8);
		dummy9.add(dummy7);
		
		// training
		
		JPanel dummy3 = new JPanel();
		dummy3.add(this.trainingCheckBox);

		JPanel dummy4 = new JPanel();
		
		dummy4.setLayout(new BoxLayout(dummy4,BoxLayout.X_AXIS));		

		this.generationSlider.setMajorTickSpacing(1);
		this.generationSlider.setPaintTicks(true);
		this.generationSlider.setPaintLabels(true);
		this.generationSlider.setMaximumSize(new Dimension(300, 50));
		this.generationSlider.setSnapToTicks(true);
		
		dummy4.add(new JLabel("Generations: "));
		dummy4.add(this.generationSlider);
		
		JPanel dummy10 = new JPanel();
		dummy10.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Parameter training"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		dummy10.setLayout(new BoxLayout(dummy10,BoxLayout.Y_AXIS));
		
		dummy10.add(dummy3);
		dummy10.add(dummy4);
		
		
		// attribute Names
		
		JPanel dummy12 = new JPanel();
		dummy12.setLayout(new BoxLayout(dummy12,BoxLayout.X_AXIS));
		dummy12.add(new JLabel("Node attribute connected component: "));
		dummy12.add(this.attributeNameCC);
		
		JPanel dummy13 = new JPanel();
		dummy13.setLayout(new BoxLayout(dummy13,BoxLayout.X_AXIS));
		dummy13.add(new JLabel("Node attribute FORCE cluster: "));
		dummy13.add(this.attributeNameCluster);
		
		JPanel dummy14 = new JPanel();
		dummy14.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Cytoscape"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
		
		dummy14.setLayout(new BoxLayout(dummy14,BoxLayout.Y_AXIS));
		dummy14.add(dummy12);
		dummy14.add(dummy13);
		
		
		// MAIN
		
		
		this.advanvedOptionsPanelMemory.add(dummy11);
		this.advanvedOptionsPanelMemory.add(dummy9);
		this.advanvedOptionsPanelMemory.add(dummy10);
		this.advanvedOptionsPanelMemory.add(dummy14);
		
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
		
		if (c.equalsIgnoreCase("runCC")) {
			
			if (this.distRadioButton.isSelected()) {
				this.isDistanceFunction = true;
			} else {
				this.isDistanceFunction = false;
			}
			
			RunCCthread t = new RunCCthread();
			t.start();
			
		} else if (c.equalsIgnoreCase("runFORCE")) {
			
			if (this.distRadioButton.isSelected()) {
				this.isDistanceFunction = true;
			} else {
				this.isDistanceFunction = false;
			}
			
			RunFORCEthread t = new RunFORCEthread();
			t.start();
			
		} else if (c.equalsIgnoreCase("setTempDir")) {
			
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			try {
				fc.showOpenDialog(Cytoscape.getDesktop());
				String dummy = fc.getSelectedFile().getAbsolutePath();
				dummy = dummy.replaceAll("\\\\", "/");
				this.tempDirTextField.setText(dummy);
			} catch (NullPointerException ex) {
				
			}
			
		} else if (c.equalsIgnoreCase("reloadAttributes")) {
			
			fillValidEdgeAttributesComboBox();
			
		} else if (c.equalsIgnoreCase("showAdvancedOptions")) {
			
			showAdvancedOptions();
			
		} else if (c.equalsIgnoreCase("hideAdvancedOptions")) {
			
			hideAdvancedOptions();
			
		} else if (c.equalsIgnoreCase("info")) {
			
			new CytoscapeFORCEinfoFrame();
			
		} else if (c.equalsIgnoreCase("mergeNodesCheckBox")) {
			
			if (this.mergeNodesCheckBox.isSelected()) {
				this.mergeNodesThresholdTextField.setEnabled(true);
			} else {
				this.mergeNodesThresholdTextField.setEnabled(false);
			}
			
		} else if (c.equalsIgnoreCase("trainingCheckBox")) {
			
			if (this.trainingCheckBox.isSelected()) {
				this.generationSlider.setEnabled(true);
			} else {
				this.generationSlider.setEnabled(false);
			}
			
		}else if (c.equalsIgnoreCase("runComparison")) {
			
			if (this.distRadioButton.isSelected()) {
				this.isDistanceFunction = true;
			} else {
				this.isDistanceFunction = false;
			}
			this.runComparisonButton.setActionCommand("abortComparison");
			this.runComparisonButton.setText("Stop comparison");
			
			rct = new RunComparisonthread();
			rct.start();

		}else if(c.equalsIgnoreCase("abortComparison")){
			
			rct.stop();
			this.runComparisonButton.setActionCommand("runComparison");
			this.runComparisonButton.setText("Run Comparison");
			
		}
		
	}
	
	private RunComparisonthread rct;
	
	
	private class RunCCthread extends Thread {
		
		public void run() {
			runCC();
		}
		
	}
	
	private class RunFORCEthread extends Thread {
		
		public void run() {
			try {
				boolean suc = runCC();
				if(suc) runFORCE();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - " + e1.getMessage());
			}
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
	
	private void runFORCE() throws IOException {
		
		
		
		cyNodeAttributes.deleteAttribute(this.attributeNameCluster.getText());
		
		threshold = Double.parseDouble(this.thresholdTextField.getText());
		
		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		

		
		Vector<Vector<CyNode>> connectedComponents = new Vector<Vector<CyNode>>();
		//Hashtable<String, Integer> ccNrForGivenCCName = Hashtable<String, Integer>();
		
		for (Iterator<CyNode> i = nodes.iterator(); i.hasNext();) {
			
			CyNode n = i.next();
			
			int ccNr = cyNodeAttributes.getIntegerAttribute(n.getIdentifier(), this.attributeNameCC.getText());
			
			try {
				connectedComponents.get(ccNr).add(n);
			} catch (Exception e) {
				Vector<CyNode> v = new Vector<CyNode>();
				v.add(n);
				connectedComponents.add(ccNr, v);
			}
			
			
		
		}
		
		Console.startNewConsoleWindow(0,connectedComponents.size()*2,"FORCE");
		
		Date date = new Date(System.currentTimeMillis());
		String dateTimeStr = date.toString().replaceAll(" ", "_");
		dateTimeStr = dateTimeStr.replaceAll(":", "-");
		
		String cmSubTempDirName = "cm_" + dateTimeStr;
		String cmTempDir = this.tempDirTextField.getText() + "/" + cmSubTempDirName;
		
		boolean suc = (new File(cmTempDir)).mkdir();
		if (!suc) {
			throw new IOException("Can't write to temp directory.");
		}
		
		for (int i = 0; i < connectedComponents.size(); i++) {
			
			Console.setBarValue(i);
			Console.setBarText("Writing temp file nr. " + i + " of " + connectedComponents.size());
			
			Vector<CyNode> cc = connectedComponents.get(i);
			
			if(this.mergeNodesCheckBox.isSelected()){
				writeCCtoTempDirWithMergedNodes(cmTempDir, cc, i);
			}else{
				writeCCtoTempDir(cmTempDir, cc, i);
			}
			
			
		}
		
		Console.setBarText("Running FORCE clustering (might take a while)...");
		
		String resultsFileName = cmTempDir + "_results.txt";
		

		if(this.trainingCheckBox.isSelected()){
			String[] args = {"-i",cmTempDir,"-o",resultsFileName,"-cf","FALSE","-ld","" + this.dimensionSlider.getValue(),"-fi","" + this.iterationsSlider.getValue(),"-lp",TaskConfig.parameterTrainingClass,"-lpn",""+this.generationSlider.getValue(),"-fa", this.attractionTextField.getText(),"-fr",this.repulsionTextField.getText()};
			FORCEnD_ACC.main(args);
			TaskConfig.doLayoutParameterTraining = false;
		}else{
			String[] args = {"-i",cmTempDir,"-o",resultsFileName,"-cf","FALSE","-ld","" + this.dimensionSlider.getValue(),"-fi","" + this.iterationsSlider.getValue(),"-fa", this.attractionTextField.getText(),"-fr",this.repulsionTextField.getText()};
			FORCEnD_ACC.main(args);
		}
		
		
		readFORCEresults(resultsFileName);
		
		deleteDirectory(new File(cmTempDir));
		new File(resultsFileName).delete();
		
		Console.closeWindow();
	}
	
	private void writeCCtoTempDirWithMergedNodes(String cmTempDir, Vector<CyNode> cc, int i) throws IOException {
		
		double normalizedUpperBound = 0;
		
		if (!this.isDistanceFunction) { // is sim function
			normalizedUpperBound = this.getNormalizedValue(this.minSim, this.maxSim, Double.parseDouble(this.mergeNodesThresholdTextField.getText()));
		} else {
			normalizedUpperBound = 100-this.getNormalizedValue(this.minSim, this.maxSim, Double.parseDouble(this.mergeNodesThresholdTextField.getText()));
		}
		
		// find connected components with upperBound to merge them to one node
		Vector<Vector<CyNode>> upperBoundMergedNodes = new Vector<Vector<CyNode>>();
		
		Hashtable<String, Boolean> already = new Hashtable<String, Boolean>();
		
		for (int j = 0; j < cc.size(); j++) {
			
			CyNode n = cc.get(j);
			
			if(!already.containsKey(n.getIdentifier())){
			
				Vector<CyNode> v = new Vector<CyNode>();
				
				findMergeNodes(n,cc,v,already,normalizedUpperBound);
				upperBoundMergedNodes.add(v);
				
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter((cmTempDir + "/costmatrix_nr_" + i + "_size_" + cc.size() + ".rcm")));
		
		bw.write("0");
		bw.newLine();
		bw.write("" + upperBoundMergedNodes.size());
		bw.newLine();
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			Vector<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = 0; k < v.size()-1; k++) {
				bw.write(v.get(k).getIdentifier() + "\t");
			}
			bw.write(v.get(v.size()-1).getIdentifier());
			bw.newLine();
		}
		for (int j = 0; j < upperBoundMergedNodes.size(); j++) {
			Vector<CyNode> v = upperBoundMergedNodes.get(j);
			for (int k = j+1; k < upperBoundMergedNodes.size(); k++) {			
				Vector<CyNode> v2 = upperBoundMergedNodes.get(k);
				double sim = calculateSimilarityForMergeNodes(v,v2);
				bw.write(Double.toString(sim));
				if(k<upperBoundMergedNodes.size()-1){
					bw.write("\t");
				}else if(j<upperBoundMergedNodes.size()-1){
					bw.write("\n");
				}
			}	
		}
		
		bw.close();
		
		
		
		
	}

	private double calculateSimilarityForMergeNodes(Vector<CyNode> v, Vector<CyNode> v2) {
		
		double value = 0;
		
		for (int i = 0; i < v.size(); i++) {
			CyNode n = v.get(i);
			for (int j = 0; j < v2.size(); j++) {
				CyNode n2 = v2.get(j);
				String key = n.getIdentifier() + "#" + n2.getIdentifier();
				String keyI = n2.getIdentifier() + "#" + n.getIdentifier();
				if(this.normalizedSimilaritiesForGivenEdges.containsKey(key)){
					value+=(this.normalizedSimilaritiesForGivenEdges.get(key)-this.threshold);
				}else if(this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)){
					value+=(this.normalizedSimilaritiesForGivenEdges.get(keyI)-this.threshold);
				}
			}
		}
		
		
		return value;
	}

	private void findMergeNodes(CyNode n, Vector<CyNode> cc, Vector<CyNode> v, Hashtable<String, Boolean> already, double normalizedUpperBound) {
		
		v.add(n);
		already.put(n.getIdentifier(), true);
		
		for (int i = 0; i < cc.size(); i++) {
			
			CyNode n2 = cc.get(i);
			
			String key = n.getIdentifier() + "#" + n2.getIdentifier();
			String keyI = n2.getIdentifier() + "#" + n.getIdentifier();
			
			if(!already.containsKey(n2.getIdentifier())){
				
				if(this.normalizedSimilaritiesForGivenEdges.containsKey(key)){
					if(this.normalizedSimilaritiesForGivenEdges.get(key)<normalizedUpperBound){
						findMergeNodes(n2, cc, v, already, normalizedUpperBound);
					}
				}else if(this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)){
					if(this.normalizedSimilaritiesForGivenEdges.get(keyI)<normalizedUpperBound){
						findMergeNodes(n2, cc, v, already, normalizedUpperBound);
					}
				}
				
			}
			
		}
		
	}

	private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}
	
	private void readFORCEresults(String resultsFileName) throws IOException {
		
		Hashtable<String, Integer> clusterForGivenNode = new Hashtable<String, Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(resultsFileName));
		
		String line;
		while ((line=br.readLine()) != null) {
			
			String[] d = line.split("\t");
			
			clusterForGivenNode.put(d[0].trim(), Integer.parseInt(d[1].trim()));
			
		}
		
		br.close();
		
		Hashtable<Integer, Vector<CyNode>> nodeListForGivenClusterNumber = new Hashtable<Integer, Vector<CyNode>>();
		
		for (Iterator<CyNode> i = nodes.iterator(); i.hasNext();) {
			
			CyNode n = i.next();
			
			int clusterNr =  clusterForGivenNode.get(n.getIdentifier());
			cyNodeAttributes.setAttribute(n.getIdentifier(), this.attributeNameCluster.getText(), clusterNr);
			
			Vector<CyNode> v = new Vector<CyNode>();
			if (nodeListForGivenClusterNumber.containsKey(clusterNr)) {
				v = nodeListForGivenClusterNumber.get(clusterNr);
			}
			v.add(n);
			nodeListForGivenClusterNumber.put(clusterNr, v);
		}
		
		
		
		
		
		// See if we already have groups defined (from a previous run?)
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		String networkID = Cytoscape.getCurrentNetwork().getIdentifier();
		if (netAttributes.hasAttribute(networkID, this.groupAttribute)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, this.groupAttribute);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}
		
		
		
		// Now, create the groups
		List<String>groupList = new ArrayList<String>();
		for (Integer clusterNumber: nodeListForGivenClusterNumber.keySet()) {
			String groupName = this.attributeNameCluster.getText()+"_"+clusterNumber;
			List<CyNode>nodeList = nodeListForGivenClusterNumber.get(clusterNumber);
			// Create the group
			CyGroup newgroup = CyGroupManager.createGroup(groupName, nodeList, null);
			if (newgroup != null) {
				// Now tell the metanode viewer about it
				CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), true);
				groupList.add(groupName);
			}
		}

		// Save the network attribute so we remember which groups are ours
		netAttributes.setListAttribute(networkID, this.groupAttribute, groupList);
		
		
	}

	private void writeCCtoTempDir(String cmTempDir, Vector<CyNode> cc, int ccNr) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter((cmTempDir + "/costmatrix_nr_" + ccNr + "_size_" + cc.size() + ".cm")));
		
		bw.write("" + cc.size());
		bw.newLine();
		
		for (int i = 0; i < cc.size(); i++) {
			CyNode n = cc.get(i);
			bw.write(n.getIdentifier());
			bw.newLine();
		}
		
		for (int i = 0; i < cc.size(); i++) {
			String s = cc.get(i).getIdentifier();
			for (int j = i+1; j < cc.size(); j++) {
				String t = cc.get(j).getIdentifier();
				
				double cost = -this.normalizedThreshold;
				if (this.normalizedSimilaritiesForGivenEdges.containsKey(s + "#" + t)) {
					cost = this.normalizedSimilaritiesForGivenEdges.get(s + "#" + t) - this.normalizedThreshold;
				} else if (this.normalizedSimilaritiesForGivenEdges.containsKey(t + "#" + s)) {
					cost = this.normalizedSimilaritiesForGivenEdges.get(t + "#" + s) - this.normalizedThreshold;
				}
				
				if (j != cc.size()-1) {
					bw.write(cost + "\t");
				} else {
					bw.write("" + cost);
				}
			}
			
			if (i != cc.size()-1) {
				bw.newLine();
			}
			
		}
		
		bw.close();
		
	}
	
	
	private CyAttributes cyNodeAttributes;
	private CyAttributes cyEdgeAttributes;
	private double threshold;
	private List<CyNode> nodes;
	private List<CyEdge> edges;
	
	private Hashtable<String, Double> similaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	private Hashtable<String, Double> normalizedSimilaritiesForGivenEdges = new Hashtable<String, Double>(); // key: s#t
	
	private double maxSim = Double.NEGATIVE_INFINITY;
	private double minSim = Double.POSITIVE_INFINITY;
	private boolean isDistanceFunction = false;
	private double normalizedThreshold = 0;
	
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
	
	private boolean runCC() {
		
		buildNodesAndEdgesList();
		
		this.threshold = Double.parseDouble(this.thresholdTextField.getText());
		
		cyNodeAttributes = Cytoscape.getNodeAttributes();
		cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		
		int step = 0;
		int max = this.edges.size()*2 + this.nodes.size();
		Console.startNewConsoleWindow(0,max,"Assigning connected components...");
		Console.setBarValue(step);
		
		int edgeAttrIndex = validEdgeAttributesComboBox.getSelectedIndex();
		if (edgeAttrIndex == -1 ) {
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select an edge attribute! ");
			return false;
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
			
			step++;
			if ((step%10) == 0) Console.setBarValue(step);
		}
		
		if (!this.isDistanceFunction) { // is sim function
			this.normalizedThreshold = this.getNormalizedValue(this.minSim, this.maxSim, this.threshold);
		} else {
			this.normalizedThreshold = 100-this.getNormalizedValue(this.minSim, this.maxSim, this.threshold);
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
			
			step++;
			if ((step%10) == 0) Console.setBarValue(step);
		}
				
		Hashtable<String, Boolean> already = new Hashtable<String, Boolean>();
		
		int clusterNr = 0;
		
		// TODO: metanode stuff
		
		String networkID = Cytoscape.getCurrentNetwork().getIdentifier();
		CyAttributes netAttributes = Cytoscape.getNetworkAttributes();
		if (netAttributes.hasAttribute(networkID, groupAttributeCC)) {
			List<String> groupList = (List<String>)netAttributes.getListAttribute(networkID, groupAttributeCC);
			for (String groupName: groupList) {
				CyGroup group = CyGroupManager.findGroup(groupName);
				if (group != null)
					CyGroupManager.removeGroup(group);
			}
		}
		
		List<String>groupList = new ArrayList<String>();
		for (Iterator<CyNode> i = nodes.iterator(); i.hasNext();) {
			
			CyNode node = i.next();
			String id = node.getIdentifier();
			
			if (!already.containsKey(id)) {
				
				Vector<CyNode> nodesInThisCluster = new Vector<CyNode>();
				assingNodeToCluster(already, node, id, clusterNr, nodesInThisCluster);
				clusterNr++;
				
				String groupName = this.attributeNameCC.getText() +"_" + clusterNr;
				
				CyGroup newgroup = CyGroupManager.createGroup(groupName, nodesInThisCluster, null);
				if (newgroup != null) {
					CyGroupManager.setGroupViewer(newgroup, "metaNode", Cytoscape.getCurrentNetworkView(), true);
					groupList.add(groupName);
				}
				
			}
			
			step++;
			if ((step%10) == 0) Console.setBarValue(step);
		}
		
		netAttributes.setListAttribute(networkID, groupAttributeCC, groupList);
		
		Console.closeWindow();
		return true;
	}
	
	private void assingNodeToCluster(Hashtable<String, Boolean> already, CyNode node, String id, int clusterNr, Vector<CyNode> nodesInThisCluster) {
		
		already.put(id, true);
		
		cyNodeAttributes.setAttribute(id, this.attributeNameCC.getText(), clusterNr);
		
		for (Iterator<CyNode> i = nodes.iterator(); i.hasNext();) {
			
			CyNode node2 = i.next();
			String id2 = node2.getIdentifier();
			
			if (!id.equalsIgnoreCase("Source") && !id.equalsIgnoreCase("Target") && !id2.equalsIgnoreCase("Source") && !id2.equalsIgnoreCase("Target")) {
				
				if (!already.containsKey(id2)) {
					
					String key = id + "#" + id2;
					String keyI = id2 + "#" + id;
					
					double sim = this.normalizedThreshold -1;
					if (this.normalizedSimilaritiesForGivenEdges.containsKey(key)) {
						sim = this.normalizedSimilaritiesForGivenEdges.get(key);
					} else if (this.normalizedSimilaritiesForGivenEdges.containsKey(keyI)) {
						sim = this.normalizedSimilaritiesForGivenEdges.get(keyI);
					}
					
					// System.out.println("sim: " + sim);
					
					if (sim >= this.normalizedThreshold) {
						assingNodeToCluster(already, node2, id2, clusterNr, nodesInThisCluster);
					}
					
				}
				
			}
			
		}
	}
	
	
	private void runThresholdDetermination(){
		
		String dummy = this.thresholdTextField.getText();
		double minThreshold = Double.parseDouble(this.minThresholdForComparison.getText());
		double maxThreshold = Double.parseDouble(this.maxThresholdForComparison.getText());
		double stepsize = Double.parseDouble(this.stepSizeForComparison.getText());
		double threshold = minThreshold;
		
		System.out.println("minThreshold = " + minThreshold + " maxThreshold = " + maxThreshold + " stepsize = " + stepsize);
		this.threshold = minThreshold;
		this.thresholdTextField.setText(Double.toString(minThreshold));
		
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Threshold");
		columnNames.add("Recall");
		columnNames.add("Precision");
		columnNames.add("F-measure I");
		columnNames.add("F-measure II");
		
		ComparisonResultsTable crt = new ComparisonResultsTable(columnNames);
		
		while(threshold<=maxThreshold){
			this.threshold = threshold;
			this.thresholdTextField.setText(Double.toString(threshold));
			try {
				boolean suc = runCC();
				if(suc) runFORCE();
				ComparisonResults cr = runComparison();
				Vector<String> v = cr.toVector(Double.toString(threshold));
				crt.addRow(v);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - " + e1.getMessage());
			}
			
			threshold += stepsize;
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
		
		this.thresholdTextField.setText(dummy);
		
	}
	
	
	
	private ComparisonResults runComparison() {
		
		buildNodesAndEdgesList();
		CyAttributes cyNodeAttributes = Cytoscape.getNodeAttributes();
		
		int nodeGoldStandardAttrIndex = this.validNodeAttributesComparisonGoldStandardComboBox.getSelectedIndex();
		String attrNameCluster = this.attributeNameCluster.getText();
		
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
			
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "ERROR - You must select two node attributes (cluster IDs)!");
			
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
			// Not in Java 1.5
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










































