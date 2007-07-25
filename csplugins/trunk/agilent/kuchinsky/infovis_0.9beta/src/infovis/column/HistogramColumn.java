/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import hep.aida.ref.Histogram1D;
import infovis.utils.RowComparator;
import infovis.utils.RowIterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cern.colt.list.IntArrayList;

/**
 * Column computing and maintaining the histogram of a specified
 * {@link NumberColumn}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class HistogramColumn extends IntColumn implements ChangeListener {
    protected Histogram1D     histo;
    protected NumberColumn    column;
    private transient boolean valid;

    /**
     * Constructor.
     * 
     * @param column
     *            the column.
     * @param bins
     *            the number of bins.
     */
    public HistogramColumn(NumberColumn column, int bins) {
        super("#BinsFor_" + column.getName(), bins);
        this.column = column;
        for (int i = 0; i < bins; i++) {
            add(0);
        }
        column.addChangeListener(this);
        valid = false;
    }

    /**
     * Constructor.
     * 
     * @param column
     *            the column.
     */
    public HistogramColumn(NumberColumn column) {
        this(column, 200);
    }

    /**
     * Returns the column from which this histogram is built.
     * 
     * @return the column from which this histogram is built.
     */
    public NumberColumn getColumn() {
        return column;
    }

    /**
     * Sets the column from which the histogram is built.
     * 
     * @param col
     *            the column from which the histogram is built.
     * @return true if the column has changed.
     */
    public boolean setColumn(NumberColumn col) {
        if (column == col) {
            return false;
        }
        clear();
        column.removeChangeListener(this);
        column = col;
        column.addChangeListener(this);
        valid = false;
        return true;
    }

    protected void validate() {
        if (!valid) {
            valid = true;
            try {
                disableNotify();
                if (getMinIndex() == -1) {
                    for (int i = 0; i < size(); i++) {
                        setValueUndefined(i, true);
                    }
                    return;
                }
                Histogram1D histo = new Histogram1D(getName(), size(), column
                        .getDoubleMin(), column.getDoubleMax());
                NumberColumn col = column;
                for (RowIterator i = col.iterator(); i.hasNext();) {
                    histo.fill(col.getDoubleAt(i.nextRow()));
                }
                for (int i = 0; i < size(); i++) {
                    super.set(i, (int) histo.binHeight(i));
                }
            } finally {
                enableNotify();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        validate();
        return super.size();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        readonly();
    }

    /**
     * {@inheritDoc}
     */
    public void setSize(int newSize) {
        if (size() != newSize) {
            super.setSize(newSize);
            valid = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            valid = false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int get(int index) {
        validate();
        return super.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        validate();
        return super.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray() {
        validate();
        return super.toArray();
    }

    /**
     * {@inheritDoc}
     */
    public int[] toArray(int[] a) {
        validate();
        return super.toArray(a);
    }

    /**
     * {@inheritDoc}
     */
    public void sort(RowComparator comp) {
        validate();
        super.sort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public void stableSort(RowComparator comp) {
        validate();
        super.stableSort(comp);
    }

    /**
     * {@inheritDoc}
     */
    public IntArrayList getValueReference() {
        validate();
        return super.getValueReference();
    }

    /**
     * {@inheritDoc}
     */
    public void set(int index, int element) {
        readonly();
    }

    /**
     * Releases listeners.
     */
    public void dispose() {
        column.removeChangeListener(this);
        clear();
        column = null;
    }
}
