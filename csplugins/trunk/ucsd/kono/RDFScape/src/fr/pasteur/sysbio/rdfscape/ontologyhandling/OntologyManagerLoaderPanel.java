/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 5, 2005
 *
 */
package fr.pasteur.sysbio.rdfscape.ontologyhandling;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;





/**
 * @author andrea@pasteur.fr
 *
 *
 */
public class OntologyManagerLoaderPanel extends AbstractModulePanel{
	OntologyLoaderManager ontologyManager=null;
	JButton loadButton=null;
	JButton addOntologyButton=null;
	JButton deleteOntologyButton=null;
	JTable ontologyTable=null;
	
	
	/**
	 * 
	 */
	public OntologyManagerLoaderPanel(OntologyLoaderManager om) {
		super();
		
		
		ontologyManager=om;
		
		redLightText="<html>You should use correct URLs of ontologies</html>";
		yellowLightText="<html>You should load some ontology at this point</html>";
		greenLightText= "<html>Ontologies loaded with no problems</html>";
		myTabText="2) Load ontologies";
		myTabTooltip="<html>Load your ontologies (or change/create their list)</html>";
		
		addOntologyButton=new JButton("add");
		deleteOntologyButton=new JButton("delete");
		loadButton=new JButton("load ontologies");
		
		
		
		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		buttonPanel.add(addOntologyButton,BorderLayout.WEST);
		buttonPanel.add(deleteOntologyButton,BorderLayout.EAST);
		buttonPanel.add(loadButton,BorderLayout.CENTER);
		
		addOntologyButton.addActionListener(new AddOntologyButtonListener());
		deleteOntologyButton.addActionListener(new DeleteOntologyButtonListener());
		loadButton.addActionListener(new LoadButtonListener());
		
		ontologyTable=new JTable(ontologyManager);
		JScrollPane myScroll=new JScrollPane(ontologyTable);
		
		setLayout(new BorderLayout());
		add(myScroll,BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
		TableColumn colorColumn=ontologyTable.getColumnModel().getColumn(1);
		colorColumn.setCellRenderer(new StatusCellRenderer());
		
	}
	
	private class AddOntologyButtonListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			JOptionPane myOptionPane=new JOptionPane();
			String myChoice=JOptionPane.showInputDialog("Enter URL for ontology (http:// or file:// must be prefixed","");
			ontologyManager.addURL(myChoice);
		}
		
	
		
	}
	private class DeleteOntologyButtonListener implements ActionListener {
		private int[] toDelete=null;
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			toDelete=ontologyTable.getSelectedRows();
			for (int i = 0; i < toDelete.length; i++) {
				ontologyManager.deleteOntology(toDelete[i]);
			}
			
		}
		
	}
	private class LoadButtonListener implements ActionListener {

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			ontologyManager.loadOntologiesFromScratch();
			
		}
		
		
	}
	
	private class StatusCellRenderer extends JLabel implements TableCellRenderer {
		public StatusCellRenderer() {
			setOpaque(true);
			setBackground(Color.WHITE);
		}
		/* (non-Javadoc)
		 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
			String myvalue=(String)arg1;
			setText(myvalue);
			if(myvalue.startsWith("Loading")) setBackground(Color.YELLOW);
			else if(myvalue.startsWith("Read ")) setBackground(Color.GREEN);
			else if(myvalue.startsWith("ERROR")) setBackground(Color.RED);
			else setBackground(Color.WHITE);
			
			
			return this;
		}

		
		
		
	}
	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/HowToLoadOntologies";
	}

	public JPanel getHelpPanel() {	
	JPanel help=new JPanel();
	
	help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
	
	JLabel tempLabel=new JLabel("" +
			"<html>This list shows all ontologies present in the system. You can alter this list with the <b>add</b> and <b>delete</b> button <br>" +
			"Once <b>load ontologies</b> is issued, the oucome of the loading operation is reported with a message and an error code.</html>",
			Utilities.getHelpIcon("ontologyLoaderStatusButton.png"),SwingConstants.LEFT);
	tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
	help.add(tempLabel);
	help.add(Box.createRigidArea(new Dimension(10,10)));
	
	tempLabel=new JLabel(
			"<html>Adds a new ontology to the list. A form will be povided to enter the URL of the ontology. This can be a web accessible URL (http:// ) or a URL relative to the local file system (file://)",
			Utilities.getHelpIcon("addontologyButton.png"),SwingConstants.LEFT);
	tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
	help.add(tempLabel);
	help.add(Box.createRigidArea(new Dimension(10,10)));
	
	tempLabel=new JLabel("<html>Deletes the ontology currently selected in the list.<br>" +
			"Only one ontology at a time can be deleted, and ontologies succefully loaded cannot be deleted (reset is anyway possible through the analysis settings panel)</html>",Utilities.getHelpIcon("deleteOntologyButton.png"),SwingConstants.LEFT);
	tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
	help.add(tempLabel);
	help.add(Box.createRigidArea(new Dimension(10,10)));
	
	tempLabel=new JLabel("<html>Issues loading of ontologies (this may take some time)</html>",Utilities.getHelpIcon("loadOntologiesButton.png"),SwingConstants.LEFT);
	tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
	help.add(tempLabel);
	help.add(Box.createRigidArea(new Dimension(10,10)));
	
	tempLabel=new JLabel("<html>Changes the current analysis context to the selected one.</html>",Utilities.getHelpIcon("activateContextButton.png"),SwingConstants.LEFT);
	tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
	help.add(tempLabel);
	help.add(Box.createRigidArea(new Dimension(10,10)));
	
	return help;
	}

	public String getPanelName() {
		return "Loading ontologies";
	}

	public int getStatusLevel() {
		if(ontologyManager.ontologiesPresentAndCorrectlyLoaded()) return 3;
		else {
			if(ontologyManager.ontologiesPresentAndNeverLoaded()) return 2;
			else return 1;
		}
	}

	
	public void refresh() {
		// nothing is done here as thi Table appearance is synchronized to the JTable content
		
	}
	

}
