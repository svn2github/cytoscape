/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import javax.swing.JComponent;

import infovis.Visualization;
import infovis.panel.ControlPanel;
import infovis.panel.DefaultLinkVisualPanel;
import infovis.visualization.LinkVisualization;

/**
 * Class NodeLinkGraphControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 * 
 * @infovis.factory ControlPanelFactory infovis.graph.visualization.NodeLinkGraphVisualization
*/
public class NodeLinkGraphControlPanel extends ControlPanel {
    
    public NodeLinkGraphControlPanel(Visualization visualization) {
        super(visualization);
    }
    
    public NodeLinkGraphVisualization getNodeLinkVisualization() {
        return (NodeLinkGraphVisualization)getVisualization()
            .findVisualization(NodeLinkGraphVisualization.class);
    }
    
    public LinkVisualization getLinkVisualization() {
        return (LinkVisualization)getVisualization()
            .findVisualization(LinkVisualization.class);
    }
    
    protected void createOtherTabs() {
        super.createOtherTabs();
        DefaultLinkVisualPanel.addVisualPanelTab(
                this,
                getLinkVisualization(), getFilter());
    }
    
    protected JComponent createVisualPanel() {
        return new NodeLinkGraphVisualPanel(getVisualization(), getFilter());
    }
}
