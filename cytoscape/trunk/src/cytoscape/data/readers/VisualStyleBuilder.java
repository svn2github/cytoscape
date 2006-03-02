
/*
  File: VisualStyleBuilder.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute of Systems Biology
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

package cytoscape.data.readers;

import java.awt.Color;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.generated2.Graphics;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.GlobalAppearanceCalculator;
import cytoscape.visual.LineType;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.ShapeNodeRealizer;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.GenericNodeColorCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.GenericNodeSizeCalculator;
import cytoscape.visual.calculators.NodeLabelCalculator;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

/**
 * Based on the graph/node/edge view information, build new Visual Style.
 * 
 * @author kono
 * 
 */
public class VisualStyleBuilder {

	
	protected static final byte DEFAULT_SHAPE = ShapeNodeRealizer.ELLIPSE;
	protected static final Color DEFAULT_COLOR = Color.WHITE;
	protected static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
	protected static final int DEFAULT_LINE_WIDTH = 1;
	//	 Name for the new visual style
	private String styleName;
	
	// New Visual Style comverted from GML file.
	private VisualStyle xgmmlStyle;

	// Node appearence
	private NodeAppearanceCalculator nac;

	// Edge appearence
	private EdgeAppearanceCalculator eac;

	// Global appearence
	private GlobalAppearanceCalculator gac;
	private CalculatorCatalog catalog;
	
	
	private HashMap nodeGraphics, edgeGraphics, globalGraphics;
	
	
	public VisualStyleBuilder() {

		initialize();
		
	}
	
	/**
	 * Accept List of JAXB graphics objects
	 * 
	 * @param graphics
	 */
	public VisualStyleBuilder( String newName, Map nodeGraphics, Map edgeGraphics, Map globalGraphics ) {
		initialize();
		
		this.nodeGraphics = (HashMap) nodeGraphics;
		this.edgeGraphics = (HashMap) edgeGraphics;
		this.globalGraphics = (HashMap) globalGraphics;
		
		this.styleName =  newName;
		
	}
	private void initialize() {
		nac = new NodeAppearanceCalculator();
		eac = new EdgeAppearanceCalculator();
		gac = new GlobalAppearanceCalculator();

		// Unlock the size object, then we can modify the both width and height.
		nac.setNodeSizeLocked(false);
	}

