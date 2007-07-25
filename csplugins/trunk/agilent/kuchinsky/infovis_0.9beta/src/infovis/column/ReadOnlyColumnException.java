/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

/**
 * Exception raised when attempting to change a readonly column.  
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class ReadOnlyColumnException extends RuntimeException {

    /**
     *  Default constructor.
     */
    public ReadOnlyColumnException() {
        super();
    }

    /**
     * Constructor with an error message.
     * @param message the error message.
     */
    public ReadOnlyColumnException(String message) {
        super(message);
    }
}
