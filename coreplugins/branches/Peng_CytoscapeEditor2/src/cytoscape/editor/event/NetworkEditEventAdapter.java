/* -*-Java-*-
********************************************************************************
*
* File:         NetworkEditEventAdapter.java
* RCS:          $Header: $
* Description:  
* Author:       Allan Kuchinsky
* Created:      Tue May 17 11:17:49 2005
* Modified:     Wed Feb 04 08:44:13 2009 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2009, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Tue Feb 03 11:19:51 2009 (Michael L. Creech) creech@w235krbza760
*  Removed use of canvas and view in favor of deriving these from the
*  current network view. Added getCurrentDGraphView().
********************************************************************************
*/

package cytoscape.editor.event;

import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.view.CyNetworkView;

import ding.view.DGraphView;
import ding.view.InnerCanvas;

import phoebe.PhoebeCanvasDropEvent;
import phoebe.PhoebeCanvasDropListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

// TODO (02/03/09): All deprecated references to setView(), getView(),
// and start(DGraphView) should be updated along with all higher-level
// methods that pass unneeded variables DGrapViews to lower-level
// methods. ALso, this class should probably be turned into an
// interface or an abstract class and BasicNetworkEditEventHandler
// become the first full implementation.

/**
 *
 * The <b>NetworkEditEventAdapter</b> class provides stub methods for
 * specialized network edit event handlers, as part of the graph editing
 * framework. The specialized network edit event handler is the key class in the
 * Cytoscape editor for defining the behavior of the editor. The behavior is
 * defined in terms of how the event handler responds to mouse events, drag/drop
 * events, and button press events. All editors must include a network edit
 * event handler class that extends the <b>NetworkEditEventAdapter</b> class.
 *  * revised: 04/15/2006 to integrate with Cytoscape 2.3 renderer Phase 1:
 * switch underlying node identification and edge drawing code Phase 2: remove
 * dependencies upon Piccolo
 *
 * @author Allan Kuchinsky
 * @version 1.0
 *
 */
