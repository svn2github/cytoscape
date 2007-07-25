import infovis.column.DoubleColumn;
import infovis.visualization.color.EqualizedOrderedColor;
import junit.framework.TestCase;
import cern.colt.list.DoubleArrayList;
import cern.jet.random.*;
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

/**
 * Class EqualizedOrderedColorTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class EqualizedOrderedColorTest extends TestCase {
    protected EqualizedOrderedColor colorVis;
    public EqualizedOrderedColorTest(String name) {
        super(name);
    }
    
    public void testEqualizedOrderedColor() {
        DoubleColumn col = new DoubleColumn("values");
        RandomEngine engine = RandomEngine.makeDefault();
        colorVis = new EqualizedOrderedColor(col);

        AbstractDistribution constant = new AbstractDistribution() {
            public double nextDouble() {
                return 50;
            }
        };
        testDistribution(col, constant, "Constant");
        testDistribution(col, new Normal(50, 50, engine), "Normal");
        testDistribution(col, new Logarithmic(0.9, engine), "Log");
        testDistribution(col, new Exponential(0.9, engine), "Exp");
    }
    
    private void testDistribution(
            DoubleColumn col,
            AbstractDistribution dist,
            String name) {
        for (int bits = 0; bits < 20; bits += 5) {
            int size = (1<<bits)-1;
            fillDistribution(col, dist, size);
            DoubleArrayList quantiles = colorVis.getQuantiles();
            if (quantiles == null) {
                assertEquals(0, size);
            }
            else {
                assertEquals(colorVis.getCacheSize(), quantiles.size()+1);
                int cnt = 0;
                for (int i = 1; i < 63; i++) {
                    assertTrue("Quantiles not sorted", 
                            quantiles.get(i-1)<=quantiles.get(i));
                    if (quantiles.get(i-1)==quantiles.get(i)) {
                        cnt++;
                    }
                }
                if (cnt != 0) {
                    System.out.println("Distribution "+name
                            +" equal quantiles: "+cnt
                            +" for size "+size);
                }
            }
        }
    }

    protected void fillDistribution(
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
