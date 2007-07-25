/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Visualization;
import infovis.visualization.LinkVisualization;
import infovis.visualization.NodeAccessor;
import infovis.visualization.render.VisualLabel;

/**
 * Class LinkExcentricItem
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class LinkExcentricItem extends StrokingExcentricItem {
    LinkVisualization linkVisualization;

    public LinkExcentricItem(Visualization visualization, int index) {
        super(visualization, index);
        linkVisualization = (LinkVisualization) visualization;
    }

    public String getLabel() {
        String lab = super.getLabel();
        if (lab != null)
            return lab;

        NodeAccessor accessor = linkVisualization.getNodeAccessor();
        int start = accessor.getStartNode(index);
        int end = accessor.getEndNode(index);
        if (start == -1 || end == -1)
            return null;
        String startLabel =
            VisualLabel.getLabelAt(
            linkVisualization.getNodeVisualization(),
            start);
        if (startLabel == null) {
            startLabel = "node" + start;
        }
        String endLabel =
            VisualLabel.getLabelAt(
                    linkVisualization.getNodeVisualization(),
                    end);
        if (endLabel == null) {
            endLabel = "node" + end;
        }

        return startLabel + "->" + endLabel;
    }
}
