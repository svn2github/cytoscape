import java.util.*;

/**
 * A utility class for mapping Object -> int
 */
public class ObjectIntMap
{
    Map _m = new HashMap();

    public ObjectIntMap()
    {
        _m = new HashMap();
    }

    public ObjectIntMap(int initialCapacity)
    {
        _m = new HashMap(initialCapacity);
    }

    public void put(Object key, int value)
    {
        _m.put(key, new Integer(value));
    }

    public int get(Object key) throws NoSuchElementException
    {
        if(_m.containsKey(key))
        {
            return ((Integer) _m.get(key)).intValue();
        }
        else
        {
            throw new NoSuchElementException(key.toString());
        }
    }
        
    public int size()
    {
        return _m.size();
    }

    public Set keySet()
    {
        return _m.keySet();
    }

    public boolean containsKey(Object key)
    {
        return _m.containsKey(key);
    }

    public boolean containsValue(int i)
    {
        return _m.containsValue(new Integer(i));
    }
}
