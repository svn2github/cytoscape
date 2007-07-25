/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Holds a pair of int values for any purpose
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class IntPair {
    public int first;
    public int second;

    public IntPair() {
    }
    
    public IntPair(int first, int second) {
        this.first = first;
        this.second = second;
    }
    
    public IntPair(IntPair other) {
        this.first = other.first;
        this.second = other.second;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
