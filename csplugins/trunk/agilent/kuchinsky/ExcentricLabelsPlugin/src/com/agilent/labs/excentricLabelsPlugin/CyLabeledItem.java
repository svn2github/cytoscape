package com.agilent.labs.excentricLabelsPlugin;

import giny.view.NodeView;
import infovis.visualization.magicLens.LabeledComponent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import ding.view.InnerCanvas;

public class CyLabeledItem implements LabeledComponent.LabeledItem {

    private CyNetworkView view;

    private int index;

    private NodeView nv;

    private Shape shape;

    private CyLabeledComponent comp;

    private static cytoscape.data.CyAttributes nodeAttribs = Cytoscape
            .getNodeAttributes();

    public CyLabeledItem(CyNetworkView view, int idx, CyLabeledComponent comp) {
        this.view = view;
        this.index = idx;
        this.comp = comp;
        this.nv = view.getNodeView(idx);

        // for now, just return bounding box of NodeView
        this.shape = new Rectangle2D.Double(nv.getXPosition(), nv.getYPosition(),
                nv.getWidth(), nv.getHeight());
    }

    /**
     * Returns the JComponent managing this LabeledComponent
     *
     * @return the JComponent managing this LabeledComponent.
     */
    public Component getComponent() {
        return Cytoscape.getDesktop().getNetworkViewManager()
//				.getComponentForView(view);
        .getInternalFrameComponent(view);
    }

    public String getLabel() {
        String id = nv.getNode().getIdentifier();
        String label = nodeAttribs.getStringAttribute(id,
                GlobalLabelConfig.getCurrentAttributeName());
        if (label == null) {
            return "---";
        } else {
            if (label.length() > 20) {
                label = label.substring(0, 18) + "...";
            }
            return label;
        }
    }

    public Shape getShape() {
        return shape;
    }

    public Point2D getCenterIn(Rectangle2D focus, Point2D ptOut) {
        double nodeX = nv.getXPosition();
        double nodeY = nv.getYPosition();

        DGraphView dGraphView = (DGraphView) Cytoscape.getCurrentNetworkView();
        InnerCanvas innerCanvas = dGraphView.getCanvas();
        AffineTransform affineTransform = innerCanvas.getAffineTransform();

        Point2D ptSrc = new Point2D.Double(nodeX, nodeY);
        affineTransform.transform(ptSrc, ptOut);
        return ptOut;
    }

    public Color getColor() {
        //		VisualStyle visualStyle = view.getVisualStyle();
        //		NodeAppearanceCalculator nodeAppearanceCalculator = visualStyle.getNodeAppearanceCalculator();
        //		NodeColorCalculator nodeColorCalc = nodeAppearanceCalculator.getNodeFillColorCalculator();
        //		return nodeColorCalc.calculateNodeColor(nv.getNode(), view.getNetwork());
        return Color.WHITE;
    }

}
