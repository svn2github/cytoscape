package cytoscape.editor.editors;

import giny.view.NodeView;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.editor.event.BasicNetworkEditEventHandler;
import cytoscape.giny.PhoebeNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.CytoscapeToolBar;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;


/**
 * NOTE: THE CYTOSCAPE EDITOR FUNCTIONALITY IS STILL BEING EVOLVED AND IN A STATE OF TRANSITION TO A 
 * FULLY EXTENSIBLE EDITING FRAMEWORK FOR CYTOSCAPE VERSION 2.3.  
 * 
 * THE JAVADOC COMMENTS ARE OUT OF DATE IN MANY PLACES AND ARE BEING UPDATED.  
 * THE APIs WILL CHANGE AND THIS MAY IMPACT YOUR CODE IF YOU 
 * MAKE EXTENSIONS AT THIS POINT.  PLEASE CONTACT ME (mailto: allan_kuchinsky@agilent.com) 
 * IF YOU ARE INTENDING TO EXTEND THIS CODE AND I WILL WORK WITH YOU TO HELP MINIMIZE THE IMPACT TO YOUR CODE OF 
 * FUTURE CHANGES TO THE FRAMEWORK
 *
 * PLEASE SEE http://www.cytoscape.org/cgi-bin/moin.cgi/CytoscapeEditorFramework FOR 
 * DETAILS ON THE EDITOR FRAMEWORK AND PLANNED EVOLUTION FOR CYTOSCAPE VERSION 2.3.
 *
 */

/**
 * The <b>BasicCytoscapeEditor</b> provides base level graph editing functionality for Cytoscape version 2.2
 * provides a “node” button on the Cytoscape toolbar.  Click on the “node” button and cursor takes on a 
 * rectangular shape, the system goes into "ADD_MODE", and subsequent clicking of mouse on canvas creates 
 * nodes with default labels.  
 * The default label appears in an editable text field and can be edited.
 * <p>
 * Provides a "connect" button for the toolbar that puts the user in “CONNECT_MODE” mode, 
 * wherein the cursor changes to some form of “connector” icon.  The user clicks when over the desired source 
 * node, moves the mouse to the desired target node, and clicks the mouse when over the desired target node.
 * <p>
 * Provides an "Edit => Connect Selected Nodes" menu item that, when chosen, 
 * creates a clique amongst the selected nodes.  
 * <p> 
 * Going back out of “ADD_MODE" or "CONNECT_MODE is accomplished by pressing the “select” button on the toolbar
 * <p>
 * Provides accelerators for modeless addition of nodes and edges.  
 * Control-clicking at a position on the canvas creates a node with default label in that position.  
 * The default label appears in an editable text field, so the user can edit its name immediately by just 
 * beginning to type.  Hit ENTER or click (or control-click) anywhere outside the field, and the edited field 
 * is assigned as the label for the node.
 * Control-clicking on a node on the canvas starts an edge with source at that node.  Move the cursor and a 
 * rubber-banded line follows the cursor.  As the cursor passes over another node, that node is
 * highlighted and the rubber-banded line will snap to a connection point on that second node.  
 * Control-click the mouse again and the connection is established.  
 * <p>
 * Provides functionality for deleting selected nodes and edges and an undo/redo framework for deletion of nodes 
 * and edges
 * 
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 * @see BasicNetworkEditEventHandler
 *  
 */
public class BasicCytoscapeEditor implements CytoscapeEditor {

	/**
	 * name and type of the editor
	 */
	protected String editorName;


	/**
	 * pointer to Cytoscape menus
	 */
	CyMenus _cyMenus;

	/**
	 * pointer to Cytoscape toolbar
	 */
	CytoscapeToolBar _toolBar;

	/**
	 * current network view being edited
	 */
	CyNetworkView view;

	boolean DEBUG = false;

	/**
	 * customized cursor associated with "CONNECT MODE"
	 */
	Cursor _edgeCursor;

	/**
	 * customized cursor associated with "ADD MODE"
	 */
	Cursor _nodeCursor;

	/**
	 * customized cursor associated with mode for adding freestanding labels
	 * <b>not</b> used in Cytoscape 2.2
	 */
	Cursor _labelCursor;

	/**
	 * 32x32 image for node cursor
	 */
	Image _nodeCursorImage;

	/**
	 * 32x32 image for edge cursor
	 */
	Image _connectionCursorImage;


	/**
	 * default cursor used by Cytoscape
	 */
	Cursor _originalCursor;

