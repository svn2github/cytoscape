package fgraph.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import cern.colt.list.IntArrayList;

/**
 * A forrest of depth-first trees.
 */
public class GraphForrest
{
    public List forrest;
    
    public GraphForrest()
    {
        forrest = new ArrayList();
    }

    public int size()
    {
        return forrest.size();
    }
    
    public IntArrayList newTree()
    {
        IntArrayList l = new IntArrayList();
        forrest.add(l);
        return l;
    }

    public Iterator iterator()
    {
        return forrest.iterator();
    }


}
