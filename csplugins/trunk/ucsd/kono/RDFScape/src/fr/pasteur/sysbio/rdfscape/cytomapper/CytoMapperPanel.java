/**
 * Copyright 2006-2007 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import fr.pasteur.sysbio.rdfscape.AbstractModulePanel;
import fr.pasteur.sysbio.rdfscape.InteractivePanel;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.Utilities;
import fr.pasteur.sysbio.rdfscape.help.HelpManager;


/**
 * @author andrea@sgtp.net
 * A Panel that presents user controls for performing the mapping between terms in the ontology and nodes in Cytoscape.
 * It also shows statistic on results.
 */
@SuppressWarnings("serial")
public class CytoMapperPanel extends AbstractModulePanel {
	
	private CytoMapper cytoMapper=null;
	private JButton mapButton=null;						// issue mapping between URIs and nodes
	private JButton linkDataButton=null;				// issue analysis of how many URIs can be annotated with microarray data
	private JProgressBar ontologyCoverage=null;			// how many URIs in the ontology (subset specified with the query provided in CytoMapperOptionPanel have a corresponding node)
	private JProgressBar graphCoverage=null;			// how many nodes in Cytoscape have a corresponding URIs (satisfying criteria specified)
	JProgressBar ontologyDataCoverage=null; 	// how many URIs (specified...) in the ontology can be related to microarray data
	JProgressBar dataOntologyCoverage=null;		// how many elements with data in the graph have a corresponding entry in the ontology
	
	/**
	 *  
	 */
	public CytoMapperPanel(CytoMapper cs) {
		super();
		
		redLightText="<html>No ontologies correctly loaded are present</html>";
		yellowLightText="<html>No valid mapping processed</html>";
		greenLightText= "<html>You can now plot your ontologies to the network for which you have a valid mapping</html>";
		myTabText="Map ontologies on my network";
		myTabTooltip="<html>Associate ontologies to a generic network in Cytoscape</html>";
		
		cytoMapper=cs;
		mapButton=new JButton("Map");
		linkDataButton=new JButton("Link data");
		linkDataButton.setEnabled(false);
		ontologyCoverage=new JProgressBar();
		graphCoverage=new JProgressBar();
		ontologyDataCoverage=new JProgressBar();
		dataOntologyCoverage=new JProgressBar();
		ontologyCoverage.setBorder(new TitledBorder("URIs to nodes"));
		graphCoverage.setBorder(new TitledBorder("Nodes to URIs"));
		ontologyDataCoverage.setBorder(new TitledBorder("URIs to data"));
		dataOntologyCoverage.setBorder(new TitledBorder("data to URIs"));
		graphCoverage.setMinimum(0);
		graphCoverage.setStringPainted(true);
		ontologyDataCoverage.setMinimum(0);
		ontologyDataCoverage.setStringPainted(true);
		ontologyCoverage.setMinimum(0);
		//ontologyCoverage.setMaximum(cytoMapper.getTotalURIs());
		ontologyCoverage.setStringPainted(true);
		dataOntologyCoverage.setStringPainted(true);
		
		add(mapButton);
		add(linkDataButton);
		add(ontologyCoverage);
		add(graphCoverage);
		add(ontologyDataCoverage);
		add(dataOntologyCoverage);
		
		ActionListener mapButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				initOntology2GraphBars();
				cytoMapper.map();
				updateOntology2GraphBars();
				linkDataButton.setEnabled(true);
			}
			private void updateOntology2GraphBars() {
				ontologyCoverage.setValue(cytoMapper.getNumberOfMatchedURIs());
				graphCoverage.setValue(cytoMapper.getNumberOfMatchedIDs());
			}
		};
		mapButton.addActionListener(mapButtonListener);
		
		ActionListener linkButtonListener=new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(cytoMapper.preLinkData()) {
					initLinkBars();
					cytoMapper.linkData();
					updateLinkGraphBars();
				}
			}
			private void updateLinkGraphBars() {
				ontologyDataCoverage.setValue(cytoMapper.getNumberOfURIWithData());
				dataOntologyCoverage.setValue(cytoMapper.getNumberOfDataExtracted());
			}
		};
		linkDataButton.addActionListener(linkButtonListener);
	}

	/**
	 * @param totalURIs
	 * @param nodeCount
	 */
	public void initOntology2GraphBars() {
		ontologyCoverage.setMaximum(cytoMapper.getNumberOfURIsToBeMatched());
		ontologyCoverage.setValue(0);
		graphCoverage.setMaximum(cytoMapper.getNumberOfNodesToBeMatched());
		graphCoverage.setValue(0);
	}

	/**
	 * @param totalURIs
	 * @param numberOfGenes
	 */
	public void initLinkBars() {
		ontologyDataCoverage.setValue(0);
		ontologyDataCoverage.setMaximum(cytoMapper.getNumberOfURIsToBeMatched());
		dataOntologyCoverage.setValue(0);
		dataOntologyCoverage.setMaximum(cytoMapper.getNumberOfAvailableData());
		
	}
	public String getHelpLink() {
		return "http://www.bioinformatics.org/rdfscape/wiki/Main/LinkingGraphsInCytoscapeAndOntologies";
	}

	public JPanel getHelpPanel() {
		JPanel help=new JPanel();
		JLabel tempLabel=null;
		help.setLayout(new BoxLayout(help,BoxLayout.Y_AXIS));
		
		tempLabel=new JLabel(
				"<html><ul>" +
				"<li><b>Map</b> uri for which an association was found in the <b>Mapping ontologies on Cytoscape Networks</b> configuration panel. Note that execution of the configuration settings in this panel is not automatic.</li>" +
				"<li><b>Link</b> ontologies to microarray data in Cytoscape. This opertion is activated after <b>Map</b> is executed. However, data linked to the ontology is the datsa present in Cytoscape, not necessarily visualized in the current network.</li>" +
				"</ul></html>",
				Utilities.getHelpIcon("cytoMapperExecButtons.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>The percentage of the URIs for which a mapping was specified that found a node in the current network.</html>",
				Utilities.getHelpIcon("uriToNodes.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>The percentage of nodes in this network to which an URI was associated.</html>",
				Utilities.getHelpIcon("nodesToURI.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>The percentage of URIs for which a mapping was specified that can be associated to microarray data (<b>note: this does not take into account mapping to the Cytoscape network</b>!!! these two values can potentially be independent)</html>",
				Utilities.getHelpIcon("uriToData.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		tempLabel=new JLabel(
				"<html>The percentage of available microarray data thatcan be associated to URIs.</html>",
				Utilities.getHelpIcon("dataToURIs.png"),SwingConstants.LEFT);
		tempLabel.setPreferredSize(new Dimension(HelpManager.panelWidth,200));
		help.add(tempLabel);
		help.add(Box.createRigidArea(new Dimension(10,10)));
		
		return help;
	}

	public String getPanelName() {
		return "Mapping to Cytoscape";
	}

	public int getStatusLevel() {
		if(!RDFScape.getOntologyManager().ontologiesPresentAndCorrectlyLoaded() || cytoMapper.getNumberOfMappingRules()==0) return 1;
		else {
			if(cytoMapper.getNumberOfMatchedIDs()+cytoMapper.getNumberOfMatchedURIs()>0) return 2;
			else return 3;
		}
		
	}

	
	public void refresh() {
		// nothing to do here
		
	}

}
