/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column;

import infovis.Column;

import java.util.Comparator;

/**
 * Compare Columns by name to sort column in alphabetic order.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class ColumnNameComparator implements Comparator {
    static ColumnNameComparator instance = new ColumnNameComparator();
    
    /**
     * Returns a shared instance of <code>ColumnNameComparator</code>.
     * 
     * @return a shared instance of <code>ColumnNameComparator</code>.
     */
    public static ColumnNameComparator sharedInstance() {
        return instance;
    }
    
    /**
     * @see java.util.Comparator#compare(Object, Object)
     */
    public int compare(Object o1, Object o2) {
        String s1 = (o1 instanceof String) ? (String)o1 : ((Column)o1).getName();
        String s2 = (o2 instanceof String) ? (String)o2 : ((Column)o2).getName();
        return s1.compareToIgnoreCase(s2);
    }
}
