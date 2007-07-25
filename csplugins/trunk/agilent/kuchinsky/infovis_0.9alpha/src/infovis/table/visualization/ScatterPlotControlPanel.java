/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.ControlPanel;

import javax.swing.JComponent;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 *
 * @infovis.factory ControlPanelFactory infovis.table.visualization.ScatterPlotVisualization
 
 */
public class ScatterPlotControlPanel extends ControlPanel {

    /**
     * Constructor for ScatterPlotControlPanel.
     * @param visualization
     */
    public ScatterPlotControlPanel(Visualization visualization) {
        super(visualization);
    }

    /**
     * Constructor for ScatterPlotControlPanel.
     * @param visualization
     * @param filter
     */
    public ScatterPlotControlPanel(
        Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }

    /**
     * Returns the ScatterPlotVisualization.
     * 
     * @return the ScatterPlotVisualization.
     */
    public ScatterPlotVisualization getScatterPlot() {
        return (ScatterPlotVisualization)getVisualization().
            findVisualization(ScatterPlotVisualization.class);
    }

    /**
     * @see infovis.panel.ControlPanel#createStdVisualPane()
     */
    protected JComponent createVisualPanel() {
        return new ScatterPlotVisualPanel(getScatterPlot(), getFilter(), dynamicQueryPanel);
    }

}
