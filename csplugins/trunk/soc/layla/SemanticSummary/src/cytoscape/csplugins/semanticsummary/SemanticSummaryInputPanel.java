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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.MaskFormatter;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * The SemanticSummaryInputPanel class defines the panel that appears for 
 * the Semantic Summary Plugin in the West control panel.  It contains fields
 * necessary for viewing and creating new Semantic Summaries.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryInputPanel extends JPanel implements ItemListener, 
			ActionListener
{
	
	//VARIABLES
	
	DecimalFormat decFormat; //used in formatted text fields with decimals
	NumberFormat intFormat; //used in formatted text fields with integers
	
	
	//Text Fields
	private JFormattedTextField maxWordsTextField;
	private JFormattedTextField netWeightTextField;
	private JFormattedTextField clusterCutoffTextField;
	private JTextField addWordTextField;
	
	//JComboBox
	JComboBox cmbAttributes;
	JComboBox cmbRemoval;
	JComboBox cmbStyle;
	
	//JLabels
	private JLabel networkLabel;
	
	//JListData
	private DefaultListModel listValues;
	private JList cloudList;
	private CloudListSelectionHandler handler;
	
	//Buttons
	private JButton removeWordButton;
	private JButton addWordButton;
	
	//String Constants for Separators in remove word combo box
	private static final String addedSeparator = "--Added Words--";
	private static final String flaggedSeparator = "--Flagged Words--";
	private static final String stopSeparator = "--Stop Words--";
	
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
		
		intFormat = NumberFormat.getIntegerInstance();
		intFormat.setParseIntegerOnly(true);
		
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
		optionsScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		//Add button to bottom
		JPanel bottomPanel = createBottomPanel();
		
		//Add all the vertically aligned components to the main panel
		add(cloudListScroll,BorderLayout.NORTH);
		add(optionsScroll,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
	
	}
	
	//METHODS
	
	/**
	 * Creates the cloud list panel for the currently selected network.
	 * @return JPanel - the cloud list panel.
	 */
	public JPanel createCloudListPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//Name of the network
		JPanel networkPanel = new JPanel();
		networkPanel.setLayout(new FlowLayout());
		networkLabel = new JLabel();
		networkPanel.add(networkLabel);
		
		//List of Clouds
		listValues = new DefaultListModel();
		
		cloudList = new JList(listValues);
		cloudList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cloudList.setSelectedIndex(0);
		cloudList.setVisibleRowCount(10);
		cloudList.setFixedCellHeight(DEF_ROW_HEIGHT);
		cloudList.addMouseListener(new CloudListMouseListener());
		
		//Setup Selection Listener
		ListSelectionModel listSelectionModel = cloudList.getSelectionModel();
		handler = new CloudListSelectionHandler();
		listSelectionModel.addListSelectionListener(handler);
		JScrollPane listScrollPane = new JScrollPane(cloudList);
		
		//Add to panel
		panel.add(networkPanel, BorderLayout.NORTH);
		panel.add(listScrollPane, BorderLayout.CENTER);
		
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
		
		//Cloud Layout
		CollapsiblePanel cloudLayout = createCloudLayoutPanel();
		cloudLayout.setCollapsed(true);
		
		//Add all Panels
		panel.add(semAnalysis);
		panel.add(displaySettings);
		panel.add(exclusionList);
		panel.add(cloudLayout);
		
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
		
		
		JPanel attributePanel = new JPanel();
		attributePanel.setLayout(new GridBagLayout());

		JLabel nodeAttributeLabel = new JLabel("Node ID/Attribute: ");
		
		WidestStringComboBoxModel wscbm = new WidestStringComboBoxModel();
		cmbAttributes = new JComboBox(wscbm);
		cmbAttributes.addPopupMenuListener(new WidestStringComboBoxPopupMenuListener());
		cmbAttributes.setEditable(false);
	    Dimension d = cmbAttributes.getPreferredSize();
	    cmbAttributes.setPreferredSize(new Dimension(15, d.height));
	    cmbAttributes.setToolTipText("Define which node value to use for semantic analysis");

		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5,0,0,0);
		attributePanel.add(nodeAttributeLabel, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 10, 0, 0);
		attributePanel.add(cmbAttributes, gridBagConstraints);
	    
	    refreshAttributeCMB();
		
		panel.add(attributePanel);
		
		collapsiblePanel.getContentPane().add(panel,BorderLayout.NORTH);
		return collapsiblePanel;
	}
	
	/**
	 * Creates a CollapsiblePanel that holds the display settings information.
	 * @return CollapsiblePanel - display settings panel interface.
	 */
	private CollapsiblePanel createDisplaySettingsPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Advanced Parameters");
		
		//Used to retrieve defaults
		CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		
		//Max words input
		JLabel maxWordsLabel = new JLabel("Max Num of Words");
		maxWordsTextField = new JFormattedTextField(intFormat);
		maxWordsTextField.setColumns(10);
		maxWordsTextField.setValue(params.getDefaultMaxWords()); //Set to default initially
		maxWordsTextField.addPropertyChangeListener(new SemanticSummaryInputPanel.FormattedTextFieldAction());
		
		StringBuffer buf = new StringBuffer();
		buf.append("<html>" + "Sets a limit on the number of words to display in the cloud" + "<br>");
		buf.append("<b>Acceptable Values:</b> greater than or equal to 0" + "</html>");
		maxWordsTextField.setToolTipText(buf.toString());
		
		//Max words panel
		JPanel maxWordsPanel = new JPanel();
		maxWordsPanel.setLayout(new BorderLayout());
		maxWordsPanel.add(maxWordsLabel, BorderLayout.WEST);
		maxWordsPanel.add(maxWordsTextField, BorderLayout.EAST);
		
		//Network Weight Factor
		JLabel netWeightLabel = new JLabel("Network Normalization");
		netWeightTextField = new JFormattedTextField(decFormat);
		netWeightTextField.setColumns(3);
		netWeightTextField.setValue(params.getDefaultNetWeight()); //Set to default initially
		netWeightTextField.addPropertyChangeListener(new SemanticSummaryInputPanel.FormattedTextFieldAction());
		
		buf = new StringBuffer();
		buf.append("<html>" + "Determines how much weight to give the whole network when normalizing the selected nodes" + "<br>");
		buf.append("<b>Acceptable Values:</b> greater than or equal to 0 and less than or equal to 1" + "</html>");
		netWeightTextField.setToolTipText(buf.toString());
		
		//Network Weight Factor Panel
		JPanel netWeightPanel = new JPanel();
		netWeightPanel.setLayout(new BorderLayout());
		netWeightPanel.add(netWeightLabel,BorderLayout.WEST);
		netWeightPanel.add(netWeightTextField,BorderLayout.EAST);
		
		//Clustering Cutoff
		JLabel clusterCutoffLabel = new JLabel("Word Aggregation Cutoff");
		clusterCutoffTextField = new JFormattedTextField(decFormat);
		clusterCutoffTextField.setColumns(3);
		clusterCutoffTextField.setValue(params.getDefaultClusterCutoff()); //Set to default initially
		clusterCutoffTextField.addPropertyChangeListener(new SemanticSummaryInputPanel.FormattedTextFieldAction());
		
		buf = new StringBuffer();
		buf.append("<html>" + "Cutoff for placing two words in the same cluster - ratio of the observed joint probability of the words to their joint probability if the words appeared independently of each other" + "<br>");
		buf.append("<b>Acceptable Values:</b> greater than or equal to 0" + "</html>");
		clusterCutoffTextField.setToolTipText(buf.toString());
		
		//Clustering Cutoff Panel
		JPanel clusterCutoffPanel = new JPanel();
		clusterCutoffPanel.setLayout(new BorderLayout());
		clusterCutoffPanel.add(clusterCutoffLabel, BorderLayout.WEST);
		clusterCutoffPanel.add(clusterCutoffTextField, BorderLayout.EAST);
		
		
		//Add components to main panel
		panel.add(maxWordsPanel);
		panel.add(netWeightPanel);
		panel.add(clusterCutoffPanel);
		
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
		addWordTextField = new JFormattedTextField();
		addWordTextField.setColumns(15);
		
		CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
		if (params.equals(SemanticSummaryManager.getInstance().getNullCloudParameters()))
			addWordTextField.setEditable(false);
		else
			addWordTextField.setEditable(true);
		
		addWordTextField.setText("");
		addWordTextField.addPropertyChangeListener(new SemanticSummaryInputPanel.FormattedTextFieldAction());
		
		StringBuffer buf = new StringBuffer();
		buf.append("<html>" + "Allows for specification of an additional word to be excluded when doing semantic analysis" + "<br>");
		buf.append("<b>Acceptable Values:</b> Only alpha numeric values - no spaces allowed" + "</html>");
		addWordTextField.setToolTipText(buf.toString());
		
		addWordButton = new JButton();
		addWordButton.setText("Add");
		addWordButton.setEnabled(false);
		addWordButton.addActionListener(this);
		
		//Word panel
		JPanel wordPanel = new JPanel();
		wordPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5,0,0,0);
		wordPanel.add(addWordLabel, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5,10,0,10);
		wordPanel.add(addWordTextField, gridBagConstraints);
		
		//Button stuff
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new Insets(5,0,0,0);
		wordPanel.add(addWordButton, gridBagConstraints);
		
		
		// Word Removal Label
		JLabel removeWordLabel = new JLabel("Remove Word");
		
		//Word Removal Combo Box
		WidestStringComboBoxModel wscbm = new WidestStringComboBoxModel();
		cmbRemoval = new JComboBox(wscbm);
		cmbRemoval.addPopupMenuListener(new WidestStringComboBoxPopupMenuListener());
		cmbRemoval.setEditable(false);
	    Dimension d = cmbRemoval.getPreferredSize();
	    cmbRemoval.setPreferredSize(new Dimension(15, d.height));
	    cmbRemoval.addItemListener(this);
	    cmbRemoval.setToolTipText("Allows for selection a word to remove from the semantic analysis exclusion list");

	    //Word Removal Button
	    removeWordButton = new JButton();
	    removeWordButton.setText("Remove");
	    removeWordButton.setEnabled(false);
	    removeWordButton.addActionListener(this);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5,0,0,0);
		wordPanel.add(removeWordLabel, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 10, 0, 10);
		wordPanel.add(cmbRemoval, gridBagConstraints);
		
		//Button stuff
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		wordPanel.add(removeWordButton, gridBagConstraints);

		
		//Export Text Button
		//JButton exportTextButton = new JButton("txt export");
		//gridBagConstraints = new GridBagConstraints();
		//gridBagConstraints.gridy = 2;
		//gridBagConstraints.gridx = 2;
		//gridBagConstraints.anchor = GridBagConstraints.EAST;
		//gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
		//Comment out for the time being until we decide if this is needed
		//wordPanel.add(exportTextButton, gridBagConstraints);
		
		refreshRemovalCMB();
		
		//Add components to main panel
		panel.add(wordPanel);
		
		//Add to collapsible panel
		collapsiblePanel.getContentPane().add(panel, BorderLayout.NORTH);
		
		return collapsiblePanel;
	}
	
	/**
	 * Creates a CollapsiblePanel that holds the Cloud Layout information.
	 * @return CollapsiblePanel - cloud Layout panel interface.
	 */
	private CollapsiblePanel createCloudLayoutPanel()
	{
		CollapsiblePanel collapsiblePanel = new CollapsiblePanel("Cloud Layout");
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0,1));
		
		
		JPanel cloudLayoutPanel = new JPanel();
		cloudLayoutPanel.setLayout(new GridBagLayout());

		JLabel cloudStyleLabel = new JLabel("Style: ");
		
		WidestStringComboBoxModel wscbm = new WidestStringComboBoxModel();
		cmbStyle = new JComboBox(wscbm);
		cmbStyle.addPopupMenuListener(new WidestStringComboBoxPopupMenuListener());
		cmbStyle.setEditable(false);
	    Dimension d = cmbStyle.getPreferredSize();
	    cmbStyle.setPreferredSize(new Dimension(15, d.height));
	    cmbStyle.setToolTipText("Visual style for the cloud layout");

		
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.insets = new Insets(5,0,0,0);
		cloudLayoutPanel.add(cloudStyleLabel, gridBagConstraints);
		
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new Insets(5, 10, 0, 0);
		cloudLayoutPanel.add(cmbStyle, gridBagConstraints);
	    
	    buildStyleCMB();
		
		panel.add(cloudLayoutPanel);
		
		collapsiblePanel.getContentPane().add(panel,BorderLayout.NORTH);
		return collapsiblePanel;
	}
	
	
	/**
	 * Utility to create a panel for the buttons at the bottom of the Semantic 
	 * Summary Input Panel.
	 */
	private JPanel createBottomPanel()
	{
		
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		
		//Create buttons
		JButton deleteButton = new JButton("Delete");
		JButton updateButton = new JButton("Update");
		JButton createButton = new JButton("Create");
		
		//Add actions to buttons
		createButton.addActionListener(new CreateCloudAction());
		deleteButton.addActionListener(new DeleteCloudAction());
		updateButton.addActionListener(new UpdateCloudAction());
		
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
		
		//clear current values
		networkLabel.setText("");
		listValues.clear();
		
		//Set new current values
		networkLabel.setText(params.getNetworkName());
		
		//Ensure list is in order of creation
		List<CloudParameters> clouds = new ArrayList<CloudParameters>(params.getClouds().values());
		Collections.sort(clouds);
		
		Iterator<CloudParameters> iter = clouds.iterator();
		while (iter.hasNext())
		{
			CloudParameters curParam = iter.next();
			String curCloud = curParam.getCloudName();
			listValues.addElement(curCloud);
		}
		
		//Update Manager variables
		SemanticSummaryManager.getInstance().setCurNetwork(params);
		SemanticSummaryManager.getInstance().setCurCloud(
				SemanticSummaryManager.getInstance().getNullCloudParameters());
		
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
		cmbAttributes.setSelectedItem(params.getAttributeName());
		maxWordsTextField.setValue(params.getMaxWords());
		clusterCutoffTextField.setValue(params.getClusterCutoff());
		cmbStyle.setSelectedItem(params.getDisplayStyle());
		addWordTextField.setText("");
		
		if (params.equals(SemanticSummaryManager.getInstance().getNullCloudParameters()))
			{
			addWordTextField.setEditable(false);
			addWordButton.setEnabled(false);
			}
		else
		{
			addWordTextField.setEditable(true);
			addWordButton.setEnabled(true);
		}
		
		SemanticSummaryManager.getInstance().setCurCloud(params);
		this.refreshRemovalCMB();
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
		CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
		
		netWeightTextField.setValue(params.getDefaultNetWeight());
		cmbAttributes.setSelectedItem(params.getDefaultAttName());
		maxWordsTextField.setValue(params.getDefaultMaxWords());
		clusterCutoffTextField.setValue(params.getDefaultClusterCutoff());
		cmbStyle.setSelectedItem(params.getDefaultDisplayStyle());
		this.updateUI();
	}
	
	/**
	 * Update the remove word list in the remove combobox.
	 */
	private void updateCMBRemoval()
	{
		DefaultComboBoxModel cmb;
		cmb = ((DefaultComboBoxModel)cmbRemoval.getModel());
		cmb.removeAllElements();
		
		CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
		SemanticSummaryParameters networkParams = params.getNetworkParams();
		WordFilter curFilter = networkParams.getFilter();
		
		//Check if we are dealing with the Null CloudParameters
		Boolean isNull = false;
		if (params.equals(SemanticSummaryManager.getInstance().getNullCloudParameters()))
				isNull = true;
		
		//Added words
		cmb.addElement(addedSeparator);
		
		if (!isNull)
		{	
			//Add alphabetically order list of words
			ArrayList<String> addedList = new ArrayList<String>();
			for(Iterator<String> iter = curFilter.getAddedWords().iterator(); iter.hasNext();)
			{
				String curWord = iter.next();
				addedList.add(curWord);
			}
			
			Collections.sort(addedList);
			for (int i = 0; i < addedList.size(); i++)
			{
				String curWord = addedList.get(i);
				cmb.addElement(curWord);
			}
		}
		
		//Flagged words
		cmb.addElement(flaggedSeparator);
		
		if (!isNull)
		{
			ArrayList<String> flaggedList = new ArrayList<String>();
			for(Iterator<String> iter = curFilter.getFlaggedWords().iterator(); iter.hasNext();)
			{
				String curWord = iter.next();
				flaggedList.add(curWord);
			}
			
			Collections.sort(flaggedList);
			for (int i = 0; i < flaggedList.size(); i++)
			{
				String curWord = flaggedList.get(i);
				cmb.addElement(curWord);
			}
		}
		
		//Stop words
		cmb.addElement(stopSeparator);
		
		if (!isNull)
		{
			ArrayList<String> stopList = new ArrayList<String>();
			for(Iterator<String> iter = curFilter.getStopWords().iterator(); iter.hasNext();)
			{
				String curWord = iter.next();
				stopList.add(curWord);
			}
			
			Collections.sort(stopList);
			for (int i = 0; i < stopList.size(); i++)
			{
				String curWord = stopList.get(i);
				cmb.addElement(curWord);
			}
		}
	}
	
	/**
	 * Refreshes the list of words that can be removed from the exclusion list.
	 */
	public void refreshRemovalCMB()
	{
		updateCMBRemoval();
		cmbRemoval.repaint();
	}
	
	
	/**
	 * Update the attribute list in the attribute combobox.
	 */
	private void updateCMBAttributes()
	{
		DefaultComboBoxModel cmb;
		
		cmb = ((DefaultComboBoxModel)cmbAttributes.getModel());
		cmb.removeAllElements();
		cmb.addElement(SemanticSummaryManager.getInstance().
				getNullCloudParameters().getDefaultAttName());
		
		Vector<Object>av;
		
		av = getCyAttributesList("node");
		for (int i=0; i < av.size(); i++)
		{
			cmb.addElement(av.elementAt(i));
		}
	}
	
	
	/**
	 * Refreshes the list of attributes
	 */
	public void refreshAttributeCMB()
	{
		updateCMBAttributes();
		CloudParameters curCloud = SemanticSummaryManager.getInstance().getCurCloud();
		String curAttribute;
		if (curCloud == SemanticSummaryManager.getInstance().getNullCloudParameters())
			curAttribute = curCloud.getDefaultAttName();
		else
			curAttribute = curCloud.getAttributeName();
		
		cmbAttributes.setSelectedItem(curAttribute);
		cmbAttributes.repaint();
	}
	
	/**
	 * Builds the combo box of cloud style choices
	 */
	private void buildStyleCMB()
	{
		DefaultComboBoxModel cmb;
		
		/*
		cmb = ((DefaultComboBoxModel)cmbStyle.getModel());
		cmb.removeAllElements();
		cmb.addElement(SemanticSummaryManager.getInstance().getNullCloudParameters().getDefaultDisplayStyle());
		cmb.addElement("Gray Boxes");
		cmb.addElement("No Clustering");
		cmbStyle.setSelectedItem(SemanticSummaryManager.getInstance().getNullCloudParameters().getDefaultDisplayStyle());
		cmbStyle.repaint();
		*/
		cmb = ((DefaultComboBoxModel)cmbStyle.getModel());
		cmb.removeAllElements();
		cmb.addElement(CloudDisplayStyles.CLUSTERED_STANDARD);
		cmb.addElement(CloudDisplayStyles.CLUSTERED_BOXES);
		cmb.addElement(CloudDisplayStyles.NO_CLUSTERING);
		cmbStyle.setSelectedItem(CloudDisplayStyles.DEFAULT_STYLE);
		cmbStyle.repaint();
	}
	
	/*
	 * Get the list of attribute names for either "node" or "edge". The attribute names will be
	 * prefixed either with "node." or "edge.". Those attributes whose data type is not
	 * "String" will be excluded
	 */
	private Vector<Object> getCyAttributesList(String pType) {
		Vector<String> attributeList = new Vector<String>();
		CyAttributes attributes = null;
		
		if (pType.equalsIgnoreCase("node")) {
			attributes = Cytoscape.getNodeAttributes();
			
		}
		else if (pType.equalsIgnoreCase("edge")){
			attributes = Cytoscape.getEdgeAttributes();			
		}
				
		String[] attributeNames = attributes.getAttributeNames();

		if (attributeNames != null) {
			//  Show all attributes, with type of String or Number
			for (int i = 0; i < attributeNames.length; i++) {
				int type = attributes.getType(attributeNames[i]);
				
				//  only show user visible attributes,with type = String
				if (!attributes.getUserVisible(attributeNames[i])) {
					continue;
				}
				
				// Filter out vizmap attributes
				if (attributeNames[i].contains("vizmap"))
					continue;
				
				if (type == CyAttributes.TYPE_STRING) {
					attributeList.add(attributeNames[i]);
				}
				else if (type == CyAttributes.TYPE_SIMPLE_LIST)
				{
					attributeList.add(attributeNames[i]);
				}
			} //for loop
		
			//  Alphabetical sort
			Collections.sort(attributeList);
		}

		// type conversion
		Vector<Object> retList = new Vector<Object>();

		for (int i=0; i<attributeList.size(); i++) {
			retList.add(attributeList.elementAt(i));
		}
		return retList;
	}

	/**
	 * Handles the activation of the add/remove buttons for the word
	 * exclusion list.
	 * @param ItemEvent - event that triggered this reaction.
	 */
	public void itemStateChanged(ItemEvent e)
	{
		Object source = e.getSource();
		
		if (source instanceof JComboBox)
		{
			JComboBox cmb = (JComboBox) source;
			if (cmb == cmbRemoval)
			{
				Object selectObject = cmbRemoval.getSelectedItem();
				if (selectObject != null)
				{
					String selectItem = selectObject.toString();
					if (selectItem.equalsIgnoreCase(addedSeparator) || 
							selectItem.equalsIgnoreCase(flaggedSeparator) ||
							selectItem.equalsIgnoreCase(stopSeparator))
						removeWordButton.setEnabled(false);
						else
							removeWordButton.setEnabled(true);
				}//end if not null
			}//end if cmbRemoval
		}//end if combo box
	}
	
	/**
	 * Handles button presses in the Input Panel.
	 * @param ActionEvent - event that triggered this method.
	 */
	public void actionPerformed(ActionEvent e)
	{
		Object _actionObject = e.getSource();
		
		//Handle button events
		if (_actionObject instanceof JButton)
		{
			JButton _btn = (JButton)_actionObject;
			
			if (_btn == removeWordButton)
			{
				//Get Selected word
				Object selectObject = cmbRemoval.getSelectedItem();
				if (selectObject != null)
				{
					String selectItem = selectObject.toString();
					if (!selectItem.equalsIgnoreCase(addedSeparator) || 
							!selectItem.equalsIgnoreCase(flaggedSeparator) ||
							!selectItem.equalsIgnoreCase(stopSeparator))
					{
						CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
						SemanticSummaryParameters networkParams = params.getNetworkParams();
						WordFilter curFilter = networkParams.getFilter();
						
						//Remove from filter
						curFilter.remove(selectItem);
						
						//Reset Flags
						params.setCountInitialized(false);
						params.setSelInitialized(false);
						params.setRatiosInitialized(false);
						
						//Refresh word removal list
						this.refreshRemovalCMB();
					}//end if appropriate selection
				}//end if not null selected
			}//end remove word button
			
			if (_btn == addWordButton)
			{
				String value = (String)addWordTextField.getText();
				if (value.matches(""))
					return;
				
				else if (value.matches("[\\w]*"))
				{ 
					//add value to cloud parameters filter and update
					CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
					SemanticSummaryParameters networkParams = params.getNetworkParams();
					WordFilter curFilter = networkParams.getFilter();
					value.toLowerCase();
					curFilter.add(value);
					
					//Reset flags
					params.setCountInitialized(false);
					params.setSelInitialized(false);
					params.setRatiosInitialized(false);
					
					//Refresh view
					this.refreshRemovalCMB();
					addWordTextField.setText(null);
				}
				else
				{
					addWordTextField.setSelectionStart(0);
					addWordTextField.setSelectionEnd(value.length());
					addWordTextField.updateUI();
					String message = "You can only add a word that contains letters or numbers and no spaces";
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
				}
			}
		}//end button	
	}
	
	
	
	//Getters and Setters
	public JFormattedTextField getNetWeightTextField()
	{
		return netWeightTextField;
	}
	
	public JFormattedTextField getMaxWordsTextField()
	{
		return maxWordsTextField;
	}
	
	public JFormattedTextField getClusterCutoffTextField()
	{
		return clusterCutoffTextField;
	}

	public JTextField getAddWordTextField()
	{
		return addWordTextField;
	}

	public JButton getAddWordButton()
	{
		return addWordButton;
	}
	
	public JLabel getNetworkLabel()
	{
		return networkLabel;
	}
	
	public DefaultListModel getListValues()
	{
		return listValues;
	}
	
	public JList getCloudList()
	{
		return cloudList;
	}
	
	public JComboBox getCMBAttributes()
	{
		return cmbAttributes;
	}
	
	public JComboBox getCMBRemoval()
	{
		return cmbRemoval;
	}
	
	public JComboBox getCMBStyle()
	{
		return cmbStyle;
	}
	
	
	public CloudListSelectionHandler getCloudListSelectionHandler()
	{
		return handler;
	}
	
	
	/**
	 * Private Class to ensure that text fields are being set properly
	 */
	private class FormattedTextFieldAction implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent e)
		{
			JFormattedTextField source = (JFormattedTextField) e.getSource();
			
			CloudParameters params = SemanticSummaryManager.getInstance().getCurCloud();
			
			String message = "The value you have entered is invalid. \n";
			boolean invalid = false;
			
			//Net Weight Text Field
			if (source == netWeightTextField)
			{
				Number value = (Number) netWeightTextField.getValue();
				if ((value != null) && (value.doubleValue() >= 0.0) && (value.doubleValue() <= 1))
				{
					//All is well - leave it be
				}
				else
				{
					Double defaultNetWeight = params.getDefaultNetWeight();
					netWeightTextField.setValue(defaultNetWeight);
					message += "The network weight factor must be greater than or equal to 0 and less than or equal to 1";
					invalid = true;
				}
			}// end Net Weight Factor
			
			//Max Words
			else if (source == maxWordsTextField)
			{
				Number value = (Number) maxWordsTextField.getValue();
				if ((value != null) && (value.intValue() >= 0))
				{
					//All is well - do nothing
				}
				else
				{
					Integer defaultMaxWords = params.getDefaultMaxWords();
					maxWordsTextField.setValue(defaultMaxWords);
					message += "The maximum number of words to display must be greater than or equal to 0.";
					invalid = true;
				}
			}// end max Words
			
			else if (source == clusterCutoffTextField)
			{
				Number value = (Number) clusterCutoffTextField.getValue();
				if ((value != null) && (value.doubleValue() >= 0.0))
				{
					//All is well - leave it be
				}
				else
				{
					Double defaultClusterCutoff = params.getDefaultClusterCutoff();
					clusterCutoffTextField.setValue(defaultClusterCutoff);
					message += "The cluster cutoff must be greater than or equal to 0";
					invalid = true;
				}
			}
			
			else if (source == addWordTextField)
			{
				String value = (String)addWordTextField.getText();
				if (value.equals("") || value.matches("[\\w]*"))
				{ 
					//All is well, leave it be
				}
				else
				{
					//addWordTextField.setValue("");
					//message += "You can only add a word that contains letters or numbers and no spaces";
					//invalid = true;
				}
			}
			
			if (invalid)
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, "Parameter out of bounds", JOptionPane.WARNING_MESSAGE);
		}
	}
}
