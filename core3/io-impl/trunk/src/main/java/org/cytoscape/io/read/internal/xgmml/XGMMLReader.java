/*
 File: XGMMLReader.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute of Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
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
package org.cytoscape.io.read.internal.xgmml;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cytoscape.io.read.internal.AbstractNetworkReader;
import org.cytoscape.io.read.internal.VisualStyleBuilder;
import org.cytoscape.io.read.internal.xgmml.handler.AttributeValueUtil;
import org.cytoscape.io.read.internal.xgmml.handler.ReadDataManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserAdapter;

/**
 * XGMML file reader.<br>
 * This version is Metanode-compatible.
 * 
 * @version 1.0
 * @since Cytoscape 2.3
 * @see cytoscape.data.writers.XGMMLWriter
 * @author kono
 * 
 */
public class XGMMLReader extends AbstractNetworkReader {

	protected static final String CY_NAMESPACE = "http://www.cytoscape.org";

	private XGMMLParser parser;
	private ReadDataManager readDataManager;

	private AttributeValueUtil attributeValueUtil;

	private Properties prop;

	private CyNetworkView view;

	/**
	 * Constructor.
	 */
	public XGMMLReader() {
		super();
	}

	public void setReadDataManager(ReadDataManager readDataManager) {
		this.readDataManager = readDataManager;
	}

	public void setAttributeValueUtil(AttributeValueUtil attributeValueUtil) {
		this.attributeValueUtil = attributeValueUtil;
	}

	/*
	 * Setters for DI
	 */
	public void setParser(XGMMLParser parser) {
		this.parser = parser;
	}

	public void setProperties(Properties prop) {
		this.prop = prop;
	}

