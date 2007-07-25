/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.function.IntIntFunction;

/**
 * Class MinFunction
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class MinFunction implements IntIntFunction {
    public static final MinFunction instance = new MinFunction();

    public int apply(int a, int b) {
        return Math.min(a, b);
    }

}
