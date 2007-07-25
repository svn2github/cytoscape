/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.ShortArrayList;

/**
 * Class ShortStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ShortStack extends ShortArrayList {

    public ShortStack() {
        super();
    }

    public ShortStack(short[] elements) {
        super(elements);
    }

    public ShortStack(int initialCapacity) {
        super(initialCapacity);
    }

    public void push(short v) {
        add(v);
    }
    
    public short pop() {
        short v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public short top() {
        return getQuick(size()-1);
    }
}
