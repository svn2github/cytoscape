import java.util.Iterator;
import java.util.Arrays;

/**
 * A StateSet is a collection of states.  The set maps each state to
 * a distinct number in the interval [1,N] where N is the size of the
 * StateSet.
 */
public class StateSet
{
    public static final StateSet PATH_ACTIVE = new StateSet("pathActive_ss",
                                                            State.ZERO, State.ONE, State.ONE);
    
    public static final StateSet EDGE = new StateSet("edge_ss",
                                                     State.ZERO, State.ONE, State.ONE);

    public static final StateSet SIGN = new StateSet("sign_ss",
                                                     State.MINUS, State.PLUS, State.MINUS);
    
    public static final StateSet DIR = new StateSet("dir_ss",
                                                    State.MINUS, State.PLUS, State.MINUS);
    
    public static final StateSet KO = new StateSet("ko_ss",
                                                   State.ZERO, State.PLUS, State.MINUS, State.ZERO);
    
    private State[] _s;
    private int _sz;
    private String _name;
    private State _default;
    
    private static final double[] _default2 = {1, 1};
    private static final double[] _default3 = {1, 1, 1};
    
    private StateSet(String name, State s1, State s2, State def)
    {
        _name = name;
        _sz = 2;
        _s = new State[_sz];
        _s[0] = s1;
        _s[1] = s2;
        _default = def;
    }

    private StateSet(String name, State s1, State s2, State s3, State def)
    {
        _name = name;
        _sz = 3;
        _s = new State[_sz];
        _s[0] = s1;
        _s[1] = s2;
        _s[2] = s3;
        _default = def;
    }
    
    public State defaultState()
    {
        return _default;
    }
    
    public double[] defaultProbs()
    {
        if(_sz == 2)
        {
            return _default2;
        }
        else if (_sz == 3)
        {
            return _default3;
        }
        else
        {
            double[] p = new double[_sz];
            Arrays.fill(p, 1);
            return p;
        }
    }
    
    /**
     * @return the number of states in this set
     */
    public int size()
    {
        return _sz;
    }

    /**
     * @param st a state in this set.
     * @return the index of the state or -1 if the state is not in this set.
     */
    public int getIndex(State st)
    {
        for(int x=0, n=_s.length; x < n; x++)
        {
            if(st == _s[x])
            {
                return x;
            }
        }

        return -1;
    }


    /**
     * @return The State corresponding to the index or null if the
     * index is out of bounds.
     */
    public State getState(int index)
    {
        if(index >= 0 && index < _s.length)
        {
            return _s[index];
        }
        else
        {
            return null;
        }
    }
    
    /**
     * @return an iterator over the states in this set
     */
    public Iterator iterator()
    {
        return new StateIterator();
    }
    
    class StateIterator implements Iterator
    {
        int i;
        StateIterator()
        {
            i=0;
        }

        public boolean hasNext()
        {
            return i < _s.length;
        }

        public Object next()
        {
            return _s[i++];
        }

        public void remove() {}
    }

    public String toString()
    {
        return _name;
    }
}
