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
import y.view.Graph2D;

import cytoscape.CyProject;
import cytoscape.GraphObjAttributes;
import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.readers.GMLReader;
import cytoscape.data.servers.BioDataServer;
//import cytoscape.data.ExpressionData;
//-------------------------------------------------------------------------
/**
 * Defines static methods to construct a Network object.
 */
public class CyNetworkFactory {
    
    /**
     * This method constructs a network using information from a CyProject
     * argument, which contains information on the location of the graph
     * file, any node/edge attribute files, and a possible expression
     * data file.
     * If the data server argument is non-null and the project requests
     * canonicalization, the data server will be used for name resolution
     * given the names in the graph/attributes files.
     *
     * @see CyProject
     */
    public static CyNetwork createNetworkFromProject(CyProject project,
                                                     BioDataServer bioDataServer) {
        if (project == null) {return null;}
        Graph2D graph = null;
        GraphObjAttributes nodeAttributes = new GraphObjAttributes();
        //edge attributes are created by the graph readers, which install
        //the canonical names and interaction types of the edges
        GraphObjAttributes edgeAttributes = null;
        if (project.getInteractionsFilename() != null) {
            //read graph from interaction data
            InteractionsReader reader = new InteractionsReader(bioDataServer,
                project.getDefaultSpeciesName(), project.getInteractionsFilename() );
            reader.read( project.getCanonicalize() );
            graph = reader.getGraph();
            edgeAttributes = reader.getEdgeAttributes();
            if (graph == null) {
                System.err.println("Warning: unable to read graph from " +
                                   project.getInteractionsFilename() );
            }
        } else if (project.getGeometryFilename() != null) {
            //read a GML file
            GMLReader reader = new GMLReader( project.getGeometryFilename() );
            reader.read( project.getCanonicalize() );
            graph = reader.getGraph();
            edgeAttributes = reader.getEdgeAttributes();
            if (graph == null) {
                System.err.println("Warning: unable to read graph from " +
                                   project.getGeometryFilename() );
            }
        }
        //create default objects as needed
        if (graph == null) {graph = new Graph2D();}
        if (edgeAttributes == null) {edgeAttributes = new GraphObjAttributes();}
        
        //add name mapping for nodes in the graph
        Node[] allNodes = graph.getNodeArray();
        for (int i=0; i<allNodes.length; i++) {
            String canonicalName = allNodes[i].toString();
            nodeAttributes.addNameMapping(canonicalName, allNodes[i]);
        }
        
        //load node attribute files
        String[] nodeAttributeFilenames = project.getNodeAttributeFilenames();
        for (int i=0; i<nodeAttributeFilenames.length; i++) {
            try {
                nodeAttributes.readAttributesFromFile(bioDataServer,
                                                      project.getDefaultSpeciesName(),
                                                      nodeAttributeFilenames[i],
                                                      project.getCanonicalize() );
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        //load edge attribute files
        String[] edgeAttributeFilenames = project.getEdgeAttributeFilenames();
        for (int i=0; i<edgeAttributeFilenames.length; i++) {
            try {
                edgeAttributes.readAttributesFromFile(edgeAttributeFilenames[i]);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
        }
        
        //load expression data
        ExpressionData expData = null;
        if (project.getExpressionFilename() != null) {
            expData = new ExpressionData( project.getExpressionFilename() );
        }
        //construct the network
        CyNetwork network = new CyNetwork(graph, nodeAttributes,
                                          edgeAttributes, expData);
        
        return network;
    }
}
        
