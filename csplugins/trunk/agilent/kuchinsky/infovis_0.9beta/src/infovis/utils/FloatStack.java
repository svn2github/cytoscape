/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.FloatArrayList;

/**
 * Class FloatStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class FloatStack extends FloatArrayList {

    public FloatStack() {
        super();
    }

    public FloatStack(float[] elements) {
        super(elements);
    }

    public FloatStack(int initialCapacity) {
        super(initialCapacity);
    }
    
    public void push(float v) {
        add(v);
    }
    
    public float pop() {
        float v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public float top() {
        return getQuick(size()-1);
    }

}
