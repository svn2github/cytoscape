/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.model.CyNetwork;

import java.awt.event.ActionEvent;


/**
 * creates a new network and associates an editor with it
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class NewNetworkAction extends CytoscapeAction {

	private static final long serialVersionUID = -5729080768973677821L;

	/**
	 * Creates a new NewNetworkAction object.
	 */
	public NewNetworkAction() {
		super("Empty Network");
		setPreferredMenu("File.New.Network");
	}

	/**
	 * create the new network and view
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO
		//CyNetwork newNet = Cytoscape.createNetwork( CyNetworkNaming.getSuggestedNetworkTitle("Network") );
	}
}
