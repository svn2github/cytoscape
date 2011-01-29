package org.cytoscape.io.internal.read.xgmml.handler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.xml.sax.Attributes;

public class ReadDataManager {

	protected final static String XLINK = "http://www.w3.org/1999/xlink";
	protected static final double documentVersion = 1.0;

	protected String networkName;

	/* RDF Data */
	protected String RDFDate = null;
	protected String RDFTitle = null;
	protected String RDFIdentifier = null;
	protected String RDFDescription = null;
	protected String RDFSource = null;
	protected String RDFType = null;
	protected String RDFFormat = null;

	/* Internal lists of the created nodes and edges */
	protected List<CyNode> nodeList = null;
	protected List<CyEdge> edgeList = null;
	/* Map of Groups to lists of node references that haven't been processed */
	protected HashMap<CyNode, List<String>> nodeLinks = null;
	/* Map of XML ID's to nodes */
	protected HashMap<String, CyNode> idMap = null;

	// Groups might actually recurse on us, so we need to
	// maintain a stack
	protected Stack<CyNode> groupStack = null;

	protected CyNode currentNode = null;
	protected CyEdge currentEdge = null;
	protected CyNode currentGroupNode = null;

	/* Attribute values */
	protected ParseState attState = ParseState.NONE;
	protected String currentAttributeID = null;
	protected CyRow currentAttributes = null;
	protected String objectTarget = null;

	/* Complex attribute data */
	protected int level = 0;
	protected int numKeys = 0;
	protected Map complexMap[] = null;
	protected Object complexKey[] = null;
	protected byte[] attributeDefinition = null;
	protected byte valueType;
	protected boolean complexAttributeDefined = false;
	// protected MultiHashMap mhm = null;

	/* Edge handle list */
	protected List<String> handleList = null;

	/* X handle */
	protected String edgeBendX = null;
	/* Y handle */
	protected String edgeBendY = null;

	protected List<Object> listAttrHolder;

	protected Map<String, Object> mapAttrHolder;

	protected CyNetwork network;

	/*
	 * The graph-global directedness, which will be used as default directedness
	 * of edges.
	 */
	protected boolean currentNetworkisDirected = true;

	protected Map<CyNode, Attributes> nodeGraphicsMap;
	protected Map<CyEdge, Attributes> edgeGraphicsMap;
	protected String backgroundColor;
	protected Map<CyNode, List<CyNode>> groupMap;

	public void initAllData() {
		networkName = null;
		backgroundColor = null;

		/* RDF Data */
		RDFDate = null;
		RDFTitle = null;
		RDFIdentifier = null;
		RDFDescription = null;
		RDFSource = null;
		RDFType = null;
		RDFFormat = null;

		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();

		nodeLinks = new HashMap<CyNode, List<String>>();
		idMap = new HashMap<String, CyNode>();
		groupMap = new HashMap<CyNode, List<CyNode>>();

		groupStack = new Stack<CyNode>();

		nodeGraphicsMap = new HashMap<CyNode, Attributes>();
		edgeGraphicsMap = new HashMap<CyEdge, Attributes>();

		currentNode = null;
		currentEdge = null;
		currentGroupNode = null;

		network = null;

		currentNetworkisDirected = true;

		attState = ParseState.NONE;
		currentAttributeID = null;
		currentAttributes = null;
		objectTarget = null;

		/* Complex attribute data */
		level = 0;
		numKeys = 0;
		complexMap = null;
		complexKey = null;
		attributeDefinition = null;
		valueType = (byte) 4;
		complexAttributeDefined = false;

		/* Edge handle list */
		handleList = null;

		edgeBendX = null;
		edgeBendY = null;
	}

	public ReadDataManager() {
		nodeGraphicsMap = new HashMap<CyNode, Attributes>();
		edgeGraphicsMap = new HashMap<CyEdge, Attributes>();
		groupMap = new HashMap<CyNode, List<CyNode>>();
		initAllData();
	}

	public String getNetworkName() {
		return networkName;
	}

	public Map<CyNode, Attributes> getNodeGraphics() {
		return nodeGraphicsMap;
	}

	public Map<CyEdge, Attributes> getEdgeGraphics() {
		return edgeGraphicsMap;
	}

	public Color getBackgroundColor() {
		if (backgroundColor == null)
			return null;
		return new Color(Integer.parseInt(backgroundColor.substring(1), 16));
	}

	public Map<CyNode, List<CyNode>> getGroupMap() {
		return groupMap;
	}
	
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}
	
	public CyNetwork getNetwork() {
		return this.network;
	}

}