	public void buildStyle() {
		
		CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		VisualMappingManager vizmapper = cyDesktop.getVizMapManager();
		catalog = vizmapper.getCalculatorCatalog();

		setNodeMaps(vizmapper);
//		setEdgeMaps(vizmapper);

		//
		// Create new VS and apply it
		//
		gac.setDefaultBackgroundColor(new Color(255, 255, 204));
		xgmmlStyle = new VisualStyle(styleName, nac, eac, gac);

		// System.out.println(nac.getDescription());
		// System.out.println(eac.getDescription());

		vizmapper.setVisualStyle(xgmmlStyle);

	}
	

	
	protected void setNodeMaps(VisualMappingManager vizmapper) {
		//
		// Set label for the nodes. (Uses "label" tag in the GML file)
		//
		String cName = "XGMML Labels";
		NodeLabelCalculator nlc = catalog.getNodeLabelCalculator(cName);
		if (nlc == null) {
			PassThroughMapping m = new PassThroughMapping(new String(),
					Semantics.COMMON_NAME);
			nlc = new GenericNodeLabelCalculator(cName, m);
		}
		nac.setNodeLabelCalculator(nlc);

		//
		// Set node shapes (Uses "type" tag in the GML file)
		//
		DiscreteMapping nodeShapeMapping = new DiscreteMapping(new Byte(
				ShapeNodeRealizer.ELLIPSE), "commonName",
				ObjectMapping.NODE_MAPPING);
		nodeShapeMapping.setControllingAttributeName(Semantics.COMMON_NAME,
				vizmapper.getNetwork(), false);

		
		//
		// Set the color of the node
		//
		DiscreteMapping nodeColorMapping = new DiscreteMapping(DEFAULT_COLOR,
				ObjectMapping.NODE_MAPPING);
		nodeColorMapping.setControllingAttributeName(Semantics.COMMON_NAME,
				vizmapper.getNetwork(), true);
		
		DiscreteMapping nodeBorderColorMapping = new DiscreteMapping(DEFAULT_BORDER_COLOR,
				ObjectMapping.NODE_MAPPING);
		nodeBorderColorMapping.setControllingAttributeName(
				Semantics.COMMON_NAME, vizmapper.getNetwork(), true);
		
		
		Double defaultWidth = new Double(nac.getDefaultNodeWidth());
		DiscreteMapping nodeWMapping = new DiscreteMapping(defaultWidth,
				ObjectMapping.NODE_MAPPING);
		nodeWMapping.setControllingAttributeName(Semantics.COMMON_NAME,
				vizmapper.getNetwork(), true);
		
		
		Double defaultHeight = new Double(nac.getDefaultNodeHeight());
		DiscreteMapping nodeHMapping = new DiscreteMapping(defaultHeight,
				ObjectMapping.NODE_MAPPING);
		nodeHMapping.setControllingAttributeName(Semantics.COMMON_NAME,
				vizmapper.getNetwork(), true);
		
		DiscreteMapping nodeBorderTypeMapping = new DiscreteMapping(
				LineType.LINE_1, ObjectMapping.NODE_MAPPING);
		nodeBorderTypeMapping.setControllingAttributeName(
				Semantics.COMMON_NAME, vizmapper.getNetwork(), false);
		
		Iterator it = nodeGraphics.keySet().iterator();
		
		//for (int i = 0; i < node_names.size(); i++) {
		while(it.hasNext()) {
			String key = (String) it.next();
			Byte shapeValue;
			Color nodeColor;
			Color nodeBorderColor;
			Double w;
			Double h;
			
			LineType lt;
			
			// Extract node graphics object from the given map
			Graphics curGraphics = (Graphics) nodeGraphics.get(key);
			
			// Get node shape
			if( curGraphics.getType() != null) {
				shapeValue = ShapeNodeRealizer.parseNodeShapeTextIntoByte(curGraphics.getType());
				nodeColor = getColor(curGraphics.getFill());
				nodeBorderColor = getColor(curGraphics.getOutline());
				w = new Double(curGraphics.getW());
				h = new Double(curGraphics.getH());
				BigInteger lineWidth = curGraphics.getWidth();
				if( lineWidth != null ) {
					lt = getLineType(lineWidth.intValue());
				} else {
					lt = LineType.LINE_1;
				}
			} else {
				shapeValue = new Byte(DEFAULT_SHAPE);
				nodeColor = DEFAULT_COLOR;
				nodeBorderColor = DEFAULT_BORDER_COLOR;
				w = defaultWidth;
				h = defaultHeight;
				lt = LineType.LINE_1;
			}
			nodeShapeMapping.putMapValue(key, shapeValue);
			nodeColorMapping.putMapValue(key, nodeColor);
			nodeBorderColorMapping.putMapValue(key, nodeBorderColor);
			nodeWMapping.putMapValue(key, w);
			nodeHMapping.putMapValue(key, h);
			nodeBorderTypeMapping.putMapValue(key, lt);
		}
		GenericNodeShapeCalculator shapeCalculator = new GenericNodeShapeCalculator(
				"XGMML Node Shape", nodeShapeMapping);
		nac.setNodeShapeCalculator(shapeCalculator);

		GenericNodeColorCalculator nodeColorCalculator = new GenericNodeColorCalculator(
				"XGMML Node Color", nodeColorMapping);
		nac.setNodeFillColorCalculator(nodeColorCalculator);
		
		GenericNodeColorCalculator nodeBorderColorCalculator = new GenericNodeColorCalculator(
				"XGMML Node Border Color", nodeBorderColorMapping);
		nac.setNodeBorderColorCalculator(nodeBorderColorCalculator);
	
		GenericNodeSizeCalculator nodeSizeCalculatorW = new GenericNodeSizeCalculator(
				"XGMML Node Width", nodeWMapping);
		nac.setNodeWidthCalculator(nodeSizeCalculatorW);
		
		GenericNodeSizeCalculator nodeSizeCalculatorH = new GenericNodeSizeCalculator(
				"XGMML Node Height", nodeHMapping);
		nac.setNodeHeightCalculator(nodeSizeCalculatorH);
		GenericNodeLineTypeCalculator nodeBoderTypeCalculator = new GenericNodeLineTypeCalculator(
				"GML Node Border", nodeBorderTypeMapping);
		nac.setNodeLineTypeCalculator(nodeBoderTypeCalculator);
		
	}

	
	/**
	 * Create a color object from the string like it is stored in a gml file
	 */
	public Color getColor(String colorString) {
		// int red = Integer.parseInt(colorString.substring(1,3),16);
		// int green = Integer.parseInt(colorString.substring(3,5),16);
		// int blue = Integer.parseInt(colorString.substring(5,7),16);
		return new Color(Integer.parseInt(colorString.substring(1), 16));
	}
	
//	 Since GML represents line type as width, we need to
	// convert it to "LINE_TYPE"
	public static LineType getLineType(int width) {
		if (width == 1) {
			return LineType.LINE_1;
		} else if (width == 2) {
			return LineType.LINE_2;
		} else if (width == 3) {
			return LineType.LINE_3;
		} else if (width == 4) {
			return LineType.LINE_4;
		} else if (width == 5) {
			return LineType.LINE_5;
		} else if (width == 6) {
			return LineType.LINE_6;
		} else if (width == 7) {
			return LineType.LINE_7;
		} else {
			return LineType.LINE_1;
		}
	}
	
}
