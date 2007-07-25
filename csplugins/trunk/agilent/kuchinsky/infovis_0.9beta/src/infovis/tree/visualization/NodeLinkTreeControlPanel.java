/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import javax.swing.JComponent;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.DefaultLinkVisualPanel;

/**
 * Control Panel for NodeLinkTreeVisualizations.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 * 
 * @infovis.factory ControlPanelFactory infovis.tree.visualization.NodeLinkTreeVisualization
 */
public class NodeLinkTreeControlPanel extends TreeControlPanel {
    /**
     * Constructor.
     * @param visualization the visualization.
     */
    public NodeLinkTreeControlPanel(Visualization visualization) {
        this(visualization, TreeStructrureFilter.sharedInstance());
    }

    /**
     * Constructor.
     * @param visualization the visualization
     * @param filter the ColumnFilter
     */
    public NodeLinkTreeControlPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
        DefaultLinkVisualPanel.addVisualPanelTab(this,
            getNodeLinkTreeVisualization().getLinkVisualization(), 
            filter);
    }

    /**
     * Returns the associated NodeLinkTreeVisualization.
     * @return the associated NodeLinkTreeVisualization.
     */
    public NodeLinkTreeVisualization getNodeLinkTreeVisualization() {
        return (NodeLinkTreeVisualization)getVisualization()
            .findVisualization(NodeLinkTreeVisualization.class);
    }
    
    protected JComponent createVisualPanel() {
        JComponent ret = new NodeLinkTreeVisualPanel(getVisualization(), getFilter());
        return ret;
    }
}
