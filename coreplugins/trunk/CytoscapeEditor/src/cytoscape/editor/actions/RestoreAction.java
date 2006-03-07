/*
 * Created on Oct 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package cytoscape.editor.actions;

import giny.model.Edge;
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.editor.CytoscapeEditorManager;
import cytoscape.util.CytoscapeAction;

/**
 * @author ajk
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RestoreAction  extends CytoscapeAction {

	public RestoreAction() {
		// AJK: 10/24/05 change name to "Delete" rather than "Destroy"
//		super("Destroy Selected Nodes/Edges");
		super("Restore Deleted Nodes/Edges");
		setPreferredMenu("Edit");
	}

	public void actionPerformed(ActionEvent e) {
		CyNetwork gp = Cytoscape.getCurrentNetwork();
		int [] hiddenNodes = CytoscapeEditorManager.getHiddenNodesForNetwork(gp);
		int [] hiddenEdges = CytoscapeEditorManager.getHiddenEdgesForNetwork(gp);
		
		gp.restoreNodes(hiddenNodes);
		gp.restoreEdges(hiddenEdges);
		
		// AJK: 09/14/05 BEGIN
		//      fire a NETWORK_MODIFIED event

		Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, gp);


		// AJK: 09/14/05 END
	}//action performed
}
