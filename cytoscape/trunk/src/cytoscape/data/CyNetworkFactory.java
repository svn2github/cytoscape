/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
 //-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.data;
//-------------------------------------------------------------------------
import y.base.Node;
import y.base.Edge;
import y.view.Graph2D;

import giny.model.RootGraph;

import cytoscape.CyProject;
import cytoscape.GraphObjAttributes;
import cytoscape.data.readers.*;
import cytoscape.data.servers.BioDataServer;
import java.util.*;
//import cytoscape.data.ExpressionData;
//-------------------------------------------------------------------------
/**
 * Defines static methods to construct a CyNetwork object or to load data
 * into an existing network.
 *
 * To load a new graph into an existing network, first construct a new
 * network from the new graph using the methods of this class, then call
 * the loadNewGraph method on the existing network with the new network
 * as an argument.
 */
public class CyNetworkFactory {
    /**
     * Creates a network with an empty graph, empty attributes object, and
     * no expression data.
     */
    public static CyNetwork createEmptyNetwork(boolean isYFiles) {
	if (isYFiles) {
	    return new CyNetwork();
	} else {
	    return new CyNetwork(false);
	}
    }

    /**
     * Constructs a network from an interactions file describing the graph.
     * The node and edge attributes will be filled with the object-to-name
     * mappings, and the edge attributes filled with the interaction type
     * for each edge.
     *
     * This method creates a reader for the file and delegates to
     * createNetworkFromGraphReader.
     *
     * @param location  the location of the external file containing the graph data
     * @param canonicalize  a flag indicating whether to convert names in the
     *                      external file to canonical names using the bioDataServer
     * @param bioDataServer  provides the name conversion service
     * @param species  the species to use as argument to the bioDataServer's
     *                 naming services
     *
     * If location is null, or no graph can be parsed from the file, then this
     * method return null.
     * If canonicalize is false, and/or either of the last two arguments is
     * null, then the graph will be read but no name conversion services
     * will be supplied. Otherwise, the names in the graph file will be
     * converted to canonical names using the bioDataServer.
     *
     * createNetworkFromInteractionsFile(String, boolean, BioDataServer, String, boolean)
     * called from 
     *    cytoscape.cytoscape
     *    cytoscape.actions.LoadInteractionFileAction
     *
     * createNetworkFromInteractionsFile(String)
     * createNetworkFromInteractionsFile(String, boolean, BioDataServer, String)
     * called from 
     *    data.unitTests.CyNetworkFactoryTest
     */
    public static CyNetwork createNetworkFromInteractionsFile ( String location,
								boolean canonicalize, 
								BioDataServer bioDataServer, 
								String species, 
								boolean isYFiles ) {
	if (location == null) {return null;}
	InteractionsReader reader = new InteractionsReader(bioDataServer, species, location, isYFiles);
	CyNetwork network = createNetworkFromGraphReader(reader, canonicalize, isYFiles);
	if (network != null) {network.setNeedsLayout(true);}
	return network;
    }
    /**
     * wrapper for createNetworkFromInteractionsFile(String, boolean, BioDataServer, String, boolean)
     */
    public static CyNetwork createNetworkFromInteractionsFile ( String location,   
								boolean canonicalize, 
								BioDataServer bioDataServer, 
								String species ) {
	return createNetworkFromInteractionsFile(location, canonicalize, bioDataServer, species, true);
    }
    /**
     * wrapper for createNetworkFromInteractionsFile(String, boolean, BioDataServer, String, boolean)
     */
    public static CyNetwork createNetworkFromInteractionsFile ( String location ) {
	return createNetworkFromInteractionsFile(location, false, null, null, true );
    }

