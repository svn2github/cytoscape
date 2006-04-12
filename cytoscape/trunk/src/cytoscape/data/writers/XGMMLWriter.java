/*
 File: XGMMLWriter.java 
 
 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
 
 The Cytoscape Consortium is: 
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Pasteur Institute
 - Agilent Technologies
 
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

package cytoscape.data.writers;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.data.Semantics;
import cytoscape.data.readers.MetadataParser;
import cytoscape.generated2.Att;
import cytoscape.generated2.Edge;
import cytoscape.generated2.Graph;
import cytoscape.generated2.Graphics;
import cytoscape.generated2.Node;
import cytoscape.generated2.ObjectFactory;
import cytoscape.generated2.RdfRDF;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.LineType;

/**
 * 
 * Write network and attributes in a streme.
 * 
 * @author kono
 * 
 */
public class XGMMLWriter {

	// Package to be used for data binding.
	static final String PACKAGE_NAME = "cytoscape.generated2";
	static final String METADATA_NAME = "networkMetadata";

	// GML-Compatible Pre-defined Shapes
	protected static String RECTANGLE = "rectangle";
	protected static String ELLIPSE = "ellipse";
	protected static String LINE = "Line"; // This is the Polyline object.
	protected static String POINT = "point";
	protected static String DIAMOND = "diamond";
	protected static String HEXAGON = "hexagon";
	protected static String OCTAGON = "octagon";
	protected static String PARALELLOGRAM = "parallelogram";
	protected static String TRIANGLE = "triangle";

	// Node types
	protected static String NORMAL = "normal";
	protected static String METANODE = "metanode";
	protected static String REFERENCE = "reference";

	// Object types
	protected static int NODE = 1;
	protected static int EDGE = 2;
	protected static int NETWORK = 3;

	protected static final String BACKGROUND = "backgroundColor";

	private CyAttributes nodeAttributes;
	private CyAttributes edgeAttributes;
	private CyAttributes networkAttributes;
	private String[] nodeAttNames = null;
	private String[] edgeAttNames = null;
	private String[] networkAttNames = null;

	private CyNetwork network;
	private CyNetworkView networkView;

	private ArrayList nodeList;
	private ArrayList metanodeList;
	private ArrayList edgeList;

	JAXBContext jc;
	ObjectFactory objFactory;

	MetadataParser mdp;
	Graph graph = null;
	ExpressionData expression;

	// Default CSS file name. Will be distributed with Cytoscape 2.3.
	private static final String CSS_FILE = "base.css";

	protected static final String FLOAT_TYPE = "float";
	protected static final String INT_TYPE = "int";
	protected static final String STRING_TYPE = "string";
	protected static final String BOOLEAN_TYPE = "boolean";
	protected static final String LIST_TYPE = "list";
	protected static final String MAP_TYPE = "map";

