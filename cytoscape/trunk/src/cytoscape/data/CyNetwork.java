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
import y.view.Graph2D;

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
/**
 * This object contains a graph and data associated with that graph, and
 * is the central data object of Cytoscape.
 */
public class CyNetwork {
    
    Graph2D graph;                         //the graph
    GraphObjAttributes nodeAttributes;     //attributes for nodes
    GraphObjAttributes edgeAttributes;     //attributes for edges
    ExpressionData expressionData;         //expression data
    
    /**
     * Constructor that ensures that a valid graph and attributes objects
     * exist. The ExpressionData argument may be null.
     *
     * WARNING: many methods expect that the node attributes hold a canonical
     * name and common name attribute for every node in the graph, and the
     * edge attributes hold an interaction attribute for every edge. This
     * constructor does not guarantee these fields are present. Most users
     * should instead use a factory method to construct their network.
     */
    public CyNetwork(Graph2D graph,
                     GraphObjAttributes nodeAttributes,
                     GraphObjAttributes edgeAttributes,
                     ExpressionData expressionData) {
        //not completely sure how to handle null arguments
        if (graph != null) {
            this.graph = graph;
        } else {
            this.graph = new Graph2D();
        }
        if (nodeAttributes != null) {
            this.nodeAttributes = nodeAttributes;
        } else {
            this.nodeAttributes = new GraphObjAttributes();
        }
        if (edgeAttributes != null) {
            this.edgeAttributes = edgeAttributes;
        } else {
            this.edgeAttributes = new GraphObjAttributes();
        }
        //null expression data is OK
        this.expressionData = expressionData;
    }
    
    //methods to get each member
    /**
     * Returns the graph object for this network.
     */
    public Graph2D getGraph() {return graph;}
    /**
     * Sets the graph for this network.
     *
     * @deprecated This method does not guarantee that the new graph is
     *             synchronized with the data attribute structures. Instead,
     *             one should construct a completely new Network object
     *             with the new graph and appropriate node and edge attributes.
     */
    public void setGraph(Graph2D newGraph) {
        if (newGraph != null) {this.graph = newGraph;}
    }
    /**
     * Returns the node attributes data object for this network.
     */
    public GraphObjAttributes getNodeAttributes() {return nodeAttributes;}
    /**
     * Sets the node attribute data structure for this network.
     *
     * WARNING: many methods expect that the node attributes contain a
     * canonical name and a common name for each node in the graph. This
     * method presumes that the caller has suitably initialized these fields
     * in the supplied argument. Most users should instead use the
     * add(GraphObjAttributes) method on the existing node attributes object
     * to add the new attributes to the existing ones.
     */
    public void setNodeAttributes(GraphObjAttributes newNodeAttributes) {
        if (newNodeAttributes != null) {
            this.nodeAttributes = newNodeAttributes;
        }
    }
    /**
     * Returns the edge attributes data object for this network.
     */
    public GraphObjAttributes getEdgeAttributes() {return edgeAttributes;}
    /**
     * Sets the edge attribute data structure for this network.
     *
     * WARNING: many methods expect that the edge attributes hold an
     * interaction attribute for every edge in the graph. This
     * method presumes that the caller has suitably initialized this
     * field in the supplied argument. Most users should instead use the
     * add(GraphObjAttributes) method on the existing edge attributes object
     * to add the new attributes to the existing ones.
     */
    public void setEdgeAttributes(GraphObjAttributes newEdgeAttributes) {
        if (newEdgeAttributes != null) {
            this.edgeAttributes = newEdgeAttributes;
        }
    }
    /**
     * Returns the expression data object associated with this network.
     */
    public ExpressionData getExpressionData() {return expressionData;}
    /**
     * Sets the expression data object associated with this network.
     */
    public void setExpressionData(ExpressionData newData) {
        this.expressionData = newData;
    }
}

