/*
 File: VisualStyleBuilder.java

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
package cytoscape.data.readers;

import cytoscape.Cytoscape;

import cytoscape.generated2.Att;
import cytoscape.generated2.Graphics;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.NodeShape;
import cytoscape.visual.LineStyle;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.calculators.*;

import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import giny.view.EdgeView;

import java.awt.Color;
import java.awt.Font;

import java.math.BigInteger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Based on the graph/node/edge view information, build new Visual Style.
 *
 * This class accepts JAXB object called "Graphics" as input value. We can add
 * information by adding elements to the object as attributes (Object
 * cytoscape.generated2.Att)
 *
 * @author kono
 *
 */
public class VisualStyleBuilder {
	protected static final NodeShape DEFAULT_SHAPE = NodeShape.ELLIPSE;
	protected static final Color DEFAULT_COLOR = Color.WHITE;
	protected static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	protected static final float DEFAULT_LINE_WIDTH = 1.0f;
	protected static final int NOT_AN_ARROW = Integer.MIN_VALUE;

	// Name for the new visual style
	/**
	 * @uml.property  name="styleName"
	 */
	private String styleName;

	// New Visual Style comverted from GML file.
	/**
	 * @uml.property  name="xgmmlStyle"
	 * @uml.associationEnd
	 */
	private VisualStyle xgmmlStyle;

	// Node appearence
	/**
	 * @uml.property  name="nac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private NodeAppearanceCalculator nac;

	// Edge appearence
	/**
	 * @uml.property  name="eac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private EdgeAppearanceCalculator eac;

	// Global appearence
	/**
	 * @uml.property  name="gac"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GlobalAppearanceCalculator gac;

	/**
	 * @uml.property  name="catalog"
	 * @uml.associationEnd
	 */
	private CalculatorCatalog catalog;

	/**
	 * @uml.property  name="nodeGraphics"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="cytoscape.generated2.Att" qualifier="key:java.lang.String cytoscape.generated2.Graphics"
	 */
	private HashMap nodeGraphics;

	/**
	 * @uml.property  name="edgeGraphics"
	 * @uml.associationEnd  multiplicity="(0 -1)" ordering="true" elementType="cytoscape.generated2.Att" qualifier="key:java.lang.String cytoscape.generated2.Graphics"
	 */
	private HashMap edgeGraphics;

	/**
	 * @uml.property  name="globalGraphics"
	 */
	private HashMap globalGraphics;

	/**
	 * Creates a new VisualStyleBuilder object.
	 */
	public VisualStyleBuilder() {
		initialize();
	}

	/**
	 * Accept List of JAXB graphics objects
	 *
	 * @param graphics
	 */
	public VisualStyleBuilder(String newName, Map nodeGraphics, Map edgeGraphics, Map globalGraphics) {
		initialize();

		this.nodeGraphics = (HashMap) nodeGraphics;
		this.edgeGraphics = (HashMap) edgeGraphics;
		this.globalGraphics = (HashMap) globalGraphics;

		this.styleName = newName;
	}

