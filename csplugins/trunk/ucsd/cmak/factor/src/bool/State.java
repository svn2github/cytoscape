package bool;

import java.util.Set;
import java.util.HashSet;

public class State
{
    public static State FALSE = new State("false", 0);
    public static State TRUE = new State("true", 1);
    
    public static Set BOOLEAN_SET = new HashSet();

    static {
        BOOLEAN_SET.add(FALSE);
        BOOLEAN_SET.add(TRUE);
    }

    private int _val;
    private String _name;
    private State(String name, int val)
    {
        _val = val;
        _name = name;
    }

    public int toInt() 
    {
        return _val;
    }

    public String toString()
    {
        return "[" + _name + ", " + _val + "]";
    }
}
