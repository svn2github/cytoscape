/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.IntArrayList;

/**
 * Class IntStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class IntStack extends IntArrayList {

    public IntStack() {
        super();
    }

    public IntStack(int[] elements) {
        super(elements);
    }

    public IntStack(int initialCapacity) {
        super(initialCapacity);
    }
    
    public void push(int v) {
        add(v);
    }
    
    public int pop() {
        int v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public int top() {
        return getQuick(size()-1);
    }
    
    public void setTop(int v) {
        setQuick(size()-1, v);
    }
}