	private void initialize() {
		nac = new NodeAppearanceCalculator();
		eac = new EdgeAppearanceCalculator();
		gac = new GlobalAppearanceCalculator();

		// Unlock the size object, then we can modify the both width and height.
		nac.setNodeSizeLocked(false);
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void buildStyle() {
		VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		catalog = vizmapper.getCalculatorCatalog();

		setNodeMaps(vizmapper);
		setEdgeMaps(vizmapper);

		//
		// Create new VS and apply it
		//
		gac.setDefaultBackgroundColor(new Color(255, 255, 204));
		xgmmlStyle = new VisualStyle(styleName, nac, eac, gac);

		// System.out.println(nac.getDescription());
		// System.out.println(eac.getDescription());
		catalog.addVisualStyle(xgmmlStyle);
		vizmapper.setVisualStyle(xgmmlStyle);
	}

	protected void setNodeMaps(VisualMappingManager vizmapper) {
		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "XGMML Labels";
		Calculator nlc = catalog.getCalculator(VisualPropertyType.NODE_LABEL, cName);

		if (nlc == null) {
			PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);
			nlc = new GenericNodeLabelCalculator(cName, m);
		}

		nac.setCalculator(nlc);

		//
		// Set node shapes (Uses "type" tag in the GML file)
		//
		DiscreteMapping nodeShapeMapping = new DiscreteMapping(NodeShape.ELLIPSE,
		                                                       AbstractCalculator.ID,
		                                                       ObjectMapping.NODE_MAPPING);
		nodeShapeMapping.setControllingAttributeName(AbstractCalculator.ID, vizmapper.getNetwork(),
		                                             false);

		//
		// Set the color of the node
		//
		DiscreteMapping nodeColorMapping = new DiscreteMapping(DEFAULT_COLOR,
		                                                       ObjectMapping.NODE_MAPPING);
		nodeColorMapping.setControllingAttributeName(AbstractCalculator.ID, vizmapper.getNetwork(),
		                                             true);

		DiscreteMapping nodeBorderColorMapping = new DiscreteMapping(DEFAULT_BORDER_COLOR,
		                                                             ObjectMapping.NODE_MAPPING);
		nodeBorderColorMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                   vizmapper.getNetwork(), true);

		Double defaultWidth = new Double(nac.getDefaultAppearance().getWidth());
		DiscreteMapping nodeWMapping = new DiscreteMapping(defaultWidth, ObjectMapping.NODE_MAPPING);
		nodeWMapping.setControllingAttributeName(AbstractCalculator.ID, vizmapper.getNetwork(), true);

		Double defaultHeight = new Double(nac.getDefaultAppearance().getHeight());
		DiscreteMapping nodeHMapping = new DiscreteMapping(defaultHeight, ObjectMapping.NODE_MAPPING);
		nodeHMapping.setControllingAttributeName(AbstractCalculator.ID, vizmapper.getNetwork(), true);

