package csplugins.layout.algorithms;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.LayoutTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.TaskMonitor;

public class StackedNodeLayoutTask extends LayoutTask {

	private double y_start_position;
	private double x_position;
	private Collection nodes;

	public StackedNodeLayoutTask(final CyNetworkView networkView, final String name,
			  final boolean selectedOnly, final Set<View<CyNode>> staticNodes)

	{
		super(networkView, name, selectedOnly, staticNodes);
	}

	final protected void doLayout(final TaskMonitor taskMonitor, final CyNetwork network) {
		
		construct();
	}
	
	/**
	 *  DOCUMENT ME!
	 */
	public void construct() {
		Iterator it = nodes.iterator();
		double yPosition = y_start_position;

		while (it.hasNext()) {
			CyNode node = (CyNode) it.next();
			View<CyNode> nodeView = networkView.getNodeView(node);
			nodeView.setVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION, x_position);
			nodeView.setVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION, yPosition);
			yPosition += (nodeView.getVisualProperty(TwoDVisualLexicon.NODE_Y_SIZE) * 2);
		}
	}


	
}
