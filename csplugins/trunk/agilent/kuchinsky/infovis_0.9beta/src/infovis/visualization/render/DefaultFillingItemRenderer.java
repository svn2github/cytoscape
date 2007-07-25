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
 * Default set of item renderers for visualizations with filled shapes.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 * 
 * @infovis.factory ItemRendererFactory infovis.visualization.DefaultVisualization
 */
public class DefaultFillingItemRenderer extends GroupItemRenderer {
    public DefaultFillingItemRenderer() {
        super(
                new LayoutVisual(new VisualSize()), 
                new VisualFilter(
                        new VisualClipBounds(
                        new VisualFisheye(
                                new VisualColor(
                                new VisualArea(
                                        new VisualAlpha(Fill.instance))),
                        new VisualSelection(
                                Stroke.instance,
                                Color.RED,
                                Color.BLACK),
                        new DefaultVisualLabel(null))))
                        .addRenderer(new VisualVisualization()));
    }
    // ((layout size) (filter (clip (fisheye (color (alpha fill)) (selection[red
    // contrast] stroke) label))))
    // <layout><size/></layout>
    // <filter><clip><fisheye>
    //   <color><alpha><fill/></alpha></color>
    //   <selection color="red" contrast="true"><stroke/></selection>
    //   <label/>
    // </fisheye></clip></filter>
}
