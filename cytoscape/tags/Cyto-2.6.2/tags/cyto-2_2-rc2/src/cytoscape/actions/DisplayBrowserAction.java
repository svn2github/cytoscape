package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Properties;
import java.util.Vector;

import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelState;

/**
 * 
 *  This class is enabled only when browser plugin is loaded.
 *  User can on/off browser panel (CytoPanel3) by using f5 key.
 * 
 * @author kono
 *
 */
public class DisplayBrowserAction extends CytoscapeAction {

	Vector attributeCategoriesToIgnore = new Vector();
	final static String invisibilityPropertyName = "nodeAttributeCategories.invisibleToBrowser";
	String webBrowserScript = "";

	public DisplayBrowserAction() {
		super("Show/Hide attribute browser");
		setPreferredMenu("Data");
		setAcceleratorCombo(KeyEvent.VK_F5, 0);
		
		Properties configProps = CytoscapeInit.getProperties();
		// webBrowserScript = configProps.getProperty("webBrowserScript",
		// "noScriptDefined");
		// attributeCategoriesToIgnore = Misc.getPropertyValues(configProps,
		// invisibilityPropertyName);
		for (int i = 0; i < attributeCategoriesToIgnore.size(); i++) {
			System.out.println("  ignore type "
					+ attributeCategoriesToIgnore.get(i));
		}

	}

	public void actionPerformed(ActionEvent ev) {
		// List nvlist = Cytoscape.getCurrentNetworkView().getSelectedNodes();
		// ArrayList nodeList = new ArrayList( nvlist.size() );
		// Iterator ni = nvlist.iterator();
		// while ( ni.hasNext() ) {
		// NodeView nview = ( NodeView )ni.next();
		// //giny.model.Node n = nview.getNode();
		// giny.model.Node n = Cytoscape.getRootGraph().getNode(
		// nview.getRootGraphIndex() );
		// nodeList.add(n);
		// }//while

		// Node[] selectedNodes = ( giny.model.Node[] ) nodeList.toArray (new
		// giny.model.Node [0]);
		giny.model.Node[] selectedNodes = (giny.model.Node[]) Cytoscape
				.getCurrentNetwork().getFlaggedNodes().toArray(
						new giny.model.Node[0]);

		// List evList = Cytoscape.getCurrentNetworkView().getSelectedEdges();

		// ArrayList edgeList = new ArrayList( evList.size() );
		// Iterator ei = evList.iterator();
		// while (ei.hasNext()) {
		// EdgeView eview =(EdgeView) ei.next();
		// //giny.model.Edge e = eview.getEdge();
		// giny.model.Edge e = Cytoscape.getRootGraph().getEdge(
		// eview.getRootGraphIndex() );
		// edgeList.add(e);
		// }//while

		// giny.model.Edge[] selectedEdges = ( giny.model.Edge[] )
		// edgeList.toArray (new Edge [0]);

		// giny.model.Edge[] selectedEdges = (giny.model.Edge[]) Cytoscape
		// .getCurrentNetwork().getFlaggedEdges().toArray(
		// new giny.model.Edge[0]);
		// TabbedBrowser nodeBrowser = null;
		// TabbedBrowser edgeBrowser = null;
		//
		// if (selectedNodes.length == 0 && selectedEdges.length == 0) {
		// JOptionPane.showMessageDialog(null, "No selected nodes or edges",
		// "Error", JOptionPane.ERROR_MESSAGE);
		// }
		//
		// if (selectedNodes.length > 0) {
		// nodeBrowser = new TabbedBrowser(selectedNodes, Cytoscape
		// .getNodeNetworkData(), attributeCategoriesToIgnore,
		// webBrowserScript, TabbedBrowser.BROWSING_NODES);
		// }
		//
		// if (selectedEdges.length > 0) {
		// edgeBrowser = new TabbedBrowser(selectedEdges, Cytoscape
		// .getEdgeNetworkData(), attributeCategoriesToIgnore,
		// webBrowserScript, TabbedBrowser.BROWSING_EDGES);
		// }

		// Check the state of the browser Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getState();
		int panelCount = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH).getCytoPanelComponentCount();

		int targetIndex = 0;
		String curName = null;
		
		for(int i = 0; i < panelCount; i++ ) {
			Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.SOUTH).setSelectedIndex(i);
			curName = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.SOUTH).getSelectedComponent().getName();
			//System.out.println("CurName = " + curName);
			if( curName.equals("NodeAttributeBrowser")) {
				targetIndex = i;
				Cytoscape.getDesktop().getCytoPanel(
						SwingConstants.SOUTH).setSelectedIndex(targetIndex);
				break;
			}
		}
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
					CytoPanelState.DOCK);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH)
					.setSelectedIndex(targetIndex);
			
			

		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(
					CytoPanelState.HIDE);
		}

	}// action performed
}
