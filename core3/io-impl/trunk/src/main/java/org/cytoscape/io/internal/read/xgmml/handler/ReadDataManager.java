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

    /* Edge handle list */
    protected List<String> handleList;
    /* X handle */
    protected String edgeBendX;
    /* Y handle */
    protected String edgeBendY;

    protected List<Object> listAttrHolder;
    protected CyNetwork network;

    /*
     * The graph-global directedness, which will be used as default directedness
     * of edges.
     */
    protected boolean currentNetworkisDirected = true;

    private Map<CyNode, Attributes> nodeGraphicsMap;
    private Map<CyEdge, Attributes> edgeGraphicsMap;

    protected String backgroundColor;
    protected String graphZoom;
    protected String graphCenterX;
    protected String graphCenterY;
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

        /* Edge handle list */
        handleList = null;

        edgeBendX = null;
        edgeBendY = null;
    }

    public ReadDataManager() {
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
        if (backgroundColor == null) return null;
        return new Color(Integer.parseInt(backgroundColor.substring(1), 16));
    }

    public Double getGraphZoom() {
        Double d = new Double(1);

        try {
            if (graphZoom != null) d = new Double(graphZoom);
        } catch (NumberFormatException nfe) {
            // TODO: warning
        }

        return d;
    }

    public Double getGraphCenterX() {
        Double d = new Double(0);

        try {
            if (graphCenterX != null) d = new Double(graphCenterX);
        } catch (NumberFormatException nfe) {
            // TODO: warning
        }

        return d;
    }

    public Double getGraphCenterY() {
        Double d = new Double(0);

        try {
            if (graphCenterY != null) d = new Double(graphCenterY);
        } catch (NumberFormatException nfe) {
            // TODO: warning
        }

        return d;
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
