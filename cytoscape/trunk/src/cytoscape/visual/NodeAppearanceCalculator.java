/*
 File: NodeAppearanceCalculator.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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

import cytoscape.CyNetwork;

import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.NodeCalculator;
import cytoscape.visual.calculators.NodeColorCalculator;
import cytoscape.visual.calculators.NodeFontFaceCalculator;
import cytoscape.visual.calculators.NodeFontSizeCalculator;
import cytoscape.visual.calculators.NodeLabelCalculator;
import cytoscape.visual.calculators.NodeLabelColorCalculator;
import cytoscape.visual.calculators.NodeLineTypeCalculator;
import cytoscape.visual.calculators.NodeShapeCalculator;
import cytoscape.visual.calculators.NodeSizeCalculator;
import cytoscape.visual.calculators.NodeToolTipCalculator;

import cytoscape.visual.ui.VizMapUI;

import giny.model.Node;

import java.awt.Color;
import java.awt.Font;

import java.util.Properties;


/**
 * This class calculates the appearance of a Node. It holds a default value and
 * a (possibly null) calculator for each visual attribute.
 */
public class NodeAppearanceCalculator extends AppearanceCalculator {
    private NodeAppearance defaultAppearance = new NodeAppearance();

    /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
    @Deprecated
    private NodeAppearance currentAppearance;

    /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
    @Deprecated
    private CyNetwork currentNetwork;

    /** Used _ONLY_ to support deprecated code - DO NOT USE OTHERWISE!!!! */
    @Deprecated
    private Node currentNode;

    /**
     * Creates a new NodeAppearanceCalculator object.
     */
    public NodeAppearanceCalculator() {
        super();
    }

    /**
     * Creates a new NodeAppearanceCalculator and immediately customizes it by
     * calling applyProperties with the supplied arguments.
     */
    public NodeAppearanceCalculator(String name, Properties nacProps,
        String baseKey, CalculatorCatalog catalog) {
        super(name, nacProps, baseKey, catalog, new NodeAppearance());
        defaultAppearance = (NodeAppearance) tmpDefaultAppearance;
    }

    /**
     * Copy constructor. Returns a default object if the argument is null.
     */
    public NodeAppearanceCalculator(NodeAppearanceCalculator toCopy) {
        super(toCopy);
    }

    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Node in the supplied
     * CyNetwork. A new NodeApperance object will be created.
     */
    public NodeAppearance calculateNodeAppearance(Node node, CyNetwork network) {
        NodeAppearance appr = new NodeAppearance();
        calculateNodeAppearance(appr, node, network);

        return appr;
    }

