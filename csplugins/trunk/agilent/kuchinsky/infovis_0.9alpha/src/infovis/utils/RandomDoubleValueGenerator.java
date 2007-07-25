/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import cern.jet.random.engine.RandomEngine;

/**
 * Generate random values for initializing rows.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class RandomDoubleValueGenerator implements
        RowDoubleValueGenerator {
    protected RandomEngine engine;
    
    public RandomDoubleValueGenerator(RandomEngine engine) {
        this.engine = engine;
    }
    
    public RandomDoubleValueGenerator() {
        this(RandomEngine.makeDefault());
    }

    public double generate(int row) {
        return engine.apply(row);
    }

}
