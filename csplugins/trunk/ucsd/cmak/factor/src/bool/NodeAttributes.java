package bool;

import java.util.List;

public abstract class NodeAttributes
{
    double[] _probs;
    int _incidentMessages;
    int _nodeId;
    
    public NodeAttributes(int numStates)
    {
        _probs = new double[numStates];
        _incidentMessages = 0;
        _nodeId = 0;
    }

    public void setId(int id)
    {
        _nodeId = id;
    }

    public int getId()
    {
        return _nodeId;
    }

    public void setProb(int state, double val)
    {
        _probs[state] = val;
    }

    public double getProb(int state)
    {
        return _probs[state];
    }

    public void incrementMessages()
    {
        _incidentMessages += 1;
    }

    public int numIncidentMessages()
    {
        return _incidentMessages;
    }

    /** 
     * normalize the probabilities in a so that they sum to 1 
     * assumes that all of the elements of a are >= 0
     */
     
    protected void normalize()
    {
        double sum = 0;

        for(int x=0; x < _probs.length; x++)
        {
            sum += _probs[x];
        }

        // error handling here?
        if(sum > 0)
        {
            for(int x=0; x < _probs.length; x++)
            {
                _probs[x] = _probs[x]/sum;
            }
        }
    }


    public abstract Message sumProduct(List msgs, int out);
}

