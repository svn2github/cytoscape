/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import hep.aida.IAxis;
import hep.aida.ref.Histogram1D;
import infovis.Column;
import infovis.column.BasicColumn;
import infovis.column.NumberColumn;
import infovis.utils.RowIterator;

import java.text.ParseException;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Column computing and maintaining the histogram of a specified 
 * {@link NumberColumn}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class HistogramColumn extends BasicColumn 
    implements NumberColumn, ChangeListener {
    protected Histogram1D histo;
    protected transient int[] minmax;
    protected NumberColumn column;
    
    public HistogramColumn(int bins, NumberColumn column) {
        super("#BinsFor_"+column.getName());
        this.column = column;
        setSize(bins);
    }
    
    public NumberColumn getColumn() {
        return column;
    }
    
    public boolean setColumn(NumberColumn col) {
        if (column == col) {
            return false;
        }
        clear();
        column.removeChangeListener(this);
        column = col;
        column.addChangeListener(this);
        setSize(size()); // force recompute
        return true;
    }
    
    public Histogram1D getHistogram() {
        return histo;
    }
    
    public int size() {
        return histo.xAxis().bins();
    }
    
    public boolean hasUndefinedValue() {
        return false;
    }

    public void clear() {
        readonly();
    }
    
    public void ensureCapacity(int minCapacity) {
        readonly();
    }

    public void setSize(int newSize) {
        int minIndex = column.getMinIndex();
        int maxIndex = column.getMaxIndex();
        if (minIndex == maxIndex) return;
        histo = new Histogram1D(
                getName(),
                newSize,
                column.getDoubleAt(column.getMinIndex()),
                column.getDoubleAt(column.getMaxIndex()));
        NumberColumn col = column;
        histo.reset();
        for (RowIterator i = col.iterator(); i.hasNext(); ) {
            histo.fill(col.getDoubleAt(i.nextRow()));
        }
        minmax = null;
        modified();
    }
    
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            setSize(size());
        }
    }

    protected int[] getMinMax() {
        if (minmax == null) {
            minmax = histo.minMaxBins();
        }
        return minmax;
    }

    public int getIntAt(int row) {
        return (int)getDoubleAt(row);
    }

    public float getFloatAt(int row) {
        return (float)getDoubleAt(row);
    }

    public long getLongAt(int row) {
        return (long)getDoubleAt(row);
    }

    public double getDoubleAt(int row) {
        return histo.binEntries(row);
    }

    public void setIntAt(int row, int v) {
        readonly();
    }

    public void setFloatAt(int row, float v) {
        readonly();
    }

    public void setLongAt(int row, long v) {
        readonly();
    }

    public void setDoubleAt(int row, double v) {
        readonly();
    }

    public int getIntMin() {
        return (int)getDoubleMin();
    }

    public int getIntMax() {
        return (int)getDoubleMax();
    }

    public float getFloatMin() {
        return (float)getDoubleMin();
    }

    public long getLongMin() {
        return (long)getDoubleMin();
    }

    public long getLongMax() {
        return (long)getDoubleMax();
    }

    public float getFloatMax() {
        return (float)getDoubleMax();
    }

    public double getDoubleMin() {
        return getDoubleAt(histo.minMaxBins()[0]);
    }

    public double getDoubleMax() {
        return getDoubleAt(histo.minMaxBins()[1]);
    }

    public double round(double value) {
        return value;
    }

    public void setName(String name) {
        super.setName(name);
        setSize(size());
    }

    public boolean isInternal() {
        return true;
    }

    public boolean isValueUndefined(int i) {
        return i >= size();
    }

    public void setValueUndefined(int i, boolean undef) {
        readonly();
    }

    public void copyFrom(Column from) {
        readonly();
    }

    public int capacity() {
        return size();
    }
    
    public String getValueAt(int index) {
        double v = getDoubleAt(index);
        IAxis a = histo.xAxis();
        String min = column.format(a.binLowerEdge(index));
        String max = column.format(a.binUpperEdge(index));
        return "["+min+","+max+"]="+v; 
    }

    public void setValueAt(int index, String element)
        throws ParseException {
        readonly();
    }

    public Class getValueClass() {
        return Double.class;
    }

    public Histogram1D getHistogramReference() {
        return histo;
    }
    
    public String format(double value) {
        return getValueAt((int)value);
    }


}
