package ucsd.trey.SigAttributes;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import giny.model.RootGraph;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.model.Edge;
import giny.view.GraphView;
import giny.view.NodeView;
import giny.model.RootGraphChangeListener;
import giny.model.RootGraphChangeEvent;

import cytoscape.CytoscapeObj;
import cytoscape.plugin.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.data.annotation.*;
import cytoscape.data.servers.*;
import DistLib.*;

/**
 **
 */
public class SigAttributes extends AbstractPlugin {
    
    CyWindow cyWindow;
    GraphView graphView;
    CyNetwork network;
    GraphPerspective graphPerspective;
    CytoscapeObj cyObj;

    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public SigAttributes(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
	this.cyObj = cyWindow.getCytoscapeObj();

	this.graphView = cyWindow.getView();
	this.graphPerspective = graphView.getGraphPerspective();
        this.network = cyWindow.getNetwork();

	cyWindow.getCyMenus().getOperationsMenu().add( new MainPluginAction() );
    }
    
    /**
     * This class gets attached to the menu item.
     */
    public class MainPluginAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public MainPluginAction() {super("Find Enriched Attributes");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("For a selected set of nodes and node attribute, " + 
		      "determines which attribute values are enriched " + 
		      "versus all nodes");
            return sb.toString();
        }
        
	        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {

	    System.err.println("Starting Attribute Chooser");
	    
	    // update globals to ensure they are current
	    graphView = cyWindow.getView();
	    graphPerspective = graphView.getGraphPerspective();
	    network = cyWindow.getNetwork();

            //can't continue if either of these is null
            if (graphView == null || network == null) {return;}
            
	    //inform listeners that we're doing an operation on the network
            String callerID = "MainPluginAction.actionPerformed";
            network.beginActivity(callerID);
            //this is the graph structure; it should never be null,
            if (graphPerspective == null) {
                System.err.println("In " + callerID + ":");
                System.err.println("Unexpected null graph perspective in network");
                network.endActivity(callerID);
                return;
            }
            //and the view should be a view on this structure
            if (graphView.getGraphPerspective() != graphPerspective) {
                System.err.println("In " + callerID + ":");
                System.err.println("graph view is not a view on network's graph perspective");
                network.endActivity(callerID);
                return;
            }
	    Thread t = new SigAttributesThread();
	    t.start();
	    network.endActivity(callerID);
	}
    }

    class SigAttributesThread extends Thread{

	public void run(){
            //get all of the required data
	    String [] attrs = network.getNodeAttributes().getAttributeNames();
	    String [] annots;
	    try { 
		AnnotationDescription [] annotD = 
		    cyObj.getBioDataServer().getAnnotationDescriptions();
		annots = new String [annotD.length];
		for (int i=0; i<annotD.length; i++) annots[i] = annotD[i].toString();
	    } catch (NullPointerException ex) { annots = new String [0]; }

	    // choose Attribute or Annotation storing functional info of interest
	    AttributeChooser chooser = new AttributeChooser(attrs, annots, cyWindow);
	    chooser.showDialog();
	    String chosenAttr     = chooser.getAttribute();
	    String [] chosenNames = chooser.getNameAttribute();
	    double pvalueCutoff   = chooser.getCutoff();
	    int maxNum            = chooser.getMaxNumber();
	    boolean groupNodes    = chooser.shouldGroupNodes();
	    if (chooser.useAttributes()) runAttributes(chosenAttr, chosenNames, 
						       pvalueCutoff, maxNum, groupNodes);
	    if (chooser.useAnnotations()) runAnnotations(chosenAttr, chosenNames,
							 pvalueCutoff, maxNum, groupNodes);
	}

	private void runAttributes(String chosenAttr, String [] chosenNames,
				   double pvalueCutoff, int maxNum, boolean groupNodes) {
	    
	    // get all genes and selected genes
	    String [] allFunctions = network.getNodeAttributes().getUniqueStringValues(chosenAttr);
	    String [] allNodeIDs   = network.getNodeAttributes().getObjectNames(chosenAttr);
	    String [] selectedNodeIDs   = getIDsForAllSelectedNodes();
	    String [] selectedGeneNames = getNamesForAllSelectedNodes(chosenNames);	    
	    System.err.println("Attribute has " + allFunctions.length + 
			       " values over " + allNodeIDs.length + " genes");
	    
	    // get # occurrences of each function across all genes and selected genes
	    HashMap allFunctionCount  = getFunctionCount(allNodeIDs, chosenAttr, 
							network.getNodeAttributes());

	    // create and clear a node attribute for significant functions
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    String newAttrName = "Significant Attributes";
	    String newAttrName2 = "Significant Proteins";
	    nodeAttr.deleteAttribute(newAttrName);
	    nodeAttr.deleteAttribute(newAttrName2);
	    
	    //iterate over each selected node unless nodes are grouped (the default)
	    for (int k=0; k<selectedNodeIDs.length; k++) {
		
		// get (possibly multiple) genes associated with each node
		String [] consideredGeneNames;
		String [] consideredNodeIDs;
		if (groupNodes) {
		    consideredNodeIDs   = selectedNodeIDs;
		    consideredGeneNames = getNamesForAllSelectedNodes(chosenNames);
		}
		else {  // if treated individually
		    consideredNodeIDs    = new String [1];
		    consideredNodeIDs[0] = selectedNodeIDs[k];
		    consideredGeneNames = getNamesForNodeID(chosenNames, selectedNodeIDs[k]);
		}

		// compute histogram of functions across considered gene names
		HashMap thisFunctionCount = getFunctionCount(consideredGeneNames, chosenAttr, 
							     network.getNodeAttributes());

		// compute significances of enrichment for each function
		Vector sigFunctions = getSignificance(thisFunctionCount, allFunctionCount, 
						      pvalueCutoff, maxNum);
		
		// iterate over all nodes, all names per node, and all sig. functions per name
		for (int j=0; j<consideredNodeIDs.length; j++) {
		    String nodeID = consideredNodeIDs[j];
		    String [] geneNames = convertIDtoNames(nodeID, chosenNames);
		    for (Iterator it2 = sigFunctions.iterator(); it2.hasNext(); ) {
			String function = (String) it2.next();
			String [] genesWithAttr = getGenesWithAttributeValue(nodeAttr, chosenAttr,
									     geneNames, function);
			if (genesWithAttr.length > 0) {
			    nodeAttr.append(newAttrName, nodeID, function);
			    if (!groupNodes) nodeAttr.append(newAttrName2, nodeID, genesWithAttr);
			}
		    }
		}
		if (groupNodes) break;  // do not iterate if grouping all nodes as one cluster
	    }
	} // end runAttributes

	private void runAnnotations (String chosenAnnot, String [] chosenNames,
				     double pvalueCutoff, int maxNum, boolean groupNodes) {

	    // cannot run unless annotations exist
	    if (cyObj.getBioDataServer() == null) return;

	    // get annotations from BioDataServer and ensure not null
	    AnnotationDescription [] annotDesc = 
		cyObj.getBioDataServer().getAnnotationDescriptions();
	    int i=0;
	    while (!annotDesc[i].toString().equals(chosenAnnot) && (i < annotDesc.length)) i++; 
	    if (annotDesc[i] == null) return;
	    Annotation annotation = cyObj.getBioDataServer().getAnnotation(annotDesc[i]);

	    // precompute list of selected nodes, all genes & histogram of all functions
	    String [] selectedNodeIDs   = getIDsForAllSelectedNodes();
	    String [] allGeneNames      = annotation.getNames();
	    HashMap allFunctionCount    = getFunctionCount(allGeneNames, annotation);

	    // create and clear a node attribute for significant functions
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    String newAttrName  = "Significant Attributes";
	    String newAttrName2 = "Significant Proteins";
	    nodeAttr.deleteAttribute(newAttrName);
	    nodeAttr.deleteAttribute(newAttrName2);
	    
	    //iterate over each selected node unless nodes are grouped (the default)
	    for (int k=0; k<selectedNodeIDs.length; k++) {
		
		// get (possibly multiple) genes associated with each node
		String [] consideredGeneNames;
		String [] consideredNodeIDs;
		if (groupNodes) {
		    consideredNodeIDs   = selectedNodeIDs;
		    consideredGeneNames = getNamesForAllSelectedNodes(chosenNames);
		}
		else {  // if treated individually
		    consideredNodeIDs    = new String [1];
		    consideredNodeIDs[0] = selectedNodeIDs[k];
		    consideredGeneNames = getNamesForNodeID(chosenNames, selectedNodeIDs[k]);
		}
		
		// compute histogram of functions across considered gene names
		HashMap thisFunctionCount = getFunctionCount(consideredGeneNames, annotation);

		// compute significances of enrichment for each function
		Vector sigFunctions = getSignificance(thisFunctionCount, allFunctionCount, 
						      pvalueCutoff, maxNum, annotation);

		// create new node attributes for sigFunctions
		// iterate over nodes, all names per node, and all sig. functions per name
		//System.err.println("ConsideredNodes = " + Arrays.asList(consideredNodeIDs));
		for (int j=0; j<consideredNodeIDs.length; j++) {
		    String nodeID = consideredNodeIDs[j];
		    String [] geneNames = convertIDtoNames(nodeID, chosenNames);
		    //System.err.println("ConsideredNames = " + Arrays.asList(geneNames));
		    for (Iterator it = sigFunctions.iterator(); it.hasNext(); ) {
			String function = (String) it.next();
			// check to see if any of this node's gene names have this func.
			String [] genesWithAttr=getGenesWithAttributeValue(annotation, geneNames, function);
			if (genesWithAttr.length > 0) {
			    OntologyTerm term = 
				annotation.getOntology().getTerm(Integer.parseInt(function));
			    nodeAttr.append(newAttrName, nodeID, term.getName() );
			    if (!groupNodes) nodeAttr.append(newAttrName2, nodeID, genesWithAttr);
			    //System.err.println("nodeAttr.append " + newAttrName + " " 
			    //+ nodeID + " " + term.getName());
			}
		    }
		}
		if (groupNodes) break;  // do not iterate if grouping all nodes as one cluster
	    }
	} // end runAnnotations

	// Returns a hash mapping each function (attribute) name to the number
	// of times the function is observed among the named genes.
	private HashMap getFunctionCount (String [] geneNames, String attribute, 
					  GraphObjAttributes nodeAttributes) {

	    HashMap functionCount = new HashMap();
	    for (int i=0; i<geneNames.length; i++) {
		String geneName = geneNames[i];
		String [] functions = nodeAttributes.getStringArrayValues(attribute, geneName);
		for (int j=0; j<functions.length; j++) {
		    String function = functions[j];
		    if (functionCount.containsKey(function)) {
			int cnt = ((Integer) functionCount.get(function)).intValue();
			functionCount.put(function, new Integer(++cnt));
		    }
		    else functionCount.put(function, new Integer(1));
		}
	    }
	    //System.err.println("Found " + functionCount.keySet().size() + " functions");
	    return functionCount;
	} // end getFunctionCount
	
	// Polymorphic to the above function, used for annotations instead of attributes
	private HashMap getFunctionCount (String [] geneNames, Annotation annotation) {
	    Ontology ontology = annotation.getOntology();
	    HashMap functionCount = new HashMap();
	    for (int i=0; i<geneNames.length; i++) {
		String geneName = geneNames[i];
		//System.err.println("Analyzing gene " + geneName);
		int [] pathIDs = flatten(annotation.getClassifications(geneName), ontology);
		for (int k=0; k<pathIDs.length; k++) {
		    String pathID = Integer.toString(pathIDs[k]);
		    //System.err.println("  " + pathID);
		    if (functionCount.containsKey(pathID)) {
			int cnt = ((Integer) functionCount.get(pathID)).intValue();
			functionCount.put(pathID, new Integer(++cnt));
		    }
		    else functionCount.put(pathID, new Integer(1));
		}
	    }
	    System.err.println("Found " + functionCount.keySet().size() + " functions");
	    return functionCount;	    
	    
	} // end getFunctionCount

	// flattens an ontology array of leaf IDs into a 1D array of unique elements
	private int [] flatten (int [] idArray, Ontology ontology) {

	    // use a set to ensure uniqueness
	    Set s = new HashSet();
	    for (int k=0; k<idArray.length; k++) {
		int [][] twoDimArray = ontology.getAllHierarchyPaths(idArray[k]);
		
		for (int i=0; i<twoDimArray.length; i++) {
		    for (int j=0; j<twoDimArray[i].length; j++) {
			s.add(new Integer(twoDimArray[i][j]));
			//System.err.println("  " + twoDimArray[i][j]);
		    }
		}
	    }

	    // now convert set of unique elements back into an int array
	    int [] result = new int [s.size()];
	    int i=0;
	    for (Iterator it = s.iterator(); it.hasNext();) {
		result[i++] = ((Integer)it.next()).intValue(); 
	    }
	    return result;
	} // end flatten

	private String [] getIDsForAllSelectedNodes() {
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    int [] nodeIndices = graphView.getSelectedNodeIndices();
	    String [] nodeIDs = new String [nodeIndices.length];
	    for (int i=0; i<nodeIndices.length; i++) {
		int nodeIndex = nodeIndices[i];
		Node node = graphPerspective.getNode(nodeIndex);
		nodeIDs[i] = node.getIdentifier();
	    }
	    return nodeIDs;
	} // end getSelectedNodeIDs

	private String [] getNamesForAllSelectedNodes(String [] nameAttrs) {
	    // make an array of selected names, allowing more than one name per node
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    int [] nodeIndices = graphView.getSelectedNodeIndices();
	    HashSet geneNames = new HashSet();  // ensures gene namess only occur once
	    for (int i=0; i<nodeIndices.length; i++) {
		int nodeIndex = nodeIndices[i];
		Node node = graphPerspective.getNode(nodeIndex);
		for (int j=0; j<nameAttrs.length; j++) 
		    geneNames.addAll(nodeAttr.getList(nameAttrs[j], node.getIdentifier()));
	    }
	    //System.err.println("Node " + geneNames);
	    return (String []) geneNames.toArray(new String [0]);
	} // end getNamesForAllSelectedNodes

	private String [] getNamesForNodeID (String [] nameAttrs, String nodeID) {
	    // make an array of selected names, allowing more than one name per node
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    HashSet geneNames = new HashSet();  // ensures gene namess only occur once
	    for (int j=0; j<nameAttrs.length; j++) 
		geneNames.addAll(nodeAttr.getList(nameAttrs[j], nodeID));
	    return (String []) geneNames.toArray(new String [0]);
	} // end getNamesForNodeID

	private String [] convertIDtoNames (String nodeID, String [] nameAttrs) {
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    HashSet geneNames = new HashSet();   // ensures gene names only occur once
	    for (int j=0; j<nameAttrs.length; j++) 
		geneNames.addAll(nodeAttr.getList(nameAttrs[j], nodeID));
	    return (String []) geneNames.toArray(new String [0]);
	} // end convertIDtoName

	// Returns a vector of significant functional IDs.
	// Version to work with attributes, not annotations
	private Vector getSignificance (HashMap thisFunctionCount, HashMap allFunctionCount,
					double pvalueCutoff, int maxNumber){
	    
	    double n = (double) sumFunctionCounts(thisFunctionCount);
	    double t = (double) sumFunctionCounts(allFunctionCount);
	    int numTests = thisFunctionCount.size();
	    Vector funAndPvals = new Vector();
	    for (Iterator it = thisFunctionCount.keySet().iterator(); it.hasNext();) {
		String function = (String) it.next();
		double x = ((Integer) thisFunctionCount.get(function)).doubleValue();
		double NR = ((Integer) allFunctionCount.get(function)).doubleValue();
		double NB = t - NR;
		double pvalue = 1 - hypergeometric.cumulative(x, NR, NB, n);
		if (x <= 1) pvalue = 1;  // hack to correct for small sample sizes
		double pvalueCorrected = pvalue * numTests;   // Bonferroni for now
		funAndPvals.add(new FunAndPval (function, pvalue, pvalueCorrected, x, NR, NB, n));
	    }
	    Collections.sort(funAndPvals);

	    Vector sigFunctions = new Vector();
	    int sigCount = 0;
	    for (Iterator it = funAndPvals.iterator(); it.hasNext(); ) {
		FunAndPval funAndPval = (FunAndPval) it.next();
		System.err.println(funAndPval);
		if (funAndPval.getPvalue() < pvalueCutoff && sigCount++ < maxNumber) 
		    sigFunctions.add(funAndPval.getFunction());
	    }		
	    return sigFunctions;
	}

	// Returns a vector of significant functional IDs.
	// Version to work with annotations, not attributes-- uses conditional ordering of ontology
	// thisFunctionCount = # of selected genes with a particular function (x), hashed for each function
	// allFunctionCount  = # of total    genes with a particular function (n), hashed for each function
	private Vector getSignificance (HashMap thisFunctionCount, HashMap allFunctionCount,
					double pvalueCutoff, int maxNumber, Annotation annotation){
	    
	    Ontology ontology = annotation.getOntology();
	    Vector funAndPvals = new Vector();
	    int numTests = thisFunctionCount.size();
	    
	    // iterate over each functional ID in ontology
	    for (Iterator it = thisFunctionCount.keySet().iterator(); it.hasNext();) {

		// compute statistics for this function
		String function   = (String) it.next();
		double x = ((Integer) thisFunctionCount.get(function)).doubleValue();
		double n = ((Integer) allFunctionCount.get(function)).doubleValue();
		//System.err.println(function + " " + x + " " + n);
		
		// compute cumulative statistics for the parent function(s)
		OntologyTerm term = ontology.getTerm(Integer.parseInt(function));
		int [] parentTerms = term.getParents();
		//int [] parentTerms = term.getParentsAndContainers();
		double NR = 0.0; double t = 0.0; String parentFn = "";
		if (parentTerms.length == 0) { NR = x; t = n; continue; }
	        try {
		    for (int i=0; i<parentTerms.length; i++) {
			parentFn = Integer.toString(ontology.getTerm(parentTerms[i]).getId());
			NR += ((Integer) thisFunctionCount.get(parentFn)).doubleValue();
			t  += ((Integer)  allFunctionCount.get(parentFn)).doubleValue();
			//System.err.println("   " + parentFn + " " + NR + " " + t);
		    }
		} catch (NullPointerException exp) { 
		    System.err.println("Parent not in functionCount");
		    System.err.println("  Function = " + function + ", Parent = " + parentFn);
		}
		
		// compute hypergeometric pvalue
		double pvalue = 1 - hypergeometric.cumulative(x-1, NR, t-NR, n);
		double pvalueCorrected = pvalue * numTests;   // Bonferroni for now
		if (x <= 1) pvalue = 1;  // hack to correct for small sample sizes
		funAndPvals.add(new FunAndPval (function, pvalue, pvalueCorrected, x, NR, t-NR, n));
	    }
	    Collections.sort(funAndPvals);
	    
	    // select significant functions only
	    Vector sigFunctions = new Vector();
	    int sigCount = 0;
	    for (Iterator it = funAndPvals.iterator(); it.hasNext(); ) {
		FunAndPval funAndPval = (FunAndPval) it.next();
		OntologyTerm term = ontology.getTerm(Integer.parseInt(funAndPval.getFunction()));
		if (funAndPval.getPvalue() < pvalueCutoff && sigCount++ < maxNumber) 
		    sigFunctions.add(funAndPval.getFunction());
		else break; // can stop because list is reverse sorted
		System.err.println(funAndPval + "  " + term.getName());
	    }		
	    return sigFunctions;
	}

	// checks to see if the given node has been assigned the given value of chosenAttr
	private boolean hasAttributeValue(GraphObjAttributes nodeAttr, String chosenAttr,
					  String [] geneNames, String value) {
	    
	    for (int k=0; k<geneNames.length; k++) {
		String [] attrValues = nodeAttr.getStringArrayValues(chosenAttr, geneNames[k]);
		for (int i=0; i<attrValues.length; i++)
		    if (attrValues[i].equals(value)) return true;
	    }
	    return false;
	}


	// Polymorphic to the above function, used for annotations instead of attributes
	// REALLY INEFFICIENT RIGHT NOW-- SHOULD BE HASHED LATER
	private boolean hasAttributeValue(Annotation annotation, String [] geneNames, String value) {
	    
	    Ontology ontology = annotation.getOntology();
	    for (int k=0; k<geneNames.length; k++) {
		int [] allIDs = flatten(annotation.getClassifications(geneNames[k]), ontology);
		for (int j=0; j<allIDs.length; j++) {
		    String idValue = Integer.toString(allIDs[j]);
		    if (idValue.equals(value)) return true;
		}
	    }
	    return false;
	}

	// checks to see if the given node has been assigned the given value of chosenAttr
	private String [] getGenesWithAttributeValue(GraphObjAttributes nodeAttr, String chosenAttr,
						     String [] geneNames, String value) {
	    
	    Vector genesWithAttr = new Vector ();
	    for (int k=0; k<geneNames.length; k++) {
		String [] attrValues = nodeAttr.getStringArrayValues(chosenAttr, geneNames[k]);
		for (int i=0; i<attrValues.length; i++)
		    if (attrValues[i].equals(value)) {
			genesWithAttr.add(geneNames[k]);
			break;
		    }
	    }
	    return (String []) genesWithAttr.toArray(new String [0]);
	}


	// Polymorphic to the above function, used for annotations instead of attributes
	// REALLY INEFFICIENT RIGHT NOW-- SHOULD BE HASHED LATER
	private String [] getGenesWithAttributeValue(Annotation annotation, 
						     String [] geneNames, String value) {
	    
	    Vector genesWithAttr = new Vector ();
	    Ontology ontology = annotation.getOntology();
	    for (int k=0; k<geneNames.length; k++) {
		int [] allIDs = flatten(annotation.getClassifications(geneNames[k]), ontology);
		for (int j=0; j<allIDs.length; j++) {
		    String idValue = Integer.toString(allIDs[j]);
		    if (idValue.equals(value)) {
			genesWithAttr.add(geneNames[k]);
			break;
		    }
		}
	    }
	    return (String []) genesWithAttr.toArray(new String [0]);
	}

	// Input HashMap of functions to counts and compute total counts over all functions
	private int sumFunctionCounts (HashMap functionCount) {
	    int result = 0;
	    Iterator it = functionCount.values().iterator();
	    while (it.hasNext())
		result += ((Integer) it.next()).intValue();
	    return result;
	} // end sumFunctionCounts
	
    }
    
}

