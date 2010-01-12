/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.patterns;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;


/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternManagerPanel extends AbstractModulePanel{
	PatternManager selectionManager =null;
	JTabbedPane mySelections=null;
	JButton takeButton=null;
	JButton deleteButton=null;
	JButton addButton=null;
	JButton searchButton=null;
	/**
	 * 
	 */
	public PatternManagerPanel(PatternManager sm) {
		super();
		selectionManager=sm;
		setLayout(new BorderLayout());
		mySelections=new JTabbedPane();
		add(mySelections,BorderLayout.CENTER);
		
		redLightText="<html>No ontologies are present and correctly loaded</html>";
		yellowLightText="<html>Ontologies are present, but no pattern is defined yet</html>";
		greenLightText= "<html>Ontologies are present and "+selectionManager.getNumberOfPatterns()+"patterns are defined</html>";
		myTabText="Pattern library";
		myTabTooltip="<html>List of patterns defined, for a each patterns, is a set of features is available (see help)</html>";
		
		//JPanel buttonPanel=new JPanel(new GridLayout(1,4));
		//takeButton=new JButton("Take selection");
		//deleteButton=new JButton("Delete");
		//addButton=new JButton("Add");
		//searchButton=new JButton("Search");
		
		//takeButton.addActionListener(new TakeActionListener());
		//deleteButton.addActionListener(new DeleteActionListener());
		//addButton.addActionListener(new AddActionListener());
		//searchButton.addActionListener(new SearchActionListener());
		
		//buttonPanel.add(takeButton);
		//buttonPanel.add(deleteButton);
		//buttonPanel.add(addButton);
		//buttonPanel.add(searchButton);
		//add(buttonPanel,BorderLayout.SOUTH);
		
	}
	public void addPatternElementPanel(PatternElement pe) {
		JPanel patternElementPanel=pe.getPanel();
		mySelections.add(patternElementPanel,pe.getName());
		validate();
	}
	public void removePatternElementPanel(PatternElement pe) {
		mySelections.remove(pe.getPanel());
		validate();
	}
	public void removeAll() {
		mySelections.removeAll();
	}
	
	
	/*
	public class TakeActionListener implements ActionListener{

		public void actionPerformed(ActionEvent arg0) {
			String selectionName=JOptionPane.showInputDialog(this,"Please eneter selection name");
			PatternElement selElement=selectionManager.makeCurrentSelection(selectionName);
			if(selElement==null) return;
			JPanel tempPain=new JPanel();
			tempPain.setLayout(new BorderLayout());
			
			tempPain.add(new JLabel(new ImageIcon(selElement.getImage())));
			
			mySelections.add(selectionName,tempPain);
			
		}
		
	}*/
	/*
	public class DeleteActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			int index=mySelections.getSelectedIndex();
			String selection=mySelections.getTitleAt(index);
			mySelections.removeTabAt(index);
			//deleteSelection(selection);
		}
		
	}
	*/
	/*
	public class AddActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			int index=mySelections.getSelectedIndex();
			String selection=mySelections.getTitleAt(index);
			PatternElement mySelElement=selectionManager.getPatternElement(selection);
			selectionManager.myRDFScapeInstance.getCytoMapper().mapNodes(mySelElement.getList(),false);
		}
		
	}*/
	/*
	public class SearchActionListener implements ActionListener {

		public void actionPerformed(ActionEvent arg0) {
			int index=mySelections.getSelectedIndex();
			String selection=mySelections.getTitleAt(index);
			PatternElement mySelElement=selectionManager.getPatternElement(selection);
			selectionManager.myRDFScapeInstance.getCytoMapper().mapNodes(mySelElement.getList(),true);
		}
		
	}*/
	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToWorkWithPatterns";
	}

	public JPanel getHelpPanel() {
	JPanel help=new JPanel();
		
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		JLabel tempLabel=new JLabel("<html>In the pattern library each pattern is presented with a proprt sub-panel. <br>" +
				"Entering a subpanel present all information, and commands, relative to its pattern.<br>" +
				"In detail, each pattern panel presents thre panels: <b>Graph</b>, <b>Pattern</b>, <b>Matches</b> and a button bar</html>",
				Utilities.getHelpIcon("patternList.png"),
				SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(20,20)));
		
		tempLabel=new JLabel(Utilities.getHelpIcon("patternGraphView.png"));
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		tempLabel=new JLabel("<html>A graphical representation of the pattern</html>");
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,50));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(Utilities.getHelpIcon("patternStringView.png"));
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
		help.add(tempLabel);
		tempLabel=new JLabel("<html>A representation of the pattern as triplets.</html>");
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,50));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(Utilities.getHelpIcon("patternExpandedView.png"));
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		tempLabel=new JLabel("<html>A presentation of all the triplets that matched this pattern in the knowledge base. This patternm is available after a search or plot operation is performed.</html>");
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,50));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		

		tempLabel=new JLabel("<html>These commands are active on a pattern:" +
				"<ul>" +
				"<li><b>Search variables</b> search all variables matching this pattern in the graph</li>" +
				"<li><b>Add variables</b> add all variables matching this pattern to the graph</li>" +
				"<li><b>Search patterns</b> search all matchings of this pattern in the graph (not only the variables in the pattern)</li>" +
				"<li><b>Add patterns</b> adds all the matchings of this pattern to the graph (not only the variables in the pattern)</li>" +
				"<li><b>Delete pattern</b> deletes this pattern</li>" +
				"</ul></html>",
				Utilities.getHelpIcon("patternButtons.png"),
				SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,100));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
	
		
		return help;
	}

	public String getPanelName() {
		return "Help on Patterns";
	}

	public int getStatusLevel() {
		if(!RDFScape.getOntologyManager().ontologiesPresentAndCorrectlyLoaded()) return 1;
		else {
			if(selectionManager.getNumberOfPatterns()==0) return 2;
			else return 3;
		}
		
	}
	
	public void refresh() {
		// nothing to to here....
		
	}
	
}
