/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/**
 * Created on Jul 5, 2005
 * @author andrea@pasteur.fr
 * CytoscapeDealer handles connections from the rest of the application to Cytoscape.
 * All the command and queries issued to Cytoscape go thorugh this object. 
 * Hence, it is possible to define here general policies about network managements:
 * i.e: What happens if a network is switched or destroid during a multi-step operation from the plugin.
 * 
 */
package fr.pasteur.sysbio.rdfscape.cytoscape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import antlr.collections.List;

//import obsolete.RuleObject;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.ding.DingNetworkView;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutAlgorithm;
import cytoscape.view.CyEdgeView;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CyNodeView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import ding.view.DGraphView;
import ding.view.InnerCanvas;
import ding.view.NodeContextMenuListener;
import ding.view.DGraphView.Canvas;
import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.DefaultSettings;
import fr.pasteur.sysbio.rdfscape.MemoryViewer;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.ontologyhandling.RDFResourceWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.GraphQueryAnswerer;
import giny.model.Edge;
import giny.model.Node;
import giny.util.JUNGSpringLayout;
import giny.view.EdgeView;
import giny.view.NodeView;

/**
 * @author andrea@pasteur.fr
 * CytoscapeDealer handles connections from the rest of the application to Cytoscape.
 * All the command and queries issued to Cytoscape go thorugh this object. 
 * Hence, it is possible to define here general policies about network managements:
 * i.e: What happens if a network is switched or destroid during a multi-step operation from the plugin.
 * 
 */
public class CytoscapeDealer implements PropertyChangeListener, MemoryViewer {
	private CyNetwork myCurrentNetwork=null;
	private CyNetworkView myCurrentNetworkView=null;
	
	//private KnowledgeWrapper myKnowledge=null;		//TODO where ?
	private CommonMemory commonMemory=null;
	
	
	private HashMap patternPanels=null;				//TODO do we need this ?
	private CyNode hackNode=null;					//TODO do we still need this ?
	private static int currentID=0;
	
	private Hashtable networkType=null;				//TODO do we need this ?
	private HashSet isEditable=null;				//TODO do we still need this ?
	private Collection varRestrictions=null;		//TODO do we need this ?
	
	private Hashtable<String,Boolean> networkHasMenu=null; 	//TODO it will replace networkType
	
	private ArrayList tempFilterConditions=null;
	private static int bNodeCounter=0;
	private static int literalNodeCounter=0;
	private static int nodeIDCounter=0;
	
	private boolean showRDFSLabels=false;
	private boolean collapseDatatypeAttributes=false;
	private boolean enableSplit=false;
	
	private HashSet uri2Split=null;
	private PatternMenu menuListener=null;
	
	private String[] nodeAttributesNames=new String[0];
	private String[] edgeAttributesNames=new String[0];
	
	private String[] nodeStringAttributesNames=new String[0];
	private String[] nodeDoubleAttributesNames=new String[0];
	private String[] nodeBooleanAttributesNames=new String[0];
	private String[] nodeIntegerAttributesNames=new String[0];
	
	private String[] edgeStringAttributesNames=new String[0];
	private String[] edgeDoubleAttributesNames=new String[0];
	private String[] edgeBooleanAttributesNames=new String[0];
	private String[] edgeIntegerAttributesNames=new String[0];
	
	
	private Set nodeStringAttributes=new HashSet<String>();
	private Set nodeDoubleAttributes=new HashSet<String>();
	private Set nodeBooleanAttributes=new HashSet<String>();
	private Set nodeIntegerAttributes=new HashSet<String>();
	
	private Set edgeStringAttributes=new HashSet<String>();
	private Set edgeDoubleAttributes=new HashSet<String>();
	private Set edgeBooleanAttributes=new HashSet<String>();
	private Set edgeIntegerAttributes=new HashSet<String>();
	
	CyAttributes nodeAttributes=null;
	CyAttributes edgeAttributes=null;
	
	/**
	 * @param rs the RDFPlugin instance that generated this object. 
	 */
	public CytoscapeDealer() throws Exception{
		System.out.print("\tCytoscapeDealer... ");
		System.out.print(" 0");
		link(); 
		System.out.print(" 1");
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener
		(CytoscapeDesktop.NETWORK_VIEW_FOCUS, this );
		Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener
		(CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
		myCurrentNetwork=Cytoscape.getCurrentNetwork();
		myCurrentNetworkView=Cytoscape.getCurrentNetworkView();
		System.out.print(" 2");
		menuListener=new PatternMenu();
		System.out.print(" 3");
		patternPanels=new HashMap();
		networkType=new Hashtable();
		networkHasMenu=new Hashtable<String, Boolean>();
		System.out.print(" 4");
		//activePatternPanels=new HashMap();
		RDFScape.getCommonMemory().addViewerElement(this);
		reset();
		System.out.print(" 5 ");
		System.out.println("Ok");
	}
	
	/**
	 * Relinks a CommonMemory object to CytoscapeDealer (this never happens in current implementation).
	 * Note the new CommonMemory be be the one already linked with no effects.
	 * Then this should map attibutes from memory to graph.
	 *
	 */
	private void relink() throws Exception {
		link();
		/**
		 * TODO
		 * here we should remap all attributes
		 */
	}
	private void link() throws Exception {
		if(RDFScape.getCommonMemory()==null) {
			System.out.print("!!!");
			throw new Exception("Cannot build CytoscapeDealer : missing CommonMemory");
		}
		System.out.print(".");
		commonMemory=RDFScape.getCommonMemory();
		commonMemory.addViewerElement(this);
		System.out.print(".");
	}
	
	/**
	 * Intercepts a change of focuse and updates myCurrentNetwork and myCurrentNetworkView
	 * Also register context menu
	 */
	public void propertyChange(PropertyChangeEvent e) {
		System.out.print("Event ");
		/*
		if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_FOCUSED ) {
		       myCurrentNetworkView = ( CyNetworkView )e.getNewValue();
		       myCurrentNetwork=myCurrentNetworkView.getNetwork();
		}*/
		if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
		       myCurrentNetworkView = ( CyNetworkView )e.getNewValue();
		       System.out.print("focus change ->"+myCurrentNetworkView);
		}
		myCurrentNetwork=Cytoscape.getCurrentNetwork();
		myCurrentNetworkView=Cytoscape.getCurrentNetworkView();
		//myCurrentNetworkView.addNodeContextMenuListener(this);
		System.out.println();
		//Which kind of network are we dealing with ?
		
	}
	
	
	
	
	public void reset() {
		isEditable=new HashSet();
		tempFilterConditions=new ArrayList();
		restoreSplitConditions();
	}
	
