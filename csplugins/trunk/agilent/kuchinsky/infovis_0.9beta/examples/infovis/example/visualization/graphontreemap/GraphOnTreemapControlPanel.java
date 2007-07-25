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
 * Control panel for Graph on Treemap.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */

public class GraphOnTreemapControlPanel extends TreemapControlPanel{
    /**
     * Constructor.
     * @param visualization the visualization.
     */
    public GraphOnTreemapControlPanel(Visualization visualization) {
        super(visualization);
        DefaultLinkVisualPanel.addVisualPanelTab(
            this,
            ((GraphOnTreemapVisualization) getTreemapVisualization())
                .getLinkVisualization(),
            getFilter());
    }
}