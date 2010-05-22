package cytoscape.layout.label;

import cytoscape.Cytoscape;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.CyNode;
import cytoscape.visual.LabelPosition;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.layout.LayoutProperties;

import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.EdgeWeighter;
import csplugins.layout.algorithms.graphPartition.*;

import giny.view.NodeView;

import prefuse.util.force.*;

import java.util.*;

import javax.swing.JPanel;

/**
 * This class is a label plugin that is capable of repositioning both nodes
 * and labels of a network in order to improve the network's readability.
 * The degree to which nodes and labels are moved is controlled by the values
 * that users enter for the fields that control the algorithm, found in 
 * Layout --> Settings --> Label Force-Directed Layout.  All node and label
 * movement is based on the Force-Directed Layout algorithm.
 * 
 * NOTE: This class is based on the Force-Directed Layout class 
 * (csplugins.layout.algorithms.force).  Modifications were made to the
 * Force-Directed Layout class, and indicated by "// vmui".  The beginning of
 * a block of modified code is indicated by "// vmui -->", while the end is
 * marked by "// <-- vmui".
 */
public class LabelForceDirectedLayout extends AbstractGraphPartition
{
	// vmui
	private boolean resetPosition = false;
	
	// vmui
	private ForceSimulator label_sim;
	private ForceSimulator node_sim;

	private int numIterations = 100;
	double defaultNodeMass = 3.0;
	
	// Spring coefficient and length of network edges
	double defaultSpringCoefficient = 0.000005;
	double defaultSpringLength = 50;
	
	// Spring coefficient and length of label edges (those connecting a label
	// LayoutNode with a network LayoutNode) - vmui
	double defaultLabelSpringCoefficient = 0.5;
	double defaultLabelSpringLength = 5.0;
	
	// The percentage of numIterations in which network nodes will also be moved
	double defaultPercentage = 80.0; // vmui
	
	// Whether network nodes will be moved or not - vmui
	boolean moveNodes = false;

	private CyAttributes nodeAtts = Cytoscape.getNodeAttributes(); // vmui
	
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
	
	// vmui --->
	// Maps a label LayoutNode to its parent node's LayoutNode
	private Map<LayoutNode, LayoutNode> labelToParent = 
		new HashMap<LayoutNode, LayoutNode>();
	
	// List of all LayoutNodes; including network and label nodes
	ArrayList<LayoutNode> allLayoutNodes = new ArrayList<LayoutNode>();
	
	// List of all LayoutEdges; including network and label edges
	ArrayList<LayoutEdge> allLayoutEdges = new ArrayList<LayoutEdge>();

	// <--- vmui
	
	public LabelForceDirectedLayout() {
		super();

		if (edgeWeighter == null)
			edgeWeighter = new EdgeWeighter();
		
		// vmui
		node_sim = new ForceSimulator();
		node_sim.addForce(new NBodyForce());
		node_sim.addForce(new SpringForce());
		node_sim.addForce(new DragForce());
		
		// vmui
		label_sim = new ForceSimulator();
		label_sim.addForce(new NBodyForce());
		label_sim.addForce(new SpringForce());
		label_sim.addForce(new DragForce());

		layoutProperties = new LayoutProperties(getName());
		initialize_properties();
		forceItems = new HashMap<LayoutNode,ForceItem>();
	}
	
	public String getName() {
		return "label-force-directed"; // vmui
	}

	public String toString() {
		return "Label Force-Directed Layout"; // vmui
	}

	protected void initialize_local() {
	}


