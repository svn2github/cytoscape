/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A dialog for the GraphExpander plug-in.
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.org,
 *         iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */

package graphExpander.gui;

import javax.swing.*;
import java.io.File;
import java.awt.*;
import java.awt.event.ActionEvent;
import cytoscape.*;

import java.util.*;
import graphExpander.expander.*;

public class GraphExpanderDialog extends JDialog {

	public static final String DIALOG_TITLE = "Graph Expader";
	public static final int DEFAULT_PATH_SIZE = 2;
	public static final int DEFAULT_NUM_NEIGHBORS = 1;
	public static final int EXISTING_NODES = 0;
	public static final int DISCONNECTED_NODES = 1;
	public static final int SHORTEST_PATHS = 2;
	public static final int NEIGHBORS = 3;
	
	protected File sourceCurrentDirectory = new File(System
			.getProperty("user.home"));
	protected File toExpandCurrentDirectory = new File(System
			.getProperty("user.home"));
	
	protected File saveToDirectory = new File(System.getProperty("user.home"));
	
	protected JComboBox sourceGraphs;
	protected JComboBox targetGraphs;
	protected JComboBox saveToFiles;
	protected JRadioButton existingNodesOption;
	protected JRadioButton disconnectedNodesOption;
	protected JRadioButton shortestPathsOption;
	protected JRadioButton neighborsOption;
	protected JTextField neighborsField;
	protected JTextField spField;

	/**
	 * Constructor.
	 */
	public GraphExpanderDialog() {
		super();
		setTitle(DIALOG_TITLE);
		create();
	}//constructor

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner Frame of this dialog
	 */
	public GraphExpanderDialog(Frame owner) {
		super(owner, DIALOG_TITLE);
		create();
	}//constructor

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the dialog that owns this dialog
	 */
	public GraphExpanderDialog(Dialog owner) {
		super(owner, DIALOG_TITLE);
		create();
	}//constructor

