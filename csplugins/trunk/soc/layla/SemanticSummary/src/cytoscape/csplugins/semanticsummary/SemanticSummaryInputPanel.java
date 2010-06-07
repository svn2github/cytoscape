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
	
	//Parameters
	private SemanticSummaryParameters networkParams;
	private CloudParameters cloudParams;
	
	//Text Fields
	private JFormattedTextField maxWordsTextField;
	private JFormattedTextField netWeightTextField;
	
	//JLabels
	private JLabel networkLabel;
	
	//JListData
	private DefaultListModel listValues;
	private JList cloudList;
	
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
		
		//TODO
		//ADD VALUES TO CLOUD LIST HERE
		//listValues.addElement("Cloud 1");//TEMP CODE
		//listValues.addElement("Cloud 2");//TEMP CODE
		cloudList = new JList(listValues);
		cloudList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cloudList.setSelectedIndex(0);
		cloudList.setVisibleRowCount(10);
		cloudList.setFixedCellHeight(DEF_ROW_HEIGHT);
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
		//netWeightTextField.addPropertyChangeListener("value", listener);
		netWeightTextField.setValue(1.0); //Set to 1 initially
		
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
	 * Sets the list of clouds to be for the supplied network.
	 * @param SemanticSummaryParameter - parameter for the network to display.
	 */
	public void setNetworkList(SemanticSummaryParameters params)
	{
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
		
		
		//Reset defaults
		//netWeightTextField.setValue(1.0); //make retrieve default
		
		//Set parameters
		networkParams = params;
		cloudParams = null;
		
		this.updateUI();
	}
	
	/**
	 * Sets the selected cloud to be one supplied.
	 * @params CloudParameters - cloud to be selected in list.
	 */
	public void setSelectedCloud(CloudParameters params)
	{
		SemanticSummaryParameters parent = params.getNetworkParams();
		setNetworkList(parent);
		
		cloudParams = params;
		int index = listValues.lastIndexOf(params.getCloudName());
		
		cloudList.setSelectedIndex(index);
		
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
		
		//Add buttons to panel
		panel.add(deleteButton);
		panel.add(updateButton);
		panel.add(createButton);
		
		return panel;
	}
	
	
	//TODO - Remove?
	/**
	 * Handles setting for the text field parameters that are numbers.
	 * Makes sure that the numbers make sense.
	 */
	private class FormattedTextFieldAction implements PropertyChangeListener
	{
		//METHOD
		public void propertyChange(PropertyChangeEvent e)
		{
			JFormattedTextField source = (JFormattedTextField) e.getSource();
			boolean invalid = false;
			
			if (source == netWeightTextField)
			{
				Number value = (Number) netWeightTextField.getValue();
				if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1))
				{
					//Update params here...hum...
				}
			}
		}
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
