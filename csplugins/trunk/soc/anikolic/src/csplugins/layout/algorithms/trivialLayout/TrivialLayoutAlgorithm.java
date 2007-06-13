package csplugins.layout.algorithms.trivialLayout;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import cytoscape.task.TaskMonitor;

import cytoscape.view.CyNetworkView;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.GridLayout;

import java.util.*;

import javax.swing.JPanel;


public class TrivialLayoutAlgorithm extends AbstractLayout
{
	private int nodeHorizontalSpacing = 64;
	private int nodeVerticalSpacing = 32;
	private int leftEdge = 32;
	private int topEdge = 32;
	private int rightMargin = 1000;
	
	private boolean selected_only = false;
	private LayoutProperties layoutProperties;
	
	public TrivialLayoutAlgorithm() 
	{
		super();
		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
	}
	
	public boolean supportsSelectedOnly() 
	{
		return true;
	}
	
	public void construct() 
	{
		taskMonitor.setStatus("Initializing");
		initialize(); // Calls initialize_local
		layout();
		
	}

	public String getName() 
	{
		return "trivial";
	}

	public String toString() 
	{
		return "Trivial Layout";
	}
	
	public void layout() 
	{
		taskMonitor.setPercentCompleted(0);
		taskMonitor.setStatus("Capturing snapshot of network and selected nodes");

		if (canceled)
			return;
		
		List selectedNodes = networkView.getSelectedNodes();
		int numSelectedNodes = selectedNodes.size();

		if (!selectedOnly)
			numSelectedNodes = 0;

		if (numSelectedNodes == 1) {
			// We were asked to do a trivial layout of a single node -- done!
			return;
		}

		final int numNodes = networkView.getNodeViewCount();
		final int numLayoutNodes = (numSelectedNodes < 1) ? numNodes : numSelectedNodes;

		Iterator iter;
		if (numSelectedNodes > 1) 
		{
			iter = selectedNodes.iterator();
		}
		else
		{
			iter = networkView.getNodeViewsIterator(); /* all nodes */
		}
		NodeView[] nodeView = new NodeView[numLayoutNodes];
		int nextNode = 0; 
		while (iter.hasNext() && !canceled) 
		{
			NodeView nv = (NodeView) (iter.next());
			nodeView[nextNode++] = nv;
		}
		
		int startX = leftEdge;
		int startY = topEdge;
		for (int i = 0; i < nextNode / 3; i++)
		{
			nodeView[i].setOffset(startX, startY);
			startX += nodeHorizontalSpacing;
		}
		
		startX = leftEdge;
		startY = topEdge;
		int secondPoint = (nextNode / 3) * nodeHorizontalSpacing + leftEdge;
		int thirdPointX = (secondPoint + startX) / 2;
		int deltaX = (thirdPointX - startX) / (nextNode / 3);
		for (int i = nextNode / 3; i < 2 * (nextNode / 3); i++)
		{
			nodeView[i].setOffset(startX, startY);
			startX += deltaX;
			startY += nodeVerticalSpacing;
		}
		
		startX = secondPoint;
		startY = topEdge;		
		for (int i = 2*(nextNode/3); i < nextNode; i++)
		{
			nodeView[i].setOffset(startX, startY);
			startX -= deltaX;
			startY += nodeVerticalSpacing;
		}
		
		taskMonitor.setPercentCompleted(100);
		taskMonitor.setStatus("trivial layout complete");

	}
	
	public void halt() {
		canceled = true;
	}
	
	protected void initialize_properties() {
		layoutProperties.add(new Tunable("nodeHorizontalSpacing",
		                                 "Horizontal spacing between nodes", Tunable.INTEGER,
		                                 new Integer(64)));
		layoutProperties.add(new Tunable("nodeVerticalSpacing", "Vertical spacing between nodes",
		                                 Tunable.INTEGER, new Integer(32)));
		
		layoutProperties.add(new Tunable("leftEdge", "Left edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("topEdge", "Top edge margin", Tunable.INTEGER,
		                                 new Integer(32)));
		layoutProperties.add(new Tunable("rightMargin", "Right edge margin", Tunable.INTEGER,
		                                 new Integer(1000)));
		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));
		// We've now set all of our tunables, so we can read the property 
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything.  We need to do this to update
		// any of our values based on what we read from the property file
		updateSettings(true);
	}

	public void updateSettings() {
		updateSettings(false);
	}

	public void setTaskMonitor(TaskMonitor tm) {
		taskMonitor = tm;
	}
	/**
	 *  DOCUMENT ME!
	 *
	 * @param force DOCUMENT ME!
	 */
	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("nodeHorizontalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeVerticalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("nodeVerticalSpacing");

		if ((t != null) && (t.valueChanged() || force))
			nodeVerticalSpacing = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("leftEdge");

		if ((t != null) && (t.valueChanged() || force))
			leftEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("topEdge");

		if ((t != null) && (t.valueChanged() || force))
			topEdge = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("rightMargin");

		if ((t != null) && (t.valueChanged() || force))
			rightMargin = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("selected_only");

		if ((t != null) && (t.valueChanged() || force))
			selected_only = ((Boolean) t.getValue()).booleanValue();
	}

	public JPanel getSettingsPanel() {
		JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.add(layoutProperties.getTunablePanel());

		return panel;
	}

	
	public String getTitle() {
		return new String("Trivial Layout");
	}
}
