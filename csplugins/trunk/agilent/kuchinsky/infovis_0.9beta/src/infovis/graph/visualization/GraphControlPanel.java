/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import javax.swing.JComponent;

import infovis.Graph;
import infovis.Visualization;
import infovis.panel.ControlPanel;
import infovis.panel.DefaultVisualPanel;

/**
 * Control panel for graphs. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 * 
 * @infovis.factory ControlPanelFactory infovis.graph.visualization.GraphVisualization
 */
public class GraphControlPanel extends ControlPanel {

    public GraphControlPanel(Visualization visualization) {
        super(visualization);
    }
    
    public GraphVisualization getGraphVisualization() {
        return (GraphVisualization)getVisualization()
            .findVisualization(GraphVisualization.class);
    }
    
    public Graph getGraph() {
        return getGraphVisualization().getGraph();
    }

    protected JComponent createVisualPanel() {
        DefaultVisualPanel panel = new DefaultVisualPanel(getVisualization(), getFilter());
        panel.addOrientationButtons();
        return panel;
    }
}
