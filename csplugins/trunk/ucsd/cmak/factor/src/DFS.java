import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.list.IntArrayList;

import giny.model.RootGraph;

/**
 * A class that implements Depth First Search
 *
 * 
 */ 
public class DFS
{
    final static int WHITE = 0; // undiscovered
    final static int GREY = 1; // discovered
    final static int BLACK = 2; // finished
    
    final static int NIL = 0;
    
    private RootGraph g;
    private int numNodes;
    private int numEdges;
        
    private int infinity;

    private int[] nodes;

    // map graph indices to a label in the set [0,numNodes]
    private OpenIntIntHashMap labelMap;

    private int[] color; // tracks node status
    private int[] pred; // predecessor
    private int[] dt; // discovery time
    private int[] ft; // finish time
        
    private int time;

    public DFS(RootGraph graph)
    {
        g = graph;
        numNodes = g.getNodeCount();
        numEdges = g.getEdgeCount();
        
        infinity = numEdges + 1;
        nodes = g.getNodeIndicesArray();

        // map graph indices to a label in the set [0,numNodes]
        labelMap = new OpenIntIntHashMap(numNodes);

        color = new int[numNodes];
        pred = new int[numNodes]; // predecessor
        dt = new int[numNodes]; // discovery time
        ft = new int[numNodes]; // finish time
    }

    /**
     * Traverse the graph using depth first search.
     * @return the number of trees in the graph
     *
    public int traverse()
    {
        // initialize data structures
        labelMap.clear();
        for(int x = 0; x < numNodes; x++)
        {
            color[x] = WHITE;
            pred[x] = NIL;
            dt[x] = 0;
            ft[x] = 0;
            labelMap.put(nodes[x], x);
        }

        time = 0;
        int trees = 0;
        for(int x=0; x < nodes.length; x++)
        {
            int label = labelMap.get(nodes[x]);

            if(color[label] == WHITE)
            {
                _dfsVisit(nodes[x], label);
                trees++;
            }
        }

        return trees;
    }
    */

    public GraphForrest traverse()
    {
        // initialize data structures
        labelMap.clear();
        for(int x = 0; x < numNodes; x++)
        {
            color[x] = WHITE;
            pred[x] = NIL;
            dt[x] = 0;
            ft[x] = 0;
            labelMap.put(nodes[x], x);
        }

        time = 0;
        GraphForrest forrest = new GraphForrest();
        
        for(int x=0; x < nodes.length; x++)
        {
            int label = labelMap.get(nodes[x]);

            if(color[label] == WHITE)
            {
                _dfsVisit(nodes[x], label, forrest.newTree());
            }
        }

        return forrest;
    }


    
    /**
     * @return time
     */
    private void _dfsVisit(int node, int label, IntArrayList tree)
    {
        color[label] = GREY;
        time++;
        dt[label] = time;

        tree.add(node);
        
        int[] neighbors = _getReachableNeighbors(node);
        for(int n=0; n < neighbors.length; n++)
        {
            int ln = labelMap.get(neighbors[n]);
            if(color[ln] == WHITE)
            {
                pred[ln] = node;
                _dfsVisit(neighbors[n], ln, tree);
            }
        }
        color[label] = BLACK;
        time++;
        ft[label] = time;
    }

    /**
     * Enumerate all paths from the sourceNode up to length
     * maxDepth
     *
     * @return a List of int[] objects.  Each int[] is a path.
     * The sourceNode is the first element in the array.
     * Each element of the int[] array is a node in the path.
     */
    public List genPaths(int sourceNode, int maxDepth)
    {
        // initialize data structures
        labelMap.clear();
        for(int n = 0; n < numNodes; n++)
        {
            color[n] = WHITE;
            pred[n] = NIL;
            dt[n] = 0;
            ft[n] = 0;
            labelMap.put(nodes[n], n);
        }

        time = 0;

        if(!labelMap.containsKey(sourceNode))
        {
            System.err.println(sourceNode + " is not a node in the graph");
            return new ArrayList();
        }

        int l = labelMap.get(sourceNode);

        List paths = new ArrayList();
        dfsPath(sourceNode, l, maxDepth, new IntArrayList(), paths);

        return paths;
    }

    /**
     * @return time
     */
    protected void dfsPath(int node, int label, int maxDepth, 
                            IntArrayList branch, List paths)
    {
        //        trace();
        color[label] = GREY; // GREY == node is in the current path
        //time++;
        //dt[label] = time;
        
        branch.add(node);

        //printPath(branch);
        addPath(paths, branch);

        if(branch.size() < maxDepth)
        {
            int[] adj = _getReachableNeighbors(node);

            for(int n=0; n < adj.length; n++)
            {
                int ln = labelMap.get(adj[n]);
                if(color[ln] != GREY)
                {
                    dfsPath(adj[n], ln, maxDepth, branch, paths);
                }
            }
        }

        color[label] = WHITE;
        //time++;
        //ft[label] = time;
        branch.remove(branch.size() - 1);
    }

    protected void addPath(List paths, IntArrayList branch)
    {
        int[] p = new int[branch.size()];
        int[] elts = branch.elements();
        for(int x=0; x < p.length; x++)
        {
            p[x] = elts[x];
        }
        paths.add(p);
    }

    void printPath(IntArrayList path)
    {
        StringBuffer b = new StringBuffer("> ");
        String space = " ";
        for(int x=0; x < path.size(); x++)
        {
            b.append(path.get(x));
            b.append(space);
        }

        System.out.println(b.toString());
    }


    private int[] _getReachableNeighbors(int node)
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
        if((color.length != dt.length) ||
           (color.length != pred.length) ||
           (color.length != ft.length))
        {
            System.out.println("trace: arrays of different length");
            return;
        }

        IntArrayList keys = labelMap.keys();
        for(int x=0; x < keys.size(); x++)
        {
            int node = keys.get(x);
            int l = labelMap.get(node);
            System.out.println(node + ":"
                               + " color=" + colorOf(color[l])
                               + " t=" + dt[l] + "/" + ft[l]
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


    boolean checkPred(int[][] expected)
    {
        return check(expected, pred);
    }

    boolean checkDiscovery(int[][] expected)
    {
        return check(expected, dt);
    }

    boolean checkFinish(int[][] expected)
    {
        return check(expected, ft);
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
