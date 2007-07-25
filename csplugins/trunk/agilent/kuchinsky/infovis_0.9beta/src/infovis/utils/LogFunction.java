/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.colt.function.DoubleFunction;

/**
 * Class LogFunction
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class LogFunction implements DoubleFunction {
    private static final LogFunction instance = new LogFunction(); 
    
    public static LogFunction getInstance() {
        return instance;
    }
    
    protected LogFunction() {
    }

    public double apply(double v) {
        return Math.log(v);
    }
}
