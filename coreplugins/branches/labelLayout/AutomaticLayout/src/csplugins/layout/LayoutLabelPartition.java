package csplugins.layout;

import cytoscape.logger.CyLogger;

import csplugins.layout.LayoutEdge;
import csplugins.layout.LayoutNode;
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

    protected Map <LayoutNode,LayoutNode> labelToParentMap;
    protected ArrayList<LayoutNode> allLayoutNodesArray;
    protected ArrayList<LayoutEdge> allLayoutEdgesArray;

    public LayoutLabelPartition(int nodeCount, int edgeCount) {
	super(nodeCount, edgeCount);
    }


    public LayoutLabelPartition createLabelPartition(LayoutPartition part) {

	// Create new empty partition
	LayoutLabelPartition newPart = new LayoutLabelPartition(part.size(),
								part.getEdgeList().size());

	// Copy fields from part
	newPart.nodeList = (ArrayList<LayoutNode>) part.nodeList.clone();
	newPart.edgeList = (ArrayList<LayoutEdge>) part.edgeList.clone();
	newPart.nodeToLayoutNode = (HashMap<CyNode,LayoutNode>) part.nodeToLayoutNode.clone();
	newPart.partitionNumber = part.partitionNumber;
	newPart.edgeWeighter = part.edgeWeighter;

	newPart.maxX = part.maxX;
	newPart.maxY = part.maxY;
	newPart.minX = part.minX;
	newPart.minY = part.minY;
	newPart.width = part.width;
	newPart.height = part.height;
	
	newPart.averageX = part.averageX;
	newPart.averageY = part.averageY;
	
	newPart.lockedNodes = part.lockedNodes;

	// Returns the already initialized label layoutPartition
	return newPart;
    }

}