	public XGMMLWriter(CyNetwork network, CyNetworkView view) {
		this.network = network;
		this.networkView = view;
		expression = Cytoscape.getExpressionData();

		nodeAttributes = Cytoscape.getNodeAttributes();
		edgeAttributes = Cytoscape.getEdgeAttributes();
		networkAttributes = Cytoscape.getNetworkAttributes();

		nodeList = new ArrayList();
		metanodeList = new ArrayList();
		edgeList = new ArrayList();

		nodeAttNames = nodeAttributes.getAttributeNames();
		edgeAttNames = edgeAttributes.getAttributeNames();
		networkAttNames = networkAttributes.getAttributeNames();

		try {
			initialize();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Generate JAXB objects for the XGMML file.
	 * 
	 * @throws JAXBException
	 * @throws URISyntaxException
	 */
	private void initialize() throws JAXBException, URISyntaxException {
		objFactory = new ObjectFactory();
		RdfRDF metadata = null;
		Att graphAtt = null;
		Att globalGraphics = null;

		jc = JAXBContext.newInstance(PACKAGE_NAME);
		graph = objFactory.createGraph();

		graphAtt = objFactory.createAtt();
		graph.setId(network.getIdentifier());
		graph.setLabel(network.getTitle());

		// Metadata
		mdp = new MetadataParser(network);
		metadata = mdp.getMetadata();

		graphAtt.setName(METADATA_NAME);
		graphAtt.getContent().add(metadata);
		graph.getAtt().add(graphAtt);

		// Store background color
		if (networkView != null) {
			globalGraphics = objFactory.createAtt();
			globalGraphics.setName(BACKGROUND);

			globalGraphics.setValue(paint2string(networkView
					.getBackgroundPaint()));
			graph.getAtt().add(globalGraphics);
		}

	}

	/**
	 * Write the XGMML file.
	 * 
	 * @param writer
	 *            :Witer to create XGMML file
	 * @throws JAXBException
	 * @throws IOException
	 */
	public void write(Writer writer) throws JAXBException, IOException {

		// writeNodes(nodeIt);
		writeBaseNodes();
		writeMetanodes();

		// Create edge objects
		writeEdges();

		// write out network attributes
		writeNetworkAttributes();

		// This creates the header of the XML document.
		writer.write("<?xml version='1.0'?>\n");

		// Will be restored when CSS is ready.
		// writer.write("<?xml-stylesheet type='text/css' href='" + CSS_FILE
		// + "' ?>\n");

		Marshaller m = jc.createMarshaller();

		// Set proper namespace prefix (mainly for metadata)
		try {
			m.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			m.setProperty("com.sun.xml.bind.namespacePrefixMapper",
					new NamespacePrefixMapperImpl());
		} catch (PropertyException e) {
			// if the JAXB provider doesn't recognize the prefix mapper,
			// it will throw this exception. Since being unable to specify
			// a human friendly prefix is not really a fatal problem,
			// you can just continue marshalling without failing
			;
		}

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(graph, writer);

	}

	private void writeEdges() throws JAXBException {
		Iterator it = network.edgesIterator();

		CyEdge curEdge = null;
		Edge jxbEdge = null;

		while (it.hasNext()) {
			curEdge = (CyEdge) it.next();
			jxbEdge = objFactory.createEdge();

			jxbEdge.setId(curEdge.getIdentifier());
			jxbEdge.setLabel(edgeAttributes.getStringAttribute(curEdge
					.getIdentifier(), Semantics.CANONICAL_NAME));

			// jxbEdge.setSource(curEdge.getSource().getIdentifier());
			// jxbEdge.setTarget(curEdge.getTarget().getIdentifier());

			jxbEdge.setSource(Integer.toString(curEdge.getSource()
					.getRootGraphIndex()));
			jxbEdge.setTarget(Integer.toString(curEdge.getTarget()
					.getRootGraphIndex()));

			if (networkView != null) {
				EdgeView curEdgeView = networkView.getEdgeView(curEdge);

				jxbEdge.setGraphics(getGraphics(EDGE, curEdgeView));
			}
			attributeWriter(EDGE, curEdge.getIdentifier(), jxbEdge);

			edgeList.add(curEdge);
			graph.getNodeOrEdge().add(jxbEdge);

		}

	}

	// write out network attributes
	protected void writeNetworkAttributes() throws JAXBException {

		attributeWriter(NETWORK, network.getIdentifier(), null);
	}

	// Expand metanode information
	protected void writeMetanode() {

	}

	/**
	 * Extract attributes and map it to JAXB object
	 * 
	 * @param type
	 * @param ID
	 * @return
	 * @throws JAXBException
	 */
	protected void attributeWriter(int type, String id, Object target)
			throws JAXBException {
		byte attType;
		Att attr;

		if (type == NODE) {
			Node targetNode = (Node) target;
			for (int i = 0; i < nodeAttNames.length; i++) {
				if (nodeAttNames[i] == "node.width"
						|| nodeAttNames[i] == "node.height") {
					// Ignore
				} else if (nodeAttNames[i] == "nodeType") {
					String nType = nodeAttributes.getStringAttribute(id,
							nodeAttNames[i]);
					if (nType != null) {
						targetNode.setName(nType);
					} else {
						targetNode.setName("base");
					}
				} else {
					attr = objFactory.createAtt();
					attType = nodeAttributes.getType(nodeAttNames[i]);
					if (attType == CyAttributes.TYPE_FLOATING) {
						Double dAttr = nodeAttributes.getDoubleAttribute(id,
								nodeAttNames[i]);
						attr.setName(nodeAttNames[i]);
						attr.setLabel(FLOAT_TYPE);
						if (dAttr != null) {
							attr.setValue(dAttr.toString());
						}
					} else if (attType == CyAttributes.TYPE_INTEGER) {
						Integer iAttr = nodeAttributes.getIntegerAttribute(id,
								nodeAttNames[i]);
						attr.setName(nodeAttNames[i]);
						attr.setLabel(INT_TYPE);
						if (iAttr != null) {
							attr.setValue(iAttr.toString());
						}
					} else if (attType == CyAttributes.TYPE_STRING) {
						String sAttr = nodeAttributes.getStringAttribute(id,
								nodeAttNames[i]);

						if (sAttr != null) {
							attr.setValue(sAttr.toString());
						} else if (sAttr == null
								&& nodeAttNames[i] == "nodeType") {
							attr.setValue(NORMAL);
						}
						attr.setLabel(STRING_TYPE);
						attr.setName(nodeAttNames[i]);

					} else if (attType == CyAttributes.TYPE_BOOLEAN) {
						Boolean bAttr = nodeAttributes.getBooleanAttribute(id,
								nodeAttNames[i]);
						attr.setName(nodeAttNames[i]);
						attr.setLabel(BOOLEAN_TYPE);
						if (bAttr != null)
							attr.setValue(bAttr.toString());
					} else if (attType == CyAttributes.TYPE_SIMPLE_LIST) {
						// TODO: HANDLE LISTS
						List listAttr = nodeAttributes.getAttributeList(id,
								nodeAttNames[i]);

						attr.setName(nodeAttNames[i]);
						attr.setLabel(LIST_TYPE);
						Iterator listIt = listAttr.iterator();

						while (listIt.hasNext()) {
							// Object oneAttr = listIt.next();
							// memberAttr = objFactory.createAtt();
							//							
							// memberAttr.setValue(oneAttr.toString());
							// attr.getContent().add(memberAttr);
							Object obj = listIt.next();
							Att memberAttr = objFactory.createAtt();
							memberAttr.setValue(obj.toString());
							memberAttr.setLabel(checkType(obj));
							// System.out.println("!!!!!!!!!!!List obj: " +
							// obj);
							attr.getContent().add(memberAttr);

						}
						// attr.getContent().add(listAttr);
					} else if (attType == CyAttributes.TYPE_SIMPLE_MAP) {
						// TODO: HANDLE MAP
					}

					targetNode.getAtt().add(attr);
				}
			}
		} else if (type == EDGE) {
			Edge targetEdge = (Edge) target;
			for (int i = 0; i < edgeAttNames.length; i++) {
				attr = objFactory.createAtt();
				attType = edgeAttributes.getType(edgeAttNames[i]);
				if (attType == CyAttributes.TYPE_FLOATING) {
					Double dAttr = edgeAttributes.getDoubleAttribute(id,
							edgeAttNames[i]);
					attr.setName(edgeAttNames[i]);
					attr.setLabel(FLOAT_TYPE);
					if (dAttr != null)
						attr.setValue(dAttr.toString());
				} else if (attType == CyAttributes.TYPE_INTEGER) {
					Integer iAttr = edgeAttributes.getIntegerAttribute(id,
							edgeAttNames[i]);
					attr.setName(edgeAttNames[i]);
					attr.setLabel(INT_TYPE);
					if (iAttr != null)
						attr.setValue(iAttr.toString());
				} else if (attType == CyAttributes.TYPE_STRING) {
					String sAttr = edgeAttributes.getStringAttribute(id,
							edgeAttNames[i]);
					attr.setName(edgeAttNames[i]);
					attr.setLabel(STRING_TYPE);
					if (sAttr != null)
						attr.setValue(sAttr.toString());
				} else if (attType == CyAttributes.TYPE_BOOLEAN) {
					Boolean bAttr = edgeAttributes.getBooleanAttribute(id,
							edgeAttNames[i]);
					attr.setName(edgeAttNames[i]);
					attr.setLabel(BOOLEAN_TYPE);
					if (bAttr != null)
						attr.setValue(bAttr.toString());
				} else if (attType == CyAttributes.TYPE_SIMPLE_LIST) {
					// TODO: HANDLE LISTS
				} else if (attType == CyAttributes.TYPE_SIMPLE_MAP) {
					// TODO: HANDLE MAP
				}
				targetEdge.getAtt().add(attr);
			}
		}
		// process network attributes
		else if (type == NETWORK) {
			for (int i = 0; i < networkAttNames.length; i++){
				// create the attribute and set its type
				attr = objFactory.createAtt();
				attType = networkAttributes.getType(networkAttNames[i]);
				// floating attribute
				if (attType == CyAttributes.TYPE_FLOATING) {
					Double dAttr = networkAttributes.getDoubleAttribute(id, networkAttNames[i]);
					attr.setName(networkAttNames[i]);
					attr.setLabel(FLOAT_TYPE);
					if (dAttr != null) attr.setValue(dAttr.toString());
				}
				// integer type
				else if (attType == CyAttributes.TYPE_INTEGER) {
					Integer iAttr = networkAttributes.getIntegerAttribute(id, networkAttNames[i]);
					attr.setName(networkAttNames[i]);
					attr.setLabel(INT_TYPE);
					if (iAttr != null) attr.setValue(iAttr.toString());
				}
				// string type
				else if (attType == CyAttributes.TYPE_STRING) {
					String sAttr = networkAttributes.getStringAttribute(id, networkAttNames[i]);
					attr.setName(networkAttNames[i]);
					attr.setLabel(STRING_TYPE);
					if (sAttr != null) attr.setValue(sAttr.toString());
				}
				// boolean type
				else if (attType == CyAttributes.TYPE_BOOLEAN) {
					Boolean bAttr = networkAttributes.getBooleanAttribute(id, networkAttNames[i]);
					attr.setName(networkAttNames[i]);
					attr.setLabel(BOOLEAN_TYPE);
					if (bAttr != null) attr.setValue(bAttr.toString());
				}
				// simple list
				else if (attType == CyAttributes.TYPE_SIMPLE_LIST) {
					// tbd
				}
				// simple map
				else if (attType == CyAttributes.TYPE_SIMPLE_MAP) {
					// tdb
				}
				graph.getAtt().add(attr);
			}
		}
	}

	protected Graphics getGraphics(int type, Object target)
			throws JAXBException {

		Graphics graphics = objFactory.createGraphics();

		if (type == NODE) {
			NodeView curNodeView = (NodeView) target;

			/**
			 * GML compatible attributes
			 */
			// Node shape
			graphics.setType(number2shape(curNodeView.getShape()));

			// Node size and position
			graphics.setH(curNodeView.getHeight());
			graphics.setW(curNodeView.getWidth());
			graphics.setX(curNodeView.getXPosition());
			graphics.setY(curNodeView.getYPosition());

			// Node color
			graphics.setFill(paint2string(curNodeView.getUnselectedPaint()));

			// Node border basic info.
			BasicStroke borderType = (BasicStroke) curNodeView.getBorder();

			float borderWidth = borderType.getLineWidth();
			BigInteger intWidth = BigInteger.valueOf((long) borderWidth);
			graphics.setWidth(intWidth);
			graphics.setOutline(paint2string(curNodeView.getBorderPaint()));

			/**
			 * Extended attributes supported by GINY
			 */
			// Store Cytoscap-local graphical attributes
			Att cytoscapeNodeAttr = objFactory.createAtt();
			cytoscapeNodeAttr.setName("cytoscapeNodeGraphicsAttributes");

			Att transparency = objFactory.createAtt();
			Att nodeLabelFont = objFactory.createAtt();
			Att borderLineType = objFactory.createAtt();

			transparency.setName("nodeTransparency");
			nodeLabelFont.setName("nodeLabelFont");
			borderLineType.setName("borderLineType");

			transparency.setValue(Double
					.toString(curNodeView.getTransparency()));
			nodeLabelFont
					.setValue(encodeFont(curNodeView.getLabel().getFont()));

			// Where should we store line-type info???
			float[] dash = borderType.getDashArray();
			if (dash == null) {
				// System.out.println("##Border is NORMAL LINE");
				borderLineType.setValue("solid");
			} else {
				// System.out.println("##Border is DASHED LINE");
				String dashArray = null;
				for (int i = 0; i < dash.length; i++) {
					dashArray = Double.toString(dash[i]);
					if (i < dash.length - 1) {
						dashArray = dashArray + ",";
					}
				}
				borderLineType.setValue(dashArray);
			}
			cytoscapeNodeAttr.getContent().add(transparency);
			cytoscapeNodeAttr.getContent().add(nodeLabelFont);
			cytoscapeNodeAttr.getContent().add(borderLineType);

			graphics.getAtt().add(cytoscapeNodeAttr);

			return graphics;
		} else if (type == EDGE) {
			EdgeView curEdgeView = (EdgeView) target;

			/**
			 * GML compatible attributes
			 */
			// Width
			graphics.setWidth(BigInteger.valueOf((long) curEdgeView
					.getStrokeWidth()));
			// Color
			graphics.setFill(paint2string(curEdgeView.getUnselectedPaint()));

			/**
			 * Extended attributes supported by GINY
			 */
			// Store Cytoscap-local graphical attributes
			Att cytoscapeEdgeAttr = objFactory.createAtt();
			cytoscapeEdgeAttr.setName("cytoscapeEdgeGraphicsAttributes");

			Att sourceArrow = objFactory.createAtt();
			Att targetArrow = objFactory.createAtt();
			Att edgeLabelFont = objFactory.createAtt();
			Att edgeLineType = objFactory.createAtt();
			Att sourceArrowColor = objFactory.createAtt();
			Att targetArrowColor = objFactory.createAtt();

			sourceArrow.setName("sourceArrow");
			targetArrow.setName("targetArrow");
			edgeLabelFont.setName("edgeLabelFont");
			edgeLineType.setName("edgeLineType");
			sourceArrowColor.setName("sourceArrowColor");
			targetArrowColor.setName("targetArrowColor");

			sourceArrow.setValue(Integer.toString(curEdgeView
					.getSourceEdgeEnd()));
			targetArrow.setValue(Integer.toString(curEdgeView
					.getTargetEdgeEnd()));

			edgeLabelFont
					.setValue(encodeFont(curEdgeView.getLabel().getFont()));

			edgeLineType.setValue(lineTypeBuilder(curEdgeView).toString());

			// System.out.println("Source Color is :" +
			// curEdgeView.getSourceEdgeEndPaint().toString());
			// System.out.println("Target Color is :" +
			// curEdgeView.getTargetEdgeEndPaint().toString());
			// System.out.println("Source Type is :" +
			// curEdgeView.getSourceEdgeEnd());
			// System.out.println("Target Type is :" +
			// curEdgeView.getTargetEdgeEnd());
			//			

			sourceArrowColor.setValue(paint2string(curEdgeView
					.getSourceEdgeEndPaint()));
			targetArrowColor.setValue(paint2string(curEdgeView
					.getTargetEdgeEndPaint()));

			cytoscapeEdgeAttr.getContent().add(sourceArrow);
			cytoscapeEdgeAttr.getContent().add(targetArrow);
			cytoscapeEdgeAttr.getContent().add(edgeLabelFont);
			cytoscapeEdgeAttr.getContent().add(edgeLineType);
			cytoscapeEdgeAttr.getContent().add(sourceArrowColor);
			cytoscapeEdgeAttr.getContent().add(targetArrowColor);

			graphics.getAtt().add(cytoscapeEdgeAttr);

			return graphics;
		}

		return null;
	}

	protected void expand(CyNode node, Node metanode, int[] childrenIndices)
			throws JAXBException {
		CyNode childNode = null;
		Att children = objFactory.createAtt();
		children.setName("metanodeChildren");
		Graph subGraph = objFactory.createGraph();
		Node jxbChildNode = null;

		// test

		for (int i = 0; i < childrenIndices.length; i++) {
			childNode = (CyNode) Cytoscape.getRootGraph().getNode(
					childrenIndices[i]);

			jxbChildNode = objFactory.createNode();
			jxbChildNode.setId(childNode.getIdentifier());
			jxbChildNode.setLabel(nodeAttributes.getStringAttribute(childNode
					.getIdentifier(), Semantics.CANONICAL_NAME));
			subGraph.getNodeOrEdge().add(jxbChildNode);
			int[] grandChildrenIndices = network
					.getRootGraph()
					.getNodeMetaChildIndicesArray(childNode.getRootGraphIndex());
			if (grandChildrenIndices == null
					|| grandChildrenIndices.length == 0) {
				attributeWriter(NODE, childNode.getIdentifier(), jxbChildNode);
				metanode.setGraphics(getGraphics(NODE, networkView
						.getNodeView(node)));
			} else {

				// System.out.print("This is a metanode!: "
				// + jxbChildNode.getLabel() + ", number = "
				// + childrenIndices.length);
				expand(childNode, jxbChildNode, grandChildrenIndices);
			}

		}
		attributeWriter(NODE, metanode.getId(), metanode);

		children.getContent().add(subGraph);
		metanode.getAtt().add(children);
	}

	// Convert number to shape string
	protected String number2shape(int type) {
		if (type == NodeView.ELLIPSE) {
			return ELLIPSE;
		} else if (type == NodeView.RECTANGLE) {
			return RECTANGLE;
		} else if (type == NodeView.DIAMOND) {
			return DIAMOND;
		} else if (type == NodeView.HEXAGON) {
			return HEXAGON;
		} else if (type == NodeView.OCTAGON) {
			return OCTAGON;
		} else if (type == NodeView.PARALELLOGRAM) {
			return PARALELLOGRAM;
		} else if (type == NodeView.TRIANGLE) {
			return TRIANGLE;
		} else {
			return null;
		}
	}

	protected String paint2string(Paint p) {

		Color c = (Color) p;
		return ("#"// +Integer.toHexString(c.getRGB());
				+ Integer.toHexString(256 + c.getRed()).substring(1)
				+ Integer.toHexString(256 + c.getGreen()).substring(1) + Integer
				.toHexString(256 + c.getBlue()).substring(1));
	}

	/**
	 * Returns all nodes in this network.
	 * 
	 * @throws JAXBException
	 * 
	 * 
	 */
	private void writeBaseNodes() throws JAXBException {

		Node jxbNode = null;

		CyNode curNode = null;

		Iterator it = network.nodesIterator();

		while (it.hasNext()) {
			curNode = (CyNode) it.next();
			jxbNode = objFactory.createNode();

			String targetnodeID = Integer.toString(curNode.getRootGraphIndex());
			// System.out.println("nodeID ======== " + targetnodeID);

			// jxbNode.setId(curNode.getIdentifier());

			jxbNode.setId(targetnodeID);
			jxbNode.setLabel(nodeAttributes.getStringAttribute(curNode
					.getIdentifier(), Semantics.CANONICAL_NAME));
			jxbNode.setName("base");

			// Add graphics if available
			if (networkView != null) {
				NodeView curNodeView = networkView.getNodeView(curNode);
				jxbNode.setGraphics(getGraphics(NODE, curNodeView));
			}
			
			attributeWriter(NODE, curNode.getIdentifier(), jxbNode);
			if (isMetanode(curNode)) {
				nodeList.add(curNode);
				metanodeList.add(curNode);
				expandChildren(curNode);
			} else {
				nodeList.add(curNode);
				graph.getNodeOrEdge().add(jxbNode);
			}

		}

		// int count = 0;
		// Iterator it2 = metanodeList.iterator();
		// while (it2.hasNext()) {
		// CyNode test = (CyNode) it2.next();
		// count++;
		// System.out.println("%%%%% it test count: " + count + ", "
		// + test.getIdentifier());
		// }

	}

	private Node buildJAXBNode(CyNode node) throws JAXBException {
		Node jxbNode = null;

		jxbNode = objFactory.createNode();
		String targetnodeID = Integer.toString(node.getRootGraphIndex());
		jxbNode.setId(targetnodeID);
		jxbNode.setLabel(nodeAttributes.getStringAttribute(
				node.getIdentifier(), Semantics.CANONICAL_NAME));
		
		if(networkView != null) {
			NodeView curNodeView = networkView.getNodeView(node);
			jxbNode.setGraphics(getGraphics(NODE, curNodeView));
		}
		attributeWriter(NODE, node.getIdentifier(), jxbNode);
		return jxbNode;
	}

	private void expandChildren(CyNode node) throws JAXBException {

		CyNode childNode = null;
		Node jxbNode = null;

		int[] childrenIndices = network.getRootGraph()
				.getNodeMetaChildIndicesArray(node.getRootGraphIndex());

		for (int i = 0; i < childrenIndices.length; i++) {
			childNode = (CyNode) network.getRootGraph().getNode(
					childrenIndices[i]);

			if (isMetanode(childNode)) {
				metanodeList.add(childNode);
				nodeList.add(childNode);
				expandChildren(childNode);

			} else {
				nodeList.add(childNode);
				jxbNode = buildJAXBNode(childNode);
				jxbNode.setName("base");
				graph.getNodeOrEdge().add(jxbNode);
			}
		}
	}

	/**
	 * Metanode has different format in XML. It is a node with subgraph.
	 * 
	 * @throws JAXBException
	 * 
	 */
	private void writeMetanodes() throws JAXBException {
		Iterator it = metanodeList.iterator();

		while (it.hasNext()) {
			CyNode curNode = (CyNode) it.next();
			Node jxbNode = null;
			jxbNode = buildJAXBNode(curNode);

			jxbNode.setName("metaNode");

			int[] childrenIndices = network.getRootGraph()
					.getNodeMetaChildIndicesArray(curNode.getRootGraphIndex());
			Att children = objFactory.createAtt();
			children.setName("metanodeChildren");
			Graph subGraph = objFactory.createGraph();

			for (int i = 0; i < childrenIndices.length; i++) {
				CyNode childNode = null;
				Node childJxbNode = null;

				childNode = (CyNode) network.getRootGraph().getNode(
						childrenIndices[i]);
				childJxbNode = objFactory.createNode();
				childJxbNode.setId(childNode.getIdentifier());

				childJxbNode.setName("reference");
				subGraph.getNodeOrEdge().add(childJxbNode);

			}
			children.getContent().add(subGraph);
			jxbNode.getAtt().add(children);
			graph.getAtt().add(jxbNode);
		}

	}

	/**
	 * Returns true if the node is a metanode.
	 * 
	 * @param node
	 * @return
	 */
	private boolean isMetanode(CyNode node) {

		int[] childrenIndices = network.getRootGraph()
				.getNodeMetaChildIndicesArray(node.getRootGraphIndex());
		if (childrenIndices == null || childrenIndices.length == 0) {
			return false;
		} else {
			return true;
		}
	}

	private String encodeFont(Font font) {
		// Encode font into "fontname-style-pointsize" string
		String fontString = font.getName() + "-" + font.getStyle() + "-"
				+ font.getSize();

		return fontString;
	}

	private String checkType(Object obj) {
		if (obj.getClass() == String.class) {
			return STRING_TYPE;
		} else if (obj.getClass() == Integer.class) {
			return INT_TYPE;
		} else if (obj.getClass() == Double.class
				|| obj.getClass() == Float.class) {
			return FLOAT_TYPE;
		} else if (obj.getClass() == String.class) {
			return STRING_TYPE;
		} else if (obj.getClass() == Boolean.class) {
			return BOOLEAN_TYPE;
		} else
			return null;
	}

	private LineType lineTypeBuilder(EdgeView view) {

		LineType lineType = LineType.LINE_1;
		BasicStroke stroke = (BasicStroke) view.getStroke();

		float[] dash = stroke.getDashArray();

		float width = stroke.getLineWidth();
		if (dash == null) {
			// Normal line. check width
			if (width == 1.0) {
				lineType = LineType.LINE_1;
			} else if (width == 2.0) {
				lineType = LineType.LINE_2;
			} else if (width == 3.0) {
				lineType = LineType.LINE_3;
			} else if (width == 4.0) {
				lineType = LineType.LINE_4;
			} else if (width == 5.0) {
				lineType = LineType.LINE_5;
			} else if (width == 6.0) {
				lineType = LineType.LINE_6;
			} else if (width == 7.0) {
				lineType = LineType.LINE_7;
			}
			// System.out.println("SOLID: " + width);
		} else {
			if (width == 1.0) {
				lineType = LineType.DASHED_1;
			} else if (width == 2.0) {
				lineType = LineType.DASHED_2;
			} else if (width == 3.0) {
				lineType = LineType.DASHED_3;
			} else if (width == 4.0) {
				lineType = LineType.DASHED_4;
			} else if (width == 5.0) {
				lineType = LineType.DASHED_5;
			}
			// System.out.println("DASH: " + width);
		}

		return lineType;
	}

}

class NamespacePrefixMapperImpl extends NamespacePrefixMapper {

	/**
	 * Returns a preferred prefix for the given namespace URI.
	 * 
	 * This method is intended to be overrided by a derived class.
	 * 
	 * @param namespaceUri
	 *            The namespace URI for which the prefix needs to be found.
	 *            Never be null. "" is used to denote the default namespace.
	 * @param suggestion
	 *            When the content tree has a suggestion for the prefix to the
	 *            given namespaceUri, that suggestion is passed as a parameter.
	 *            Typicall this value comes from the QName.getPrefix to show the
	 *            preference of the content tree. This parameter may be null,
	 *            and this parameter may represent an already occupied prefix.
	 * @param requirePrefix
	 *            If this method is expected to return non-empty prefix. When
	 *            this flag is true, it means that the given namespace URI
	 *            cannot be set as the default namespace.
	 * 
	 * @return null if there's no prefered prefix for the namespace URI. In this
	 *         case, the system will generate a prefix for you.
	 * 
	 * Otherwise the system will try to use the returned prefix, but generally
	 * there's no guarantee if the prefix will be actually used or not.
	 * 
	 * return "" to map this namespace URI to the default namespace. Again,
	 * there's no guarantee that this preference will be honored.
	 * 
	 * If this method returns "" when requirePrefix=true, the return value will
	 * be ignored and the system will generate one.
	 */
	public String getPreferredPrefix(String namespaceUri, String suggestion,
			boolean requirePrefix) {
		// I want this namespace to be mapped to "xsi"
		if ("http://www.w3.org/2001/XMLSchema-instance".equals(namespaceUri))
			return "xsi";

		// For RDF.
		if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(namespaceUri))
			return "rdf";

		// Dublin core semantics.
		if ("http://purl.org/dc/elements/1.1/".equals(namespaceUri))
			return "dc";

		// otherwise I don't care. Just use the default suggestion, whatever it
		// may be.
		return suggestion;
	}

	public String[] getPreDeclaredNamespaceUris() {
		return new String[] { "http://www.w3.org/2001/XMLSchema-instance",
				"http://www.w3.org/1999/xlink",
				"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
				"http://purl.org/dc/elements/1.1/" };
	}
}