	/**
	 * 
	 */
	public CyNetwork getCurrentNetwork() {
		return Cytoscape.getCurrentNetwork();
		
	}
	/**
	 * @param myID
	 * @param attributemap
	 */
	/*
	private CyNode addNode(String myID, Map attributemap, boolean searchOnly) {
		boolean toFlag=false;
		CyNode node=Cytoscape.getCyNode(myID,true);
		if(myCurrentNetwork.containsNode(node)) toFlag=true;
		else if(searchOnly) return null;
		
		Collection attributes=attributemap.keySet();
		for (Iterator attribute = attributes.iterator(); attribute.hasNext();) {
			String attributestring = (String) attribute.next();
			myCurrentNetwork.setNodeAttributeValue(node,attributestring,attributemap.get(attributestring));
		}
		
		
		myCurrentNetwork.addNode(node);
		if(toFlag) myCurrentNetwork.setFlagged(node,true);
		return node;
		
	}
	*/
	/**
	 * @param i
	 * @param j
	 * @return
	 */
	public Image getScreenshot(int x, int y) {
		System.out.println("Going to get a screenshot");
		/* The old way
		CyNetworkView  view=Cytoscape.getCurrentNetworkView();
		Image image=((DGraphView)view).getCanvas().createImage(300,300);
		*/
		
		InnerCanvas canvas= ((DGraphView)Cytoscape.getCurrentNetworkView()).getCanvas();  
		System.out.println("Dimensions: "+canvas.getWidth()+"x"+canvas.getHeight());
        BufferedImage image = new BufferedImage(canvas.getWidth(), canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = image.createGraphics();
        canvas.print(gfx);
            	
		 return image.getScaledInstance(300,300, Image.SCALE_DEFAULT);
	}
	/**
	 * @return
	 */
	public String[] getSelectedOntologyNodes() {
		myCurrentNetwork=Cytoscape.getCurrentNetwork();
		Collection flagged=myCurrentNetwork.getSelectedNodes();
		ArrayList selectedurislist=new ArrayList();
		//String[] selecteduris=new String[flagged.size()];
		for (Iterator iter = flagged.iterator(); iter.hasNext();) {
			CyNode node = (CyNode) iter.next();
			// TODO restore
			/*
			if(myRDFScapeInstance.getMyMemory().getNodeAttribute((node.getIdentifier()),"URI")!=null) {
				selectedurislist.add(myRDFScapeInstance.getMyMemory().getNodeAttribute((node.getIdentifier()),"URI"));
			}*/
		}
		String[] selecteduris=new String[selectedurislist.size()];
		for (int j = 0; j < selectedurislist.size(); j++) {
			selecteduris[j]=(String) selectedurislist.get(j);
		}
		
		return selecteduris;
		
	}
	/**
	 * Return a CyNetwork object registered as as a rule-pattern editor.
	 * Always return a network. In case a network is not present (ot is null) return a new network.
	 * @param myRule the rule this editor-network is relative to.
	 */
	/*
	public void activatePatternPanel(RuleObject myRule) {
		if(patternPanels.get(myRule)==null) {
			patternPanels.put(myRule,Cytoscape.createNetwork(myRule.getName()).getIdentifier());
			//activePatternPanels.put(myRule,null);
			myCurrentNetworkView.addContextMethod("class phoebe.PNodeView",
					"fr.pasteur.sysbio.rdfscape.cytoactions.PatternMenu",
					"makeNodeVar",
					new Object[] {myCurrentNetworkView , myRDFScapeInstance},
					CytoscapeInit.getClassLoader());
			
			myCurrentNetworkView.addContextMethod("class phoebe.PNodeView",
					"fr.pasteur.sysbio.rdfscape.cytoactions.PatternMenu",
					"makeNodeFilter",
					new Object[] {myCurrentNetworkView , myRDFScapeInstance,commonMemory},
					CytoscapeInit.getClassLoader());
			
			myCurrentNetworkView.addContextMethod("class phoebe.PNodeView",
					"fr.pasteur.sysbio.rdfscape.cytoactions.PatternMenu",
					"extendNetwork",
					new Object[] {myCurrentNetworkView , myRDFScapeInstance},
					CytoscapeInit.getClassLoader());
			
			myCurrentNetworkView.addContextMethod("class phoebe.PEdgeView",
					"fr.pasteur.sysbio.rdfscape.cytoactions.PatternMenu",
					"makeEdgeVar",
					new Object[] {myCurrentNetworkView, myRDFScapeInstance},
					CytoscapeInit.getClassLoader());
			
			myCurrentNetworkView.addContextMethod("class phoebe.PEdgeView",
					"fr.pasteur.sysbio.rdfscape.cytoactions.PatternMenu",
					"makeEdgeFilter",
					new Object[] {myCurrentNetworkView, myRDFScapeInstance},
					CytoscapeInit.getClassLoader());
		
		
		}
		myCurrentNetwork=Cytoscape.getNetwork((String)patternPanels.get(myRule));
		if(myCurrentNetwork.getNodeCount()==0) {
			hackNode=Cytoscape.getCyNode("",true);
			myCurrentNetwork.addNode(hackNode);
		}
		Cytoscape.getDesktop().setFocus(myCurrentNetwork.getIdentifier());
		System.out.println(myCurrentNetwork.getIdentifier());
		
		
		//myCurrentNetwork.removeNode(hackNode.getRootGraphIndex(),false);
		//myCurrentNetworkView=Cytoscape.createNetworkView(myCurrentNetwork,"test");
		
		
	}
	*/
	
	
	
	/**
	 * @param myRule
	 * @return
	 */
	/*
	public Collection getPatternSentences(RuleObject myRule) {
		Collection myResult=new ArrayList();
		myCurrentNetwork=Cytoscape.getNetwork((String)patternPanels.get(myRule));
		myCurrentNetwork.flagAllEdges(); // TODO test
		for (Iterator iter = myCurrentNetwork.getFlaggedEdges().iterator(); iter.hasNext();) {
			Edge edge = (Edge) iter.next();
			Node source=edge.getSource();
			Node target=edge.getTarget();
			String sourceURI=(String) myCurrentNetwork.getNodeAttributeValue(source,"URI");
			String sourceVAR=(String) myCurrentNetwork.getNodeAttributeValue(source,"VAR");
			String sourceFILTER=(String) myCurrentNetwork.getNodeAttributeValue(source,"FILTER");
			String targetURI=(String) myCurrentNetwork.getNodeAttributeValue(target,"URI");
			String targetVAR=(String) myCurrentNetwork.getNodeAttributeValue(target,"VAR");
			String targetFILTER=(String) myCurrentNetwork.getNodeAttributeValue(target,"FILTER");
			String edgeURI=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"URI");
			String edgeVAR=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"VAR");
			String edgeFILTER=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"FILTER");
			String[] myLine={sourceURI,sourceVAR,sourceFILTER,edgeURI,edgeVAR,edgeFILTER,targetURI,targetVAR,targetFILTER};
			myResult.add(myLine);
			
		}
		myCurrentNetwork.unFlagAllEdges(); // TODO test
		return myResult;
		
	}
*/
	
	/**
	 * @param mode
	 */
	public void makeNewPanel(String mode) {
		String newName="RDFScape ontology browser # "+currentID;
		currentID++;
		myCurrentNetwork=Cytoscape.createNetwork(newName);
		myCurrentNetworkView=Cytoscape.createNetworkView(myCurrentNetwork);
		
		VisualMappingManager manager=Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog=manager.getCalculatorCatalog();
		
		VisualStyle vs=catalog.getVisualStyle("rdfscape");
		if(vs==null) {
			System.out.println("Cannot find RDFScape visual style");
		}
		myCurrentNetworkView.setVisualStyle(vs.getName());
		manager.setVisualStyle(vs);
		myCurrentNetworkView.redrawGraph(true, true);
		
		networkType.put(newName,mode);
		networkHasMenu.put(myCurrentNetworkView.getIdentifier(),new Boolean(true));
		if(true) {
			
			myCurrentNetworkView.addNodeContextMenuListener(menuListener);
			myCurrentNetworkView.addEdgeContextMenuListener(menuListener);
		}
	}
	
	
	/**
	 * @param s
	 */
	/*
	public CyNode addPatternNode(RDFResourceWrapper rw) {
		checkGenericType();
		CyNode tempnode=null;
		
		String networkTitle=myCurrentNetwork.getTitle();
		System.out.println("Asked to map "+rw.getDisplayText()+" to "+networkTitle+"("+myCurrentNetwork.getIdentifier()+")");
		//if(patternPanels.containsValue(myCurrentNetwork.getIdentifier())) {
		if(networkTitle==null) {
			JOptionPane.showMessageDialog(null, "Create a network first!");
			return tempnode;
		}
		
		if(myCurrentNetwork.getIdentifier().equals("0")) {
			JOptionPane.showMessageDialog(null, "Create a network first!");
			return tempnode;
		}
		
		
		tempnode=getNodeWithURI(rw.getURI());
		if(tempnode!=null) {
			System.out.println("Known node :"+tempnode.getIdentifier());
			return tempnode;
		}
		else {
			tempnode=Cytoscape.getCyNode(rw.getURI(),true);
			System.out.println("New node :"+tempnode.getIdentifier());
		}
			//Note: if we find the node in the network...
		
		if(((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("RDF") ||
				((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("GENERIC")) {	
			
			Cytoscape.setNodeAttributeValue(tempnode,"NSCOLOR",rw.getColorString());
			Cytoscape.setNodeAttributeValue(tempnode,"URI",rw.getURI());
			Cytoscape.setNodeAttributeValue(tempnode,"LABEL",rw.getDisplayText());
			if(rw.isBlank()) {
				Cytoscape.setNodeAttributeValue(tempnode,"RDFTYPE","BNODE");
				Cytoscape.setNodeAttributeValue(tempnode,"VAR",rw.getBnodeVarName());
				
			}
			else if(rw.isLiteral()) Cytoscape.setNodeAttributeValue(tempnode,"RDFTYPE","LITERAL");
			else Cytoscape.setNodeAttributeValue(tempnode,"RDFTYPE","RESOURCE");
			Cytoscape.setNodeAttributeValue(tempnode,"HACK","P");
			myCurrentNetwork.addNode(tempnode);
			//if(myCurrentNetwork.containsNode(hackNode)) myCurrentNetwork.removeNode(hackNode.getRootGraphIndex(),false);
			System.out.println(myCurrentNetworkView);
			myCurrentNetworkView.redrawGraph(true,false);
			myCurrentNetworkView.fitContent();
			myCurrentNetworkView.updateView();
		}
		return tempnode;
	}
	*/
	/**
	 * @param subject
	 * @param property
	 * @param object
	 */
	/*
	public void addPatternEdge(RDFResourceWrapper subject, RDFResourceWrapper property, RDFResourceWrapper object) {
		checkGenericType();
		//if(!patternPanels.containsValue(myCurrentNetwork.getIdentifier())) return;
		if(((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("RDF") ||
				((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("GENERIC")	) {
			CyNode subjectNode=addPatternNode(subject);
			CyNode objectNode=addPatternNode(object);
			CyEdge edge=Cytoscape.getCyEdge(subject.getURI(),property.getURI(),object.getURI(),"_concept_");
			Cytoscape.setEdgeAttributeValue(edge,"NSCOLOR",property.getColorString());
			Cytoscape.setEdgeAttributeValue(edge,"URI",property.getURI());
			Cytoscape.setEdgeAttributeValue(edge,"LABEL",property.getDisplayText());
			Cytoscape.setEdgeAttributeValue(edge,"HACK","P");
			myCurrentNetwork.addEdge(edge);
			myCurrentNetworkView.redrawGraph(true,false);
			myCurrentNetworkView.fitContent();
			myCurrentNetworkView.updateView();
		}
	}
	*/
	/**
	 * 
	 */
	public void setEditable() {
		String networkName=myCurrentNetwork.getTitle();
		if(networkName==null) return; 
		if(!isEditable.contains(networkName)) {
			isEditable.add(networkName);
		}
	}
	/**
	 * @return
	 */
	/*
	public ArrayList getPatternsCollection() {
		ArrayList patternCollection=new ArrayList();
		//myCurrentNetwork.flagAllEdges(); // TODO test
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		System.out.println(myCurrentNetwork.getTitle()+"-> "+(String)networkType.get(myCurrentNetwork.getTitle()));
		checkGenericType();
		if(((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("RDF") ||
				((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("GENERIC")) {
			System.out.println("IN");
			for (int i=0;i<edgeIndices.length;i++) {
				System.out.println(".");
				Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
				Node source=edge.getSource();
				Node target=edge.getTarget();
		
				
				String firstTerm=null;
				String secondTerm=null;
				String thirdTerm=null;
				
				String sourceURI=(String) myCurrentNetwork.getNodeAttributeValue(source,"URI");
				String sourceVAR=(String) myCurrentNetwork.getNodeAttributeValue(source,"VAR");
				if(sourceVAR!=null) firstTerm=sourceVAR;
				else firstTerm=sourceURI;
				String firstFilter=(String) myCurrentNetwork.getNodeAttributeValue(source,"FILTER");
				
				String targetURI=(String) myCurrentNetwork.getNodeAttributeValue(target,"URI");
				String targetVAR=(String) myCurrentNetwork.getNodeAttributeValue(target,"VAR");
				if(targetVAR!=null) thirdTerm=targetVAR;
				else thirdTerm=targetURI;
				String thirdFilter=(String) myCurrentNetwork.getNodeAttributeValue(target,"FILTER");
				
				String edgeURI=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"URI");
				String edgeVAR=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"VAR");
				if(edgeVAR!=null) secondTerm=edgeVAR;
				else secondTerm=edgeURI;
				String secondFilter=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"FILTER");
				
				String[] myLine={firstTerm,secondTerm,thirdTerm,firstFilter,secondFilter,thirdFilter};
				patternCollection.add(myLine);
			
			}
		}
		//myCurrentNetwork.unFlagAllEdges(); 
		
		return patternCollection;
	}
	*/
	/**
	 * @return
	 */
	public String getCurrentNetworkType() {
		return (String)networkType.get(myCurrentNetwork.getTitle());
	}
	/**
	 * @param string
	 */
	public void searchNode(String string) {
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		for (Iterator iter = myCurrentNetwork.nodesIterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			String nodeURI=(String) cyNodeAttributes.getStringAttribute(node.getIdentifier(),"URI");
			if(nodeURI!=null)
				if(nodeURI.equalsIgnoreCase(string)) myCurrentNetwork.setSelectedNodeState(node,true);
	
		}
		
	}
	/**
	 * @param string
	 */
	public CyNode getNodeWithURI(String uri) {
		CyNode mynode=RDFScape.getCommonMemory().getCyNodesForURI(uri)[0];
		return mynode;
		/*
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		CyNode mynode=null;
		for (Iterator iter = myCurrentNetwork.nodesIterator(); iter.hasNext();) {
			Node node = (Node) iter.next();
			String nodeURI=(String) cyNodeAttributes.getStringAttribute(node.getIdentifier(),"URI");
			if(nodeURI!=null)
				if(cyNodeAttributes.getStringAttribute(node.getIdentifier(),"HACK")==null)
					if(nodeURI.equalsIgnoreCase(string)) return (CyNode) node;
	
		}
		return mynode;
		*/
	}
	/**
	 * @param string
	 */
	/*
	public void searchEdge(String string) {
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			String edgeURI=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"URI");
			if(edgeURI!=null)
				if(edgeURI.equalsIgnoreCase(string)) myCurrentNetwork.setFlagged(edge,true);
	
		}
	}
	*/
	/*
	public CyEdge getEdgeWithURI(String string) {
		CyEdge myEdge=null;
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			String edgeURI=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"URI");
			if(edgeURI!=null)
				if(edgeURI.equalsIgnoreCase(string)) return (CyEdge) myEdge;
	
		}
		return myEdge; Checking
	}
	*/
	private void checkGenericType() {
		if((String)networkType.get(myCurrentNetwork.getTitle())==null) {
			networkType.put(myCurrentNetwork.getTitle(),"GENERIC");
			System.out.println("Made generic");
		}
	}
	/**
	 * @param string
	 * @param functionName
	 * @return
	 */
	public String getStringNodeAttributeValueByURI(String nodeURI, String functionName) {
		System.out.println("Request for String attribute "+functionName+" for Node "+nodeURI);
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		CyNode myNode=getNodeWithURI(nodeURI);
		if(myNode==null) {
			System.out.println("Node "+nodeURI+" not found");
			return null;
		}
		if(!nodeStringAttributes.contains(functionName)) {
			System.out.println("String attribute "+functionName+" was not found");
			return null;
		}
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		return (String) cyNodeAttributes.getStringAttribute(myNode.getIdentifier(),functionName);
	}
	
	public Double getDoubleNodeAttributeValueByURI(String nodeURI, String functionName) {
		System.out.println("Request for Double attribute "+functionName+" for Node "+nodeURI);
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		CyNode myNode=getNodeWithURI(nodeURI);
		if(myNode==null) {
			System.out.println("Node "+nodeURI+" not found");
			return null;
		}
		if(!nodeDoubleAttributes.contains(functionName)) {
			System.out.println("Double attribute "+functionName+" was not found");
			return null;
		}
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		return  cyNodeAttributes.getDoubleAttribute(myNode.getIdentifier(),functionName);
	}
	
	public Boolean getBooleanNodeAttributeValueByURI(String nodeURI, String functionName) {
		System.out.println("Request for Boolean attribute "+functionName+" for Node "+nodeURI);
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		CyNode myNode=getNodeWithURI(nodeURI);
		if(myNode==null) {
			System.out.println("Node "+nodeURI+" not found");
			return null;
		}
		if(!nodeBooleanAttributes.contains(functionName)) {
			System.out.println("Boolean attribute "+functionName+" was not found");
			return null;
		}
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		return cyNodeAttributes.getBooleanAttribute(myNode.getIdentifier(),functionName);
	}
	
	public Integer getIntegerNodeAttributeValueByURI(String nodeURI, String functionName) {
		System.out.println("Request for Integer attribute "+functionName+" for Node "+nodeURI);
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		CyNode myNode=getNodeWithURI(nodeURI);
		if(myNode==null) {
			System.out.println("Node "+nodeURI+" not found");
			return null;
		}
		if(!nodeIntegerAttributes.contains(functionName)) {
			System.out.println("Integer attribute "+functionName+" was not found");
			return null;
		}
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		return  cyNodeAttributes.getIntegerAttribute(myNode.getIdentifier(),functionName);
	}
	
	public Double getNumericAttributeValueByURI(String nodeURI, String functionName) {
		if(getDoubleNodeAttributeValueByURI(nodeURI, functionName)!=null) return getDoubleNodeAttributeValueByURI(nodeURI, functionName);
		else if(getBooleanNodeAttributeValueByURI(nodeURI, functionName)!=null) {
			if(getBooleanNodeAttributeValueByURI(nodeURI, functionName).booleanValue()) return 1.0;
			else return 0.0;
		}
		else if(getIntegerNodeAttributeValueByURI(nodeURI, functionName)!=null) return new Double(getIntegerNodeAttributeValueByURI(nodeURI, functionName));
		else if(getStringNodeAttributeValueByURI(nodeURI, functionName)!=null) {
			Double result=null;
			try {
				result= Double.parseDouble(getStringNodeAttributeValueByURI(nodeURI, functionName));
			} catch (Exception e) {
				System.out.println("Cannot parse "+functionName+" as a number");
				return null;
			}
			return result;
		}
		else return null;
	}
	
	
	
	
	
	/**
	 * For edges!
	 */
	public String getStringEdgeAttributeValueByURI(String sourceNodeURI, String targetNodeURI, String functionName) {
		System.out.println("Request for String attribute "+functionName+" for Edge "+sourceNodeURI+"->"+targetNodeURI);
		if(sourceNodeURI==null || targetNodeURI==null || functionName==null) return null;
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		if(!edgeStringAttributes.contains(functionName)) {
			System.out.println(functionName+" is not a String");
			return null;
		}
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		String source;
		String target;
		
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			source=(String) nodeAttributes.getStringAttribute(edge.getSource().getIdentifier(),"URI");
			target=(String) nodeAttributes.getStringAttribute(edge.getTarget().getIdentifier(),"URI");
			if(source!=null && target!=null) {
				if(source.equals(sourceNodeURI) && target.equals(targetNodeURI)) {
					//if( cyEdgeAttributes.getStringAttribute(edge.getIdentifier(),"HACK")==null);
					return (String) edgeAttributes.getStringAttribute(edge.getIdentifier(),functionName);
				}
			}
		}
		return null;
	}
	public Double getDoubleEdgeAttributeValueByURI(String sourceNodeURI, String targetNodeURI, String functionName) {
		System.out.println("Request for Double attribute "+functionName+" for Edge "+sourceNodeURI+"->"+targetNodeURI);
		if(sourceNodeURI==null || targetNodeURI==null || functionName==null) return null;
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		if(!edgeDoubleAttributes.contains(functionName)) {
			System.out.println(functionName+" is not a Double");
			return null;
		}
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		String source;
		String target;
		
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			source=(String) nodeAttributes.getStringAttribute(edge.getSource().getIdentifier(),"URI");
			target=(String) nodeAttributes.getStringAttribute(edge.getTarget().getIdentifier(),"URI");
			if(source!=null && target!=null) {
				if(source.equals(sourceNodeURI) && target.equals(targetNodeURI)) {
					//if( cyEdgeAttributes.getStringAttribute(edge.getIdentifier(),"HACK")==null);
					return  edgeAttributes.getDoubleAttribute(edge.getIdentifier(),functionName);
				}
			}
		}
		return null;
	}
	public Boolean getBooleanEdgeAttributeValueByURI(String sourceNodeURI, String targetNodeURI, String functionName) {
		System.out.println("Request for Boolean attribute "+functionName+" for Edge "+sourceNodeURI+"->"+targetNodeURI);
		if(sourceNodeURI==null || targetNodeURI==null || functionName==null) return null;
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		if(!edgeBooleanAttributes.contains(functionName)) {
			System.out.println(functionName+" is not a Boolean");
			return null;
		}
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		String source;
		String target;
	
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			source=(String) nodeAttributes.getStringAttribute(edge.getSource().getIdentifier(),"URI");
			target=(String) nodeAttributes.getStringAttribute(edge.getTarget().getIdentifier(),"URI");
			if(source!=null && target!=null) {
				if(source.equals(sourceNodeURI) && target.equals(targetNodeURI)) {
					//if( cyEdgeAttributes.getStringAttribute(edge.getIdentifier(),"HACK")==null);
					return  edgeAttributes.getBooleanAttribute(edge.getIdentifier(),functionName);
				}
			}
		}
		return null;
	}

	public Integer getIntegerEdgeAttributeValueByURI(String sourceNodeURI, String targetNodeURI, String functionName) {
		System.out.println("Request for Integer attribute "+functionName+" for Edge "+sourceNodeURI+"->"+targetNodeURI);
		if(sourceNodeURI==null || targetNodeURI==null || functionName==null) return null;
		if(myCurrentNetwork==null) {
			System.out.println("No network!");
			return null;
		}
		if(!edgeIntegerAttributes.contains(functionName)) {
			System.out.println(functionName+" is not a String");
			return null;
		}
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		String source;
		String target;
	
		for (int i=0;i<edgeIndices.length;i++) {
			Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
			source=(String) nodeAttributes.getStringAttribute(edge.getSource().getIdentifier(),"URI");
			target=(String) nodeAttributes.getStringAttribute(edge.getTarget().getIdentifier(),"URI");
			if(source!=null && target!=null) {
				if(source.equals(sourceNodeURI) && target.equals(targetNodeURI)) {
					//if( cyEdgeAttributes.getStringAttribute(edge.getIdentifier(),"HACK")==null);
					return  edgeAttributes.getIntegerAttribute(edge.getIdentifier(),functionName);
				}
			}
		}
		return null;
	}
	public Double getEdgeAttributeNumericValueByURI(String sourceNodeURI, String targetNodeURI, String functionName) {
		Double result=null;
		if(edgeDoubleAttributes.contains(functionName)) {
			result=getDoubleEdgeAttributeValueByURI(sourceNodeURI, targetNodeURI,functionName);
		}
		if(result!=null) return result;
		if(edgeIntegerAttributes.contains(functionName)) {
			Integer resultInt=getIntegerEdgeAttributeValueByURI(sourceNodeURI, targetNodeURI,functionName);
			if(resultInt!=null) result=new Double(resultInt);
		}
		if(result!=null) return result;
		if(edgeBooleanAttributes.contains(functionName)) {
			Boolean resultBool=getBooleanEdgeAttributeValueByURI(sourceNodeURI, targetNodeURI,functionName);
			if(resultBool!=null) {
				if(resultBool.booleanValue()) result=new Double(1.0);
				else result=new Double(0.0);
			}
		}
		if(result!=null) return result;
		if(edgeStringAttributes.contains(functionName)) {
			try {
				result= Double.parseDouble(getStringEdgeAttributeValueByURI(sourceNodeURI, targetNodeURI,functionName));
			} catch (Exception e) {
				System.out.println("Cannot parse "+functionName+" as a number");
				return null;
			}
		}
		return result;
	}
	

	
	
	
	
	/**
	 * @return
	 */
	public int getNodeCount() {
		if(myCurrentNetwork==null) {
			System.out.println("mhhhh where ?");
			return 0;
		}
		return myCurrentNetwork.getNodeCount();
	}
	/**
	 * @param tempID
	 * @param string
	 * @return
	 */
	/*
	public boolean mapURI(String tempID, String string) {
		CyNode node=Cytoscape.getCyNode(tempID,false);
		if(node!=null) {
			if(myCurrentNetwork.containsNode(node)) {
				Cytoscape.setNodeAttributeValue(node,"URI",string);
				return true;
			}
		}
		return false;
	}
	*/
	/**
	 * @return
	 */
	public ExpressionData getExpressionData() {
		return Cytoscape.getExpressionData();
	}
	
	/**
	 * NEW
	 * @param queryResult
	 * @param selectedIndexes
	 */
	public void addSelectedSetOfNodes(AbstractQueryResultTable queryResult, int[][] selectedIndexes) {
		if(hasGraph()==false) {
			RDFScape.warn("Don't know where to plot nodes. Going to create a network panel for you.");
			makeNewPanel("Auto");
		}
		if(queryResult==null) {
			RDFScape.warn("null Query. This should not happen.");
			return;
		} 
		if(queryResult.getRowCount()<1) {
			RDFScape.warn("Make a selection first!");
			return;
		} 
		System.out.println("Plotting "+selectedIndexes.length+" to "+myCurrentNetwork.getTitle());
		for (int i = 0; i < selectedIndexes.length; i++) {
			int x=selectedIndexes[i][0];
			int y=selectedIndexes[i][1];
			if(queryResult.isURI(x,y)) {
				//TODO check directives...
				addSimpleURI(queryResult.getURI(x,y),queryResult.getNamespace(x,y),queryResult.getLabel(x,y));
			}
			if(queryResult.isLiteral(x,y)) {
				//addSimpleLiteral();
			}
			if(queryResult.isBlank(x,y)) {
				//addSimpleBlankNode(queryResult.getValueAt(x,y));
			}
			
		}
		layoutAll();
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
	
		
	}
	/**
	 * New addNode functions...
	 */
	
	/**
	 * NEW
	 * Add a simple URI to Cytoscape. Makes proper links in memory. Return the CyNode added.
	 * @param uri the Uri for the node to be added
	 * @param namespace The namespace for this uri
	 * @param label the label that should be visualized 
	 * note: this method does not check for the presence of a graph. 
	 * It will generate a null pointer exception if not used properly.
	 */
	public CyNode[] addSimpleURI(String uri, String namespace, String label) {
		KnowledgeWrapper myKnowledge=RDFScape.getKnowledgeEngine();
		String nodeID="";
		//System.out.println("Adding "+uri+"("+namespace+")"+"->"+label);
		//System.out.println("Network : "+myCurrentNetwork);
		CyAttributes cyAttributes=Cytoscape.getNodeAttributes();
		ArrayList mappedCytoscapeIDList=RDFScape.getCytoMapper().getCytoscapeIDsForURI(uri);
		ArrayList<CyNode> myNodesList=new ArrayList<CyNode>();
		if(mappedCytoscapeIDList.size()==0) {
			nodeID=uri;
			mappedCytoscapeIDList.add(nodeID);
		}
		else {
			if(commonMemory.splitEnabled) {
				if(uri2Split.contains(uri)) {
					nodeID=uri+nodeIDCounter++;
					mappedCytoscapeIDList.add(nodeID);
				}
			}
		}
		Iterator cytoIDListIer=mappedCytoscapeIDList.iterator();
		while (cytoIDListIer.hasNext()) {
			String cytoID = (String) cytoIDListIer.next();
			CyNode node=Cytoscape.getCyNode(cytoID,true);
			cyAttributes.setAttribute(node.getIdentifier(),"TYPE","URI");
			cyAttributes.setAttribute(node.getIdentifier(),"URI",uri);
			cyAttributes.setAttribute(node.getIdentifier(),"LABEL",label);		
			cyAttributes.setAttribute(node.getIdentifier(),"COLOR",DefaultSettings.translateColor2String(commonMemory.getNamespaceColor(namespace)));
			if(commonMemory.collapseDataTypes) {
				// We should have a different design here... for some performance issues...
				if(KnowledgeWrapper.hasGraphAccessSupport(myKnowledge)) {
					String[][] attributeBox=((GraphQueryAnswerer)myKnowledge).getDatatypeAttributeBox(uri);
					for (int i = 0; i < attributeBox.length; i++) {
						cyAttributes.setAttribute(node.getIdentifier(),attributeBox[i][0],attributeBox[i][1]);
					}
				}
			}
		
			myCurrentNetwork.addNode(node);
			//myCurrentNetwork.setFlagged(node,true);
		
		
			commonMemory.registerURINamespace(uri,namespace);
			commonMemory.registerURICytoNode(uri,node);
			commonMemory.registerURILabel(uri,label);
			myNodesList.add(node);
		}
		return myNodesList.toArray(new CyNode[0]);
	}
	/**
	 * NEW 
	 * @param value the value of the literal.
	 * @param type the type of the literal (URI)
	 * @param color a string representing the color this
	 * @param literalNode an object representing this literal in the reasoning system.
	 * @return the cynode corresponding to this literal.
	 */
	public CyNode addSimpleLiteral(String value, String type, String color, Object literalNode ) {
		//System.out.println("Adding Literal "+value+"^^"+type);
		//System.out.println("Network : "+myCurrentNetwork);
		
		CyNode node=Cytoscape.getCyNode(value+"^^"+type,true);	
		//if(myCurrentNetwork.containsNode(node)) toFlag=true;
		//System.out.println(node);
		//System.out.println(node.getIdentifier());
		
		CyAttributes cyAttributes=Cytoscape.getNodeAttributes();
		cyAttributes.setAttribute(node.getIdentifier(),"TYPE","LITERAL");
		cyAttributes.setAttribute(node.getIdentifier(),"DATATYPE",type);
		cyAttributes.setAttribute(node.getIdentifier(),"LABEL",value);
		cyAttributes.setAttribute(node.getIdentifier(),"COLOR",color);
		cyAttributes.setAttribute(node.getIdentifier(),"VALUE",value);
				
		myCurrentNetwork.addNode(node);
		//myCurrentNetwork.setFlagged(node,true);
		
		//updating memory
		commonMemory.registerLiteralCyNode(literalNode,node);
		commonMemory.registerLiteralNode2Label(literalNode,value);
		if(type!=null) commonMemory.registerDatatypeURI2Literal(type,literalNode);
		return node;
		
	}
	public CyNode addSimpleBlankNode(Object bnode) {
//		TODO to compete
		System.out.println("Adding blank node "+bnode.toString());
		boolean toFlag=false;
		System.out.println("Network : "+myCurrentNetwork);
		// For simple URIs, the node of the CyNode equals the URI
		CyNode node=Cytoscape.getCyNode(bnode.toString(),true);	
		//if(myCurrentNetwork.containsNode(node)) toFlag=true;
		//System.out.println("A");
		System.out.println(node);
		//System.out.println("A2");
		System.out.println(node.getIdentifier());
		//System.out.println("A3");
		CyAttributes cyAttributes=Cytoscape.getNodeAttributes();
		cyAttributes.setAttribute(node.getIdentifier(),"TYPE","BLANK");
		cyAttributes.setAttribute(node.getIdentifier(),"LABEL","B");
		cyAttributes.setAttribute(node.getIdentifier(),"COLOR","LIGHT_GRAY");
		//System.out.println("B");
		
		//if(toFlag) 
		
		myCurrentNetwork.addNode(node);
		//myCurrentNetwork.setFlagged(node,true);
		//System.out.println("C");
		//updating memory
		commonMemory.registerBNodeCyNode(bnode,node);
		
		
		return node;
		
	}
	/*
	public CyNode addRichURI() {
//		TODO to compete
		return null;
	}
	*/
	public CyNode[] addSplittedURI() {
//		TODO to compete
		return null;		
	}
	
	public CyNode searchNodeByURI(String uri) {
//		TODO to compete
		return null;
	}
	public void updateView() {
		if(myCurrentNetworkView!=null) {
			myCurrentNetworkView.redrawGraph(true,false);
			myCurrentNetworkView.fitContent();
			myCurrentNetworkView.updateView();
		}
		
	}
	
	/**
	 * NEW Update colors after namespace properties have changed
	 * @param namespace the namespace for which some property changed
	 */
	public void updateNamespaceView(String namespace) {
		System.out.println("Repainting after change in ns: "+namespace);
		if(myCurrentNetworkView==null) return;	//Nothing to do yet
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		CyAttributes cyEdgeAttributes=Cytoscape.getEdgeAttributes();
		
		Color newColor=commonMemory.getNamespaceColor(namespace);
		
		// Nodes
		CyNode[] myNodes=commonMemory.getNamespaceCyNodes(namespace);
		for (int i = 0; i < myNodes.length; i++) {
			//cyAttributes.setAttribute(myNodes[i].getIdentifier(),"LABEL",label);
			if(!cyNodeAttributes.hasAttribute(myNodes[i].getIdentifier(),"VAR"))
				cyNodeAttributes.setAttribute(myNodes[i].getIdentifier(),"COLOR",DefaultSettings.translateColor2String(newColor));
		}
		
		// Literals
		myNodes=commonMemory.getCyNodesForLiteralDatatypeURINamespace(namespace);
		for (int i = 0; i < myNodes.length; i++) {
			//cyAttributes.setAttribute(myNodes[i].getIdentifier(),"LABEL",label);
			if(!cyNodeAttributes.hasAttribute(myNodes[i].getIdentifier(),"VAR"))
				cyNodeAttributes.setAttribute(myNodes[i].getIdentifier(),"COLOR",DefaultSettings.translateColor2String(newColor));
		}
		
		// Edges
		CyEdge[] myEdges=commonMemory.getNamespaceCyEdges(namespace);
		for (int i = 0; i < myEdges.length; i++) {
			//cyAttributes.setAttribute(myNodes[i].getIdentifier(),"LABEL",label);
			if(!cyEdgeAttributes.hasAttribute(myEdges[i].getIdentifier(),"VAR"))
				cyEdgeAttributes.setAttribute(myEdges[i].getIdentifier(),"COLOR",DefaultSettings.translateColor2String(newColor));
		}
		
		/**
		 * Maybe we need something more here...
		 */
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.updateView();
		
		
		
	}
	
	/**
	 * NEW
	 * @return
	 */
	public boolean canCollapseDatatypes() {
		return KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine());
	}
	
	/**
	 * NEW
	 * @return
	 */
	public boolean canExtend() {
		return KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine());
	}
	
	
	/**
	 * NEW
	 * @param km
	 */
	/*
	public void setKnowledge(KnowledgeWrapper km) {
		myKnowledge=km;
		
	}
	*/
	/**
	 * NEW
	 * @param node
	 * @param table
	 * @param row
	 * @param myMemory
	 */
	public void addIncomingEdge(CyNode node, AbstractQueryResultTable table, int row,CommonMemory myMemory) {
		CyNode addedNode = null;
		CyNode[] addedNodes=null;
		if(table.isURI(row,0)) {
			//TODO check directives...
			addedNodes=addSimpleURI(table.getURI(row,0),table.getNamespace(row,0),table.getLabel(row,0));
			for (int i = 0; i < addedNodes.length; i++) {
				addSimpleEdge(addedNodes[i],table.getURI(row,1),table.getNamespace(row,1),table.getLabel(row,1) ,node,myMemory);
			}
			
		}
		if(table.isLiteral(row,0)) {
			addedNode=addSimpleLiteral(table.getDatatypeValue(row,0),table.getDatatypeType(row,0),DefaultSettings.translateColor2String(table.getColor(row,0)),table.getValueAt(row,0));
			addSimpleEdge(addedNode,table.getURI(row,1),table.getNamespace(row,1),table.getLabel(row,1) ,node,myMemory);
		}
		if(table.isBlank(row,0)) {
			addedNode=addSimpleBlankNode(table.getValueAt(row,0));
			addSimpleEdge(addedNode,table.getURI(row,1),table.getNamespace(row,1),table.getLabel(row,1) ,node,myMemory);
		}
		/*
		if(addedNode==null) {
			System.out.println("Unable to add node");
			return;
		}
		*/
		CyNode[] nn=new CyNode[1];
		nn[0]=node;
		
		//myCurrentNetworkView.applyLockedLayout(CyLayouts.getDefaultLayout(), nn,new CyEdge[0] );
		//myCurrentNetworkView.redrawGraph(false,true);
		CyLayoutAlgorithm myLayout=getMyDefaultLayout();
		
		
		
		if(myCurrentNetworkView.getNodeViewCount()<5)  layoutAll();
		else if(myCurrentNetworkView.getNodeViewCount()<15)  myCurrentNetworkView.applyLockedLayout(CyLayouts.getDefaultLayout(),nn,new CyEdge[0] );
		myCurrentNetworkView.redrawGraph(false,true);
		
	
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.setZoom(myCurrentNetworkView.getZoom()*0.8);
		myCurrentNetworkView.updateView();
		
	}
	private CyLayoutAlgorithm getMyDefaultLayout() {
		CyLayoutAlgorithm defaultLayout=CyLayouts.getDefaultLayout();
		if(defaultLayout.getName().equalsIgnoreCase("grid")) {
			System.out.println("Default layout was grid... I'll use circular");
				CyLayouts.getLayout("jgraph-circle");
		}
		return defaultLayout;
	}

	/**
	 * NEW
	 */
	public void addOutgoingEdge(CyNode node, AbstractQueryResultTable table, int row,CommonMemory myMemory) {
		CyNode[] addedNodes=null;
		CyNode addedNode=null;
		CyEdge addedEdge=null;
		if(table.isURI(row,1)) {
			//TODO check directives...
			addedNodes=addSimpleURI(table.getURI(row,1),table.getNamespace(row,1),table.getLabel(row,1));
			for (int i = 0; i < addedNodes.length; i++) {
				addedEdge=addSimpleEdge(node,table.getURI(row,0),table.getNamespace(row,0),table.getLabel(row,0) ,addedNodes[i],myMemory);
			}
			
		}
		if(table.isLiteral(row,1)) {
			addedNode=addSimpleLiteral(table.getDatatypeValue(row,1),table.getDatatypeType(row,1),DefaultSettings.translateColor2String(table.getColor(row,1)),table.getValueAt(row,1));
			addedEdge=addSimpleEdge(node,table.getURI(row,0),table.getNamespace(row,0),table.getLabel(row,0) ,addedNode,myMemory);
		}
		if(table.isBlank(row,1)) {
			addedNode=addSimpleBlankNode(table.getValueAt(row,1));
			addedEdge=addSimpleEdge(node,table.getURI(row,0),table.getNamespace(row,0),table.getLabel(row,0) ,addedNode,myMemory);
		}
		System.out.println("Adding outgoing edge");
		//myCurrentNetworkView.fitContent();
		
		CyNode[] nn=new CyNode[1];
		nn[0]=node;
		
		
		CyLayoutAlgorithm myLayout=getMyDefaultLayout();
		
		
		
		if(myCurrentNetworkView.getNodeViewCount()<5)  layoutAll();
		else if(myCurrentNetworkView.getNodeViewCount()<15)  myCurrentNetworkView.applyLockedLayout(CyLayouts.getDefaultLayout(),nn,new CyEdge[0] );
		myCurrentNetworkView.redrawGraph(false,true);
	
		/*
		System.out.println("Deaful layout: "+CyLayouts.getDefaultLayout());
		Collection<CyLayoutAlgorithm> layoutsList=CyLayouts.getAllLayouts();
		for (Iterator iterator = layoutsList.iterator(); iterator.hasNext();) {
			CyLayoutAlgorithm current = (CyLayoutAlgorithm) iterator.next();
				System.out.println("> "+current.getName());
			
		}
		if(myCurrentNetworkView.getNodeViewCount()<=3) myCurrentNetworkView.redrawGraph(true,true);
		else {
			myCurrentNetworkView.redrawGraph(false,true);
			myCurrentNetworkView.applyLockedLayout(CyLayouts.getDefaultLayout(),nodeList,edgeList );
		}
		*/
		//layoutAll();
		
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.setZoom(myCurrentNetworkView.getZoom()*0.8);
		myCurrentNetworkView.updateView();
		
	}
	
	/**
	 * NEW
	 * @param source
	 * @param uri
	 * @param namespace
	 * @param label
	 * @param object
	 * @param myMemory
	 */
	private CyEdge addSimpleEdge(CyNode source, String uri, String namespace, String label, CyNode object,CommonMemory myMemory) {
		CyEdge edge=Cytoscape.getCyEdge(source.getIdentifier(),uri,object.getIdentifier(),"_concept_");
		CyAttributes cyAttributes=Cytoscape.getEdgeAttributes();
		System.out.println("Adding edge with the following attributes:\n" +
				"COLOR "+DefaultSettings.translateColor2String(myMemory.getNamespaceColor(namespace))+"\n"+
				"URI "+uri+"\n"+
				"LABEL "+label+"\n");
		cyAttributes.setAttribute(edge.getIdentifier(),"COLOR",DefaultSettings.translateColor2String(myMemory.getNamespaceColor(namespace)));
		cyAttributes.setAttribute(edge.getIdentifier(),"URI",uri);
		cyAttributes.setAttribute(edge.getIdentifier(),"LABEL",label);
		cyAttributes.setAttribute(edge.getIdentifier(),"TYPE","PROPERTY");
			
			
		//Cytoscape.setEdgeAttributeValue(edge,"HACK","P");
		myCurrentNetwork.addEdge(edge);
		//myCurrentNetwork.setFlagged(edge,true);
		//myCurrentNetworkView.redrawGraph(true,false);
		//myCurrentNetworkView.fitContent();
		//myCurrentNetworkView.updateView();
		
		
		//updating memory
		//commonMemory.registerURINamespace(uri,namespace);
		commonMemory.registerURINamespace(uri, namespace);
		commonMemory.registerURICytoEdge(uri,edge);
		commonMemory.registerURILabel(uri,label);
		return edge;
		
	}
	public String[][] getTripleWithVariablesArrayList() {
		if(myCurrentNetwork.getTitle()==null) return new String[0][3];
		tempFilterConditions=new ArrayList();
		ArrayList patternCollection=new ArrayList();
		Iterator edgeIterator=myCurrentNetwork.edgesIterator();
		CyAttributes nodeAttributes=Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes=Cytoscape.getEdgeAttributes();
		while(edgeIterator.hasNext()) {
			Edge currentEdge=(Edge)edgeIterator.next();
			Node source=currentEdge.getSource();
			Node target=currentEdge.getTarget();
			String firstTerm=null;
			String secondTerm=null;
			String thirdTerm=null;
			if(nodeAttributes.hasAttribute(source.getIdentifier(),"VAR")) {
				firstTerm=nodeAttributes.getStringAttribute(source.getIdentifier(),"VAR");
				if(nodeAttributes.hasAttribute(source.getIdentifier(),"FILTER")) {
					String firstTermFilter=nodeAttributes.getStringAttribute(source.getIdentifier(),"FILTER");
					String[] filterElement={firstTerm,firstTermFilter,"F"};
					tempFilterConditions.add(filterElement);
				}
			}
			else {
				String type=nodeAttributes.getStringAttribute(source.getIdentifier(),"TYPE");
				if(type.equalsIgnoreCase("BLANK")) {
					firstTerm="?b"+bNodeCounter++;
				}
				if(type.equalsIgnoreCase("LITERAL")) {
					String firstTermFilter=nodeAttributes.getStringAttribute(source.getIdentifier(),"VALUE");
					firstTerm="?l"+literalNodeCounter++;
					String[] filterElement={firstTerm,firstTermFilter,"P"};
					tempFilterConditions.add(filterElement);
				}
				if(type.equalsIgnoreCase("URI")) {
					firstTerm=nodeAttributes.getStringAttribute(source.getIdentifier(),"URI");
				}
			}
			
			
			if(edgeAttributes.hasAttribute(currentEdge.getIdentifier(),"VAR")) {
				secondTerm=edgeAttributes.getStringAttribute(currentEdge.getIdentifier(),"VAR");
			}
			else {
				secondTerm=edgeAttributes.getStringAttribute(currentEdge.getIdentifier(),"URI");
			}
			
			if(nodeAttributes.hasAttribute(target.getIdentifier(),"VAR")) {
				thirdTerm=nodeAttributes.getStringAttribute(target.getIdentifier(),"VAR");
				if(nodeAttributes.hasAttribute(target.getIdentifier(),"FILTER")) {
					String thirdTermFilter=nodeAttributes.getStringAttribute(target.getIdentifier(),"FILTER");
					String[] filterElement={thirdTerm,thirdTermFilter,"F"};
					tempFilterConditions.add(filterElement);
				}
			}
			else {
				String type=nodeAttributes.getStringAttribute(target.getIdentifier(),"TYPE");
				if(type.equalsIgnoreCase("BLANK")) {
					thirdTerm="?b"+bNodeCounter++;
				}
				if(type.equalsIgnoreCase("LITERAL")) {
					String thirdTermFilter=nodeAttributes.getStringAttribute(target.getIdentifier(),"VALUE");
					thirdTerm="?l"+literalNodeCounter++;
					String[] filterElement={thirdTerm,thirdTermFilter,"P"};
					tempFilterConditions.add(filterElement);
				}
				if(type.equalsIgnoreCase("URI")) {
					thirdTerm=nodeAttributes.getStringAttribute(target.getIdentifier(),"URI");
				}
			}
			String[] thisElement={firstTerm,secondTerm,thirdTerm};
			System.out.println("Pattern: "+firstTerm+"---"+secondTerm+"---"+thirdTerm);
			patternCollection.add(thisElement);
			
		}
		/*
		int[] edgeIndices=myCurrentNetwork.getEdgeIndicesArray();
		System.out.println(myCurrentNetwork.getTitle()+"-> "+(String)networkType.get(myCurrentNetwork.getTitle()));
		checkGenericType();
		if(((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("RDF") ||
				((String)networkType.get(myCurrentNetwork.getTitle())).equalsIgnoreCase("GENERIC")) {
			System.out.println("IN");
			for (int i=0;i<edgeIndices.length;i++) {
				System.out.println(".");
				Edge edge = (Edge) myCurrentNetwork.getEdge(edgeIndices[i]);
				Node source=edge.getSource();
				Node target=edge.getTarget();
		
				
				String firstTerm=null;
				String secondTerm=null;
				String thirdTerm=null;
				
				String sourceURI=(String) myCurrentNetwork.getNodeAttributeValue(source,"URI");
				String sourceVAR=(String) myCurrentNetwork.getNodeAttributeValue(source,"VAR");
				if(sourceVAR!=null) firstTerm=sourceVAR;
				else firstTerm=sourceURI;
				String firstFilter=(String) myCurrentNetwork.getNodeAttributeValue(source,"FILTER");
				
				String targetURI=(String) myCurrentNetwork.getNodeAttributeValue(target,"URI");
				String targetVAR=(String) myCurrentNetwork.getNodeAttributeValue(target,"VAR");
				if(targetVAR!=null) thirdTerm=targetVAR;
				else thirdTerm=targetURI;
				String thirdFilter=(String) myCurrentNetwork.getNodeAttributeValue(target,"FILTER");
				
				String edgeURI=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"URI");
				String edgeVAR=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"VAR");
				if(edgeVAR!=null) secondTerm=edgeVAR;
				else secondTerm=edgeURI;
				String secondFilter=(String) myCurrentNetwork.getEdgeAttributeValue(edge,"FILTER");
				
				String[] myLine={firstTerm,secondTerm,thirdTerm,firstFilter,secondFilter,thirdFilter};
				patternCollection.add(myLine);
			
			}
		}
		//myCurrentNetwork.unFlagAllEdges(); 
		// TODO Auto-generated method stub
		return null;
		*/
		System.out.println("Filters: ");
		for (Iterator iter = tempFilterConditions.iterator(); iter.hasNext();) {
			String[] element = (String[]) iter.next();
			System.out.println(element[0]+" "+element[1]+" "+element[2]);
		}
		String[][] patternCollectionArray=new String[0][3];
		return (String[][]) patternCollection.toArray(patternCollectionArray);
	}
	public String[][] getFilterConditionsArray() {
		String[][] filterConditionArray=new String[0][3];
		if(tempFilterConditions.size()==0) return filterConditionArray;
		else return (String[][]) tempFilterConditions.toArray(filterConditionArray);
	}
	public void searchURIs(AbstractQueryResultTable myResult) {
		if(!hasGraph()) {
			RDFScape.warn("No graph selected.\nPlease create a graph first.");
			return;
		} 
		for (int i = 0; i < myResult.getRowCount(); i++) {
			for (int j = 0; j < myResult.getColumnCount(); j++) {
				if(myResult.isURI(i,j)) {
					CyNode[] cyNodes=commonMemory.getCyNodesForURI(myResult.getURI(i,j));
					for (int k = 0; k < cyNodes.length; k++) {
						myCurrentNetwork.setSelectedNodeState(cyNodes[k],true);
					}	
					
				}
			}
		}
		myCurrentNetworkView.redrawGraph(true,false);
		//myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
		
	}
	public void addURIs(AbstractQueryResultTable myResult) {
		if(!hasGraph()) {
			RDFScape.warn("No graph selected.\nPlease create a graph first.");
			return;
		} 
		for (int i = 0; i < myResult.getRowCount(); i++) {
			for (int j = 0; j < myResult.getColumnCount(); j++) {
				if(myResult.isURI(i,j)) {
					addSimpleURI(myResult.getURI(i,j),myResult.getNamespace(i,j),myResult.getLabel(i,j));
				}
			}
		}
		layoutAll();
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
		
		
	}
	public void searchPattern(AbstractQueryResultTable myResult) {
		if(!hasGraph()) {
			RDFScape.warn("No graph selected.\nPlease create a graph first.");
			return;
		} 
		
		/**
		 * Must speed up a little... introdusing some caching...
		 * 
		 */
		System.out.println("Going to search for this pattern");
		Hashtable<String,Hashtable<String,CyEdge>> edgesCache=new Hashtable<String,Hashtable<String,CyEdge>>();
		
		for (int i = 0; i < myResult.getRowCount(); i++) {
			System.out.println("Checking result line #"+i);
			CyNode[] sources=searchGenericNodeFromTable(myResult,i,0,false);
			CyNode[] targets=searchGenericNodeFromTable(myResult,i,2,false);
			//This is going to be long!
			Hashtable<String,CyEdge> myEdgesByNodes=edgesCache.get(myResult.getURI(i,1));
			if(myEdgesByNodes==null) {
				System.out.println("First time I see uri: "+myResult.getURI(i,1));
				myEdgesByNodes=new Hashtable<String,CyEdge>();
				CyEdge[] edges=commonMemory.getCyEdgesFromURI(myResult.getURI(i,1));
				for (int j = 0; j < edges.length; j++) {
					System.out.println("Processing edge #"+j);
					CyNode currentSource=(CyNode) edges[j].getSource();
					CyNode currentTarget=(CyNode) edges[j].getTarget();
					myEdgesByNodes.put(currentSource.getIdentifier()+currentTarget.getIdentifier(),edges[j]);
				}
				edgesCache.put(myResult.getURI(i,1),myEdgesByNodes);
			}
			
			
			for (int l = 0; l < sources.length; l++) {
				for (int r = 0; r < targets.length; r++) {
					CyEdge tempEdge=myEdgesByNodes.get(sources[l].getIdentifier()+targets[l].getIdentifier());
					if(tempEdge!=null) {
						myCurrentNetwork.setSelectedEdgeState(tempEdge,true);
						myCurrentNetwork.setSelectedNodeState(tempEdge.getSource(),true);
						myCurrentNetwork.setSelectedNodeState(tempEdge.getTarget(),true);
						
					}
				}
				
				
			}
			
			
		
		}
		myCurrentNetworkView.redrawGraph(true,false);
		//myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
		
	}
	public void addPattern(AbstractQueryResultTable myResult) {
		if(!hasGraph()) {
			RDFScape.warn("Don't know where to plot it. Going to create a network panel for you");
			makeNewPanel("Auto");
		} 
		if(myResult.getRowCount()==0) {
			RDFScape.warn("No matches.");
			return;
		}
		for (int i = 0; i < myResult.getRowCount(); i++) {
				CyNode[] firstNodes=null;
				CyNode[] secondNodes=null;
				//System.out.println(i+","+j+"-> U "+myResult.isURI(i,j)+" B "+
				//		myResult.isBlank(i,j)+" L "+myResult.isLiteral(i,j));
				firstNodes=addGenericNodeFromTable(myResult,i,0);
				secondNodes=addGenericNodeFromTable(myResult,i,2);
				for (int j = 0; j < firstNodes.length; j++) {
					for (int j2 = 0; j2 < secondNodes.length; j2++) {
						addSimpleEdge(firstNodes[j],myResult.getURI(i,1),myResult.getNamespace(i,1),myResult.getLabel(i,1),secondNodes[j2],commonMemory);
					}
					
				}
				
				
			
		}
		layoutAll();
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
		
		
	}

	private CyNode[] addGenericNodeFromTable(AbstractQueryResultTable table,int x, int y) {
		if(table.isURI(x,y)) {
		   /*
			System.out.println("Adding node with attributes:\n" +
					"URI "+table.getURI(x,y)+"\n" +
					"Namespace "+table.getNamespace(x,y)+"\n" +
					"Label "+table.getLabel(x,y)+"\n");
			*/
			return addSimpleURI(table.getURI(x,y),table.getNamespace(x,y),table.getLabel(x,y));
		} else 
		if(table.isLiteral(x,y)) {
			/*
			System.out.println("Adding literal node with attributes:\n"+
					"Value "+table.getDatatypeValue(x,y)+"\n"+
					"Type "+table.getDatatypeType(x,y)+"\n"+
					"Color "+DefaultSettings.translateColor2String(table.getColor(x,y))+"\n"+
					"? "+table.getValueAt(x,y)+"\n");
			*/
			CyNode[] result=new CyNode[1];
			result[0]= addSimpleLiteral(table.getDatatypeValue(x,y),table.getDatatypeType(x,y),DefaultSettings.translateColor2String(table.getColor(x,y)),table.getValueAt(x,y));
			return result;
		} else
		if(table.isBlank(x,y)) {
			/*
			System.out.println("Adding blank node with attributes:\n" +
					"Value "+table.getValueAt(x,y)+"\n");
			*/
			CyNode[] result=new CyNode[1];
			result[0]= addSimpleBlankNode(table.getValueAt(x,y));
			return result;
		} else return null;
	}
	
	private CyNode[] searchGenericNodeFromTable(AbstractQueryResultTable table,int x,int y, boolean b) {
		if(table.isURI(x,y)) {
			CyNode[] uris=commonMemory.getCyNodesForURI(table.getURI(x,y));
			if(uris!=null) {
				for (int j = 0; j < uris.length; j++) {
					if(b) myCurrentNetwork.setSelectedNodeState(uris[j],true);
				}
			}
			return uris;
			
		}
		else if(table.isLiteral(x,y)) {
			CyNode[] literals=commonMemory.getCyNodeForLiteralNode(table.getValueAt(x,y));
			if(literals!=null) {
				for (int j = 0; j < literals.length; j++) {
					if(b) myCurrentNetwork.setSelectedNodeState(literals[j],true);
				}
			}
			return literals;
		}
		else if(table.isBlank(x,y)) {
			CyNode[] blank=commonMemory.getCyNodesForBNode(table.getValueAt(x,y));
			if(blank!=null) {
				for (int j = 0; j < blank.length; j++) {
					if(b) myCurrentNetwork.setSelectedNodeState(blank[j],true);
				}
			}
			return blank;
		}
		else return new CyNode[0];
	}
	public  void updateURILabel(String string) {
		CyAttributes cyNodeAttributes=Cytoscape.getNodeAttributes();
		CyNode[] nodes=commonMemory.getCyNodesForURI(string);
		for (int i = 0; i < nodes.length; i++) {
			String label=commonMemory.getLabelForURI(string);
			if(label!=null) 
				cyNodeAttributes.setAttribute(nodes[i].getIdentifier(),"LABEL",label);
		}
		
	}
	public void addSplitConditions(AbstractQueryResultTable mySplit) {
		System.out.println("Split!");
		if(mySplit.getColumnCount()!=1) {
			System.out.println("I want only uris!");
			return;
		}
		for (int i = 0; i < mySplit.getRowCount(); i++) {
			if(mySplit.isURI(i,0)) uri2Split.add(mySplit.getURI(i,0));
		}
		
	}
	public void restoreSplitConditions() {
		uri2Split=new HashSet();
		
	}
	public int[] mapNodes(Hashtable id2URI) {
		HashSet uniqueURIs=new HashSet();
		int nodesMatched=0;
		int[] results={0,0};
		if(myCurrentNetwork==null) return results;
		CyAttributes nodeAttributes=Cytoscape.getNodeAttributes();
		CyAttributes edgeAttributes=Cytoscape.getEdgeAttributes();
		Iterator nodeIterator= myCurrentNetwork.nodesIterator();
		int ncounter=0;
		// Init Attributes 
		
		analyzeAttributes();
		RDFScape.getCommonMemory().registerNameSpace("http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#");
		RDFScape.getCommonMemory().registerPrefix("http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#","n"+Cytoscape.getCurrentNetwork().getIdentifier());
		RDFScape.getCommonMemory().registerNameSpaceColor("http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#",DefaultSettings.translateString2Color("ORANGE"));
		
		while(nodeIterator.hasNext()) {
			System.out.print("Checking node # "+ncounter++ +" : ");
			CyNode currentNode=(CyNode) nodeIterator.next();
			System.out.println(currentNode.getIdentifier());
			if(nodeAttributes.hasAttribute(currentNode.getIdentifier(),"URI")) {
				String uri=nodeAttributes.getStringAttribute(currentNode.getIdentifier(),"URI");
				nodesMatched++;
				uniqueURIs.add(uri);
				
				extractNode(currentNode,uri,nodeAttributes);
			}
			else {
				String uri=(String) id2URI.get(currentNode.getIdentifier());
				System.out.println(currentNode.getIdentifier()+"->"+uri);
				if(uri!=null) {
					nodesMatched++;
					uniqueURIs.add(uri);
					nodeAttributes.setAttribute(currentNode.getIdentifier(), "URI", uri);
					extractNode(currentNode,uri,nodeAttributes);
					
				}
			}
			
		}
		Iterator edgeIterator=myCurrentNetwork.edgesIterator();
		int ecounter=0;
		
		while(edgeIterator.hasNext()) {
			//System.out.print("Edge: "+ecounter++ +"  ");
			CyEdge currentEdge=(CyEdge) edgeIterator.next();
			if(edgeAttributes.hasAttribute(currentEdge.getIdentifier(),"URI")) {
				String uri=edgeAttributes.getStringAttribute(currentEdge.getIdentifier(),"URI");
				extractEdge(currentEdge,uri,edgeAttributes);
			}
			else {
				String uri=(String) id2URI.get(currentEdge.getIdentifier());
				// Note, for edges, this should be always empty
				if(uri!=null) {
					extractEdge(currentEdge,uri,edgeAttributes);
				}
			}
			
		}
		
		updateView();
		results[0]=nodesMatched;
		results[1]=uniqueURIs.size();
		System.out.println("Nodes matched: "+nodesMatched);
		System.out.println("URIs resolved: "+uniqueURIs.size());
		
		if(networkHasMenu.get(myCurrentNetworkView.getIdentifier())==null) {
			myCurrentNetworkView.addNodeContextMenuListener(menuListener);
			myCurrentNetworkView.addEdgeContextMenuListener(menuListener);
		}
		networkHasMenu.put(myCurrentNetwork.getIdentifier(),new Boolean(true));
		
		
		RDFScape.getKnowledgeEngine().touch();
		return results;
		
	}
	private void extractNode(CyNode node, String uri, CyAttributes attributes) {
		//commonMemory.registerURINamespace(uri);
		commonMemory.registerURICytoNode(uri,node);
		String nodeID=node.getIdentifier();
		String label=uri;
		if(KnowledgeWrapper.hasGraphAccessSupport(RDFScape.getKnowledgeEngine())) {
			/*
			if(attributes.hasAttribute(node.getIdentifier(),"canonicalName")) {
				String labelString=attributes.getStringAttribute(node.getIdentifier(),"canonicalName");
				((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addDataStatement(uri,"http://www.w3.org/2000/01/rdf-schema#label",labelString);
				label=((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).getShortLabelForURI(uri);
				if(label==null) label="N\\A";
			}
			*/
			System.out.println("NEW-NODE-EXCTRACTION-UNOPTIMIZED-BEGIN");
			// We don't get Strings... just values
			/*
			for (int i = 0; i < nodeStringAttributesNames.length; i++) {
				if(attributes.hasAttribute(nodeID, nodeStringAttributesNames[i])) {
					System.out.println("Found String attribute "+nodeStringAttributesNames[i]+" for node "+nodeID);
					String attributeValue=attributes.getStringAttribute(nodeID,nodeStringAttributesNames[i]);
					((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addTypedDataStatement(uri,"http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#"+nodeStringAttributesNames[i],attributeValue, "String");
					
				}
			}
			*/
			for (int i = 0; i < nodeDoubleAttributesNames.length; i++) {
				if(attributes.hasAttribute(nodeID, nodeDoubleAttributesNames[i])) {
					System.out.println("Found String attribute "+nodeDoubleAttributesNames[i]+" for node "+nodeID);
					Double attributeValue=attributes.getDoubleAttribute(nodeID,nodeDoubleAttributesNames[i]);
					((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addTypedDataStatement(uri,"http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#"+nodeDoubleAttributesNames[i],attributeValue, "Double");
					
				}
			}
			for (int i = 0; i < nodeBooleanAttributesNames.length; i++) {
				if(attributes.hasAttribute(nodeID, nodeBooleanAttributesNames[i])) {
					System.out.println("Found String attribute "+nodeBooleanAttributesNames[i]+" for node "+nodeID);
					Boolean attributeValue=attributes.getBooleanAttribute(nodeID,nodeBooleanAttributesNames[i]);
					((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addTypedDataStatement(uri,"http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#"+nodeBooleanAttributesNames[i],attributeValue, "Boolean");
					
				}
			}
			for (int i = 0; i < nodeIntegerAttributesNames.length; i++) {
				if(attributes.hasAttribute(nodeID, nodeIntegerAttributesNames[i])) {
					System.out.println("Found String attribute "+nodeIntegerAttributesNames[i]+" for node "+nodeID);
					Integer attributeValue=attributes.getIntegerAttribute(nodeID,nodeIntegerAttributesNames[i]);
					((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addTypedDataStatement(uri,"http://rdfscape/network"+Cytoscape.getCurrentNetwork().getIdentifier()+"#"+nodeIntegerAttributesNames[i],attributeValue, "Integer");
					
				}
			}
			
			System.out.println("NEW-NODE-EXTRACTION-UNOPTIMIZED-END");
			
		}
		commonMemory.registerURILabel(uri,label);
		
		
	}
	private void extractEdge(CyEdge edge, String uri,CyAttributes attributes) {
		//commonMemory.registerURINamespace(uri);
		commonMemory.registerURICytoEdge(uri,edge);
		String label=uri;
		
		// Edges corresponds to generic properties, doesn't make sense to get specific values for them.
		/*
		if(KnowledgeWrapper.hasGraphAccessSupport(RDFScape.getKnowledgeEngine())) {
			if(attributes.hasAttribute(edge.getIdentifier(),"canonicalName")) {
				String labelString=attributes.getStringAttribute(edge.getIdentifier(),"canonicalName");
				((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).addDataStatement(uri,"http://www.w3.org/2000/01/rdf-schema#label",labelString);
				if(commonMemory.showRDFSLabels) label=((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).getRDFLabelForURI(uri);
				else label=((GraphQueryAnswerer)RDFScape.getKnowledgeEngine()).getShortLabelForURI(uri);
			}
		}
		*/
		commonMemory.registerURILabel(uri,label);
		
	}
	public boolean hasGraph() {
		//if(myCurrentNetwork.getTitle()==null || myCurrentNetwork.getTitle()=="0") return false;
		System.out.println("Number of graphs: "+Cytoscape.getNetworkSet().size());
		//if(Cytoscape.getCurrentNetwork ==Cytoscape.getNullNetwork()) return false;
		if(!Cytoscape.viewExists(Cytoscape.getCurrentNetwork().getIdentifier())) return false;
		if(Cytoscape.getNetworkSet().size()>0) return true;
		else return false;
	}
	
	
	public CyNetworkView getCurrentNetworkView() {
		return myCurrentNetworkView;
	}
	
	/**
	 * Return true if the current network is editable
	 * @return
	 */
	public boolean isEditable() {
		String networkName=myCurrentNetwork.getTitle();
		if(networkName==null) return false;
		if(isEditable.contains(networkName))
			return true;
		else return false;
	}
	
	/**
	 * return true if the Cytoscape node is labeled as a variable
	 */
	public boolean isVariable(NodeView node) {
		CyNode cyNode=(CyNode) node.getNode();
		CyAttributes nodeAttributes=Cytoscape.getNodeAttributes();
		
		if(nodeAttributes.hasAttribute(cyNode.getIdentifier(),"VAR"))
		
		return true;
		else return false;
	}
	
	/**
	 * return true if the Cytoscape edge is labeled as a variable
	 */
	public boolean isVariable(EdgeView edge) {
		CyEdge cyEdge=(CyEdge) edge.getEdge();
		CyAttributes edgeAttributes=Cytoscape.getEdgeAttributes();
		if(edgeAttributes.hasAttribute(cyEdge.getIdentifier(),"VAR"))
			return true;
			else return false;	
	
	}
	
	/**
	 * Layout the current graph view using the "default" layout algorithm.
	 * Note that now we have only a dirty dirty workaround (activating menus!!!)
	 * TODO wait for Cytoscape team to shed the light on layout managers.
	 * TODO if kept like this, should be able at least to get a default layout algorithm
	 *
	 */
	public void layoutAll() {
		JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getLayoutMenu();
        MenuElement[] popup = layoutMenu.getSubElements();
        MenuElement[] submenus = ((JPopupMenu) popup[0]).getSubElements();
        JMenuItem yFiles = null;
        for(int i=0; i<submenus.length; i++){
            yFiles = ((JMenuItem)submenus[i]);
            if(yFiles.getText().equals("yFiles"))
                break;
        }
        popup = yFiles.getSubElements();
        submenus = ((JPopupMenu) popup[0]).getSubElements();
        JMenuItem yCircular = null;
        for(int i=0; i<submenus.length; i++){
            yCircular = ((JMenuItem)submenus[i]);
            if(yCircular.getText().equals("Circular"))
                break;
        }
        yCircular.doClick();
		
	}
	
	/**
	 * Layout the Cytoscape graph after a bunch of element is added
	 * TODO to clean the sequence of updates
	 */
	public void displayAdaptAfterMassAddition() {
		layoutAll();
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
	}
	
	/**
	 * Layout the Cytoscape graph after an element is added
	 * @param lastPoint (athe last point added or to be considered in particular... hint for the layot algorithm)
	 * TODO ti implement specific functionalities, as Layouts in Cytoscape get more clear.
	 * TODO to clean the sequence of updates.
	 */
	public void displayAdaptAfterSingleAdditon(CyNode lastPoint) {
		layoutAll();
		myCurrentNetworkView.redrawGraph(true,false);
		myCurrentNetworkView.fitContent();
		myCurrentNetworkView.updateView();
	}
	
	private void analyzeAttributes() {
		nodeAttributes=Cytoscape.getNodeAttributes();
		edgeAttributes=Cytoscape.getEdgeAttributes();
		
		nodeAttributesNames=nodeAttributes.getAttributeNames();
		edgeAttributesNames=edgeAttributes.getAttributeNames();
		
		ArrayList<String> tempStringList=new ArrayList<String>();
		ArrayList<String> tempDoubleList=new ArrayList<String>();
		ArrayList<String> tempBooleanList=new ArrayList<String>();
		ArrayList<String> tempIntegerList=new ArrayList<String>();
		
		for (int i = 0; i < nodeAttributesNames.length; i++) {
			if(nodeAttributes.getType(nodeAttributesNames[i])==CyAttributes.TYPE_STRING) {
				tempStringList.add(nodeAttributesNames[i]);
			}
			if(nodeAttributes.getType(nodeAttributesNames[i])==CyAttributes.TYPE_FLOATING) {
				tempDoubleList.add(nodeAttributesNames[i]);
			}
			if(nodeAttributes.getType(nodeAttributesNames[i])==CyAttributes.TYPE_BOOLEAN) {
				tempBooleanList.add(nodeAttributesNames[i]);
			}
			if(nodeAttributes.getType(nodeAttributesNames[i])==CyAttributes.TYPE_INTEGER) {
				tempIntegerList.add(nodeAttributesNames[i]);
			}
			
		}
		
		nodeStringAttributesNames=tempStringList.toArray(nodeStringAttributesNames);
		nodeDoubleAttributesNames=tempDoubleList.toArray(nodeDoubleAttributesNames);
		nodeBooleanAttributesNames=tempBooleanList.toArray(nodeBooleanAttributesNames);
		nodeIntegerAttributesNames=tempIntegerList.toArray(nodeIntegerAttributesNames);
		
		tempStringList=new ArrayList<String>();
		tempDoubleList=new ArrayList<String>();
		tempBooleanList=new ArrayList<String>();
		tempIntegerList=new ArrayList<String>();
		
		for (int i = 0; i < edgeAttributesNames.length; i++) {
			if(edgeAttributes.getType(edgeAttributesNames[i])==CyAttributes.TYPE_STRING) {
				tempStringList.add(edgeAttributesNames[i]);
			}
			if(edgeAttributes.getType(edgeAttributesNames[i])==CyAttributes.TYPE_FLOATING) {
				tempDoubleList.add(edgeAttributesNames[i]);
			}
			if(edgeAttributes.getType(edgeAttributesNames[i])==CyAttributes.TYPE_BOOLEAN) {
				tempBooleanList.add(edgeAttributesNames[i]);
			}
			if(edgeAttributes.getType(edgeAttributesNames[i])==CyAttributes.TYPE_INTEGER) {
				tempIntegerList.add(edgeAttributesNames[i]);
			}
		}
		edgeStringAttributesNames=tempStringList.toArray(edgeStringAttributesNames);
		edgeDoubleAttributesNames=tempDoubleList.toArray(edgeDoubleAttributesNames);
		edgeBooleanAttributesNames=tempBooleanList.toArray(edgeBooleanAttributesNames);
		edgeIntegerAttributesNames=tempIntegerList.toArray(edgeIntegerAttributesNames);
		
		
		System.out.println("Had a look at attributes, found: ");
		System.out.println(nodeStringAttributesNames.length+" node String attributes");
		System.out.println(nodeDoubleAttributesNames.length+" node Double attributes");
		System.out.println(nodeBooleanAttributesNames.length+" node Boolean attributes");
		System.out.println(nodeIntegerAttributesNames.length+" node Integer attributes");
		System.out.println(edgeStringAttributesNames.length+" edge String attributes");
		System.out.println(edgeDoubleAttributesNames.length+" edge Double attributes");
		System.out.println(edgeBooleanAttributesNames.length+" edge Boolean attributes");
		System.out.println(edgeIntegerAttributesNames.length+" edge Integer attributes");
		
		nodeStringAttributes=new HashSet<String>(Arrays.asList(nodeStringAttributesNames));
		nodeDoubleAttributes=new HashSet<String>(Arrays.asList(nodeDoubleAttributesNames));
		nodeBooleanAttributes=new HashSet<String>(Arrays.asList(nodeBooleanAttributesNames));
		nodeIntegerAttributes=new HashSet<String>(Arrays.asList(nodeIntegerAttributesNames));
		
		edgeStringAttributes=new HashSet<String>(Arrays.asList(edgeStringAttributesNames));
		edgeDoubleAttributes=new HashSet<String>(Arrays.asList(edgeDoubleAttributesNames));
		edgeBooleanAttributes=new HashSet<String>(Arrays.asList(edgeBooleanAttributesNames));
		edgeIntegerAttributes=new HashSet<String>(Arrays.asList(edgeIntegerAttributesNames));
		
		
	
		
		

	}
	

}
