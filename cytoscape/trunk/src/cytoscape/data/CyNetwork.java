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
    GraphObjAttributes nodeAttributes;     //attributes for nodes
    GraphObjAttributes edgeAttributes;     //attributes for edges
    ExpressionData expressionData;         //expression data
    
    Set listeners = new HashSet();
    int activityCount = 0;
    
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
     * CynetworkEvent of type CyNetworkEvent.END to all registered listeners.
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

