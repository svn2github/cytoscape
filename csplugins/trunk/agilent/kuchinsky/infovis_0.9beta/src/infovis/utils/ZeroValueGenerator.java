/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

/**
 * Class ZeroValueGenerator
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class ZeroValueGenerator implements RowDoubleValueGenerator {
    public static final ZeroValueGenerator instance = new ZeroValueGenerator();

    public static ZeroValueGenerator getInstance() {
        return instance;
    }
    
    public ZeroValueGenerator() {
    }

    public double generate(int row) {
        return 0;
    }

}
