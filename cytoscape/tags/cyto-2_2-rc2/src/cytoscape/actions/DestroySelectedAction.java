// -------------------------------------------------------------------------
package cytoscape.actions;

//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.undo.AbstractUndoableEdit;

import giny.model.*;
import giny.view.*;
import java.util.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.util.CytoscapeAction;

//-------------------------------------------------------------------------
public class DestroySelectedAction extends CytoscapeAction {

	public DestroySelectedAction() {
		// AJK: 10/24/05 change name to "Delete" rather than "Destroy"
//		super("Destroy Selected Nodes/Edges");
		super("Delete Selected Nodes/Edges");
		setPreferredMenu("Edit");
	}

	public DestroySelectedAction(boolean label) {
		super();
	}

	public void actionPerformed(ActionEvent e) {
		final CyNetwork gp = Cytoscape.getCurrentNetwork();
		Set flaggedNodes = gp.getFlaggedNodes();
		Set flaggedEdges = gp.getFlaggedEdges();
		final int[] hiddenNodeIndices = new int[flaggedNodes.size()];
		final int[] hiddenEdgeIndices = new int[flaggedEdges.size()];

		int j = 0;
		for (Iterator i = flaggedNodes.iterator(); i.hasNext();) {
			hiddenNodeIndices[j++] = gp.getIndex((Node) i.next());
		}
		j = 0;
		for (Iterator i = flaggedEdges.iterator(); i.hasNext();) {
			hiddenEdgeIndices[j++] = gp.getIndex((Edge) i.next());
		}

		// unflag then hide nodes from graph perspective
		gp.unFlagAllNodes();
		gp.unFlagAllEdges();
		gp.hideEdges(hiddenEdgeIndices);
		gp.hideNodes(hiddenNodeIndices);
		
//		Cytoscape.getDesktop().getUndo().addEdit(new AbstractUndoableEdit() {
//
//			final String network_id = gp.getIdentifier();
//
//			public String getPresentationName() {
//				// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
////				return "Delete";
//				return  "Delete";
//			}
//
//			public String getRedoPresentationName() {
//				if (hiddenEdgeIndices.length == 0)
//					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//					return "Redo: Deleted Nodes";
////					return " ";
//				else
//					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//					return "Redo: Deleted Nodes and Edges";
////					return " ";
//		}
//
//			public String getUndoPresentationName() {
//
//				if (hiddenEdgeIndices.length == 0)
//					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//					return "Undo: Deleted Nodes";
////					return null;
//				else
//					// AJK: 10/21/05 return null as presentation name because we are using iconic buttons
//					return "Undo: Deleted Nodes and Edges";
////					return null;
//			}
//
//			public void redo() {
////				super.redo();
//				// removes the removed nodes and edges from the network
//				CyNetwork network = Cytoscape.getNetwork(network_id);
//				if (network != null) {
//					network.hideEdges(hiddenEdgeIndices);
//					network.hideNodes(hiddenNodeIndices);
////					CytoscapeEditorManager.getNodeClipBoard().elements(nodes);
////					CytoscapeEditorManager.getEdgeClipBoard().elements(edges); // sets elements
//
//				}
//
//			}
//
//			public void undo() {
////				super.undo();
//				CyNetwork network = Cytoscape.getNetwork(network_id);
//				if (network != null) {
//					network.restoreNodes(hiddenNodeIndices);
//					network.restoreEdges(hiddenEdgeIndices);
//					
//				}
//			}
//
//		});

		// AJK: 09/14/05 BEGIN
		//      fire a NETWORK_MODIFIED event

		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, gp);


		// AJK: 09/14/05 END
	}//action performed
}

