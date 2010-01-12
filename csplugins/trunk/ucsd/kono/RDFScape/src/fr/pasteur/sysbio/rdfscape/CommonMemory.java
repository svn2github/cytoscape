/**
 * Copyright 2005-2006 Andrea Splendiani
 * Created on Jan 24, 2006
 *
 * 
 */
package fr.pasteur.sysbio.rdfscape;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;

import cytoscape.CyEdge;
import cytoscape.CyNode;
import fr.pasteur.sysbio.rdfscape.knowledge.JenaWrapper;
/**
 * @author andrea@sgtp.net
 * mantains all links between cytoscape and SW resources and all information common to modules.
 * 
 */
public class CommonMemory implements RDFScapeModuleInterface{
	private Hashtable namespace2prefix=null;
	private Hashtable prefix2namespace=null;
	private Hashtable namespace2color;
	private Hashtable color2namespace;
	private HashSet		activeNamespaces;
	private HashSet		inactiveNamespaces;
	private Hashtable namespace2URIs;
	private Hashtable uri2Namespace;
	private Hashtable uri2CyNode;
	private Hashtable cyNode2URI;
	private Hashtable bnode2CyNode;
	private Hashtable cyNode2BNode;
	private Hashtable literalNode2CyNode;
	private Hashtable cyNode2LiteralNode;
	private Hashtable datatypeURI2Literals;
	private Hashtable literal2DatatypeURI;
	private Hashtable uri2CyEdge=null;
	private Hashtable cyEdge2URI=null;
	private Hashtable label2URI=null;
	private Hashtable uri2Label=null;
	private Hashtable literalNode2Label=null;
	
	private ArrayList namespaces;				// This must be ordered!
	
	//Hashtable handlerToRichResource;
	//Hashtable namespaceToRichResourcesList;
	//private Hashtable CytoscapeIDToRichResource;
	
	private HashSet dirtyNsList; 					// Namespaces whose properties have changed (Cyoscape must be updated!)
	//private HashSet	dirtyCList;
	
	ArrayList viewerList=null;
	private static int nsc=0;
	
	Pattern nonNamespacePattern;    
	
	public boolean collapseDataTypes;
	boolean namespaceConditionInAnd;
	boolean propagateURI=false;
	public boolean showRDFSLabels=false;
	public boolean splitEnabled=false;
	
	public CommonMemory() {
		super();
		nonNamespacePattern=Pattern.compile("[^/#]*$");   
		viewerList=new ArrayList();
		initialize();
		
		
	}


	/**
	 * @param prefix
	 * @param namespace
	 * If the namespace is new, it registers namespace / prefix
	 * If the namespace was already known, it register the prefix only if this was absent.
	 * In other words, old prefixes have the precedence.
	 */
	public void registerNameSpace(String namespace) {
		System.out.println("r");
		if(!namespaces.contains(namespace)) {
			System.out.println("R");
			namespaces.add(namespace);
			System.out.println(namespace+" + "+DefaultSettings.defaultColor.toString());
			registerNameSpaceColor(namespace,DefaultSettings.defaultColor);
			activeNamespaces.add(namespace);
			System.out.println("K");
			
			
		}
	}
	
	/**
	 * 
	 */
	private void updateNamespaceView() {
		
		
	}

	public void registerPrefixAsync(String namespace, String prefix) {
		System.out.println("Registering prefix "+prefix+" for namespace "+namespace);
		if(prefix==null) prefix="";
		//We have for cases we handle here
		/**
		 * TODO this is now redoundant since the same check is in touch() in namspacemnager
		 */
		/*
		while(isNSPConflict(namespace, prefix)) {
			prefix="ns"+nsc;
			nsc++;
		} 
		*/
		
		
		String oldPrefix=(String)namespace2prefix.get(namespace);
		namespace2prefix.put(namespace,prefix);
		if(oldPrefix!=null) prefix2namespace.remove(oldPrefix);
		prefix2namespace.put(prefix,namespace);
		
		if(!prefix.equals(oldPrefix)) {
			dirtyNsList.add(namespace);
			//updateViews();
		}
		updateNamespaceViews(namespace);
	}
	public void registerPrefix(String namespace, String prefix) {
		registerPrefixAsync(namespace,prefix);
		updateNamespaceViews(namespace);
	}
	
