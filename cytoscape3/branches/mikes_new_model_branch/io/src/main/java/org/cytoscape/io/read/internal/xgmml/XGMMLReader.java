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

import org.cytoscape.io.read.CyNetworkReader; 
import org.cytoscape.io.read.CyNetworkViewReader; 

import org.cytoscape.io.read.internal.VisualStyleBuilder; 

import cytoscape.task.TaskMonitor;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.groups.CyGroup;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.LayoutAdapter;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.GraphView;
import org.cytoscape.view.GraphViewFactory;
import org.cytoscape.view.NodeView;

import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.VisualPropertyType;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.ParserAdapter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ArrayList;


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
public class XGMMLReader implements CyNetworkReader, CyNetworkViewReader {
	// Graph Tags
	protected static final String GRAPH = "graph";
	protected static final String NODE = "node";
	protected static final String EDGE = "edge";
	protected static final String GRAPHICS = "graphics";
	protected static final String LABEL = "label";
	protected static final String SOURCE = "source";
	protected static final String TARGET = "target";

	// Shapes used in Cytoscape (not GML standard)
	// In GML, they are called "type"
	protected static final String RECTANGLE = "rectangle";
	protected static final String ELLIPSE = "ellipse";
	protected static final String LINE = "Line"; // This is the Polyline
	                                             // object.
	protected static final String POINT = "point";
	protected static final String DIAMOND = "diamond";
	protected static final String HEXAGON = "hexagon";
	protected static final String OCTAGON = "octagon";
	protected static final String PARALELLOGRAM = "parallelogram";
	protected static final String TRIANGLE = "triangle";

	// XGMML shapes (these should be mapped to Cytoscape shapes
	protected static final String BOX = "box"; // Map to rectangle
	protected static final String CIRCLE = "circle"; // Map to ellipse
	protected static final String VELLIPSIS = "ver_ellipsis";
	protected static final String HELLIPSIS = "hor_ellipsis";
	protected static final String RHOMBUS = "rhombus"; // Map to parallelogram
	protected static final String PENTAGON = "pentagon"; // Map to hexagon

	protected static final String CY_NAMESPACE = "http://www.cytoscape.org";

	// XGMML file name to be loaded.
	private Double graphViewZoom;
	private Double graphViewCenterX;
	private Double graphViewCenterY;
	private XGMMLParser parser;

	private Properties prop; 

	private InputStream networkStream;
	private TaskMonitor taskMonitor;

	private CyNetwork newNet;
	private GraphView view;

	// TODO set up injection
	public void setProperties(Properties prop) {
		this.prop = prop;
	}

	/**
	 * Constructor.<br>
	 * This is usually used for remote file loading.
	 *
	 * @param is
	 *            Input stream of XGMML file,
	 *
	 */
	// TODO network to be injected... or something
	public XGMMLReader(CyNetwork net) { 
		newNet = net;
	}

	public void setInput(InputStream is) {
		networkStream = is;
	}

	public List<CyNetwork> getReadNetworks() {
		List<CyNetwork> l = new ArrayList<CyNetwork>(1);
		l.add(newNet);
		return l;
	}

	public List<GraphView> getReadNetworkViews() {
		return null;
	}

	/**
 	 * Sets the task monitor we want to use
 	 *
 	 * @param monitor the TaskMonitor to use
 	 */
	public void setTaskMonitor(TaskMonitor monitor) {
		this.taskMonitor = monitor;
	}


	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	public void read() throws IOException {
		try {
			this.readXGMML();
		} catch (SAXException e) {
			if (taskMonitor != null) {
				taskMonitor.setException(e, e.getMessage());
			}
			// e.printStackTrace();
			throw new IOException(e.getMessage());
		}
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
		try {
			 // Read the file and map the entire XML document into data structure.
			if (taskMonitor != null) {
				taskMonitor.setPercentCompleted(-1);
				taskMonitor.setStatus("Reading XGMML data...");
			}

			// Get our parser
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			ParserAdapter pa = new ParserAdapter(sp.getParser());
			parser = new XGMMLParser(newNet);
			pa.setContentHandler(parser);
			pa.setErrorHandler(parser);
			pa.parse(new InputSource(networkStream));

		} catch (OutOfMemoryError oe) {
			// It's not generally a good idea to catch OutOfMemoryErrors, but in
			// this case, where we know the culprit (a file that is too large),
			// we can at least try to degrade gracefully.
			System.gc();
			throw new RuntimeException("Out of memory error caught! The network being loaded is too large for the current memory allocation.  Use the -Xmx flag for the java virtual machine to increase the amount of memory available, e.g. java -Xmx1G cytoscape.jar -p plugins ....");
		} catch (ParserConfigurationException e) {
		} catch (SAXParseException e) {
			System.err.println("XGMMLParser: fatal parsing error on line "+e.getLineNumber()+" -- '"+e.getMessage()+"'");
			throw e;
		} finally {
			if (networkStream != null) {
				networkStream.close();
				networkStream = null;
			}
		}
	}



