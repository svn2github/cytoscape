/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.table.visualization;

import infovis.Column;
import infovis.column.ColumnFilter;
import infovis.column.filter.*;
import infovis.panel.*;
import infovis.panel.FilteredColumnListModel;
import infovis.panel.DefaultVisualPanel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;

import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;


/**
 * DOCUMENT ME!
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class ScatterPlotVisualPanel extends DefaultVisualPanel {
    protected FilteredColumnListModel xAxisModel;
    protected JComboBox               xAxisCombo;
    protected FilteredColumnListModel yAxisModel;
    protected JComboBox               yAxisCombo;
    protected DynamicQueryPanel       dqPanel;

    /**
     * Constructor for ScatterPlotVisualPanel.
     *
     * @param visualization
     * @param filter
     */
    public ScatterPlotVisualPanel(ScatterPlotVisualization visualization,
                                  ColumnFilter filter,
                                  DynamicQueryPanel dqPanel) {
        super(visualization, filter);
        this.dqPanel = dqPanel;
        getScatterPlot().setXAxisModel(getModelFor(getScatterPlot().getXAxisColumn()));
        getScatterPlot().setYAxisModel(getModelFor(getScatterPlot().getYAxisColumn()));
    }

    /**
     * Returns the ScatterPlotVisualization.
     * 
     * @return the ScatterPlotVisualization.
     */
    public ScatterPlotVisualization getScatterPlot() {
        return (ScatterPlotVisualization)getVisualization()
            .findVisualization(ScatterPlotVisualization.class);
    }

    /**
     * @see infovis.panel.DefaultVisualPanel#createAll(Visualization)
     */
    protected void createAll() {
        super.createAll();
        addXAxis();
        addYAxis();
    }

    protected void addXAxis() {
        xAxisModel = new FilteredColumnListModel(table, filter);
        xAxisModel.setFilter(new ComposeOrFilter(InternalFilter.sharedInstance(),
                                                 NotStringOrNumberFilter.sharedInstance()));
        xAxisModel.setNullAdded(true);
        xAxisCombo = createJCombo(xAxisModel,
                                  getScatterPlot().getXAxisColumn(),
                                  "X Axis by");
    }

    protected void addYAxis() {
        yAxisModel = new FilteredColumnListModel(table, filter);
        yAxisModel.setFilter(new ComposeOrFilter(InternalFilter.sharedInstance(),
                                                 NotStringOrNumberFilter.sharedInstance()));
        yAxisCombo = createJCombo(yAxisModel,
                                  getScatterPlot().getYAxisColumn(),
                                  "Y Axis by");
    }
    
    /**
     * @see infovis.panel.DefaultVisualPanel#contentsChanged(ListDataEvent)
     */
    public void contentsChanged(ListDataEvent e) {
        if (e.getSource() == xAxisModel) {
            Column column = (Column)xAxisCombo.getSelectedItem();
            getScatterPlot().setXAxisColumn(column);
            getScatterPlot().setXAxisModel(getModelFor(column));
        }
        else if (e.getSource() == yAxisModel) {
            Column column = (Column)yAxisCombo.getSelectedItem();
            getScatterPlot().setYAxisColumn(column);
            getScatterPlot().setYAxisModel(getModelFor(column));
        }
        else
            super.contentsChanged(e);
    }
    
    public NumberColumnBoundedRangeModel getModelFor(Column column) {
        if (dqPanel == null)
            return null;
        DynamicQuery dq = dqPanel.getColumnDynamicQuery(column);
        if (dq instanceof NumberColumnBoundedRangeModel) {
            NumberColumnBoundedRangeModel ret = (NumberColumnBoundedRangeModel) dq;
            return ret;
        }
        return null;
    }

}
