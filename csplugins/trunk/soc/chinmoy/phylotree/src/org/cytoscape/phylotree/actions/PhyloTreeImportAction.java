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
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.PhylogenyParserException;
import org.forester.io.parsers.phyloxml.PhyloXmlParser;


import org.forester.phylogeny.data.*;
import org.forester.phylogeny.factories.*;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import org.cytoscape.phylotree.visualstyle.DepthwiseColor;
import org.cytoscape.phylotree.visualstyle.DepthwiseSize;
import org.cytoscape.phylotree.visualstyle.PhyloVisualStyleManager;

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
			if(validatePhylipFile(pFile))
			{
				loadnetwork_phylip(pFile);


			}
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

		CyAttributes cyEdgeAttributes = Cytoscape.getEdgeAttributes();
		CyAttributes cyNodeAttributes = Cytoscape.getNodeAttributes();

		// Get all nodes in the PHYLIP tree
		List<PhylotreeNode> nodeList = phylipParser.getNodeList();

		// Add all the nodes
		for(Iterator<PhylotreeNode> nodeListIterator = nodeList.iterator(); nodeListIterator.hasNext();)
		{
			PhylotreeNode pNode = nodeListIterator.next();

			CyNode node = Cytoscape.getCyNode(""+PhylotreePlugin.universalNodeIndexCounter, true);

			pNode.setID(""+PhylotreePlugin.universalNodeIndexCounter);
			PhylotreePlugin.universalNodeIndexCounter--;
			cyNodeAttributes.setAttribute(node.getIdentifier(), "Name", pNode.getName());

			cyNetwork.addNode(node);
		}

		// For each node, add all edges
		for(Iterator<PhylotreeNode> nodeListIterator = nodeList.iterator(); nodeListIterator.hasNext();)
		{
			PhylotreeNode pNode = nodeListIterator.next();

			// Get edges
			List<PhylotreeEdge> edgeList = phylipParser.getOutgoingEdges(pNode);

			// For each edge, get the nodes connected
			
			for (Iterator<PhylotreeEdge> edgeListIterator = edgeList.iterator(); edgeListIterator.hasNext();)
			{
				PhylotreeEdge pEdge = edgeListIterator.next();

				

				CyNode sourceNode = Cytoscape.getCyNode(pEdge.getSourceNode().getID());
				CyNode targetNode = Cytoscape.getCyNode(pEdge.getTargetNode().getID());

				CyEdge cyEdge = Cytoscape.getCyEdge(sourceNode, targetNode,	Semantics.INTERACTION, "pp", true, true);

				// Get edge attributes and set them
				List<Object> edgeAttributes = phylipParser.getEdgeAttribute(pEdge);

				cyEdgeAttributes.setAttribute(cyEdge.getIdentifier(), "branchLength",(Double)edgeAttributes.get(0));

				cyNetwork.addEdge(cyEdge);
			}
		}

	


	//		cyEdgeAttributes.setUserEditable("branchLength", false);
	//		cyEdgeAttributes.setUserVisible("branchLength", true);



	// Apply default layout
	CyLayouts.getLayout("slanted_cladogram").doLayout();

	// Add the phylogenetic tree specific visual styles
	PhyloVisualStyleManager phyloVSMan = new PhyloVisualStyleManager();
	phyloVSMan.addVisualStyle(new DepthwiseSize());
	phyloVSMan.addVisualStyle(new DepthwiseColor());


}

