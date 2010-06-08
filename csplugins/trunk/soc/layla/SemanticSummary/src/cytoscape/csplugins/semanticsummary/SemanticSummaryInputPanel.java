/*
 File: SemanticSummaryInputPanel.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

/**
 * The SemanticSummaryInputPanel class defines the panel that appears for 
 * the Semantic Summary Plugin in the West control panel.  It contains fields
 * necessary for viewing and creating new Semantic Summaries.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryInputPanel extends JPanel
{
	
	//VARIABLES
	
	DecimalFormat decFormat; //used in formatted text fields
	
	//TODO
	
	//Text Fields
	private JFormattedTextField maxWordsTextField;
	private JFormattedTextField netWeightTextField;
	
	//JLabels
	private JLabel networkLabel;
	
	//JListData
	private DefaultListModel listValues;
	private JList cloudList;
	private CloudListSelectionHandler handler;
	
	private static final int DEF_ROW_HEIGHT = 20;

	
	//instruction text
	//TODO
	
	//tool tips
	//TODO
	
	
	//CONSTRUCTORS
	public SemanticSummaryInputPanel()
	{
		decFormat = new DecimalFormat();
		decFormat.setParseIntegerOnly(false);
		
		//TODO	
		setLayout(new BorderLayout());
		
		//INITIALIZE PARAMETERS
		
		//Create the three main panels: CloudList, Options, and Bottom
		
		// Put the CloudList in a scroll pane
		JPanel cloudList = createCloudListPanel();
		JScrollPane cloudListScroll = new JScrollPane(cloudList);
		
		//Put the Options in a scroll pane
		CollapsiblePanel optionsPanel = createOptionsPanel();
		JScrollPane optionsScroll = new JScrollPane(optionsPanel);
		
		//Add button to bottom
		JPanel bottomPanel = createBottomPanel();
		
		//Add all the vertically aligned components to the main panel
		add(cloudListScroll,BorderLayout.NORTH);
		add(optionsScroll,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
	
	}
	
	//METHODS
	//TODO - add any button listener methods necessary
	
	/**
	 * Creates the cloud list panel for the currently selected network.
	 * @return JPanel - the cloud list panel.
	 */
	public JPanel createCloudListPanel()
	{
		JPanel panel = new JPanel();
		//panel.setLayout(new BorderLayout());
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		//Name of the network
		JPanel networkPanel = new JPanel();
		networkPanel.setLayout(new BorderLayout());
		networkLabel = new JLabel();
		networkPanel.add(networkLabel, BorderLayout.CENTER);
		
		//List of Clouds
		listValues = new DefaultListModel();
		
		cloudList = new JList(listValues);
		cloudList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cloudList.setSelectedIndex(0);
		cloudList.setVisibleRowCount(10);
		cloudList.setFixedCellHeight(DEF_ROW_HEIGHT);
		
		//Setup Selection Listener
		ListSelectionModel listSelectionModel = cloudList.getSelectionModel();
		handler = new CloudListSelectionHandler();
		listSelectionModel.addListSelectionListener(handler);
		
		JScrollPane listScrollPane = new JScrollPane(cloudList);
		
		//Add to panel
		panel.add(networkPanel);
		panel.add(listScrollPane);
		
		
		return panel;
	}
	
	
	/**
	 * Creates a collapsable panel that holds all of the user entered
	 * cloud parameters.
	 * 
	 * @return collapsiblePanel - main panel with cloud parameters
	 */
	public CollapsiblePanel createOptionsPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Cloud Parameters");
		collapsiblePanel.setCollapsed(false);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		//Semantic Analysis Panel
		CollapsiblePanel semAnalysis = createSemAnalysisPanel();
		semAnalysis.setCollapsed(false);
		
		//Display Settings
		CollapsiblePanel displaySettings = createDisplaySettingsPanel();
		displaySettings.setCollapsed(true);
		
		//Word Exclusion
		CollapsiblePanel exclusionList = createExclusionListPanel();
		exclusionList.setCollapsed(true);
		
		//TODO - Finish
		panel.add(semAnalysis);
		panel.add(displaySettings);
		panel.add(exclusionList);
		
		collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
		
		return collapsiblePanel;
	}
	
	/**
	 * Creates a CollapsiblePanel that holds the Semantic Analysis information.
	 * @return CollapsiblePanel - semantic analysis panel interface.
	 */
	private CollapsiblePanel createSemAnalysisPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Semantic Analysis");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		
		JRadioButton name = new JRadioButton("Node Name");
		
		JPanel attributePanel = new JPanel();
		attributePanel.setLayout(new BorderLayout());
		
		JRadioButton attribute = new JRadioButton("Attribute");
		JFormattedTextField attributeVal = new JFormattedTextField();
		attributeVal.setEditable(false);
		attributeVal.setColumns(10);
		attributePanel.add(attribute,BorderLayout.WEST);
		attributePanel.add(attributeVal,BorderLayout.EAST);
		
		panel.add(name);
		panel.add(attributePanel);
		
		//TODO - add listeners and attribute selections
		
		collapsiblePanel.getContentPane().add(panel,BorderLayout.NORTH);
		return collapsiblePanel;
	}
	
	/**
	 * Creates a CollapsiblePanel that holds the display settings information.
	 * @return CollapsiblePanel - display settings panel interface.
	 */
	private CollapsiblePanel createDisplaySettingsPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Display Settings");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		
		//Max words input
		JLabel maxWordsLabel = new JLabel("Max Num of Words");
		maxWordsTextField = new JFormattedTextField();
		maxWordsTextField.setColumns(10);
		
		//Max words panel
		JPanel maxWordsPanel = new JPanel();
		maxWordsPanel.setLayout(new BorderLayout());
		maxWordsPanel.add(maxWordsLabel, BorderLayout.WEST);
		maxWordsPanel.add(maxWordsTextField, BorderLayout.EAST);
		
		//Network Weight Factor
		JLabel netWeightLabel = new JLabel("Network Weight Factor");
		netWeightTextField = new JFormattedTextField(decFormat);
		netWeightTextField.setColumns(3);
		netWeightTextField.setValue(SemanticSummaryManager.getInstance().
				getDefaultNetWeight()); //Set to default initially
		
		//Network Weight Factor Panel
		JPanel netWeightPanel = new JPanel();
		netWeightPanel.setLayout(new BorderLayout());
		netWeightPanel.add(netWeightLabel,BorderLayout.WEST);
		netWeightPanel.add(netWeightTextField,BorderLayout.EAST);
		
		//Add components to main panel
		panel.add(maxWordsPanel);
		panel.add(netWeightPanel);
		
		//Add to collapsible panel
		collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
		
		return collapsiblePanel;
	}
	
	/**
	 * Creates a CollapsiblePanel that holds the word exclusion list information.
	 * @return CollapsiblePanel - word exclusion list panel interface.
	 */
	private CollapsiblePanel createExclusionListPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Word Exclusion List");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		
		//Add Word
		JLabel addWordLabel = new JLabel("Add Word");
		JFormattedTextField addWordTextField = new JFormattedTextField();
		addWordTextField.setColumns(15);
		
		//Add Word panel
		JPanel addWordPanel = new JPanel();
		addWordPanel.setLayout(new BorderLayout());
		addWordPanel.add(addWordLabel, BorderLayout.WEST);
		addWordPanel.add(addWordTextField, BorderLayout.EAST);
		
		//Remove Word
		JLabel removeWordLabel = new JLabel("Remove Word");
		JFormattedTextField removeWordTextField = new JFormattedTextField();
		removeWordTextField.setColumns(15);
		
		//Remove Word Panel
		JPanel removeWordPanel = new JPanel();
		removeWordPanel.setLayout(new BorderLayout());
		removeWordPanel.add(removeWordLabel, BorderLayout.WEST);
		removeWordPanel.add(removeWordTextField, BorderLayout.EAST);
		
		//Export Text Button
		JButton exportTextButton = new JButton("txt export");
		JPanel exportTextPanel = new JPanel();
		exportTextPanel.setLayout(new BorderLayout());
		exportTextPanel.add(exportTextButton, BorderLayout.WEST);
		
		//Add components to main panel
		panel.add(addWordPanel);
		panel.add(removeWordPanel);
		panel.add(exportTextPanel);
		
		//Add to collapsible panel
		collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
		
		return collapsiblePanel;
	}
	
	/**
	 * Utility to create a panel for the buttons at the bottom of the Semantic 
	 * Summary Input Panel.
	 */
	private JPanel createBottomPanel()
	{
		//TODO - add listeners to all of the buttons
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		//Create buttons
		JButton deleteButton = new JButton("Delete");
		JButton updateButton = new JButton("Update");
		JButton createButton = new JButton("Create");
		
		//Add actions to buttons
		createButton.addActionListener(new CreateCloudAction());
		deleteButton.addActionListener(new DeleteCloudAction());
		
		//Add buttons to panel
		panel.add(deleteButton);
		panel.add(updateButton);
		panel.add(createButton);
		
		return panel;
	}
	
	/**
	 * Sets the list of clouds to be for the supplied network.
	 * @param SemanticSummaryParameter - parameter for the network to display.
	 */
	public void setNetworkList(SemanticSummaryParameters params)
	{
		//TODO - add check to see if network has changed?
		
		//clear current values
		networkLabel.setText("");
		listValues.clear();
		
		//Set new current values
		networkLabel.setText(params.getNetworkName());
		
		List<String> clouds = new ArrayList<String>(params.getClouds().keySet());
		Collections.sort(clouds);
		
		Iterator<String> iter = clouds.iterator();
		while(iter.hasNext())
		{
			String curCloud = iter.next();
			listValues.addElement(curCloud);
		}
		
		//Update Manager variables
		SemanticSummaryManager.getInstance().setCurNetwork(params);
		SemanticSummaryManager.getInstance().setCurCloud(null);
		
		this.updateUI();
	}
	
	/**
	 * Loads the values from a specified cloud in the user supplied parameters
	 * and sets it as the current cloud.
	 * Assumes that parent list is already updated.
	 * @param CloudParameters - of the cloud to load.
	 */
	public void loadCurrentCloud(CloudParameters params)
	{
		netWeightTextField.setValue(params.getNetWeightFactor());
		SemanticSummaryManager.getInstance().setCurCloud(params);
	}
	
	
	/**
	 * Adds the cloud to the list and sets it to be the selected cloud.
	 * @param CloudParameters - cloud to add to list.
	 */
	public void addNewCloud(CloudParameters params)
	{
		//Turn off listener while doing work
		ListSelectionModel listSelectionModel = cloudList.getSelectionModel();
		listSelectionModel.removeListSelectionListener(handler);
		
		//Update current cloud and add to list
		SemanticSummaryManager.getInstance().setCurCloud(params);
		String cloudName = params.getCloudName();
		listValues.addElement(cloudName);
		int index = listValues.lastIndexOf(cloudName);
		
		//Set to be selected
		cloudList.setSelectedIndex(index);
		
		//Turn listener back on
		listSelectionModel.addListSelectionListener(handler);
	}
	/**
	 * Sets all user input fields to their default values.
	 */
	public void setUserDefaults()
	{
		netWeightTextField.setValue(SemanticSummaryManager.getInstance().getDefaultNetWeight());
		this.updateUI();
	}
	
	
	//Getters and Setters
	public JFormattedTextField getNetWeightTextField()
	{
		return netWeightTextField;
	}
	
	public JLabel getNetworkLabel()
	{
		return networkLabel;
	}
	
	public DefaultListModel getListValues()
	{
		return listValues;
	}
}
