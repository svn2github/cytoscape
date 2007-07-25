/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cern.colt.function.DoubleFunction;

/**
 * Applies a unary function to a column and
 * maintains the result into this column. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class FunctionColumn extends DoubleColumn implements
        ChangeListener {
    protected NumberColumn col;

    protected DoubleFunction fn;

    /**
     * Create a FunctionColumn from a NumberColumn and a
     * DoubleFunction.
     * @param col the NumberColumn
     * @param fn the DoubleFunction
     */
    public FunctionColumn(NumberColumn col, DoubleFunction fn) {
        super(col.getName(), col.size());
        this.col = col;
        this.fn = fn;
        update();
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        update();
    }

    /**
     * Recompute the new values when the initial column
     * has been modified.
     *
     */
    public void update() {
        try {
            disableNotify();
            clear();
            for (int i = 0; i < col.size(); i++) {
                if (col.isValueUndefined(i)) {
                    setValueUndefined(i, true);
                } else {
                    set(i, fn.apply(col.getDoubleAt(i)));
                }
            }
        } finally {
            enableNotify();
        }
    }
}