	public void layoutPartion(LayoutPartition part) {
		
		// Reset the label position of all nodes - vmui
		if (resetPosition) {
			resetNodeLabelPosition(nodeAtts, part.getNodeList());
			return; // Finishes the execution of this layout algorithm
		}
		
		LabelPosition lp = null; // vmui

		// vmui
		node_sim.clear();
		label_sim.clear();
		
		part.calculateEdgeWeights();
		
		// vmui --->
		allLayoutNodes.addAll(part.getNodeList());
		
		LayoutNode labelNode;
		
		// --- Create LayoutNodes and LayoutEdges for each node label ---
		for (LayoutNode ln : allLayoutNodes) {
				
			labelNode = new LayoutNode(ln.getNodeView(), ln.getIndex());
			
			// Set labelNode's location to parent node's current label position
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
			
			/* Unlock labelNode if:
			 * - algorithm is to be applied to the entire network
			 * - algorithm is to be applied to the selected nodes only, and ln
			 * is selected
			 * 
			 * Unlock ln if:
			 * - either of the above conditions is true, and the user has
			 * specified that the network nodes are to be moved as well
			 * 
			 * Lock labelNode and/or ln otherwise. */
			labelNode.lock();
			ln.lock();
			if (!selectedOnly 
					|| network.getSelectedNodes().contains(ln.getNode())) {
				labelNode.unLock();
				if (moveNodes) {
					ln.unLock();
				}
			}
			
		}
		
		allLayoutNodes.addAll(labelToParent.keySet());
		
		// --- initialize nodes ---
		for (LayoutNode ln: allLayoutNodes) {
			if ( !forceItems.containsKey(ln) )
				forceItems.put(ln, new ForceItem());
			ForceItem fitem = forceItems.get(ln);
			fitem.mass = getMassValue(ln);
			fitem.location[0] = 0f; 
			fitem.location[1] = 0f;
			
			// Depending on whether ln is a label or a node, add them to 
			// label_sim or node_sim accordingly.
			if (labelToParent.containsKey(ln)) { // vmui
				label_sim.addItem(fitem);
			} else {
				node_sim.addItem(fitem);
			}
		}
		
		allLayoutEdges.addAll(part.getEdgeList());

		// --- initialize edges ---
		for (LayoutEdge e: allLayoutEdges) {
			LayoutNode n1 = e.getSource();
			ForceItem f1 = forceItems.get(n1); 
			LayoutNode n2 = e.getTarget();
			ForceItem f2 = forceItems.get(n2); 
			if ( f1 == null || f2 == null )
				continue;
			
			/* Depending on whether e is a label or network edge, add a new
			 * spring to label_sim or node_sim accordingly, with the correct
			 * spring length and coefficient. */
			if (labelToParent.containsKey(n1) 
					|| labelToParent.containsKey(n2)) {
				// Case: n1 or n2 are label nodes; e is label edge.
				label_sim.addSpring(f1, f2, getLabelSpringCoefficient(e), 
						getLabelSpringLength(e));
			} else {
				node_sim.addSpring(f1, f2, getSpringCoefficient(e),
						getSpringLength(e));
			}
			
		} // <--- vmui

		// setTaskStatus(5); // This is a rough approximation, but probably good enough
		if (taskMonitor != null) {
			taskMonitor.setStatus("Initializing partition "+part.getPartitionNumber());
		}
		
		// --- Prepare for layout execution --- vmui
		double multiple = 0;
		
		// Handle if defaultPercentage is not a valid percentage
		if (defaultPercentage > 100.0) {
			defaultPercentage = 100.0;
		} else if (defaultPercentage < 0.0) {
			defaultPercentage = 0.0;
		}
		
		// Calculate multiple if network nodes are to move
		if (moveNodes && defaultPercentage != 0.0 && numIterations != 0) {
			multiple = numIterations / (defaultPercentage / 100.0 * numIterations);
		}
		
		
		// --- Perform layout ---
		long timestep = 1000L;
		
		Multiples mult = new Multiples(multiple); // vmui
		
		for ( int i = 0; i < numIterations && !canceled; i++ ) {
			timestep *= (1.0 - i/(double)numIterations);
			long step = timestep+50;
			
			/* Apply the algorithm on network nodes if i = multiple * j, where
			 * j is an integer.
			 * 
			 * Since i is an int, it needs to be compared with an int.  
			 * However, the value of getCurrent() isn't necessarily always an 
			 * int, so the ceiling of this value will be used instead.  Notice 
			 * that some rounding errors may occur.
			 */
			if (multiple != 0.0 && i == Math.ceil(mult.getCurrent())) { // vmui
				node_sim.runSimulator(step);
				mult.next();
			}
			
			label_sim.runSimulator(step); // vmui
			
			setTaskStatus((int)(((double)i/(double)numIterations)*90.+5));
		}
		

		// --- Update positions ---
		lp = new LabelPosition(); // vmui
		
        for (LayoutNode ln: allLayoutNodes) { // vmui
            
            if (!ln.isLocked()) {
                
                ForceItem fitem = forceItems.get(ln);
                
                if (labelToParent.containsKey(ln)) {
                	
                    // ln is a label LayoutNode
                    NodeView lnNodeView = ln.getNodeView();
                    nodeAtts = Cytoscape.getNodeAttributes();
                    ForceItem fitemParent = forceItems.get(labelToParent.get(ln));
                    
                    // Reposition
                    lp.setOffsetX(fitem.location[0] - fitemParent.location[0]);
                    lp.setOffsetY(fitem.location[1] - fitemParent.location[1]);
                    nodeAtts.setAttribute(lnNodeView.getNode().getIdentifier(),
                            "node.labelPosition", lp.shortString());
                    
                } else { // ln is a network LayoutNode
                	
                	if (moveNodes && defaultPercentage != 0.0) { // unlocked
                        ln.setX(fitem.location[0]);
                        ln.setY(fitem.location[1]);
                        part.moveNodeToLocation(ln);
                	}
                }
            }
        }
		
		// vmui -->
    	networkView.updateView();
    	networkView.redrawGraph(true, true);
   
		clear();
		
		// <-- vmui
	}
	
