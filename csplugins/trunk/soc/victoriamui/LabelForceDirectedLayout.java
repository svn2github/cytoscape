
/*
 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package csplugins.layout.algorithms.force;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;
import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.EdgeWeighter;
import csplugins.layout.algorithms.graphPartition.*;

import cytoscape.view.CyNetworkView;
import cytoscape.visual.LabelPosition;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import ding.view.DGraphView;

import java.awt.GridLayout;
import java.awt.Dimension;

import java.util.*;

import javax.swing.JPanel;

import giny.view.NodeView;

import prefuse.util.force.*;

/**
 * Force-Directed layouts for repositioning network labels.
 */
public class LabelForceDirectedLayout extends AbstractGraphPartition
{
	private ForceSimulator m_fsim;

	private int numIterations = 100;
	double defaultSpringCoefficient = 1e-4f;
	double defaultSpringLength = 50;
	double defaultNodeMass = 3.0;
	
	// The percentage of numIterations in which network nodes will also be moved
	double defaultPercentage = 0.0; // VMUI
	
	boolean moveNodes = false;

	private CyAttributes nodeAtts = Cytoscape.getNodeAttributes(); // VMUI
	
	/**
	 * Value to set for doing unweighted layouts
	 */
	public static final String UNWEIGHTEDATTRIBUTE = "(unweighted)";

	/**
	 * Integrators
	 */
	String[] integratorArray = {"Runge-Kutta", "Euler"};

	private boolean supportWeights = true;
	private LayoutProperties layoutProperties;
	Map<LayoutNode,ForceItem> forceItems;

	private Integrator integrator = null;
	
	// VMUI --->
	// Maps a label LayoutNode with its parent node's LayoutNode
	private Map<LayoutNode, LayoutNode> labelToParent = 
		new HashMap<LayoutNode, LayoutNode>();
	
	// List of all LayoutNodes; including network and label nodes
	ArrayList<LayoutNode> allLayoutNodes = new ArrayList<LayoutNode>();
	
	// List of all LayoutEdges; including network and label edges
	ArrayList<LayoutEdge> allLayoutEdges = new ArrayList<LayoutEdge>();

	// <--- VMUI
	
	public LabelForceDirectedLayout() {
		super();

		if (edgeWeighter == null)
			edgeWeighter = new EdgeWeighter();

		m_fsim = new ForceSimulator();
		m_fsim.addForce(new NBodyForce());
		m_fsim.addForce(new SpringForce());
		m_fsim.addForce(new DragForce());

		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
		forceItems = new HashMap<LayoutNode,ForceItem>();
	}
	
	public String getName() {
		return "label-force-directed"; // VMUI
	}

	public String toString() {
		return "Label Force-Directed Layout"; // VMUI
	}

	protected void initialize_local() {
	}


