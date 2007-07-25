/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.LongArrayList;

/**
 * Class LongStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class LongStack extends LongArrayList {

    public LongStack() {
        super();
    }

    public LongStack(long[] elements) {
        super(elements);
    }

    public LongStack(int initialCapacity) {
        super(initialCapacity);
    }
    
    public void push(long v) {
        add(v);
    }
    
    public long pop() {
        long v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public long top() {
        return getQuick(size()-1);
    }
}
