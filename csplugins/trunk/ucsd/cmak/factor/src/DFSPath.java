import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

import giny.model.RootGraph;

/**
 * A class that uses Depth First Search to efficiently
 * enumerate paths in an InteractionGraph
 * 
 */ 
public class DFSPath
{
    final static int WHITE = 0; // undiscovered
    final static int GREY = 1; // discovered
    final static int BLACK = 2; // finished
    
    final static int NIL = 0;
    
    private InteractionGraph ig;
    private RootGraph g;
    private int numNodes;
    private int numEdges;
        
    private int infinity;

    private int[] nodes;

    // map graph indices to a label in the set [0,numNodes]
    private OpenIntIntHashMap labelMap;

    // map target node to IntArrayList path
    private Target2PathMap target2pathMap;

    // map nodes to adjacent edges
    private OpenIntObjectHashMap adjMap;

    // data structure that holds results of path search
    private PathResult result;

    private int[] color; // tracks node status
    private int[] pred; // predecessor
    private int[] dt; // discovery time
    private int[] ft; // finish time
        
    private int koNode;
    private int pathCount;

    public DFSPath(InteractionGraph graph)
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
        //pred = new int[numNodes]; // predecessor
        //dt = new int[numNodes]; // discovery time
        //ft = new int[numNodes]; // finish time

        adjMap = new OpenIntObjectHashMap(numNodes);
    }

    /**
     * Enumerate all paths from the sourceNode to its targets.
     * All paths will consist of less that or equal to "maxDepth" edges.
     * 
     * @return a PathResult object
     */
    public PathResult findPaths(int sourceNode, int maxDepth)
    {
        return findPaths(new int[] {sourceNode}, maxDepth);
    }

    /**
     * Find paths from multiple sources to all of their targets.
     * All paths will consist of less that or equal to "maxDepth" edges.
     * 
     * @return a PathResult object
     */
    public PathResult findPaths(int[] sources, int maxDepth)
    {
        // initialize data structures
        result = new PathResult(numNodes, numEdges);
        labelMap.clear();
        adjMap.clear();
        for(int n = 0; n < numNodes; n++)
        {
            color[n] = WHITE;
            labelMap.put(nodes[n], n);
        }

        pathCount = 0;

        for(int s=0; s < sources.length; s++)
        {
            int sourceNode = sources[s];
            koNode = sourceNode;

            if(!labelMap.containsKey(sourceNode))
            {
                System.err.println(sourceNode + " is not a node in the graph");
                break;
            }

            target2pathMap = result.addKO(sourceNode);
            
            int l = labelMap.get(sourceNode);
            color[l] = GREY; // the source node is part of every path
            
            int depth = 0;
            int[] adj = _getAdjacentEdges(sourceNode);
            for(int e=0; e < adj.length; e++)
            {
                dfsVisit(adj[e], sourceNode, depth, maxDepth);
            }
            color[l] = WHITE; // unmark the source.
        }

        result.setPathCount(pathCount);
        return result;
    }


    
    /**
     * 
     */
    protected void dfsVisit(int edge, int source, int depth, int maxDepth) 
    {
        int target;
        State dir;

        // how to return the dir from _getRelativeTarget
        int s = g.getEdgeSourceIndex(edge);
        int t = g.getEdgeTargetIndex(edge);

        if(source == s)
        {
            dir = State.PLUS;
            target = t;
        }
        else if (source == t)
        {
            dir = State.MINUS;
            target =  s;
        }
        else
        {
            System.err.println("dfsVisit ERROR edge=" + edge + " src=" + source);
            return;
        }
        
        int tLabel = labelMap.get(target);

        color[tLabel] = GREY; // GREY == node is in the current path
        depth++;

        int ct1 = pathCount;

        /* isAffected: enforce constraint that the expression of the target
         *             changes
         *
         * isProteinDNA: enforce constraint that the last edge in a path
         *               is a protein-DNA edge
         *
         * If constraints satisfied, then "pathCount" identifies a path from
         * the knockout to "target".
         */
        if(isAffected(target) && isProteinDNA(edge))
        { 
            target2pathMap.addPath(target, pathCount);
            pathCount++;
        }
        
        if(depth < maxDepth)
        {
            int[] adj = _getAdjacentEdges(target);

            for(int e=0; e < adj.length; e++)
            {
                int neighbor = _getRelativeTarget(adj[e], target);
                int l = labelMap.get(neighbor);
                if(color[l] != GREY)
                {
                    dfsVisit(adj[e], target, depth, maxDepth);
                }
            }
        }

        // label this node WHITE so that this node can be an intermediate
        // vertex in other paths.
        color[tLabel] = WHITE;

        if(ct1 != pathCount)
        {
            // paths # ct through pathCount pass through or terminate at
            // this node.  
            PathResult.Interval i = result.addInterval(edge);
            i.setStart(ct1);
            i.setEnd(pathCount);
            i.setDir(dir);
        }
    }


    protected boolean isProteinDNA(int edge)
    {
        return ig.isProteinDNA(edge);
    }

    
    protected boolean isAffected(int node)
    {
        return ig.expressionChanges(koNode, node);
    }


    private int _getRelativeTarget(int edge, int source)
    {
        int s = g.getEdgeSourceIndex(edge);
        int t = g.getEdgeTargetIndex(edge);

        if(source == s)
        {
            return t;
        }
        else if (source == t)
        {
            return s;
        }
        else
        {
            // should not get here
            System.err.println("ERROR: _getRelativeTarget: " + source 
                               + " is not an endpoint of " + edge);
            return t;
        }
    }

    private int[] _getAdjacentEdges(int node)
    {

        // cache array of adjacent edges because getAdjacenctEdgeIndicesArray()
        // is an expensive GINY operation.
        if(adjMap.containsKey(node))
        {
            return (int[]) adjMap.get(node);
        }
        else
        {
            /* include undirected edges = true
             * include incoming edges = false
             * include outgoing edges = true
             */
            int[] adj = g.getAdjacentEdgeIndicesArray(node, true, false, true);
            
            if(adj == null)
            {
                adj = new int[0];
            }
            
            adjMap.put(node, adj);
            
            return adj;
        }
    }

    /*
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
    */
    void trace()
    {

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

    /*
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
    */

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
