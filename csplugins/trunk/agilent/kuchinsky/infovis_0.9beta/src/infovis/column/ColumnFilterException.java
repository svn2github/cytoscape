/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

/**
 * Class ColumnFilterException.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class ColumnFilterException extends RuntimeException {
    /**
     * Creator.
     * @param msg the error message.
     */
    public ColumnFilterException(String msg) {
        super(msg);
    }
}
