import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cern.colt.bitvector.BitVector;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

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

    final static int SOURCE = 0; // index of source in _edges[][]
    final static int TARGET = 1; // index of target in _edges[][]
    
    final static int NIL = 0;
    
    private InteractionGraph ig;
    private RootGraph g;
    private int numNodes;
    private int numEdges;
        
    private int infinity;

    private int[] nodes;

    // map graph indices to a label in the set [0,numNodes]
    private OpenIntIntHashMap labelMap;

    private OpenIntIntHashMap _edgeLabelMap;

    private int[][] _edges;
    
    // map target node to IntArrayList path
    private Target2PathMap target2pathMap;

    // map nodes to adjacent edges
    //private OpenIntObjectHashMap adjMap;
    private ObjectArrayList _adjacentEdges;
    
    // bits indicating whether a gene is affected by
    // the current koNode
    // if(_affected.get(n) == true) then the gene with label n is affected.
    private BitVector _affected;
    
    // data structure that holds results of path search
    private PathResult result;

    private int[] color; // tracks node status
    private int[] pred; // predecessor
    private int[] dt; // discovery time
    private int[] ft; // finish time
        
    private int _koNode;
    private int pathCount;

    private int notCounted;
    
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

        //adjMap = new OpenIntObjectHashMap(numNodes);
        _adjacentEdges = new ObjectArrayList(numNodes);
        _affected = new BitVector(numNodes);
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
        System.out.println("DFSPath: numNodes=" + numNodes +
                           " numEdges=" + numEdges);
        
        // initialize data structures
        result = new PathResult(numNodes, numEdges);
        labelMap.clear();
        _adjacentEdges.clear();

        for(int n = 0; n < numNodes; n++)
        {
            labelMap.put(nodes[n], n);

            color[n] = WHITE;

            int[] adj = g.getAdjacentEdgeIndicesArray(nodes[n], true, false, true);
            if(adj == null)
            {
                adj = new int[0];
            }
            _adjacentEdges.add(adj);
        }

        _initEdges();
        
        pathCount = 0;
        notCounted = 0;
        
        for(int s=0; s < sources.length; s++)
        {
            int sourceNode = sources[s];

            _initIsAffected(sourceNode);

            if(!labelMap.containsKey(sourceNode))
            {
                System.err.println(sourceNode + " is not a node in the graph");
                break;
            }

            target2pathMap = result.addKO(sourceNode);
            
            int l = labelMap.get(sourceNode);
            color[l] = GREY; // the source node is part of every path
            
            int depth = 0;
            int[] adj = _getAdjacentEdges(l);
            for(int e=0; e < adj.length; e++)
            {
                int edgeLabel = _edgeLabelMap.get(adj[e]);
                dfsVisit(adj[e], edgeLabel, sourceNode, depth, maxDepth);
            }
            color[l] = WHITE; // unmark the source.

            System.out.println("DFSPath finished: " + sourceNode + " total paths = "
                               + pathCount + ". Paths not counted = " +
                               notCounted);
        }

        result.setPathCount(pathCount);
        return result;
    }


    
    /**
     * 
     */
    protected void dfsVisit(int edge, int edgeLabel,
                            int source, int depth, int maxDepth) 
    {
        int target;
        State dir;

        // how to return the dir from _getRelativeTarget
        int s = _edges[edgeLabel][SOURCE];
        int t = _edges[edgeLabel][TARGET];

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
        if(isAffected(tLabel) && isProteinDNA(edge))
        { 
            target2pathMap.addPath(target, pathCount);
            pathCount++;
        }
        else
        {
            notCounted += 1;
        }
        
        if(depth < maxDepth)
        {
            int[] adj = _getAdjacentEdges(tLabel);

            for(int e=0; e < adj.length; e++)
            {
                int adjLabel = _edgeLabelMap.get(adj[e]);
                int neighbor = _getRelativeTarget(adjLabel, target);
                int nLabel = labelMap.get(neighbor);
                if(color[nLabel] != GREY)
                {
                    dfsVisit(adj[e], adjLabel, target, depth, maxDepth);
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

    
    protected boolean isAffected(int nodeLabel)
    {
        return _affected.get(nodeLabel);
    }


    /**
     * Initialize the affected BitVector.
     * The labelMap maps each node to an integer label, i (0<=i< numNodes)
     * The i-th bit in "affected" is set if the gene corresponding to
     * label, i, is affected by knocking out "knockoutNode".
     */
    private void _initIsAffected(int knockoutNode)
    {
        _koNode = knockoutNode;

        _affected.clear();
        
        for(int n = 0; n < nodes.length; n++)
        {
            int node = nodes[n];
            
            if(ig.expressionChanges(_koNode, node))
            {
                _affected.set(labelMap.get(node));
            }
        }
    }

    private void _initEdges()
    {
        int[] edges = g.getEdgeIndicesArray();

        _edges = new int[edges.length][2];
        _edgeLabelMap = new OpenIntIntHashMap(edges.length);
        
        for(int x=0; x < edges.length; x++)
        {
            int e= edges[x];
            _edgeLabelMap.put(e, x);
            _edges[x][SOURCE] = g.getEdgeSourceIndex(e);
            _edges[x][TARGET] = g.getEdgeTargetIndex(e);
        }
    }

    
    private int _getRelativeTarget(int edgeLabel, int source)
    {
        int s = _edges[edgeLabel][SOURCE];
        int t = _edges[edgeLabel][TARGET];
        
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
                               + " is not an endpoint of edge labeled:" + edgeLabel);
            return t;
        }
    }

    private int[] _getAdjacentEdges(int nodeLabel)
    {
        return (int[]) _adjacentEdges.get(nodeLabel);
    }

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
