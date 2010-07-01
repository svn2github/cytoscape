

package cytoscape.layout.label;

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutLabelNodeImpl;
import csplugins.layout.LayoutPartition;
import csplugins.layout.LayoutLabelPartition;
import csplugins.layout.Profile;

import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.logger.CyLogger;

import java.awt.Dimension;

import giny.view.NodeView;
import giny.view.*;

import java.util.*;

import java.lang.Thread;


public class LabelBioLayoutFRAlgorithm extends ModifiedBioLayoutFRAlgorithm {


    /**
     * Whether Labels should be repositioned in their default positions 
     */
    private boolean resetPosition = false;
    
    /**
     * Whether network nodes will be moved or not 
     */
    private boolean moveNodes = false;

    /**
     * Coefficient to determine label edge weights
     */
    private double weightCoefficient = 2.0;


    
    /**
     * This is the constructor for the bioLayout algorithm.
     */
    public LabelBioLayoutFRAlgorithm(boolean supportEdgeWeights) {

	super(supportEdgeWeights);
	
	logger = CyLogger.getLogger(LabelBioLayoutFRAlgorithm.class);

	supportWeights = supportEdgeWeights;

	this.initializeProperties();
    }

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

	layoutProperties.add(new Tunable("labels_settings", 
					 "Label specific settings",
					 Tunable.GROUP, new Integer(3))); 

	layoutProperties.add(new Tunable("resetPosition", 
					 "Reset label positions",
					 Tunable.BOOLEAN, new Boolean(false)));

	layoutProperties.add(new Tunable("moveNodes", 
					 "Allow nodes to move",
					 Tunable.BOOLEAN, new Boolean(false)));

	layoutProperties.add(new Tunable("weightCoefficient", 
					 "weightCoefficient",
					 Tunable.DOUBLE, new Double(2.0)));

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

	Tunable t = layoutProperties.get("resetPosition");
	if ((t != null) && (t.valueChanged() || force))
	    resetPosition = ((Boolean) t.getValue()).booleanValue();

	t = layoutProperties.get("moveNodes");
	if ((t != null) && (t.valueChanged() || force))
	    moveNodes = ((Boolean) t.getValue()).booleanValue();

	t = layoutProperties.get("weightCoefficient");
	if ((t != null) && (t.valueChanged() || force))
	    weightCoefficient = ((Double) t.getValue()).doubleValue();
    }

    /**
     * Perform a layout
     */
    public void layoutPartion(LayoutPartition partition) {

	Dimension initialLocation = null;

	if (canceled)
	    return;

	// Reset the label position of all nodes if necessary 
	if (resetPosition) {
	    resetNodeLabelPosition(partition);
	    return;
	}
	
	// Logs information about this task
	logger.info("Laying out partition " + partition.getPartitionNumber() + " which has "+ partition.nodeCount()
		    + " nodes and " + partition.edgeCount() + " edges: ");

	// Create new Label partition
	LayoutLabelPartition newPartition = new LayoutLabelPartition(partition,
								     weightCoefficient,
								     moveNodes,
								     selectedOnly);

	logger.info("New partition succesfully created!");

	// Figure out our starting point - This will be used when:
	// 1- Laying out labels off all nodes
	// - and- 
	// 2- (normal) Nodes are not allowed to move
	if (!selectedOnly && !moveNodes) {
	    initialLocation = newPartition.getAverageLocation();
	}

	if (canceled)
	    return;

	// Layout the new partition using the parent class layout algorithm
	super.layoutPartition(newPartition);

	// Move labels to a random position near their parents
	// for(LayoutLabelNodeImpl node: newPartition.getLabelNodes() ) {
// 	    logger.info("moving label of node " + node.getIdentifier());
// 	    node.increment(100 * Math.random(), 100 * Math.random());
// 	    node.moveToLocation();    
// 	    logger.info("label moved!");	    
// 	}


	// Not quite done, yet. We may need to migrate labels back to their starting position
	// This will be necessary if:
	// 1- Laying out only selected nodes
	// - or- 
	// 2- (normal) Nodes are not allowed to move
// 	if (selectedOnly || !moveNodes) {
// 	    logger.info("moving back labels (and possibly nodes) to their location");

// 	    double xDelta = 0.0;
// 	    double yDelta = 0.0;
// 	    Dimension finalLocation = newPartition.getAverageLocation();
// 	    xDelta = finalLocation.getWidth() - initialLocation.getWidth();
// 	    yDelta = finalLocation.getHeight() - initialLocation.getHeight();

// 	    for (LayoutNode v: newPartition.getNodeList()) {
// 		if (!v.isLocked()) {
// 		    v.decrement(xDelta, yDelta);
// 		    newPartition.moveNodeToLocation(v);
// 		}
// 	    }
// 	}


	// make sure nodes are where they should be
	for(LayoutNode node: newPartition.getLabelToParentMap().values() ) {
	    node.moveToLocation();
	    logger.info( node.getIdentifier() + node.printLocation() );
	}

	// make sure that all labels are where they should be 
	for(LayoutLabelNodeImpl node: newPartition.getLabelNodes() ) {	
	    node.moveToLocation();
	    logger.info( node.getIdentifier() + node.printLocation() );
	}

	// redraw the network so that the new label positions are visible
	networkView.updateView();
    	networkView.redrawGraph(true, true);

	logger.info("Label/Node layout of partition " + partition.getPartitionNumber() + " complete");
    }


    /**
     * Moves labels to the same position in which their parent nodes are
     */
    protected void resetNodeLabelPosition(LayoutPartition part) {

	logger.info("Reseting labels position");

	CyAttributes nodeAtts = Cytoscape.getNodeAttributes();

	// Go through all labels setting their position to be the same as their parent's
	List<LayoutNode> array = part.getNodeList();

	
	for(LayoutNode node: array) {
	    if (!selectedOnly || !node.isLocked()) {
		if (nodeAtts.hasAttribute(node.getIdentifier(), "node.labelPosition")) {
		    nodeAtts.deleteAttribute(node.getIdentifier(), "node.labelPosition");
		}
	    }
	}
	
	// redraw the network so that the new label positions are visible
	networkView.updateView();
    	networkView.redrawGraph(true, true);
    }


}