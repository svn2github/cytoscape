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
 * Class NodeLinkTreeControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 * @infovis.factory ControlPanelFactory infovis.tree.visualization.NodeLinkTreeVisualization
 */
public class NodeLinkTreeControlPanel extends TreeControlPanel {
    public NodeLinkTreeControlPanel(Visualization visualization) {
        this(visualization, TreeStructrureFilter.sharedInstance());
    }

    public NodeLinkTreeVisualization getNodeLinkTreeVisualization() {
        return (NodeLinkTreeVisualization)getVisualization()
            .findVisualization(NodeLinkTreeVisualization.class);
    }
        
    public NodeLinkTreeControlPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
        DefaultLinkVisualPanel.addVisualPanelTab(this,
            getNodeLinkTreeVisualization().getLinkVisualization(), 
            filter);
    }
    
    protected JComponent createVisualPanel() {
        JComponent ret = new NodeLinkTreeVisualPanel(getVisualization(), getFilter());
        return ret;
    }
}