	public void setInputStream(InputStream is) {
		readDataManager.initAllData();
		if (is == null)
			throw new NullPointerException("Input stream is null");
		inputStream = is;

		this.readDataManager.setNetwork(cyNetworkFactory.getInstance());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public Map<Class<?>, Object> read() throws IOException {
		try {

			this.readXGMML();
			this.readObjects.put(CyNetwork.class, readDataManager.getNetwork());
			createView(readDataManager.getNetwork());
			readObjects.put(CyNetworkView.class, view);
		} catch (SAXException e) {
			throw new IOException("Could not parse XGMML file: ");
		}
		
		return readObjects;
	}

	/**
	 * Actual method to read XGMML documents.
	 * 
	 * @throws IOException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private void readXGMML() throws SAXException, IOException {

		final SAXParserFactory spf = SAXParserFactory.newInstance();

		try {
			// Get our parser
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			pa.setContentHandler(parser);
			pa.setErrorHandler(parser);
			pa.parse(new InputSource(inputStream));

		} catch (OutOfMemoryError oe) {
			// It's not generally a good idea to catch OutOfMemoryErrors, but in
			// this case, where we know the culprit (a file that is too large),
			// we can at least try to degrade gracefully.
			System.gc();
			throw new RuntimeException(
					"Out of memory error caught! The network being loaded is too large for the current memory allocation.  Use the -Xmx flag for the java virtual machine to increase the amount of memory available, e.g. java -Xmx1G cytoscape.jar -p plugins ....");
		} catch (ParserConfigurationException e) {
		} catch (SAXParseException e) {
			System.err.println("XGMMLParser: fatal parsing error on line "
					+ e.getLineNumber() + " -- '" + e.getMessage() + "'");
			throw e;
		} finally {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
		}
	}

	/**
	 * layout the graph based on the graphic attributes
	 * 
	 * @param myView
	 *            the view of the network we want to layout
	 */
	private void layout() {
		if ((view == null) || (view.getSource().getNodeCount() == 0))
			return;

		// TODO: Inject correct property
		// String vsbSwitch = prop.getProperty("visualStyleBuilder");

		boolean buildStyle = false;

		// TODO: make VS builder working with new code.
		// if (vsbSwitch != null && vsbSwitch.equals("off"))
		// buildStyle = false;
		//
		// VisualStyleBuilder graphStyle = new
		// VisualStyleBuilder(readDataManager
		// .getNetworkName(), false);

		// Set background clolor
		// TODO update with new view
//		if (readDataManager.getBackgroundColor() != null)
//			view.setBackgroundPaint(readDataManager.getBackgroundColor());

		// Layout nodes
		layoutNodes(null, buildStyle);

		// Layout edges
		layoutEdges(null, buildStyle);
		view.updateView();

		// if (buildStyle)
		// graphStyle.buildStyle();
	}

	/**
	 * Layout nodes if view is available.
	 * 
	 * @param myView
	 *            GINY's graph view object for the current network.
	 * @param graphStyle
	 *            the visual style creator object
	 * @param buildStyle
	 *            if true, build the graphical style
	 */
	private void layoutNodes(final VisualStyleBuilder graphStyle,
			boolean buildStyle) {
			/*
		String label = null;
		int tempid = 0;
		View<CyNode> nv = null;

		final Map<CyNode, Attributes> nodeGraphicsMap = readDataManager
				.getNodeGraphics();

		for (CyNode node : nodeGraphicsMap.keySet()) {
			nv = view.getNodeView(node);
			label = node.attrs().get("name", String.class);

			if ((label != null) && (nv != null)) {
				nv.getLabel().setText(label);
			} else if (view != null) {
				nv.getLabel().setText("node(" + tempid + ")");
				tempid++;
			}

			if ((nodeGraphicsMap != null) && (nv != null)) {
				layoutNodeGraphics(nodeGraphicsMap.get(node), nv, graphStyle,
						buildStyle);
			}
		}
		*/
	}

	/**
	 * Extract node graphics information from JAXB object.<br>
	 * 
	 * @param graphics
	 *            Graphics information for a node as JAXB object.
	 * @param nodeView
	 *            Actual node view for the target node.
	 * @param graphStyle
	 *            the visual style creator object
	 * @param buildStyle
	 *            if true, build the graphical style
	 * 
	 */
	private void layoutNodeGraphics(final Attributes graphics,
			final View<CyNode> nodeView, final VisualStyleBuilder graphStyle,
			final boolean buildStyle) {

		// The identifier of this node
		CyRow nodeAttrs = nodeView.getSource().attrs();
		/*

		// Location and size of the node
		double x;

		// Location and size of the node
		double y;

		// Location and size of the node
		double h;

		// Location and size of the node
		double w;

		x = attributeValueUtil.getDoubleAttribute(graphics, "x");
		y = attributeValueUtil.getDoubleAttribute(graphics, "y");
		h = attributeValueUtil.getDoubleAttribute(graphics, "h");
		w = attributeValueUtil.getDoubleAttribute(graphics, "w");

		nodeView.setXPosition(x);
		nodeView.setYPosition(y);

		if (buildStyle && h != 0.0) {
			// nodeView.setHeight(h);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_HEIGHT,
					"" + h);
		}
		if (buildStyle && w != 0.0) {
			// nodeView.setWidth(w);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_WIDTH, ""
					+ w);
		}

		// Set color
		if (buildStyle
				&& attributeValueUtil.getAttribute(graphics, "fill") != null) {
			String fillColor = attributeValueUtil
					.getAttribute(graphics, "fill");
			graphStyle.addProperty(nodeAttrs,
					VisualPropertyType.NODE_FILL_COLOR, fillColor);
			// nodeView.setUnselectedPaint(fillColor);
		}

		// Set border line color
		if (buildStyle
				&& attributeValueUtil.getAttribute(graphics, "outline") != null) {
			String outlineColor = attributeValueUtil.getAttribute(graphics,
					"outline");
			// nodeView.setBorderPaint(outlineColor);
			graphStyle.addProperty(nodeAttrs,
					VisualPropertyType.NODE_BORDER_COLOR, outlineColor);
		}

		// Set border line width
		if (buildStyle
				&& attributeValueUtil.getAttribute(graphics, "width") != null) {
			String lineWidth = attributeValueUtil.getAttribute(graphics,
					"width");
			// nodeView.setBorderWidth(lineWidth);
			graphStyle.addProperty(nodeAttrs,
					VisualPropertyType.NODE_LINE_WIDTH, lineWidth);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics,
						"nodeTransparency", CY_NAMESPACE) != null) {
			String opString = attributeValueUtil.getAttributeNS(graphics,
					"nodeTransparency", CY_NAMESPACE);
			float opacity = (float) Double.parseDouble(opString) * 255;
			// Opacity is saved as a float from 0-1, but internally we use 0-255
			// nodeView.setTransparency(opacity);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_OPACITY,
					"" + opacity);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics, "opacity",
						CY_NAMESPACE) != null) {
			String opString = attributeValueUtil.getAttributeNS(graphics,
					"opacity", CY_NAMESPACE);
			float opacity = (float) Double.parseDouble(opString);
			// nodeView.setTransparency(opacity);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_OPACITY,
					opString);
		}

		// These are saved in the exported XGMML, but it's not clear how they
		// get set
		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics, "nodeLabelFont",
						CY_NAMESPACE) != null) {
			String nodeLabelFont = attributeValueUtil.getAttributeNS(graphics,
					"nodeLabelFont", CY_NAMESPACE);
			graphStyle.addProperty(nodeAttrs,
					VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics,
						"borderLineType", CY_NAMESPACE) != null) {
			String borderLineType = attributeValueUtil.getAttributeNS(graphics,
					"borderLineType", CY_NAMESPACE);
			graphStyle.addProperty(nodeAttrs,
					VisualPropertyType.NODE_LINE_STYLE, borderLineType);
		}

		String type = attributeValueUtil.getAttribute(graphics, "type");
		if (buildStyle && type != null) {
			if (type.equals("rhombus"))
				graphStyle.addProperty(nodeAttrs,
						VisualPropertyType.NODE_SHAPE, "parallelogram");
			else
				graphStyle.addProperty(nodeAttrs,
						VisualPropertyType.NODE_SHAPE, type);
		}
		*/
	}

	/**
	 * Layout edges if view is available.
	 * 
	 * @param myView
	 *            GINY's graph view object for the current network.
	 * @param graphStyle
	 *            the visual style creator object
	 * @param buildStyle
	 *            if true, build the graphical style
	 */
	private void layoutEdges(final VisualStyleBuilder graphStyle,
			final boolean buildStyle) {
		String label = null;
		int tempid = 0;
		View<CyEdge> ev = null;
		Map<CyEdge, Attributes> edgeGraphicsMap = readDataManager
				.getEdgeGraphics();

		for (CyEdge edge : edgeGraphicsMap.keySet()) {
			ev = view.getEdgeView(edge);

			if ((edgeGraphicsMap != null) && (ev != null)) {
				layoutEdgeGraphics(edgeGraphicsMap.get(edge), ev, graphStyle,
						buildStyle);
			}
		}
	}

	/**
	 * Layout an edge using the stored graphics attributes
	 * 
	 * @param graphics
	 *            Graphics information for an edge as SAX attributes.
	 * @param edgeView
	 *            Actual edge view for the target edge.
	 * 
	 */
	private void layoutEdgeGraphics(final Attributes graphics,
			final View<CyEdge> edgeView, final VisualStyleBuilder graphStyle,
			final boolean buildStyle) {
		CyRow edgeAttrs = edgeView.getSource().attrs();
		// TODO fix for new style view
/*
		if (buildStyle
				&& attributeValueUtil.getAttribute(graphics, "width") != null) {
			String lineWidth = attributeValueUtil.getAttribute(graphics,
					"width");
			// edgeView.setStrokeWidth(lineWidth);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_LINE_WIDTH, lineWidth);
		}

		if (buildStyle
				&& attributeValueUtil.getAttribute(graphics, "fill") != null) {
			String edgeColor = attributeValueUtil
					.getAttribute(graphics, "fill");
			// edgeView.setUnselectedPaint(edgeColor);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_COLOR,
					edgeColor);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics, "sourceArrow",
						CY_NAMESPACE) != null) {
			Integer arrowType = attributeValueUtil.getIntegerAttributeNS(
					graphics, "sourceArrow", CY_NAMESPACE);
			ArrowShape shape = ArrowShape.getArrowShape(arrowType);
			String arrowName = shape.getName();
			// edgeView.setSourceEdgeEnd(arrowType);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics, "targetArrow",
						CY_NAMESPACE) != null) {
			Integer arrowType = attributeValueUtil.getIntegerAttributeNS(
					graphics, "targetArrow", CY_NAMESPACE);
			ArrowShape shape = ArrowShape.getArrowShape(arrowType);
			String arrowName = shape.getName();
			// edgeView.setTargetEdgeEnd(arrowType);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics,
						"sourceArrowColor", CY_NAMESPACE) != null) {
			String arrowColor = attributeValueUtil.getAttributeNS(graphics,
					"sourceArrowColor", CY_NAMESPACE);
			// edgeView.setSourceEdgeEndPaint(arrowColor);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_SRCARROW_COLOR, arrowColor);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics,
						"targetArrowColor", CY_NAMESPACE) != null) {
			String arrowColor = attributeValueUtil.getAttributeNS(graphics,
					"targetArrowColor", CY_NAMESPACE);
			// edgeView.setTargetEdgeEndPaint(arrowColor);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_TGTARROW_COLOR, arrowColor);
		}

		if (buildStyle
				&& attributeValueUtil.getAttributeNS(graphics, "edgeLineType",
						CY_NAMESPACE) != null) {
			String value = attributeValueUtil.getAttributeNS(graphics,
					"edgeLineType", CY_NAMESPACE);
			graphStyle.addProperty(edgeAttrs,
					VisualPropertyType.EDGE_LINE_STYLE, value);
		}

		if (attributeValueUtil.getAttributeNS(graphics, "curved", CY_NAMESPACE) != null) {
			String value = attributeValueUtil.getAttributeNS(graphics,
					"curved", CY_NAMESPACE);
			if (value.equals("STRAIGHT_LINES")) {
				edgeView.setLineType(EdgeView.STRAIGHT_LINES);
			} else if (value.equals("CURVED_LINES")) {
				edgeView.setLineType(EdgeView.CURVED_LINES);
			}
		}

		if (attributeValueUtil.getAttribute(graphics, "edgeHandleList") != null) {
			// System.out.println("See edgeHandleList");
			String handles[] = attributeValueUtil.getAttribute(graphics,
					"edgeHandleList").split(";");
			for (int i = 0; i < handles.length; i++) {
				String points[] = handles[i].split(",");
				double x = (new Double(points[0])).doubleValue();
				double y = (new Double(points[1])).doubleValue();
				Point2D.Double point = new Point2D.Double();
				point.setLocation(x, y);
				edgeView.getBend().addHandle(point);
			}
		}
		*/
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param network
	 *            DOCUMENT ME!
	 */
	private void createView(CyNetwork network) {

		// Get the view. Note that for large networks this might be the null
		// view
		view = cyNetworkViewFactory.getNetworkViewFor(network);

		layout();

		// Now that we have a network, handle the groups
		// This is done here rather than in layout because layout is
		// only called when we create a view. For large networks,
		// we don't create views by default, but groups should still
		// exist even when we don't create the view
		/*
		 * // TODO Map<CyNode,List<CyNode>>groupMap = parser.getGroupMap(); if
		 * (groupMap != null) {
		 * 
		 * for (CyNode groupNode: groupMap.keySet()) { CyGroup newGroup = null;
		 * List<CyNode> childList = groupMap.get(groupNode); // TODO USER
		 * namespace here? String viewer =
		 * groupNode.attrs().get(CyGroup.GROUP_VIEWER_ATTR, String.class);
		 * 
		 * // Note that we need to leave the group node in the network so that
		 * the saved // location information (if there is any) can be utilized
		 * by the group viewer. // This means that it will be the responsibility
		 * of the group viewer to remove // the node if they don't want it to be
		 * visible
		 * 
		 * // Do we already have a view? if (view == null ) { // No, just create
		 * the group, but don't assign a viewer newGroup =
		 * CyGroupManager.createGroup(groupNode, childList, null); } else { //
		 * Yes, see if the group already exists newGroup =
		 * CyGroupManager.getCyGroup(groupNode); if (newGroup == null) { // No,
		 * OK so create it and pass down the viewer
		 * CyGroupManager.createGroup(groupNode, childList, viewer); } else { //
		 * Either the group doesn't have a viewer or it has a different viewer
		 * -- change it CyGroupManager.setGroupViewer(newGroup, viewer, view,
		 * true); } } } }
		 */

	}

}