	JButton _addNodeButton, _addEdgeButton, _resetCursorButton,
			_addLabelButton;

	private static final String ICONS_REL_LOC = "images/";


	/**
	 *  
	 */
	public BasicCytoscapeEditor() {
		super();
	}


	/**
	 * specialized initialization code for editor, called by
	 * CytoscapeEditorManager when a new editor is built, should be overridden
	 * 
	 * @param args
	 *            an arbitrary list of arguments passed to initialization
	 *            routine. Not used in this editor
	 */
	public void initializeControls(List args) {

		// first, check to see if there already is a menu item to "Connect Selected Nodes"
		//    if not, then add an item
		// TODO: look for routines to find a menu item given a string; there should be such a utility
		JMenu editMenu = Cytoscape.getDesktop().getCyMenus().getEditMenu();
		boolean foundConnectSelected = false;
//		System.out.println("checking against edit menu: " + editMenu);
		System.out.println("item count = " + editMenu.getItemCount());
		for (int i = 0; i < editMenu.getItemCount(); i++) {
			JMenuItem jIt = editMenu.getItem(i);
			if (jIt != null) {
				String name = jIt.getText();
//				System.out
//						.println("Checking for get selected against: " + name);
				if (name.equals("Connect Selected Nodes")) {
					foundConnectSelected = true;
					break;
				}
			}
		}
		if (!foundConnectSelected) {
			ConnectSelectedNodesAction connectAction = new ConnectSelectedNodesAction();
			connectAction.setPreferredMenu("Edit");
			Cytoscape.getDesktop().getCyMenus().addAction(connectAction);
		}

		_cyMenus = Cytoscape.getDesktop().getCyMenus();
		
		// AJK: 10/03/05 BEGIN
		//       comment out toolbar icons; disable the 'stamp editor'
		//       remove this code later
		/*
		_toolBar = _cyMenus.getToolBar();

		_toolBar.addSeparator();

		_resetCursorButton = _toolBar.add(new ResetCursorAction());
		_resetCursorButton.setIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "UpLeftWhite.gif")));
		_resetCursorButton.setToolTipText("Reset Cursor");
		_resetCursorButton.setDisabledIcon(new ImageIcon(getClass()
				.getResource(ICONS_REL_LOC + "DisabledUpLeftWhite.gif")));

		_addNodeButton = _toolBar.add(new AddNodeAction());
		_addNodeButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "rect.gif")));
				//				ICONS_REL_LOC + "ovalNodeCursor.gif")));
				ICONS_REL_LOC + "node16_centered.gif")));
		_addNodeButton.setToolTipText("Add a new Node");
		_addNodeButton.setDisabledIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "Disabledrect.gif")));

		//		_addLabelButton = _toolBar.add(new AddLabelAction());
		//		_addLabelButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "label.gif")));
		//		_addLabelButton.setToolTipText("Add a new Label");

		_addEdgeButton = _toolBar.add(new AddEdgeAction());
		_addEdgeButton.setIcon(new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "UpRightWhite.gif")));
				ICONS_REL_LOC + "UpRightBlue.gif")));
		_addEdgeButton.setToolTipText("Add a new Edge");
		_addEdgeButton.setDisabledIcon(new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "DisabledUpRightWhite.gif")));

		Toolkit tk = Toolkit.getDefaultToolkit();
		ImageIcon img;
		//		ImageIcon img = new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "label.gif"));
		//		Image labelPointer = img.getImage();
		//		_labelCursor = tk.createCustomCursor(labelPointer, new Point(1, 1),
		//				"LabelPointer");

		img = new ImageIcon(getClass().getResource(
		//				ICONS_REL_LOC + "rect.gif"));
				ICONS_REL_LOC + "node32.gif"));
		Image nodePointer = img.getImage();
		_nodeCursor = tk.createCustomCursor(nodePointer, new Point(1, 1),
				"NodePointer");

		img = new ImageIcon(getClass().getResource(
				ICONS_REL_LOC + "fit36_blue_cursor.gif"));
		Image edgePointer = img.getImage();
		_edgeCursor = tk.createCustomCursor(edgePointer, new Point(30, 1),
				"EdgePointer");
				*/
		// AJK: 10/03/05 END

		_originalCursor = Cytoscape.getDesktop().getCursor();

	}

