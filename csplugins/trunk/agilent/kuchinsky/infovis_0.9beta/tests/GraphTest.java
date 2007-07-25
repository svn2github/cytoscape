import infovis.Graph;
import infovis.graph.DefaultGraph;
import infovis.graph.DenseGraph;
import infovis.graph.algorithm.DijkstraShortestPath;
import infovis.utils.RowIterator;

import java.util.BitSet;

import junit.framework.TestCase;
import cern.colt.list.IntArrayList;

/**
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.18 $
 */
public class GraphTest extends TestCase {
    public GraphTest(String name) {
        super(name);
    }

    public void testGraph() {
        Graph graph = new DefaultGraph();

        //TableTest.testInvariants(graph.getEdgeTable());
        //TableTest.testInvariants(graph.getVertexTable());
        assertEquals(0, graph.getVerticesCount());
        
        int vertex = graph.addVertex();
        assertEquals(1, graph.getVerticesCount());
        
        int vertex2 = graph.addVertex();
        assertEquals(2, graph.getVerticesCount());
        
        int edge = graph.addEdge(vertex, vertex2);
        assertEquals(2, graph.getVerticesCount());
        assertEquals(graph.getOutEdgeAt(vertex, 0), edge);
        assertEquals(1, graph.getEdgesCount());
        assertEquals(graph.getSecondVertex(edge), vertex2);
        assertEquals(graph.getFirstVertex(edge), vertex);
        
        RowIterator iter = graph.outEdgeIterator(vertex);
        assertEquals(iter.nextRow(), edge);
        assertEquals("end of iterator", false, iter.hasNext());
        
        int vertex3 = graph.addVertex();
        assertEquals(3, graph.getVerticesCount());
        
        int edge2 = graph.addEdge(vertex, vertex3);
        assertEquals(2, graph.getEdgesCount());
        assertEquals(2, graph.getOutDegree(vertex));
        assertEquals(3, graph.getVerticesCount());
        
        iter = graph.outEdgeIterator(vertex);
        assertEquals(iter.nextRow(), edge);
        
        assertEquals(iter.nextRow(), edge2);
        
        assertEquals("end of iterator", false, iter.hasNext());
        
        assertEquals(graph.getFirstVertex(edge2), vertex);
        
        assertEquals(graph.getSecondVertex(edge2), vertex3);

        graph.removeVertex(vertex);
        assertEquals(2, graph.getVerticesCount());
        assertEquals(0, graph.getEdgesCount());
        assertEquals("invalid vertex", false, graph.getVertexTable().isRowValid(vertex));
        assertEquals("invalid edge", false, graph.getEdgeTable().isRowValid(edge));
        assertEquals("invalid edge", false, graph.getEdgeTable().isRowValid(edge2));
        
        iter = graph.vertexIterator();
        assertEquals(vertex2, iter.nextRow());
        assertEquals(vertex3, iter.nextRow());
        assertEquals("end of iterator", false, iter.hasNext());
        
        iter = graph.edgeIterator();
        assertEquals("end of iterator", false, iter.hasNext());
        
        vertex = graph.addVertex();
        assertEquals(3, graph.getVerticesCount());
        assertEquals(0, graph.getOutDegree(vertex));
        
        graph.clear();
        assertEquals("Empty graph", 0, graph.getVerticesCount());
        assertEquals("Empty graph", 0, graph.getEdgesCount());
        
        int i;
        for (i = 0; i < 100; i++) {
            assertEquals(i, graph.addVertex());
        }
        
        assertEquals(0, graph.addEdge(0, 0));
        for (i = 1; i < 100; i++) {
            assertEquals(i, graph.addEdge(i, i-1));
        }
        for (i = 0; i < 99; i++) {
            assertEquals(i+100, graph.addEdge(i, i+1));
        }
        
        for (i = 0; i < 9; i++) {
            graph.removeVertex(i*10+1);
            assertEquals(graph.getVerticesCount(), 100 - i - 1);
            // Vertex 0 has 3 edges, the others have 4 edges
            assertEquals(199 - 4*(i+1), graph.getEdgesCount());
        }
    }
    
