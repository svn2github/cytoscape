/*
 Copyright (c) 2006, 2010 The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.io.internal.write.xgmml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.TwoDVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

enum GraphicsType {
    ARC("arc"),
    BITMAP("bitmap"),
    IMAGE("image"),
    LINE("line"),
    OVAL("oval"),
    POLYGON("polygon"),
    RECTANGLE("rectangle"),
    TEXT("text"),
    BOX("box"),
    CIRCLE("circle"),
    VER_ELLIPSIS("ver_ellipsis"),
    HOR_ELLIPSIS("hor_ellipsis"),
    RHOMBUS("rhombus"),
    TRIANGLE("triangle"),
    PENTAGON("pentagon"),
    HEXAGON("hexagon"),
    OCTAGON("octagon"),
    ELLIPSE("ellipse"),
    DIAMOND("diamond"),
    PARALLELOGRAM("parallelogram"),
    ROUNDED_RECTANGLE("rounded_rectangle");

    private final String value;

    GraphicsType(String v) {
        value = v;
    }

    String value() {
        return value;
    }
}

enum ObjectType {
    LIST("list"),
    STRING("string"),
    REAL("real"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    MAP("map"),
    COMPLEX("complex");

    private final String value;

    ObjectType(String v) {
        value = v;
    }

    String value() {
        return value;
    }

    static ObjectType fromValue(String v) {
        for (ObjectType c : ObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

    public String toString() {
        return value;
    }
}

public class XGMMLWriter extends AbstractTask implements CyWriter {

    // XML preamble information
    public static final String ENCODING = "UTF-8";
    private static final String XML_STRING = "<?xml version=\"1.0\" encoding=\"" + ENCODING + "\" standalone=\"yes\"?>";

    private static final String[] NAMESPACES = { "xmlns:dc=\"http://purl.org/dc/elements/1.1/\"",
            "xmlns:xlink=\"http://www.w3.org/1999/xlink\"",
            "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"", "xmlns:cy=\"http://www.cytoscape.org\"",
            "xmlns=\"http://www.cs.rpi.edu/XGMML\"" };

    // File format version. For compatibility.
    private static final String FORMAT_VERSION = "documentVersion";
    private static final float VERSION = (float) 1.1;
    private static final String METADATA_NAME = "networkMetadata";
    private static final String METADATA_ATTR_NAME = "Network Metadata";

    // Node types
    protected static final String NORMAL = "normal";
    protected static final String METANODE = "group";
    protected static final String REFERENCE = "reference";

    // Object types
    protected static final int NODE = 1;
    protected static final int EDGE = 2;
    protected static final int NETWORK = 3;

    public static final String BACKGROUND = "backgroundColor";
    public static final String GRAPH_VIEW_ZOOM = "GRAPH_VIEW_ZOOM";
    public static final String GRAPH_VIEW_CENTER_X = "GRAPH_VIEW_CENTER_X";
    public static final String GRAPH_VIEW_CENTER_Y = "GRAPH_VIEW_CENTER_Y";
    public static final String ENCODE_PROPERTY = "cytoscape.encode.xgmml.attributes";

    private final OutputStream outputStream;
    private final CyNetwork network;
    private final VisualLexicon visualLexicon;
    private final CyNetworkView networkView;

    private boolean isMixed;
    private HashMap<CyNode, CyNode> nodeMap = new HashMap<CyNode, CyNode>();
    private HashMap<CyEdge, CyEdge> edgeMap = new HashMap<CyEdge, CyEdge>();
    private boolean noCytoscapeGraphics = false;

    private int depth = 0; // XML
    // depth
    private String indentString = "";
    private Writer writer = null;

    private boolean doFullEncoding;

    public XGMMLWriter(final OutputStream outputStream,
                       final RenderingEngineManager renderingEngineManager,
                       final CyNetworkView networkView,
                       final boolean noCytoscapeGraphics) {
        this.outputStream = outputStream;
        this.networkView = networkView;
        this.network = networkView.getModel();
        this.noCytoscapeGraphics = noCytoscapeGraphics;
        this.visualLexicon = renderingEngineManager.getDefaultVisualLexicon();

        // Create our indent string (480 blanks);
        for (int i = 0; i < 20; i++)
            indentString += "                        ";

        doFullEncoding = Boolean.valueOf(System.getProperty(ENCODE_PROPERTY, "true"));
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        writer = new OutputStreamWriter(outputStream);

        // write out the XGMML preamble
        writePreamble();
        depth++;

        // write out our metadata
        writeMetadata();

        // write out network attributes
        writeNetworkAttributes();

        // Output our nodes
        writeNodes();
        // TODO obviously, fix this
        // writeGroups();

        // Create edge objects
        writeEdges();

        depth--;
        // Wwrite final tag
        writeElement("</graph>\n");

        writer.flush();
    }

    /**
     * Output the XML preamble.  This includes the XML line as well as the initial
     * &lt;graph&gt; element, along with all of our namespaces.
     *
     * @throws IOException
     */
    private void writePreamble() throws IOException {
        String directed = getDirectionality();
        writeElement(XML_STRING + "\n");
        writeElement("<graph label=\"" + getNetworkName(network) + "\" directed=\"" + directed + "\" ");
        for (int ns = 0; ns < NAMESPACES.length; ns++)
            writer.write(NAMESPACES[ns] + " ");
        writer.write(">\n");
    }

    /**
     * Check directionality of edges, return directionality string to use in xml
     * file as attribute of graph element.
     *
     * Set isMixed field true if network is a mixed network (contains directed
     * and undirected edges), and false otherwise (if only one type of edges are
     * present.)
     *
     * @returns flag to use in XGMML file for graph element's 'directed'
     *          attribute
     */
    private String getDirectionality() {
        boolean seen_directed = false;
        boolean seen_undirected = false;

        for (CyEdge edge : network.getEdgeList()) {
            if (edge.isDirected())
                seen_directed = true;
            else
                seen_undirected = true;
        }

        if (seen_undirected && seen_directed)
            isMixed = true;
        else
            isMixed = false;

        if ((!seen_directed) && seen_undirected)
            return "0"; // only undir. edges
        else
            return "1"; // either only directed or mixed. For both cases, use
        // dir. as default
    }

    /**
     * Output the network metadata.  This includes our format version and our RDF
     * data.
     *
     * @throws IOException
     */
    private void writeMetadata() throws IOException {
        writeElement("<att name=\"" + FORMAT_VERSION + "\" value=\"" + VERSION + "\"/>\n");
        writeElement("<att name=\"networkMetadata\">\n");
        depth++;
        writeRDF();
        depth--;
        writeElement("</att>\n");
    }

    /**
     * Output the RDF information for this network.
     *     <rdf:RDF>
     *         <rdf:Description rdf:about="http://www.cytoscape.org/">
     *             <dc:type>Protein-Protein Interaction</dc:type>
     *             <dc:description>N/A</dc:description>
     *             <dc:identifier>N/A</dc:identifier>
     *             <dc:date>2007-01-16 13:29:50</dc:date>
     *             <dc:title>Amidohydrolase Superfamily--child</dc:title>
     *             <dc:source>http://www.cytoscape.org/</dc:source>
     *             <dc:format>Cytoscape-XGMML</dc:format>
     *         </rdf:Description>
     *     </rdf:RDF>
     *
     * @throws IOException
     */
    private void writeRDF() throws IOException {
        writeElement("<rdf:RDF>\n");
        depth++;
        writeElement("<rdf:Description rdf:about=\"http://www.cytoscape.org/\">\n");
        depth++;
        writeElement("<dc:type>Protein-Protein Interaction</dc:type>\n");
        writeElement("<dc:description>N/A</dc:description>\n");
        writeElement("<dc:identifier>N/A</dc:identifier>\n");
        java.util.Date now = new java.util.Date();
        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        writeElement("<dc:date>" + df.format(now) + "</dc:date>\n");
        // TODO fix the use of hardcoded "name" here
        writeElement("<dc:title>" + getNetworkName(network) + "</dc:title>\n");
        writeElement("<dc:source>http://www.cytoscape.org/</dc:source>\n");
        writeElement("<dc:format>Cytoscape-XGMML</dc:format>\n");
        depth--;
        writeElement("</rdf:Description>\n");
        depth--;
        writeElement("</rdf:RDF>\n");
    }

    /**
     * Output any network attributes we have defined, including
     * the network graphics information we encode as attributes:
     * backgroundColor, zoom, and the graph center.
     *
     * @throws IOException
     */
    private void writeNetworkAttributes() throws IOException {
        if (networkView != null) {
            // Get our background color
            Paint paint = networkView.getVisualProperty(TwoDVisualLexicon.NETWORK_BACKGROUND_PAINT);
            String bgColor = paint2string(paint, Color.WHITE);

            writeElement("<att type=\"string\" name=\"" + BACKGROUND + "\" value=\"" + bgColor + "\"/>\n");

            // Write the graph zoom
            final Double zoom = networkView.getVisualProperty(TwoDVisualLexicon.NETWORK_SCALE_FACTOR);
            writeAttributeXML(GRAPH_VIEW_ZOOM, ObjectType.REAL, zoom, true);

            // Write the graph center
            final Double cx = networkView.getVisualProperty(TwoDVisualLexicon.NETWORK_CENTER_X_LOCATION);
            writeAttributeXML(GRAPH_VIEW_CENTER_X, ObjectType.REAL, cx, true);

            final Double cy = networkView.getVisualProperty(TwoDVisualLexicon.NETWORK_CENTER_Y_LOCATION);
            writeAttributeXML(GRAPH_VIEW_CENTER_Y, ObjectType.REAL, cy, true);

            // TODO: Figure out if our node height and width is locked
            //            VisualStyle networkStyle = Cytoscape.getCurrentNetworkView().getVisualStyle();
            //            VisualPropertyDependency vpd = networkStyle.getDependency();
            //            if (vpd.check(VisualPropertyDependency.Definition.NODE_SIZE_LOCKED))
            //                writeAttributeXML(NODE_SIZE_LOCKED, ObjectType.BOOLEAN, Boolean.TRUE ,true);
        }

        // Now handle all of the other network attributes
        CyRow row = network.getCyRow();
        CyTable table = row.getTable();

        for (final CyColumn column : table.getColumns())
            writeAttribute(row, column.getName());
    }

    /**
     * Output Cytoscape nodes as XGMML
     *
     * @throws IOException
     */
    private void writeNodes() throws IOException {
        for (CyNode node : network.getNodeList()) {
            // TODO
            // if (!CyGroupManager.isaGroup(curNode))
            writeNode(node, null);
        }
    }

    /**
     * Output a single CyNode as XGMML
     *
     * @param node the node to output
     * @throws IOException
     */
    private void writeNode(CyNode node, List<CyNode> groupList) throws IOException {
        // Remember that we've seen this node
        nodeMap.put(node, node);

        // Output the node
        writeElement("<node label=" + quote(node.getCyRow().get("name", String.class)));
        writer.write(" id=" + quote(Integer.toString(node.getIndex())) + ">\n");
        depth++;

        // Output the node attributes
        // TODO This isn't handling namespaces
        for (final CyColumn column : node.getCyRow().getTable().getColumns())
            writeAttribute(node.getCyRow(), column.getName());

        // TODO deal with groups
        //        if (groupList != null && groupList.size() > 0) {
        //            // If we're a group, output the graph attribute now
        //            writeElement("<att>\n");
        //            depth++;
        //            writeElement("<graph>\n");
        //            depth++;
        //            for (CyNode childNode : groupList) {
        //                if (CyGroupManager.isaGroup(childNode)) {
        //                    // We have an embeddedgroup -- recurse
        //                    CyGroup childGroup = CyGroupManager.getCyGroup(childNode);
        //                    writeNode(childGroup.getGroupNode(), childGroup.getNodes());
        //                } else {
        //                    if (nodeMap.containsKey(childNode))
        //                        writeElement("<node xlink:href=\"#" + childNode.getIndex() + "\"/>\n");
        //                    else
        //                        writeNode(childNode, null);
        //                }
        //            }
        //            depth--;
        //            writeElement("</graph>\n");
        //            depth--;
        //            writeElement("</att>\n");
        //        }

        // Output the node graphics if we have a view
        if (networkView != null) writeGraphics(networkView.getNodeView(node));

        depth--;
        writeElement("</node>\n");
    }

    /*
     * // TODO fix! private void writeGroups() throws IOException { // Two pass
     * approach. First, walk through the list // and see if any of the children
     * of a group are // themselves a group. If so, remove them from // the list
     * & will pick them up on recursion groupList =
     * CyGroupManager.getGroupList();
     *
     * if ((groupList == null) || groupList.isEmpty()) return;
     *
     * HashMap<CyGroup,CyGroup> embeddedGroupList = new
     * HashMap<CyGroup,CyGroup>();
     *
     * for (CyGroup group: groupList) { List<CyNode> childList =
     * group.getNodes();
     *
     * if ((childList == null) || (childList.size() == 0)) continue;
     *
     * for (CyNode childNode: childList) { if
     * (CyGroupManager.isaGroup(childNode)) { // Get the actual group CyGroup
     * embGroup = CyGroupManager.getCyGroup(childNode);
     * embeddedGroupList.put(embGroup, embGroup); } } }
     *
     * for (CyGroup group: groupList) { // Is this an embedded group? if
     * (embeddedGroupList.containsKey(group)) continue; // Yes, skip it
     *
     * writeGroup(group); } }
     *
     * private void writeGroup(CyGroup group) throws IOException { CyNode
     * groupNode = group.getGroupNode(); writeNode(groupNode, group.getNodes());
     * }
     */
    /**
     * Output Cytoscape edges as XGMML
     *
     * @throws IOException
     */
    private void writeEdges() throws IOException {
        for (CyEdge edge : network.getEdgeList()) {
            edgeMap.put(edge, edge);
            writeEdge(edge);
        }
    }

    /**
     * Output a Cytoscape edge as XGMML
     *
     * @param curEdge the edge to output
     *
     * @throws IOException
     */
    private void writeEdge(CyEdge curEdge) throws IOException {
        // Write the edge
        String target = quote(Integer.toString(curEdge.getTarget().getIndex()));
        String source = quote(Integer.toString(curEdge.getSource().getIndex()));

        // Make sure these nodes exist
        if (!nodeMap.containsKey(curEdge.getTarget()) || !nodeMap.containsKey(curEdge.getSource())) return;

        String directed = quote(curEdge.isDirected() ? "1" : "0");

        writeElement("<edge label=" + quote(curEdge.getCyRow().get("name", String.class)) + " source=" + source +
                     " target=" + target + " cy:directed=" + directed + ">\n");

        depth++;

        // Write the edge attributes
        // TODO This isn't handling namespaces
        for (final CyColumn column : curEdge.getCyRow().getTable().getColumns())
            writeAttribute(curEdge.getCyRow(), column.getName());

        // Write the edge graphics
        if (networkView != null) writeGraphics(networkView.getEdgeView(curEdge));

        depth--;
        writeElement("</edge>\n");
    }

    private void writeGraphics(View<? extends CyTableEntry> view) throws IOException {
        if (view == null) return;
        CyTableEntry element = view.getModel();

        writeElement("<graphics");

        Set<VisualProperty<?>> visualProperties = visualLexicon.getAllVisualProperties();

        for (VisualProperty vp : visualProperties) {
            if (!vp.getTargetDataType().isAssignableFrom(element.getClass())) continue;
            
            String key = getGraphicsKey(vp);
            Object value = view.getVisualProperty(vp);

            if (key != null && value != null) {
                writeAttributePair(key, vp.toSerializableString(value));
            }
        }

        // TODO: Handle bends
        if (element instanceof CyEdge) {
            //   final Bend bendData = edgeView.getBend();
            //   final List<Point2D> handles = new ArrayList<Point2D>(); //final List<Point2D> handles = bendData.getHandles();
            //
            //   if (handles.size() == 0) {
            //       writer.write("/>\n");
            //   } else {
            //       writer.write(">\n");
            //       depth++;
            //       writeElement("<att name=\"edgeBend\">\n");
            //       depth++;
            //       for (Point2D handle: handles) {
            //           String x = Double.toString(handle.getX());
            //           String y = Double.toString(handle.getY());
            //           writeElement("<att name=\"handle\" x=\""+x+"\" y=\""+y+"\" />\n");
            //       }
            //       depth--;
            //       writeElement("</att>\n");
            //       depth--;
            //       writeElement("</graphics>\n");
            //   }
        }

        writer.write("/>\n");
    }

    private String getGraphicsKey(VisualProperty<?> vp) {
        //Nodes
        if (vp.equals(TwoDVisualLexicon.NODE_X_LOCATION)) return "x";
        if (vp.equals(TwoDVisualLexicon.NODE_Y_LOCATION)) return "y";
        if (vp.equals(TwoDVisualLexicon.NODE_X_SIZE)) return "w";
        if (vp.equals(TwoDVisualLexicon.NODE_Y_SIZE)) return "h";
        if (vp.equals(TwoDVisualLexicon.NODE_COLOR)) return "fill";
        // TODO: Visual Lexicon has to expose these properties
        //        if (vp.equals(TwoDVisualLexicon.NODE_SHAPE)) return "type";
        //        if (vp.equals(TwoDVisualLexicon.NODE_)) return "wth";
        //        if (vp.equals(TwoDVisualLexicon.NODE_)) return "outline";
        //        if (vp.equals(TwoDVisualLexicon.NODE_)) return "cy:nodeTransparency";
        //        if (vp.equals(TwoDVisualLexicon.NODE_)) return "cy:borderLineType";
        //        if (vp.equals(TwoDVisualLexicon.NODE_)) return "cy:nodeLabelFont";

        // Edges
        if (vp.equals(TwoDVisualLexicon.EDGE_WIDTH)) return "width";
        if (vp.equals(TwoDVisualLexicon.EDGE_PAINT)) return "fill";
        // TODO:
        //            "cy:sourceArrow"
        //            "cy:targetArrow"
        //            "cy:sourceArrowColor"
        //            "cy:targetArrowColor"
        //            "cy:edgeLabelFont"
        //            "cy:edgeLineType"

        return null;
    }

    /**
     * Creates an attribute to write into XGMML file.
     *
     * @param id -
     *            id of node, edge or network
     * @param row -
     *            CyRow to load
     * @param attName -
     *            attribute name
     * @return att - Att to return (gets written into xgmml file - CAN BE NULL)
     *
     * @throws IOException
     */
    private void writeAttribute(final CyRow row, final String attName) throws IOException {
        // create an attribute and its type:
        final CyColumn column = row.getTable().getColumn(attName);
        if (column == null) return;
        final Class<?> attType = column.getType();

        String value = null;
        String type = null;

        // TODO: Equations
        //        final Equation equation = row.getEquation(id, attributeName);

        if (attType == Double.class) {
            //            if (equation != null) {
            //                writeEquationAttributeXML(attName, ObjectType.REAL, equation.toString(),
            //                                          true, hidden, editable);
            //            } else {
            Double dAttr = row.get(attName, Double.class);
            writeAttributeXML(attName, ObjectType.REAL, dAttr, true);
            //            }
        } else {
            if (attType == Integer.class) {
                //                if (equation != null) {
                //                    writeEquationAttributeXML(attName, ObjectType.INTEGER, equation.toString(), true, hidden, editable);
                //                } else {
                Integer iAttr = row.get(attName, Integer.class);
                writeAttributeXML(attName, ObjectType.INTEGER, iAttr, true);
                //                }
            } else if (attType == String.class) {
                //                if (equation != null) {
                //                    writeEquationAttributeXML(attName, ObjectType.STRING, equation.toString(),
                //                                              true, hidden, editable);
                //                } else {
                String sAttr = row.get(attName, String.class);
                // Protect tabs and returns
                if (sAttr != null) {
                    sAttr = sAttr.replace("\n", "\\n");
                    sAttr = sAttr.replace("\t", "\\t");
                }
                // TODO: nested networks
                //                if (attName.equals(CyNode.NESTED_NETWORK_ID_ATTR)) {
                //                    // This is a special attribute for nested network.
                //                    sAttr = Cytoscape.getNetwork(sAttr).getTitle();
                //                }
                writeAttributeXML(attName, ObjectType.STRING, sAttr, true);
                //                }
            } else if (attType == Boolean.class) {
                //                if (equation != null) {
                //                    writeEquationAttributeXML(attName, ObjectType.BOOLEAN, equation.toString(), true, hidden, editable);
                //                } else {
                Boolean bAttr = row.get(attName, Boolean.class);
                writeAttributeXML(attName, ObjectType.BOOLEAN, bAttr, true);
                //                }
            } else if (attType == List.class) {
                final List<?> listAttr = row.getList(attName, column.getListElementType());
                writeAttributeXML(attName, ObjectType.LIST, null, false);

                if (listAttr != null) {
                    depth++;
                    // interate through the list
                    for (Object obj : listAttr) {
                        // Protect tabs and returns (if necessary)
                        String sAttr = obj.toString();
                        if (sAttr != null) {
                            sAttr = sAttr.replace("\n", "\\n");
                            sAttr = sAttr.replace("\t", "\\t");
                        }
                        // set child attribute value & label
                        writeAttributeXML(attName, checkType(obj), sAttr, true);
                    }
                    depth--;
                }
                writeAttributeXML(null, null, null, true);
            } else if (attType == Map.class) {
                // process SIMPLE MAP
                // get the attribute map
                final Map mapAttr = row.get(attName, Map.class);
                writeAttributeXML(attName, ObjectType.MAP, null, false);

                if (mapAttr != null) {
                    depth++;
                    // interate through the map
                    for (Object obj : mapAttr.keySet()) {
                        // get the attribute from the map
                        String key = (String) obj;
                        Object val = mapAttr.get(key);
                        String sAttr = val.toString();
                        if (sAttr != null) {
                            sAttr = sAttr.replace("\n", "\\n");
                            sAttr = sAttr.replace("\t", "\\t");
                        }

                        writeAttributeXML(key, checkType(val), sAttr, true);
                    }
                    depth--;
                }
                writeAttributeXML(null, null, null, true);
            }
        }

        // TODO: process COMPLEX MAP
        //        else if (attType == CyAttributes.TYPE_COMPLEX) {
        //            MultiHashMap mmap = attributes.getMultiHashMap();
        //            MultiHashMapDefinition mmapDef = attributes.getMultiHashMapDefinition();
        //
        //            // get the number & types of dimensions
        //            byte[] dimTypes = mmapDef.getAttributeKeyspaceDimensionTypes(attName);
        //
        //            // Check to see if id has value assigned to attribute
        //            if (!objectHasKey(id, attributes, attName)) {
        //                return;
        //            }
        //            // Output the first <att>
        //            writeAttributeXML(attName, ObjectType.COMPLEX, String.valueOf(dimTypes.length), false, hidden, editable);
        //
        //            // grab the complex attribute structure
        //            Map complexAttributeStructure = getComplexAttributeStructure(mmap, id, attributeName, null,
        //                                                                         0, dimTypes.length);
        //
        //            // determine val type, get its string equivalent to store in XGMML
        //            ObjectType valType = getType(mmapDef.getAttributeValueType(attributeName));
        //
        //            depth++;
        //            // walk the structure
        //            writeComplexAttribute(complexAttributeStructure, valType, dimTypes, 0);
        //            depth--;
        //            // Close
        //            writeAttributeXML(null, null, null, true);
        //        }
    }

    /**
     * Returns a map where the key(s) are each key in the attribute key space,
     * and the value is another map or the attribute value.
     *
     * For example, if the following key:
     *
     * {externalref1}{authors}{1} pointed to the following value:
     *
     * "author 1 name",
     *
     * Then we would have a Map where the key is externalref1, the value is a
     * Map where the key is {authors}, the value is a Map where the key is {1},
     * the value is "author 1 name".
     *
     * @param mmap -
     *            reference to MultiHashMap used by CyAttributes
     * @param id -
     *            id of node, edge or network
     * @param attributeName -
     *            name of attribute
     * @param keys -
     *            array of objects which store attribute keys
     * @param keysIndex -
     *            index into keys array we should add the next key
     * @param numKeyDimensions -
     *            the number of keys used for given attribute name
     * @return Map - ref to Map interface
    private Map<Object,Object> getComplexAttributeStructure(MultiHashMap mmap, String id, String attributeName,
                                             Object[] keys, int keysIndex, int numKeyDimensions) {
    	// are we done?
    	if (keysIndex == numKeyDimensions)
    		return null;

    	// the hashmap to return
    	Map<Object,Object> keyHashMap = new HashMap<Object,Object>();

    	// create a new object array to store keys for this interation
    	// copy all existing keys into it
    	Object[] newKeys = new Object[keysIndex + 1];

    	for (int lc = 0; lc < keysIndex; lc++) {
    		newKeys[lc] = keys[lc];
    	}

    	// get the key span
    	Iterator keyspan = mmap.getAttributeKeyspan(id, attributeName, keys);

    	while (keyspan.hasNext()) {
    		Object newKey = keyspan.next();
    		newKeys[keysIndex] = newKey;

    		Map nextLevelMap = getComplexAttributeStructure(mmap, id, attributeName, newKeys,
    		                                                keysIndex + 1, numKeyDimensions);
    		Object objectToStore = (nextLevelMap == null)
    		                       ? mmap.getAttributeValue(id, attributeName, newKeys) : nextLevelMap;
    		keyHashMap.put(newKey, objectToStore);
    	}
    	return keyHashMap;
    }
     */

    /**
     * This method is a recursive routine to output a complex attribute.
     *
     * @param complexAttributeStructure the structure of the attribute
     * @param type the type of the attribute
     * @param dimTypes the array of dimension types
     * @param dimTypesIndex which dimType we're working on
    private void writeComplexAttribute(Map complexAttributeStructure, ObjectType type,
                                       byte[] dimTypes, int dimTypesIndex) throws IOException {
    	for (Object key: complexAttributeStructure.keySet()) {
    		Object possibleAttributeValue = complexAttributeStructure.get(key);

    		// Is this a leaf or are we still dealing with maps?
    		if (possibleAttributeValue instanceof Map) {
    			// Another map
    			writeAttributeXML(key.toString(), getType(dimTypes[dimTypesIndex]),
    			                  String.valueOf(((Map) possibleAttributeValue).size()), false);
    			// Recurse
    			depth++;
    			writeComplexAttribute((Map)possibleAttributeValue, type, dimTypes, dimTypesIndex+1);
    			depth--;
    			// Close
    			writeAttributeXML(null, null, null, true);
    		} else {
    			// Final key
    			writeAttributeXML(key.toString(), getType(dimTypes[dimTypesIndex]),
    			                  String.valueOf(1), false);
    			depth++;
    			writeAttributeXML(null, type, possibleAttributeValue.toString(), true);
    			depth--;
    			writeAttributeXML(null, null, null, true);
    		}
    	}
    }
     */

    /**
     * writeAttributeXML outputs an XGMML attribute
     *
     * @param name is the name of the attribute we are outputting
     * @param type is the XGMML type of the attribute
     * @param value is the value of the attribute we're outputting
     * @param end is a flag to tell us if the attribute should include a tag end
     *
     * @throws IOException
     */
    private void writeAttributeXML(String name, ObjectType type, Object value, boolean end) throws IOException {
        if (name == null && type == null)
            writeElement("</att>\n");
        else {
            writeElement("<att type=" + quote(type.toString()));

            if (name != null) writer.write(" name=" + quote(name));
            if (value != null) writer.write(" value=" + quote(value.toString()));

            if (end)
                writer.write("/>\n");
            else
                writer.write(">\n");
        }
    }

    /**
     * writeAttributePair outputs the name,value pairs for an attribute
     *
     * @param name is the name of the attribute we are outputting
     * @param value is the value of the attribute we're outputting
     *
     * @throws IOException
     */
    private void writeAttributePair(String name, Object value) throws IOException {
        writer.write(" " + name + "=" + quote(value.toString()));
    }

    /**
     * writeElement outputs the name,value pairs for an attribute
     *
     * @param line is the element string to output
     *
     * @throws IOException
     */
    private void writeElement(String line) throws IOException {
        while (depth * 2 > indentString.length() - 1)
            indentString = indentString + "                        ";
        writer.write(indentString, 0, depth * 2);
        writer.write(line);
    }

    /**
     * Convert enumerated shapes into human-readable string.<br>
     *
     * @param type
     *            Enumerated node shape.
     * @return Shape in string.
     */
    private GraphicsType number2shape(final int type) {
        switch (type) {
            // TODO:
            //            case NodeView.ELLIPSE:
            //                return GraphicsType.ELLIPSE;
            //            case NodeView.RECTANGLE:
            //                return GraphicsType.RECTANGLE;
            //            case NodeView.ROUNDED_RECTANGLE:
            //                return GraphicsType.ROUNDED_RECTANGLE;
            //            case NodeView.DIAMOND:
            //                return GraphicsType.DIAMOND;
            //            case NodeView.HEXAGON:
            //                return GraphicsType.HEXAGON;
            //            case NodeView.OCTAGON:
            //                return GraphicsType.OCTAGON;
            //            case NodeView.PARALELLOGRAM:
            //                return GraphicsType.PARALLELOGRAM;
            //            case NodeView.TRIANGLE:
            //                return GraphicsType.TRIANGLE;
            //            case NodeView.VEE:
            //                return GraphicsType.VEE;
            default:
                return null;
        }
    }

    /**
     * Convert color (paint) to RGB string.<br>
     *
     * @param paint Paint object to be converted.
     * @param defColor An optional default color, in case paint is not a simple Color.
     * @return Color in RGB string.
     */
    private String paint2string(final Paint paint, Color defColor) {
        Color c = null;

        if (paint instanceof Color)
            c = (Color) paint;
        else
            c = defColor == null ? Color.WHITE : defColor;

        return ("#" // +Integer.toHexString(c.getRGB());
                +
                Integer.toHexString(256 + c.getRed()).substring(1) +
                Integer.toHexString(256 + c.getGreen()).substring(1) + Integer.toHexString(256 + c.getBlue())
                .substring(1));
    }

    /**
     * Encode font into a human-readable string.<br>
     *
     * @param font
     *            Font object.
     * @return String extracted from the given Font object.
     */
    private String encodeFont(final Font font) {
        // Encode font into "fontname-style-pointsize" string
        return font.getName() + "-" + font.getStyle() + "-" + font.getSize();
    }

    /**
     * Check the type of Attributes.
     *
     * @param obj
     * @return Attribute type in string.
     *
     */
    private ObjectType checkType(final Object obj) {
        if (obj.getClass() == String.class) {
            return ObjectType.STRING;
        } else if (obj.getClass() == Integer.class) {
            return ObjectType.INTEGER;
        } else if ((obj.getClass() == Double.class) || (obj.getClass() == Float.class)) {
            return ObjectType.REAL;
        } else if (obj.getClass() == Boolean.class) {
            return ObjectType.BOOLEAN;
        } else {
            return null;
        }
    }

    /**
     * Given a byte describing a MultiHashMapDefinition TYPE_*, return the
     * proper XGMMLWriter type.
     *
     * @param dimType -
     *            byte as described in MultiHashMapDefinition
     * @return the type pointed to by this dim
    private ObjectType getType(final byte dimType) {
    	if (dimType == MultiHashMapDefinition.TYPE_BOOLEAN)
    		return ObjectType.BOOLEAN;

    	if (dimType == MultiHashMapDefinition.TYPE_FLOATING_POINT)
    		return ObjectType.REAL;

    	if (dimType == MultiHashMapDefinition.TYPE_INTEGER)
    		return ObjectType.INTEGER;

    	if (dimType == MultiHashMapDefinition.TYPE_STRING)
    		return ObjectType.STRING;

    	// houston we have a problem
    	return null;
    }
     */

    private String getNetworkName(CyNetwork network) {
        String name = encode(network.getCyRow().get("name", String.class));
        if (name == null) name = "UNDEFINED";

        return name;
    }

    /**
     * encode returns a quoted string appropriate for use as an XML attribute
     *
     * @param str the string to encode
     * @return the encoded string
     */
    private String encode(String str) {
        // Find and replace any "magic", control, non-printable etc. characters
        // For maximum safety, everything other than printable ASCII (0x20 thru 0x7E) is converted into a character entity
        String s = null;

        if (str != null) {
            StringBuilder sb = new StringBuilder(str.length());

            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);

                if ((c < ' ') || (c > '~')) {
                    if (doFullEncoding) {
                        sb.append("&#x");
                        sb.append(Integer.toHexString((int) c));
                        sb.append(";");
                    } else {
                        sb.append(c);
                    }
                } else if (c == '"') {
                    sb.append("&quot;");
                } else if (c == '\'') {
                    sb.append("&apos;");
                } else if (c == '&') {
                    sb.append("&amp;");
                } else if (c == '<') {
                    sb.append("&lt;");
                } else if (c == '>') {
                    sb.append("&gt;");
                } else {
                    sb.append(c);
                }
            }

            s = sb.toString();
        }

        return s;
    }

    /**
     * quote returns a quoted string appropriate for use as an XML attribute
     *
     * @param str the string to quote
     * @return the quoted string
     */
    private String quote(String str) {
        return '"' + encode(str) + '"';
    }

    /**
     * Determines if object has key in multihashmap
     *
     * @param id -
     *            node, edge, network id
     * @param attributes -
     *            CyAttributes ref
     * @param attributeName -
     *            attribute name
     *
     * @return boolean
    private boolean objectHasKey(String id, CyAttributes attributes, String attributeName) {
    	MultiHashMap mmap = attributes.getMultiHashMap();

    	for (Iterator keysIt = mmap.getObjectKeys(attributeName); keysIt.hasNext();) {
    		String thisKey = (String) keysIt.next();

    		if ((thisKey != null) && thisKey.equals(id)) {
    			return true;
    		}
    	}
    	return false;
    }
     */
}
