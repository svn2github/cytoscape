/*
 * Created on Jul 30, 2005
 *
 */
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.util.CytoscapeAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.GraphView;

import java.awt.event.ActionEvent;


/**
 * creates a new network and associates an editor with it
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *
 */
public class NewNetworkAction extends CytoscapeAction {

	private static final long serialVersionUID = -5729080768973677821L;

	private CyNetworkFactory cnf;
	private GraphViewFactory gvf;

	/**
	 * Creates a new NewNetworkAction object.
	 */
	public NewNetworkAction(CyNetworkFactory f, GraphViewFactory g, CyNetworkManager netmgr) {
		super("Empty Network",netmgr);
		setPreferredMenu("File.New.Network");
		cnf = f;
		gvf = g;
	}

	/**
	 * create the new network and view
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetwork newNet = cnf.getInstance();
		newNet.attrs().set("name","Network");
		System.out.println("newNet: " + newNet.getSUID());
		GraphView view = gvf.createGraphView(newNet);
		// TODO figure this out
		netmgr.addNetwork(newNet, view, null);
	}
}
