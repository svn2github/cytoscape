package fgraph;

import fgraph.util.Target2PathMap;

import java.io.*;

import java.text.DecimalFormat;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import cern.colt.bitvector.BitVector;
import cern.colt.bitvector.BitMatrix;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import giny.model.RootGraph;

/**
 * A class that uses Depth First Search to efficiently
 * enumerate paths from knockout nodes to affected nodes
 * in an InteractionGraph.
 * <p>
 * Adapted from Depth first search described in
 * Cormen,Rivest,Leiseson. Intro to Algorithms.
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

    // the InteractionGraph's underlying RootGraph
    private RootGraph g;
    private int numNodes;
    private int numEdges;

    // root graph node indices
    private int[] nodes;

    // map root graph node indices to an integer label in the set [0,numNodes]
    private OpenIntIntHashMap _nodeLabelMap;
    private int[] _label2node;
    
    // map root graph edge indices to an integer label in the set [0, numEdges]
    private OpenIntIntHashMap _edgeLabelMap;

    // array of edge source and target nodes, indexed by edge label.
    private int[][] _edges;
    
    // map target node to IntArrayList path
    private Target2PathMap target2pathMap;

    // map nodes to adjacent edges
    //private OpenIntObjectHashMap adjMap;
    private ObjectArrayList _adjacentEdges;
    
    // bits indicating genes that are affected by a knockout.
    // 
    // if(_affected.get(k, n) == true) then the gene with label n
    // is affected by the knockout node with label k.
    private BitMatrix _affected;
        
    // bits indicating whether a gene is a knockout node
    // if(_isKO.get(n) == true) then the gene with label n is a KO.
    private BitVector _isKO;
    
    // data structure that holds results of path search
    private PathResult result;

    private int[] color; // tracks node status

    //private int infinity;
    //private int[] pred; // predecessor
    //private int[] dt; // discovery time
    //private int[] ft; // finish time

    // number of paths that satisfy criteria
    private int pathCount;

    // number of paths not counted
    private int notCounted;

    // Stream where paths are printed as they are discovered.
    // For debugging purposes
    private PrintStream _out;
    
    /**
     * Create a new search object that will find paths on "graph"
     *
     * @param graph an InteractionGraph that contains expression data.
     */
    public DFSPath(InteractionGraph graph)
    {
        ig = graph;
        g = ig.getRootGraph();
        numNodes = g.getNodeCount();
        numEdges = g.getEdgeCount();

        nodes = g.getNodeIndicesArray();

        // map graph indices to a label in the set [0,numNodes]
        _nodeLabelMap = new OpenIntIntHashMap(numNodes);
        _label2node = new int[numNodes];
        
        color = new int[numNodes];

        //infinity = numEdges + 1;
        //pred = new int[numNodes]; // predecessor
        //dt = new int[numNodes]; // discovery time
        //ft = new int[numNodes]; // finish time

        _adjacentEdges = new ObjectArrayList(numNodes);
        _affected = new BitMatrix(0,0);
        _isKO = new BitVector(numNodes);

        // stuff for printing paths
        try{
            _out = new PrintStream(new FileOutputStream("dfspath.out"));
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
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
     * @param sources an array of node root graph indicies that should
     * be used as sources for the search.  These should be the knockouts.
     * The InteractionGraph should contain expression data for each of the
     * source nodes.
     * 
     * @param maxDepth the maxDepth to stop the search.
     *
     * @return a PathResult object
     */
    public PathResult findPaths(int[] sources, int maxDepth)
    {
        System.out.println("DFSPath: numNodes=" + numNodes +
                           " numEdges=" + numEdges);
        
        // initialize data structures
        result = new PathResult(maxDepth, numNodes, numEdges);

        _nodeLabelMap.clear();
        _adjacentEdges.clear();
        _isKO.clear();

        // 1. color each node WHITE
        // 2. Store the edge adjacency array for each node.
        //    This improves performance.
        // 3. Build the isKO matrix for fast lookup of whether a
        //    node is one of the sources.
        for(int n = 0; n < numNodes; n++)
        {
            int node = nodes[n];
            _nodeLabelMap.put(node, n);
            _label2node[n] = node;
            
            color[n] = WHITE;

            int[] adj = g.getAdjacentEdgeIndicesArray(node, true, false, true);
            if(adj == null)
            {
                adj = new int[0];
            }
            _adjacentEdges.add(adj);

            if(Arrays.binarySearch(sources, node) >= 0)
            {
                _isKO.set(n);
            }
        }

        _initEdges();

        _initIsAffected(sources);
        
        pathCount = 0;
        notCounted = 0;

        // array that will record the labels of all nodes along the current
        // path.  This is used to check that intermediate genes that are
        // deleted along the path also affect the last node.
        int[] curPath = new int[maxDepth + 1];

        // Iterateively do a DFS starting at each source node.
        // The paths that start at each source are stored in a
        // Target2PathMap object in the PathResults.
        for(int s=0; s < sources.length; s++)
        {
            int sourceNode = sources[s];

            if(!_nodeLabelMap.containsKey(sourceNode))
            {
                System.err.println(sourceNode + " is not a node in the graph");
                break;
            }

            // this map will store all of the paths from the sourceNode
            // to all each target node.
            target2pathMap = result.addKO(sourceNode);
            
            int sourceLabel = _nodeLabelMap.get(sourceNode);
            color[sourceLabel] = GREY; // the source node is part of every path
            
            int depth = 0;
            int[] adj = _getAdjacentEdges(sourceLabel);
            curPath[0] = sourceLabel;
            
            for(int e=0; e < adj.length; e++)
            {
                int edgeLabel = _edgeLabelMap.get(adj[e]);
                dfsVisit(adj[e], edgeLabel, sourceNode, depth, maxDepth, curPath);
            }
            color[sourceLabel] = WHITE; // unmark the source.

            System.out.println("DFSPath finished: " + sourceNode
                               + " total paths = " + pathCount
                               + ". Paths not counted = " + notCounted);
        }

        result.setPathCount(pathCount);
        return result;
    }


    
    /**
     * Do a recursive depth first visit along "edge"
     *
     * @param edge the edge to start the visit (a root graph index)
     * @param edgeLabel the integer label for edge
     * @param source the source node of the edge
     * @param depth the current depth of the search
     * @param maxDepth the depth at which to stop the search.
     *  (e.g. if maxDepth = 5, paths with 5 or fewer edges will be found.
     *        
     * @param curPath an array of node labels that are on the current path.
     * Invariant: the nodes in curPath should always be GREY.
     * Invariant: curPath[0] is the knockout node for the path
     */
    protected void dfsVisit(int edge, int edgeLabel, int source,
                            int depth, int maxDepth,
                            int[] curPath) 
    {

        // duplicate code from _getRelativeTarget
        // can this be fixed?
        // how to cleanly return direction and target from _getRelativeTarget?
        int s = _edges[edgeLabel][SOURCE];
        int t = _edges[edgeLabel][TARGET];

        int target;
        State dir;
        
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

        depth++;
        
        int tLabel = _nodeLabelMap.get(target);

        color[tLabel] = GREY; // GREY == node is in the current path

        curPath[depth] = tLabel;

        int startCount = pathCount;

        /* isAffected: enforce constraint that the expression of the target
         *             changes
         *
         * isProteinDNA: enforce constraint that the last edge in a path
         *               is a protein-DNA edge
         *
         * checkIntermediateKOs: enforce constraint: if intermediate nodes
         *                       on the path are knocked out, they must
         *                       affected the last node in the path.
         *               
         * If all constraints satisfied, then "pathCount" identifies a
         * path from the knockout to "target".
         */
        if(isAffected(curPath[0], tLabel) &&
           isProteinDNA(edge) &&
           checkIntermediateKOs(curPath, depth))
        { 
            target2pathMap.addPath(target, pathCount);

            printPath(pathCount, curPath, depth);
            pathCount++;
        }
        else
        {
            notCounted++;
        }

        // recursively visit each adjacent edge
        if(depth < maxDepth)
        {
            int[] adj = _getAdjacentEdges(tLabel);

            for(int e=0; e < adj.length; e++)
            {
                int adjLabel = _edgeLabelMap.get(adj[e]);
                int neighbor = _getRelativeTarget(adjLabel, target);
                int nLabel = _nodeLabelMap.get(neighbor);
                // GREY nodes are already on this path
                if(color[nLabel] != GREY)
                {
                    dfsVisit(adj[e], adjLabel, target, depth, maxDepth, curPath);
                }
            }
        }

        // label this node WHITE so that this node can be an intermediate
        // vertex in other paths.
        color[tLabel] = WHITE;

        if(startCount != pathCount)
        {
            // paths # startCount through pathCount pass through, or
            // terminate at, this node.  
            PathResult.Interval i = result.addInterval(edge);
            i.setStart(startCount);
            i.setEnd(pathCount);
            i.setDir(dir);
        }
    }

    private void printPath(int pathCount, int[] curPath, int depth)
    {
        StringBuffer b = new StringBuffer();
        //b.append(pathCount);
        //b.append(": ");
        
        for(int x=0; x <= depth; x++)
        {
            b.append(ig.node2Name(_label2node[curPath[x]]));
            if(x < depth)
            {
                b.append(" ");
            }
        }

        _out.println(b.toString());
    }
    
    /**
     *
     *
     * @param curPath nodes along the current path
     *        curPath[0] is the starting knockout node
     *        curPath[depth] is the target node
     *        curPath[1 through (depth-1)] are intermediate nodes.
     * @param depth the current depth
     * 
     * @return For each of the intermediate nodes in curPath
     * that are knockouts, return true iff the knockout affects
     * the target node.
     */
    protected boolean checkIntermediateKOs(int[] curPath, int depth)
    {
        for(int x=1; x < depth; x++)
        {
            if(isKO(curPath[x]))
            {
                if(!isAffected(curPath[x], curPath[depth]))
                {
                    return false;
                }
            }
        }
        
        return true;
    }

    boolean isKO(int nodeLabel)
    {
        return _isKO.getQuick(nodeLabel);
    }

    
    
    /**
     * @param edge the root graph index of an edge
     * @return true if edge is a protein-DNA edge.
     */
    protected boolean isProteinDNA(int edge)
    {
        return ig.isProteinDNA(edge);
    }


    /**
     * @param nodeLabel the node
     * @return true if the node label nodeLabel is affected by
     * the current knockout gene.
     */
    protected boolean isAffected(int koLabel, int nodeLabel)
    {
        return _affected.getQuick(koLabel, nodeLabel);
    }


    /**
     * Initialize the "_affected" BitVector.
     * <p>
     * The _nodeLabelMap maps each node to an integer label, i (0<=i< numNodes)
     * The i-th bit in "affected" is set if the gene corresponding to
     * label, i, is affected by knocking out "knockoutNode".
     */
    private void _initIsAffected(int[] kos)
    {
        // ~640kb used for 6400 yeast proteins.
        // potentially inefficient if numNodes is larger.
        _affected = new BitMatrix(numNodes, numNodes);
        _affected.clear();

        int[] koLabel = new int[kos.length];
        for(int x=0; x < kos.length; x++)
        {
            koLabel[x] = _nodeLabelMap.get(kos[x]);
        }

        DecimalFormat format = new DecimalFormat("0.000000");
        
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream("dfs.kos"));
        
            for(int n = 0; n < numNodes; n++)
            {
                // not really needed since nodes are mapped to their
                // index in nodes[], but better to be safe.
                // e.g. int node = n;
                int node = nodes[n];
                int nodeLabel = _nodeLabelMap.get(node);
                
                for(int x=0; x < kos.length; x++)
                {
                    if(ig.expressionChanges(kos[x], node))
                    {
                        StringBuffer b = new StringBuffer();
                        b.append(ig.node2Name(kos[x]));
                        b.append(" ");
                        b.append(ig.node2Name(node));
                        b.append(" ");
                        b.append(format.format(ig.getExprPval(kos[x], node)));
                        
                        out.println(b.toString());
                        
                        _affected.put(koLabel[x], nodeLabel, true);
                    }
                }
            }
            
            out.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Initialize the _edgeLabelMap and the _edges array.
     * _edgeLabelMap maps each root graph edge index to an
     * edgeLabel (an integer on [0, numEdges))
     *
     * _edges is an array indexed by edgeLabel that provides
     * fast access to the source and target nodes of each edge.
     * 
     */
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


    /**
     * Efficiently calculate the other endpoint of the edge that is not
     * equal to "source"
     * 
     * @param edgeLabel the edge
     * @param source the source node
     * @return the other endpoint of the edge that is not "source"
     */
    private int _getRelativeTarget(int edgeLabel, int source)
    {

        /* safer, but worse performance method
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
        */

        /* better performance, Nerius suggestion
         * (Note: ^ == bitwise XOR)
         *
         * Claim: Given a set of integers S = { a, b } and an integer
         *        c which is an element of S, then S \ { c } == (c ^ a) ^ b.
         *
         * Proof:
         * 
         * Suppose c == a, then (a ^ a) = 0 and (0 ^ b) = b
         *     and we return b. OK.
         *     
         * Suppose c == b, consider the four cases that can occur
         * when comparing bits in a and b.
         *  case 1 (a=0, b=0): b^a = 0, 0 ^ b = 0 = a.  OK
         *  case 2 (a=1, b=1): b^a = 0, 0 ^ b = 1 = a.  OK
         *  case 3 (a=1, b=0): b^a = 1, 1 ^ b = 1 = a.  OK
         *  case 4 (a=0, b=1): b^a = 1, 1 ^ b = 0 = a.  OK
         *     
         */
        return ((source ^ _edges[edgeLabel][SOURCE]) ^ _edges[edgeLabel][TARGET]);
    }

    /**
     * Get the outgoing and undirected edges that are adjacent
     * to a node.
     *
     * @param nodeLabel the integer label of a node
     * @return an array of root graph edge indicies that are
     *         adjacent to the node.
     */
    private int[] _getAdjacentEdges(int nodeLabel)
    {
        return (int[]) _adjacentEdges.get(nodeLabel);
    }


    // methods for testing
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
            int label = _nodeLabelMap.get(expected[i][0]);
            if(data[label] != expected[i][1])
            {
                return false;
            }
        }

        return true;
    }

    void trace()
    {

    }

}
