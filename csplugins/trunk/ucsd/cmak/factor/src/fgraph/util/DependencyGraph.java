package fgraph.util;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.list.IntArrayList;

import giny.model.RootGraph;

import cytoscape.util.GinyFactory;

public class DependencyGraph
{
    // map factor graph index to dependency graph index
    protected OpenIntIntHashMap _ig2dMap;

    protected RootGraph _g;
    
    public DependencyGraph(int numNodes)
    {
        _g = GinyFactory.createRootGraph();
        _g.ensureCapacity(numNodes, 2*numNodes);
        _ig2dMap = new OpenIntIntHashMap(numNodes);
    }

    public RootGraph getRootGraph()
    {
        return _g;
    }

    /**
     *
     *
     * @return a map the associates each dependency graph index
     * with an index in the interaction graph
     */
    public OpenIntIntHashMap getDep2InteractionMap()
    {
        // reverse the interaction 2 dependency map
        // create a map that associates dependency graph indicies
        // with interaction graph indicies.
        int sz = _ig2dMap.size();
        OpenIntIntHashMap d2i = new OpenIntIntHashMap(sz);

        IntArrayList keys = new IntArrayList(sz);
        IntArrayList vals = new IntArrayList(sz);

        _ig2dMap.pairsSortedByKey(keys, vals);
        keys.trimToSize();
        vals.trimToSize();
        int[] k = keys.elements();
        int[] v = vals.elements();

        for(int x=0; x < k.length; x++)
        {
            d2i.put(v[x], k[x]);
        }
        
        return d2i;
    }
    
    public void add(int n1, int n2)
    {
        int d1 = getDepGraphIndex(n1);
        int d2 = getDepGraphIndex(n2);
        
        if(!_g.edgeExists(d1, d2))
        {

            System.out.println("creating edge: DG (" + d1 + ", " + d2 + ") from IG ("
                               + n1 + ", " + n2 + ")");
            _g.createEdge(d1, d2, true);
        }
    }

    private int getDepGraphIndex(int iIndex)
    {
        if(_ig2dMap.containsKey(iIndex))
        {
            return _ig2dMap.get(iIndex);
        }
        else
        {
            int n = _g.createNode();
            _ig2dMap.put(iIndex, n);
            return n;
        }
    }
}
