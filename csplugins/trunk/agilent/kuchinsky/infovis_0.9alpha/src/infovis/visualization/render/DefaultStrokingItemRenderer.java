/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import java.awt.Color;

/**
 * Default ItemRenderer for visualizations with shapes stroked.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory ItemRendererFactory infovis.visualization.StrokingVisualization
 */
public class DefaultStrokingItemRenderer extends GroupItemRenderer {
    public DefaultStrokingItemRenderer() {
        super(
                new LayoutVisual(new VisualSize("length")),
                new VisualFilter(
                new VisualColor(
                new VisualStrokeSize(
                new VisualClipBounds(
                new VisualFisheye(
                new GroupItemRenderer(
                new VisualSelection(
                new VisualAlpha(
                        Stroke.instance,
                        new VisualArrowHead(Fill.instance)),
                Color.RED,
                null),
                new VisualStrokingLabel(null))), true)))));
        VisualColor.get(this).setDefaultColor(Color.BLACK);
    }

}