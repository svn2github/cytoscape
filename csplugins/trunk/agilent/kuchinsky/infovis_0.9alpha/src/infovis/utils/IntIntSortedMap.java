/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.utils;

import cern.colt.function.IntComparator;

/**
 * Int Int Map
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class IntIntSortedMap extends RBTree {
    public IntIntSortedMap(IntIntSortedMap other) {
        super(other);
    }

    public IntIntSortedMap() {
        super();
    }

    public IntIntSortedMap(IntComparator comparator) {
        super(comparator);
    }

    public void insert(int key, int value) {
        throw new RuntimeException("Insert not allowed in IntIntSortedMap");
    }
}

