/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

/**
 * Column returning ONE for all its rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ColumnOne extends ConstantColumn {
    /**
     * An instance of this column.
     */
    public static final ColumnOne instance = new ColumnOne();
    
    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "#one";
    }
    
    /**
     * Returns the constant 1 for each row.
     * @param row the row
     * @return the constant 1 for each row
     */
    public double getDoubleAt(int row) {
        return 1;
    }

}
