
package cytoscape.layout.label;

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutPartition;
import csplugins.layout.Profile;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.visual.LabelPosition;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.logger.CyLogger;

import java.awt.Dimension;

import giny.view.NodeView;

import java.util.*;



// TODO: Change this Javadoc description 

/**
 * Lays out the nodes in a graph using a modification of the Fruchterman-Rheingold
 * algorithm.
 * <p>
 * The basic layout algorithm follows from the paper:
 * <em>"Graph Drawing by Force-Directed Placement"</em>
 * by Thomas M.J. Fruchterman and Edward M. Reingold.
 * <p>
 * The algorithm has been modified to take into account edge weights, which
 * allows for its use for laying out similarity networks, which are useful
 * for biological problems.
 *
 * @author <a href="mailto:scooter@cgl.ucsf.edu">Scooter Morris</a>
 * @version 0.9
 */
public class LabelBioLayoutFRAlgorithm extends ModifiedBioLayoutFRAlgorithm {

    // LABEL -->    

    private double label_attraction_multiplier = 0.1;
    private double label_repulsion_multiplier = 0.1;
    private double label_attraction_constant;
    private double label_repulsion_constant;

    /**
     * Whether Labels should be repositioned in their default positions 
     */
    private boolean resetPosition = false;

    /**
     * The percentage of numIterations in which network nodes will also be moved
     */
    private double defaultPercentage = 80.0; 
    
    /**
     * Whether network nodes will be moved or not 
     */
    private boolean moveNodes = false;

    /**
     *
     */
    private CyAttributes nodeAtts = Cytoscape.getNodeAttributes(); 
   
    /**
     * Maps a label LayoutNode to its parent node's LayoutNode
     */
    private Map<LayoutNode, LayoutNode> labelToParentMap = 
	new HashMap<LayoutNode, LayoutNode>();
    
    /**
     * List of all LayoutNodes; including network and label nodes
     */
    ArrayList<LayoutNode> allLayoutNodesArray = new ArrayList<LayoutNode>();

    /**
     * List of all LayoutEdges; including network and label edges
     */
    ArrayList<LayoutEdge> allLayoutEdgesArray = new ArrayList<LayoutEdge>();


    // <-- LABEL

    /**
     * This is the constructor for the bioLayout algorithm.
     */
    public LabelBioLayoutFRAlgorithm(boolean supportEdgeWeights) {
	super();
	logger = CyLogger.getLogger(LabelBioLayoutFRAlgorithm.class);

	supportWeights = supportEdgeWeights;

	displacementArray = new ArrayList<Double>(100);
	this.initializeProperties();
    }

    /**
     * Required methods (and overrides) for AbstractLayout
     */

    /**
     * Return the "name" of this algorithm.  This is meant
     * to be used by programs for deciding which algorithm to
     * use.  toString() should be used for the human-readable
     * name.
     *
     * @return the algorithm name
     */
    public String getName() {
	return "Fruchterman-Rheingold-Label-Layout";
    }

    /**
     * Return the "title" of this algorithm.  This is meant
     * to be used for titles and labels that represent this
     * algorithm.
     *
     * @return the human-readable algorithm name
     */
    public String toString() {
	if (supportWeights)
	    return "Edge-Weighted Force-Directed Label Layout (Label BioLayout)";
	else

	    return "Force-Directed Label Layout (Label BioLayout)";
    }



    /**
     * Reads all of our properties from the cytoscape properties map and sets
     * the values as appropriates.
     */
    public void initializeProperties() {
	super.initializeProperties();

	/**
	 * Adds Tunables
	 */

	// LABEL -->

	layoutProperties.add(new Tunable("labels_settings", 
					 "Algorithm's label specific settings",
					 Tunable.GROUP, new Integer(5))); 

	layoutProperties.add(new Tunable("resetPosition", 
					 "Reset the label position of all nodes",
					 Tunable.BOOLEAN, new Boolean(false)));

	layoutProperties.add(new Tunable("moveNodes", 
					 "Allow nodes to move",
					 Tunable.BOOLEAN, new Boolean(false)));

	layoutProperties.add(new Tunable("defaultPercentage", 
					 "Default Percentage (%) [For how long nodes are going to be allowed to move]",
					 Tunable.DOUBLE, new Double(defaultPercentage)));

	layoutProperties.add(new Tunable("label-repulsion_multiplier",
					 "Multiplier to calculate the repulsion force between labels and nodes",
					 Tunable.DOUBLE, new Double(0.01)));

	layoutProperties.add(new Tunable("label-attraction_multiplier",
					 "Divisor to calculate the attraction force between nodes and their labels",
					 Tunable.DOUBLE, new Double(0.01)));
	// <-- LABEL

	// We've now set all of our tunables, so we can read the property 
	// file now and adjust as appropriate
	layoutProperties.initializeProperties();

	// Finally, update everything.  We need to do this to update
	// any of our values based on what we read from the property file
	updateSettings(true);
    }

    /**
     *  update our tunable settings
     */
    public void updateSettings() {
	updateSettings(false);
    }

