package ucsd.cmak.MergeEquivalentNodes;

import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import cytoscape.*;
import cytoscape.giny.*;
import cytoscape.plugin.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.view.CyWindow;
import cytoscape.data.annotation.*;
import cytoscape.data.servers.*;

import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;

import giny.model.*;

/**
 **
 */
public class MergeEquivalentNodes extends CytoscapePlugin {
    
    CyNetwork network;
    CytoscapeObj cyObj;
    boolean DEBUG=true;


    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public MergeEquivalentNodes() {
	this.cyObj = Cytoscape.getCytoscapeObj();
	JMenu MergeMenu = new JMenu("Merge Nodes");
	MergeMenu.add( new MergeSelectedAction() );
	MergeMenu.add( new MergeEquivalentAction() );
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().
	    add( MergeMenu );
    }
    

    /**
     * This class gets attached to the menu item.
     */
    public class MergeEquivalentAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MergeEquivalentAction() {super("Auto-merge nodes w/ same neighbors");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Merges equivalent nodes");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
	    network = Cytoscape.getCurrentNetwork();
            if (network == null) {return;}
	    Thread t = new MergeEquivalentNodesThread();
	    t.start();
	}
    }

    /**
     * This class gets attached to the menu item.
     */
    public class MergeSelectedAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MergeSelectedAction() {super("Merge selected nodes");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Merges selected nodes");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
	    network = Cytoscape.getCurrentNetwork();
            if (network == null) {return;}
	    Thread t = new MergeSelectedNodesThread();
	    t.start();
	}
    }

    class MergeSelectedNodesThread extends Thread{
	// just like MergeEquivalentNodesThread below but no 
	// need to compute the nodes to collapse

	public void run(){
	    System.err.println("Starting MergeSelectedNodes plugin");
	    
	    // get selected nodes and convert to vector of root graph indices
	    int [] flaggedNodesGP = network.getFlaggedNodeIndicesArray();
	    Vector flaggedNodesRG = new Vector();
	    for (int i=0; i<flaggedNodesGP.length; i++)
		flaggedNodesRG.add(
		   new Integer(network.getRootGraphNodeIndex(flaggedNodesGP[i])));
	    
	    // now do the collapsing
	    collapse(convertIntVectorToArray((Vector) flaggedNodesRG ));
	    System.err.println("Finished MergeSelectedNodes");
	}

    }

    class MergeEquivalentNodesThread extends Thread{

        private Map partitionNeighbors(int[] indices, String attr)
        {
            Map p = new HashMap();

            /*
             *                 HashMap sameType = new HashMap();
                for(int e=0; e < neighborIndices.length; e++)
                {
                    int[] edges = network.getEdgeIndicesArray(neighborIndices[e],
                                                              thisNodeGP,
                                                              true);

                    System.err.println("   " + thisNodeGP +
                                       " tgt=" + edges.length);

                    for(int x=0; x < edges.length; x++)
                    {
                        String intType = (String) network.
                            getEdgeAttributeValue(edges[x], "interaction");

                        int tgtIndex = rootGraph.getEdgeSourceIndex(edges[x]);

                        Set s;
                        if(sameType.containsKey(intType))
                        {
                            s = (Set) sameType.get(intType);
                        }
                        else
                        {
                            s = new HashSet();
                            sameType.put(intType, s);
                        }
                        s.add(new Integer(tgtIndex));
                    }
                }

                for(Iterator it2 = sameType.values().iterator(); it2.hasNext();)
                {
                    int[] neighborsSameType = intSetToArray((Set) it2.next());
                    Arrays.sort(neighborsSameType);
                    nodeNeighborArrays.add(neighborsSameType);
                    nodeIndicesRG.add(new Integer(thisNodeRG));

                    System.err.println("adding: " + thisNodeGP
                                       + " [" + toString(neighborsSameType)+"]");
                }
            */
            
            return p;
        }

        public void run(){
	    System.err.println("Starting MergeEquivalentNodes plugin");
	    System.err.println("Getting node neighbor arrays");
	    CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();

	    //for each node, store sorted array of neighbor indices
	    //nodes will be compared by the gp indices of their neighbors
	    //however, the index of similar nodes will be recorded as
	    //the root graph index, as gp indices are mutable.
            int nodeCount = network.getNodeCount();
            System.err.println("Node Count: " + nodeCount);
            
            int [][] nodeNeighborArrays = new int [nodeCount][];
            Map[] partitions = new Map[nodeCount];
            int [] nodeIndicesRG = new int [nodeCount];
            

            // precompute neighbors for each node
            // partition neighbors using attribute
            int i=0;
	    for (Iterator nIt=network.nodesIterator(); nIt.hasNext();)
            {
		int thisNodeGP = network.getIndex((CyNode)nIt.next());
		int thisNodeRG = network.getRootGraphNodeIndex(thisNodeGP);

                int [] neighborIndices = network.neighborsArray(thisNodeGP);

                Arrays.sort(neighborIndices);
                nodeNeighborArrays[i] = neighborIndices;
                partitions[i] = partitionNeighbors(neighborIndices, "interaction");
                nodeIndicesRG[i] = thisNodeRG;
                
                System.err.println("node: " + thisNodeGP +
                                   " #N=" + neighborIndices.length +
                                   " #P=" = partitions[i].size());

                i++;
            }
	    
	    // go back and compare neighbor index arrays
	    // record rootgraph indices because gp indices are mutable
	    System.err.println("Determining node groups to be collapsed");
	    Vector collapseStack = new Vector();
	    
	    boolean [] alreadyCollapsed = new boolean [nodeCount];

            /*
            HashMap alreadyCollapsed = new HashMap();
            
            for(int i=0; i < nodeIndicesRG.size(); i++)
            {
                alreadyCollapsed.put(nodeIndicesRG.get(i), Boolean.FALSE);
            }
            */
            //boolean [] alreadyCollapsed = new boolean [nodeCount];
	    for (i=0; i<nodeIndicesRG.length; i++) 
            {
                Integer indexI = (Integer) nodeIndicesRG.get(i);
                if (((Boolean) alreadyCollapsed.get(indexI)).booleanValue()) continue; 

                int [] neighborsI = (int []) nodeNeighborArrays.get(i);

		if (DEBUG)
                {
                    //System.err.println("Visit node " + rootGraph.getNode(indexI.intValue()));
                    System.err.println("Visit node " + indexI);
                }
		Vector iEquivalentNodes = new Vector();
		iEquivalentNodes.add(indexI);

                alreadyCollapsed.put(indexI, Boolean.TRUE);
		for (int j=i+1; j<nodeNeighborArrays.size(); j++) {
		    int [] neighborsJ = (int[]) nodeNeighborArrays.get(j);
		    Integer indexJ = (Integer) nodeIndicesRG.get(j);
		    //if (DEBUG) 
                    //System.err.print(" Node " + rootGraph.getNode(indexJ.intValue()));

                    /*
                    if(DEBUG) System.err.println("comparing: [" + toString(neighborsI)
                                                 + "] to ["
                                                 + toString(neighborsJ) + "]");
                    */
                    if (Arrays.equals(neighborsI, neighborsJ)) {
			iEquivalentNodes.add(indexJ);
                        alreadyCollapsed.put(indexJ, Boolean.TRUE);
			if (DEBUG) System.err.println(indexI + " " +
                                                      indexJ + " are Equivalent");
		    }
		}
		if (iEquivalentNodes.size() > 1) { // prepare to collapse these
		    collapseStack.add(iEquivalentNodes);
		}
	    }
	    
	    // now do the collapsing
	    System.err.println("Collapsing node groups");
	    for (Iterator vIt=collapseStack.iterator(); vIt.hasNext();)
		collapse(convertIntVectorToArray((Vector) vIt.next()));


            // cmak 4.13.05
            // adjust node sizes
            for (Iterator nIt=network.nodesIterator(); nIt.hasNext();) {
		int thisNodeGP = network.getIndex((CyNode)nIt.next());
		int thisNodeRG = network.getRootGraphNodeIndex(thisNodeGP);
		
                adjustNodeSize(thisNodeRG);
            }

            System.err.println("Finished MergeEquivalentNodes");
	} // end method run()


        private String toString(int[] a)
        {
            StringBuffer b = new StringBuffer();
            for(int x=0; x < a.length; x++)
            {
                b.append(a[x]);
                if(x < a.length - 1)
                {
                    b.append(" ");
                }
                
            }
            return b.toString();
        }
       
        private int[] intSetToArray(Set s)
        {
            int[] i = new int[s.size()];
            int x=0;
            for(Iterator it = s.iterator(); it.hasNext();)
            {
                i[x] = ((Integer) it.next()).intValue();
                x++;
            }
            return i;
        }
    } // end mergeEquivalentNodesThread


    private String toString(int[] a)
    {
        StringBuffer b = new StringBuffer();
        for(int x=0; x < a.length; x++)
        {
            b.append(a[x]);
            if(x < a.length - 1)
            {
                b.append(" ");
            }
            
        }
        return b.toString();
    }
        
    
    private void collapse(int [] RGindices) {
	
	// output debugging info
	if (DEBUG)System.err.println("Collapsing " + toString(RGindices));
	
	//create new node in rgraph representing collapsed node
	CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
	int groupNodeRG = rootGraph.createNode();
	int groupNodeGP = network.addNode(groupNodeRG);
	transferNodeAttributes(RGindices, groupNodeRG);

        
	// add edges to the newly created "group" node
	// First, construct a set of all nodes in the group
	HashSet nodesInGroup = new HashSet();
	for (int i=0; i<RGindices.length; i++)
	    nodesInGroup.add(new Integer(RGindices[i]));
	
	// Next, iterate over neighbors of the group
	List neighborsList = rootGraph.neighborsList(rootGraph.getNode(RGindices[0]));
	for (Iterator nIt = neighborsList.iterator(); nIt.hasNext();) {
	    giny.model.Node nodeRG = (giny.model.Node) nIt.next();
	    int [] adjEdgeIndices = rootGraph.
		getAdjacentEdgeIndicesArray(rootGraph.getIndex(nodeRG),true,true,true);
	    Vector validAdjEdgeIndices = new Vector ();
	    int groupEdgeRG = Integer.MIN_VALUE;  // out of range initialization
	    // Visit the edges incoming from each neighbor	  
	    for (int i=0; i<adjEdgeIndices.length; i++) {
		int edgeRG = adjEdgeIndices[i];
		if (!network.containsEdge(rootGraph.getEdge(edgeRG))) continue;
		int tgtNodeRG =rootGraph.getEdgeTargetIndex(adjEdgeIndices[i]);
		int srcNodeRG =rootGraph.getEdgeSourceIndex(adjEdgeIndices[i]);
		if      (nodesInGroup.contains(new Integer(tgtNodeRG))) 
		    tgtNodeRG = groupNodeRG;
		else if (nodesInGroup.contains(new Integer(srcNodeRG)))
		    srcNodeRG = groupNodeRG;
		else    continue;  // this edge is not to group
		// if first time through loop, create the new edge
		// regardless, remember the current edge so as to later get its attributes
		if (validAdjEdgeIndices.size() == 0) { 
		    groupEdgeRG = rootGraph.createEdge(srcNodeRG,tgtNodeRG);
		    network.addEdge(groupEdgeRG);
		    if (DEBUG) System.err.println("### Adding edge from src="
						  + rootGraph.getNode(srcNodeRG) + " to tgt="
						  + rootGraph.getNode(tgtNodeRG));
		}
		validAdjEdgeIndices.add(new Integer (edgeRG));
	    }
	    // now transfer the old edge attrs to the new edges
	    int [] validAdjEdgeIndicesArray = new int[validAdjEdgeIndices.size()];
	    for (int i=0; i<validAdjEdgeIndices.size(); i++)
		validAdjEdgeIndicesArray[i] = 
		    ((Integer)validAdjEdgeIndices.get(i)).intValue();
	    transferEdgeAttributes(validAdjEdgeIndicesArray, groupEdgeRG);
	}
	
	// remove nodes from GP 
	network.hideNodes(convertRGtoGPindices(RGindices));
    } // end method collapse
    
    // utility methods are below here
    private int [] convertIntVectorToArray (Vector vtr) {
	int [] arr = new int [vtr.size()];
	int i=0;
	for (Iterator it = vtr.iterator(); it.hasNext(); ) {
	    arr[i++] = ((Integer) it.next()).intValue();
	}
	return arr;
    } // end method convertIntVectorToArray
    
    private int [] convertRGtoGPindices (int [] RGindices) {
	int [] GPindices = new int [RGindices.length];
	for (int i=0; i<RGindices.length; i++)
	    GPindices[i] = network.getNodeIndex(RGindices[i]); 
	return GPindices;
    } // end method convertRGtoGPindices
    
    private String printNodeArray (int [] RGindices) {
	String s = new String();
	int i=0;
	while (i<RGindices.length-1)
	    s += Cytoscape.getRootGraph().getNode(RGindices[i++])+", ";
	s += Cytoscape.getRootGraph().getNode(RGindices[i]);
	return s;
    } // end method printNodeArray
    
    private void transferEdgeAttributes (int [] fromEdgeIndices, int toEdgeIndex) {

	// get basic data structures
	CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
	GraphObjAttributes attrs = network.getEdgeAttributes();
	String [] attrNames = network.getEdgeAttributesList();
	if (fromEdgeIndices == null || rootGraph.getEdge(toEdgeIndex) == null) return;
	giny.model.Edge toEdge   = rootGraph.getEdge(toEdgeIndex);
	
	// set canonicalName and commonName attribute of the new "to Edge"
	int srcIndex = rootGraph.getEdgeSourceIndex(toEdgeIndex);
	int tgtIndex = rootGraph.getEdgeTargetIndex(toEdgeIndex);
	String intType = (String) network.
	    getEdgeAttributeValue(fromEdgeIndices[0], "interaction");
	String edgeName = rootGraph.getNode(srcIndex).getIdentifier()
	    + " (" + intType + ") "
	    + rootGraph.getNode(tgtIndex).getIdentifier();
	toEdge.setIdentifier(edgeName);
	attrs.addNameMapping(edgeName, toEdge);
	
	// build up vector of incoming attr values
	for (int i=0; i<attrNames.length; i++) {
	    if (DEBUG) System.err.println("Visiting edge attr " + attrNames[i]);
	    Vector values = new Vector();
	    Object attrVal=null;
	    for (int j=0; j<fromEdgeIndices.length; j++) {
		giny.model.Edge fromEdge = rootGraph.getEdge(fromEdgeIndices[j]);
		attrVal = network.getEdgeAttributeValue(fromEdge, attrNames[i]);
		values.add(attrVal);
	    }
	    
	    // distribute the vector depending on object type
	    if (values.size() > 1) {
		if (DEBUG) System.err.println("Edge " + toEdge 
					      + ": Merging attr " + attrNames[i]);
		Object mergedVal = merge(values);		
		network.setEdgeAttributeValue(toEdge, attrNames[i], mergedVal);
	    }
	    else if (attrVal != null) {
		if (DEBUG) System.err.println("Edge " + toEdge
					      + ": direct xfer attr " + attrNames[i]);
		network.setEdgeAttributeValue(toEdge, attrNames[i], attrVal);
	    }
	}
    } // end method transferEdgeAttributes

    /**
     * Adjust the size of the node based on the NodeName attribute
     *
     * NOTE: the size multipliers (eg 7, 16, 20) are probably dependent
     * of the font size used.  These multipliers were determined by
     * trial and error.
     *
     * NOTE: This is duplicate code (see ResizeNodesPlugin).
     * Can we call the ResizeNodesPlugin from this plugin?
     * 
     * @param nodeIndex root graph index of a node
     *
     * @author cmak
     */
    private void adjustNodeSize(int nodeIndex)
    {
        CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
        giny.model.Node node = rootGraph.getNode(nodeIndex);
        Object val = network.getNodeAttributeValue(node, "NodeName");
        if(val != null)
        {
            String name = (String) val;
            String[] nameArray = name.split("\n");
            String longest = findLongestName(nameArray);

            // If the longest name has fewer than 6 chars,
            // the node will be the default width.
            if(longest.length() > 5)
            {
                network.setNodeAttributeValue(node, "NodeWidth",
                                              new Double(7 * longest.length()));
            }
            else if(longest.length() > 3)
            {
                network.setNodeAttributeValue(node, "NodeWidth",
                                              new Double(10 * longest.length()));
            }
            
            if(nameArray.length > 5)
            {
                network.setNodeAttributeValue(node, "NodeHeight",
                                              new Double(16 * nameArray.length));
            }
            // If there is only 1 name, the node will be the default height
            else if(nameArray.length > 1)
            {
                network.setNodeAttributeValue(node, "NodeHeight",
                                              new Double(20 * nameArray.length));
            }
        }
    }


    private String findLongestName(String[] values)
    {
        int max = 0;
        String longest = "";
        for (int x=0; x < values.length; x++) {
	    String s = values[x];

            if(s.length() > max)
            {
                longest = s;
                max = s.length();
            }
        }

        return longest;
    }
    
    
    private void transferNodeAttributes (int [] fromNodeIndices, int toNodeIndex) {
	
	// get basic data structures
	CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
	GraphObjAttributes attrs = network.getNodeAttributes();
	if (fromNodeIndices == null || rootGraph.getNode(toNodeIndex) == null) return;
	giny.model.Node toNode = rootGraph.getNode(toNodeIndex);
	String [] attrNames = network.getNodeAttributesList();
	
	// set canonicalName and commoName attribute of the new "toNode"
	String nodeName = "Group" + toNodeIndex;
	toNode.setIdentifier(nodeName);
	attrs.addNameMapping(nodeName, toNode);
	network.setNodeAttributeValue(toNode, "canonicalName2",
				      printNodeArray(fromNodeIndices));
	
	// transfer other attributes
	for (int i=0; i<attrNames.length; i++) {
	    Vector values = new Vector();
	    Object attrVal=null;
	    if (attrNames[i].equals("canonicalName")) continue;
	    //		if (attrNames[i].equals("commonName")) continue;
	    if (DEBUG) System.err.println("Visiting node attr " + attrNames[i]);
	    // build up vector of incoming attr values
	    for (int j=0; j<fromNodeIndices.length; j++) {
		giny.model.Node fromNode = rootGraph.getNode(fromNodeIndices[j]);
		attrVal = network.getNodeAttributeValue(fromNode, attrNames[i]);
		values.add(attrVal);
	    }
	    // distribute this vector depending on object type
	    if (values.size() > 1) {
		if (DEBUG) System.err.println("Node " + toNode 
					      + ": Merging attr " + attrNames[i]);
		Object mergedVal = merge(values);
		network.setNodeAttributeValue(toNode, attrNames[i], mergedVal);

            }
	    else if (attrVal != null) {
		if (DEBUG) System.err.println("Node " + toNode 
					      + ": Direct xfer attr " + attrNames[i]);
		network.setNodeAttributeValue(toNode, attrNames[i], attrVal);
	    }
	}

        // set the shape of the node
        /* this doesn't work
        String name = attrs.getCanonicalName(toNode);

        attrs.set(NodeAppearanceCalculator.nodeShapeBypass, name,
                  ShapeNodeRealizer.ROUND_RECT);
        */

        
    } // end method transferNodeAttributes

    // utility methods to handle specific attribute object types
    
    private Object merge (Vector values) {
	if (values == null || values.get(0) == null) return null;
	Class cType = values.get(0).getClass();
	if      (String.class.isAssignableFrom(cType)) return mergeString(values);
	else if (Number.class.isAssignableFrom(cType)) return mergeNumber(values);
	else if (Vector.class.isAssignableFrom(cType)) return mergeVector(values);
	else if (cType.isArray())                      return mergeArray(values);
	else System.err.println("WARNING: Attribute not transferred because object type " 
				+ cType + " is not recognized. ");
	return null;
    }
    
    // for strings, return comma separated list of distinct values
    private String mergeString (Vector values) {
	HashSet valuesSet = new HashSet(values);
	String stringVal = "";
	for (Iterator it=valuesSet.iterator(); it.hasNext();) {
	    String s = (String) it.next();
	    if (s != null) { 
		stringVal += s;
		//if (it.hasNext()) stringVal += ", ";
                // cmak
                if (it.hasNext()) stringVal += "\n";
	    }
	}
	return stringVal;
    }
    
    // for numbers, return average
    private Number mergeNumber (Vector values) {
	double avg = 0.0;
	String className = values.get(0).getClass().getName();
	for (Iterator it=values.iterator(); it.hasNext();) {
	    Number num = (Number) it.next();
	    if (num != null) avg += num.doubleValue();
	}
	avg /= (double) values.size();
	if (className.equals("java.lang.Double"))  return new Double(avg);
	if (className.equals("java.lang.Float"))   return new Float(avg);
	if (className.equals("java.lang.Integer")) return new Integer((int)avg);
	if (className.equals("java.lang.Long"))    return new Long((int)avg);
	if (className.equals("java.lang.Short"))   return new Short((short)avg);
	System.err.println("ERROR: unrecognized number type");
	return null;
    }

    // for vectors, flatten into one vector of distinct values
    private Object [] mergeVector (Vector values) {
	HashSet valuesSet = new HashSet();
	for (Iterator it=values.iterator(); it.hasNext();) {
	    for (Iterator it2=((Vector)it.next()).iterator(); it2.hasNext();) {
		valuesSet.add(it2.next());
	    }
	}
	return valuesSet.toArray();
    }

    // for arrays, flatten into one array of distinct values
    private Object [] mergeArray (Vector values) {
	HashSet valuesSet = new HashSet();
	for (Iterator it=values.iterator(); it.hasNext();) {
	    for (Iterator it2=((Vector)it.next()).iterator(); it2.hasNext();) {
		valuesSet.add(it2.next());
	    }
	}
	return valuesSet.toArray();
    }
}