    /**
     * Using the rules defined by the default values and calculators in this
     * object, compute an appearance for the requested Node in the supplied
     * CyNetwork. The supplied NodeAppearance object will be changed to hold the
     * new values.
     */
    public void calculateNodeAppearance(NodeAppearance appr, Node node,
        CyNetwork network) {
        appr.copy(defaultAppearance); // set defaults and node lock state

        for (Calculator nc : calcs)
            nc.apply(appr, node, network);

        appr.applyBypass(node);

        currentAppearance = appr;
        currentNode = node;
        currentNetwork = network;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeAppearance getDefaultAppearance() {
        return defaultAppearance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void setDefaultAppearance(NodeAppearance n) {
        defaultAppearance = n;
    }

    /**
     * Returns a text description of the current default values and calculator
     * names.
     */
    public String getDescription() {
        return getDescription("NodeAppearanceCalculator", defaultAppearance);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param nacProps DOCUMENT ME!
     * @param baseKey DOCUMENT ME!
     * @param catalog DOCUMENT ME!
     */
    public void applyProperties(String name, Properties nacProps,
        String baseKey, CalculatorCatalog catalog) {
        applyProperties(defaultAppearance, name, nacProps, baseKey, catalog);
    }

    /**
     * DOCUMENT ME!
     *
     * @param baseKey DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Properties getProperties(String baseKey) {
        return getProperties(defaultAppearance, baseKey);
    }

    protected void copyDefaultAppearance(AppearanceCalculator toCopy) {
        defaultAppearance = (NodeAppearance) (((NodeAppearanceCalculator) toCopy).getDefaultAppearance().clone());
    }

    // probably shouldn't be here now
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getNodeSizeLocked() {
        return defaultAppearance.getNodeSizeLocked();
    }

    // probably shouldn't be here now
    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setNodeSizeLocked(boolean b) {
        defaultAppearance.setNodeSizeLocked(b);
    }

    protected boolean isValidCalculator(Calculator c) {
        if (c instanceof NodeCalculator)
            return true;
        else

            return false;
    }

    // ===================================================================================
    //
    // Beyond this point all code is deprecated or exists solely to support
    // said deprecated code.
    // 
    // Avert your eyes and save yourself the pain!!!
    //
    //

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeFillColor(Color c) {
        defaultAppearance.setFillColor(c);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeBorderColor(Color c) {
        defaultAppearance.setBorderColor(c);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeLineType(LineType lt) {
        defaultAppearance.setBorderLineType(lt);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeShape(byte s) {
        defaultAppearance.setShape(s);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeWidth(double d) {
        defaultAppearance.setWidth(d);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeHeight(double d) {
        defaultAppearance.setHeight(d);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeLabel(String s) {
        defaultAppearance.setLabel(s);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeToolTip(String s) {
        defaultAppearance.setToolTip(s);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeFont(Font f) {
        defaultAppearance.setFont(f);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeFontFace(Font f) {
        defaultAppearance.setFont(f);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeFontSize(float f) {
        defaultAppearance.setFontSize(f);
    }

    /**
     * @deprecated Use getDefaultAppearance() and then set that. This method
     *             will be removed Sept 2007.
     */
    public void setDefaultNodeLabelColor(Color c) {
        defaultAppearance.setLabelColor(c);
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public Color getDefaultNodeFillColor() {
        return defaultAppearance.getFillColor();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public Color getDefaultNodeBorderColor() {
        return defaultAppearance.getBorderColor();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public LineType getDefaultNodeLineType() {
        return defaultAppearance.getBorderLineType();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public byte getDefaultNodeShape() {
        return defaultAppearance.getShape();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public double getDefaultNodeWidth() {
        return defaultAppearance.getWidth();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public double getDefaultNodeHeight() {
        return defaultAppearance.getHeight();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public String getDefaultNodeLabel() {
        return defaultAppearance.getLabel();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public String getDefaultNodeToolTip() {
        return defaultAppearance.getToolTip();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public Font getDefaultNodeFont() {
        return defaultAppearance.getFont();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public Font getDefaultNodeFontFace() {
        return defaultAppearance.getFont();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public float getDefaultNodeFontSize() {
        return defaultAppearance.getFontSize();
    }

    /**
     * @deprecated Use setDefaultAppearance() instead. This method will be
     *             removed Sept 2007.
     */
    public Color getDefaultNodeLabelColor() {
        return defaultAppearance.getLabelColor();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public Color calculateNodeFillColor(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getFillColor();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public Color calculateNodeBorderColor(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getBorderColor();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public LineType calculateNodeLineType(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getBorderLineType();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public byte calculateNodeShape(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getShape();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public double calculateNodeWidth(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getWidth();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public double calculateNodeHeight(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getHeight();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public String calculateNodeLabel(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getLabel();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public String calculateNodeToolTip(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getToolTip();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public Font calculateNodeFont(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getFont();
    }

    /**
     * @deprecated Use calculateNodeAppearance() and get the value from the
     *             NodeAppearance. This method will be removed Sept 2007.
     */
    public Color calculateNodeLabelColor(Node node, CyNetwork network) {
        doCalc(node, network);

        return currentAppearance.getLabelColor();
    }

    /** Used _ONLY_ to support deprecated code - DO NOT USE for anything else!!!! */
    private void doCalc(Node node, CyNetwork network) {
        if ((node != currentNode) && (network != currentNetwork))
            calculateNodeAppearance(node, network);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeColorCalculator getNodeFillColorCalculator() {
        return (NodeColorCalculator) getCalculator(VizMapUI.NODE_COLOR);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeColorCalculator getNodeBorderColorCalculator() {
        return (NodeColorCalculator) getCalculator(VizMapUI.NODE_BORDER_COLOR);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeLineTypeCalculator getNodeLineTypeCalculator() {
        return (NodeLineTypeCalculator) getCalculator(VizMapUI.NODE_LINETYPE);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeShapeCalculator getNodeShapeCalculator() {
        return (NodeShapeCalculator) getCalculator(VizMapUI.NODE_SHAPE);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeSizeCalculator getNodeWidthCalculator() {
        if (getNodeSizeLocked())
            return (NodeSizeCalculator) getCalculator(VizMapUI.NODE_SIZE);
        else

            return (NodeSizeCalculator) getCalculator(VizMapUI.NODE_WIDTH);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeSizeCalculator getNodeHeightCalculator() {
        if (getNodeSizeLocked())
            return (NodeSizeCalculator) getCalculator(VizMapUI.NODE_SIZE);
        else

            return (NodeSizeCalculator) getCalculator(VizMapUI.NODE_HEIGHT);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeLabelCalculator getNodeLabelCalculator() {
        return (NodeLabelCalculator) getCalculator(VizMapUI.NODE_LABEL);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeToolTipCalculator getNodeToolTipCalculator() {
        return (NodeToolTipCalculator) getCalculator(VizMapUI.NODE_TOOLTIP);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeFontFaceCalculator getNodeFontFaceCalculator() {
        return (NodeFontFaceCalculator) getCalculator(VizMapUI.NODE_FONT_FACE);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeFontSizeCalculator getNodeFontSizeCalculator() {
        return (NodeFontSizeCalculator) getCalculator(VizMapUI.NODE_FONT_SIZE);
    }

    /**
     * @deprecated Use getCalculator(type) instead. This method will be removed
     *             Sept 2007.
     */
    public NodeLabelColorCalculator getNodeLabelColorCalculator() {
        return (NodeLabelColorCalculator) getCalculator(VizMapUI.NODE_LABEL_COLOR);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeFillColorCalculator(NodeColorCalculator c) {
        // special handling for deprecated code
        c.set(VisualPropertyType.NODE_FILL_COLOR);
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeBorderColorCalculator(NodeColorCalculator c) {
        // special handling for deprecated code
        c.set(VisualPropertyType.NODE_BORDER_COLOR);
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeLineTypeCalculator(NodeLineTypeCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeShapeCalculator(NodeShapeCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeWidthCalculator(NodeSizeCalculator c) {
        // special handling for deprecated code
        if (getNodeSizeLocked())
            c.set(VisualPropertyType.NODE_SIZE);
        else
            c.set(VisualPropertyType.NODE_WIDTH);

        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeHeightCalculator(NodeSizeCalculator c) {
        // special handling for deprecated code
        if (getNodeSizeLocked())
            c.set(VisualPropertyType.NODE_SIZE);
        else
            c.set(VisualPropertyType.NODE_HEIGHT);

        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeLabelCalculator(NodeLabelCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeToolTipCalculator(NodeToolTipCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeFontFaceCalculator(NodeFontFaceCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeFontSizeCalculator(NodeFontSizeCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use setCalculator(calc) instead. This method will be removed
     *             Sept 2007.
     */
    public void setNodeLabelColorCalculator(NodeLabelColorCalculator c) {
        setCalculator(c);
    }

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeFillColorBypass = "node.fillColor";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeBorderColorBypass = "node.borderColor";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeLineTypeBypass = "node.lineType";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeShapeBypass = "node.shape";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeWidthBypass = "node.width";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeHeightBypass = "node.height";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeLabelBypass = "node.label";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeToolTipBypass = "node.toolTip";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeFontBypass = "node.font";

    /**
     * @deprecated Use calculator.getAttrNameBypass() instead. Will be removed
     *             Sept 2007.
     */
    public static final String nodeLabelColorBypass = "node.labelColor";
}
