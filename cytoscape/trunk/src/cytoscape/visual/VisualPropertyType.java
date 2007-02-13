
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
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

package cytoscape.visual;

import cytoscape.visual.calculators.GenericEdgeColorCalculator;
import cytoscape.visual.calculators.GenericEdgeFontFaceCalculator;
import cytoscape.visual.calculators.GenericEdgeFontSizeCalculator;
import cytoscape.visual.calculators.GenericEdgeLabelCalculator;
import cytoscape.visual.calculators.GenericEdgeLabelColorCalculator;
import cytoscape.visual.calculators.GenericEdgeLineTypeCalculator;
import cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeToolTipCalculator;
import cytoscape.visual.calculators.GenericNodeBorderColorCalculator;
import cytoscape.visual.calculators.GenericNodeFillColorCalculator;
import cytoscape.visual.calculators.GenericNodeFontFaceCalculator;
import cytoscape.visual.calculators.GenericNodeFontSizeCalculator;
import cytoscape.visual.calculators.GenericNodeHeightCalculator;
import cytoscape.visual.calculators.GenericNodeLabelCalculator;
import cytoscape.visual.calculators.GenericNodeLabelColorCalculator;
import cytoscape.visual.calculators.GenericNodeLabelPositionCalculator;
import cytoscape.visual.calculators.GenericNodeLineTypeCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.GenericNodeToolTipCalculator;
import cytoscape.visual.calculators.GenericNodeUniformSizeCalculator;
import cytoscape.visual.calculators.GenericNodeWidthCalculator;

