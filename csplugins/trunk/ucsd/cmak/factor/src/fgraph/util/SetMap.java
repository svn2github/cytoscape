package fgraph.util;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * A SetMap is a map that maps object keys to a Set of values.
 *
 */
public class SetMap 
{
    private Map _m;
    
    public SetMap()
    {
        _m = new HashMap();
    }

    public SetMap(int initialCapacity)
    {
        _m = new HashMap(initialCapacity);
    }

    public SetMap(int initialCapacity, float loadFactor)
    {
        _m = new HashMap(initialCapacity, loadFactor);
    }

    
    public void put(Object key, Object val)
    {
        if(_m.containsKey(key))
        {
            ((Set) _m.get(key)).add(val);
        }
        else
        {
            Set s = new HashSet();
            s.add(val);

            _m.put(key, s);
        }
    }

    public Set get(Object key)
    {
        if(_m.containsKey(key))
        {
            return (Set) _m.get(key);
        }
        else
        {
            return null;
        }
    }

    /**
     * @return true if val is in the Set associated with key, false otherwise.
     */
    public boolean containsMapping(Object key, Object val)
    {
        if(_m.containsKey(key))
        {
            return ((Set) _m.get(key)).contains(val);
        }

        return false;
    }
    
    public int size()
    {
        return _m.size();
    }
}
