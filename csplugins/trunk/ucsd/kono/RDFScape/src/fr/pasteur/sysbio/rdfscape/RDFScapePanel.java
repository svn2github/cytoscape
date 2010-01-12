/**
 * Copyright 2005-2008 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.hp.hpl.jena.reasoner.Reasoner;

/**
 * @author andrea@pasteur.fr
 * This class is the container of all other panels.
 * It also provides some panels for functionalities present in the RDFScape class.
 */
public class RDFScapePanel extends JFrame {
	
	private JTabbedPane tabbedPane=null;
	private JTabbedPane optionTabbedPane=null;
	private AbstractModulePanel contextManagerPanel=null;
	private AbstractModulePanel ontologyLoaderPanel=null;
	private AbstractModulePanel browserManagerPanel=null;
	private AbstractModulePanel patternManagerPanel=null;
	private AbstractModulePanel cytoMapperPanel=null;
	private AbstractModulePanel namespaceManagerPanel=null;
	private AbstractModulePanel cytoMapperOptionPanel=null;
	private AbstractModulePanel browserOptionPanel=null;
	private AbstractModulePanel reasonerManagerPanel=null;
	/**
	 * @param rs reference to the RDFScapePlugin Object
	 */
	public RDFScapePanel() {
		super("RDFScape");
		tabbedPane=new JTabbedPane();
		optionTabbedPane=new JTabbedPane();
		
		contextManagerPanel=RDFScape.getContextManager().getContextManagerPanel();
		ontologyLoaderPanel=RDFScape.getOntologyManager().getOntologyManagerPanel();
		browserManagerPanel=RDFScape.getBrowserManager().getPanel();
		patternManagerPanel=RDFScape.getPatternManager().getPatternManagerPanel();
		cytoMapperPanel=RDFScape.getCytoMapper().getCytoMapperPanel();
		namespaceManagerPanel=RDFScape.getNameSpaceManager().getNameSpaceManagerPanel();
		cytoMapperOptionPanel=RDFScape.getCytoMapper().getOptionPanel();
		browserOptionPanel=RDFScape.getBrowserManager().getOptionPanel();
		reasonerManagerPanel=RDFScape.getReasonerManager().getReasonerManagerPanel();
		
		tabbedPane.addTab(
				contextManagerPanel.getTabText(),
				contextManagerPanel.getStatusIcon(),
				contextManagerPanel,
				contextManagerPanel.getTabTooltip()
		);
		tabbedPane.addTab(
				ontologyLoaderPanel.getTabText(),
				ontologyLoaderPanel.getStatusIcon(),
				ontologyLoaderPanel,
				ontologyLoaderPanel.getTabTooltip()
		);
		optionTabbedPane.addTab(
				namespaceManagerPanel.getTabText(),
				namespaceManagerPanel.getStatusIcon(),
				namespaceManagerPanel,
				namespaceManagerPanel.getTabTooltip()
		);
				
		optionTabbedPane.addTab(
				reasonerManagerPanel.getTabText(),
				reasonerManagerPanel.getStatusIcon(),
				reasonerManagerPanel,
				reasonerManagerPanel.getTabTooltip()
		);
		optionTabbedPane.addTab(
				browserOptionPanel.getTabText(),
				browserOptionPanel.getStatusIcon(),
				browserOptionPanel,
				browserOptionPanel.getTabTooltip()
		);
		optionTabbedPane.addTab(
				cytoMapperOptionPanel.getTabText(),
				cytoMapperOptionPanel.getStatusIcon(),
				cytoMapperOptionPanel,
				cytoMapperOptionPanel.getTabTooltip()
		);
		
		tabbedPane.addTab(
				"3) (optional) configure options",
				Utilities.getGreenlightIcon(),
				optionTabbedPane,
				"<html>Configure appearance and reasoning settings</html>"
		);
		
		tabbedPane.addTab(
				browserManagerPanel.getTabText(),
				browserManagerPanel.getStatusIcon(),
				browserManagerPanel,
				browserManagerPanel.getTabTooltip()
		);
		tabbedPane.addTab(
				patternManagerPanel.getTabText(),
				patternManagerPanel.getStatusIcon(),
				patternManagerPanel,
				patternManagerPanel.getTabTooltip()
		);
		tabbedPane.addTab(
				cytoMapperPanel.getTabText(),
				cytoMapperPanel.getStatusIcon(),
				cytoMapperPanel,
				cytoMapperPanel.getTabTooltip()
		);
		
		
		getContentPane().add(tabbedPane,BorderLayout.CENTER);
		
		JButton helpButton=new JButton("Help");
		getContentPane().add(helpButton,BorderLayout.SOUTH);
		class HelpListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				int selectedPanel=tabbedPane.getSelectedIndex();
				InteractivePanel panelToHelp=null;
				if(selectedPanel!=2)  panelToHelp=(InteractivePanel) tabbedPane.getSelectedComponent();
				else panelToHelp=(InteractivePanel) optionTabbedPane.getSelectedComponent();
				System.out.println("Help on "+panelToHelp.getPanelName());
				RDFScape.getHelpManager().getHelpPanelForInteractivePanel(panelToHelp).setVisible(true);
				
				
			}
			
		}
		helpButton.addActionListener(new HelpListener());
		setSize(new Dimension(700,600)); // TODO maybe should be get from a Default Object
		setVisible(true);
		
	}

	/**
	 * @param string
	 */
	public void alert(String string) {
		JOptionPane.showMessageDialog(this,string,"RDFScape speaks: ",JOptionPane.WARNING_MESSAGE);
		
	}

	public void refreshTabs() {
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(contextManagerPanel),contextManagerPanel.getStatusIcon());
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(ontologyLoaderPanel),ontologyLoaderPanel.getStatusIcon());
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(browserManagerPanel),browserManagerPanel.getStatusIcon());
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(patternManagerPanel),patternManagerPanel.getStatusIcon());
		tabbedPane.setIconAt(tabbedPane.indexOfComponent(cytoMapperPanel),cytoMapperPanel.getStatusIcon());
		optionTabbedPane.setIconAt(optionTabbedPane.indexOfComponent(namespaceManagerPanel),namespaceManagerPanel.getStatusIcon());
		optionTabbedPane.setIconAt(optionTabbedPane.indexOfComponent(browserOptionPanel),browserOptionPanel.getStatusIcon());
		optionTabbedPane.setIconAt(optionTabbedPane.indexOfComponent(cytoMapperOptionPanel),cytoMapperOptionPanel.getStatusIcon());
		optionTabbedPane.setIconAt(optionTabbedPane.indexOfComponent(reasonerManagerPanel),reasonerManagerPanel.getStatusIcon());
	}
	
	
	
	

}