    //-------------------------------------------------------------------------
    /**
     * Constructs a network from a GML file describing the graph. The node
     * and edge attributes objects will be filled with the object-to-name mappings.
     * This method creates a reader for the file and delegates to
     * createNetworkFromGraphReader.
     *
     * The argument is the file location. This method returns null if the
     * argument is null or cannot be parsed into a graph.
     */
    public static CyNetwork createNetworkFromGMLFile(String location, boolean isYFiles) {
	if (location == null) {return null;}
	GMLReader reader = new GMLReader(location, isYFiles);
	//the GMlReader ignores the canonicalize argument
	CyNetwork network = createNetworkFromGraphReader(reader, false, isYFiles);
	if (network != null) {network.setNeedsLayout(false);}
	return network;
    }
    //-------------------------------------------------------------------------
    /**
     * Constructs a network from the supplied graph reader. The node and
     * edge attribute objects will be filled with the data available from
     * the graph file, such as the object-to-name- mappings and the
     * interaction type.
     *
     * This method is protected because it depends on the public methods
     * of this class to provide some wrapping functionality (for example,
     * deciding if a new layout is needed for the graph).
     *
     * The canonicalize argument is passed to the read() method of the
     * graph reader and controls whether the names in the file should
     * be converted to canonical names using the facilities of the
     * BioDataServer.
     *
     * The reader argument is assumed to be non-null. Returns null if no
     * graph can be parsed by the reader.
     */
    protected static CyNetwork createNetworkFromGraphReader( GraphReader reader,
							     boolean canonicalize, 
							     boolean isYFiles ) {
	try {
	    reader.read();
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace();
	    return null;
	}
	if ( isYFiles ) {
	    Graph2D graph = reader.getGraph();
	    if (graph == null) {return null;} //unable to parse into a graph
	    GraphObjAttributes nodeAttributes = new GraphObjAttributes();
	    GraphObjAttributes edgeAttributes = reader.getEdgeAttributes();
	    //the graph reader fills the edge attributes with the interaction
	    //type (if the source is an interactions file), and provides the
	    //mapping from edges to edge canonical names. It does not provide
	    //the mapping of nodes to names, so we do that here
	    if (graph != null) {
		Node[] allNodes = graph.getNodeArray();
		for (int i=0; i<allNodes.length; i++) {
		    String canonicalName = allNodes[i].toString();
		    nodeAttributes.addNameMapping(canonicalName, allNodes[i]);
		}
	    }
	    return new CyNetwork(graph, nodeAttributes, edgeAttributes);
	}
	else { //using giny
	    RootGraph rootGraph = reader.getRootGraph();
	    if (rootGraph == null) {return null;} //unable to parse into a graph
	    GraphObjAttributes nodeAttributes = new GraphObjAttributes();
	    GraphObjAttributes edgeAttributes = reader.getEdgeAttributes();
	    //the graph reader fills the edge attributes with the interaction
	    //type (if the source is an interactions file), and provides the
	    //mapping from edges to edge canonical names. It does not provide
	    //the mapping of nodes to names, so we do that here
	    
	   List nodes = rootGraph.nodesList();
	   if (rootGraph != null) {
		Iterator i = nodes.iterator();
		while (i.hasNext()) {
		    giny.model.Node node = (giny.model.Node) i.next();
		    String canonicalName = node.getIdentifier();
		    //System.out.println("LOADED:" + canonicalName);
		    if(canonicalName == null || canonicalName.length() == 0){
			throw new IllegalStateException("The Node object " + node + 
							" has a null or empty canonical name");
		    }
		    
		    nodeAttributes.addNameMapping (canonicalName, node);
		}
	   }
	   return new CyNetwork(rootGraph, nodeAttributes, edgeAttributes, null, false);
	}
    }
    /**
     * wrapper for createNetworkFromGraphReader(GraphReader, boolean, boolean)
     */
    //protected static CyNetwork createNetworkFromGraphReader( GraphReader reader,
    //							     boolean canonicalize ) {
    //	return createNetworkFromGraphReader( reader, canonicalize, true );
    //    }