	/**
	 * layout the graph based on the graphic attributes
	 *
	 * @param myView the view of the network we want to layout
	 */
	private void layout(GraphView myView) {
		if ((myView == null) || (myView.nodeCount() == 0)) {
			return;
		}

		String vsbSwitch = prop.getProperty("visualStyleBuilder");

		// Create our visual style creator.  We use the vsbSwitch to tell the style builder
		// whether to create the override attributes or not
		boolean buildStyle = true;
		if (vsbSwitch != null && vsbSwitch.equals("off"))
			buildStyle = false;

		VisualStyleBuilder graphStyle = new VisualStyleBuilder(parser.getNetworkName(), false);

		// Set background clolor
		if (parser.getBackgroundColor() != null) {
			myView.setBackgroundPaint(parser.getBackgroundColor());
		}

		// Layout nodes
		layoutNodes(myView, graphStyle, buildStyle);

		// Layout edges
		layoutEdges(myView, graphStyle, buildStyle);

		if (buildStyle)
			graphStyle.buildStyle();
	}

	/**
	 * Layout nodes if view is available.
	 *
	 * @param myView
	 *            GINY's graph view object for the current network.
	 * @param graphStyle the visual style creator object
	 * @param buildStyle if true, build the graphical style
	 */
	private void layoutNodes(final GraphView myView, 
	                         final VisualStyleBuilder graphStyle, 
	                         boolean buildStyle) {
		String label = null;
		int tempid = 0;
		NodeView view = null;
		HashMap<CyNode, Attributes> nodeGraphicsMap = parser.getNodeGraphics();

		for (CyNode node: nodeGraphicsMap.keySet()) {
			view = myView.getNodeView(node);

			if ((label != null) && (view != null)) {
				view.getLabel().setText(label);
			} else if (view != null) {
				view.getLabel().setText("node(" + tempid + ")");
				tempid++;
			}

			if ((nodeGraphicsMap != null) && (view != null)) {
				layoutNodeGraphics(nodeGraphicsMap.get(node), view, graphStyle, buildStyle);
			}
		}
	}

	/**
	 * Extract node graphics information from JAXB object.<br>
	 *
	 * @param graphics
	 *            Graphics information for a node as JAXB object.
	 * @param nodeView
	 *            Actual node view for the target node.
	 * @param graphStyle the visual style creator object
	 * @param buildStyle if true, build the graphical style
	 *
	 */
	private void layoutNodeGraphics(final Attributes graphics, 
	                                final NodeView nodeView,
	                                final VisualStyleBuilder graphStyle,
	                                final boolean buildStyle) {

		// The identifier of this node
		CyRow nodeAttrs = nodeView.getNode().attrs();

		// Location and size of the node
		double x;

		// Location and size of the node
		double y;

		// Location and size of the node
		double h;

		// Location and size of the node
		double w;

		x = XGMMLParser.getDoubleAttribute(graphics,"x");
		y = XGMMLParser.getDoubleAttribute(graphics,"y");
		h = XGMMLParser.getDoubleAttribute(graphics,"h");
		w = XGMMLParser.getDoubleAttribute(graphics,"w");

		nodeView.setXPosition(x);
		nodeView.setYPosition(y);

		if (buildStyle && h != 0.0) {
			// nodeView.setHeight(h);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_HEIGHT, ""+h);
		}
		if (buildStyle && w != 0.0) {
			// nodeView.setWidth(w);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_WIDTH, ""+w);
		}

		// Set color
		if (buildStyle && XGMMLParser.getAttribute(graphics,"fill") != null) {
			String fillColor = XGMMLParser.getAttribute(graphics, "fill");
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_FILL_COLOR, fillColor);
			// nodeView.setUnselectedPaint(fillColor);
		}

