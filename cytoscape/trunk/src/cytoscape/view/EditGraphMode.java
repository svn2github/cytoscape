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
package cytoscape.view;
//-------------------------------------------------------------------------
import java.util.HashMap;

import y.base.Node;
import y.base.Edge;
import y.view.EditMode;
import y.view.NodeRealizer;
import y.option.OptionHandler;

import cytoscape.GraphObjAttributes;
//-------------------------------------------------------------------------
/**
 * This class implements an editing mode that allows the user to
 * create and delete graph objects. It provides methods that respond
 * to node/edge creation to guide the user in initializing the new
 * objects, and creates the tooltips that are shown when the user
 * mouses over a node or edge.
 */
public class EditGraphMode extends EditMode {
    NetworkView parent;
    
    public EditGraphMode(NetworkView networkView) { 
        super();
        this.parent = networkView;
        allowNodeCreation(true);
        allowEdgeCreation(true);
        allowBendCreation(true);
        showNodeTips(true);
        showEdgeTips(true);
        
        // added by dramage 2002-08-16
        setMoveSelectionMode(new StraightLineMoveMode());
    }
    
    /**
     * Creates the tooltip shown when the user mouses over the
     * given node. The tooltip is a concatenation of the current
     * label of the node and its canonical name, or just the
     * label if it is the same as the canonical name.
     */
    protected String getNodeTip(Node node) {
        //note that in yFiles, labels are stored in the graph object
        String geneName = parent.getGraphView().getGraph2D().getLabelText(node);
        String canonicalName = parent.getNetwork().getNodeAttributes().getCanonicalName(node);
        if (canonicalName != null && canonicalName.length () > 0 &&
            !canonicalName.equals (geneName)) {
            return geneName + " " + canonicalName;
        } else {
            return geneName;
        }
    } // getNodeTip

    /**
     * Called when the user creates a new node. Calls configureNewNode
     * to guild the user in initializing the new node, then sets the node
     * label to the commonName attribute (if it exists) and adds the
     * attribute bundle to the node attributes for the current network.
     */
    protected void nodeCreated(Node newNode) {
        //note that in yFiles, labels are stored in the graph object
        String defaultName = parent.getGraphView().getGraph2D().getLabelText(newNode);
        HashMap nodeAttributeBundle = configureNewNode(newNode);
        String commonNameKey = "commonName";
        String commonName = defaultName;
        if (nodeAttributeBundle.containsKey(commonNameKey)) {
            commonName = (String) nodeAttributeBundle.get(commonNameKey);
            NodeRealizer r = parent.getNetwork().getGraph().getRealizer(newNode);
            r.setLabelText(commonName);
        }
        String canonicalName = (String) nodeAttributeBundle.get("canonicalName");
        if (canonicalName == null || canonicalName.length () < 1) {
            canonicalName = commonName;
        }
        
        if (canonicalName == null || canonicalName.length () < 1) {
            canonicalName = defaultName;
        }
        
        //nodeAttributes.add (canonicalName, nodeAttributeBundle);
        parent.getNetwork().getNodeAttributes().set(canonicalName, nodeAttributeBundle);
        parent.getNetwork().getNodeAttributes().addNameMapping(canonicalName, newNode);
    } // nodeCreated

    /**
     * Called when an edge is created; currently does nothing.
     */
    protected void edgeCreated(Edge e) {
        //cytoscapeWindow.getLogger().info("edge created: " + e);
    }

    /**
     * Returns the canonical name of the edge, to be displayed as the
     * tooltip when the user mouses over the given edge.
     */
    protected String getEdgeTip(Edge edge) {
        return parent.getNetwork().getEdgeAttributes().getCanonicalName(edge);
    } // getEdgeTip

    /**
     * Guides the user in initializing the newly created node. Grabs
     * the existing node attributes for the current network and gives
     * the user a UI to set values for those attributes. Returns the
     * bundle of attributes set by the user.
     */
    public HashMap configureNewNode (Node node) {
        OptionHandler options = new OptionHandler ("New Node");
        GraphObjAttributes nodeAttributes = parent.getNetwork().getNodeAttributes();
        
        String [] attributeNames = nodeAttributes.getAttributeNames ();
        
        if (attributeNames.length == 0) {
            options.addComment ("commonName is required; canonicalName is optional and defaults to commonName");
            options.addString ("commonName", "");
            options.addString ("canonicalName", "");
        } else {
            for (int i=0; i < attributeNames.length; i++) {
                String attributeName = attributeNames [i];
                Class attributeClass = nodeAttributes.getClass (attributeName);
                if (attributeClass.equals ("string".getClass ())) {
                    options.addString (attributeName, "");
                } else if (attributeClass.equals (new Double (0.0).getClass ())) {
                    options.addDouble (attributeName, 0);
                } else if (attributeClass.equals (new Integer (0).getClass ())) {
                    options.addInt (attributeName, 0);
                }
            }
        } // else
        
        options.showEditor ();
        
        HashMap result = new HashMap ();
        
        if (attributeNames.length == 0) {
            result.put ("commonName", (String) options.get ("commonName"));
            result.put ("canonicalName", (String) options.get ("canonicalName"));
        } else {
            for (int i=0; i < attributeNames.length; i++) {
                String attributeName = attributeNames [i];
                Class attributeClass = nodeAttributes.getClass (attributeName);
                if (attributeClass.equals ("string".getClass ())) {
                    result.put (attributeName, (String) options.get (attributeName));
                } else if (attributeClass.equals (new Double (0.0).getClass ())) {
                    result.put (attributeName, (Double) options.get (attributeName));
                } else if (attributeClass.equals (new Integer (0).getClass ())) {
                    result.put (attributeName, (Integer) options.get (attributeName));
                }
            }
        } // else
        
        return result;
        
    } // configureNode
}

