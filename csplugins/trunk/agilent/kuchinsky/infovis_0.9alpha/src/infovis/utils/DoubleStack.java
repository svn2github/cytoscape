/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.DoubleArrayList;

/**
 * Class DoubleStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class DoubleStack extends DoubleArrayList {

    public DoubleStack() {
        super();
    }

    public DoubleStack(double[] elements) {
        super(elements);
    }

    public DoubleStack(int initialCapacity) {
        super(initialCapacity);
    }

    public void push(double v) {
        add(v);
    }
    
    public double pop() {
        double v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public double top() {
        return getQuick(size()-1);
    }
}