	public void layoutPartion(LayoutPartition part) {
		
		Dimension initialLocation = null;
		LabelPosition lp = null; // VMUI
		
		// Calculate our edge weights
		part.calculateEdgeWeights();

		m_fsim.clear();

		// VMUI --->
		// Add all existing network nodes to allLayoutNodes
		allLayoutNodes.addAll(part.getNodeList());
		
		// Create LayoutNodes and LayoutEdges for each node label
		for (LayoutNode ln : allLayoutNodes) {
				
			// Create a new LayoutNode object for the current label.
			LayoutNode labelNode = new LayoutNode(ln.getNodeView(), ln.getIndex());
			
			// Set labelNode's location to parent node's label position
			nodeAtts = Cytoscape.getNodeAttributes();
			String labelPosition = (String) nodeAtts.getAttribute(ln.getNode().
					getIdentifier(), "node.labelPosition");

			if (labelPosition == null) {
				lp = new LabelPosition();
			} else {
				lp = LabelPosition.parse(labelPosition);
			}
			
			labelNode.setX(lp.getOffsetX() + ln.getNodeView().getXPosition());
			labelNode.setY(lp.getOffsetY() + ln.getNodeView().getYPosition());
			
			// Add labelNode --> ln to labelToParent map
			labelToParent.put(labelNode, ln);
			
			// Create a new LayoutEdge between labelNode and its parent ln
			// Add this new LayoutEdge to allLayoutEdges
			LayoutEdge labelEdge = new LayoutEdge();
			labelEdge.addNodes(ln, labelNode);
			allLayoutEdges.add(labelEdge);
			
			// Lock unselected nodes; Unlock selected nodes
			labelNode.lock();
			ln.lock();
			if (!selectedOnly) { // all nodes
				labelNode.unLock();
				if (moveNodes) {
					ln.unLock();
				}
			} else if (network.getSelectedNodes().contains(ln.getNode())) { // ln selected
					labelNode.unLock();
				if (moveNodes) {
					ln.unLock();
				}
			}
			
		}
		
		// Add all labelNodes to the set of all LayoutNodes
		allLayoutNodes.addAll(labelToParent.keySet());
		
		// initialize nodes
		for (LayoutNode ln: allLayoutNodes) {
			if ( !forceItems.containsKey(ln) )
				forceItems.put(ln, new ForceItem());
			ForceItem fitem = forceItems.get(ln);
			fitem.mass = getMassValue(ln);
			fitem.location[0] = 0f; 
			fitem.location[1] = 0f;	
			m_fsim.addItem(fitem);
		}
		
		allLayoutEdges.addAll(part.getEdgeList());

		// initialize edges
		for (LayoutEdge e: allLayoutEdges) {
			LayoutNode n1 = e.getSource();
			ForceItem f1 = forceItems.get(n1); 
			LayoutNode n2 = e.getTarget();
			ForceItem f2 = forceItems.get(n2); 
			if ( f1 == null || f2 == null )
				continue;
			m_fsim.addSpring(f1, f2, getSpringCoefficient(e), getSpringLength(e));
		}
		
		// <--- VMUI

		// setTaskStatus(5); // This is a rough approximation, but probably good enough
		if (taskMonitor != null) {
			taskMonitor.setStatus("Initializing partition "+part.getPartitionNumber());
		}

		// Figure out our starting point
		if (selectedOnly) {
			initialLocation = part.getAverageLocation();
		}

		// perform layout
		long timestep = 1000L;
		for ( int i = 0; i < numIterations && !canceled; i++ ) {
			timestep *= (1.0 - i/(double)numIterations);
			long step = timestep+50;
			m_fsim.runSimulator(step);
			setTaskStatus((int)(((double)i/(double)numIterations)*90.+5));
		}
		
		// VMUI --->
		lp = new LabelPosition();
		
		// update positions
		for (LayoutNode ln: allLayoutNodes) {
			
			// ln is unlocked and selected
			if (!ln.isLocked()) {
				
				ForceItem fitem = forceItems.get(ln);
				
				// Case where ln is a label node
				if (labelToParent.containsKey(ln)) {
					
					NodeView lnNodeView = ln.getNodeView();
					nodeAtts = Cytoscape.getNodeAttributes();
					ForceItem fitemParent = forceItems.get(labelToParent.get(ln));
					
		    		lp.setOffsetX(fitem.location[0] - fitemParent.location[0]);
		    		lp.setOffsetY(fitem.location[1] - fitemParent.location[1]);
		    		
		    		nodeAtts.setAttribute(lnNodeView.getNode().getIdentifier(), 
		    				"node.labelPosition", lp.shortString());
				} else {
					// Case where ln is a network node (unlocked)
					ln.setX(fitem.location[0]);
					ln.setY(fitem.location[1]);
					part.moveNodeToLocation(ln);
				}
			}
		}
		
    	networkView.updateView();
    	networkView.redrawGraph(true, true);
		
    	// VMUI [removed code]
    	
		clear();
		
		System.out.println("Default Percentage: " + defaultPercentage);
		System.out.println("Allow nodes to move: " + moveNodes);

		// <--- VMUI
	}
	
	/**
	 * Clears all LayoutNodes and LayoutEdges created in order to successfully
	 * perform this label layout algorithm.
	 */
	private void clear() { // VMUI
		allLayoutNodes.clear();
		labelToParent.clear();
		allLayoutEdges.clear();
	}

	/**
	 * Get the mass value associated with the given node. Subclasses should
	 * override this method to perform custom mass assignment.
	 * @param n the node for which to compute the mass value
	 * @return the mass value for the node. By default, all items are given
	 * a mass value of 1.0.
	 */
	protected float getMassValue(LayoutNode n) {
		return (float)defaultNodeMass;
	}

	/**
	 * Get the spring length for the given edge. Subclasses should
	 * override this method to perform custom spring length assignment.
	 * @param e the edge for which to compute the spring length
	 * @return the spring length for the edge. A return value of
	 * -1 means to ignore this method and use the global default.
	*/
	protected float getSpringLength(LayoutEdge e) {
		double weight = e.getWeight();
		return (float)(defaultSpringLength/weight);
	}

