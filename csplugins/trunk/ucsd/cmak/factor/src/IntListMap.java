import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

/**
 * A utility class for mapping int -> List
 */
public class IntListMap
{
    private OpenIntObjectHashMap _m;

    public IntListMap()
    {
        _m = new OpenIntObjectHashMap();
    }

    public IntListMap(int initialCapacity)
    {
        _m = new OpenIntObjectHashMap(initialCapacity);
    }

    public void put(int key, List value)
    {
        _m.put(key, value);
    }

    public void add(int key, Object o)
    {
        List l;
        if(_m.containsKey(key))
        {
            l = (List) _m.get(key);
        }
        else
        {
            l = new ArrayList();
            _m.put(key, l);
        }

        l.add(o);
    }
    
    public List get(int key) throws NoSuchElementException
    {
        if(_m.containsKey(key))
        {
            return (List) _m.get(key);
        }
        else
        {
            throw new NoSuchElementException(String.valueOf(key));
        }
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
