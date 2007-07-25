/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Class CaseInsensitiveComparator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class CaseInsensitiveComparator implements Comparator, Serializable {
    public static final CaseInsensitiveComparator sharedInstance = new CaseInsensitiveComparator();
    
    public static CaseInsensitiveComparator getSharedInstance() {
        return sharedInstance;
    }
    
    public int compare(Object o1, Object o2) {
        String s1 = (String) o1;
        String s2 = (String) o2;
        
        return s1.compareToIgnoreCase(s2);
    }
}
