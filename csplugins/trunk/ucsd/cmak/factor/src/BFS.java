import java.util.Iterator;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.list.IntArrayList;

import giny.model.RootGraph;

/**
 * A class that implements Breadth First Search
 *
 * 
 */ 
public class BFS
{
    final static int WHITE = 0;
    final static int GREY = 1;
    final static int BLACK = 2;
    
    final static int NIL = 0;

    private InteractionGraph ig;
    private RootGraph g;
    private int numNodes;
    private int numEdges;
        
    private int infinity;

    private int[] nodes;

    // map graph indices to a label in the set [0,numNodes]
    private OpenIntIntHashMap labelMap;

    private int[] color; // tracks node status
    private int[] pred; // predecessor
    private int[] dist; // distance from source
    
    public BFS(InteractionGraph graph)
    {
        ig = graph;
        g = ig.getRootGraph();
        numNodes = g.getNodeCount();
        numEdges = g.getEdgeCount();
        
        infinity = numEdges + 1;
        nodes = g.getNodeIndicesArray();

        // map graph indices to a label in the set [0,numNodes]
        labelMap = new OpenIntIntHashMap(numNodes);

        color = new int[numNodes];
        pred = new int[numNodes]; // predecessor
        dist = new int[numNodes]; // distance from source
    }

    public void traverse(int sourceNode)
    {
        // initialize data structures
        IntQueue Q = new IntQueue();
        labelMap.clear();
        for(int n = 0; n < numNodes; n++)
        {
            color[n] = WHITE;
            pred[n] = NIL;
            dist[n] = infinity;
            labelMap.put(nodes[n], n);
        }
        
        if(!labelMap.containsKey(sourceNode))
        {
            System.err.println(sourceNode + " is not a node in the graph");
            return;
        }

        int N = labelMap.get(sourceNode);
        color[N] = GREY;
        dist[N] = 0;
        pred[N] = NIL;
        Q.enqueue(sourceNode);

        while(Q.size() > 0)
        {
            /* print the Q for debugging
            System.out.print("Qs: ");
            for(Iterator qit = Q.iterator(); qit.hasNext();)
            {
                System.out.print(qit.next() + " ");
            }
            System.out.print("\n");
            */

            int u = Q.head();
            int lu = labelMap.get(u);
            int[] neighbors = getReachableNeighbors(u);
            for(int x=0; x < neighbors.length; x++)
            {
                int v = neighbors[x];
                int lv = labelMap.get(v);
                if(color[lv] == WHITE)
                {
                    color[lv] = GREY;
                    dist[lv] = dist[lu] + 1;
                    pred[lv] = u;
                    Q.enqueue(v);
                }
            }
            Q.dequeue();
            color[lu] = BLACK;
        }
    } // traverse

    private int[] getReachableNeighbors(int node)
    {
        // include undirected and outgoing edges
        int[] adj = g.getAdjacentEdgeIndicesArray(node, true, false, true);
        int[] neighbors;
        if(adj != null)
        {
            neighbors = new int[adj.length];

            for(int e=0; e < adj.length; e++)
            {
                int src = g.getEdgeSourceIndex(adj[e]);
                int tgt = g.getEdgeTargetIndex(adj[e]);
                if(node == src)
                {
                    neighbors[e] = tgt;
                }
                else
                {
                    neighbors[e] = src;
                }
            }
        }
        else
        {
            neighbors = new int[0];
        }
        
        return neighbors;
    }


    void trace()
    {
        if((color.length != dist.length) ||
           (color.length != pred.length))
        {
            System.out.println("trace: error: arrays of different length");
            return;
        }

        IntArrayList keys = labelMap.keys();
        for(int x=0; x < keys.size(); x++)
        {
            int node = keys.get(x);
            int l = labelMap.get(node);
            System.out.println(node + ":"
                               + " color=" + colorOf(color[l])
                               + " dist=" + dist[l]
                               + " pred=" + pred[l]);
        }
    }
    
    private String colorOf(int c)
    {
        switch(c)
        {
        case WHITE:
            return "white";
        case GREY:
            return "grey";
        case BLACK:
            return "black";
        default:
            return "none";
        }
    }

    boolean checkDist(int[][] expected)
    {
        return check(expected, dist);
    }


    boolean checkPred(int[][] expected)
    {
        return check(expected, pred);
    }

    boolean checkColor(int[][] expected)
    {
        return check(expected, color);
    }

    private boolean check(int[][] expected, int[] data)
    {
        for(int i =0; i < expected.length; i++)
        {
            int label = labelMap.get(expected[i][0]);
            if(data[label] != expected[i][1])
            {
                return false;
            }
        }

        return true;
    }
}
