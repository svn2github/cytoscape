/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis;

import infovis.metadata.Constants;

import java.util.Map;

/**
 * Metadata interface and constants for qualifying columns and tables.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public interface Metadata extends Constants {
    /**
     * Returns the Map associating keys to values for qualifying
     * the data contained in the instance of this interface.
     * 
     * @return the map associating metadate keys to values.
     */
    public Map getMetadata();
    
    /**
     * Returns a Map associating keys to values holding
     * user-supplied data.
     * 
     * @return a Map holding user-supplied keys/values.
     */
    public Map getClientPropery();
}
