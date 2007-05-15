package sbmlreader;

import javax.swing.JFileChooser;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.util.*;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import jigcell.sbml2.*;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import giny.model.Node;
import cytoscape.CyNode;
import cytoscape.CyEdge;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.visual.*;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.readers.AbstractGraphReader;

/*
 * Created on September 27, 2005, 9:23 AM
 * This generates the Dialog for the loading of an SBML file and does the processing 
 * of the SBML file using the java SBML2 library.
 */

/**
 * @author  W.P.A. Ligtenberg, Eindhoven University of Technology
 * @author  Mike Smoot 
 */
public class SBMLGraphReader extends AbstractGraphReader implements GraphReader {

    ArrayList<Integer> nodeIds; 
    ArrayList<Integer> edgeIds;
    URL fileURL;
    
    public SBMLGraphReader(String filename) {
    	super(filename);
	fileURL = null;
    }

    public SBMLGraphReader(URL url) {
    	super(null);
	fileURL = url;
	fileName = null;
    }

    public void read() throws IOException {
    	
	InputStream instream;
	if ( fileURL == null && fileName != null )
		instream = new FileInputStream(fileName);
	else if ( fileURL != null && fileName == null )
		instream = fileURL.openStream();
	else
		throw new IOException("No file to open!");

        // proces file
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

	nodeIds = new ArrayList<Integer>();
	edgeIds = new ArrayList<Integer>();

        SBMLLevel2Document document; 
	try {
            document = SBMLLevel2Document.readDocument(instream);
	} catch (IOException ioe) {
		if ( ioe.getMessage().equals("Unable to parse input.") ) 
			throw new IOException("Unable to parse input: Document must be level 2 SBML");
		else
			throw (ioe);
	}
            Model model = document.getModel();


            // Get all the species in the network
            List listOfSpecies = model.getSpecies();
            for(Iterator i = listOfSpecies.iterator(); i.hasNext();) {
                Species species = (Species)i.next();
                CyNode node = Cytoscape.getCyNode( species.getId(), true );
                nodeAttributes.setAttribute( species.getId(), "sbml name", species.getName() );
                nodeAttributes.setAttribute( species.getId(), "sbml type", "species" );
                nodeAttributes.setAttribute( species.getId(), "sbml id", species.getId() );
                nodeAttributes.setAttribute( species.getId(), "sbml initial concentration", new Double( species.getInitialConcentration() ) );
                nodeAttributes.setAttribute( species.getId(), "sbml initial amount", new Double( species.getInitialAmount() ) );

				String comp = species.getCompartment();
				if ( comp != null )
                	nodeAttributes.setAttribute( species.getId(), "sbml compartment", comp );

                nodeAttributes.setAttribute( species.getId(), "sbml charge", new Integer( species.getCharge() ) );
               	nodeIds.add( node.getRootGraphIndex() ); 
            }

            // Get all reactions and create a node
            List listOfReactions = model.getReactions();
            for(Iterator i = listOfReactions.iterator(); i.hasNext();) {
                Reaction reaction = (Reaction)i.next();
                CyNode node = Cytoscape.getCyNode(reaction.getId(), true);
                nodeAttributes.setAttribute(reaction.getId(), "sbml type", "reaction");
                nodeAttributes.setAttribute(reaction.getId(), "sbml id", reaction.getId());

				String rname = reaction.getName();
				if ( rname != null )
                	nodeAttributes.setAttribute(reaction.getId(), "sbml name", rname);

               	nodeIds.add( node.getRootGraphIndex() ); 

                //Get all products and link them to the reaction node
                List products = reaction.getProduct();
                for(Iterator j = products.iterator(); j.hasNext();) {
                    SpeciesReference species = (SpeciesReference)j.next();
                    CyNode product = Cytoscape.getCyNode(species.getSpecies(), false);
                    CyEdge edge = Cytoscape.getCyEdge(node, product, Semantics.INTERACTION, "reaction-product", true);
		    edgeIds.add(edge.getRootGraphIndex());
                }

                //Get all reactants and link them to the reaction node
                List reactants = reaction.getReactant();
                for(Iterator j = reactants.iterator(); j.hasNext();) {
                    SpeciesReference species = (SpeciesReference)j.next();
                    CyNode reactant = Cytoscape.getCyNode(species.getSpecies(), false);
                    CyEdge edge = Cytoscape.getCyEdge(node, reactant, Semantics.INTERACTION, "reaction-reactant", true);
		    edgeIds.add(edge.getRootGraphIndex());
                }

                //Get all modifiers and link them to the reaction node
                List modifiers = reaction.getModifier();
                for(Iterator j = modifiers.iterator(); j.hasNext();) {
                    ModifierSpeciesReference species = (ModifierSpeciesReference)j.next();
                    CyNode modifier = Cytoscape.getCyNode(species.getSpecies(), false);
                    CyEdge edge = Cytoscape.getCyEdge(modifier, node, Semantics.INTERACTION, "reaction-modifier", true);
		    edgeIds.add(edge.getRootGraphIndex());
                }
            }
   }

   public void doPostProcessing(CyNetwork network) {

            // Set SBML specific visual style

            VisualMappingManager manager = Cytoscape.getVisualMappingManager();
            CalculatorCatalog catalog = manager.getCalculatorCatalog();

            VisualStyle vs = catalog.getVisualStyle(SBMLVisualStyleFactory.SBMLReader_VS); 

            if(vs == null){
                vs = SBMLVisualStyleFactory.createVisualStyle(network);
                catalog.addVisualStyle(vs);
            }

            manager.setVisualStyle(vs);
            Cytoscape.getCurrentNetworkView().setVisualStyle(vs.getName());
            Cytoscape.getCurrentNetworkView().applyVizmapper(vs);
    }

    public int[] getNodeIndicesArray() {
	int[] nodes = new int[nodeIds.size()];
	for (int i = 0; i < nodes.length; i++ )
		nodes[i] = nodeIds.get(i).intValue();
	return nodes;
    }

    public int[] getEdgeIndicesArray() {
	int[] edges = new int[edgeIds.size()];
	for (int i = 0; i < edges.length; i++ )
		edges[i] = edgeIds.get(i).intValue();
	return edges;
    }

}