public class NetworkEditEventAdapter implements MouseListener, MouseMotionListener, ActionListener,
                                                PhoebeCanvasDropListener, KeyListener,
                                                cytoscape.data.attr.MultiHashMapListener {
    // MLC 02/03/09 BEGIN:
    //	protected InnerCanvas canvas;
    //	protected DGraphView view;
    // CytoscapeEditor _caller;
	protected CytoscapeEditor _caller;
    private boolean started; 
    // MLC 02/03/09 END.

	/**
	 * Creates a new NetworkEditEventAdapter object.
	 */
	public NetworkEditEventAdapter() {
	}

	/**
	 * starts up the event handler on the input network view adds an input event
	 * listener to the view's canvas
	 *
	 * @deprecated Use no argument start().
	 * @param view
	 *            a Cytoscape network view
	 */

	public void start(DGraphView view) {
	    // MLC 02/03/09 BEGIN:
	    start();
	    //	    this.view = view;
	    //	    this.canvas = view.getCanvas();
	    //	    // AJK: 04/15/06 for Cytoscape 2.3
	    //	    // canvas.addInputEventListener(this);
	    //		
	    //	    canvas.addMouseListener(this);
	    //	    canvas.addMouseMotionListener(this);
	    //	    canvas.addKeyListener(this);
	    //	    // CytoscapeEditorManager.log("Mouse and MotionListeners added to " + canvas);
	    //	    // CytoscapeEditorManager.log("Canvas has total number of Listeners = " +
	    //	    // canvas.getMouseListeners().length);
	    // MLC 02/03/09 END.
	}

    // MLC 02/03/09 BEGIN:
    public void start() {
	InnerCanvas canvas = getCurrentDGraphView().getCanvas();
	started = true;
	canvas.addMouseListener(this);
	canvas.addMouseMotionListener(this);
	canvas.addKeyListener(this);
    }

    // MLC 02/03/09 END.
	/**
	 * stops the event handler by removing the input event listener from the
	 * canvas this is called when the user switches between editors
	 *
	 */
	public void stop() {
	    // MLC 02/03/09 BEGIN:
	    // if (canvas != null) {
	    if (started) {
		InnerCanvas canvas = getCurrentDGraphView().getCanvas();
	    // MLC 02/03/09 END.
		// AJKL: 04/15/06 for Cytoscape 2.3
		// canvas.removeInputEventListener(this);
		canvas.removeMouseListener(this);
		canvas.removeMouseMotionListener(this);
		canvas.removeKeyListener(this);
		// MLC 02/03/09 BEGIN:
		started = false;
		// this.view = null;
		// this.canvas = null;
		// MLC 02/03/09 END.
		}
	    }

	/**
	 * Gets the current canvas based on Cytoscape.getCurrentNetworkView().
	 * @return the current canvas
	 */

	// public PCanvas getCanvas() {
	public InnerCanvas getCanvas() {
	    // MLC 02/03/09 BEGIN:
	    // return canvas;
	    return getCurrentDGraphView().getCanvas();
	    // MLC 02/03/09 END.
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseMoved(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseClicked(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void keyPressed(KeyEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void keyReleased(KeyEvent event) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param event DOCUMENT ME!
	 */
	public void keyTyped(KeyEvent event) {
	}

	/**
	 * method for rendering an edge under construction as the user moves the
	 * mouse typically this may be done via a rubberband-line that udpates as
	 * the mouse position changes
	 *
	 */
	public void updateEdge() {
	}

	/**
	 * actionPerformed() method should be overwritten by child classes
	 */
	public void actionPerformed(ActionEvent evt) {
	}

	/**
	 * method for responding when an item is dropped onto the canvas. typically
	 * this would result in the addition of a node or an edge to the current
	 * Cytoscape network.
	 */
	public void itemDropped(PhoebeCanvasDropEvent dte) {
	}

	/**
	 *
	 * MultiHashMapListener methods
	 */
	public void attributeValueAssigned(java.lang.String objectKey, java.lang.String attributeName,
	                                   java.lang.Object[] keyIntoValue,
	                                   java.lang.Object oldAttributeValue,
	                                   java.lang.Object newAttributeValue) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 * @param keyIntoValue DOCUMENT ME!
	 * @param attributeValue DOCUMENT ME!
	 */
	public void attributeValueRemoved(java.lang.String objectKey, java.lang.String attributeName,
	                                  java.lang.Object[] keyIntoValue,
	                                  java.lang.Object attributeValue) {
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param objectKey DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 */
	public void allAttributeValuesRemoved(java.lang.String objectKey, java.lang.String attributeName) {
	}

	/**
	 * @return Returns the _caller.
	 */
	public CytoscapeEditor get_caller() {
		return _caller;
	}

	/**
	 * @param _caller
	 *            The _caller to set.
	 */
	public void set_caller(CytoscapeEditor _caller) {
		this._caller = _caller;
	}

	/**
	 * @deprecated Use getCurrentDGraphView().
	 * @return Returns the view.
	 */

	// public PGraphView getView() {

	public DGraphView getView() {
	    // MLC 02/03/09 BEGIN:
	    return getCurrentDGraphView ();
	    // return view;
	    // MLC 02/03/09 END.
	}

	/**
	 * @deprecated There is no longer any need to set the view.
	 * @param view
	 *            The view to set.
	 */
	// public void setView(PGraphView view) {
	public void setView(DGraphView view) {
	    
	    // this.view = view;
	}

    // MLC 02/03/09 BEGIN:
    // Return the Cytoscape current network view as a DGraphView:
     public DGraphView getCurrentDGraphView () {
	 CyNetworkView cnv = Cytoscape.getCurrentNetworkView();
	 // CytoscapeEditorManager.log ("current newtork view used: " + cnv.getTitle());
	 return (DGraphView)cnv;	
     }
    // MLC 02/03/09 END.

}
