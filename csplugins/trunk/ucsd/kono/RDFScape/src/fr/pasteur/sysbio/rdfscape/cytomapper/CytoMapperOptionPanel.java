/**
 * Copyright 2006-2007 Andrea Splendiani
 * Released under GPL license
 */


package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;


/**
 * @author andrea
 * This Panel presents a set of subpanel containing individual "mapping rules". 
 * It additionally allows the user to test a mapping and verify the results
 */

public class CytoMapperOptionPanel extends AbstractModulePanel{
	private CytoMapper cytoMapper=null;
	private JPanel rulePanel=null;
	private JTable myResultTable=null;
	private JTabbedPane ruleTextPanels=null;
	private JLabel statLabel=new JLabel();
	
	private JButton addButton=null;
	private JButton delButton=null;
	private JButton resolveButton=null;
	//private JButton mergeButton=null;
	
	
	//int totalURIs=0;
	//int totalIDs=0;
	//int clashes=0;
	//int multi=0;
	/**
	 * 
	 */
	public CytoMapperOptionPanel(CytoMapper ct) {
		super();
		cytoMapper=ct;
		
		redLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		yellowLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		greenLightText= "<html>You can always specify mapping options</html>";
		myTabText="Mapping ontologies on Cytoscape networks";
		myTabTooltip="<html>Configure the mappings between ontologies and other networks in Cytoscape (see Help for details)</html>";
		
		setLayout(new BorderLayout());
		JTabbedPane myTopLevelTabbedPane=new JTabbedPane();
		
		rulePanel=new JPanel(new BorderLayout());
		
		myResultTable=new JTable(cytoMapper.mapperTable);
		JScrollPane resultPanel=new JScrollPane(myResultTable);
		ruleTextPanels=new JTabbedPane();
		
		
		JPanel buttonPanel=new JPanel(new GridLayout(1,4));
		addButton=new JButton("New");
		delButton=new JButton("Del");
		resolveButton=new JButton("Resolve");
		//mergeButton=new JButton("Merge");
		
		buttonPanel.add(addButton);
		buttonPanel.add(delButton);
		
		buttonPanel.add(resolveButton);
		//buttonPanel.add(mergeButton);
		
		
		rulePanel.add(ruleTextPanels,BorderLayout.CENTER);
		rulePanel.add(buttonPanel,BorderLayout.SOUTH);
		
		myTopLevelTabbedPane.add(rulePanel,"Rules");
		myTopLevelTabbedPane.add(resultPanel,"Results");
		
		ActionListener addButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String name=JOptionPane.showInputDialog("Insert rule name");
				if(name==null) return;
				MappingRule myRule=new MappingRule();
				myRule.name=name;
				cytoMapper.addMappingRule(myRule);
				
			}
			
		};
		
		addButton.addActionListener(addButtonListener);
		
		ActionListener delButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(ruleTextPanels.getSelectedIndex()>=0) {
					cytoMapper.mappingRuleList.remove(ruleTextPanels.getTitleAt(ruleTextPanels.getSelectedIndex()));
					ruleTextPanels.remove(ruleTextPanels.getSelectedComponent());
				}
			}
			
		};
		delButton.addActionListener(delButtonListener);
		
		ActionListener resolveButtonListener = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cytoMapper.resolve();
				showStats();
			}
			
		};
		resolveButton.addActionListener(resolveButtonListener);
		
		add(myTopLevelTabbedPane,BorderLayout.CENTER);
		add(statLabel,BorderLayout.NORTH);
		showStats();
	}

	
	private void showStats() {
		remove(statLabel);
		statLabel=new JLabel("URIs: "+cytoMapper.totalURIs+"              IDs: "+cytoMapper.totalIDs+"              Conflicts: "+cytoMapper.clashes+"             Multi: "+cytoMapper.multi);
		add(statLabel,BorderLayout.NORTH);
		validate();
	}


	/**
	 * @param mappingRule
	 */
	/*
	public void addMappingRule(MappingRule mappingRule) {
		ruleTextPanels.add(mappingRule.getPanel(),mappingRule.name);
		
	}*/
	public void refresh() {
		reset();
		Enumeration myRules=cytoMapper.mappingRuleList.elements();
		while(myRules.hasMoreElements()) {
			MappingRule tempRule=(MappingRule)myRules.nextElement();
			ruleTextPanels.add(tempRule.getPanel(),tempRule.name);
		}
	}
	
	public void reset() {
	
		Component[] myComponents=ruleTextPanels.getComponents();
		for (int i = 0; i < myComponents.length; i++) {
			ruleTextPanels.remove(myComponents[i]);
		}
		
	}


	public void resetAfterKnowledgeBaseChange() {
		if(cytoMapper.canResolve()) {
			resolveButton.setEnabled(true);
		}
		else {
			resolveButton.setEnabled(false);
		}
		
	}
	

	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/LinkingGraphsInCytoscapeAndOntologies";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		JLabel tempLabel=null;
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		tempLabel=new JLabel(
				"<html>Mapping between ontologies and networks in Cytoscape is specified through a set of <i>query bodies</i> or <i>patterns</i>.<br>" +
				"For each pattern, a tab is present. Swithcing to <b>Results</b> shows all mappings currently possible. That is, alla association between URIs in the ontology" +
				"and IDs in Cytoscape. These are potential IDs. They are not necessarily representeed in the current network.<br>" +
				"<b>Note that mapping is defined for nodes only!</b></html>",
				Utilities.getHelpIcon("cytoMapperOptionTabs.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		
		tempLabel=new JLabel(Utilities.getHelpIcon("cytoMapperRuleBody.png"));
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		tempLabel=new JLabel(
				"<html>The specification of a <i>Pattern</i>. All this patterns are composed in a query to the ontology that is evaluated in OR." +
				"Within the result of this query associations between URIs and object of datatype properties corresponding to IDs in Cytoscape is found.<br>" +
				"Which element in the result is the URI, and which the literal corresponding to the Cytoscape ID, can be indicated in  the boxes <b>URI</b> and <b>ID</b>.<br>" +
				"<b>Commit</b> records the rule in memory. It does not execute it (use <b>Resolve</b>), neither store it permanently (refer to <b>Analysis contexts</b>)." +
				"</html>"
		);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		
		tempLabel=new JLabel(
				"<html>Add a new mapping pattern.</html>",
				Utilities.getHelpIcon("newCytoMapperButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>Deletes the mapping pattern currently displayed.</html>",
				Utilities.getHelpIcon("delCytoMapperButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>Applies all patterns (in OR) to generate correspondences between URIs and possible IDs.<br>" +
				"Results of this operation are displayed in the <b>Results</b> panel.<br>" +
				"This action enables <b>Map</b> in the <b>Map ontologies to my network</b> panel. </html>",
				Utilities.getHelpIcon("cytoResolveButton.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(Utilities.getHelpIcon("cytoMapperResolveResults.png"));
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,50));
		help.add(tempLabel);
		tempLabel=new JLabel(
				"<html><ul>" +
				"<li><b>URIs:</b> The total number of URIs identified by the specified patterns</li>"+
				"<li><b>IDs:</b> The total number of IDs identified by the specified patterns</li>"+
				"<li><b>Conflicts:</b> The number of cases in which two URIs can be attributes to the same ID. Note that this situation should not conceptually happen (each node is assumed to represent one concept in the ontology). The ontology should be augmented accordingly (through a proper mapping file). No garantee are done on the result: only one of the possible URIs chosed randomly will be assigne to the node.</li>"+
				"<li><b>Multi:</b> The number of URIs that have multiple Identifier. This is possible, but should not be the standard consition in which analysis of microarray data will be applied.</li></ul> </html>"
		);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		return help;
	}

	public String getPanelName() {
		return "Mapping to Cytoscape Options";
	}

	public int getStatusLevel() {
		return 3;
	}
}
