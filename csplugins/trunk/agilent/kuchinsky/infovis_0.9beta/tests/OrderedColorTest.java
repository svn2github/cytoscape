import infovis.column.DoubleColumn;
import infovis.column.NumberColumn;
import infovis.visualization.color.OrderedColor;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;

import junit.framework.TestCase;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

public class OrderedColorTest extends TestCase {
    private OrderedColor colorVis;
    
    public OrderedColorTest(String name) {
        super(name);
    }
    
    public void testOrderedColor() {
        DoubleColumn col = new DoubleColumn("values");
        RandomEngine engine = RandomEngine.makeDefault();
        colorVis = new OrderedColor(col);
        test1000(col);
        test01(col, engine);
        Normal dist = new Normal(-50, 50, engine);
        testDistribution(col, dist);
    }

    private void test1000(DoubleColumn col) {
        col.disableNotify();
        col.clear();
        for (int i = 0; i < 257; i++) {
            col.add(i);
        }
        col.enableNotify();
        testLinearDistribution(col);
    }
    
    private void test01(DoubleColumn col, RandomEngine engine) {
        col.disableNotify();
        col.clear();
        for (int i = 0; i < 100; i++) {
            col.add(engine.nextDouble() < 0.5 ? 0 : 1);
            if (engine.nextDouble() > 0.8) {
                col.setValueUndefined(col.size()-1, true);
            }
        }
        col.enableNotify();
        int size = col.size();
        HashMap map = new HashMap();
        for (int i = 0; i < size; i++) {
            Color c = colorVis.getColor(i);
            if (c == null) continue;
            Object o = map.get(c);
            if (o == null) {
                map.put(c, new Integer(1));
            }
            else {
                map.put(c, new Integer(((Integer)o).intValue()+1));
            }
        }
        assertEquals(2, map.size());
    }

    
    private void testDistribution(
            DoubleColumn col,
            AbstractDistribution dist) {
        for (int bits = 0; bits < 20; bits += 5) {
            int size = (1<<bits)-1;
            fillDistribution(col, dist, size);
            
        }
    }
    
    private void testLinearDistribution(NumberColumn col) {
        int size = col.size();
        HashMap map = new HashMap();
        for (int i = 0; i < size; i++) {
            Color c = colorVis.getColor(i);
            Object o = map.get(c);
            if (o == null) {
                map.put(c, new Integer(1));
            }
            else {
                map.put(c, new Integer(((Integer)o).intValue()+1));
            }
        }
        assertEquals(colorVis.getCacheSize(),map.size());
        float load = (float)size / colorVis.getCacheSize();
        for (Iterator iter = map.values().iterator(); iter.hasNext(); ) {
            Integer val = (Integer)iter.next();
            int v = val.intValue();
            assertTrue(Math.abs(v-load) <= 1);
        }
    }
    
    private void fillDistribution(
            DoubleColumn col,
            AbstractDistribution dist,
            int size) {
        try {
            col.disableNotify();
            col.clear();
            for (int i = 0; i < size; i++) {
                col.add(dist.nextDouble());
            }
        } finally {
            col.enableNotify();
        }
    }
}
