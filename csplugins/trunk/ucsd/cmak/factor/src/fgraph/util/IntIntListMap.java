package fgraph.util;

import java.util.NoSuchElementException;

import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

/**
 * A utility class for mapping int -> IntArrayList
 */
public class IntIntListMap
{
    private OpenIntObjectHashMap _m;

    public IntIntListMap()
    {
        _m = new OpenIntObjectHashMap();
    }

    public IntIntListMap(int initialCapacity)
    {
        _m = new OpenIntObjectHashMap(initialCapacity);
    }

    public void ensureCapacity(int c)
    {
        _m.ensureCapacity(c);
    }
    
    public void put(int key, IntArrayList value)
    {
        _m.put(key, value);
    }

    public void add(int key, int value)
    {
        IntArrayList l;
        if(_m.containsKey(key))
        {
            l = (IntArrayList) _m.get(key);
        }
        else
        {
            l = new IntArrayList();
            _m.put(key, l);
        }

        l.add(value);
    }
    
    public IntArrayList get(int key) throws NoSuchElementException
    {
        if(_m.containsKey(key))
        {
            return (IntArrayList) _m.get(key);
        }
        else
        {
            throw new NoSuchElementException(String.valueOf(key));
        }
    }

    public void clear()
    {
        _m.clear();
    }
    
    public int size()
    {
        return _m.size();
    }

    public IntArrayList keys()
    {
        return _m.keys();
    }

    public boolean containsKey(int key)
    {
        return _m.containsKey(key);
    }
}
