package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableEntry;
import org.xml.sax.Attributes;

public class ReadDataManager {

	protected final static String XLINK = "http://www.w3.org/1999/xlink";

	protected double documentVersion;
	protected String networkName;

	/* RDF Data */
	protected String RDFDate;
	protected String RDFTitle;
	protected String RDFIdentifier;
	protected String RDFDescription;
	protected String RDFSource;
	protected String RDFType;
	protected String RDFFormat;

	/* Internal lists of the created nodes and edges */
	protected List<CyNode> nodeList;
	protected List<CyEdge> edgeList;
	/* Map of Groups to lists of node references that haven't been processed */
	protected HashMap<CyNode, List<String>> nodeLinks;
	/* Map of XML ID's to nodes */
	protected HashMap<String, CyNode> idMap;

	/* Groups might actually recurse on us, so we need to maintain a stack */
	protected Stack<CyNode> groupStack;

	protected CyNode currentNode;
	protected CyEdge currentEdge;
	protected CyNode currentGroupNode;

	/* Attribute values */
	protected ParseState attState = ParseState.NONE;
	protected String currentAttributeID;
	protected CyRow currentAttributes;
	protected String objectTarget;

	/** Edge handle list */
	protected List<String> handleList;
	/** X handle */
	protected String edgeBendX;
	/** Y handle */
	protected String edgeBendY;

	protected List<Object> listAttrHolder;
	protected CyNetwork network;

	/**
	 * The graph-global directedness, which will be used as default directedness of edges.
	 */
	protected boolean currentNetworkIsDirected = true;

	private Map<String/*att name*/, String/*att value*/> networkAttributes;
	
	private Map<CyNode, Map<String/*att name*/, String/*att value*/>> nodeGraphics;
	private Map<CyEdge, Map<String/*att name*/, String/*att value*/>> edgeGraphics;

	protected Map<CyNode, List<CyNode>> groupMap;
	
	private boolean sessionFormat;

	public boolean isSessionFormat() {
		return sessionFormat;
	}
	
	public void setSessionFormat(boolean sessionFormat) {
		this.sessionFormat = sessionFormat;
	}

	public void initAllData() {
		networkName = null;

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

		networkAttributes = new LinkedHashMap<String, String>();
		nodeGraphics = new LinkedHashMap<CyNode, Map<String, String>>();
		edgeGraphics = new LinkedHashMap<CyEdge, Map<String, String>>();

		currentNode = null;
		currentEdge = null;
		currentGroupNode = null;

		network = null;

		currentNetworkIsDirected = true;

		attState = ParseState.NONE;
		currentAttributeID = null;
		currentAttributes = null;
		objectTarget = null;

		/* Edge handle list */
		handleList = null;

		edgeBendX = null;
		edgeBendY = null;
		
		sessionFormat = false;
	}

	public ReadDataManager() {
		initAllData();
	}

	public String getNetworkName() {
		return networkName;
	}

	public Set<CyNode> getNodes() {
		return nodeGraphics.keySet();
	}

	public Set<CyEdge> getEdges() {
		return edgeGraphics.keySet();
	}

	/**
	 * @param <T> CyNode or CyEdge
	 * @param element A CyNode or CyEdge
	 * @param attName The name of the attribute
	 * @param attValue The value of the attribute
	 */
	@SuppressWarnings("unchecked")
	public <T extends CyTableEntry> void addGraphicsAttribute(T element, String attName, String attValue) {
		if (!ignoreGraphicsAttribute(element, attName)) {
			Map<T, Map<String, String>> graphics = null;

			if (element instanceof CyNode) graphics = (Map<T, Map<String, String>>) nodeGraphics;
			if (element instanceof CyEdge) graphics = (Map<T, Map<String, String>>) edgeGraphics;

			Map<String, String> attributes = graphics.get(element);

			if (attributes == null) {
				attributes = new HashMap<String, String>();
				graphics.put(element, attributes);
			}

			attributes.put(attName, attValue);
		}
	}
	
	public void addNetworkGraphicsAttribute(String attName, String attValue) {
		if (attName != null) {
			networkAttributes.put(attName, attValue);
		}
	}

	public void addGraphicsAttributes(CyTableEntry element, Attributes atts) {
		if (element != null) {
			final int attrLength = atts.getLength();

			for (int i = 0; i < attrLength; i++) {
				if (element instanceof CyNode)
					addGraphicsAttribute((CyNode) element, atts.getLocalName(i), atts.getValue(i));
				else if (element instanceof CyEdge)
					addGraphicsAttribute((CyEdge) element, atts.getLocalName(i), atts.getValue(i));
			}
		}
	}

	public <T extends CyTableEntry> Map<String, String> getGraphicsAttributes(T element) {
		if (element instanceof CyNetwork) return networkAttributes;
		if (element instanceof CyNode) return nodeGraphics.get(element);
		if (element instanceof CyEdge) return edgeGraphics.get(element);

		return null;
	}

	public Map<CyNode, Map<String, String>> getNodeGraphics() {
		return nodeGraphics;
	}

	public Map<CyEdge, Map<String, String>> getEdgeGraphics() {
		return edgeGraphics;
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

	public void addEquation(String columnName, CyRow row, String equationStr) {
		// TODO: should just store all the equation strings per columnName/row?
		// TODO: create all equations after all CyTables/CyColumns are loaded?
	}
	
	/**
	 * It controls which graphics attributes should be parsed.
	 * @param element
	 * @param attName
	 * @return
	 */
	protected boolean ignoreGraphicsAttribute(final CyTableEntry element, String attName) {
		boolean b = false;
		
		// When reading XGMML as part of a CYS file, these graphics attributes should not be parsed.
		if (sessionFormat && element != null && attName != null) {
			// Network
			b = b || (element instanceof CyNetwork && attName.matches("backgroundColor"));
			// Nodes or Edges (these are standard XGMML and 2.x <graphics> attributes, not 3.0 bypass properties)
			b = b ||
				((element instanceof CyNode || element instanceof CyEdge) && attName
						.matches("type|fill|w|h|size|width|outline|"
								 + "(cy:)?((node|edge)Transparency|(node|edge)LabelFont|(border|edge)LineType)|"
								 + "(cy:)?(source|target)Arrow(Color)?"));
		}

		return b;
	}
}
