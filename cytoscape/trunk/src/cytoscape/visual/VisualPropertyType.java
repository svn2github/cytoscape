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
import cytoscape.visual.calculators.GenericEdgeLineWidthCalculator;
import cytoscape.visual.calculators.GenericEdgeSourceArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeSourceArrowColorCalculator;
import cytoscape.visual.calculators.GenericEdgeSourceArrowShapeCalculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowCalculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowColorCalculator;
import cytoscape.visual.calculators.GenericEdgeTargetArrowShapeCalculator;
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
import cytoscape.visual.calculators.GenericNodeLineWidthCalculator;
import cytoscape.visual.calculators.GenericNodeShapeCalculator;
import cytoscape.visual.calculators.GenericNodeToolTipCalculator;
import cytoscape.visual.calculators.GenericNodeUniformSizeCalculator;
import cytoscape.visual.calculators.GenericNodeWidthCalculator;

import cytoscape.visual.ui.EditorDisplayer;
import cytoscape.visual.ui.EditorDisplayer.EditorType;
import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;

import java.awt.Color;
import java.awt.Font;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Enum for calculator types.<br>
 *
 * This will replace public constants defined in VizMapperUI class.<br>
 * This Enum defines visual attributes used in Cytoscape.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public enum VisualPropertyType {
    NODE_FILL_COLOR("Node Color", "nodeFillColorCalculator", "node.fillColor",
        "defaultNodeFillColor", GenericNodeFillColorCalculator.class,
        Color.class), 
    NODE_BORDER_COLOR("Node Border Color", "nodeBorderColorCalculator",
        "node.borderColor", "defaultNodeBorderColor",
        GenericNodeBorderColorCalculator.class, Color.class), 
    NODE_LINETYPE("Node Line Type", "nodeLineTypeCalculator", "node.lineType",
        "defaultNodeLineType", GenericNodeLineTypeCalculator.class, Line.class), 
    NODE_SHAPE("Node Shape", "nodeShapeCalculator", "node.shape",
        "defaultNodeShape", GenericNodeShapeCalculator.class, NodeShape.class), 
    NODE_SIZE("Node Size", "nodeUniformSizeCalculator", "node.size",
        "defaultNodeSize", GenericNodeUniformSizeCalculator.class, Number.class), 
    NODE_WIDTH("Node Width", "nodeWidthCalculator", "node.width",
        "defaultNodeWidth", GenericNodeWidthCalculator.class, Number.class), 
    NODE_HEIGHT("Node Height", "nodeHeightCalculator", "node.height",
        "defaultNodeHight", GenericNodeHeightCalculator.class, Number.class), 
    NODE_LABEL("Node Label", "nodeLabelCalculator", "node.label",
        "defaultNodeLabel", GenericNodeLabelCalculator.class, String.class), 
    NODE_FONT_FACE("Node Font Face", "nodeFontFaceCalculator", "node.font",
        "defaultNodeFont", GenericNodeFontFaceCalculator.class, Font.class), 
    NODE_FONT_SIZE("Node Font Size", "nodeFontSizeCalculator", "node.fontSize",
        "defaultNodeFontSize", GenericNodeFontSizeCalculator.class, Number.class), 
    NODE_LABEL_COLOR("Node Label Color", "nodeLabelColor", "node.labelColor",
        "defaultNodeLabelColor", GenericNodeLabelColorCalculator.class,
        Color.class), 
    NODE_TOOLTIP("Node Tooltip", "nodeTooltipCalculator", "node.toolTip",
        "defaultNodeToolTip", GenericNodeToolTipCalculator.class, String.class), 
    NODE_LABEL_POSITION("Node Label Position", "nodeLabelPositionCalculator",
        "node.labelPosition", "defaultNodeLabelPosition",
        GenericNodeLabelPositionCalculator.class, LabelPosition.class), 
    EDGE_COLOR("Edge Color", "edgeColorCalculator", "edge.color",
        "defaultEdgeColor", GenericEdgeColorCalculator.class, Color.class), 
    EDGE_LINETYPE("Edge Line Type", "edgeLineTypeCalculator", "edge.lineType",
        "defaultEdgeLineType", GenericEdgeLineTypeCalculator.class, Line.class), 
    EDGE_SRCARROW("Edge Source Arrow", "edgeSourceArrowCalculator",
        "edge.sourceArrow", "defaultEdgeSourceArrow",
        GenericEdgeSourceArrowCalculator.class, Arrow.class), 
    EDGE_TGTARROW("Edge Target Arrow", "edgeTargetArrowCalculator",
        "edge.targetArrow", "defaultEdgeTargetArrow",
        GenericEdgeTargetArrowCalculator.class, Arrow.class), 
    EDGE_LABEL("Edge Label", "edgeLabelCalculator", "edge.label",
        "defaultEdgeLabel", GenericEdgeLabelCalculator.class, String.class), 
    EDGE_FONT_FACE("Edge Font Face", "edgeFontFaceCalculator", "edge.font",
        "defaultEdgeFont", GenericEdgeFontFaceCalculator.class, Font.class), 
    EDGE_FONT_SIZE("Edge Font Size", "edgeFontSizeCalculator", "edge.fontSize",
        "defaultEdgeFontSize", GenericEdgeFontSizeCalculator.class, Number.class), 
    EDGE_LABEL_COLOR("Edge Label Color", "edgeLabelColorCalculator",
        "edge.labelColor", "defaultEdgeLabelColor",
        GenericEdgeLabelColorCalculator.class, Color.class), 
    EDGE_TOOLTIP("Edge Tooltip", "edgeTooltipCalculator", "edge.toolTip",
        "defaultEdgeToolTip", GenericEdgeToolTipCalculator.class, String.class), 

    // New from 2.5: line can have arbitrary width.
    NODE_LINE_WIDTH("Node Line Width", "nodeLineWidthCalculator",
        "node.lineWidth", "defaultNodeLineWidth",
        GenericNodeLineWidthCalculator.class, Number.class), 
    EDGE_LINE_WIDTH("Edge Line Width", "edgeLineWidthCalculator",
        "edge.lineWidth", "defaultEdgeLineWidth",
        GenericEdgeLineWidthCalculator.class, Number.class), 

    // New from 2.5: arrows have its own color, shape, and size.
    EDGE_SRCARROW_SHAPE("Edge Source Arrow Shape",
        "edgeSourceArrowShapeCalculator", "edge.sourceArrowShape",
        "defaultEdgeSourceArrowShape",
        GenericEdgeSourceArrowShapeCalculator.class, Arrow.class), 
    EDGE_TGTARROW_SHAPE("Edge Target Arrow Shape",
        "edgeTargetArrowShapeCalculator", "edge.targetArrowShape",
        "defaultEdgeTargetArrowShape",
        GenericEdgeTargetArrowShapeCalculator.class, Arrow.class), 
    EDGE_SRCARROW_COLOR("Edge Source Arrow Color",
        "edgeSourceArrowColorCalculator", "edge.sourceArrowColor",
        "defaultEdgeSourceArrowColor",
        GenericEdgeSourceArrowColorCalculator.class, Color.class), 
    EDGE_TGTARROW_COLOR("Edge Target Arrow Color",
        "edgeTargetArrowColorCalculator", "edge.targetArrowColor",
        "defaultEdgeTargetArrowColor",
        GenericEdgeTargetArrowColorCalculator.class, Color.class), 

    // Not yet implemented in version 2.5
    EDGE_LABEL_POSITION("Edge Label Position", "edgeLabelPositionCalculator",
        "edge.labelPosition", "defaultEdgeLabelPosition", null, null);
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

    // Data type for the actual visual property.
    private Class dataType;

    /*
     * private constructor to put name into this enum.
     */
    private VisualPropertyType(final String calcName,
        final String propertyLabel, final String bypassAttrName,
        final String defaultPropertyLabel, final Class calculatorClass,
        final Class dataType) {
        this.calcName = calcName;
        this.propertyLabel = propertyLabel;
        this.bypassAttrName = bypassAttrName;
        this.defaultPropertyLabel = defaultPropertyLabel;
        this.calculatorClass = calculatorClass;
        this.dataType = dataType;
    }

    /**
     * Returns name of calculator.
     */
    @Override
    public String toString() {
        return calcName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBypassAttrName() {
        return bypassAttrName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDefaultPropertyLabel() {
        return defaultPropertyLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param baseKey
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDefaultPropertyKey(final String baseKey) {
        return baseKey + "." + defaultPropertyLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getCalculatorClass() {
        return calculatorClass;
    }

    /**
     * Return position in thie enum as byte.<br>
     * Will be used as type.
     *
     * DO NOT USE THIS. This is only for backward compatibility.<br>
     * Replace your "byte" with is emum!
     *
     * @return byte type
     */
    @Deprecated
    public byte getType() {
        return (byte) ordinal();
    }

    /**
     * DO NOT USE THIS. This is only for backward compatibility.
     *
     * @param type
     * @return
     */
    @Deprecated
    public static VisualPropertyType getVisualPorpertyType(byte type) {
        /*
         * Type is always equal to ordinal.
         */
        return values()[type];
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getDataType() {
        return dataType;
    }

    /**
     * Check this visual property is for node or not.
     *
     * @return true if vp is for node.
     */
    public boolean isNodeProp() {
        if (calcName.startsWith("Node"))
            return true;
        else

            return false;
    }

    private Object showEditor(EditorDisplayer action)
        throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        Method method = null;

        method = action.getActionClass()
                       .getMethod(
                action.getCommand(),
                action.getParamTypes());

        Object ret = null;

        ret = method.invoke(
                null,
                action.getParameters());

        // This is an editor.
        if(ret != null && ret instanceof ContinuousMappingEditorPanel) {
        	return ret;
        } else if ((ret != null) && (action.getCompatibleClass() != ret.getClass()))
            ret = Double.parseDouble(ret.toString());

        return ret;
    }

    /**
     * Display discrete value editor for this visual property.
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Object showDiscreteEditor()
        throws Exception {
        return showEditor(EditorDisplayer.getEditor(this, EditorType.DISCRETE));
    }

    /**
     * Display continuous value editor.
     *
     * <p>
     *         Continuous editor always update mapping automatically, so there is no return value.
     * </p>
     * @throws Exception DOCUMENT ME!
     */
    public Object showContinuousEditor()
        throws Exception {
        final EditorDisplayer editor = EditorDisplayer.getEditor(this,
                EditorType.CONTINUOUS);
        editor.setParameters(
            new Object[] { 450, 200, "Gradient Editor for " + this.calcName, this });
        
        return showEditor(editor);
    }
}
