/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.browsing;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BrowserOptionPanel extends AbstractModulePanel {
	BrowserManager browserManager=null;
	private JCheckBox allNamespaceConditionCheckBox=null;
	private JCheckBox collapseDatatypesCheckBox=null;
	//private JCheckBox propagateURI=null;
	//private JCheckBox ignoreBlankNodes=null;
	JCheckBox useLabelsCheckBox=null;
	JCheckBox splitPropertiesCheckBox=null;
	JList splitPropertiesList=null;
	JTextArea splitPropertyArea=null;
	//JButton addPropertyButton=null;
	//JButton delPropertyButton=null;
	//JButton commitPropertyButton=null;
	JTextArea splitPropertiesTextArea=null;
	JButton	splitPropertiesButton=null;
	/**
	 * 
	 */
	public BrowserOptionPanel(BrowserManager bm) {
		super();
		browserManager=bm;
		
		redLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		yellowLightText="<html>This should not happen! You are experiencing problems here... please report!</html>";
		greenLightText= "<html>You can always specify browsing options</html>";
		myTabText="Representation of ontologies in Cytoscape";
		myTabTooltip="<html>Specify features of the representation of ontologies in Cytoscape (see Help for details)</html>";
		
		
		allNamespaceConditionCheckBox=new JCheckBox("Namespace selection in and",browserManager.isNamespaceSelectionInAnd());
		collapseDatatypesCheckBox=new JCheckBox("Treat datatypes as attributes",browserManager.isCollapseAttributesTrue());
		//propagateURI=new JCheckBox("Add URI as an attribute");
		//ignoreBlankNodes=new JCheckBox("ignore blank nodes in namespace conditions");
		
		useLabelsCheckBox=new JCheckBox("Use labels instead of URIs");
		splitPropertiesCheckBox=new JCheckBox("Treat some URI as multiple nodes");
		splitPropertiesTextArea=new JTextArea(20,60);
		JScrollPane textScrollPane=new JScrollPane(splitPropertiesTextArea);
		//textScrollPane.setSize(600,300);
		splitPropertiesButton=new JButton("Commit");
		
		allNamespaceConditionCheckBox.setToolTipText("If on, when extending a network, both properties and objects must be in selected namespaces. If off, onw of the two will suffice.");
		collapseDatatypesCheckBox.setToolTipText("If on, datatype properties will be treated as attributes in a raph representation.");
		//ignoreBlankNodes.setToolTipText("blank nodes counts as part of an unselected namespace");
		
		
		
		allNamespaceConditionCheckBox.addActionListener(new allNamespaceConditionBoxListener());
		collapseDatatypesCheckBox.addActionListener(new collapseDatatypesCheckBoxListener());
		useLabelsCheckBox.addActionListener(new useLabelsCheckBoxListener());
		//propagateURI.addActionListener(new propagateURIListener());
		splitPropertiesCheckBox.addActionListener(new splitCheckBoxListener());
		splitPropertiesButton.addActionListener(new splitPropertiesButtonListener());
		
		//splitPropertiesList=new JList();
		splitPropertyArea=new JTextArea();
		
		//addPropertyButton=new JButton("Add");
		//delPropertyButton=new JButton("Delete");
		//commitPropertyButton=new JButton("Commit");
		
		add(allNamespaceConditionCheckBox);
		add(collapseDatatypesCheckBox);
		add(useLabelsCheckBox);
		add(splitPropertiesCheckBox);
		add(textScrollPane);
		add(splitPropertiesButton);
		
		/**
		 * note: this will change when we'll read settings from a file.
		 */
		splitPropertiesButton.setEnabled(false);
		splitPropertiesTextArea.setEnabled(false);
		
		//add(propagateURI);
	}
	
	public void refreshAfterKnowledgeChange() {
		if(!browserManager.canCollapseDatatypes()) collapseDatatypesCheckBox.setEnabled(false);
		if(!browserManager.canExtend()) allNamespaceConditionCheckBox.setEnabled(false);
		if(!browserManager.canHandleLabels()) useLabelsCheckBox.setEnabled(false);
		if(!browserManager.canHandleSplit()) {
			splitPropertiesCheckBox.setEnabled(false);
			splitPropertiesTextArea.setEnabled(false);
		}
	}
	
	private class allNamespaceConditionBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			browserManager.setNamespaceSelectionInAnd(allNamespaceConditionCheckBox.isSelected());
			
		}
		
	}
	
	private class collapseDatatypesCheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			browserManager.setCollapseAttributes(collapseDatatypesCheckBox.isSelected());
			
		}
		
	}
	/*
	private class propagateURIListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			browserManager.setPropagateURI(true);
			
		}
		
	}
	*/
	private class useLabelsCheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			browserManager.setUseLabels(useLabelsCheckBox.isSelected());
			
		}
		
	}
	private class splitCheckBoxListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			splitPropertiesButton.setEnabled(splitPropertiesCheckBox.isSelected());
			splitPropertiesTextArea.setEnabled(splitPropertiesCheckBox.isSelected());
			browserManager.setEnableSplit(splitPropertiesCheckBox.isSelected());
		}
		
	}
	private class splitPropertiesButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			browserManager.setSplitConditions(splitPropertiesTextArea.getText());
			
		}
		
	}
	
	public void setSplitConditions(String text) {
		splitPropertiesTextArea.setText(text);
	}

	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToTuneVisualizationOptions";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		JLabel tempLabel=new JLabel("" +
		"<html>When browsing an ontology in a cytoscape network, visible alternatives are determined on the basis of which" +
		"namespaces have been selected as <i>visible</i>.By enabling this option, only statements " +
		"where <i>both the predicate and the object (or subject)</i> are in selected namespaces will be presented. <br>" +
		"</html>",
		Utilities.getHelpIcon("namespaceSelectionInAnd.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html><br>" +
				"When this option is selected, when a resource is selected all of its datatype properties (for which a value is asserted) are added to the node as attributes.<br>" +
				"Note that datatypes properties are anyway presented while browsing.</html>",
				Utilities.getHelpIcon("treatDatatypesAsAttributes.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		tempLabel=new JLabel(
				"<html> Add the object of the properties rddf:label as a label, instead of the URI of the node" +
				"</html>",
				Utilities.getHelpIcon("useLabelsInsteadOfURIs.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel("<html>It is possible to specifiy conditions under which some URIs should be visualized with a distinct node each time they are added to the network." +
				"The most notable application of this is the visualization of currency metabolites.<br>" +
				"To specify this URIs, an RDQL query (for more infos refer to the help pages available on the web) yielding the results that should follow this behaviour must be specified.<br>" +
				"This query will act after the checkbox is selected one more time (activate checkbox, input query, <b>commit</b> query, de-activate and re-activate the option).<br>" +
				"This functinality is meant to be a-priori implemented in an analysis context.</html>",
				Utilities.getHelpIcon("treatSomeURIsAsMultipleNodes.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		
		
		return help;
	}

	public String getPanelName() {
		return "Browsing options";
	}

	public int getStatusLevel() {
		return 3;
	}

	
	public void refresh() {
		// TODO nothing to do here
		
	}

	
}
