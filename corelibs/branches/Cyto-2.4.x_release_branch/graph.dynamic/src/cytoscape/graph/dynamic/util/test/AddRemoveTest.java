package cytoscape.graph.dynamic.util.test;

import cytoscape.graph.dynamic.DynamicGraph;
import cytoscape.graph.dynamic.util.DynamicGraphFactory;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class AddRemoveTest {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        final DynamicGraph graph = DynamicGraphFactory.instantiateDynamicGraph();
        final int[][] nodesArr = new int[][] { new int[100000], new int[99980], new int[100010] };
        final int[] edges = new int[1000000];
        final int iterations = 10000;

        for (int foo = 0; foo < iterations; foo++) {
            boolean print = false;

            if ((foo % 10) == 0) {
                print = true;
            }

            if (print) {
                System.out.println("at add/remove iteration " + (foo + 1) + " of " + iterations);
            }

            if (print) {
                System.out.println("creating nodes");
            }

            final int[] nodes = nodesArr[foo % nodesArr.length];

            for (int i = 0; i < nodes.length; i++)
                nodes[i] = graph.nodeCreate();

            if (print) {
                System.out.println("creating edges");
            }

            for (int i = 0; i < edges.length; i++)
                edges[i] = graph.edgeCreate(nodes[i % nodes.length], nodes[(i * 3) % nodes.length],
                        true);

            if (print) {
                System.out.println("in graph: " + graph.nodes().numRemaining() + " nodes and " +
                    graph.edges().numRemaining() + " edges");
            }

            if (print) {
                System.out.println();
            }

            if (print) {
                System.out.println("removing edges");
            }

            for (int i = 0; i < edges.length; i++)
                graph.edgeRemove(edges[i]);

            if (print) {
                System.out.println("removing nodes");
            }

            for (int i = 0; i < nodes.length; i++)
                graph.nodeRemove(nodes[i]);

            if (print) {
                System.out.println("in graph: " + graph.nodes().numRemaining() + " nodes and " +
                    graph.edges().numRemaining() + " edges");
            }

            if (print) {
                System.out.println();
            }
        }
    }
}
