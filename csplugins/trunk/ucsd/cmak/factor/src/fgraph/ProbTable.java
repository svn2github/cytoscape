package fgraph;

import java.util.Iterator;

/**
 * A ProbTable maps States to a probability
 */
public class ProbTable
{
    private static final double EPSILON = 1e-10;

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

    /**
     *
     *
     * @param
     * @return a copy of the probability array of this table.
     * @throws
     */
    double[] currentProbs()
    {
        double[] d = new double[_probs.length];
        System.arraycopy(_probs, 0, d, 0, _probs.length);

        return d;
    }
    
    public void init(double[] p)
    {
        System.arraycopy(p, 0, _probs, 0, p.length);

        _normalize(_probs);
        _findMax();
    }

    private void _findMax()
    {
        // find max
        _uniqueMax = true;
        _indOfMax = 0;
        for(int x=0, n=_probs.length; x < n; x++)
        {
            double diff = _probs[x] - _probs[_indOfMax];

            // probs[x] is greater than the old max
            if( diff > EPSILON)
            {
                _indOfMax = x;
                _uniqueMax = true;
            }
            // probs[x] is within epsilon of the old max
            // and x is not the index of the old max
            else if((Math.abs(diff) < EPSILON) && (x != _indOfMax))
            {
                _uniqueMax = false;
            }
        }
    }

    private void _normalize(double[] p)
    {
        double sum = 0;
        for(int x=0, n=p.length; x < n; x++)
        {
            sum += p[x];
        }

        if(sum != 0 && sum != 1)
        {
            for(int x=0, n=p.length; x < n; x++)
            {
                p[x] = p[x]/sum;
            }
        }
    }

    public boolean hasUniqueMax()
    {
        return _uniqueMax;
    }

    public State maxState()
    {
        if(hasUniqueMax())
        {
            return _ss.getState(_indOfMax);
        }
        else
        {
            return _ss.defaultState();
        }
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
            _normalize(_probs);
            _findMax();
        }
    }

    /**
     * @return true if the probabilites of this ProbTable are equal to the
     * probs of "p" within epsilon e.
     */
    public boolean equals(ProbTable p, double epsilon)
    {
        if(p.stateSet() == this.stateSet())
        {
            for(int x=0, n=_ss.size(); x < n; x++)
            {
                if(Math.abs(_probs[x] - p.prob(_ss.getState(x))) > epsilon)
                {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
    

    /**
     * @return true if the probabilites of this ProbTable are equal to the
     * probs of "p" within epsilon e.
     */
    public boolean equals(double[] p, double epsilon)
    {
        if(p.length == _probs.length)
        {
            _normalize(p);
            
            // check each element of p againt _probs
            for(int x=0, n=p.length; x < n; x++)
            {
                if(Math.abs(_probs[x] - p[x]) > epsilon)
                {
                    /*
                    System.out.println("ne: " + Math.abs(_probs[x] - p[x]) +
                                       " " + _probs[x] + " " + p[x]);
                    */
                    return false;
                }
            }
            return true;
        }

        return false;
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
                sb.append(" ");
            }
        }
        sb.append(']');

        return sb.toString();
    }

    
}
