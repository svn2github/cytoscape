/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.column.HistogramColumn;
import infovis.column.NumberColumn;
import infovis.column.visualization.ColumnVisualization;
import infovis.table.DefaultTable;
import infovis.visualization.render.VisualArea;

import java.awt.geom.Rectangle2D;

/**
 * Class HistogramVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class HistogramVisualization extends ColumnVisualization {
    protected HistogramColumn histo;
    protected int samples;

    public HistogramVisualization(NumberColumn column, int samples) {
        super(new DefaultTable(), new HistogramColumn(samples, column));
        histo = (HistogramColumn)this.column;
        setVisualColumn(VisualArea.VISUAL, histo);

        setSamples(samples);
    }

    public HistogramVisualization(NumberColumn column) {
        this(column, 200);
    }

    public HistogramColumn getHistogram() {
        return histo;
    }

    public void setSamples(int bins) {
        this.samples = bins;
        getHistogram().setSize(bins);
    }
    
    public int getSamples() {
        return samples;
    }

    public boolean setColumn(NumberColumn column) {
        if (this.histo.getColumn() == column)
            return false;
        histo.setColumn(column);
        invalidate();
        return true;
    }

    public void computeShapes(Rectangle2D bounds) {
        if (bounds != null) {
            samples = (int) (bounds.getWidth() / 5);
            if (samples == 0)
                samples = 1;
            if (samples != histo.size()) {
                histo.setSize(samples);
            }
        }
        super.computeShapes(bounds);
    }
}
