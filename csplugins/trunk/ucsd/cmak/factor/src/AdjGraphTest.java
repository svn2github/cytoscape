import junit.framework.*;
import java.util.*;

import giny.model.RootGraph;

import cern.jet.random.sampling.RandomSampler;
import cern.jet.random.engine.MersenneTwister;
import cern.colt.list.IntArrayList;

public class AdjGraphTest extends TestCase
{
    AdjacencyListRootGraph g;
    
    protected void setUp()
    {
        g = new AdjacencyListRootGraph(1000, 5000);
    }

    protected void tearDown()
    {
        System.out.println(g.toString());
    }
    
    public void testCreateNode()
    {
        int num = 20;

        int[] n = new int[num];
        
        for(int x=0; x < num; x++)
        {
            int i = g.createNode();
            n[x] = i;
            
            System.out.println("created node: " + i);
        }
        
        IntArrayList graphNodes = new IntArrayList(g.getNodeIndicesArray());

        for(int x=0; x < num; x++)
        {
            assertTrue(graphNodes.contains(n[x]));
        }

        assertEquals("num nodes", num, g.getNodeCount());
    }

    public void testCreateEdge()
    {
        IntArrayList n = new IntArrayList();
        int N = 20;
        for(int x=0; x < N; x++)
        {
            n.add(g.createNode());
        }

        MersenneTwister mt = new MersenneTwister(new java.util.Date());
        
        long[] vals = new long[2];

        for(int x=0; x < N; x++)
        {
            RandomSampler.sample(2, N-1, 2, 0, vals, 0, mt);
            int e = g.createEdge(n.get((int) vals[0]),
                                 n.get((int) vals[1]));

            System.out.println("created edge: " + e);

            assertTrue(g.edgeExists(n.get((int) vals[0]),
                                    n.get((int) vals[1])));

            assertTrue( ! g.edgeExists(n.get((int) vals[1]),
                                       n.get((int) vals[0])));

            
        }

        assertEquals("num edges", N, g.getEdgeCount());
    }

    
    public void testCreateUndirectedEdge()
    {
        IntArrayList n = new IntArrayList();
        int N = 20;
        for(int x=0; x < N; x++)
        {
            n.add(g.createNode());
        }

        MersenneTwister mt = new MersenneTwister(new java.util.Date());
        
        long[] vals = new long[2];

        for(int x=0; x < N; x++)
        {
            RandomSampler.sample(2, N-1, 2, 0, vals, 0, mt);
            int e = g.createEdge(n.get((int) vals[0]),
                                 n.get((int) vals[1]), false);

            System.out.println("created edge: " + e);

            assertTrue(g.edgeExists(n.get((int) vals[0]),
                                    n.get((int) vals[1])));

            assertTrue(g.edgeExists(n.get((int) vals[1]),
                                    n.get((int) vals[0])));

        }

        assertEquals("num edges", N, g.getEdgeCount());
    }

    
  public void testCreation () throws Exception {
    // Node creation: the returned index should be negative.
    int node_index_0 = g.createNode();
    assertTrue( node_index_0 < 0 );
    // Each returned index should be unique.
    int node_index_1 = g.createNode();
    assertTrue( node_index_1 < 0 );
    assertTrue( node_index_1 != node_index_0 );

    // Edge creation: the returned index should be negative.
    int edge_index_0 = // undirected
      g.createEdge( node_index_0, node_index_1, false );

    assertTrue( edge_index_0 < 0 );
    // RootG node count
    int node_count = g.getNodeCount();
    assertTrue( node_count == 2 );
    // RootG edge count
    int edge_count = g.getEdgeCount();
    assertTrue( edge_count == 1 );
    // Node edge count, do count undirected edges
    edge_count =
      g.getEdgeCount( node_index_0, node_index_1, true );
    assertTrue("one edge", edge_count == 1 );

    int edge_index_1 = // directed
      g.createEdge( node_index_0, node_index_1, true );

    // Each returned index should be unique.
    assertTrue("ei < 0", edge_index_1 < 0 );
    assertTrue("ei unique",  edge_index_1 != edge_index_0 );
    // Node edge count, don't count undirected edges
      
    assertEquals("don't count undirected 0->1",
                 1, g.getEdgeCount( node_index_0, node_index_1, false ));
    
    
    assertEquals("don't count undirected 1->0",
                 0, g.getEdgeCount( node_index_1, node_index_0, false ));
    // Node edge count, do count undirected edges


    assertEquals("count undirected 0->1",
                 2, g.getEdgeCount( node_index_0, node_index_1, true ));


    assertEquals("count undirected 1->0",
                 1, g.getEdgeCount( node_index_1, node_index_0, true ));
  } // testColtRootG

}