	/**
	 * Creates the dialog.
	 */
	protected void create() {

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel inputOutputPanel = new JPanel();
		inputOutputPanel.setLayout(new BoxLayout(inputOutputPanel,
				BoxLayout.Y_AXIS));
		inputOutputPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Input/Output"));
		JPanel expandWhatPanel = createInputOutputPanel();
		inputOutputPanel.add(expandWhatPanel);

		JPanel expandPanel = new JPanel();
		expandPanel.setLayout(new BoxLayout(expandPanel, BoxLayout.Y_AXIS));
		expandPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), "Expand Graph"));
		JPanel expandOptionsPanel = createExpandOptions();
		expandPanel.add(expandOptionsPanel);

		JPanel buttonsPanel = createButtonsPanel();

		mainPanel.add(inputOutputPanel);
		mainPanel.add(expandPanel);
		mainPanel.add(buttonsPanel);

		setContentPane(mainPanel);
		setResizable(false);
	}//create

	/**
	 * Creates the panel that specifies where to get the interactions from, what
	 * to expand, and what to do with the results of the expansion.
	 * 
	 * @return a JPanel
	 */
	protected JPanel createInputOutputPanel() {

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel sourcePanel = new JPanel(new GridLayout(4, 2));

		JLabel sourceLabel = new JLabel("Expand using interactions in:");
		Set nets = Cytoscape.getNetworkSet();
		CyNetwork[] allLoadedNets = (CyNetwork[]) nets
				.toArray(new CyNetwork[nets.size()]);
		this.sourceGraphs = new JComboBox(allLoadedNets);
		this.sourceGraphs.setEditable(true); // user can enter his own graph
		// that is not in Cytoscape
		JButton browseButton1 = new JButton("Browse");
		browseButton1.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser(
						GraphExpanderDialog.this.sourceCurrentDirectory);
				int returnVal = fileChooser
						.showOpenDialog(GraphExpanderDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					sourceGraphs.addItem(selectedFile);
					sourceGraphs.setSelectedItem(selectedFile);
					GraphExpanderDialog.this.sourceCurrentDirectory = fileChooser
							.getSelectedFile().getParentFile();
				}
			}
		});

		JPanel sourceGraphsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		sourceGraphsPanel.add(this.sourceGraphs);
		sourceGraphsPanel.add(browseButton1);
		sourcePanel.add(sourceLabel);
		sourcePanel.add(sourceGraphsPanel);

		JLabel targetLabel = new JLabel("Graph to expand:");
		this.targetGraphs = new JComboBox(allLoadedNets);
		this.targetGraphs.setEditable(true); // user can enter his own graph
		// that is not in Cytoscape
		JButton browseButton2 = new JButton("Browse");
		browseButton2.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser(
						GraphExpanderDialog.this.toExpandCurrentDirectory);
				int returnVal = fileChooser
						.showOpenDialog(GraphExpanderDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					targetGraphs.addItem(selectedFile);
					targetGraphs.setSelectedItem(selectedFile); // this makes
																// the combo-box
																// resize...FIX
					GraphExpanderDialog.this.toExpandCurrentDirectory = fileChooser
							.getSelectedFile().getParentFile();
				}
			}
		});
		JPanel sourceGraphsPanel2 = new JPanel(
				new FlowLayout(FlowLayout.CENTER));
		sourceGraphsPanel2.add(this.targetGraphs);
		sourceGraphsPanel2.add(browseButton2);
		sourcePanel.add(targetLabel);
		sourcePanel.add(sourceGraphsPanel2);

		JCheckBox toFileOption = new JCheckBox("Save result to file:");
		JPanel filePathInputPanel = new JPanel(
				new FlowLayout(FlowLayout.CENTER));
		this.saveToFiles = new JComboBox();
		this.saveToFiles.setEditable(true);
		JButton browseButton3 = new JButton("Browse");
		browseButton3.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser(
						GraphExpanderDialog.this.saveToDirectory);
				int returnVal = fileChooser
						.showOpenDialog(GraphExpanderDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					saveToFiles.addItem(selectedFile);
					saveToFiles.setSelectedItem(selectedFile);
					GraphExpanderDialog.this.saveToDirectory = fileChooser
							.getSelectedFile().getParentFile();
				}
			}
		});
		filePathInputPanel.add(this.saveToFiles);
		filePathInputPanel.add(browseButton3);
		sourcePanel.add(toFileOption);
		sourcePanel.add(filePathInputPanel);

		JCheckBox newGraphOption = new JCheckBox("Create new graph from result");
		sourcePanel.add(newGraphOption);
		sourcePanel.add(Box.createHorizontalGlue());

		mainPanel.add(sourcePanel);

		return mainPanel;
	}//createInputOutputPanel

	/**
	 * Creates the panel that specifies how to expand the graph.
	 * 
	 * @return a JPanel
	 */
	protected JPanel createExpandOptions() {

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		JPanel existingNodesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.existingNodesOption = new JRadioButton(
				"Add interactions between existing nodes.");
		this.existingNodesOption.setSelected(true);
		buttonGroup.add(this.existingNodesOption);
		existingNodesPanel.add(this.existingNodesOption);
		JPanel disconnectedNodesPanel = new JPanel(new FlowLayout(
				FlowLayout.LEFT));
		this.disconnectedNodesOption = new JRadioButton(
				"Add interactions to connect disconnected nodes.");
		buttonGroup.add(this.disconnectedNodesOption);
		disconnectedNodesPanel.add(this.disconnectedNodesOption);

		optionsPanel.add(existingNodesPanel);
		optionsPanel.add(disconnectedNodesPanel);

		JPanel shortestPathsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.shortestPathsOption = new JRadioButton(
				"Add interactions and nodes in shortest paths of lenght <=");
		buttonGroup.add(this.shortestPathsOption);
		this.spField = new JTextField(Integer
				.toString(GraphExpanderDialog.DEFAULT_PATH_SIZE), 2);
		shortestPathsPanel.add(this.shortestPathsOption);
		shortestPathsPanel.add(spField);
		optionsPanel.add(shortestPathsPanel);

		JPanel neighborsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.neighborsOption = new JRadioButton("Add nodes with >= ");
		buttonGroup.add(this.neighborsOption);
		this.neighborsField = new JTextField(Integer
				.toString(GraphExpanderDialog.DEFAULT_NUM_NEIGHBORS), 2);
		JLabel neighborsLabel = new JLabel(" neighbors in the graph to expand");
		neighborsPanel.add(this.neighborsOption);
		neighborsPanel.add(neighborsField);
		neighborsPanel.add(neighborsLabel);
		optionsPanel.add(neighborsPanel);

		mainPanel.add(optionsPanel);

		JPanel expandPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton filtersButton = new JButton("Filters...");
		JButton expandButton = new JButton("Expand");
		expandButton.addActionListener(new ExpandButtonListener());
		expandPanel.add(filtersButton);
		expandPanel.add(expandButton);
		mainPanel.add(expandPanel);

		return mainPanel;
	}//createExpandOptions

	/**
	 * Creates the buttons at the end of the dialog.
	 * 
	 * @return a JPanel
	 */
	protected JPanel createButtonsPanel() {

		JPanel mainPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent event) {
				GraphExpanderDialog.this.dispose();
			}
		});
		mainPanel.add(closeButton);

		return mainPanel;
	}//createButtonsPanel

	/**
	 * Overrides super.setVisible so that things that need to be updated are
	 * updated.
	 * 
	 * @param visible
	 *            whether to set this Dialog visible or not
	 */
	public void setVisible(boolean visible) {

		// Update the available graphs combo boxes in case that the user loaded
		// new graphs
		if (visible) {
			Set nets = Cytoscape.getNetworkSet();
			CyNetwork[] allLoadedNets = (CyNetwork[]) nets
					.toArray(new CyNetwork[nets.size()]);

			if (this.sourceGraphs.getItemCount() == 0) {
				for (int i = 0; i < allLoadedNets.length; i++) {
					this.sourceGraphs.addItem(allLoadedNets[i]);
				}
			} else {
				int numItems = this.sourceGraphs.getItemCount();
				int j;
				for (int i = 0; i < allLoadedNets.length; i++) {
					for (j = 0; j < numItems; j++) {
						if (allLoadedNets[i] == this.sourceGraphs.getItemAt(j)) {
							break;
						}
					}//for j
					if (j == numItems) {
						this.sourceGraphs.addItem(allLoadedNets[i]);
					}
				}//for i
			}// else

			if (this.targetGraphs.getItemCount() == 0) {
				for (int i = 0; i < allLoadedNets.length; i++) {
					this.targetGraphs.addItem(allLoadedNets[i]);
				}
			} else {
				int numItems = this.targetGraphs.getItemCount();
				int j;
				for (int i = 0; i < allLoadedNets.length; i++) {
					for (j = 0; j < numItems; j++) {
						if (allLoadedNets[i] == this.targetGraphs.getItemAt(j)) {
							break;
						}
					}//for j
					if (j == numItems) {
						this.targetGraphs.addItem(allLoadedNets[i]);
					}
				}//for i
			}
		}// if (visible)

		super.setVisible(visible);
	}//setVisible

	/**
	 * @return the selected object that contains the source nodes and edges,
	 *         this Object is either a CyNetwork, or a File that contains the
	 *         interactions
	 */
	public Object getSelectedSource() {
		return this.sourceGraphs.getSelectedItem();
	}//getSelectedSource

	/**
	 * @return the selected object that contains the graph to be modified, this
	 *         Object is either a CyNetwork or a File that contains the target
	 *         graph
	 */
	public Object getSelectedTarget() {
		return this.targetGraphs.getSelectedItem();
	}//getSelectedTarget

	/**
	 * Creates a CyNetwork with no view given the graph file.
	 * 
	 * @param graphFile
	 *            the file that describes the graph to create
	 * @return the created CyNetwork
	 */
	protected CyNetwork createCyNetwork(File graphFile) {
		// The file needs to have a .sif or .gml termination!!!
		CyNetwork network = Cytoscape.createNetworkFromFile(graphFile
				.getAbsolutePath());
		return network;
	}//createCyNetwork

	/**
	 * Replaces oldObject by newObject in sourceGraphs JComboBox
	 * 
	 * @param oldObject
	 * @param newObject
	 */
	protected void replaceSourceObject(Object oldObject, Object newObject) {
		int numItems = this.sourceGraphs.getItemCount();
		for (int i = 0; i < numItems; i++) {
			if (this.sourceGraphs.getItemAt(i) == oldObject) {
				this.sourceGraphs.removeItemAt(i);
				this.sourceGraphs.insertItemAt(newObject, i);
				break;
			}
		}//for i
	}//replaceSourceObject

	/**
	 * Replaces oldObject by newObject in targetGraphs JComboBox
	 * 
	 * @param oldObject
	 * @param newObject
	 */
	protected void replaceTargetObject(Object oldObject, Object newObject) {
		int numItems = this.targetGraphs.getItemCount();
		for (int i = 0; i < numItems; i++) {
			if (this.targetGraphs.getItemAt(i) == oldObject) {
				this.targetGraphs.removeItemAt(i);
				this.targetGraphs.insertItemAt(newObject, i);
				break;
			}
		}//for i
	}//replaceTargetObject

	/**
	 * @return the currently selected expanding method: EXISTING_NODES, 
	 * DISCONNECTED_NODES, SHORTEST_PATHS, or NEIGHBORS, -1 if none selected
	 */
	public int getSelectedExpandingMethod (){
		if(this.existingNodesOption.isSelected()){
			return EXISTING_NODES;
		}
		
		if(this.disconnectedNodesOption.isSelected()){
			return DISCONNECTED_NODES;
		}
		
		if(this.shortestPathsOption.isSelected()){
			return SHORTEST_PATHS;
		}
		
		if(this.neighborsOption.isSelected()){
			return NEIGHBORS;
		}
	
		return -1;
	}//getSelectedExpandingMethod
	
	/**
	 * 
	 * @return the maximum path lenght for the shortest paths expanding method
	 */
	public int getMaxPathLenght (){
		String text = this.spField.getText();
		int maxPathLength = -1;
		try{
			maxPathLength = Integer.parseInt(text);
		}catch (NumberFormatException exception){
			System.err.println("Not a number.");
		}
		
		return maxPathLength;
	}//getMaxPathLength
	
	/**
	 * 
	 * @return the minimum number of neighbors for the K-neighbors expanding method
	 */
	public int getMinNumNeighbors (){
		String text = this.neighborsField.getText();
		int minNumNeighbors = -1;
		try{
			minNumNeighbors = Integer.parseInt(text);
		}catch(NumberFormatException exception){
			System.err.println("Not a number");
		}
		return minNumNeighbors;
	}//getMinNumNeighbors
	
	/**
	 * Applies the selected method of expansion.
	 * 
	 * @ return an array of size two, indicating number of added nodes (at index
	 *         GraphExpander.NUM_ADDED_NODES_INDEX), and the number of edges added (at index
	 *         GraphExpander.NUM_ADDED_EDGES_INDEX)
	 */
	protected int [] applySelectedExpandingMethod (CyNetwork source_net, CyNetwork target_net){
		
		int [] empty = {0,0};
		
		if(this.existingNodesOption.isSelected()){
			return GraphExpander.addInteractionsBetweenExistingNodes(
					source_net, target_net);
		}
		
		if(this.disconnectedNodesOption.isSelected()){
			return GraphExpander.addInteractionsToConnectGraph(
					source_net,target_net);
		}
		
		if(this.shortestPathsOption.isSelected()){
			int maxPathLength = getMaxPathLenght();
			if(maxPathLength < 0){
				JOptionPane.showMessageDialog(this,"Please enter a positive integer.","Input Error",JOptionPane.ERROR_MESSAGE);
				return empty;
			}
			return GraphExpander.addShortestPaths(source_net,target_net,maxPathLength);
		}
		
		if(this.neighborsOption.isSelected()){
			int minNumNeighbors = getMinNumNeighbors();
			if(minNumNeighbors < 0){
				JOptionPane.showMessageDialog(this,"Please enter a positive integer.","Input Error",JOptionPane.ERROR_MESSAGE);
				return empty;
			}
			return GraphExpander.addNodesWithKneighbors(source_net,target_net,minNumNeighbors);
		}
		
		return empty;
	}//applySelectedExpandingMethod
	
	// --------------- internal classes ------------------//

	class ExpandButtonListener extends AbstractAction {

		public ExpandButtonListener() {
			super();
		}//cons

		public void actionPerformed(ActionEvent event) {
			Object sourceObject = getSelectedSource();
			Object targetObject = getSelectedTarget();
			if (sourceObject == null || targetObject == null) {
				JOptionPane.showMessageDialog(GraphExpanderDialog.this,
						"Please select a source and a target graph.");
				return;
			}
			CyNetwork sourceNetwork = null;
			CyNetwork targetNetwork = null;
			if (sourceObject instanceof File) {
				// This will create a view as well, if the number of nodes
				// is < max graph size
				// TODO: Change Cytoscape method so that this is not so!
				sourceNetwork = createCyNetwork((File) sourceObject);
				// replace the File object by the network since it is likely
				// that the user will make use of it again
				replaceSourceObject(sourceObject, sourceNetwork);
				GraphExpanderDialog.this.sourceGraphs
						.setSelectedItem(sourceNetwork);
			} else if (sourceObject instanceof CyNetwork) {
				sourceNetwork = (CyNetwork) sourceObject;
			} else{
				
				System.err.println("Oops! sourceObject is not a File or a CyNetwork!");
			}
			if (targetObject instanceof File) {
				targetNetwork = createCyNetwork((File) targetObject);
				System.out.println("targetNetwork contains " + targetNetwork.getNodeCount() + " nodes.");
				replaceTargetObject(targetObject, targetNetwork);
				GraphExpanderDialog.this.targetGraphs
						.setSelectedItem(targetNetwork);
			} else if (targetObject instanceof CyNetwork) {
				targetNetwork = (CyNetwork) targetObject;
			} else{
				System.err.println("Oops! targetObject is not a File or a CyNetwork!");
			}
			
			int [] numNewNodesEdges = applySelectedExpandingMethod(sourceNetwork, targetNetwork);
			System.out.println("new nodes = " + numNewNodesEdges[0] + " new edges = " + numNewNodesEdges[1]);
			
		}//actionPerformed

	}//ExpandButtonListener

}// class GraphExpanderDialog
