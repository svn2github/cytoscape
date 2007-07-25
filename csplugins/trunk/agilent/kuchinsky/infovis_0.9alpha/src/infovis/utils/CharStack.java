/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.list.CharArrayList;

/**
 * Class CharStack
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class CharStack extends CharArrayList {

    public CharStack() {
        super();
    }

    public CharStack(char[] elements) {
        super(elements);
    }

    public CharStack(int initialCapacity) {
        super(initialCapacity);
    }

    public void push(char v) {
        add(v);
    }
    
    public char pop() {
        char v = getQuick(size()-1);
        remove(size()-1);
        return v;
    }
    
    public char top() {
        return getQuick(size()-1);
    }

}
