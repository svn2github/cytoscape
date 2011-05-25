/*
 Copyright (c) 2011, The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
/*
*
* Revisions:
*
* Wed Jan 28 15:53:50 2009 (Michael L. Creech) creech@w235krbza760
*  Changed mousePressed() and itemDropped() to use current network
*  view vs cached CytoscapeEditor value, deprecated two argument constructor.
*  Fixed bug whre itemDropped() would allow dropping of a palette entry
*  on a NetworkView that wasn't the current network view.
* Wed Jul 09 09:56:39 2008 (Michael L. Creech) creech@w235krbza760
*  Added check that Editor component is active to itemDropped()
*  to avoid handling events when the editor tab isn't the current tab.
* Wed Oct 24 13:34:37 2007 (Michael L. Creech) creech@w235krbza760
*  Removed some debugging output.
* Tue Jan 16 09:20:14 2007 (Michael L. Creech) creech@w235krbza760
*  Commented out some debugging statements.
********************************************************************************
*/
package cytoscape.hyperedge.editor.event;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;

import cytoscape.editor.event.PaletteNetworkEditEventHandler;

import cytoscape.editor.impl.BasicCytoShapeEntity;
import cytoscape.hyperedge.editor.HyperEdgeEditor;

import cytoscape.view.CyNetworkView;

import giny.view.EdgeView;
import phoebe.PhoebeCanvasDropEvent;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import ding.view.InnerCanvas;



/**
 * @author Michael L. Creech
 *
 */
public class HyperEdgeEditEventHandler extends PaletteNetworkEditEventHandler {
    public HyperEdgeEditEventHandler() {
        super();
    }

    public HyperEdgeEditEventHandler(CytoscapeEditor caller) {
        super(caller);
    }
    /**
     * @deprecated use  single argument constructor
     */
    public HyperEdgeEditEventHandler(CytoscapeEditor caller, CyNetworkView view) {
	// MLC 02/03/09:
        // super(caller, view);
	// MLC 02/03/09:
        super(caller);
    }

    /**
     * The <b>itemDropped()</b> method is at the heart of the
     * palette-based editor.  The method can respond to a variety
     * of DataFlavors that correspond to the shape being dragged
     * and dropped from the palette.  These include Cytoscape
     * nodes and edges, as well as URLs that can be dragged and
     * dropped from other applications onto the palette.
     *
     */
    @Override public void itemDropped(PhoebeCanvasDropEvent e) {
	// MLC 07/09/08 BEGIN:
	// TODO: This check should really be avoided by having the editor remove the PhoebeCanvasDropListener
	//       when the editor looses focus (another tab is clicked on).
	//       Since this is somewhat involved and so is left for when the editor is refactored.
	if (!CytoscapeEditorManager.isEditorInOperation()) {
	    return;
	}
        Point                location = e.getLocation();
	// MLC 02/03/09 BEGIN:
	InnerCanvas dropCanvas = (InnerCanvas) e.getSource();
	InnerCanvas currentCanvas = getCurrentDGraphView().getCanvas();
	
	if (dropCanvas != currentCanvas)
	    {
		// We are attempting to drop on a NetworkView that
		// isn't the current NetworkView:
		return;
	    }
	// MLC 02/03/09 END.
        BasicCytoShapeEntity myShape = getShapeEntityForLocation(location,
                                                                 e.getTransferable());

        if (myShape == null) {
            return;
        }

        // need to handle nodes and edges differently 
        String attributeName  = myShape.getAttributeName();
        String attributeValue = myShape.getAttributeValue();

	// MLC 01/15/07:
        // HEUtils.log("Item dropped attribute name = " + attributeName +
        //            " attribute value = " + attributeValue);
        if (HyperEdgeEditor.COMPLEX_TYPE.equals(attributeName)) {
	    // MLC 01/15/07:
            // HEUtils.log("getView() = " + getView());
            // HEUtils.log("getCurrentView() = " +
            //            Cytoscape.getCurrentNetworkView());
            // MLC 02/03/09:
            ((HyperEdgeEditor) get_caller()).determineAction(getCurrentDGraphView(),
                                                             attributeValue,
                                                             location);
        } else {
            super.itemDropped(e);
        }
    }

    //    // override handleDroppedEdge in PaletteNetworkEditEventHandler:
    //    protected handleDroppedEdge (String attributeName, String attributeValue, Point loc) {
    //        if (isEdgeStarted()) {
    //            // if there is another edit in progress, then don't
    //            // process a drag/drop
    //            return;
    //        }
    //
    //	NodeView targetNode = getView().getPickedNodeView(location);
    //        if (targetNode != null) {
    //	    // if we reach this point, then the edge shape has been
    //	    // dropped onto a node begin Edge creation
    //	    setHandlingEdgeDrop(true);
    //	    beginEdge(location,targetNode);	
    //	    return;
    //        }
    //	EdgeView targetEdge = getView().getPickedEdgeView(location);	
    //        if (targetEdge != null) {
    //	    // if we reach this point, then the edge shape has been
    //	    // dropped onto an edge begin Edge creation
    //	    setHandlingEdgeDrop(true);
    //	    beginEdge(location,targetEdge);	
    //        }
    //    }
    //
    //    // overloaded version of BasicNetworkEditEventHandler.beginEdge():
    //    private void beginEdge(Point2D location, EdgeView ev) {
    //	edgeSource = ev;
    //	setupBeginEdgeState (location);
    //    }

    // override mousePressed() in BasicNetworkEditEventHandler:
    @Override public void mousePressed(MouseEvent e) {
	// TODO: This check should really be avoided by having the editor remove all mouse and key
	//       listeners when the editor looses focus (another tab is clicked on).
	//       This is somewhat involved and so is left for when the editor is refactored.
	if (!CytoscapeEditorManager.isEditorInOperation()) {
	    return;
	}
	// CytoscapeEditorManager.log("HEE: mousePressed!");
	// MLC 02/03/09 BEGIN:
        // nextPoint = e.getPoint();
        Point2D pressedPoint = e.getPoint();

        // EdgeView ev     = getView().getPickedEdgeView(nextPoint);
        EdgeView ev     = getCurrentDGraphView().getPickedEdgeView(pressedPoint);	
	// MLC 02/03/09 END.
        boolean  onEdge = (ev != null);

        if (!onEdge) {
            // to normal mouse operation:
            super.mousePressed(e);
        } else if (onEdge && isEdgeStarted() && !e.isControlDown()) {
            // special use for HyperEdgeEditor.
            // Finish Edge Creation
	    // MLC 02/03/09:
            // finishEdge(nextPoint, ev);
	    // MLC 02/03/09:
            finishEdge(pressedPoint, ev);
        } else {
            super.mousePressed(e);
        }
    }

    // Overloading finishEdge
    // Finish dropping an edge onto another edge.
    public void finishEdge(Point2D location, EdgeView target) {
        CyEdge tEdge = (CyEdge) target.getEdge();

        // CyNetworkView    netView = (CyNetworkView) getView();
        CyNetworkView netView = (CyNetworkView) target.getGraphView();
        ((HyperEdgeEditor) get_caller()).convertEdgeIntoHyperEdge(location,
                                                                  tEdge,
                                                                  (CyNode) (getNode()
                                                                                .getNode()),
                                                                  getEdgeAttributeName(),
                                                                  getEdgeAttributeValue(),
                                                                  netView);
        completeFinishEdge();
    }
}