    protected void checkRemoved(BitSet removed, Graph graph) {
        for (RowIterator iter = graph.edgeIterator(); iter.hasNext(); ) {
            int e = iter.nextRow();
            assertTrue("Edge "+e+" invalid", graph.getEdgeTable().isRowValid(e));
            if (graph.getFirstVertex(e) < 0) {
                int v1 = graph.getFirstVertex(e);
                int v2 = graph.getSecondVertex(e);
                assertTrue("Edge removed by vertex "+(-v1)+" or "+(-v2),false);
                assertTrue(
                        "Invalid 'in' vertex: "+graph.getFirstVertex(e),
                        graph.getVertexTable().isRowValid(graph.getFirstVertex(e)));
            }
            assertTrue(
                    "Invalid 'in' vertex: "+graph.getFirstVertex(e),
                    graph.getVertexTable().isRowValid(graph.getFirstVertex(e)));
            assertTrue(
                    "Invalid 'out' vertex: "+graph.getSecondVertex(e),
                    graph.getVertexTable().isRowValid(graph.getSecondVertex(e)));
            assertTrue(
                    "Edge "+e+" references vertex "+graph.getFirstVertex(e),
                    removed.get(graph.getFirstVertex(e)));
            
            assertTrue(
                    "Edge "+e+" references vertex "+graph.getSecondVertex(e),
                    removed.get(graph.getSecondVertex(e)));
        }
    }
    
    public void testInsertRemove() {
        DefaultGraph graph = new DefaultGraph();
        int u, v;
        IntArrayList ial = new IntArrayList(100);
        BitSet bs = new BitSet();

        for (v = 0; v < 100; v++) {
            assertEquals(v, graph.addVertex());
            ial.add(v);
            bs.set(v);
        }
        
        for (v = 0; v < 90; v++) {
            for (u = v; u < v+10; u++) {
                graph.addEdge(v, u);
            }
        }
//        cern.jet.random.Uniform gen = new cern.jet.random.Uniform(new cern.jet.random.engine.DRand(100));
//        for (int i=0; i<99; i++) { 
//            int random = gen.nextIntFromTo(i, 99);
//    
//            //swap(i, random)
//            int tmpElement = ial.getQuick(random);
//            ial.setQuick(random,ial.getQuick(i)); 
//            ial.setQuick(i,tmpElement); 
//        }          
        ial.shuffle();
        for (v = 0; v < 100; v++) {
            int i = ial.get(v);
            assertTrue("Vertex already removed "+i, graph.getVertexTable().isRowValid(i));
            graph.removeVertex(i);
            assertTrue("Vertex not removed "+i, !graph.getVertexTable().isRowValid(i));
            bs.clear(i);
            checkRemoved(bs, graph);
        }
    }
    
    public void testDenseGraph() {
        int size = 5;
        DenseGraph g = new DenseGraph(size);
        assertEquals(size, g.getVerticesCount());
        assertEquals(size*size, g.getEdgesCount());
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                assertEquals(i*size+j, g.findEdge(i, j));
            }
        }
        for (RowIterator vIter = g.vertexIterator(); vIter.hasNext(); ) {
            int v = vIter.nextRow();
            for (RowIterator eIter = g.outEdgeIterator(v); eIter.hasNext(); ) {
                int e = eIter.nextRow();
                System.out.print(e+" ");
            }
            System.out.println();
        }
        for (RowIterator vIter = g.vertexIterator(); vIter.hasNext(); ) {
            int v = vIter.nextRow();
            for (RowIterator eIter = g.inEdgeIterator(v); eIter.hasNext(); ) {
                int e = eIter.nextRow();
                System.out.print(e+" ");
            }
            System.out.println();
        }        
    }
    
    public void testDijkstra() {
        DefaultGraph g = new DefaultGraph();
        int v1 = g.addVertex();
        int v2 = g.addVertex();
        int v3 = g.addVertex();
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        DijkstraShortestPath dijkstra = new DijkstraShortestPath(g);
        DijkstraShortestPath.Predecessor p;
        p = dijkstra.shortestPath(v1, v1);
        assertEquals(0, (int)p.getWeight());
        p = dijkstra.shortestPath(v1, v2);
        assertEquals(1, (int)p.getWeight());
        p = dijkstra.shortestPath(v1, v3);
        assertEquals(2, (int)p.getWeight());
    }
}
