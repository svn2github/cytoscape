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
import java.util.*;

import y.view.Graph2D;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import luna.*; 

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
/**
 * This object contains a graph and data associated with that graph, and
 * is the central data object of Cytoscape.
 *
 * This class supports listener objects that are notified when someone is
 * operating on the network. Algorithms should call the beginActivity
 * method before working with the network, and call endActivity when done.
 */
public class CyNetwork {
    
    Graph2D graph;                         //the graph
    RootGraph rootGraph;                   //giny root graph
    GraphPerspective graphPerspective; 
    boolean needsLayout = false;           //is layout required before displaying graph
    boolean isYFiles = true;
    GraphObjAttributes nodeAttributes;     //attributes for nodes
    GraphObjAttributes edgeAttributes;     //attributes for edges
    ExpressionData expressionData;         //expression data
    
    Set listeners = new HashSet();
    int activityCount = 0;
    
    /**
     * Default constructor. Equivalent to
     * CyNetwork(null, null, null, null).
     */
     public CyNetwork() {this(null, null, null, null);}
    
    /**
     * Constructor specifying no expression data. Equivalent to
     * CyNetwork(graph, nodeAttributes, edgeAttributes, null).
     */
    public CyNetwork(Graph2D graph,
                     GraphObjAttributes nodeAttributes,
                     GraphObjAttributes edgeAttributes) {
        this(graph, nodeAttributes, edgeAttributes, null);
    }
    
    /**
     * Standard CyNetwork constructor. Ensures that a valid graph and
     * attributes objects exist; any of the arguments may be null, but
     * this constructor will construct default objects for any missing
     * arguments, except for expression data.
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
    public CyNetwork(RootGraph rootGraph,
                     GraphObjAttributes nodeAttributes,
                     GraphObjAttributes edgeAttributes,
                     ExpressionData expressionData, boolean isYFiles) {
	this.isYFiles	 = isYFiles;	     
        //not completely sure how to handle null arguments
        if (rootGraph != null) {
            this.rootGraph = rootGraph;
	    graphPerspective = rootGraph.createGraphPerspective( rootGraph.getNodeIndicesArray(), rootGraph.getEdgeIndicesArray() ); 
        } else {
            this.rootGraph = new LunaRootGraph();
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
	 
	
    }//cr
    
    
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
    public CyNetwork(GraphPerspective graphPerspective,
                     GraphObjAttributes nodeAttributes,
                     GraphObjAttributes edgeAttributes,
                     ExpressionData expressionData, boolean isYFiles) {
	this.isYFiles	 = isYFiles;	     
        //not completely sure how to handle null arguments
        if (graphPerspective != null) {
            this.graphPerspective = graphPerspective;
	    //graphPerspective = rootGraph.createGraphPerspective( rootGraph.getNodeIndicesArray(), rootGraph.getEdgeIndicesArray() ); 
	    this.rootGraph = graphPerspective.getRootGraph();
	} else {
            this.rootGraph = new LunaRootGraph();
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
	 
	
    }//cr
    
    
    //methods to get each member
    /**
     * Returns the graph object for this network.
     */
    public Graph2D getGraph() {return graph;}
    /**
    * Returns a Root Graph of this network
    */
    public RootGraph getRootGraph() {return rootGraph;}
    /**
    * returns graphPerspective of this networks root graph
    */
    public GraphPerspective getGraphPerspective() {return graphPerspective;}
    /**
     * Sets the graph for this network.
     *
     * @deprecated This method does not guarantee that the new graph is
     *             synchronized with the data attribute structures. Instead,
     *             one should call setNewGraphFrom with a properly constructed
     *             network, or just construct and use a new network object.
     */
    public void setGraph(Graph2D newGraph) {
        if (newGraph != null) {
            this.graph = newGraph;
            fireEvent(CyNetworkEvent.GRAPH_REPLACED);
        }
    }
    /**
     * Sets a new graph for this network by replacing this object's graph
     * with the graph from the network argument. If the boolean flag
     * replaceAttributes is true, then the node and edge attribute objects
     * will also be replaced with the coresponding objects from the network
     * argument; if this flag is false, the attributes from the network
     * argument will be copied into the attributes of this object.
     * If the network argument is null, this method does nothing.
     *
     * To load a new graph, first create a new network for that graph using
     * the utilities of CyNetworkFactory, then call this method with that
     * new network. This ensures that the attributes that are constructed
     * when the graph is read are properly installed in this network along
     * with the new graph.
     *
     * This method will fire an event of type CyNetworkEvent.GRAPH_REPLACED
     * to all registered listeners. Note that it is the responsibility of
     * the caller to make sure that no one is currently operating on this
     * network before changing the graph.
     */
    public void setNewGraphFrom(CyNetwork newNetwork, boolean replaceAttributes) {
        if (newNetwork == null) {return;}
        this.graph = newNetwork.getGraph();
        this.setNeedsLayout( newNetwork.getNeedsLayout() );
        if (replaceAttributes) {
            this.nodeAttributes = newNetwork.getNodeAttributes();
            this.edgeAttributes = newNetwork.getEdgeAttributes();
        } else {
            this.nodeAttributes.inputAll( newNetwork.getNodeAttributes() );
            this.edgeAttributes.inputAll( newNetwork.getEdgeAttributes() );
        }
        fireEvent(CyNetworkEvent.GRAPH_REPLACED);
    }
    