	/**
	 * Get the spring coefficient for the given edge, which controls the
	 * tension or strength of the spring. Subclasses should
	 * override this method to perform custom spring tension assignment.
	 * @param e the edge for which to compute the spring coefficient.
	 * @return the spring coefficient for the edge. A return value of
	 * -1 means to ignore this method and use the global default.
	 */
	protected float getSpringCoefficient(LayoutEdge e) {
		return (float)defaultSpringCoefficient;
	}

	/**
	 * Return information about our algorithm
	 */
	public boolean supportsSelectedOnly() {
		return true;
	}

	public byte[] supportsEdgeAttributes() {
		if (!supportWeights)
			return null;
		byte[] attrs = { CyAttributes.TYPE_INTEGER, CyAttributes.TYPE_FLOATING };

		return attrs;
	}

	public List getInitialAttributeList() {
		ArrayList list = new ArrayList();
		list.add(UNWEIGHTEDATTRIBUTE);

		return list;
	}

	
	protected void initialize_properties() {

		layoutProperties.add(new Tunable("standard", "Standard settings",
		                                 Tunable.GROUP, new Integer(3))); // VMUI

		layoutProperties.add(new Tunable("partition", "Partition graph before layout",
		                                 Tunable.BOOLEAN, new Boolean(true)));

		layoutProperties.add(new Tunable("selected_only", "Only layout selected nodes",
		                                 Tunable.BOOLEAN, new Boolean(false)));
		
		// VMUI
		layoutProperties.add(new Tunable("moveNodes", "Allow nodes to move",
                Tunable.BOOLEAN, new Boolean(false)));

		edgeWeighter.getWeightTunables(layoutProperties, getInitialAttributeList());

		layoutProperties.add(new Tunable("force_alg_settings", "Algorithm settings",
		                                 Tunable.GROUP, new Integer(5)));

		layoutProperties.add(new Tunable("defaultSpringCoefficient", "Default Spring Coefficient",
		                                 Tunable.DOUBLE, new Double(defaultSpringCoefficient)));

		layoutProperties.add(new Tunable("defaultSpringLength", "Default Spring Length",
		                                 Tunable.DOUBLE, new Double(defaultSpringLength)));
		
		// VMUI
		layoutProperties.add(new Tunable("defaultPercentage", "Default Percentage (%)",
				Tunable.DOUBLE, new Double(defaultPercentage)));

		layoutProperties.add(new Tunable("defaultNodeMass", "Default Node Mass",
		                                 Tunable.DOUBLE, new Double(defaultNodeMass)));

		layoutProperties.add(new Tunable("numIterations", "Number of Iterations",
		                                 Tunable.INTEGER, new Integer(numIterations)));

		layoutProperties.add(new Tunable("integrator", "Integration algorithm to use",
		                                 Tunable.LIST, new Integer(0), 
		                                 (Object) integratorArray, (Object) null, 0));

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

	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("selected_only");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
		
		// VMUI
		t = layoutProperties.get("moveNodes");
		if ((t != null) && (t.valueChanged() || force))
			moveNodes = ((Boolean) t.getValue()).booleanValue();

		t = layoutProperties.get("partition");
		if ((t != null) && (t.valueChanged() || force))
			setPartition(t.getValue().toString());

		t = layoutProperties.get("defaultSpringCoefficient");
		if ((t != null) && (t.valueChanged() || force))
			defaultSpringCoefficient = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("defaultSpringLength");
		if ((t != null) && (t.valueChanged() || force))
			defaultSpringLength = ((Double) t.getValue()).doubleValue();
		
		// VMUI
		t = layoutProperties.get("defaultPercentage");
		if ((t != null) && (t.valueChanged() || force))
			defaultPercentage = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("defaultNodeMass");
		if ((t != null) && (t.valueChanged() || force))
			defaultNodeMass = ((Double) t.getValue()).doubleValue();

		t = layoutProperties.get("numIterations");
		if ((t != null) && (t.valueChanged() || force))
			numIterations = ((Integer) t.getValue()).intValue();

		t = layoutProperties.get("integrator");
		if ((t != null) && (t.valueChanged() || force)) {
			if (((Integer) t.getValue()).intValue() == 0)
				integrator = new RungeKuttaIntegrator();
			else if (((Integer) t.getValue()).intValue() == 1)
				integrator = new EulerIntegrator();
			else
				return;
			m_fsim.setIntegrator(integrator);
		}

		edgeWeighter.updateSettings(layoutProperties, force);
	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	public JPanel getSettingsPanel() {
		// JPanel panel = new JPanel(new GridLayout(0, 1));
		// panel.add(layoutProperties.getTunablePanel());
		// return panel;
		return layoutProperties.getTunablePanel();

	}
}
