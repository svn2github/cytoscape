package csplugins.layout;

import cytoscape.logger.CyLogger;

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
import csplugins.layout.LayoutLabelNodeImpl;
import csplugins.layout.Profile;
import csplugins.layout.EdgeWeighter;

import cytoscape.util.intr.IntIntHash;
import cytoscape.util.intr.IntObjHash;

import cytoscape.*;

import cytoscape.view.*;

import giny.view.*;

import java.awt.Dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.Collection;


/**
 * The LayoutLabelPartition class ....
 *
 * @author <a href="mailto:gerardohuck .at. gmail .dot. com">Gerardo Huck</a>
 * @version 0.1
 */
public class LayoutLabelPartition extends LayoutPartition {

    protected Map <LayoutLabelNodeImpl,LayoutNode> labelToParentMap;
    protected ArrayList<LayoutNode> allLayoutNodesArray;
    protected ArrayList<LayoutEdge> allLayoutEdgesArray;

    protected double weightCoefficient = 1.0;
    protected boolean moveNodes = false;
    protected boolean selectedOnly = false;
    protected boolean isWeighted = false;

    public LayoutLabelPartition(int nodeCount, int edgeCount) {
	super(nodeCount, edgeCount);
    }


    public LayoutLabelPartition(LayoutPartition part, 
				double weightCoefficient, 
				boolean moveNodes, 
				boolean selectedOnly,
				boolean isWeighted) {

	super(part.size(), part.getEdgeList().size());

	this.weightCoefficient = weightCoefficient;
	this.moveNodes = moveNodes;
	this.selectedOnly = selectedOnly;
	this.isWeighted = isWeighted;

	// Copy fields from part
	this.nodeList = (ArrayList<LayoutNode>) part.nodeList.clone();
	this.edgeList = (ArrayList<LayoutEdge>) part.edgeList.clone();
	this.nodeToLayoutNode = (HashMap<CyNode,LayoutNode>) part.nodeToLayoutNode.clone();
	this.partitionNumber = part.partitionNumber;
	this.edgeWeighter = part.edgeWeighter;

	this.maxX = part.maxX;
	this.maxY = part.maxY;
	this.minX = part.minX;
	this.minY = part.minY;
	this.width = part.width;
	this.height = part.height;
	
	this.averageX = part.averageX;
	this.averageY = part.averageY;
	
	this.lockedNodes = part.lockedNodes;

	// Initialize 'label' fields
	labelToParentMap = new HashMap<LayoutLabelNodeImpl,LayoutNode>(part.size());
       	allLayoutNodesArray = new ArrayList<LayoutNode>(part.size());
	allLayoutEdgesArray = new ArrayList<LayoutEdge>(part.size());
	
	// This method handles the creation of LayoutLabelNodes, the mappings, etc
	this.initializeLabels();

	// Calculate weights for this partition using an overriding method
	//	this.calculateEdgeWeights();
    }

    /**
     * Handles the initization of all the infraestructure needed in order to use a 
     * LayoutLabelPartition to layout labels, such as Label nodes creation, etc.
     */
    protected void initializeLabels() {	

	edgeWeighter.setLabelWeightCoefficient(weightCoefficient);
	edgeWeighter.calculateMaxWeight();

	for (LayoutNode ln: nodeList ) {
	   
	    // Creates a new LabelNode, child of ln
	    NodeView nv = ln.getNodeView();
	    int ind = ln.getIndex() + nodeList.size();
	    
	    // wonder how this is even possible
	    if (nv == null) {
		logger.error("Found a layout node without a NodeView!");
		continue;
	    }
	   
	    LayoutLabelNodeImpl labelNode = new LayoutLabelNodeImpl(nv, ind);

	    /* Unlock labelNode if:
	     * - algorithm is to be applied to the entire network
	     * - algorithm is to be applied to the selected nodes only, and ln
	     * is selected
	     */
	    if (!selectedOnly || !(ln.isLocked()) ) {
		labelNode.unLock();
		updateMinMax(labelNode.getX(), labelNode.getY());
		this.width += labelNode.getWidth();
		this.height += labelNode.getHeight();
	    } else {
		    labelNode.lock();
		    lockedNodes++;
	    }

	    // Lock ln if it is unlocked but nodes are not allowed to move
	    if (!moveNodes && !ln.isLocked()) {
		ln.lock();
		lockedNodes++;
	    }

	    // Add labelNode -> parentNode to Map
	    labelToParentMap.put(labelNode, ln);

	    // Creates label Edge: ln <---> labelNode
	    LayoutEdge labelEdge = new LayoutEdge();
	    labelEdge.addNodes(ln, labelNode);

	    // This takes care of the weight of this edge
	    updateWeights(labelEdge);

	    // Adds it to edgeList
	    edgeList.add(labelEdge);


	}

	// Adds all LabelNodes to nodeList
	nodeList.addAll(labelToParentMap.keySet());

    }

    public void calculateEdgeWeights() {
	Double weight = null;
	
	// -- First set labelEdges weights --
	if (this.isWeighted) {
	    // Calculate maximum preexisting weight
	    Double maxWeight = new Double(Double.MIN_VALUE);

	    ListIterator<LayoutEdge>iter = edgeList.listIterator();

	    while (iter.hasNext()) {
		LayoutEdge edge = iter.next();
	    
		// Only consider non label edges
		if (labelToParentMap.keySet().contains(edge.getSource()) || 
		    labelToParentMap.keySet().contains(edge.getTarget()) ) {
		    continue;
		}

		if (  edge.getWeight() > maxWeight ) {
		    maxWeight = edge.getWeight();
		}
	    }

	    // Value which will be used as weight for all label edges
	    weight = maxWeight *  weightCoefficient;
	} 
	else { // if (!this.isWeighted)
	    weight = weightCoefficient;
	}
	
	// Set all labelEdge weights
	ListIterator<LayoutEdge>iter2 = edgeList.listIterator();

	while (iter2.hasNext()) {
	    LayoutEdge edge = iter2.next();
	    
	    if (labelToParentMap.keySet().contains(edge.getSource()) || 
		labelToParentMap.keySet().contains(edge.getTarget()) ) {		
		// set label edges weights
		edge.setWeight(weight);		
	    } else{ // non label edge (normal one)
		if (!this.isWeighted){
		    edge.setWeight(1.0);
		}
	    }
	}


	// -- Then call parent method to calculate all weights --
	super.calculateEdgeWeights();
    }


    public Map<LayoutLabelNodeImpl,LayoutNode> getLabelToParentMap() {
	return labelToParentMap;
    }

    /**
     * Returns a list with all the LayoutLabelNodes
     */
    public ArrayList<LayoutLabelNodeImpl> getLabelNodes() {
	ArrayList<LayoutLabelNodeImpl> array = new ArrayList<LayoutLabelNodeImpl> ();
	array.addAll(labelToParentMap.keySet());
	return array;
    }

    



}
