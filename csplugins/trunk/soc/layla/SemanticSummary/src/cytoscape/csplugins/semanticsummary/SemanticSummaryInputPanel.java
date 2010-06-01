/*
 File: SemanticSummaryInputPanel.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.csplugins.semanticsummary;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

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
	//TODO
	//Collapsible panels
	//CollapsiblePanel parameters;
	//CollapsiblePanel semanticAnalysis;
	//CollapsiblePanel displaySettings;
	//CollapsiblePanel exclusionList;
	
	//Parameters
	private SemanticSummaryParameters networkParams;
	private CloudParameters cloudParams;
	
	//Radio Buttons
	//private JRadioButton nodeName;
	//private JRadioButton nodeAttribute;
	
	//Text Fields
	private JFormattedTextField maxWordsTextField;
	
	//instruction text
	//TODO
	
	//tool tips
	//TODO
	
	
	//CONSTRUCTORS
	public SemanticSummaryInputPanel()
	{
	//TODO	
		setLayout(new BorderLayout());
		
		//INITIALIZE PARAMETERS
		
		//Create the three main panels: CloudList, Options, and Bottom
		
		// Put the CloudList in a scroll pane
		//JPanel cloudList = createCloudListPanel();
		//JScrollPane cloudListScroll = new JScrollPane(cloudList);
		
		//Put the Options in a scroll pane
		CollapsiblePanel optionsPanel = createOptionsPanel();
		JScrollPane optionsScroll = new JScrollPane(optionsPanel);
		
		//Add button to bottom
		JPanel bottomPanel = createBottomPanel();
		
		//Add all the vertically aligned components to the main panel
		//add(cloudListScroll,BorderLayout.NORTH);
		add(optionsScroll,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
	
	}
	
	//METHODS
	//TODO - add any button listener methods necessary
	
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
		JRadioButton attribute = new JRadioButton("Attribute");
		
		panel.add(name);
		panel.add(attribute);
		
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
		
		//Add components to main panel
		panel.add(maxWordsPanel);
		
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
		
		JButton deleteButton = new JButton("Delete");
		JButton updateButton = new JButton("Update");
		JButton createButton = new JButton("Create");
		
		panel.add(deleteButton);
		panel.add(updateButton);
		panel.add(createButton);
		
		return panel;
	}
}
