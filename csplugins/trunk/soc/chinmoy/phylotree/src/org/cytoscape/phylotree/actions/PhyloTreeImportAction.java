package org.cytoscape.phylotree.actions;

import java.awt.event.ActionEvent;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.phylotree.PhylotreePlugin;
import org.cytoscape.phylotree.ui.PhyloFileDialog;
import org.cytoscape.phylotree.parser.*;
import org.forester.io.parsers.PhylogenyParserException;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;

import cytoscape.layout.CyLayouts;

public class PhyloTreeImportAction extends CytoscapeAction{
	
	public PhyloTreeImportAction(PhylotreePlugin p) {
		// Add the menu item under menu pulldown "File->Import"
		super("Phylogenetic tree...");
		setPreferredMenu("File.Import");
	}

	public void actionPerformed(ActionEvent e) {
		PhyloFileDialog fileDialog = new PhyloFileDialog(this);	
	}


	public void ImportTreeFromFile(File pFile, String pFormat){
		if (pFile == null){
			System.out.println("pFile = null");
			return;
		}
				
		if (pFormat.equalsIgnoreCase("phylip")){
			loadnetwork_phylip(pFile);
		}
		else if (pFormat.equalsIgnoreCase("phyloxml")){
			loadnetwork_phyloXML(pFile);
		}		
	}
	
	
	private void loadnetwork_phylip(File pFile) {
		// Use the parser to read the file
		
		// Parse the file
		PhylipTreeImpl phylipParser = new PhylipTreeImpl(pFile);
		
		CyNetwork cyNetwork = Cytoscape.createNetwork(pFile.getName(), true);
		
		CyAttributes cyAttributes = Cytoscape.getEdgeAttributes();
		
		
		// Get all nodes in the PHYLIP tree
		List<PhylotreeNode> nodeList = phylipParser.getNodeList();
		
		// For each node, get all edges
		for(Iterator<PhylotreeNode> nodeListIterator = nodeList.iterator(); nodeListIterator.hasNext();)
		{
			PhylotreeNode pNode = nodeListIterator.next();
			
			// Get edges
			List<PhylotreeEdge> edgeList = phylipParser.getEdges(pNode);
			
			// For each edge, create the two nodes connected if they do not exist
			for(Iterator<PhylotreeEdge> edgeListIterator = edgeList.iterator(); edgeListIterator.hasNext();)
			{
				PhylotreeEdge pEdge = edgeListIterator.next();

				CyNode node1 = Cytoscape.getCyNode(pEdge.getSourceNode().getName(), true);
				CyNode node2 = Cytoscape.getCyNode(pEdge.getTargetNode().getName(), true);
				
				CyEdge cyEdge = Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, "pp", true, true);
			
				// Get edge attributes and set them
				List<Object> edgeAttributes = phylipParser.getEdgeAttribute(pEdge);
				cyAttributes.setAttribute(cyEdge.getIdentifier(), "branchLength", (Double)edgeAttributes.get(0));
				cyNetwork.addEdge(cyEdge);
				cyNetwork.addNode(node1);
				cyNetwork.addNode(node2);
			}
		}
		
		cyAttributes.setUserEditable("branchLength", false);
		cyAttributes.setUserVisible("branchLength", true);
		
		// Apply default layout
		CyLayouts.getLayout("slanted_cladogram").doLayout();
		
		// Apply visual style
		Cytoscape.firePropertyChange(Cytoscape.VIZMAP_LOADED, null, "phyloVizMap.props");

	}
	
	private void loadnetwork_phyloXML(File pFile) {
		PhyloXmlParser parser = new PhyloXmlParser();

		parser.setSource(pFile);

		Phylogeny[] trees;
		try {
			trees = parser.parse();	
		}
		catch (PhylogenyParserException ppe){
			// give some message here
			return;
		}
		catch(IOException ioe){
			// give some message here
			return;
		}

		for (int i=0; i< trees.length; i++){
			PhylogenyNodeIterator it = trees[i].iteratorLevelOrder();

			// process each node, get node, edge and attributes			
			while (it.hasNext()){
				PhylogenyNode node = (PhylogenyNode) it.next();
				//String nodeName = node.getNodeName();
				//NodeData nodeData = node.getNodeData(); // for attributes
				//Node parent = node.getParent(); // for edge

			}
		}

		//build Cytoscape network here, create Cytoscape attributes here
		
	}

}
