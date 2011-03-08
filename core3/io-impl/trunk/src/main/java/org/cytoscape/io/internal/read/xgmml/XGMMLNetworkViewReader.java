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
package org.cytoscape.io.internal.read.xgmml;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.cytoscape.io.internal.read.AbstractNetworkViewReader;
import org.cytoscape.io.internal.read.VisualStyleBuilder;
import org.cytoscape.io.internal.read.xgmml.handler.AttributeValueUtil;
import org.cytoscape.io.internal.read.xgmml.handler.ReadDataManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class XGMMLNetworkViewReader extends AbstractNetworkViewReader {

    protected static final String CY_NAMESPACE = "http://www.cytoscape.org";

    private final XGMMLParser parser;
    private final ReadDataManager readDataManager;
    private final AttributeValueUtil attributeValueUtil;
    private final VisualStyleFactory styleFactory;
    private final VisualMappingManager visMappingManager;
    private final VisualMappingFunctionFactory discreteMappingFactory;
    private final CyProperty<Properties> properties;

    private VisualLexicon lexicon;
    private CyNetworkView view;

    private List<GraphicsConverter<?>> nodeConverters;
    private List<GraphicsConverter<?>> edgeConverters;

    private static final Logger logger = LoggerFactory.getLogger(XGMMLNetworkViewReader.class);

    /**
     * Constructor.
     */
    public XGMMLNetworkViewReader(InputStream inputStream,
                                  RenderingEngineManager renderingEngineManager,
                                  CyNetworkViewFactory cyNetworkViewFactory,
                                  CyNetworkFactory cyNetworkFactory,
                                  ReadDataManager readDataManager,
                                  AttributeValueUtil attributeValueUtil,
                                  VisualStyleFactory styleFactory,
                                  VisualMappingManager visMappingManager,
                                  VisualMappingFunctionFactory discreteMappingFactory,
                                  XGMMLParser parser,
                                  CyProperty<Properties> properties) {
        super(inputStream, cyNetworkViewFactory, cyNetworkFactory);
        this.readDataManager = readDataManager;
        this.attributeValueUtil = attributeValueUtil;
        this.styleFactory = styleFactory;
        this.visMappingManager = visMappingManager;
        this.discreteMappingFactory = discreteMappingFactory;
        this.parser = parser;
        this.properties = properties;
        this.lexicon = renderingEngineManager.getDefaultVisualLexicon();

        createConverters();
    }

    @Override
    public void run(TaskMonitor tm) throws IOException {
        tm.setProgress(-1.0);

        readDataManager.initAllData();
        this.readDataManager.setNetwork(cyNetworkFactory.getInstance());

        try {
            this.readXGMML();
            // this.readObjects.put(CyNetwork.class,
            // readDataManager.getNetwork());
            createView(readDataManager.getNetwork());
            // readObjects.put(CyNetworkView.class, view);
        } catch (SAXException e) {
            throw new IOException("Could not parse XGMML file: ");
        }

        tm.setProgress(1.0);
    }

    @Override
    public void cancel() {
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
            System.err.println("XGMMLParser: fatal parsing error on line " + e.getLineNumber() + " -- '" +
                               e.getMessage() + "'");
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
        if (view == null) return;

        // Get the current state of the style builder switch
        Properties prop = properties.getProperties();
        String vsbSwitch = prop.getProperty("visualStyleBuilder");

        // Create our visual style creator. We use the vsbSwitch to tell the
        // style builder
        // whether to create the override attributes or not
        boolean buildStyle = vsbSwitch == null || vsbSwitch.equals("on");
        VisualStyleBuilder styleBuilder = null;

        if (buildStyle)
            styleBuilder = new VisualStyleBuilder(readDataManager.getNetworkName(), styleFactory, visMappingManager,
                                                  discreteMappingFactory, cyNetworkFactory.getInstance()
                                                          .getDefaultNodeTable(), cyNetworkFactory.getInstance()
                                                          .getDefaultEdgeTable());

        if (view.getModel().getNodeCount() > 0) {
            // Layout nodes and edges
            layoutNodes(styleBuilder);
            layoutEdges(styleBuilder);
        }

        CyRow netRow = view.getModel().getCyRow();

        String netName = readDataManager.getNetworkName();
        netRow.set(NODE_NAME_ATTR_LABEL, netName);

        if (styleBuilder != null) {
            // Network name
            styleBuilder.addProperty(netRow, TwoDVisualLexicon.NETWORK_TITLE, netName);

            // Background color
            Color bgColor = readDataManager.getBackgroundColor();
            styleBuilder.addProperty(netRow, TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT, bgColor);

            // Graph center and zoom
            styleBuilder.addProperty(netRow, TwoDVisualLexicon.NETWORK_SCALE_FACTOR, readDataManager.getGraphZoom());
            styleBuilder.addProperty(netRow, TwoDVisualLexicon.NETWORK_CENTER_X_LOCATION, readDataManager
                    .getGraphCenterX());
            styleBuilder.addProperty(netRow, TwoDVisualLexicon.NETWORK_CENTER_Y_LOCATION, readDataManager
                    .getGraphCenterY());

            // Create and set the visual style
            VisualStyle style = styleBuilder.buildStyle();
            visualstyles = new VisualStyle[] { style };

            // Add and apply the new style
            visMappingManager.addVisualStyle(style);
            visMappingManager.setVisualStyle(style, view);
            style.apply(view);
            view.updateView();
        }
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
    private void layoutNodes(final VisualStyleBuilder styleBuilder) {
        // Graphics (defaults & mappings)
        final Map<CyNode, Attributes> graphicsMap = readDataManager.getNodeGraphics();

        for (Entry<CyNode, Attributes> entry : graphicsMap.entrySet()) {
            CyNode node = entry.getKey();
            Attributes attr = entry.getValue();
            View<CyNode> nv = view.getNodeView(node);

            if (nv != null) {
                layoutNodeGraphics(attr, nv, styleBuilder);
            }
        }
    }

    /**
     * Extract node graphics information from JAXB object.<br>
     * 
     * @param attr
     *            Graphics information for a node as JAXB object.
     * @param nodeView
     *            Actual node view for the target node.
     * @param graphStyle
     *            the visual style creator object
     * @param buildStyle
     *            if true, build the graphical style
     * 
     */
    private void layoutNodeGraphics(final Attributes attr,
                                    final View<CyNode> nodeView,
                                    final VisualStyleBuilder styleBuilder) {
        // Location and size of the node
        double x = attributeValueUtil.getDoubleAttribute(attr, "x");
        double y = attributeValueUtil.getDoubleAttribute(attr, "y");

        nodeView.setVisualProperty(TwoDVisualLexicon.NODE_X_LOCATION, x);
        nodeView.setVisualProperty(TwoDVisualLexicon.NODE_Y_LOCATION, y);

        layoutGraphics(attr, nodeView, styleBuilder);
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
    private void layoutEdges(final VisualStyleBuilder styleBuilder) {
        View<CyEdge> ev = null;
        Map<CyEdge, Attributes> edgeGraphicsMap = readDataManager.getEdgeGraphics();

        for (CyEdge edge : edgeGraphicsMap.keySet()) {
            ev = view.getEdgeView(edge);

            if ((edgeGraphicsMap != null) && (ev != null)) {
                layoutEdgeGraphics(edgeGraphicsMap.get(edge), ev, styleBuilder);
            }
        }
    }

    /**
     * Layout an edge using the stored graphics attributes
     * 
     * @param attr
     *            Graphics information for an edge as SAX attributes.
     * @param edgeView
     *            Actual edge view for the target edge.
     * 
     */
    private void layoutEdgeGraphics(final Attributes attr,
                                    final View<CyEdge> edgeView,
                                    final VisualStyleBuilder styleBuilder) {

        layoutGraphics(attr, edgeView, styleBuilder);

        // TODO missing styles:
        //        if (attributeValueUtil.getAttributeNS(attr, "curved", CY_NAMESPACE) != null) {
        //            String value = attributeValueUtil.getAttributeNS(attr, "curved", CY_NAMESPACE);
        //            if (value.equals("STRAIGHT_LINES")) {
        //                edgeView.setLineType(EdgeView.STRAIGHT_LINES);
        //            } else if (value.equals("CURVED_LINES")) {
        //                edgeView.setLineType(EdgeView.CURVED_LINES);
        //            }
        //        }
        //        if (attributeValueUtil.getAttribute(attr, "edgeHandleList") != null) {
        //            String handles[] = attributeValueUtil.getAttribute(attr, "edgeHandleList").split(";");
        //            for (int i = 0; i < andles.length; i++) {
        //                String points[] = handles[i].split(",");
        //                double x = (new Double(points[0])).doubleValue();
        //                double y = (new Double(points[1])).doubleValue();
        //                Point2D.Double point = new Point2D.Double();
        //                point.setLocation(x, y);
        //                edgeView.getBend().addHandle(point);
        //            }
        //        }
    }

    private void layoutGraphics(final Attributes attr,
                                final View<? extends CyTableEntry> view,
                                final VisualStyleBuilder styleBuilder) {

        // The attributes of this view
        CyTableEntry model = view.getModel();
        CyRow row = model.getCyRow();
        Class<?> type = (model instanceof CyNode) ? CyNode.class : CyEdge.class;
        List<GraphicsConverter<?>> converters = (type == CyNode.class) ? nodeConverters : edgeConverters;

        for (GraphicsConverter<?> conv : converters) {
            String key = conv.getKey();
            VisualProperty vp = lexicon.lookup(type, key);

            if (vp != null) {
                Object value = conv.getValue(attr, attributeValueUtil, vp);

                if (value != null) {
                    if (conv.isBypass()) {
                        view.setLockedValue(vp, value);
                    } else if (styleBuilder != null) {
                        styleBuilder.addProperty(row, vp, value);
                    }
                }
            }
        }
    }

    /**
     * Create and layout the view.
     * 
     * @param network
     *            The network we just parsed.
     */
    private void createView(CyNetwork network) {
        view = cyNetworkViewFactory.getNetworkView(network);
        cyNetworkViews = new CyNetworkView[] { view };
        layout();
    }

    private void createConverters() {
        nodeConverters = new ArrayList<GraphicsConverter<?>>();
        edgeConverters = new ArrayList<GraphicsConverter<?>>();

        // graphics:
        // ----------------------------------
        // TODO: NODE_SIZE: locking h/w
        nodeConverters.add(new GraphicsConverter<Double>("h", Double.class));
        nodeConverters.add(new GraphicsConverter<Double>("w", Double.class));
        nodeConverters.add(new GraphicsConverter<Color>("fill", Color.class));
        nodeConverters.add(new GraphicsConverter<Color>("outline", Color.class));
        nodeConverters.add(new GraphicsConverter<Double>("width", Double.class));
        nodeConverters.add(new GraphicsConverter<Object>("borderLineType", Object.class));
        nodeConverters.add(new GraphicsConverter<Font>("nodeLabelFont", Font.class, CY_NAMESPACE));
        nodeConverters.add(new GraphicsConverter<Integer>("nodeTransparency", Integer.class, CY_NAMESPACE));
        nodeConverters.add(new GraphicsConverter<Object>("type", Object.class));

        // TODO: add more
        edgeConverters.add(new GraphicsConverter<Double>("width", Double.class));
        edgeConverters.add(new GraphicsConverter<Color>("fill", Color.class));
        edgeConverters.add(new GraphicsConverter<Object>("sourceArrow", Object.class, CY_NAMESPACE));
        edgeConverters.add(new GraphicsConverter<Object>("targetArrow", Object.class, CY_NAMESPACE));
        edgeConverters.add(new GraphicsConverter<Font>("edgeLabelFont", Font.class, CY_NAMESPACE));
        edgeConverters.add(new GraphicsConverter<Integer>("edgeTransparency", Integer.class, CY_NAMESPACE));

        // attr (bypass):
        // ----------------------------------
        // TODO: add more
        nodeConverters.add(new GraphicsConverter<Object>("nodeshape", Object.class, true));
        nodeConverters.add(new GraphicsConverter<Double>("nodeheight", Double.class, true));
        nodeConverters.add(new GraphicsConverter<Double>("nodewidth", Double.class, true));
        nodeConverters.add(new GraphicsConverter<Color>("nodefillcolor", Color.class, true));
        nodeConverters.add(new GraphicsConverter<Color>("nodebordercolor", Color.class, true));
        nodeConverters.add(new GraphicsConverter<Double>("nodelinewidth", Double.class, true));
        nodeConverters.add(new GraphicsConverter<Integer>("nodeopacity", Integer.class, true));
        nodeConverters.add(new GraphicsConverter<Font>("nodefont", Font.class, true));
        nodeConverters.add(new GraphicsConverter<Integer>("nodefontsize", Integer.class, true));
        nodeConverters.add(new GraphicsConverter<String>("nodelabel", String.class, true));
        nodeConverters.add(new GraphicsConverter<Color>("nodelabelcolor", Color.class, true));
        nodeConverters.add(new GraphicsConverter<Integer>("nodelabelopacity", Integer.class, true));
        nodeConverters.add(new GraphicsConverter<Double>("nodelabelwidth", Double.class, true));

        edgeConverters.add(new GraphicsConverter<Double>("edgelinewidth", Double.class, true));
        edgeConverters.add(new GraphicsConverter<Color>("edgecolor", Color.class, true));
        edgeConverters.add(new GraphicsConverter<Integer>("edgeopacity", Integer.class, true));
        edgeConverters.add(new GraphicsConverter<Font>("edgefont", Font.class, true));
        edgeConverters.add(new GraphicsConverter<Integer>("edgefontsize", Integer.class, true));
        edgeConverters.add(new GraphicsConverter<String>("edgelabel", String.class, true));
        edgeConverters.add(new GraphicsConverter<Color>("edgelabelcolor", Color.class, true));
        edgeConverters.add(new GraphicsConverter<Integer>("edgelabelopacity", Integer.class, true));
        edgeConverters.add(new GraphicsConverter<Double>("edgelabelwidth", Double.class, true));
        edgeConverters.add(new GraphicsConverter<Object>("edgesourcearrow", Object.class, true));
        edgeConverters.add(new GraphicsConverter<Color>("edgesourcearrowcolor", Color.class, true));
        edgeConverters.add(new GraphicsConverter<Integer>("edgesourcearrowopacity", Integer.class, true));
        edgeConverters.add(new GraphicsConverter<Object>("edgetargetarrow", Object.class, true));
        edgeConverters.add(new GraphicsConverter<Color>("edgetargetarrowcolor", Color.class, true));
        edgeConverters.add(new GraphicsConverter<Integer>("edgetargetarrowopacity", Integer.class, true));
        edgeConverters.add(new GraphicsConverter<String>("edgetooltip", String.class, true));
    }
}

class GraphicsConverter<T> {

    public String key;
    public Class<T> type;
    public String namespace;
    public boolean bypass;

    private static final Logger logger = LoggerFactory.getLogger(GraphicsConverter.class);

    public GraphicsConverter(String key, Class<T> type) {
        this(key, type, false);
    }

    public GraphicsConverter(String key, Class<T> type, boolean bypass) {
        this.key = key;
        this.type = type;
        this.bypass = bypass;
    }

    public GraphicsConverter(String key, Class<T> type, String namespace) {
        this(key, type, false);
        this.namespace = namespace;
    }

    public T getValue(final Attributes attr, final AttributeValueUtil attributeValueUtil, final VisualProperty<T> vp) {
        Object value = null;

        if (attr != null) {
            if (type == Color.class) {
                value = attributeValueUtil.getColorAttribute(attr, key);
            } else if (type == Font.class) {
                String name = null;
                boolean bold = false;
                String sSize = null;

                if (isBypass()) {
                    String s = attributeValueUtil.getAttribute(attr, key);

                    if (s != null) {
                        // e.g. "Monospaced,plain,12"
                        name = s.replaceAll(",[a-z]+,d+", "");
                        sSize = s.replaceAll("[^,]+,[a-z]+,", "");
                        bold = s.matches("(?i)[^,]+,bold,[^,]+");
                    }
                } else {
                    String s = attributeValueUtil.getAttributeNS(attr, key, namespace);

                    if (s != null) {
                        // e.g. "SansSerif.bold-0-14"
                        name = s.replaceAll("(\\.[bB]old)?-\\d+(\\.\\d+)?-\\d+(\\.\\d+)?", "");
                        bold = s.matches("(?i).*\\.bold-.*");
                        sSize = s.replaceAll(".+-[^\\-]+-", "");
                    }
                }

                if (name != null) {
                    int style = bold ? Font.BOLD : Font.PLAIN;
                    int size = 12;

                    try {
                        size = Integer.parseInt(sSize);
                    } catch (NumberFormatException nfe) {
                        // TODO: log
                    }

                    value = new Font(name, style, size);
                }
            } else {
                // String, Number or Object...
                String s = null;

                if (namespace == null)
                    s = attributeValueUtil.getAttribute(attr, key);
                else
                    s = attributeValueUtil.getAttributeNS(attr, key, namespace);

                if (type == String.class) {
                    value = s;
                } else if (s != null && Number.class.isAssignableFrom(type)) {
                    Double d = null;

                    try {
                        value = d = Double.parseDouble(s);
                    } catch (NumberFormatException nfe) {
                        logger.error("Cannot convert \"" + key + "\" from \"" + s + "\" to Double");
                    }

                    if (d != null) {
                        if (type == Integer.class) {
                            if (isTransparency() && !isBypass()) {
                                // Opacity is saved as a float from 0.0-1.0, but internally we use 0-255
                                value = new Integer((int) (d.doubleValue() * 255));
                            } else {
                                value = new Integer((int) d.doubleValue());
                            }
                        } else if (type == Float.class) {
                            value = new Float(d.floatValue());
                        }
                    }
                } else if (s != null) {
                    value = vp.parseSerializableString(s);
                }
            }
        }

        return (T) value;
    }

    public boolean isTransparency() {
        return key.matches("(?i)[a-z]*(transparency|opacity)[a-z]*");
    }

    public String getKey() {
        return key;
    }

    public Class<T> getType() {
        return type;
    }

    public boolean isBypass() {
        return bypass;
    }

    public String getNamespace() {
        return namespace;
    }
}
