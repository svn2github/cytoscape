import java.util.Iterator;

/**
 * A ProbTable maps States to a probability
 */
public class ProbTable
{
    private StateSet _ss;
    private double[] _probs;

    /* index of the max prob.  If there are multiple max (within epsilon?)
     * then this is the index of the first max in the array of probs
     */
    private int _indOfMax;
    private boolean _uniqueMax;

    public ProbTable(StateSet ss)
    {
        _ss = ss;
        _probs = new double[ss.size()];
        _indOfMax = 0;
        _uniqueMax = false;
    }

    public void init(double[] p)
    {
        System.arraycopy(p, 0, _probs, 0, p.length);

        _normalize();
        _findMax();
    }

    private void _findMax()
    {
        // find max
        _uniqueMax = true;
        _indOfMax = 0;
        for(int x=0, n=_probs.length; x < n; x++)
        {
            if(_probs[x] > _probs[_indOfMax])
            {
                _indOfMax = x;
            }
            if(_probs[x] == _probs[_indOfMax] && x != _indOfMax)
            {
                _uniqueMax = false;
            }
        }
    }

    private void _normalize()
    {
        double sum = 0;
        for(int x=0, n=_probs.length; x < n; x++)
        {
            sum += _probs[x];
        }
        for(int x=0, n=_probs.length; x < n; x++)
        {
            _probs[x] = _probs[x]/sum;
        }
    }

    public boolean hasUniqueMax()
    {
        return _uniqueMax;
    }

    public State maxState()
    {
        return _ss.getState(_indOfMax);
    }

    public double max()
    {
        return _probs[_indOfMax];
    }

    public double prob(State s)
    {
        int i = _ss.getIndex(s);
        if(i >= 0)
        {
            return _probs[i];
        }
        
        return 0;
    }

    public StateSet stateSet()
    {
        return _ss;
    }

    /**
     * Modify the probabilities of this table by multiply by another prob table
     */
    public void multiplyBy(ProbTable p)
    {
        if(p.stateSet() == this.stateSet())
        {
            for(int x=0, n=_ss.size(); x < n; x++)
            {
                _probs[x] = _probs[x] * p.prob(_ss.getState(x));
            }
            _normalize();
            _findMax();
        }
    }

    public String toStringDetailed()
    {
        String tab = "\t";
        StringBuffer sb = new StringBuffer();
        for(Iterator it = stateSet().iterator(); it.hasNext();)
        {
            State s = (State) it.next();
            sb.append(s);
            sb.append(tab);
            sb.append(prob(s));
            sb.append("\n");
        }
        sb.append("max=");
        sb.append(max());
        sb.append(" at state ");
        sb.append(maxState());
        sb.append(", hasUniqueMax=");
        sb.append(hasUniqueMax());

        return sb.toString();
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        for(Iterator it = stateSet().iterator(); it.hasNext();)
        {
            State s = (State) it.next();
            
            sb.append(prob(s));
            if(it.hasNext())
            {
                sb.append(", ");
            }
        }
        sb.append(']');

        return sb.toString();
    }

    
}