    /**
     *  update our tunable settings
     *
     * @param force whether or not to force the update
     */
    public void updateSettings(boolean force) {
	super.updateSettings(force);

	// LABEL -->

	Tunable t = layoutProperties.get("resetPosition");
	if ((t != null) && (t.valueChanged() || force))
	    resetPosition = ((Boolean) t.getValue()).booleanValue();

	t = layoutProperties.get("moveNodes");
	if ((t != null) && (t.valueChanged() || force))
	    moveNodes = ((Boolean) t.getValue()).booleanValue();

	t = layoutProperties.get("defaultPercentage");
	if ((t != null) && (t.valueChanged() || force))
	    defaultPercentage = ((Double) t.getValue()).doubleValue();

	t = layoutProperties.get("label-repulsion_multiplier");
	if ((t != null) && (t.valueChanged() || force)) {
	    setLabelRepulsionMultiplier(t.getValue().toString());
	    if (t.valueChanged())
		layoutProperties.setProperty(t.getName(), t.getValue().toString());
	}

	t = layoutProperties.get("label-attraction_multiplier");
	if ((t != null) && (t.valueChanged() || force)) {
	    setLabelAttractionMultiplier(t.getValue().toString());
	    if (t.valueChanged())
		layoutProperties.setProperty(t.getName(), t.getValue().toString());
	}

	// <-- LABEL

    }

    /**
     * Perform a layout
     */
    public void layoutPartion(LayoutPartition partition) {
	this.partition = partition;

	Dimension initialLocation = null;

	// Logs information about this task
	logger.info("Laying out partition " + partition.getPartitionNumber() + " which has "+ partition.nodeCount()
		    + " nodes and " + partition.edgeCount() + " edges: ");



	// LABEL -->

	// Reset the label position of all nodes if necessary 
	if (resetPosition) {
	    resetNodeLabelPosition(nodeAtts, partition.getNodeList());
	    return;
	}

	// Handle if defaultPercentage is not a valid percentage
	if (defaultPercentage > 100.0) {
	    defaultPercentage = 100.0;
	} else if (defaultPercentage < 0.0) {
	    defaultPercentage = 0.0;
	}

	// Ads all network nodes to list
	allLayoutNodesArray.addAll(partition.getNodeList());
	
	LayoutNode labelNode;
	LabelPosition lp = null; 
	
	// --- Create LayoutNodes and LayoutEdges for each node label ---
	for (LayoutNode ln : allLayoutNodesArray) {
	    
	    // Create a new node
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
			
	    // Add labelNode --> ln to labelToParentMap
	    labelToParentMap.put(labelNode, ln);
			
	    // Create a new LayoutEdge between labelNode and its parent ln
	    // Add this new LayoutEdge to allLayoutEdges
	    LayoutEdge labelEdge = new LayoutEdge();
	    labelEdge.addNodes(ln, labelNode);
	    allLayoutEdgesArray.add(labelEdge);
			
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
		
	// Adds all LabelNodes (who are the keys in labelToParentMap) to allLayoutNodesArray
	allLayoutNodesArray.addAll(labelToParentMap.keySet());

	// LABEL <--




	// LABEL -->

	updatePositions();

	// <-- LABEL



	// Not quite done, yet.  If we're only laying out selected nodes, we need
	// to migrate the selected nodes back to their starting position
	if (selectedOnly) {
	    double xDelta = 0.0;
	    double yDelta = 0.0;
	    Dimension finalLocation = partition.getAverageLocation();
	    xDelta = finalLocation.getWidth() - initialLocation.getWidth();
	    yDelta = finalLocation.getHeight() - initialLocation.getHeight();

	    for (LayoutNode v: partition.getNodeList()) {
		if (!v.isLocked()) {
		    v.decrement(xDelta, yDelta);
		    partition.moveNodeToLocation(v);
		}
	    }
	}

    	networkView.updateView();
    	networkView.redrawGraph(true, true);
   
	clear();		

	logger.info("Label/Node layout of partition " + partition.getPartitionNumber() + " complete after " + iteration + " iterations");
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
     * Clears all LayoutNodes and LayoutEdges
     */
    private void clear() { // LABEL-LAYOUT
	allLayoutNodesArray.clear();
	labelToParentMap.clear();
	allLayoutEdgesArray.clear();
    }

    /**
     * Updates the position of labels and nodes
     */
    private void updatePositions() {

	LabelPosition lp = new LabelPosition(); 
		
        for (LayoutNode ln: allLayoutNodesArray) { 
            
            if (!ln.isLocked()) {
                
                if (labelToParentMap.containsKey(ln)) { // If it is a Label Node
                	
                    // Get ln and its parent positions
                    NodeView lnNodeView = ln.getNodeView();
                    nodeAtts = Cytoscape.getNodeAttributes();
		    LayoutNode lParent = labelToParentMap.get(ln);

                    // Reposition
                    lp.setOffsetX(ln.getX() - lParent.getX());
                    lp.setOffsetY(ln.getY() - lParent.getY());
                    nodeAtts.setAttribute(lnNodeView.getNode().getIdentifier(),
					  "node.labelPosition", lp.shortString());
                    
                } else { // ln is a network LayoutNode
                	
		    if (moveNodes && defaultPercentage != 0.0) { // unlocked
			partition.moveNodeToLocation(ln);
		    }
                }
            }
        }


    }


}