/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.column.NumberColumn;
import infovis.panel.DefaultDoubleBoundedRangeModel;
import infovis.panel.DynamicQuery;
import infovis.utils.TableIterator;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Bounded range model used as a dynamic query for range sliders.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class NumberColumnBoundedRangeModel extends
        DefaultDoubleBoundedRangeModel implements ChangeListener, DynamicQuery {
    protected NumberColumn      column;
    protected FilterColumn      filter;
    protected DoubleRangeSlider component;

    /**
     * Constructor from a NumberColumn.
     * 
     * @param column
     *            the numbercolumn.
     */
    public NumberColumnBoundedRangeModel(NumberColumn column) {
        setColumn(column);
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == column) {
            update();
        }
    }

    /**
     * Recomputes the range when the column is modified.
     */
    public void update() {
        if (column.getMaxIndex() == -1 || column.getMinIndex() == -1) {
            if (component != null)
                component.setEnabled(false);
            return;
        }
        if (component != null && !component.isEnabled()) {
            component.setEnabled(true);
        }
        setRangeProperties(column.getDoubleMin(), column.getDoubleMax()
                - column.getDoubleMin(), column.getDoubleMin(), column
                .getDoubleMax(), false);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (column.isValueUndefined(row))
            return false;
        double v = column.getDoubleAt(row);
        double min = column.round(getValue());
        double max = column.round(getValue() + getExtent());
        boolean ret = !(v >= min && v <= max);
        // if (ret) {
        // assert(ret);
        // }
        return ret;
    }

    /**
     * Returns the column.
     * 
     * @return Column
     */
    public Column getColumn() {
        return column;
    }

    /**
     * Sets the column.
     * 
     * @param column
     *            The column to set
     */
    public void setColumn(NumberColumn column) {
        if (column == this.column)
            return;
        if (this.column != null)
            this.column.removeChangeListener(this);
        this.column = column;
        if (this.column != null)
            this.column.addChangeListener(this);
        update();
    }

    /**
     * {@inheritDoc}
     */
    public FilterColumn getFilterColumn() {
        return filter;
    }

    /**
     * {@inheritDoc}
     */
    public void setFilterColumn(FilterColumn filter) {
        if (this.filter != null) {
            this.filter.removeDynamicQuery(this);
        }
        this.filter = filter;
        if (this.filter != null) {
            this.filter.addDynamicQuery(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (filter != null) {
            filter.applyDynamicQuery(
                    this, 
                    new TableIterator(0, Math.max(filter.size(), column.size())));
        }
    }

    /*
     * {@inheritDoc}
     */
    protected void fireStateChanged() {
        super.fireStateChanged();
        apply();
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        if (component == null) {
            component = new DoubleRangeSlider(this);
            component.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            if (column.getMaxIndex() != -1)
                component.setEnabled(true);
        }
        return component;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public static class Creator implements DynamicQueryFactory.Creator {
//        public DynamicQuery create(Column c, String type) {
//            int category = ValueCategory.findValueCategory(c);
//
//            if (c instanceof NumberColumn
//                    && category != ValueCategory.TYPE_CATEGORIAL) {
//                NumberColumn number = (NumberColumn) c;
//                return new NumberColumnBoundedRangeModel(number);
//            }
//            return null;
//        }
//    };
}