    //-------------------------------------------------------------------------
    /**
     * Loads node and edge attribute data into the network from the provided
     * locations, which should be attribute files in a format recognized by
     * the reading methods of GraphObjAttributes.
     *
     * @param network  the network to load the data into
     * @param nodeAttrLocations  an array of node attribute file locations. May be null.
     * @param edgeAttrLocations  an array of edge attribute file locations. May be null.
     * @param canonicalize  a flag indicating whether to convert names in the
     *                      external file to canonical names using the bioDataServer
     * @param bioDataServer  provides the name conversion service
     * @param species  the species to use as argument to the bioDataServer's
     *                 naming services
     *
     * If the network argument is null, this method does nothing. Null arguments
     * for the locations are allowed and skipped. If canonicalize is true and
     * both a bioDataServer and species are specified (i.e., non-null), then
     * the names in the external file will be converted to canonical names using
     * the facilities of the bioDataServer.
     */
    public static void loadAttributes( CyNetwork network, 
				       String[] nodeAttrLocations,
				       String[] edgeAttrLocations,
				       boolean canonicalize,
				       BioDataServer bioDataServer, 
				       String species ) {
	if (network == null) {return;}
	if (nodeAttrLocations != null) {
	    GraphObjAttributes nodeAttributes = network.getNodeAttributes();
	    for (int i=0; i<nodeAttrLocations.length; i++) {
		try {
		    nodeAttributes.readAttributesFromFile(bioDataServer, species,
							  nodeAttrLocations[i],
							  canonicalize );
		} catch (Exception e) {
		    System.err.println(e.getMessage());
		    e.printStackTrace();
		}
	    }
	}
	if (edgeAttrLocations != null) {
	    GraphObjAttributes edgeAttributes = network.getEdgeAttributes();
	    for (int j=0; j<edgeAttrLocations.length; j++) {
		try {
		    edgeAttributes.readAttributesFromFile(edgeAttrLocations[j]);
		} catch (Exception e) {
		    System.err.println(e.getMessage());
		    e.printStackTrace();
		}
	    }
	}
    }
    /**
     * Loads node and edge attribute data into the network from the provided
     * locations, which should be attribute files in a format recognized by
     * the reading methods of GraphObjAttributes. No naming services will be
     * provided by a BioDataServer. This method is equivalent to
     * loadAttributes(network, nodeAttrLocations, edgeAttrLocations, false, null, null).
     */
    public static void loadAttributes( CyNetwork network, 
				       String[] nodeAttrLocations,
				       String[] edgeAttrLocations ) {
	loadAttributes(network, nodeAttrLocations, edgeAttrLocations, false, null, null);
    }


    //-------------------------------------------------------------------------
    /**
     * Constructs a network using information from a CyProject argument that
     * contains information on the location of the graph file, any node/edge
     * attribute files, and a possible expression data file.
     * If the data server argument is non-null and the project requests
     * canonicalization, the data server will be used for name resolution
     * given the names in the graph/attributes files.
     *
     * @see CyProject
     */
    public static CyNetwork createNetworkFromProject(CyProject project,
						     BioDataServer bioDataServer) {
	if (project == null) {return null;}
    
	boolean canonicalize = project.getCanonicalize();
	String species = project.getDefaultSpeciesName();
	CyNetwork network = null;
	if (project.getInteractionsFilename() != null) {
	    //read graph from interaction data
	    String filename = project.getInteractionsFilename();
	    network = createNetworkFromInteractionsFile(filename, canonicalize,
							bioDataServer, species, true); // @@@@@@ add giny support!!!! 
	}
	else if (project.getGeometryFilename() != null) {
	    //read a GML file
	    String filename = project.getGeometryFilename();
	    network = createNetworkFromGMLFile(filename, true); // @@@@@@ add giny support!!!!
	}
	if (network == null) {//no graph specified, or unable to read
	    //create a default network
	    network = new CyNetwork();
	    network.setNeedsLayout(true);
	}
	
	//load attributes files
	String[] nodeAttributeFilenames = project.getNodeAttributeFilenames();
	String[] edgeAttributeFilenames = project.getEdgeAttributeFilenames();
	loadAttributes(network, nodeAttributeFilenames, edgeAttributeFilenames,
		       canonicalize, bioDataServer, species);
	
	//load expression data
	ExpressionData expData = null;
	if (project.getExpressionFilename() != null) {
	    expData = new ExpressionData( project.getExpressionFilename() );
	    network.setExpressionData(expData);
	}
	
	return network;
    }
    //-------------------------------------------------------------------------
}
        
