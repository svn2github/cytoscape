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
import cytoscape.AbstractPlugin;
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
	    if (chooser.useAttributes())  runAttributes(chosenAttr, chosenNames, 
							pvalueCutoff, maxNum);
	    if (chooser.useAnnotations()) runAnnotations(chosenAttr, chosenNames,
							 pvalueCutoff, maxNum);
	}

	private void runAttributes(String chosenAttr, String [] chosenNames,
				   double pvalueCutoff, int maxNum) {
	    
	    // get all genes and selected genes
	    String [] allFunctions = network.getNodeAttributes().getUniqueStringValues(chosenAttr);
	    String [] allGeneNames = network.getNodeAttributes().getObjectNames(chosenAttr);
	    String [] nodeNames = getSelectedNames(chosenNames);	    
	    System.err.println("Attribute has " + allFunctions.length + 
			       " values over " + allGeneNames.length + " genes");
	    
	    // get # occurrences of each function across all genes and selected genes
	    HashMap allFunctionCount = getFunctionCount(allGeneNames, chosenAttr, 
							network.getNodeAttributes());
	    HashMap thisFunctionCount = getFunctionCount(nodeNames, chosenAttr, 
							 network.getNodeAttributes());

	    // compute significances of enrichment for each function
	    Vector sigFunctions = getSignificance(thisFunctionCount, allFunctionCount, 
						  pvalueCutoff, maxNum, null);

	    // create new node attribute for significant functions
	    String newAttrName = "Significant Attributes";
	    network.getNodeAttributes().deleteAttribute(newAttrName);
	    for (int i=0; i<nodeNames.length; i++) {
		for (Iterator it = sigFunctions.iterator(); it.hasNext(); ) {
		    String function = (String) it.next();
		    if (hasAttributeValue(network.getNodeAttributes(), chosenAttr, 
					  nodeNames[i], function))
			network.getNodeAttributes().append(newAttrName, nodeNames[i], function);
		}
	    }
	}

	private void runAnnotations (String chosenAnnot, String [] chosenNames,
				     double pvalueCutoff, int maxNum) {

	    // cannot run unless annotations exist
	    if (cyObj.getBioDataServer() == null) return;

	    // get annotations from BioDataServer and ensure not null
	    AnnotationDescription [] annotDesc = 
		cyObj.getBioDataServer().getAnnotationDescriptions();
	    int i=0;
	    while (!annotDesc[i].toString().equals(chosenAnnot) && (i < annotDesc.length)) i++; 
	    if (annotDesc[i] == null) return;
	    Annotation annotation = cyObj.getBioDataServer().getAnnotation(annotDesc[i]);

	    // get all genes and selected genes
	    String [] allGeneNames = annotation.getNames();
	    String [] nodeNames = getSelectedNames(chosenNames);

	    // get # occurrences of each function across all genes and selected genes	    
	    HashMap allFunctionCount = getFunctionCount(allGeneNames, annotation);
	    HashMap thisFunctionCount = getFunctionCount(nodeNames, annotation);
	    
	    // compute significances of enrichment for each function
	    Vector sigFunctions = getSignificance(thisFunctionCount, allFunctionCount, 
						  pvalueCutoff, maxNum, annotation);
	    
	    // create new node attribute for significant functions
	    String newAttrName = "Significant Annotations";
	    network.getNodeAttributes().deleteAttribute(newAttrName);
	    for (i=0; i<nodeNames.length; i++) {
		for (Iterator it = sigFunctions.iterator(); it.hasNext(); ) {
		    String function = (String) it.next();
		    if (hasAttributeValue(annotation, nodeNames[i], function)) {
			OntologyTerm term = 
			    annotation.getOntology().getTerm(Integer.parseInt(function));
			network.getNodeAttributes().append(newAttrName, 
							   nodeNames[i], term.getName());
		    }
		}
	    }
	}

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

	private String [] getSelectedNames(String [] nameAttrs) {
	    // make an array of selected names, allowing more than one name per node
	    GraphObjAttributes nodeAttr = network.getNodeAttributes();
	    int [] nodeIndices = graphView.getSelectedNodeIndices();
	    HashSet nodeNames = new HashSet();  // ensures nodes only occur once
	    for (int i=0; i<nodeIndices.length; i++) {
		int nodeIndex = nodeIndices[i];
		Node node = graphPerspective.getNode(nodeIndex);
		for (int j=0; j<nameAttrs.length; j++) 
		    nodeNames.addAll(nodeAttr.getList(nameAttrs[j], node.getIdentifier()));
	    }
	    //System.err.println("Node " + nodeNames);
	    return (String []) nodeNames.toArray(new String [0]);
	} // end getSelectedNames

	private Vector getSignificance (HashMap thisFunctionCount, HashMap allFunctionCount,
					double pvalueCutoff, int maxNumber, Annotation annotation) {
	    
	    double n = (double) sumFunctionCounts(thisFunctionCount);
	    double t = (double) sumFunctionCounts(allFunctionCount);
	    Vector funAndPvals = new Vector();
	    for (Iterator it = thisFunctionCount.keySet().iterator(); it.hasNext();) {
		String function = (String) it.next();
		double x = ((Integer) thisFunctionCount.get(function)).doubleValue();
		double NR = ((Integer) allFunctionCount.get(function)).doubleValue();
		double NB = t - NR;
		double pvalue = 1 - hypergeometric.cumulative(x, NR, NB, n);
		if (x <= 1) pvalue = 1;  // hack to correct for small sample sizes
		funAndPvals.add(new FunAndPval (function, pvalue, x, NR, NB, n));
	    }
	    Collections.sort(funAndPvals);

	    Vector sigFunctions = new Vector();
	    int sigCount = 0;
	    for (Iterator it = funAndPvals.iterator(); it.hasNext(); ) {
		FunAndPval funAndPval = (FunAndPval) it.next();
		System.err.print(funAndPval);
		if (annotation != null) {
		    OntologyTerm term = 
		      annotation.getOntology().getTerm(Integer.parseInt(funAndPval.getFunction()));
		    System.err.print(" " + term.getName());
		}
		System.err.println();
		if (funAndPval.getPvalue() < pvalueCutoff && sigCount++ < maxNumber) 
		    sigFunctions.add(funAndPval.getFunction());
	    }		
	    return sigFunctions;
	}

	// checks to see if the given node has been assigned the given value of chosenAttr
	private boolean hasAttributeValue(GraphObjAttributes nodeAttr, String chosenAttr,
					  String geneName, String value) {
	    
	    String [] attrValues = nodeAttr.getStringArrayValues(chosenAttr, geneName);
	    for (int i=0; i<attrValues.length; i++)
		if (attrValues[i].equals(value)) return true;
	  
	    return false;
	}


	// Polymorphic to the above function, used for annotations instead of attributes
	// REALLY INEFFICIENT RIGHT NOW-- SHOULD BE HASHED LATER
	private boolean hasAttributeValue(Annotation annotation, String geneName, String value) {
	    
	    Ontology ontology = annotation.getOntology();
	    int [] allIDs = flatten(annotation.getClassifications(geneName), ontology);
	    for (int j=0; j<allIDs.length; j++) {
		String idValue = Integer.toString(allIDs[j]);
		if (idValue.equals(value)) return true;
	    }
	    return false;
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

