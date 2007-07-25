/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Holds a pair of double values for any purpose
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DoublePair {
    public double first;
    public double second;
    
    public DoublePair() {
    }
    
    public DoublePair(double first, double second) {
        this.first = first;
        this.second = second;
    }
    
    public DoublePair(DoublePair other) {
        this.first = other.first;
        this.second = other.second;
    }

    public double getFirst() {
        return first;
    }

    public void setFirst(double first) {
        this.first = first;
    }

    public double getSecond() {
        return second;
    }

    public void setSecond(double second) {
        this.second = second;
    }
}
