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
 * Class FunctionColumn
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class FunctionColumn extends DoubleColumn implements
        ChangeListener {
    protected NumberColumn col;

    protected DoubleFunction fn;

    public FunctionColumn(NumberColumn col, DoubleFunction fn) {
        super(col.getName(), col.size());
        this.col = col;
        this.fn = fn;
        update();
    }

    public void stateChanged(ChangeEvent e) {
        update();
    }

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