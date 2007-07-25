package infovis.example.visualization.graphontreemap;
import infovis.Visualization;
import infovis.panel.DefaultLinkVisualPanel;
import infovis.tree.visualization.treemap.TreemapControlPanel;

/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/

/**
 * Class GraphOnTreemapControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */

public class GraphOnTreemapControlPanel extends TreemapControlPanel {
    public GraphOnTreemapControlPanel(Visualization visualization) {
        super(visualization);
        DefaultLinkVisualPanel.addVisualPanelTab(
            this,
            ((GraphOnTreemapVisualization) getTreemapVisualization())
                .getLinkVisualization(),
            getFilter());
    }
}