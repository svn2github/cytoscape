/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import infovis.Column;
import infovis.Visualization;
import infovis.column.HistogramColumn;
import infovis.column.NumberColumn;
import infovis.table.DefaultTable;
import infovis.visualization.Orientation;

import java.awt.Dimension;

/**
 * Visualization of histogram.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class HistogramVisualization extends ColumnVisualization {
    protected HistogramColumn histogram;
    
    /**
     * Constructor.
     * @param column the column to build a histogram.
     * @param bins number of bins.
     */
    public HistogramVisualization(NumberColumn column, int bins) {
        super(new DefaultTable(), new HistogramColumn(column, bins));
        histogram = (HistogramColumn)getColumn();
        getTable().addColumn(histogram);
    }
    
    /**
     * Constructor.
     * @param column the column to build a histogram.
     */
    public HistogramVisualization(NumberColumn column) {
        this(column, 50);
    }

    /**
     * @return Returns the histogram.
     */
    public HistogramColumn getHistogram() {
        return histogram;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean setColumn(Column column) {
        return false;
    }
    
    /**
     * Change the column of the histogram.
     * @param column the new column.
     */
    public void setHistogramColumn(NumberColumn column) {
        histogram.setColumn(column);
    }
    
    /**
     * Change the number of bins of the histogram.
     * @param bins the new bin number
     */
    public void setBins(int bins) {
        histogram.setSize(bins);
    }
    
    /**
     * Returns the number of bins of this histogram.
     * @return the number of bins of this histogram.
     */
    public int getBins() {
        return histogram.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize(Visualization vis) {
        if (Orientation.isHorizontal(getOrientation())) {
            return new Dimension(5 * histogram.size(), 50);
        }
        else {
            return new Dimension(50, 5 * histogram.size());
        }
    }

}
