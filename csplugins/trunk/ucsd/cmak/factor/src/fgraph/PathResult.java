package fgraph;

import fgraph.util.IntListMap;
import fgraph.util.Target2PathMap;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

public class PathResult
{
    // Map indicies of edges to a List of Intervals (paths).
    // The edges indicies refer to the InteractionGraph RootGraph.
    private IntListMap _pathIntervalMap;
    
    /**
     * Maps knock-outs (referenced by its index in the interaction RootGraph)
     * to a Target2PathMap.
     * Each Target2PathMap maps the targets (referenced by indicies in the 
     * interaction RootGraph) of the knockout to an
     * IntArrayList of paths that connect the knockout to the target
     */
    private OpenIntObjectHashMap _ko2targetMap;

    /** The total number of paths for all knockouts
     */
    private int _pathCount;

    private int _numNodes;
    
    private int _maxPathLength;
    
    /**
     *  Create a new path results
     *
     * @param numNodes number of nodes in the interaction graph
     * @param numEdges number of edges in the interaction graph
     */
    PathResult(int maxPathLength, int numNodes, int numEdges)
    {
        _maxPathLength = maxPathLength;
        _numNodes = numNodes;
        _pathIntervalMap = new IntListMap(numEdges);

        _ko2targetMap = new OpenIntObjectHashMap();
    }

    void setPathCount(int c)
    {
        _pathCount = c;
    }

    int getPathCount()
    {
        return _pathCount;
    }

    int getMaxPathLength()
    {
        return _maxPathLength;
    }

    int getNumKOPairs()
    {
        int cnt = 0;
        
        IntArrayList kos = getKOs();
        for(int x=0, N = kos.size(); x < N; x++)
        {
            int knockedOut =  kos.get(x);
            Target2PathMap target2path = getTarget2PathMap(knockedOut);

            cnt += target2path.size();
        }

        return cnt;
    }
    
    int countKOWithTargets()
    {
        int cnt = 0;
        
        IntArrayList kos = getKOs();
        for(int x=0, N = kos.size(); x < N; x++)
        {
            int knockedOut =  kos.get(x);
            Target2PathMap target2path = getTarget2PathMap(knockedOut);

            if(target2path.size() > 0)
            {
                cnt++;
            }
        }

        return cnt;
    }
    

    
    /**
     * @return a map of edge indices to DFSPath.Interval objects.
     */
    public IntListMap getEdge2PathMap()
    {
        return _pathIntervalMap;
    }


    /**
     * Check if an edge is on one path
     * @see #isEdgeOnPath(int, int[])
     */
    public boolean isEdgeOnPath(int edge, int path)
    {
        return isEdgeOnPath(edge, new int[] {path});
    }


    /**
     * Is there a more efficient way to do this?
     *
     * @param edge the edge
     * @param path an array of paths to test
     * @return true if edge is on at least one of the paths in "path", false otherwise
     */
    public boolean isEdgeOnPath(int edge, int[] path)
    {
        if(_pathIntervalMap.containsKey(edge))
        {
            List intervals = _pathIntervalMap.get(edge);

            for(int x=0, N=intervals.size(); x < N; x++)
            {
                for(int p=0; p < path.length; p++)
                {
                    if( ((Interval) intervals.get(x)).contains(path[p]))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    /**
     * Is there a more efficient way to do this?
     *
     * @param edge the edge
     * @param path an array of paths to test
     * @return The paths that pass through the edge
     */
    public int[] pathsThroughEdge(int edge, int[] path)
    {
        IntArrayList ok = new IntArrayList();

        if(_pathIntervalMap.containsKey(edge))
        {
            List intervals = _pathIntervalMap.get(edge);

            for(int x=0, N=intervals.size(); x < N; x++)
            {
                for(int p=0; p < path.length; p++)
                {
                    if( ((Interval) intervals.get(x)).contains(path[p]))
                    {
                        ok.add(path[p]);
                    }
                }
            }
        }
        ok.trimToSize();
        
        return ok.elements();
    }

    
    
    public IntArrayList getKOs()
    {
        return _ko2targetMap.keys();
    }

    /**
     * @return a map of target node indicies affected by the KO 
     * to an IntArrayList of paths that connect the KO'd gene to the target.
     */
    public Target2PathMap getTarget2PathMap(int ko)
    {
        return (Target2PathMap) _ko2targetMap.get(ko);
    }


    Target2PathMap addKO(int sourceNode)
    {
        if(_ko2targetMap.containsKey(sourceNode))
        {
            return (Target2PathMap) _ko2targetMap.get(sourceNode);
        }
        else
        {
            Target2PathMap target2path = new Target2PathMap(_numNodes);
            _ko2targetMap.put(sourceNode, target2path);
            return target2path;
        }
    }


    protected Interval addInterval(int edge)
    {
        Interval i = new Interval();
        _pathIntervalMap.add(edge, i);

        return i;
    }

    class Interval
    {
        private int start = -1;
        private int end = -1;
        private State dir;
        
        private static final String s1 = "[";
        private static final String s2 = ", ";
        private static final String s3 = "]";
        Interval() {}
        void setStart(int s) { start = s; }
        void setEnd(int e) { end = e; }

        /*
         * State.PLUS == path traverses edge from RootGraph source to target.
         * State.MINUS == path traverses edge from RootGraph target to source.
         */
        void setDir(State s) { dir = s; }

        int getStart() { return start; }
        int getEnd() { return end; }
        State getDir() { return dir; }

        public String toString()
        {
            StringBuffer b = new StringBuffer(s1);
            b.append(start);
            b.append(s2);
            b.append(end);
            b.append(s2);
            b.append(dir);
            b.append(s3);
            
            return b.toString();
        }

        boolean contains(int path)
        {
            return (path >= start) && (path < end);
        }
    }

    public String toString(InteractionGraph ig)
    {
        StringBuffer b = new StringBuffer();
        
        b.append("pathCount: ");
        b.append(_pathCount);
        b.append("\n");
        
        IntArrayList edges = _pathIntervalMap.keys();
        for(int x=0, N=edges.size(); x < N; x++)
        {
            int edge = edges.get(x);

            List l = _pathIntervalMap.get(edge);

            if(ig != null)
            {
                b.append("edge ");
                b.append(ig.edgeLabel(edge));
                b.append(": paths-> ");
            }
            else
            {
                b.append("edge ");
                b.append(edge);
                b.append(": paths-> ");
            }

            b.append(l);
            b.append("\n");
        }

        IntArrayList kos = _ko2targetMap.keys();
        for(int x=0, Nk=kos.size(); x < Nk; x++)
        {
            int ko = kos.get(x);
            Target2PathMap target2pathMap = (Target2PathMap) _ko2targetMap.get(ko);
            IntArrayList targets = target2pathMap.keys();

            for(int y=0, Nt=targets.size(); y < Nt; y++)
            {
                int t = targets.get(y);
                IntArrayList l = (IntArrayList) target2pathMap.get(t);
                
                b.append(ig.node2Name(ko));
                b.append(": target node ");
                b.append(ig.node2Name(t));
                b.append(": paths-> ");
                b.append(l.toString());
                b.append("\n");
            }
        }
        b.append("pathCount: ");
        b.append(_pathCount);
        b.append("\n");

        return b.toString();
    }
    
}
