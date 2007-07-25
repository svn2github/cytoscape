import infovis.Graph;
import infovis.column.IntColumn;
import infovis.graph.DefaultGraph;
import infovis.graph.algorithm.KCoreDecomposition;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class CorenessTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class CorenessTest extends TestCase {

    public CorenessTest(String name) {
        super(name);
    }
    
    public void testCoreness() {
        Graph g = new DefaultGraph();
        g.setDirected(false);
        
        for (int i = 0; i < 16; i++) {
            assertEquals(i, g.addVertex());
        }
        g.addEdge(0, 9);
        g.addEdge(1, 9);
        g.addEdge(2, 8);
        g.addEdge(3, 4);
        g.addEdge(4, 7);
        g.addEdge(4, 8);
        g.addEdge(5, 6);
        g.addEdge(5, 7);
        g.addEdge(6, 7);
        g.addEdge(7, 8);
        g.addEdge(7, 10);
        g.addEdge(7, 9);
        g.addEdge(8, 9);
        g.addEdge(8, 10);
        g.addEdge(9, 10);
        g.addEdge(11, 12);
        g.addEdge(12, 13);
        g.addEdge(12, 14);
        g.addEdge(12, 15);
        g.addEdge(13, 14);
        g.addEdge(13, 15);
        g.addEdge(14, 15);
        IntColumn coreness = KCoreDecomposition.computeCoreness(g, null);

        assertEquals(1, coreness.get(0));
        assertEquals(1, coreness.get(1));
        assertEquals(1, coreness.get(2));
        assertEquals(1, coreness.get(3));
        assertEquals(1, coreness.get(11));
      
        assertEquals(2, coreness.get(4));
        assertEquals(2, coreness.get(5));
        assertEquals(2, coreness.get(6));
        
        assertEquals(3, coreness.get(7));
        assertEquals(3, coreness.get(8));
        assertEquals(3, coreness.get(9));
        assertEquals(3, coreness.get(10));
        assertEquals(3, coreness.get(12));
        assertEquals(3, coreness.get(13));
        assertEquals(3, coreness.get(14));
        assertEquals(3, coreness.get(15));
    }

}
