/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

/**
 * Class ReadOnlyColumnException
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ReadOnlyColumnException extends RuntimeException {

    public ReadOnlyColumnException() {
        super();
    }

    public ReadOnlyColumnException(String message) {
        super(message);
    }

    public ReadOnlyColumnException(Throwable cause) {
        super(cause);
    }

    public ReadOnlyColumnException(String message, Throwable cause) {
        super(message, cause);
    }

}
