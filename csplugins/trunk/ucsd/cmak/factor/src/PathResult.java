import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;


public class PathResult
{
    // map edges to List of Intervals (paths)
    OpenIntObjectHashMap pathIntervalMap;

    
    /** maps knock-outs (referenced by its index in the interaction RootGraph)
     * to a "target2path" OpenIntObjectHashMap
     * each "target2path" maps the targets (referenced by indicies in the 
     * interaction RootGraph) of the knockout to an
     * IntArrayList of paths that connect the knockout to the target
     */
    OpenIntObjectHashMap ko2targetMap;

    private int _pathCount;

    private int _numNodes;
        
    PathResult(int numNodes, int numEdges)
    {
        _numNodes = numNodes;
        pathIntervalMap = new OpenIntObjectHashMap(numEdges);

        ko2targetMap = new OpenIntObjectHashMap();
    }

    void setPathCount(int c)
    {
        _pathCount = c;
    }

    int getPathCount()
    {
        return _pathCount;
    }

    /**
     * @return a map of edge indices to DFSPath.Interval objects.
     */
    public OpenIntObjectHashMap getEdge2PathMap()
    {
        return pathIntervalMap;
    }


    public IntArrayList getKOs()
    {
        return ko2targetMap.keys();
    }

    /**
     * @return a map of target node indicies affected by the KO 
     * to an IntArrayList of paths that connect the KO'd gene to the target.
     */
    public Target2PathMap getTarget2PathMap(int ko)
    {
        return (Target2PathMap) ko2targetMap.get(ko);
    }


    Target2PathMap addKO(int sourceNode)
    {
        if(ko2targetMap.containsKey(sourceNode))
        {
            return (Target2PathMap) ko2targetMap.get(sourceNode);
        }
        else
        {
            Target2PathMap target2path = new Target2PathMap(_numNodes);
            ko2targetMap.put(sourceNode, target2path);
            return target2path;
        }
    }


    protected Interval addInterval(int edge)
    {
        List l;

        if(!pathIntervalMap.containsKey(edge))
        {
            l = new ArrayList();
            pathIntervalMap.put(edge, l);
        }
        else
        {
            l = (List) pathIntervalMap.get(edge);
        }

        Interval i = new Interval();
        l.add(i);

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
    }

    public void print(InteractionGraph ig)
    {
        System.out.println("pathCount: " + _pathCount);

        IntArrayList edges = pathIntervalMap.keys();
        for(int x=0, N=edges.size(); x < N; x++)
        {
            int edge = edges.get(x);

            List l = (List) pathIntervalMap.get(edge);

            if(ig != null)
            {
                System.out.print("edge " + ig.edgeLabel(edge) + ": paths-> ");
            }
            else
            {
                System.out.print("edge " + edge + ": paths-> ");
            }

            System.out.println(l);
        }

        IntArrayList kos = ko2targetMap.keys();
        for(int x=0, Nk=kos.size(); x < Nk; x++)
        {
            int ko = kos.get(x);
            Target2PathMap target2pathMap = (Target2PathMap) ko2targetMap.get(ko);
            IntArrayList targets = target2pathMap.keys();

            for(int y=0, Nt=targets.size(); y < Nt; y++)
            {
                int t = targets.get(y);
                IntArrayList l = (IntArrayList) target2pathMap.get(t);
                
                System.out.print(ko + ": target node " + t + ": paths-> ");
                System.out.println(l.toString());
            }
        }
        System.out.println("pathCount: " + _pathCount);
    }
}
