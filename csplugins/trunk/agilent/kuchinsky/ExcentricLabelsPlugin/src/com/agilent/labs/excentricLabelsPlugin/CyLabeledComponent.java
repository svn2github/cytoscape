package com.agilent.labs.excentricLabelsPlugin;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.render.stateful.GraphRenderer;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntStack;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import giny.view.NodeView;
import infovis.visualization.magicLens.LabeledComponent;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;


/**
 * Class CyLabeledComponent declares all the methods required by
 * ExcentricLabels.
 *
 * @author Allan Kuchinsky, Jean-Daniel Fekete
 * @version $Revision: 0.1 $
 */
public class CyLabeledComponent implements LabeledComponent {

    private CyNetworkView view;

    private Rectangle2D hitBox;

    private double[] topLeft;

    private double[] bottomRight;


    public CyLabeledComponent (CyNetworkView view) {
        this.view = view;

    }

    /**
     * Returns a list of CyLabeledItems under a specified Rectangle.
     *
     * @param hitBox the rectangle that the items should intersect
     * @param bounds the bounds of the visualization
     * @param pick   an Set to use or null if a new one has be be allocated.
     * @return the Set of LabeledItems under the specified Rectangle.
     */
    public Set pickAll (Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        if (pick == null) {
            pick = new HashSet();
        }

        this.hitBox = hitBox;

        // convert hitBox rectangle corners to node coordinates
        topLeft = new double[2];
        topLeft[0] = hitBox.getMinX();
        topLeft[1] = hitBox.getMinY();
        ((DGraphView) view).xformComponentToNodeCoords(topLeft);


        bottomRight = new double[2];
        bottomRight[0] = hitBox.getMaxX();
        bottomRight[1] = hitBox.getMaxY();

        ((DGraphView) view).xformComponentToNodeCoords(bottomRight);

        final IntStack nodeStack = new IntStack();
        ((DGraphView) view).getNodesIntersectingRectangle(
                topLeft[0],
                topLeft[1],
                bottomRight[0],
                bottomRight[1],
                (((DGraphView) view).getCanvas().getLastRenderDetail() &
                        GraphRenderer.LOD_HIGH_DETAIL) == 0,
                nodeStack);
        final IntEnumerator nodesXSect = nodeStack.elements();
        while (nodesXSect.numRemaining() > 0) {
            final int nodeXSect = nodesXSect.nextInt();
            NodeView nv = view.getNodeView(nodeXSect);
            String nodeId = nv.getNode().getIdentifier();
            CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
            if (nodeAttributes != null) {
                String label = nodeAttributes.getStringAttribute(nodeId,
                        GlobalLabelConfig.getCurrentAttributeName());
                if (label != null) {
                    pick.add(new CyLabeledItem(view, nodeXSect, this));
                }
            }
        }
        return pick;
    }

    /**
     * Returns the JComponent managing this LabeledComponent
     *
     * @return the JComponent managing this LabeledComponent.
     */
    public JComponent getComponent () {
        return Cytoscape.getDesktop()
                .getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView());
    }

    public double[] getBottomRight () {
        return bottomRight;
    }

    public Rectangle2D getHitBox () {
        return hitBox;
    }

    public double[] getTopLeft () {
        return topLeft;
    }

}
