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
 * Class MaxFunction
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class MaxFunction implements IntIntFunction {
    public static final MaxFunction instance = new MaxFunction();

    public int apply(int a, int b) {
        return Math.max(a, b);
    }

}