	/**
	 * @param prefix
	 * @return
	 */
	private boolean isNSPConflict(String namespace, String prefix) {
		if(prefix2namespace.get(prefix)==null) return false;
		else if(prefix2namespace.get(prefix).equals(namespace)) return false;
		else return true;
	}


	public void setActiveAsync(String namespace, boolean active) {
		if(active) activeNamespaces.add(namespace);
		else activeNamespaces.remove(namespace);
		
	}
	public void setActive(String namespace, boolean active) {
		setActiveAsync(namespace, active);
		updateNamespaceViews(namespace);
	}
	
	/**
	 * @param namespace String
	 * @param color Color object
	 */
	public void registerNameSpaceColorAsync(String namespace, Color color) {
		namespace2color.put(namespace,color);
		System.out.println("H");
		if(color2namespace.get(color)==null) {
			color2namespace.put(color,new HashSet());
		}
		((HashSet)(color2namespace.get(color))).add(namespace);
		
	}

	public void registerNameSpaceColor(String namespace, Color color) {
		registerNameSpaceColorAsync(namespace, color);
		updateNamespaceViews(namespace);
		
		
	}
	
	/**
	 * @param namespace String
	 * @param color Color String
	 */
	public void registerNameSpaceColor(String namespace, String colorString) {
		registerNameSpaceColor(namespace,DefaultSettings.translateString2Color(colorString));
	}
	
	
	public boolean getActive(String namespace) {
		if(activeNamespaces.contains(namespace)) return true;
		else return false;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#initialize()
	 */
	public boolean initialize() {
		System.out.println("Forgetting everything");
		namespace2prefix=new Hashtable(50);
		prefix2namespace=new Hashtable(50);
		namespaces=new ArrayList(50);
		namespace2color=new Hashtable(50);
		color2namespace=new Hashtable(50);
		activeNamespaces=new HashSet(50);
		namespace2URIs=new Hashtable(50);
		uri2Namespace=new Hashtable(3000);
		uri2CyNode=new Hashtable(3000);
		cyNode2URI=new Hashtable(3000);
		bnode2CyNode=new Hashtable(1000);
		cyNode2BNode=new Hashtable(1000);
		literalNode2CyNode=new Hashtable(1000);
		cyNode2LiteralNode=new Hashtable(1000);
		datatypeURI2Literals=new Hashtable(50);
		literal2DatatypeURI=new Hashtable(1000);
		uri2CyEdge=new Hashtable(5000);
		cyEdge2URI=new Hashtable(5000);
		label2URI=new Hashtable(3000);
		uri2Label=new Hashtable(3000);
		literalNode2Label=new Hashtable(1000);
		
		
		
		//handlerToRichResource=new Hashtable();
		//namespaceToRichResourcesList=new Hashtable();
		//CytoscapeIDToRichResource=new Hashtable();
		dirtyNsList=new HashSet();
		//dirtyCList=new HashSet();
		updateViews();
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.RDFScapeModule#touch()
	 */
	public void touch() {
		updateViews();
		
	}

	
	/** 
	 * 
	 */
	public boolean canOperate() {
		return true;
	}


	/**
	 * 
	 */
	public void reset() {
		initialize(); 	// Drastic approach
		
	}
	
	public String[] getNamespaces() {
		String[] result=new String[namespaces.size()];
		int i=0;
		for (Iterator iter = namespaces.iterator(); iter.hasNext();) {
			String namespace = (String) iter.next();
			result[i]=namespace;
			i++;
		}
		return result;
	}


	/**
	 * @param string
	 * @return
	 */
	public String getNamespacePrefix(String namespace) {
		return (String) namespace2prefix.get(namespace);
	}


	/**
	 * @param ns namespace string
	 * @return color associated to this namespace
	 */
	public Color getNamespaceColor(String ns) {
		Color rc=(Color) namespace2color.get(ns);
		if(rc!=null)return rc;
		else return DefaultSettings.defaultColor;
	}


	/**
	 * @param namespace number
	 * @return namespace string
	 */
	public String getNamespaceNumber(int x) {
		return (String)namespaces.get(x);
	}

	
	/**
	 * @param ns namespace string
	 * @return 
	 */
	public String getPrefixFromNs(String ns) {
		String nsr=(String)namespace2prefix.get(ns);
		if(nsr!=null) return nsr;
		else return "";
	}
	
	/**
	 * @param ns namespace string
	 * @return a Boolean with the activation status
	 */
	public Boolean getIsActiveFromNs(String ns) {
		if(activeNamespaces.contains(ns)) return new Boolean(true);
		else return new Boolean(false);
	}


	/**
	 * @param nameSpaceToDelete
	 */
	public void removeNamespace(String nameSpaceToDelete) {
		namespaces.remove(nameSpaceToDelete);
		
	}



	
	// TODO heavily change!
	/**
	 * @param myNameSpace
	 * @return
	 */
	public String getShortName(String longname) {
		if(longname.indexOf("^^",0)>=0) {
			int divisor=longname.indexOf("^^",0);
			return longname.substring(0,divisor)+" ("+getShortName(longname.substring(divisor+2,longname.length()))+")";
			
		}
		else {
			String prefix=null;
			String namespace=null;
			String name=null;
			//String temp=null;
			//namespace=getNameSpace(longname);
			//prefix=getNameSpacePrefix(namespace);
			name=longname.substring(namespace.length());
			if(prefix.trim().equals("")) return name;
			return prefix.concat(":").concat(name);
		}
	}


	/**
	 * @return
	 */
	public ArrayList getNamespacesList() {
		return namespaces;
	}


	/**
	 * @param manager
	 */
	public void addViewerElement(MemoryViewer viewer) {
		viewerList.add(viewer);
		
	}
	/**
	 * @param manager
	 */
	 public void updateViews() {
		for (Iterator iter = viewerList.iterator(); iter.hasNext();) {
			MemoryViewer viewer = (MemoryViewer) iter.next();
			viewer.updateView();
			
		}
		
	}
	 /**
		 * @param manager
		 */
		 private void updateNamespaceViews(String namespace) {
			for (Iterator iter = viewerList.iterator(); iter.hasNext();) {
				MemoryViewer viewer = (MemoryViewer) iter.next();
				viewer.updateNamespaceView(namespace);
				
			}
			
		}

	/**
	 * clears all information related to namespaces (initializes namespaces related data structures) 
	 */
	public void initNamespaces() {
		namespace2prefix=new Hashtable();
		prefix2namespace=new Hashtable();
		namespaces=new ArrayList();
		namespace2color=new Hashtable();
		color2namespace=new Hashtable();
		activeNamespaces=new HashSet();
		updateViews();
	}


	/**
	 * 
	 */
	public int relinkURIID() {
		// TODO we do nothing now...
		
		return 0;
	}


	public void registerURINamespace(String uri, String namespace) {
		// we may avoid the following step, since it is redoundant in the overall project
		System.out.println("Registering uri,namespace :"+uri+"->"+namespace);
		registerNameSpace(namespace);
		HashSet myURIList=null;
		if(namespace2URIs.get(namespace)==null) {
			namespace2URIs.put(namespace,new HashSet(800));
			System.out.println("NEW");
		}
		myURIList=(HashSet)namespace2URIs.get(namespace);
		myURIList.add(uri);
		uri2Namespace.put(uri,namespace);
	}


	public void registerURICytoNode(String uri, CyNode node) {
		// TODO Auto-generated method stub
		if(uri2CyNode.get(uri)==null) {
			uri2CyNode.put(uri,new HashSet());
			
		}
		HashSet myCyNodeList=(HashSet)uri2CyNode.get(uri);
		myCyNodeList.add(node);
		cyNode2URI.put(node,uri);
	}


	public CyNode[] getNamespaceCyNodes(String namespace) {
		CyNode[] nodeList=new CyNode[0];
		ArrayList tempNodeList=new ArrayList();
		HashSet uris=(HashSet)namespace2URIs.get(namespace);
		System.out.println("Geeting nodes for namespace: "+namespace);
		if(uris==null) return nodeList;
		System.out.println("\tNumber of URIs found "+uris.size());
		for (Iterator iter = uris.iterator(); iter.hasNext();) {
			String uri = (String) iter.next();
			//System.out.println("->"+uri+" ");
			HashSet CyNodes=(HashSet)uri2CyNode.get(uri);
			
			if(CyNodes!=null) {
				//System.out.println("has nodes");
				for (Iterator iterator = CyNodes.iterator(); iterator.hasNext();) {
					CyNode node = (CyNode) iterator.next();
					tempNodeList.add(node);
				
				}
			}
		}
		
		return (CyNode[]) tempNodeList.toArray(nodeList);
		
	}


	public void setNamespaceConditionInAnd(boolean b) {
		namespaceConditionInAnd=b;
		
	}


	public void setCollapseAttributes(boolean b) {
		collapseDataTypes=b;
		
	}


	public boolean isNamespaceConditionInAnd() {
		return namespaceConditionInAnd;
	}


	public boolean isCollapseAttributesTrue() {
		return collapseDataTypes;
	}


	public boolean cyNodeIsURI(CyNode cyNode) {
		if(cyNode2URI.get(cyNode)!=null) return true;
		else return false;
	}


	public boolean cyNodeIsBlank(CyNode cyNode) {
		if(cyNode2BNode.get(cyNode)!=null) return true;
		else return false;
	}


	public boolean cyNodeIsLiteral(CyNode cyNode) {
		if(cyNode2LiteralNode.get(cyNode)!=null) return true;
		else return false;
	}


	public String getURIFromCyNode(CyNode cyNode) {
		String answer=(String)cyNode2URI.get(cyNode);
		if(answer==null) return "";
		return answer;
	}


	public void setPropagateURI(boolean b) {
		propagateURI=true;
		
	}
	public boolean getPropagateURI() {
		return propagateURI;
	}


	public void registerBNodeCyNode(Object bnode, CyNode cynode) {
		HashSet myList=(HashSet)bnode2CyNode.get(bnode);
		if(myList==null) {
			myList=new HashSet();
			bnode2CyNode.put(bnode,myList);
		}
		myList.add(cynode);
		cyNode2BNode.put(cynode,bnode);
		
	}


	public void registerLiteralCyNode(Object literalNode, CyNode cynode) {
		HashSet myList=(HashSet)literalNode2CyNode.get(literalNode);
		if(myList==null) {
			myList=new HashSet();
			literalNode2CyNode.put(literalNode,myList);
		}
		myList.add(cynode);
		cyNode2LiteralNode.put(cynode,literalNode);
		
	}

	public void registerDatatypeURI2Literal(String namespace, Object literalNode) {
		HashSet myLiterals=(HashSet) datatypeURI2Literals.get(namespace);
		if(myLiterals==null) {
			myLiterals=new HashSet();
			datatypeURI2Literals.put(namespace,myLiterals);
		}
		myLiterals.add(literalNode);
		literal2DatatypeURI.put(literalNode,namespace);
	}
	
	public String getDatatypeURIFromLiteralNode(Object node) {
		return (String)literal2DatatypeURI.get(node);
		
	}
	public CyNode[] getCyNodesForLiteralDatatypeURI(String datatype) {
		CyNode[] nodeList=new CyNode[0];
		ArrayList nodeArray=new ArrayList();
		HashSet myLiterals=(HashSet) datatypeURI2Literals.get(datatype);
		if(myLiterals==null) return nodeList;
		for (Iterator iter = myLiterals.iterator(); iter.hasNext();) {
			Object literal = (Object) iter.next();
			CyNode myNode= (CyNode) literalNode2CyNode.get(literal);
			if(myNode!=null) nodeArray.add(myNode);
			
		}
		return (CyNode[]) nodeArray.toArray(nodeList);
		
	}


	public Object getBNodeForCyNode(CyNode cyNode) {
		return cyNode2BNode.get(cyNode);
	}


	public CyNode[] getCyNodesForLiteralDatatypeURINamespace(String namespace) {
		String[] literalURIs=new String[0];
		literalURIs=(String[])datatypeURI2Literals.keySet().toArray(literalURIs);
		
		CyNode[] nodeList=new CyNode[0];
		ArrayList nodeArray=new ArrayList();
		
		for (int i = 0; i < literalURIs.length; i++) {
			if(literalURIs[i].indexOf(namespace)>=0) {
				//This URI starts with the right namespace
				HashSet myLiterals=(HashSet) datatypeURI2Literals.get(literalURIs[i]);
				if(myLiterals!=null) {
					for (Iterator iter = myLiterals.iterator(); iter.hasNext();) {
						Object literal = (Object) iter.next();
						System.out.println("A");
						HashSet myNodeSet= (HashSet) literalNode2CyNode.get(literal);
						if(myNodeSet!=null) {
							Iterator elements=myNodeSet.iterator();
							while(elements.hasNext()) {
								nodeArray.add((CyNode)elements.next());
							}
						}
						
					}
				}
			}
		}
		return (CyNode[]) nodeArray.toArray(nodeList);
	}


	public void registerURICytoEdge(String uri, CyEdge edge) {
		if(uri2CyEdge.get(uri)==null) {
			uri2CyEdge.put(uri,new HashSet());
			
		}
		HashSet myCyEdgeList=(HashSet)uri2CyEdge.get(uri);
		myCyEdgeList.add(edge);
		cyEdge2URI.put(edge,uri);
		
		
		
	}
	
	public String getURIFromCyEdge(CyEdge edge) {
		return (String) cyEdge2URI.get(edge);
	}
	
	public CyEdge[] getNamespaceCyEdges(String namespace) {
		System.out.println("Retrieving edges for namespace: "+namespace);
		CyEdge[] edgeList=new CyEdge[0];
		ArrayList tempEdgeList=new ArrayList();
		HashSet uris=(HashSet)namespace2URIs.get(namespace);
		if(uris==null) return edgeList;
		System.out.println("\tFound "+uris.size()+" uri for this namespace");
		for (Iterator iter = uris.iterator(); iter.hasNext();) {
			String uri = (String) iter.next();
			System.out.println("Considering uri:"+uri);
			HashSet cyEdges=(HashSet)uri2CyEdge.get(uri);
			
			if(cyEdges!=null) {
				System.out.println("Edges associated to this uri: "+cyEdges.size());
				for (Iterator iterator = cyEdges.iterator(); iterator.hasNext();) {
					CyEdge edge = (CyEdge) iterator.next();
					tempEdgeList.add(edge);
				
				}
			}
		}
		//System.out.println(3);
		return (CyEdge[]) tempEdgeList.toArray(edgeList);
		
	}


	public Object getLiteralNodeForCyNode(CyNode cyNode) {
		return cyNode2LiteralNode.get(cyNode);
	}
	
	public CyNode[] getCyNodeForLiteralNode(Object node) {
		CyNode[] myNodeList=new CyNode[0];
		HashSet nodes=(HashSet)literalNode2CyNode.get(node);
		return (CyNode[]) nodes.toArray(myNodeList);
	}


	public String getLabelForURI(String uri) {
		String result=(String) uri2Label.get(uri);
		if(result!=null) return result;
		else return uri;
	}
	
	public void registerURILabel(String uri,String label) {
		label2URI.put(label,uri);
		uri2Label.put(uri,label);
	}
	public void registerLiteralNode2Label(Object node,String label) {
		literalNode2Label.put(node,label);
	}
	public String getLabelForLiteralNode(Object node) {
		return (String) literalNode2Label.get(node);
	}


	public String getNamespaceFromURI(String uri) {
		return JenaWrapper.getNamespaceFromURI(uri);
	}


	public CyNode[] getCyNodesForURI(String uri) {
		CyNode[] myNodeList=new CyNode[0];
		HashSet nodes=(HashSet)uri2CyNode.get(uri);
		if(nodes==null) return myNodeList;
		else return (CyNode[]) nodes.toArray(myNodeList);
	}
	
	public CyNode[] getCyNodesForBNode(Object node) {
		CyNode[] myNodeList=new CyNode[0];
		HashSet nodes=(HashSet)bnode2CyNode.get(node);
		if(nodes==null) return myNodeList;
		else return (CyNode[]) nodes.toArray(myNodeList);
	}
	public CyNode[] getCyNodesForLiteralNode(Object node) {
		CyNode[] myNodeList=new CyNode[0];
		HashSet nodes=(HashSet)literalNode2CyNode.get(node);
		if(nodes==null) return myNodeList;
		else return (CyNode[]) nodes.toArray(myNodeList);
	}


	public CyEdge[] getCyEdgesFromURI(String uri) {
		CyEdge[] myEdgeList=new CyEdge[0];
		HashSet edges=(HashSet) uri2CyEdge.get(uri);
		if(edges==null) return myEdgeList;
		else return (CyEdge[]) edges.toArray(myEdgeList);
	}


	public String[] getURIs() {
		String[] uris=new String[0];
		return (String[])uri2Label.keySet().toArray(uris);
		
	}
	
	public void relink() {
		// TODO Auto-generated method stub
		
	}

	/*
	public void registerURINamespace(String uri) {
		Resource res=ResourceFactory.createResource(uri);
		registerURINamespace(uri,res.getNameSpace());
		
	}
	*/
	
}
