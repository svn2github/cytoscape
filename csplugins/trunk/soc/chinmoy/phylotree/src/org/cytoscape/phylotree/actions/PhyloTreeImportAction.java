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
import java.io.File;
import java.util.List;
import java.util.Iterator;

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
				
		if (pFormat.equalsIgnoreCase("Phylip")){
		
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
			
			
			
		}
	}
}
