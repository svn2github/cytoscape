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
import y.base.Node;
import y.base.Edge;
import y.view.EditMode;
//-------------------------------------------------------------------------
/**
 * Implements an edit mode that does not allow the user to create or
 * delete objects in the graph. Constructs the tooltips that are displayed
 * when the user mouses over a node or edge.
 */
public class ReadOnlyGraphMode extends EditMode {
    NetworkView parent;
    
    public ReadOnlyGraphMode(NetworkView networkView) { 
        super();
        this.parent = networkView;
        allowNodeCreation(false);
        allowEdgeCreation(false);
        allowBendCreation(false);
        showNodeTips(true);
        showEdgeTips(true);
        
        // added by dramage 2002-08-16
        setMoveSelectionMode(new StraightLineMoveMode());
    }
    
    /**
     * returns the tooltip displayed when the user mouses over the
     * given node. The returned string is a concatenation of the
     * current node label and the canonical name of the node, or just
     * the label if it is the same as the canonical name.
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
     * Returns the canonical name of the given edge, to be displayed as
     * the tooltip when the user mouses over the given edge.
     */
    protected String getEdgeTip(Edge edge) {
        return parent.getNetwork().getEdgeAttributes().getCanonicalName(edge);
    } // getEdgeTip
    
}

