/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.color;

import infovis.Visualization;
import infovis.column.*;
import infovis.panel.VisualizationPanel;
import infovis.visualization.HistogramVisualization;
import infovis.visualization.color.EqualizedOrderedColor;
import infovis.visualization.render.VisualColor;

/**
 * Class EqualizedOrderedColorControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class EqualizedOrderedColorControlPanel extends
        OrderedColorControlPanel {
    HistogramColumn histogram;
    HistogramVisualization histoVisu;

    public EqualizedOrderedColorControlPanel(Visualization visualization) {
        super();
        this.visualization = visualization;
        VisualColor vc = VisualColor.get(visualization);
        this.colorVisualization = vc.getColorVisualization();
        EqualizedOrderedColor eq = getEqualizedColorVisualization();
        histoVisu = new HistogramVisualization((NumberColumn)eq.getColumn());
//        histoVisu.setVisualColumn(
//                Visualization.VISUAL_COLOR,
//                histoVisu.getColumn(IdColumn.NAME));
        //histoVisu.setColorVisualization(eq);
        VisualizationPanel panel = new VisualizationPanel(histoVisu);
        //panel.setPreferredSize(new Dimension(100, 30));
        add(panel);
    }
    
    public EqualizedOrderedColor getEqualizedColorVisualization() {
        return (EqualizedOrderedColor)getOrderedColor();
    }

}