		DiscreteMapping nodeBorderWidthMapping = new DiscreteMapping(new Float(DEFAULT_LINE_WIDTH),
		                                                            ObjectMapping.NODE_MAPPING);
		nodeBorderWidthMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                  vizmapper.getNetwork(), false);

		// Non-GML graphics attributes
		Font defaultNodeFont = nac.getDefaultAppearance().getFont();
		DiscreteMapping nodeLabelFontMapping = new DiscreteMapping(defaultNodeFont,
		                                                           ObjectMapping.NODE_MAPPING);
		nodeLabelFontMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                 vizmapper.getNetwork(), true);

		Iterator it = nodeGraphics.keySet().iterator();

		// for (int i = 0; i < node_names.size(); i++) {
		while (it.hasNext()) {
			String key = (String) it.next();
			NodeShape shapeValue;
			Color nodeColor;
			Color nodeBorderColor;
			Double w;
			Double h;
			float lt = DEFAULT_LINE_WIDTH;

			// Cytoscape local graphics attributes
			String nodeLabelFont;
			String borderLineType;

			Font nodeFont = null;

			// Extract node graphics object from the given map
			Graphics curGraphics = (Graphics) nodeGraphics.get(key);
			List localNodeGraphics = null;
			Iterator localIt = null;

			if (curGraphics != null) {
				localNodeGraphics = curGraphics.getAtt();

				if (localNodeGraphics != null) {
					Att lg = (Att) localNodeGraphics.get(0);

					localIt = lg.getContent().iterator();
				}
			}

			// Get node shape
			if ((curGraphics != null) && (curGraphics.getType() != null)) {
				shapeValue = NodeShape.parseNodeShapeText(curGraphics.getType().value());
				nodeColor = getColor(curGraphics.getFill());
				nodeBorderColor = getColor(curGraphics.getOutline());
				w = new Double(curGraphics.getW());
				h = new Double(curGraphics.getH());

				BigInteger lineWidth = curGraphics.getWidth();

				if (lineWidth != null) {
					lt = lineWidth.floatValue();
				} 

				while (localIt.hasNext()) {
					Att nodeAttr = null;
					Object curObj = localIt.next();

					if (curObj.getClass().equals(Att.class)) {
						nodeAttr = (Att) curObj;

						if (nodeAttr.getName().equals("nodeLabelFont")) {
							nodeLabelFont = nodeAttr.getValue();

							String[] fontString = nodeLabelFont.split("-");
							nodeFont = new Font(fontString[0], Integer.parseInt(fontString[1]),
							                    Integer.parseInt(fontString[2]));
						} else if (nodeAttr.getName().equals("borderLineType")) {
						}
					}
				}
			} else {
				shapeValue = DEFAULT_SHAPE;
				nodeColor = DEFAULT_COLOR;
				nodeBorderColor = DEFAULT_BORDER_COLOR;
				w = defaultWidth;
				h = defaultHeight;
				lt = DEFAULT_LINE_WIDTH; 
				nodeFont = new Font("Default", 0, 10);
			}

			nodeShapeMapping.putMapValue(key, shapeValue);
			nodeColorMapping.putMapValue(key, nodeColor);
			nodeBorderColorMapping.putMapValue(key, nodeBorderColor);
			nodeWMapping.putMapValue(key, w);
			nodeHMapping.putMapValue(key, h);
			nodeBorderWidthMapping.putMapValue(key, new Float(lt));
			nodeLabelFontMapping.putMapValue(key, nodeFont);
		}

		Calculator shapeCalculator = new GenericNodeShapeCalculator("XGMML Node Shape",
		                                                            nodeShapeMapping);
		nac.setCalculator(shapeCalculator);

		Calculator nodeColorCalculator = new GenericNodeFillColorCalculator("XGMML Node Color",
		                                                                    nodeColorMapping);
		nac.setCalculator(nodeColorCalculator);

		Calculator nodeBorderColorCalculator = new GenericNodeBorderColorCalculator("XGMML Node Border Color",
		                                                                            nodeBorderColorMapping);
		nac.setCalculator(nodeBorderColorCalculator);

		Calculator nodeSizeCalculatorW = new GenericNodeWidthCalculator("XGMML Node Width",
		                                                                nodeWMapping);
		nac.setCalculator(nodeSizeCalculatorW);

		Calculator nodeSizeCalculatorH = new GenericNodeHeightCalculator("XGMML Node Height",
		                                                                 nodeHMapping);
		nac.setCalculator(nodeSizeCalculatorH);

		Calculator nodeBoderWidthCalculator = new GenericNodeLineWidthCalculator("XGMML Node Border",
		                                                                       nodeBorderWidthMapping);
		nac.setCalculator(nodeBoderWidthCalculator);

		Calculator nodeFontCalculator = new GenericNodeFontFaceCalculator("XGMML Node Label Font",
		                                                                  nodeLabelFontMapping);
		nac.setCalculator(nodeFontCalculator);
	}

	protected void setEdgeMaps(VisualMappingManager vizmapper) {
		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "XGMML Labels";
		Calculator elc = catalog.getCalculator(VisualPropertyType.EDGE_LABEL, cName);

		if (elc == null) {
			PassThroughMapping m = new PassThroughMapping("", AbstractCalculator.ID);
			elc = new GenericEdgeLabelCalculator(cName, m);
		}

		eac.setCalculator(elc);

		//
		// Set the color of the node
		//
		DiscreteMapping edgeColorMapping = new DiscreteMapping(DEFAULT_COLOR,
		                                                       ObjectMapping.EDGE_MAPPING);
		edgeColorMapping.setControllingAttributeName(AbstractCalculator.ID, vizmapper.getNetwork(),
		                                             true);

		DiscreteMapping edgeLineWidthMapping = new DiscreteMapping(new Float(4.0f),
		                                                          ObjectMapping.EDGE_MAPPING);
		edgeLineWidthMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                vizmapper.getNetwork(), true);

		DiscreteMapping edgeLineStyleMapping = new DiscreteMapping(LineStyle.SOLID,
		                                                          ObjectMapping.EDGE_MAPPING);
		edgeLineStyleMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                vizmapper.getNetwork(), true);

		// Non-GML graphics attributes
		Font defaultEdgeFont = eac.getDefaultAppearance().getFont();
		DiscreteMapping edgeLabelFontMapping = new DiscreteMapping(defaultEdgeFont,
		                                                           ObjectMapping.EDGE_MAPPING);
		edgeLabelFontMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                 vizmapper.getNetwork(), true);

		// For source & target arrows
		DiscreteMapping edgeSourceArrowShapeMapping = new DiscreteMapping(eac.getDefaultAppearance()
                                                           .get(VisualPropertyType.EDGE_SRCARROW_SHAPE),
		                                                   ObjectMapping.EDGE_MAPPING);
		edgeSourceArrowShapeMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                        vizmapper.getNetwork(), true);

		DiscreteMapping edgeTargetArrowShapeMapping = new DiscreteMapping(eac.getDefaultAppearance()
                                                           .get(VisualPropertyType.EDGE_TGTARROW_SHAPE),
		                                                   ObjectMapping.EDGE_MAPPING);
		edgeTargetArrowShapeMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                        vizmapper.getNetwork(), true);

		DiscreteMapping edgeSourceArrowColorMapping = new DiscreteMapping(eac.getDefaultAppearance()
                                                           .get(VisualPropertyType.EDGE_SRCARROW_COLOR),
		                                                   ObjectMapping.EDGE_MAPPING);
		edgeSourceArrowColorMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                        vizmapper.getNetwork(), true);

		DiscreteMapping edgeTargetArrowColorMapping = new DiscreteMapping(eac.getDefaultAppearance()
                                                           .get(VisualPropertyType.EDGE_TGTARROW_COLOR),
		                                                   ObjectMapping.EDGE_MAPPING);
		edgeTargetArrowColorMapping.setControllingAttributeName(AbstractCalculator.ID,
		                                                        vizmapper.getNetwork(), true);

		Iterator it = edgeGraphics.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();

			Color edgeColor;
			Font edgeFont = null;
			float edgeLineWidth = DEFAULT_LINE_WIDTH;
			LineStyle edgeLineStyle = LineStyle.SOLID;

			ArrowShape sourceShape = ArrowShape.NONE;
			ArrowShape targetShape = ArrowShape.NONE;
			Color sourceColor = Color.black;
			Color targetColor = Color.black;

			// Extract node graphics object from the given map
			Graphics curGraphics = (Graphics) edgeGraphics.get(key);
			List localEdgeGraphics = null;
			Iterator localIt = null;

			if (curGraphics != null) {
				localEdgeGraphics = curGraphics.getAtt();
				localIt = null;

				if ((localEdgeGraphics != null) && (localEdgeGraphics.size() != 0)) {
					Att lg = (Att) localEdgeGraphics.get(0);

					localIt = lg.getContent().iterator();
				}
			}

			// Get node shape
			if (curGraphics != null) {
				edgeColor = getColor(curGraphics.getFill());

				// Edge informaiton
				Color srcColor = null;
				Color tgtColor = null;
				int srcGinyType = NOT_AN_ARROW;
				int tgtGinyType = NOT_AN_ARROW;

				if (localIt != null) {
					while (localIt.hasNext()) {
						Att edgeAttr = null;
						Object curObj = localIt.next();

						if (curObj.getClass().equals(Att.class)) {
							edgeAttr = (Att) curObj;

							String edgeLabelFont = null;

							if (edgeAttr.getName().equals("edgeLabelFont")) {
								edgeLabelFont = edgeAttr.getValue();

								String[] fontString = edgeLabelFont.split("-");
								edgeFont = new Font(fontString[0], Integer.parseInt(fontString[1]),
								                    Integer.parseInt(fontString[2]));
							} else if (edgeAttr.getName().equals("edgeLineType")) {
								edgeLineWidth = LineStyle.parseWidth(edgeAttr.getValue());
								edgeLineStyle = LineStyle.parse(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals("sourceArrow")) {
								srcGinyType = Integer.parseInt(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals("targetArrow")) {
								tgtGinyType = Integer.parseInt(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals("sourceArrowColor")) {
								srcColor = getColor(edgeAttr.getValue());
							} else if (edgeAttr.getName().equals("targetArrowColor")) {
								tgtColor = getColor(edgeAttr.getValue());
							}
						}
					}

					// Create arrow if available
					if ((srcColor != null) && (srcGinyType != NOT_AN_ARROW)) {
						sourceShape = ArrowShape.getArrowShape(srcGinyType); 
						sourceColor = srcColor;
					}

					if ((tgtColor != null) && (tgtGinyType != NOT_AN_ARROW)) {
						targetShape = ArrowShape.getArrowShape(tgtGinyType);
						targetColor = tgtColor;
					}
				}
			} else {
				edgeColor = DEFAULT_COLOR;
				edgeLineWidth = DEFAULT_LINE_WIDTH; 
				edgeLineStyle = LineStyle.SOLID; 
				edgeFont = new Font("Default", 0, 10);
				sourceShape = ArrowShape.NONE;
				targetShape = ArrowShape.NONE;
				sourceColor = Color.black;
				targetColor = Color.black;

			}

			edgeColorMapping.putMapValue(key, edgeColor);
			edgeLineWidthMapping.putMapValue(key, new Float(edgeLineWidth));
			edgeLineStyleMapping.putMapValue(key, edgeLineStyle);
			edgeLabelFontMapping.putMapValue(key, edgeFont);

			edgeSourceArrowShapeMapping.putMapValue(key, sourceShape);
			edgeTargetArrowShapeMapping.putMapValue(key, targetShape);

			edgeSourceArrowColorMapping.putMapValue(key, sourceColor);
			edgeTargetArrowColorMapping.putMapValue(key, targetColor);
		}

		Calculator edgeColorCalculator = new GenericEdgeColorCalculator("XGMML Edge Color",
		                                                                edgeColorMapping);
		eac.setCalculator(edgeColorCalculator);

		Calculator edgeLineWidthCalculator = new GenericEdgeLineWidthCalculator("XGMML Edge Line Width",
		                                                                      edgeLineWidthMapping);
		eac.setCalculator(edgeLineWidthCalculator);

		Calculator edgeLineStyleCalculator = new GenericEdgeLineWidthCalculator("XGMML Edge Line Style",
		                                                                      edgeLineStyleMapping);
		eac.setCalculator(edgeLineStyleCalculator);

		Calculator edgeFontCalculator = new GenericEdgeFontFaceCalculator("XGMML Edge Label Font",
		                                                                  edgeLabelFontMapping);
		eac.setCalculator(edgeFontCalculator);

		Calculator edgeSourceArrowShapeCalculator = new GenericEdgeSourceArrowShapeCalculator("XGMML Source Edge Arrow Shape",
		                                                                            edgeSourceArrowShapeMapping);
		eac.setCalculator(edgeSourceArrowShapeCalculator);

		Calculator edgeTargetArrowShapeCalculator = new GenericEdgeTargetArrowShapeCalculator("XGMML Target Edge Arrow Shape",
		                                                                            edgeTargetArrowShapeMapping);
		eac.setCalculator(edgeTargetArrowShapeCalculator);

		Calculator edgeSourceArrowColorCalculator = new GenericEdgeSourceArrowColorCalculator("XGMML Source Edge Arrow Color",
		                                                                            edgeSourceArrowColorMapping);
		eac.setCalculator(edgeSourceArrowColorCalculator);

		Calculator edgeTargetArrowColorCalculator = new GenericEdgeTargetArrowColorCalculator("XGMML Target Edge Arrow Color",
		                                                                            edgeTargetArrowColorMapping);
		eac.setCalculator(edgeTargetArrowColorCalculator);
	}

	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	private Color getColor(String colorString) {
		// int red = Integer.parseInt(colorString.substring(1,3),16);
		// int green = Integer.parseInt(colorString.substring(3,5),16);
		// int blue = Integer.parseInt(colorString.substring(5,7),16);
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}
}