    /**
     * Indicates whether a layout operation should be performed on the graph
     * before displaying it in a window. This may mean that no layout information
     * is available (i.e., all nodes at coordinates (0,0)), or that someone
     * wants a new layout performed.
     *
     * This field is an artifact of the fact that yFiles stores graph connectivity
     * information and layout information in the same object, so that this object
     * is the logical choice to keep track of this flag. One can expect this
     * flag to be moved from this class to a view class at some later time.
     */
    public boolean getNeedsLayout() {return needsLayout;}
    /**
     * Sets the flag indicating whether a layout operation on the graph is needed.
     * This flag is initialized to false upon construction. Usually the object
     * that calls the constructor will also set this flag, and the view object
     * should check this flag and, if true, do the layout and then set this
     * flag back to false.
     */
    public void setNeedsLayout(boolean needsLayout) {this.needsLayout = needsLayout;}
    
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
    
    /**
     * Registers the argument as a listener to this object. Does nothing if
     * the argument is already a listener.
     */
    public void addCyNetworkListener(CyNetworkListener listener) {
        listeners.add(listener);
    }
    /**
     * Removes the argument from the set of listeners for this object. Returns
     * true if the argument was a listener before this call, false otherwise.
     */
    public boolean removeCyNetworkListener(CyNetworkListener listener) {
        return listeners.remove(listener);
    }
    /**
     * Returns the set of listeners registered with this object.
     */
    public Set getCyNetworkListeners() {return new HashSet(listeners);}
    
    /**
     * This method should be called before reading or changing the data held
     * in this network object. There are two consequences:
     *
     * 1) First, A CyNetworkEvent of type CyNetworkEvent.BEGIN will be fired to all
     * listeners attached to this object, *only* if this is the first begin of
     * a nested stack of begin/end methods. No event will be fired if a previous
     * beginActivity call hasn't been closed by a matching endActivity call.
     *
     * 2) Second, a PRE_EVENT will be fired by the graph member of this object
     * (i.e., graph.firePreEvent();), regardless of whether a CyNetworkEvent
     * was fired.
     *
     * The argument is simply a String that is useful for identifying the
     * caller of this method. This is provided for debugging purposes, in case
     * an algorithm forgets to provide a matching end method for each begin.
     */
    public void beginActivity(String callerID) {
	if (!isYFiles) { return;} //not implemented for giny yet    
        activityCount++;
        if (activityCount == 1) {fireEvent(CyNetworkEvent.BEGIN);}
        getGraph().firePreEvent();
    }
    
    /**
     * This method should be called when an algorithm is finished reading
     * or changing the data held in this network object. There are two
     * consequences:
     *
     * 1) First, a POST_EVENT will be fired by the graph member of this object
     * (i.e., graph.firePostEvent();)
     *
     * 2) Second, a CyNetworkEvent of type CyNetworkEvent.END will be fired to
     * listeners attached to this object, *only* if this is the last end in a nested
     * block of begin/end calls.
     *
     * The argument is a String for identifying the caller of this method.
     */
    public void endActivity(String callerID) {
	if (!isYFiles) { return;} //not implemented for giny yet        
        if (activityCount == 0) {return;} //discard calls without a matching begin
        activityCount--;
        getGraph().firePostEvent();
        if (activityCount == 0) {fireEvent(CyNetworkEvent.END);}
    }
    
    /**
     * This method returns true if the current state of this object is clear;
     * that is, if every beginActivity call has been followed by a matching
     * endActivity call, so that one can reasonably assume that no one is
     * currently working with the network.
     */
    public boolean isStateClear() {return (activityCount == 0);}
    
    /**
     * This method is provided as a failsafe in case an algorithm fails to
     * close its beginActivity calls without matching endActivity calls. If
     * the current state is not clear, this method resets this object to the
     * state of no activity, makes the graph fire a POST_EVENT, and fires a
     * CyNetworkEvent of type CyNetworkEvent.END to all registered listeners.
     *
     * If the current state is clear (i.e., there are no calls to beginActivity
     * without matching endActivity calls), then this method does nothing.
     *
     * The argument is a String for identifying the caller of this method.
     */
    public void forceClear(String callerID) {
        if (activityCount > 0) {
            activityCount = 0;
            getGraph().firePostEvent();
            fireEvent(CyNetworkEvent.END);
        }
    }
    
    /**
     * Fires an event to all listeners registered with this object. The argument
     * should be a constant from the CyNetworkEvent class identifying the type
     * of the event.
     */
    protected void fireEvent(int type) {
        CyNetworkEvent event = new CyNetworkEvent(this, type);
        for (Iterator i = listeners.iterator(); i.hasNext(); ) {
            CyNetworkListener listener = (CyNetworkListener)i.next();
            listener.onCyNetworkEvent(event);
        }
    }
}

