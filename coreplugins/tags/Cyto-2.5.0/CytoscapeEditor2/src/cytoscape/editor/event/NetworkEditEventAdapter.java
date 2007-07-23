/*
 * Created on May 17, 2005
 *
 */
package cytoscape.editor.event;

import cytoscape.editor.CytoscapeEditor;

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
	protected InnerCanvas canvas;
	protected DGraphView view;
	CytoscapeEditor _caller;

	/**
	 * Creates a new NetworkEditEventAdapter object.
	 */
	public NetworkEditEventAdapter() {
	}

	/**
	 * starts up the event handler on the input network view adds an input event
	 * listener to the view's canvas
	 *
	 * @param view
	 *            a Cytoscape network view
	 */

	// public void start(PGraphView view) {
	public void start(DGraphView view) {
		this.view = view;
		this.canvas = view.getCanvas();
		// AJK: 04/15/06 for Cytoscape 2.3
		// canvas.addInputEventListener(this);
		
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);

		// CytoscapeEditorManager.log("Mouse and MotionListeners added to " + canvas);
		// CytoscapeEditorManager.log("Canvas has total number of Listeners = " +
		// canvas.getMouseListeners().length);
	}

	/**
	 * stops the event handler by removing the input event listener from the
	 * canvas this is called when the user switches between editors
	 *
	 */
	public void stop() {
		if (canvas != null) {
			// AJKL: 04/15/06 for Cytoscape 2.3
			// canvas.removeInputEventListener(this);
			canvas.removeMouseListener(this);
			canvas.removeMouseMotionListener(this);
			canvas.removeKeyListener(this);
			this.view = null;
			this.canvas = null;
		}
	}

	/**
	 *
	 * @return the current canvas
	 */

	// public PCanvas getCanvas() {
	public InnerCanvas getCanvas() {
		return canvas;
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
	 * @return Returns the view.
	 */

	// public PGraphView getView() {
	public DGraphView getView() {
		return view;
	}

	/**
	 * @param view
	 *            The view to set.
	 */

	// public void setView(PGraphView view) {
	public void setView(DGraphView view) {
		this.view = view;
	}
}