private void loadnetwork_phyloXML(File pFile) {

	// PhyloXmlParser parser = new PhyloXmlParser();
	PhylogenyParser parser = new PhyloXmlParser();



	PhylogenyFactory factory = ParserBasedPhylogenyFactory.getInstance();
	Phylogeny[] trees; 
	try {
		trees = factory.create(pFile, parser);



	}
	catch (PhylogenyParserException ppe){
		System.out.println("PhloXML Parser error occured.");
		System.out.println("Verify that file is a valid PhyloXML file.");

		return;
	}
	catch(IOException ioe){
		System.out.println("I/O Error occured.");
		return;
	}	

	for (int i=0; i< trees.length; i++){
		PhylogenyNodeIterator it = trees[i].iteratorLevelOrder();


		CyNetwork cyNetwork;
		// Create a network
		if(!trees[i].getName().equals(""))
			cyNetwork = Cytoscape.createNetwork(trees[i].getName(), true);
		else
			cyNetwork = Cytoscape.createNetwork(pFile.getName(), true);



		// Add network attributes
		addPhyloXMLNetworkAttributes(trees[i], cyNetwork.getIdentifier());

		int unnamedNodeIndex = 0;
		// process each node, get node, edge and attributes			
		while (it.hasNext())
		{
			
			PhylogenyNode node = (PhylogenyNode) it.next();
			PhylogenyNode parent = (PhylogenyNode) node.getParent(); // for edge

			// Add the node to the network
			if(node.getNodeName().equals(""))
			{
				node.setName("Node"+unnamedNodeIndex);
				unnamedNodeIndex++;
			}

			
			CyNode cyNode = Cytoscape.getCyNode(""+PhylotreePlugin.universalNodeIndexCounter, true);
		
			// Add the node's attributes

			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			nodeAttributes.setAttribute(cyNode.getIdentifier(), "Name", node.getNodeName());

			node.setName(""+PhylotreePlugin.universalNodeIndexCounter);
			PhylotreePlugin.universalNodeIndexCounter--;



			cyNetwork.addNode(cyNode);

			addPhyloXMLNodeAttributes(node, cyNode.getIdentifier());

			// If parent exists, add it as well as all edges connecting the two
			if(parent!=null)
			{
				CyNode cyParent = Cytoscape.getCyNode(""+parent.getNodeName(), true);

				if(cyParent.getRootGraphIndex()!=cyNode.getRootGraphIndex())
				{
					// Add the parent's attributes
					CyEdge cyEdge = Cytoscape.getCyEdge(cyParent, cyNode, Semantics.INTERACTION, "pp", true, true);
					// Add the edge's attributes

					cyNetwork.addEdge(cyEdge);
					addPhyloXMLEdgeAttributes(node, cyEdge.getIdentifier());

				}
			}
		}



		// Apply default layout
		CyLayouts.getLayout("slanted_cladogram").doLayout();

		// Apply visual style
		// Add the phylogenetic tree specific visual styles
		PhyloVisualStyleManager phyloVSMan = new PhyloVisualStyleManager();
		phyloVSMan.addVisualStyle(new DepthwiseSize());
		phyloVSMan.addVisualStyle(new DepthwiseColor());
	}


}

private boolean validatePhylipFile(File file)
{
	// Read the file to obtain the tree
	if(file!=null)
	{
		PhylipTreeImpl phylipParser = new PhylipTreeImpl(file);


		String fileStr = phylipParser.getTreeTextFromFile(file);


		// Check to make sure file is a complete tree
		int numOpenParens = 0;
		int numCloseParens = 0;

		for(int i = 0; i<fileStr.length(); i++)
		{
			if(fileStr.charAt(i)=='(')
				numOpenParens++;
			else if(fileStr.charAt(i)==')')
				numCloseParens++;
		}

		if(numOpenParens!=numCloseParens)
		{
			System.out.println("File is not a valid Phylip/Newick format file.");
			System.out.println("Number of '(' not equal to ')'.");
			return false;
		}


		// Check to make sure file is not an empty tree
		if(numOpenParens==0)
		{
			System.out.println("File is not a valid Phylip/Newick format file.");
			System.out.println("Tree in the file does not consist of any nodes.");
			return false;
		}

		return true;
	}
	else
		return false;
}

