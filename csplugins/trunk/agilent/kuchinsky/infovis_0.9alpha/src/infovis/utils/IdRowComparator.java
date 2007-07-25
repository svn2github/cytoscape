/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class IdRowComparator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IdRowComparator implements RowComparator {
    private static final IdRowComparator instance = new IdRowComparator();
    
    public static IdRowComparator getInstance() {
        return instance;
    }

    public boolean isValueUndefined(int row) {
        return false;
    }

    public int compare(int a, int b) {
        return a-b;
    }

}
