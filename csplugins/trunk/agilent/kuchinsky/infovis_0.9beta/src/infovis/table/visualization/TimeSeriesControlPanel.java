/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import javax.swing.JComponent;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.panel.ControlPanel;

/**
 * Class TimeSeriesControlPanel
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory ControlPanelFactory infovis.table.visualization.TimeSeriesVisualization
 */
public class TimeSeriesControlPanel extends ControlPanel {

    public TimeSeriesControlPanel(Visualization visualization) {
        super(visualization);
    }

    public TimeSeriesControlPanel(
        Visualization visualization,
        ColumnFilter filter) {
        super(visualization, filter);
    }

    public TimeSeriesVisualization getTimeSeries() {
        return (TimeSeriesVisualization)getVisualization().
            findVisualization(TimeSeriesVisualization.class);
    }
    
    protected JComponent createVisualPanel() {
        return new TimeSeriesVisualPanel(getTimeSeries(), getFilter());
    }

}