/**
 * Enum for calculator types.<br>
 *
 * This will replace public constants defined in VizMapperUI class.<br>
 * This Enum defines visual attributes used in Cytoscape.
 *
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public enum VisualPropertyType {

	NODE_COLOR("Node Color", "nodeFillColorCalculator", "node.fillColor", "defaultNodeFillColor",
	           GenericNodeFillColorCalculator.class),
	NODE_BORDER_COLOR("Node Border Color", "nodeBorderColorCalculator", "node.borderColor",
	                  "defaultNodeBorderColor", GenericNodeBorderColorCalculator.class), 
	NODE_LINETYPE("Node Line Type", "nodeLineTypeCalculator", "node.lineType",
	              "defaultNodeLineType", GenericNodeLineTypeCalculator.class), 
	NODE_SHAPE("Node Shape", "nodeShapeCalculator", "node.shape", "defaultNodeShape",
	           GenericNodeShapeCalculator.class), 
	NODE_SIZE("Node Size", "nodeUniformSizeCalculator", "node.size", "defaultNodeSize",
	          GenericNodeUniformSizeCalculator.class), 
	NODE_WIDTH("Node Width", "nodeWidthCalculator", "node.width", "defaultNodeWidth",
	           GenericNodeWidthCalculator.class), 
	NODE_HEIGHT("Node Height", "nodeHeightCalculator", "node.height", "defaultNodeHight",
	            GenericNodeHeightCalculator.class), 
	NODE_LABEL("Node Label", "nodeLabelCalculator", "node.label", "defaultNodeLabel",
	           GenericNodeLabelCalculator.class), 
	NODE_FONT_FACE("Node Font Face", "nodeFontFaceCalculator", "node.font", "defaultNodeFont",
	               GenericNodeFontFaceCalculator.class), 
	NODE_FONT_SIZE("Node Font Size", "nodeFontSizeCalculator", "node.fontSize",
	               "defaultNodeFontSize", GenericNodeFontSizeCalculator.class), 
	NODE_LABEL_COLOR("Node Label Color", "nodeLabelColor", "node.labelColor",
	                 "defaultNodeLabelColor", GenericNodeLabelColorCalculator.class), 
	NODE_TOOLTIP("Node Tooltip", "nodeTooltipCalculator", "node.toolTip", "defaultNodeToolTip",
	             GenericNodeToolTipCalculator.class), 
	NODE_LABEL_POSITION("Node Label Position", "nodeLabelPositionCalculator", "node.labelPosition",
	                    "defaultNodeLabelPosition", GenericNodeLabelPositionCalculator.class), 
	EDGE_COLOR("Edge Color", "edgeColorCalculator", "edge.color", "defaultEdgeColor",
	           GenericEdgeColorCalculator.class), 
	EDGE_LINETYPE("Edge Line Type", "edgeLineTypeCalculator", "edge.lineType",
	              "defaultEdgeLineType", GenericEdgeLineTypeCalculator.class), 
	EDGE_SRCARROW("Edge Source Arrow", "edgeSourceArrowCalculator", "edge.sourceArrow",
	              "defaultEdgeSourceArrow", GenericEdgeSourceArrowCalculator.class), 
	EDGE_TGTARROW("Edge Target Arrow", "edgeTargetArrowCalculator", "edge.targetArrow",
	              "defaultEdgeTargetArrow", GenericEdgeTargetArrowCalculator.class), 
	EDGE_LABEL("Edge Label", "edgeLabelCalculator", "edge.label", "defaultEdgeLabel",
	           GenericEdgeLabelCalculator.class), 
	EDGE_FONT_FACE("Edge Font Face", "edgeFontFaceCalculator", "edge.font", "defaultEdgeFont",
	               GenericEdgeFontFaceCalculator.class), 
	EDGE_FONT_SIZE("Edge Font Size", "edgeFontSizeCalculator", "edge.fontSize",
	               "defaultEdgeFontSize", GenericEdgeFontSizeCalculator.class), 
	EDGE_LABEL_COLOR("Edge Label Color", "edgeLabelColorCalculator", "edge.labelColor",
	                 "defaultEdgeLabelColor", GenericEdgeLabelColorCalculator.class), 
	EDGE_TOOLTIP("Edge Tooltip", "edgeTooltipCalculator", "edge.toolTip", "defaultEdgeToolTip",
	             GenericEdgeToolTipCalculator.class), 

	// Will be used in future.
	EDGE_LABEL_POSITION("Edge Label Position", "edgeLabelPositionCalculator", "edge.labelPosition",
	                    "defaultEdgeLabelPosition", null);
	/*
	 * String returned by toString() method.
	 */
	private final String calcName;

	/*
	 * Property label in prop file.
	 */
	private String propertyLabel;

	/*
	 * Attribute name for vizmap bypass function (right-click bypass)
	 */
	private String bypassAttrName;
	private String defaultPropertyLabel;
	private Class calculatorClass;

	/*
	 * private constructor to put name into this enum.
	 */
	private VisualPropertyType(final String calcName, final String propertyLabel,
	                           final String bypassAttrName, final String defaultPropertyLabel,
	                           final Class calculatorClass) {
		this.calcName = calcName;
		this.propertyLabel = propertyLabel;
		this.bypassAttrName = bypassAttrName;
		this.defaultPropertyLabel = defaultPropertyLabel;
		this.calculatorClass = calculatorClass;
	}

	/**
	 * Returns name of calculator.
	 */
	@Override
	public String toString() {
		return calcName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getName() {
		return calcName;
	}

	/**
	 * Returns string used as property label in VS prop file.
	 *
	 * @return
	 */
	public String getPropertyLabel() {
		return propertyLabel;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getBypassAttrName() {
		return bypassAttrName;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDefaultPropertyLabel() {
		return defaultPropertyLabel;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param baseKey DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getDefaultPropertyKey(final String baseKey) {
		return baseKey + "." + defaultPropertyLabel;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class getCalculatorClass() {
		return calculatorClass;
	}

	/**
	 * Return position in thie enum as byte.<br>
	 * Will be used as type.
	 *
	 * @return byte type
	 */
	@Deprecated
	public byte getType() {
		return (byte) ordinal();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static VisualPropertyType getCalculatorTypes(byte type) {
		/*
		 * Type is always equal to ordinal.
		 */
		return values()[type];
	}

	/**
	 * Returns number of available visual property type
	 *
	 * @return
	 */
	public static int typeCount() {
		return values().length;
	}
}