		// Set border line color
		if (buildStyle && XGMMLParser.getAttribute(graphics,"outline") != null) {
			String outlineColor = XGMMLParser.getAttribute(graphics, "outline");
			// nodeView.setBorderPaint(outlineColor);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_BORDER_COLOR, outlineColor);
		}

		// Set border line width
		if (buildStyle && XGMMLParser.getAttribute(graphics,"width") != null) {
			String lineWidth = XGMMLParser.getAttribute(graphics,"width");
			// nodeView.setBorderWidth(lineWidth);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_LINE_WIDTH, lineWidth);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"nodeTransparency",CY_NAMESPACE) != null) {
			String opString = XGMMLParser.getAttributeNS(graphics,"nodeTransparency", CY_NAMESPACE);
			float opacity = (float)Double.parseDouble(opString)*255;
			// Opacity is saved as a float from 0-1, but internally we use 0-255
			// nodeView.setTransparency(opacity);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_OPACITY, ""+opacity);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics, "opacity", CY_NAMESPACE) != null) {
			String opString = XGMMLParser.getAttributeNS(graphics,"opacity", CY_NAMESPACE);
			float opacity = (float)Double.parseDouble(opString);
			// nodeView.setTransparency(opacity);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_OPACITY, opString);
		}

		// These are saved in the exported XGMML, but it's not clear how they get set
		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"nodeLabelFont", CY_NAMESPACE) != null) {
			String nodeLabelFont = XGMMLParser.getAttributeNS(graphics,"nodeLabelFont", CY_NAMESPACE);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_FONT_FACE, nodeLabelFont);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"borderLineType", CY_NAMESPACE) != null) {
			String borderLineType = XGMMLParser.getAttributeNS(graphics,"borderLineType", CY_NAMESPACE);
			graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_LINE_STYLE, borderLineType);
		}

		String type = XGMMLParser.getAttribute(graphics,"type");
		if (buildStyle && type != null) {
			if (type.equals("rhombus"))
				graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_SHAPE,"parallelogram");
			else
				graphStyle.addProperty(nodeAttrs, VisualPropertyType.NODE_SHAPE,type);
		}
	}

	/**
	 * Layout edges if view is available.
	 *
	 * @param myView GINY's graph view object for the current network.
	 * @param graphStyle the visual style creator object
	 * @param buildStyle if true, build the graphical style
	 */
	private void layoutEdges(final GraphView myView, 
	                         final VisualStyleBuilder graphStyle,
	                         final boolean buildStyle) {
		String label = null;
		int tempid = 0;
		EdgeView view = null;
		HashMap<CyEdge, Attributes> edgeGraphicsMap = parser.getEdgeGraphics();

		for (CyEdge edge: edgeGraphicsMap.keySet()) {
			view = myView.getEdgeView(edge);

			if ((edgeGraphicsMap != null) && (view != null)) {
				layoutEdgeGraphics(edgeGraphicsMap.get(edge), view, graphStyle, buildStyle);
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
	                                final EdgeView edgeView,
	                                final VisualStyleBuilder graphStyle,
	                                final boolean buildStyle) {
		CyRow edgeAttrs = edgeView.getEdge().attrs();

		if (buildStyle && XGMMLParser.getAttribute(graphics,"width") != null) {
			String lineWidth = XGMMLParser.getAttribute(graphics,"width");
			// edgeView.setStrokeWidth(lineWidth);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_LINE_WIDTH, lineWidth);
		}

		if (buildStyle && XGMMLParser.getAttribute(graphics,"fill") != null) {
			String edgeColor = XGMMLParser.getAttribute(graphics, "fill");
			// edgeView.setUnselectedPaint(edgeColor);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_COLOR, edgeColor);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"sourceArrow", CY_NAMESPACE) != null) {
			Integer arrowType = XGMMLParser.getIntegerAttributeNS(graphics,"sourceArrow", CY_NAMESPACE);
			ArrowShape shape = ArrowShape.getArrowShape(arrowType);
			String arrowName = shape.getName();
			// edgeView.setSourceEdgeEnd(arrowType);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_SRCARROW_SHAPE, arrowName);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"targetArrow", CY_NAMESPACE) != null) {
			Integer arrowType = XGMMLParser.getIntegerAttributeNS(graphics,"targetArrow", CY_NAMESPACE);
			ArrowShape shape = ArrowShape.getArrowShape(arrowType);
			String arrowName = shape.getName();
			// edgeView.setTargetEdgeEnd(arrowType);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_TGTARROW_SHAPE, arrowName);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"sourceArrowColor", CY_NAMESPACE) != null) {
			String arrowColor = XGMMLParser.getAttributeNS(graphics, "sourceArrowColor", CY_NAMESPACE);
			// edgeView.setSourceEdgeEndPaint(arrowColor);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_SRCARROW_COLOR, arrowColor);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"targetArrowColor", CY_NAMESPACE) != null) {
			String arrowColor = XGMMLParser.getAttributeNS(graphics, "targetArrowColor", CY_NAMESPACE);
			// edgeView.setTargetEdgeEndPaint(arrowColor);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_TGTARROW_COLOR, arrowColor);
		}

		if (buildStyle && XGMMLParser.getAttributeNS(graphics,"edgeLineType", CY_NAMESPACE) != null) {
			String value = XGMMLParser.getAttributeNS(graphics, "edgeLineType", CY_NAMESPACE);
			graphStyle.addProperty(edgeAttrs, VisualPropertyType.EDGE_LINE_STYLE, value);
		}

		if (XGMMLParser.getAttributeNS(graphics,"curved", CY_NAMESPACE) != null) {
			String value = XGMMLParser.getAttributeNS(graphics, "curved", CY_NAMESPACE);
			if (value.equals("STRAIGHT_LINES")) {
				edgeView.setLineType(EdgeView.STRAIGHT_LINES);
			} else if (value.equals("CURVED_LINES")) {
				edgeView.setLineType(EdgeView.CURVED_LINES);
			}
		}

	 	if (XGMMLParser.getAttribute(graphics,"edgeHandleList") != null) {
			// System.out.println("See edgeHandleList");
			String handles[] = XGMMLParser.getAttribute(graphics, "edgeHandleList").split(";");
			for (int i = 0; i < handles.length; i++) {
				String points[] = handles[i].split(",");
				double x = (new Double(points[0])).doubleValue();
				double y = (new Double(points[1])).doubleValue();
				Point2D.Double point = new Point2D.Double();
				point.setLocation(x,y);
				edgeView.getBend().addHandle(point);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param network DOCUMENT ME!
	 */
	private void createView(CyNetwork network) {
		parser.setMetaData(network);

		// Get the view.  Note that for large networks this might be the null view
		view = GraphViewFactory.createGraphView(network);

		layout(view);

		// Now that we have a network, handle the groups
		// This is done here rather than in layout because layout is
		// only called when we create a view.  For large networks,
		// we don't create views by default, but groups should still
		// exist even when we don't create the view
		/*
				// TODO
		Map<CyNode,List<CyNode>>groupMap = parser.getGroupMap();
		if (groupMap != null) {

			for (CyNode groupNode: groupMap.keySet()) {
				CyGroup newGroup = null;
				List<CyNode> childList = groupMap.get(groupNode);
				// TODO  USER namespace here?
				String viewer = groupNode.attrs().get(CyGroup.GROUP_VIEWER_ATTR, String.class);

				// Note that we need to leave the group node in the network so that the saved
				// location information (if there is any) can be utilized by the group viewer.
				// This means that it will be the responsibility of the group viewer to remove
				// the node if they don't want it to be visible

				// Do we already have a view?
				if (view == null ) {
					// No, just create the group, but don't assign a viewer
					newGroup = CyGroupManager.createGroup(groupNode, childList, null);
				} else {
					// Yes, see if the group already exists
					newGroup = CyGroupManager.getCyGroup(groupNode);
					if (newGroup == null) {
						// No, OK so create it and pass down the viewer
						CyGroupManager.createGroup(groupNode, childList, viewer);
					} else {
						// Either the group doesn't have a viewer or it has a different viewer -- change it
						CyGroupManager.setGroupViewer(newGroup, viewer, view, true);
					}
				}
			}
		}
		*/

		if (view == null)
			return;

		// set view zoom
		final Double zoomLevel = parser.getGraphViewZoomLevel();

		if (zoomLevel != null)
			view.setZoom(zoomLevel.doubleValue());

		// set view center
		final Point2D center = parser.getGraphViewCenter();

		if (center != null)
			view.setCenter(center.getX(), center.getY());

	}

	public String[] getExtensions() {
		return new String[]{"xgmml","xml"};
	}

	public String[] getContentTypes() {
		// TODO are these right?
		return new String[]{"text/xgmml","text/plain","text/xml"};
	}

	public String getExtensionDescription() {
		return "XGMML files";
	}
}
