/*
 File: XGMMLParser.java

 Copyright (c) 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies
 - University of California San Francisco

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package org.cytoscape.io.read.internal.xgmml; 

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

interface Handler {
	public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException;
}

enum ParseState {
	NONE("none"), 
	RDF("RDF"), 
	NETATT("Network Attribute"), 
	NODEATT("Node Attribute"), 
	EDGEATT("Edge Attribute"), 
	// Types of attributes that require special handling
  LISTATT("List Attribute"), 
	MAPATT("Map Attribute"), 
	COMPLEXATT("Complex Attribute"), 
	NODEGRAPHICS("Node Graphics"), 
	EDGEGRAPHICS("Edge Graphics"),
	// Handle edge handles
	EDGEBEND("Edge Bend"), 
	EDGEHANDLE("Edge Handle"), 
	EDGEHANDLEATT("Edge Handle Attribute"), 
  NODE("Node Element"), 
	EDGE("Edge Element"), 
	GROUP("Group"), 
	GRAPH("Graph Element"), 
	RDFDESC("RDF Description"), 
	ANY("any");

	private String name;
	private ParseState(String str) { name = str; }
	public String toString() { return name; }
};

enum RDFTags {FORMAT,TYPE,DESC,DATE,SOURCE,IDENTIFIER};

enum ObjectType {
	NONE("none"), 
	STRING("string"), 
	BOOLEAN("boolean"), 
	REAL("real"), 
	INTEGER("integer"), 
	LIST("list"), 
	MAP("map"), 
	COMPLEX("complex");

	private String name;
	private ObjectType (String s) { name = s; }
	public String toString() { return name; }
};

class XGMMLParser extends DefaultHandler {
	final static String XLINK = "http://www.w3.org/1999/xlink";
	final static String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	final static String DUBLINCORE = "http://purl.org/dc/elements/1.1/";

	private ParseState parseState = ParseState.NONE;
	private Stack<ParseState>stateStack = null;
	private String networkName = null;
	private String currentCData = null;
	private String backgroundColor = null;
	private	double graphZoom = 1.0;
	private	double graphCenterX = 0.0;
	private	double graphCenterY = 0.0;

	//
	private double documentVersion = 1.0;

	/* RDF Data */
	private String RDFDate = null;
	private String RDFTitle = null;
	private String RDFIdentifier = null;
	private String RDFDescription = null;
	private String RDFSource = null;
	private String RDFType = null;
	private String RDFFormat = null;

	/* Internal lists of the created nodes and edges */
	private List<CyNode> nodeList = null;
	private List<CyEdge> edgeList = null;
	/* Map of Groups to lists of node references that haven't been processed */
	private HashMap<CyNode,List<String>> nodeLinks = null;
	/* Map of XML ID's to nodes */
	private HashMap<String, CyNode> idMap = null;
	/* Map of group nodes to children */
	private HashMap<CyNode,List<CyNode>> groupMap = null;

	// Groups might actually recurse on us, so we need to
	// maintain a stack
	private Stack<CyNode> groupStack = null;

	/* Map of nodes to graphics information */
	private HashMap<CyNode, Attributes> nodeGraphicsMap = null;
	/* Map of edges to graphics information */
	private HashMap<CyEdge, Attributes> edgeGraphicsMap = null;

	private CyNode currentNode = null;
	private CyEdge currentEdge = null;
	private CyNode currentGroupNode = null;

	private CyNetwork network = null;
	
	/*
	 * The graph-global directedness, which will be used as default directedness
	 * of edges. */
	private boolean currentNetworkisDirected = true; 
	
	/* Attribute values */
	private ParseState attState = ParseState.NONE;
	private String currentAttributeID = null;
	private CyRow currentAttributes = null;
	private	String objectTarget = null;

	/* Complex attribute data */
	private int level = 0;
	private int numKeys = 0;
	private Map complexMap[] = null;
	private Object complexKey[] = null;
	private byte[] attributeDefinition = null;
	private byte valueType; 
	private boolean complexAttributeDefined = false;

	/* Edge handle list */
	private List<String> handleList = null;

	/* X handle */
	private String edgeBendX = null;
	/* Y handle */
	private String edgeBendY = null;

	/**
	 * Main parse table. This table controls the state machine, and follows
	 * the standard format for a state machine:
	 * StartState, Tag, EndState, Method
	 */
	final Object[][] startParseTable = {
		// Initial state.  It's all noise until we see our <graph> tag
		{ParseState.NONE, "graph", ParseState.GRAPH, new handleGraph()},
		{ParseState.GRAPH, "att", ParseState.NETATT, new handleNetworkAttribute()},
		{ParseState.NETATT, "rdf", ParseState.RDF, null},
		// RDF tags -- most of the data for the RDF tags comes from the CData
		{ParseState.RDF, "description", ParseState.RDFDESC, new handleRDF()},
		{ParseState.RDFDESC, "type", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "description", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "identifier", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "date", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "title", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "source", ParseState.RDFDESC, null},
		{ParseState.RDFDESC, "format", ParseState.RDFDESC, null},
		// Handle our nodes
		{ParseState.GRAPH, "node", ParseState.NODE, new handleNode()},
		{ParseState.NODE, "graphics", ParseState.NODEGRAPHICS, new handleNodeGraphics()},
		{ParseState.NODE, "att", ParseState.NODEATT, new handleNodeAttribute()},
		{ParseState.NODEGRAPHICS, "att", ParseState.NODEGRAPHICS, new handleNodeGraphics()},
		// Hanlde possible group
		{ParseState.NODEATT, "graph", ParseState.GROUP, new handleGroup()},
		{ParseState.GROUP, "node", ParseState.NODE, new handleGroupNode()},
		{ParseState.GROUP, "edge", ParseState.EDGE, new handleEdge()},
		// Handle our edges
		{ParseState.GRAPH, "edge", ParseState.EDGE, new handleEdge()},
		{ParseState.EDGE, "att", ParseState.EDGEATT, new handleEdgeAttribute()},
		{ParseState.EDGE, "graphics", ParseState.EDGEGRAPHICS, new handleEdgeGraphics()},
		{ParseState.EDGEGRAPHICS, "att", ParseState.EDGEGRAPHICS, new handleEdgeGraphics()},
		{ParseState.EDGEBEND, "att", ParseState.EDGEHANDLE, new handleEdgeHandle()},
		{ParseState.EDGEHANDLE, "att", ParseState.EDGEHANDLE, new handleEdgeHandle()},
		{ParseState.LISTATT, "att", ParseState.LISTATT, new handleListAttribute()},
		{ParseState.MAPATT, "att", ParseState.MAPATT, new handleMapAttribute()},
		{ParseState.COMPLEXATT, "att", ParseState.COMPLEXATT, new handleComplexAttribute()},
	};

	/**
	 * End tag parse table. This table handles calling methods on end tags
	 * under those circumstances where the CData is used, or when it is
	 * important to take some sort of post-action (e.g. associating nodes to
	 * groups)
	 */
	final Object[][] endParseTable = {
		{ParseState.RDFDESC, "type", ParseState.RDFDESC, new handleRDFType()},
		{ParseState.RDFDESC, "description", ParseState.RDFDESC, new handleRDFDescription()},
		{ParseState.RDFDESC, "identifier", ParseState.RDFDESC, new handleRDFIdentifier()},
		{ParseState.RDFDESC, "date", ParseState.RDFDESC, new handleRDFDate()},
		{ParseState.RDFDESC, "title", ParseState.RDFDESC, new handleRDFTitle()},
		{ParseState.RDFDESC, "source", ParseState.RDFDESC, new handleRDFSource()},
		{ParseState.RDFDESC, "format", ParseState.RDFDESC, new handleRDFFormat()},
		// Special handling for group completion
		{ParseState.GROUP, "graph", ParseState.NONE, new handleGroupDone()},
		// Special handling for edge handles
		{ParseState.EDGEHANDLE, "att", ParseState.EDGEBEND, new handleEdgeHandleDone()},
		{ParseState.EDGEBEND, "att", ParseState.EDGEBEND, new handleEdgeHandleList()},
		// Special handling for complex attributes
		{ParseState.COMPLEXATT, "att", ParseState.COMPLEXATT, new handleComplexAttributeDone()},
		{ParseState.GRAPH, "graph", ParseState.NONE, new handleGraphDone()},
	};

	/********************************************************************
	 * Routines to handle attributes
	 *******************************************************************/

	/**
	 * Return the string attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.  In particular, this routine looks
	 * for an attribute with a <b>name</b> or <b>label</b> of <i>key</i> and 
	 * returns the <b>value</b> of that attribute.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static String getAttributeValue(Attributes atts, String key) {
		String name = atts.getValue("name");
		if (name == null)
			name = atts.getValue("label");

		if (name != null && name.equals(key))
			return atts.getValue("value");

		return null;
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.  In particular, this routine looks
	 * for an attribute with a <b>name</b> or <b>label</b> of <i>key</i> and 
	 * returns the <b>value</b> of that attribute.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static double getDoubleAttributeValue(Attributes atts, String key) {
		String attribute = getAttributeValue(atts, key);
		if (attribute == null)
			return 0.0;
		return (new Double(attribute)).doubleValue();
	}

	/**
	 * Return the Color attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.  In particular, this routine looks
	 * for an attribute with a <b>name</b> or <b>label</b> of <i>key</i> and 
	 * returns the <b>value</b> of that attribute.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static Color getColorAttributeValue(Attributes atts, String key) {
		String attribute = getAttributeValue(atts, key);
		if (attribute == null)
			return null;
		return new Color(Integer.parseInt(attribute.substring(1), 16));
	}

	/**
	 * Return the typed attribute value for the passed attribute.  In this case,
	 * the caller has already determined that this is the correct attribute and
	 * we just lookup the value.  This routine is responsible for type conversion
	 * consistent with the passed argument.
	 *
	 * @param type the ObjectType of the value
	 * @param atts the attributes
	 * @return the value of the attribute in the appropriate type
	 */
	static Object getTypedAttributeValue(ObjectType type, Attributes atts) {
		String value = atts.getValue("value");
		return getTypedValue(type, value);
	}

	/**
	 * Return the typed value for the passed value.
	 *
	 * @param type the ObjectType of the value
	 * @param value the value to type
	 * @return the typed value
	 */
	static Object getTypedValue(ObjectType type, String value) {
		switch(type) {
		case BOOLEAN:
			if (value != null)
				return new Boolean(value);
			break;
		case REAL:
			if (value != null)
				return new Double(value);
			break;
		case INTEGER:
			if (value != null)
				return Integer.valueOf(value);
			break;
		case STRING:
			if (value != null) {
 				// Make sure we convert our newlines and tabs back
 				String sAttr = value.replace("\\t","\t");
 				sAttr = sAttr.replace("\\n","\n");
 				return sAttr;
			}
			break;
		case LIST:
			return new ArrayList();
		case MAP:
			return new HashMap();
		// case COMPLEX:
		}
		return null;
	}

	/**
	 * Return the attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static String getAttribute(Attributes atts, String key) {
		return atts.getValue(key);
	}

	/**
	 * Return the attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @param ns the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	static String getAttributeNS(Attributes atts, String key, String ns) {
		if (atts.getValue(ns, key) != null) {
			return atts.getValue(ns, key);
		} else {
			return atts.getValue(key);
		}
	}

	/**
	 * Return the integer attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static int getIntegerAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return 0;
		return (Integer.valueOf(attribute)).intValue();
	}

	/**
	 * Return the integer attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @param ns the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	static int getIntegerAttributeNS(Attributes atts, String key, String ns) {
		String attribute = atts.getValue(ns, key);
		if (attribute == null)
			return 0;
		return (Integer.valueOf(attribute)).intValue();
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static double getDoubleAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return 0.0;
		return (new Double(attribute)).doubleValue();
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @param ns the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	static double getDoubleAttributeNS(Attributes atts, String key, String ns) {
		String attribute = atts.getValue(ns, key);
		if (attribute == null)
			return 0;
		return (new Double(attribute)).doubleValue();
	}

	/**
	 * Return the Color attribute value for the attribute indicated by "key".  If
	 * no such attribute exists, return null.
	 *
	 * @param atts the attributes
	 * @param key the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	static Color getColorAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return null;
		return new Color(Integer.parseInt(attribute.substring(1), 16));
	}


	/**
	 * Main constructor for our parser.  Initialize any local arrays.  Note that this
	 * parser is designed to be as memory efficient as possible.  As a result, a minimum
	 * number of local data structures are created.
	 */
	XGMMLParser(CyNetwork network) {
		this.network = network;
		stateStack = new Stack<ParseState>();
		groupStack = new Stack<CyNode>();
		nodeList = new ArrayList<CyNode>();
		edgeList = new ArrayList<CyEdge>();
		nodeLinks = new HashMap<CyNode,List<String>>();
		nodeGraphicsMap = new HashMap<CyNode,Attributes>();
		edgeGraphicsMap = new HashMap<CyEdge,Attributes>();
		idMap = new HashMap<String, CyNode>();
	}

	/********************************************************************
	 * Interface routines.  These routines are called by the XGMMLReader
	 * to get the resulting data.
	 *******************************************************************/


	String getNetworkName() {
		return networkName;
	}

	HashMap<CyNode, Attributes> getNodeGraphics() {
		return nodeGraphicsMap;
	}

	HashMap<CyEdge, Attributes> getEdgeGraphics() {
		return edgeGraphicsMap;
	}

	Color getBackgroundColor() {
		if (backgroundColor == null) 
			return null;
		return new Color(Integer.parseInt(backgroundColor.substring(1), 16));
	}

	double getGraphViewZoomLevel() {
		return graphZoom;
	}

	Point2D getGraphViewCenter() {
		if (graphCenterX == 0.0 && graphCenterY == 0.0) 
			return null;
		return new Point2D.Double(graphCenterX, graphCenterY);
	}

	HashMap<CyNode, List<CyNode>> getGroupMap() {
		return groupMap;
	}

	void setMetaData(CyNetwork network) {
		MetadataParser mdp = new MetadataParser(network);
		if (RDFType != null)
			mdp.setMetadata(MetadataEntries.TYPE, RDFType);
		if (RDFDate != null)
			mdp.setMetadata(MetadataEntries.DATE, RDFDate);
		if (RDFTitle != null)
			mdp.setMetadata(MetadataEntries.TITLE, RDFTitle);
		if (RDFDescription != null)
			mdp.setMetadata(MetadataEntries.DESCRIPTION, RDFDescription);
		if (RDFSource != null)
			mdp.setMetadata(MetadataEntries.SOURCE, RDFSource);
		if (RDFFormat != null)
			mdp.setMetadata(MetadataEntries.FORMAT, RDFFormat);
		if (RDFIdentifier != null)
			mdp.setMetadata(MetadataEntries.IDENTIFIER, RDFIdentifier);
	}

	/********************************************************************
	 * Handler routines.  The following routines are called directly from
	 * the SAX parser.
	 *******************************************************************/

	/**
	 * startElement is called whenever the SAX parser sees a start tag.  We
	 * use this as the way to fire our state table.
	 *
	 * @param namespace the URL of the namespace (full spec)
	 * @param localName the tag itself, stripped of all namespace stuff
	 * @param qName the tag with the namespace prefix
	 * @param atts the Attributes list from the tag
	 */
   public void startElement(String namespace, 
                           String localName, 
                           String qName,
                           Attributes atts) throws SAXException {

			/*
			System.out.print("startElement("+namespace+", "+localName+", "+qName+", ");
			for (int i = 0; i < atts.getLength(); i++) {
				System.out.print(atts.getQName(i)+"="+atts.getValue(i)+" ");
			}
			System.out.println(") State: "+printState(parseState));
			*/

			ParseState nextState = handleState(startParseTable, parseState, localName, atts);
		  // System.out.println("Next state: "+printState(nextState));

			stateStack.push(parseState);
			parseState = nextState;
   }

	/**
	 * endElement is called whenever the SAX parser sees an end tag.  We
	 * use this as the way to fire our state table.
	 *
	 * @param uri the URL of the namespace (full spec)
	 * @param localName the tag itself, stripped of all namespace stuff
	 * @param qName the tag with the namespace prefix
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {
			// System.out.println("endElement("+uri+", "+localName+", "+qName+") State: "+printState(parseState));

			handleState(endParseTable, parseState, localName, null);

			parseState = stateStack.pop();
	}

	/**
	 * characters is called to handle CData
	 *
	 * @param ch the character data
	 * @param start the start of the data for this tag
	 * @param length the number of bytes for this tag
	 */
	public void characters(char[] ch, int start, int length) {
			currentCData = new String(ch, start, length);
	}

	/**
	 * fatalError -- handle a fatal parsing error
	 *
	 * @param e the exception that generated the error
	 */
	public void fatalError(SAXParseException e) throws SAXException {
		String err = "Fatal parsing error on line "+e.getLineNumber()+" -- '"+e.getMessage()+"'";
		throw new SAXException(err);
	}

	/**
	 * error -- handle a parsing error
	 *
	 * @param e the exception that generated the error
	 */
	public void error(SAXParseException e) {
		System.err.println("Parsing error on line "+e.getLineNumber()+" -- '"+e.getMessage()+"'.  Attempting to recover");
	}
   
	/********************************************************************
	 * Private parser routines.  The following routines are used to 
	 * manage the state data.
	 *******************************************************************/

	/**
	 * handleState takes as input a state table, the current state, and
	 * the tag.  It then looks for a match in the state table of current
	 * state and tag, then it calls the appropriate handler.
	 *
	 * @param table the state table to use
	 * @param currentState the current state
	 * @param tag the element tag
	 * @param atts the Attributes associated with this tag.  These will
	 *             be passed to the handler
	 * @return the new state
	 */
	private ParseState handleState(Object[][]table, ParseState currentState, 
	                               String tag, Attributes atts) throws SAXException {
		// If our element table ever gets long, we may want to make
		// this more efficient with a hash or something
		for (int i = 0; i < table.length; i++) {
			if ((table[i][0] == currentState) && tag.equals(table[i][1])) {
				Handler handler = (Handler)table[i][3];
				if (handler != null)
					return handler.handle(tag, atts, (ParseState)table[i][2]);
				return (ParseState)table[i][2];
			}
		}
		return currentState; // Throw an exception?
	}


	/********************************************************************
	 * Element handling routines.  The following routines are the methods
	 * called by the state mechine.
	 *******************************************************************/

	class handleGraph implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			String name = getLabel(atts);
			if (name != null) 
				networkName = name;
			
			/* 
			 * Read and store the graph-global default directedness of edges.
			 * Note that XGMML gives the default value of this as false (i.e.
			 * undirected is the default) but because older (pre-cy3.0) cytoscape used
			 * directed edges by default, and because compatibility with older cytoscape
			 * versions is more important than compatibility with XGMML standard, using
			 * directed edges by default makes more sense.
			 */
			String directed = atts.getValue("directed");
			if ("0".equals(directed)){
				currentNetworkisDirected = false;
			} else {
				currentNetworkisDirected = true;
			}
						
			return current;
		}
	}

	/**
	 * handleGraphDone is called when we finish parsing the entire XGMML file.  This allows us to do
	 * deal with some cleanup line creating all of our groups, etc.
	 */
	class handleGraphDone implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			// Resolve any unresolve node references
			if (nodeLinks != null) {
				for (CyNode groupNode : nodeLinks.keySet()) {
					if (!groupMap.containsKey(groupNode)) {
						groupMap.put(groupNode, new ArrayList<CyNode>());
					}
					List<CyNode>groupList = groupMap.get(groupNode);
					for (String ref: nodeLinks.get(groupNode)) {
						if (idMap.containsKey(ref)) {
							groupList.add(idMap.get(ref));
						}
					}
				}
			}
			return current;
		}
	}

	class handleNetworkAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			attState = current;
			ParseState nextState = current;
			// Look for "special" network attributes
			if (getAttributeValue(atts, "backgroundColor") != null) {
				backgroundColor = getAttributeValue(atts, "backgroundColor");
			} else if (getAttributeValue(atts, "documentVersion") != null) {
				documentVersion = getDoubleAttributeValue(atts, "documentVersion");
			} else if (getAttributeValue(atts, "GRAPH_VIEW_ZOOM") != null) {
				graphZoom = getDoubleAttributeValue(atts, "GRAPH_VIEW_ZOOM");
			} else if (getAttributeValue(atts, "GRAPH_VIEW_CENTER_X") != null) {
				graphCenterX = getDoubleAttributeValue(atts, "GRAPH_VIEW_CENTER_X");
			} else if (getAttributeValue(atts, "GRAPH_VIEW_CENTER_Y") != null) {
				graphCenterY = getDoubleAttributeValue(atts, "GRAPH_VIEW_CENTER_Y");
			} else {
				objectTarget = networkName;
				currentAttributes = network.attrs();
				nextState = handleAttribute(atts, currentAttributes);
			}

			// System.out.println("Network attribute: "+printAttribute(atts));
			if (nextState != ParseState.NONE)
				return nextState;
			
			return current;
		}
	}

	class handleNode implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			String id = atts.getValue("id");
			String label = atts.getValue("label");
			if (label == null)
				label = atts.getValue("name");    // For backwards compatibility
			String href = atts.getValue(XLINK, "href");

			if (href != null) {
				// System.out.print(" href=\""+href+"\"");
				throw new SAXException("Can't have a node reference outside of a group");
			}
			// Create the node
			currentNode = createUniqueNode(label, id);

			return current;
		}
	}

	class handleNodeAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (atts == null)
				return current;

			attState = current;
			// System.out.println("Node attribute: "+printAttribute(atts));
			// Is this a graphics override?
			String name = atts.getValue("name");

			// Check for blank attribute (e.g. surrounding a group)
			if (name == null && atts.getValue("value") == null)
				return current;

			if (name.startsWith("node.")) {
				// Yes, add it to our nodeGraphicsMap
				name = atts.getValue("name").substring(5);
				String value = atts.getValue("value");
				if (!nodeGraphicsMap.containsKey(currentNode)) {
					nodeGraphicsMap.put(currentNode, new AttributesImpl());
				} 
				((AttributesImpl)nodeGraphicsMap.get(currentNode)).addAttribute("", "", name, "string", value);
			}
			
			currentAttributes = currentNode.attrs();
			ParseState nextState = handleAttribute(atts, currentAttributes);
			if (nextState != ParseState.NONE)
				return nextState;
			return current;
		}
	}

	/**
	 * handleNodeGraphics builds the objects that will remember the node graphic
	 * information until we do the actual layout.  Unfortunately, the way the
	 * readers work, we can't apply the graphics information until we do the
	 * actual layout.
	 */
	class handleNodeGraphics implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (tag.equals("graphics")) {
				if (nodeGraphicsMap.containsKey(currentNode)) {
					addAttributes(nodeGraphicsMap.get(currentNode),atts);
				} else {
					nodeGraphicsMap.put(currentNode, new AttributesImpl(atts));
				}
			} else if (tag.equals("att")) {
				// Handle special node graphics attributes
				String name = atts.getValue("name");
				if (name != null
					&& !name.equals("cytoscapeNodeGraphicsAttributes")) {
					// Add this as a graphics attribute to the end of our list
					((AttributesImpl)nodeGraphicsMap.get(currentNode)).
						addAttribute("", "", "cy:"+name, "string",atts.getValue("value"));
				}
			}
			return current;
		}
	}

	class handleEdge implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			// Get the label, id, source and target
			String label = atts.getValue("label");
			String source = atts.getValue("source");
			String target = atts.getValue("target");
			String isDirected = atts.getValue("cy:directed");
			String sourceAlias = null;
			String targetAlias = null;
			String interaction = "";  // no longer users

			// Parse out the interaction (if this is from Cytoscape)
			// parts[0] = source alias
			// parts[1] = interaction
			// parts[2] = target alias
			String[] parts = label.split("[()]");
			if (parts.length == 3) {
				sourceAlias = parts[0];
				interaction = parts[1];
				targetAlias = parts[2];
				// System.out.println("Edge label parse: interaction = "+interaction);
			}

			boolean directed;
			if (isDirected == null){
				// xgmml files made by pre-3.0 cytoscape and strictly
				// upstream-XGMML conforming files
				// won't have directedness flag, in which case use the
				// graph-global directedness setting.
				//
				// (org.xml.sax.Attributes.getValue() returns null if attribute does not exists)
				//
				// This is the correct way to read the edge-directionality of
				// non-cytoscape xgmml files as well.
				directed = currentNetworkisDirected;
			} else { // parse directedness flag
				if ("0".equals(isDirected)){
					directed = false;
				} else {
					directed = true;
				} 
			}
			if (idMap.containsKey(source) && idMap.containsKey(target)) {
				CyNode sourceNode = idMap.get(source);
				CyNode targetNode = idMap.get(target);
				currentEdge = createEdge(sourceNode, targetNode, label, directed);
			} else if (sourceAlias != null && targetAlias != null) {
				CyNode sourceNode = idMap.get(sourceAlias);
				CyNode targetNode = idMap.get(targetAlias);
				currentEdge = createEdge(sourceNode, targetNode, label, directed);
			}
			
			return current;
		}
	}

	class handleEdgeAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {

			attState = current;
		
			// TODO what if currentEdge is null?
			currentAttributes = currentEdge.attrs();
			ParseState nextState = handleAttribute(atts, currentAttributes);
			if (nextState != ParseState.NONE)
				return nextState;
			return current;
		}
	}

	class handleEdgeGraphics implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			// System.out.println("Atts for "+currentEdge.getIdentifier()+": "+printAttributes(atts));
			if (tag.equals("graphics")) {
				if (edgeGraphicsMap.containsKey(currentEdge)) {
					addAttributes(edgeGraphicsMap.get(currentEdge),atts);
				} else
					edgeGraphicsMap.put(currentEdge, new AttributesImpl(atts));
			} else if (tag.equals("att")) {
				// Handle special edge graphics attributes
				String name = atts.getValue("name");
				if (name != null
					  && name.equals("edgeBend")) {
					handleList = new ArrayList<String>();
					return ParseState.EDGEBEND;
				} else if (name != null
					         && !name.equals("cytoscapeEdgeGraphicsAttributes")) {
					// Add this as a graphics attribute to the end of our list
					((AttributesImpl)edgeGraphicsMap.get(currentEdge)).
						addAttribute("", "", "cy:"+name, "string",atts.getValue("value"));
				}
			}
			return current;
		}
	}

	class handleGroup implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (groupMap == null) groupMap = new HashMap<CyNode, List<CyNode>>();
			if (currentGroupNode != null) groupStack.push(currentGroupNode);
			currentGroupNode = currentNode;
			groupMap.put(currentGroupNode, new ArrayList<CyNode>());
			// System.out.println("Found group: "+currentNode);
			return current;
		}
	}

	class handleGroupNode implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			String id = atts.getValue("id");
			String label = atts.getValue("label");
			String href = atts.getValue(XLINK, "href");


			// System.out.print("<node");
			if (href == null) {
				// Create the node
				currentNode = createUniqueNode(label,id);
			} else {
				id = href.substring(1);
			}

			// Note that we don't want to add the node to the group until later,
			// even if we know about it now, so that we can get the edges associated
			// with the group when we add it.

			// System.out.print(" href=\""+href+"\"");
			// Add it to the list of nodes to be resolved for this group
			if (currentGroupNode == null)
				throw new SAXException("No group to add node reference to");

			if (idMap.containsKey(id)) {
				CyNode node = idMap.get(id);
				List<CyNode>nodeList = groupMap.get(currentGroupNode);
				nodeList.add(node);
			} else {
				// Remember it for later -- we'll fix this up in handleGraphDone
				if (!nodeLinks.containsKey(currentGroupNode)) {
					nodeLinks.put(currentGroupNode, new ArrayList<String>());
				}

				List<String> links = nodeLinks.get(currentGroupNode);
				links.add(id);
			}
			// System.out.println(">");
			return current;
		}
	}

	/**
	 * Routines to handle edge bends.  There are two different formats for edge bends.  The original
	 * XGMML code encoded edge bends as:
   * <att name="edgeBend">
   *    <att name="handle">
   *       <att value="15277.6748046875" name="x"/>
   *       <att value="17113.919921875" name="y"/>
   *   </att>
   *    <att name="handle">
   *       <att value="15277.6748046875" name="x"/>
   *       <att value="17113.919921875" name="y"/>
   *   </att>
   * </att>
	 *
	 * In version 1.1, which was simplified to:
   * <att name="edgeBend">
   *    <att name="handle" x="15277.6748046875" y="17113.919921875" />
   *    <att name="handle" x="15277.6748046875" y="17113.919921875" />
   * </att>
	 */

	/**
	 * Handle the "handle" attribute.  If this is an original format XGMML file (1.0) we just
	 * punt to the next level down.  If this is a newer format file, we handle the attributes
	 * directly.
	 */
	class handleEdgeHandle implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {

			if (documentVersion == 1.0) {
				// This is the outer "handle" attribute
				if (!getAttribute(atts, "name").equals("handle")) {
					// OK, this is one of our "data" attributes
					if (getAttributeValue(atts, "x") != null) {
						edgeBendX = getAttributeValue(atts, "x");
					} else if (getAttributeValue(atts, "y") != null) {
						edgeBendY = getAttributeValue(atts, "y");
					} else {
						throw new SAXException("expected x or y value for edgeBend handle - got "+atts.getValue("name"));
					}
				}
			} else {
				// New format -- get the x and y values directly
				if (getAttribute(atts, "x") != null) {
					edgeBendX = getAttribute(atts, "x");
				} 
				if (getAttribute(atts, "y") != null) {
					edgeBendY = getAttribute(atts, "y");
				}
				// System.out.println("x="+edgeBendX+" y="+edgeBendY);
				if (edgeBendX != null && edgeBendY != null) {
					if (handleList == null) handleList = new ArrayList<String>();
					handleList.add(edgeBendX+","+edgeBendY);
					edgeBendX = null;
					edgeBendY = null;
				}
			}
			return current;
		}
	}

	class handleEdgeHandleDone implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (edgeBendX != null && edgeBendY != null && handleList != null) {
				handleList.add(edgeBendX+","+edgeBendY);
				edgeBendX = null;
				edgeBendY = null;
			}
			return current;
		}
	}

	class handleEdgeHandleList implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (handleList != null) {
				String list = "";
				for (int i = 0; i < handleList.size(); i++) {
					if (i != (handleList.size()-1)) {
						list += handleList.get(i)+";";
					} else {
						list += handleList.get(i);
					}
				}
				// System.out.println("Setting edgeHandleList to: "+list);
				// Add this as a graphics attribute to the end of our list
				((AttributesImpl)edgeGraphicsMap.get(currentEdge)).
						addAttribute("", "", "edgeHandleList", "string", list);
				handleList = null;
			}
			return current;
		}
	}
	class handleGroupDone implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			currentNode = currentGroupNode;
			// System.out.println("Group "+currentNode+" done.");
			if (!groupStack.empty())
				currentGroupNode = groupStack.pop();
			else
				currentGroupNode = null;
			return current;
		}
	}

	class handleListAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			ObjectType objType = getType(atts.getValue("type"));
			Object obj = getTypedAttributeValue(objType, atts);

			List listAttribute = null;
			if (objectTarget != null) 
				listAttribute = currentAttributes.get(currentAttributeID,List.class);

			if (listAttribute == null) 
				listAttribute = new ArrayList();

			switch (objType) {
			case BOOLEAN:
			case REAL:
			case INTEGER:
			case STRING:
				listAttribute.add(obj);
			}
			if (objectTarget != null)
				currentAttributes.set(currentAttributeID, listAttribute);
			return current;
		}
	}

	class handleMapAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			String name = atts.getValue("name");
			ObjectType objType = getType(atts.getValue("type"));
			Object obj = getTypedAttributeValue(objType, atts);

			Map mapAttribute = null;
			if (objectTarget != null) 
				mapAttribute = currentAttributes.get(currentAttributeID,Map.class);
			if (mapAttribute == null) 
				mapAttribute = new HashMap();

			switch (objType) {
			case BOOLEAN:
			case REAL:
			case INTEGER:
			case STRING:
				mapAttribute.put(name, obj);
			}
			if (objectTarget != null)
				currentAttributes.set(currentAttributeID, mapAttribute);
			return current;
		}
	}

	/**
	 * handleComplexAttribute attempts to read an arbitrarily complex attribute
	 * map from the XGMML file.  For our purposes, a complex attribute map is defined
	 * as a HashMap which has as its values Maps.  For example, consider
	 * a pseudo hash with the following structure:
	 *
	 * {"externalref1"}->{"authors"}->{1}->"author1 name";
	 * {"externalref1"}->{"authors"}->{2}->"author2 name";
	 * {"externalref1"}->{"authors"}->{3}->"author3 name";
	 *
	 * where the keys externalref1 and authors are strings, and keys 1, 2, 3 are
	 * integers, and the values (author1 name, author2 name, author3 name) are
	 * strings, we would have the following attributes written to the xgmml
	 * file:
	 *
	 * <att type="complex" name="publication references" value="3"> 
	 *    <att type="string" name="externalref1" value="1"> 
	 *       <att type="string" name="authors" value="3"> 
	 *          <att type="int" name="2" value="1"> 
	 *             <att type="string" value="author2 name"/> 
	 *          </att> 
	 *          <att type="int" name="1" value="1"> 
	 *             <att type="string" value="author1 name"/> 
	 *          </att> 
	 *          <att type="int" name="3" value="1"> 
	 *             <att type="string" value="author3 name"/> 
	 *          </att> 
	 *       </att> 
	 *    </att> 
	 * </att>
	 *
	 * Notes: - value attribute property for keys is assigned the number of
	 * sub-elements the key references - value attribute property for values is
	 * equal to the value - name attribute property for attributes is only set
	 * for keys, and the value of this property is the key name. - label
	 * attribute property is equal to the data type of the key or value. - name
	 * attribute properties are only set for keys
	 */
	class handleComplexAttribute implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			// We can't create the complex attribute until we know what the definition is, but
			// since a complex attribute is really nothing more than a HashMap with a String
			// key and Map values, we can create it on the fly.

			// Get our attributes
			ObjectType type = getType(atts.getValue("type"));
			String value = atts.getValue("value");
			// System.out.println("Complex attribute: "+currentAttributeID+" level "+level+" value="+atts.getValue("value"));

			if (level == numKeys) {
				complexMap[level-1].put(complexKey[level-1], getTypedValue(type, value));
				valueType = getMultHashMapType(type);
				// See if we've defined the attribute already
				if (Map.class == currentAttributes.contains(currentAttributeID)) {
					currentAttributes.getDataTable().createColumn(currentAttributeID, Map.class,false);
				}
				// Now define set the attribute
				if (objectTarget != null)
					currentAttributes.set(currentAttributeID,getTypedValue(type,value)); //??
			} else if (level == 0) {
				if (complexMap[level] == null) {
					complexMap[level] = new HashMap();
				}
				complexKey[level] = getTypedValue(type, atts.getValue("name"));
				attributeDefinition[level] = getMultHashMapType(type);
			} else {
				if (complexMap[level] == null) {
					complexMap[level] = new HashMap();
				}
				complexMap[level-1].put(complexKey[level-1], complexMap[level]);
				complexKey[level] = getTypedValue(type, atts.getValue("name"));
				attributeDefinition[level] = getMultHashMapType(type);
			}
			level++;

			return current;
		}
	}

	class handleComplexAttributeDone implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			if (level == 0) {
				// We are done, and have read in all of our attributes
				// System.out.println("Complex attribute "+currentAttributeID+" ComplexMap["+level+"] = "+complexMap[level]);
			} else if (level < numKeys) {
				complexMap[level] = null;
				complexKey[level] = null;
			}
			// Decrement our depth
			level--;
			return current;
		}
	}

	/* 
	 * The following routines all handle the RDF data. All of them
	 * except handleRDF are called by the endElement handler.  At
	 * this point, while we carefully read in all of this metaData,
	 * it's not clear how it ever gets used...
	 */

	class handleRDF implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			return current;
		}
	}

	class handleRDFType implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFType = currentCData;
			return current;
		}
	}

	class handleRDFDescription implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFDescription = currentCData;
			return current;
		}
	}

	class handleRDFIdentifier implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFIdentifier = currentCData;
			return current;
		}
	}

	class handleRDFDate implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFDate = currentCData;
			return current;
		}
	}

	class handleRDFTitle implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFTitle = currentCData;
			return current;
		}
	}

	class handleRDFSource implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFSource = currentCData;
			return current;
		}
	}

	class handleRDFFormat implements Handler {
		public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
			RDFFormat = currentCData;
			return current;
		}
	}

	public static String printAttributes(Attributes atts) {
		String str = " ";
		for (int i = 0; i < atts.getLength(); i++) {
			str += atts.getLocalName(i)+": "+atts.getValue(i)+", ";
		}
		return str;
	}

	/********************************************************************
	 * Utility routines.  The following routines are utilities that are
	 * used for internal purposes.
	 *******************************************************************/

	private static String getLabel(Attributes att) {
		String label = att.getValue("label");
		if (label != null) 
			return label;

		return att.getValue("id");
	}

	private ParseState handleAttribute(Attributes atts, 
	                                   CyRow cyAtts) {
		String name = atts.getValue("name");
		ObjectType objType = getType(atts.getValue("type"));
		Object obj = getTypedAttributeValue(objType, atts);

		switch (objType) {
		case BOOLEAN:
			if (obj != null && name != null)
				cyAtts.set(name, (Boolean)obj);
			break;
		case REAL:
			if (obj != null && name != null)
				cyAtts.set(name, (Double)obj);
			break;
		case INTEGER:
			if (obj != null && name != null)
				cyAtts.set(name, (Integer)obj);
			break;
		case STRING:
			if (obj != null && name != null)
				cyAtts.set(name, (String)obj);
			break;

		// We need to be *very* careful.  Because we duplicate attributes for
		// each network we write out, we wind up reading and processing each
		// attribute multiple times, once for each network.  This isn't a problem
		// for "base" attributes, but is a significant problem for attributes
		// like LIST and MAP where we add to the attribute as we parse.  So, we
		// must make sure to clear out any existing values before we parse.
		case LIST:
			currentAttributeID = name;
			if (List.class == cyAtts.contains(name))
				cyAtts.set(name,null);
			return ParseState.LISTATT;
		case MAP:
			currentAttributeID = name;
			if (Map.class == cyAtts.contains(name))
				cyAtts.set(name,null);
			return ParseState.MAPATT;
		case COMPLEX:
			currentAttributeID = name;
			if (Map.class == cyAtts.contains(name)) // assuming complex will become Map
				cyAtts.set(name,null);
			// If this is a complex attribute, we know that the value attribute
			// is an integer
			numKeys = Integer.parseInt(atts.getValue("value"));
			complexMap = new HashMap[numKeys];
			complexKey = new Object[numKeys];
			attributeDefinition = new byte[numKeys];
			level = 0;
			return ParseState.COMPLEXATT;
		}
		return ParseState.NONE;
	}


	/**
	 * Given an ObjectType, method returns a MultiHashMapDefinition byte
	 * corresponding to its type.
	 *
	 * @param objectType - the type
	 *            
	 * @return - byte
	 */
	private byte getMultHashMapType(final ObjectType objectType) {
		switch(objectType) {
		case BOOLEAN:
			return (byte)1; 
		case STRING:
 			return (byte)4; 
		case INTEGER:
			return (byte)3; 
		case REAL:
			return (byte)2; 
		}

		// outta here
		return -1;
	}


	private CyNode createUniqueNode (String label, String id) throws SAXException {
		if (label != null) {
			if (id == null)
				id = label;
				// System.out.print(" label=\""+label+"\"");
		}
		// OK, now actually create it
		CyNode node = network.addNode();
		node.attrs().set("name",label);
		// System.out.println("Created new node("+label+") id="+node.getRootGraphIndex());

		// Add it our indices
		nodeList.add(node);
		// System.out.println("Adding node "+node.getIdentifier()+"("+id+") to map");
		idMap.put(id, node);
		return node;
	}

	private CyEdge createEdge (CyNode source, CyNode target,
                               String label, boolean directed) throws SAXException {
		// OK create it
		CyEdge edge = network.addEdge(source, target, directed);
		edge.attrs().set("name",label);

		edgeList.add(edge);
		return edge;
	}

	private void addAttributes(Attributes attI, Attributes atts) {
		String localName = null;
		String qName = null;
		String type = null;
		String uri = null;
		String value = null;
		for (int i = 0; i < atts.getLength(); i++) {
			localName = atts.getLocalName(i);
			uri = atts.getURI(i);
			value = atts.getValue(i);
			type = atts.getType(i);
			qName = atts.getQName(i);
			((AttributesImpl)attI).addAttribute(uri, localName, qName,type, value);
		}
	}

	private ObjectType getType(String type) {
		if (type == null)
			return ObjectType.STRING;
		else if (type.equals("string"))
			return ObjectType.STRING;
		else if (type.equals("boolean"))
			return ObjectType.BOOLEAN;
		else if (type.equals("real"))
			return ObjectType.REAL;
		else if (type.equals("integer"))
			return ObjectType.INTEGER;
		else if (type.equals("list"))
			return ObjectType.LIST;
		else if (type.equals("map"))
			return ObjectType.MAP;
		else if (type.equals("complex"))
			return ObjectType.COMPLEX;

		return ObjectType.NONE;
	}

/*
	private String printAttribute(Attributes atts) {
		String name = atts.getValue("name");
		String value = atts.getValue("value");
		String type = atts.getValue("type");
		if (type == null) type = "string";
		String str = "<att name=\""+name+"\" type=\""+type+"\" value=\""+value+"\">";
		return str;
	}
*/
}
