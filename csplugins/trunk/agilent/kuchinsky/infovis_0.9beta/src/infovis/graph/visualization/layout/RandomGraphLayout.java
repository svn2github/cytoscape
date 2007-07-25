/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import java.awt.geom.Rectangle2D.Float;

import cern.jet.random.engine.RandomEngine;

/**
 * Class RandomGraphLayout
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 * @infovis.factory GraphLayoutFactory "Random"
 */
public class RandomGraphLayout extends AbstractGraphLayout {
    protected RandomEngine engine;
    
    public static RandomGraphLayout instance;
    
    public RandomGraphLayout() {
    }

    public RandomGraphLayout(RandomEngine engine) {
        this.engine = engine;
    }
    
    public static RandomGraphLayout getInstance() {
        if (instance == null) {
            instance = new RandomGraphLayout();
        }
        return instance;
    }
    
    public static void setInstance(RandomGraphLayout i) {
        instance = i;
    }
    
    public double nextDouble() {
        return getEngine().nextDouble();
    }
    
    public Float initRect(Float rect) {
        rect.x = (float)(nextDouble() * bounds.getWidth());
        rect.y = (float)(nextDouble() * bounds.getHeight());
        
        return rect;
    }
    

    public RandomEngine getEngine() {
        if (engine == null) {
            engine = RandomEngine.makeDefault();
        }
        return engine;
    }

    public void setEngine(RandomEngine engine) {
        this.engine = engine;
        invalidateVisualization();
    }
}