	/**
	 * sets controls invisible when editor type is switched
	 * 
	 * @param args
	 *            args an arbitrary list of arguments (not used in this editor)
	 */
	public void disableControls(List args) {
		if (_addNodeButton != null) { // make sure we have buttons before disabling them
			_addNodeButton.setVisible(false);
		}
		if (_addEdgeButton != null)  {
			_addEdgeButton.setVisible(false);
		}
		if (_resetCursorButton != null)  {
			_resetCursorButton.setVisible(false);
		}
	}

	/**
	 * sets controls visible when editor type is switched back to this editor
	 * @param args args an arbitrary list of arguments (not used in this editor)	 * 
	 */
	public void enableControls(List args) {
		System.out.println("enabling controls for " + this);
		_addNodeButton.setVisible(true);
		_addEdgeButton.setVisible(true);
		_resetCursorButton.setVisible(true);
	}

	/**
	 * gets the name (type) of this editor
	 * @return the editorName.
	 */
	public String getEditorName() {
		return editorName;
	}

	/**
	 * sets the name (type) for this editor
	 * @param editorName the editorName to set.
	 */
	public void setEditorName(String editorName) {
		this.editorName = editorName;
	}




	/**
	 * action that is invoked when the "add node" button is pressed
	 * sets the mode of the edit event handler to "ADD_MODE"
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class AddNodeAction extends CytoscapeAction {
		public AddNodeAction() {
			//			super("Add a new Node");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_nodeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.ADD_MODE);
			}
		}
	}

	/**
	 * action that is invoked when the "add label" button is pressed
	 * sets the mode of the edit event handler to "LABEL_MODE"
	 * <p>
	 * not implemented in Cytoscape 2.2
	 * 
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class AddLabelAction extends CytoscapeAction {
		public AddLabelAction() {
			//			super("Add a new Node");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_nodeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.LABEL_MODE);

			}
		}
	}

	/**
	 * action that is invoked when the "connect" button is pressed
	 * sets the mode of the edit event handler to "CONNECT_MODE"
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */	
	class AddEdgeAction extends CytoscapeAction {
		public AddEdgeAction() {
			//			super("Add a new Edge");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_edgeCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.CONNECT_MODE);
			}
		}
	}


	/**
	 * action that is invoked when the "reset cursor" button is pressed
	 * sets the mode of the edit event handler to "SELECT_MODE"
	 * <p>
	 * not implemented in Cytoscape 2.2
	 * 
	 * @author Allan Kuchinsky, Agilent Technologies
	 * @version 1.0
	 *
	 */
	class ResetCursorAction extends CytoscapeAction {
		public ResetCursorAction() {
			//			super("Add a new Edge");
			super("");
		}

		public void actionPerformed(ActionEvent ae) {
			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			((PhoebeNetworkView) Cytoscape.getCurrentNetworkView()).getCanvas()
					.setCursor(_originalCursor);
			BasicNetworkEditEventHandler event = (BasicNetworkEditEventHandler) CytoscapeEditorManager
					.getViewNetworkEditEventAdapter(view);

			if (event == null) {
				System.out
						.println("Error: cannot find event handler for view: "
								+ view);
			} else {
//				event.setMode(event.SELECT_MODE);
			}
			
			// clear any partial edges
			if (event.isEdgeStarted())
			{
				event.setEdgeStarted(false);
				event.getCanvas().getLayer().removeChild(event.getEdge());
			}
		}
	}

	/**
	 * action performed with the Edit->Connect Selected Nodes menu item is clicked on
	 * creates a clique from the set of selected nodes
	 * @author ajk
	 *
	 **/
	class ConnectSelectedNodesAction extends CytoscapeAction {
		public ConnectSelectedNodesAction() {
			super("Connect Selected Nodes");
			setPreferredMenu("Edit");
		}

		public ConnectSelectedNodesAction(boolean label) {
			super();
		}
		
		public String getName ()
		{
			return "Connect Selected Nodes";
		}

		public void actionPerformed(ActionEvent e) {

			CyNetworkView view = Cytoscape.getCurrentNetworkView();
			java.util.List nodes = view.getSelectedNodes();

			for (int i = 0; i < nodes.size() - 1; i++) {
				NodeView nv = (NodeView) nodes.get(i);
				CyNode firstCyNode = (CyNode) nv.getNode();
				for (int j = i + 1; j < nodes.size(); j++) {
					NodeView nv2 = (NodeView) nodes.get(j);
					CyNode secondCyNode = (CyNode) nv2.getNode();
					CytoscapeEditorManager.addEdge(firstCyNode, secondCyNode,
							Semantics.INTERACTION, "default", true, "DefaultEdge");
				}
			}
		}

	}

}