	/**
	 * Clears all LayoutNodes and LayoutEdges created in order to successfully
	 * perform this label layout algorithm.
	 */
	private void clear() { // vmui
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
	 * Gets the spring length of the given label edge.
	 */
	protected float getLabelSpringLength(LayoutEdge e) { // vmui
		double weight = e.getWeight();
		return (float)(defaultLabelSpringLength/weight);
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
	 * Gets the spring coefficient for the given label edge.
	 */
	protected float getLabelSpringCoefficient(LayoutEdge e) { // vmui
		return (float)defaultLabelSpringCoefficient;
	}

	/**
	 * Return information about our algorithm
	 */
	public boolean supportsSelectedOnly() {
		return true; // vmui
	}

	public byte[] supportsEdgeAttributes() {
		return null;
	}
	
	public List getInitialAttributeList() {
		ArrayList list = new ArrayList();
		list.add(UNWEIGHTEDATTRIBUTE);

		return list;
	}
	
	protected void initialize_properties() {

		layoutProperties.add(new Tunable("standard", 
				"Standard settings",
				Tunable.GROUP, new Integer(3))); // vmui

		layoutProperties.add(new Tunable("partition", 
				"Partition graph before layout",
				Tunable.BOOLEAN, new Boolean(true)));

		layoutProperties.add(new Tunable("selected_only", 
				"Only layout selected nodes",
				Tunable.BOOLEAN, new Boolean(false)));
		
		// vmui
		layoutProperties.add(new Tunable("moveNodes", 
				"Allow nodes to move",
                Tunable.BOOLEAN, new Boolean(false)));

		layoutProperties.add(new Tunable("force_alg_settings", 
				"Algorithm settings",
				Tunable.GROUP, new Integer(7))); // vmui

		layoutProperties.add(new Tunable("defaultSpringCoefficient", 
				"Default Spring Coefficient",
				Tunable.DOUBLE, new Double(defaultSpringCoefficient)));

		layoutProperties.add(new Tunable("defaultSpringLength", 
				"Default Spring Length",
				Tunable.DOUBLE, new Double(defaultSpringLength)));
		
		// vmui
		layoutProperties.add(new Tunable("defaultPercentage", 
				"Default Percentage (%)",
				Tunable.DOUBLE, new Double(defaultPercentage)));
		
		// vmui
		layoutProperties.add(new Tunable("defaultLabelSpringCoefficient", 
				"Default Label Spring Coefficient",
                Tunable.DOUBLE, new Double(defaultLabelSpringCoefficient)));
		
		// vmui
		layoutProperties.add(new Tunable("defaultLabelSpringLength", 
				"Default Label Spring Length",
                Tunable.DOUBLE, new Double(defaultLabelSpringLength)));

		layoutProperties.add(new Tunable("defaultNodeMass", 
				"Default Node Mass",
				Tunable.DOUBLE, new Double(defaultNodeMass)));

		layoutProperties.add(new Tunable("numIterations", 
				"Number of Iterations",
				Tunable.INTEGER, new Integer(numIterations)));

		layoutProperties.add(new Tunable("integrator", 
				"Integration algorithm to use",
				Tunable.LIST, new Integer(0),
				(Object) integratorArray, (Object) null, 0));
		
		// vmui
		layoutProperties.add(new Tunable("resetPosition", 
				"Reset the label position of all nodes",
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

	public void updateSettings(boolean force) {
		layoutProperties.updateValues();

		Tunable t = layoutProperties.get("selected_only");
		if ((t != null) && (t.valueChanged() || force))
			selectedOnly = ((Boolean) t.getValue()).booleanValue();
		
		// vmui
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
		
		// vmui
		t = layoutProperties.get("defaultLabelSpringCoefficient");
		if ((t != null) && (t.valueChanged() || force))
			defaultLabelSpringCoefficient = ((Double) t.getValue()).doubleValue();

		// vmui
		t = layoutProperties.get("defaultLabelSpringLength");
		if ((t != null) && (t.valueChanged() || force))
			defaultLabelSpringLength = ((Double) t.getValue()).doubleValue();
		
		// vmui
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
			
			// vmui
			label_sim.setIntegrator(integrator);
			node_sim.setIntegrator(integrator);
		}
		
		// vmui
		t = layoutProperties.get("resetPosition");
		if ((t != null) && (t.valueChanged() || force))
			resetPosition = ((Boolean) t.getValue()).booleanValue();

	}

	public LayoutProperties getSettings() {
		return layoutProperties;
	}

	public JPanel getSettingsPanel() {
		return layoutProperties.getTunablePanel();

	}
	
	public void resetNodeLabelPosition(CyAttributes nodeAtts, List<LayoutNode> nodeList) {
		for(LayoutNode n : nodeList) {
			if (nodeAtts.hasAttribute(n.getIdentifier(), "node.labelPosition")) {
				nodeAtts.deleteAttribute(n.getIdentifier(), "node.labelPosition");
			}
		}
    	networkView.updateView();
    	networkView.redrawGraph(true, true);
	}
	
	
	/**
	 * Generates and returns multiples of the double specified in the 
	 * constructor.
	 * @author vmui & jbalogh
	 *
	 */
	public class Multiples {
		
		// The number in which this class is to generate multiples of
		final private double multipleOf;
		
		// The current multiple of multipleOf
		double current = 0;
	
		/** 
		 * Creates an instance of Multiple.
		 * @param multipleOf the number this class is to generate multiples of
		 */
		public Multiples(double multipleOf) {
			this.multipleOf = multipleOf;
		}
		
		/**
		 * @return the current multiple of multipleOf
		 */
		public double getCurrent() {
			return current;
		}
		
		/**
		 * Updates current so that it is now the next multiple of multipleOf.
		 */
		public void next() {
			current += multipleOf;
		}
		
	}
}