private void addPhyloXMLNetworkAttributes(Phylogeny phy, String networkID )
{
	CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();

	// Check and assign network attributes
	if(!phy.getName().equals(""))
		networkAttributes.setAttribute(networkID,"Name",phy.getName());

	if(phy.getIdentifier()!=null)
		networkAttributes.setAttribute(networkID, "ID", phy.getIdentifier().getValue());

	if(!phy.getDescription().equals(""))
		networkAttributes.setAttribute(networkID,"Description",phy.getDescription());

	if(phy.getConfidence()!=null)
		networkAttributes.setAttribute(networkID,"Confidence",phy.getConfidence().getValue());
	if(phy.isRooted())
		networkAttributes.setAttribute(networkID, "Rooted", true);
	else
		networkAttributes.setAttribute(networkID, "Rooted", false);
	if(phy.isRerootable())
		networkAttributes.setAttribute(networkID, "Rerootable", true);
	else
		networkAttributes.setAttribute(networkID, "Rerootable", false);

	if(!phy.getType().equals(""))
		networkAttributes.setAttribute(networkID,"Type",phy.getType());

	if(!phy.getDistanceUnit().equals(""))
		networkAttributes.setAttribute(networkID,"Distance Unit",phy.getDistanceUnit());

	Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);		

}

private void addPhyloXMLNodeAttributes(PhylogenyNode node, String nodeID )
{
	NodeData nodeData = node.getNodeData();

	CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();


	// Check and assign node attributes

	if(nodeData.isHasTaxonomy())
	{
		if(!nodeData.getTaxonomy().getCommonName().equals(""))
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Common Name", nodeData.getTaxonomy().getCommonName());
		if(!nodeData.getTaxonomy().getScientificName().equals(""))
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Scientific Name", nodeData.getTaxonomy().getScientificName());
		if(!nodeData.getTaxonomy().getRank().equals(""))
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Rank", nodeData.getTaxonomy().getRank());
		if(nodeData.getTaxonomy().getIdentifier()!=null)
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Identifier", nodeData.getTaxonomy().getIdentifier().toString());
		if(!nodeData.getTaxonomy().getTaxonomyCode().equals(""))
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Code", nodeData.getTaxonomy().getTaxonomyCode());
		if(!nodeData.getTaxonomy().getType().equals(""))
			nodeAttributes.setAttribute(nodeID, "Taxonomy: Type", nodeData.getTaxonomy().getType());
		if(nodeData.getTaxonomy().getUri()!=null)
		{
			if(!nodeData.getTaxonomy().getUri().getValue().toString().equals(""))
				nodeAttributes.setAttribute(nodeID, "Taxonomy: URI", nodeData.getTaxonomy().getUri().getValue().toString());
			if(!nodeData.getTaxonomy().getUri().getType().equals(""))
				nodeAttributes.setAttribute(nodeID, "Taxonomy: URI Type", nodeData.getTaxonomy().getUri().getType());
			if(!nodeData.getTaxonomy().getUri().getDescription().equals(""))
				nodeAttributes.setAttribute(nodeID, "Taxonomy: URI Description", nodeData.getTaxonomy().getUri().getDescription());
		}
	}

	if(nodeData.isHasBinaryCharacters())
	{
		nodeAttributes.setAttribute(nodeID, "Binary Characters", nodeData.getBinaryCharacters().toString());
	}



	if(nodeData.isHasDate())
	{
		if(nodeData.getDate().getRange()!=null)
			nodeAttributes.setAttribute(nodeID, "Date: Range", nodeData.getDate().getRange().toString());
		if(!nodeData.getDate().getUnit().equals(""))
			nodeAttributes.setAttribute(nodeID, "Date: Unit", nodeData.getDate().getUnit());
		if(!nodeData.getDate().getDesc().equals(""))
			nodeAttributes.setAttribute(nodeID, "Date: Description", nodeData.getDate().getDesc());
		if(nodeData.getDate().getValue()!=null)
			nodeAttributes.setAttribute(nodeID, "Date: Value", nodeData.getDate().getValue().toString());


	}

	if(nodeData.isHasDistribution())
	{
		if(nodeData.getDistribution().getAltitude()!=null)
			nodeAttributes.setAttribute(nodeID, "Distribution: Altitude", nodeData.getDistribution().getAltitude().toString());
		if(!nodeData.getDistribution().getDesc().equals(""))
			nodeAttributes.setAttribute(nodeID, "Distribution: Description", nodeData.getDistribution().getDesc());
		if(!nodeData.getDistribution().getGeodeticDatum().equals(""))
			nodeAttributes.setAttribute(nodeID, "Distribution: Geodetic Datum", nodeData.getDistribution().getGeodeticDatum());
		if(nodeData.getDistribution().getLatitude()!=null)
			nodeAttributes.setAttribute(nodeID, "Distribution: Latitude", nodeData.getDistribution().getLatitude().toString());
		if(nodeData.getDistribution().getLongitude()!=null)
			nodeAttributes.setAttribute(nodeID, "Distribution: Longitude", nodeData.getDistribution().getLongitude().toString());

	}

	if(nodeData.isHasEvent())
	{
		if(nodeData.getEvent().getEventType()!=null)
			nodeAttributes.setAttribute(nodeID, "Event: Type", nodeData.getEvent().getEventType().name());
		if(nodeData.getEvent().getConfidence()!=null)
			nodeAttributes.setAttribute(nodeID, "Event: Confidence", nodeData.getEvent().getConfidence().getType());

		nodeAttributes.setAttribute(nodeID, "Event: # Duplications", nodeData.getEvent().getNumberOfDuplications());
		nodeAttributes.setAttribute(nodeID, "Event: # Gene Losses", nodeData.getEvent().getNumberOfGeneLosses());
		nodeAttributes.setAttribute(nodeID, "Event: # Speciations", nodeData.getEvent().getNumberOfSpeciations());

	}

	if(nodeData.isHasNodeIdentifier())
	{

		if(!nodeData.getNodeIdentifier().getValue().equals(""))
			nodeAttributes.setAttribute(nodeID,"Identifier",nodeData.getNodeIdentifier().getValue());

	}

	if(nodeData.isHasProperties())
	{
		Property[] props = nodeData.getProperties().getPropertiesArray();
		for(int i = 0; i < props.length; i++)
		{
			if(props[i].getAppliesTo()!=null)
				nodeAttributes.setAttribute(nodeID,"Property"+i+": Applies To", props[i].getAppliesTo().name());
			if(!props[i].getDataType().equals(""))
				nodeAttributes.setAttribute(nodeID,"Property"+i+": Type", props[i].getDataType());
			if(!props[i].getIdRef().equals(""))
				nodeAttributes.setAttribute(nodeID,"Property"+i+": IDRef", props[i].getIdRef());
			if(!props[i].getRef().equals(""))
				nodeAttributes.setAttribute(nodeID,"Property"+i+": Ref", props[i].getRef());
			if(!props[i].getUnit().equals(""))
				nodeAttributes.setAttribute(nodeID,"Property"+i+": Unit", props[i].getUnit());
			if(!props[i].getValue().equals(""))
				nodeAttributes.setAttribute(nodeID,"Property"+i+": Value", props[i].getValue());

		}

	}

	if(nodeData.isHasReference())
	{
		if(!nodeData.getReference().getDoi().equals(""))
			nodeAttributes.setAttribute(nodeID,"Reference: DOI", nodeData.getReference().getDoi());
		if(!nodeData.getReference().getValue().equals(""))
			nodeAttributes.setAttribute(nodeID,"Reference: Value", nodeData.getReference().getValue());
	}

	if(nodeData.isHasSequence())
	{

		if(nodeData.getSequence().getLocation().equals(""))
			nodeAttributes.setAttribute(nodeID, "Sequence: Location", nodeData.getSequence().getLocation());

		if(nodeData.getSequence().getMolecularSequence().equals(""))
			nodeAttributes.setAttribute(nodeID, "Sequence: Molecular", nodeData.getSequence().getMolecularSequence());

		if(nodeData.getSequence().getName().equals(""))
			nodeAttributes.setAttribute(nodeID, "Sequence: Name", nodeData.getSequence().getName());

		if(nodeData.getSequence().getSymbol().equals(""))
			nodeAttributes.setAttribute(nodeID, "Sequence: Symbol", nodeData.getSequence().getSymbol());

		if(nodeData.getSequence().getType().equals(""))
			nodeAttributes.setAttribute(nodeID, "Sequence: Type", nodeData.getSequence().getType());

		if(nodeData.getSequence().getUri()!=null)
		{
			if(!nodeData.getSequence().getUri().getValue().toString().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: URI", nodeData.getSequence().getUri().getValue().toString());
			if(!nodeData.getSequence().getUri().getType().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: URI Type", nodeData.getSequence().getUri().getType());
			if(!nodeData.getSequence().getUri().getDescription().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: URI Description", nodeData.getSequence().getUri().getDescription());
		}

		if(nodeData.getSequence().getAccession()!=null)
		{
			if(!nodeData.getSequence().getAccession().getSource().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: Accession Source", nodeData.getSequence().getAccession().getSource());
			if(!nodeData.getSequence().getAccession().getValue().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: Accession", nodeData.getSequence().getAccession().getValue());

		}

		if(nodeData.getSequence().getDomainArchitecture()!=null)
		{
			nodeAttributes.setAttribute(nodeID, "Sequence: Domain Total Length", nodeData.getSequence().getDomainArchitecture().getTotalLength());
			for(int i = 0; i < nodeData.getSequence().getDomainArchitecture().getNumberOfDomains(); i++)
			{
				if(!nodeData.getSequence().getDomainArchitecture().getDomain(i).getId().equals(""))
					nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" ID", nodeData.getSequence().getDomainArchitecture().getDomain(i).getId());

				if(!nodeData.getSequence().getDomainArchitecture().getDomain(i).getName().equals(""))
					nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" Name", nodeData.getSequence().getDomainArchitecture().getDomain(i).getName());

				nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" Confidence", nodeData.getSequence().getDomainArchitecture().getDomain(i).getConfidence());
				nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" From", nodeData.getSequence().getDomainArchitecture().getDomain(i).getFrom());
				nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" To", nodeData.getSequence().getDomainArchitecture().getDomain(i).getTo());
				nodeAttributes.setAttribute(nodeID, "Sequence: Domain"+i+" Length", nodeData.getSequence().getDomainArchitecture().getDomain(i).getLength());

			}

		}

		List<PhylogenyData> annotations = nodeData.getSequence().getAnnotations();
		Iterator<PhylogenyData> iterator = annotations.iterator();

		int index = 0;			
		while(iterator.hasNext())
		{
			PhylogenyData data = iterator.next();
			if(!data.toString().equals(""))
				nodeAttributes.setAttribute(nodeID, "Sequence: Annotation"+index, data.toString());
			index++;
		}




	}

	Cytoscape.firePropertyChange(Cytoscape.ATTRIBUTES_CHANGED, null, null);		
}	


private void addPhyloXMLEdgeAttributes(PhylogenyNode node, String edgeID )
{
	BranchData branchData = node.getBranchData();
	CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

	if(branchData.getBranchWidth()!=null)
		edgeAttributes.setAttribute(edgeID, "branchLength", branchData.getBranchWidth().getValue());
	else
	{
		if(node.getDistanceToParent()>=0)
			edgeAttributes.setAttribute(edgeID, "branchLength", node.getDistanceToParent());
		else
			edgeAttributes.setAttribute(edgeID, "branchLength", 0.0);
	}
	if(branchData.getBranchColor()!=null)
		edgeAttributes.setAttribute(edgeID, "Color", branchData.getBranchColor().getValue().toString());

	List<Confidence> confidences = branchData.getConfidences();
	Iterator<Confidence> iterator = confidences.iterator();
	int index = 0;
	while(iterator.hasNext())
	{
		Confidence c = iterator.next();
		edgeAttributes.setAttribute(edgeID, "Confidence"+index, c.getValue());
		edgeAttributes.setAttribute(edgeID, "Confidence"+index+" type", c.getType());
		index++;

	}

}


}
