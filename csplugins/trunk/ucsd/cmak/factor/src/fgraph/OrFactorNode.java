package fgraph;

import java.util.List;

public class OrFactorNode extends FactorNode
{
    protected static final double epOR = 0.001;
    
    private static OrFactorNode __singleton = new OrFactorNode();

    private OrFactorNode()
    {
        super(NodeType.OR_FACTOR);
    }

    public static OrFactorNode getInstance()
    {
        return __singleton;
    }

    public ProbTable maxProduct(List incomingMsgs, int n)
        throws AlgorithmException
    {
        ProbTable[] msgs = new ProbTable[incomingMsgs.size() - 1];
        for(int x=0, y=0; x < incomingMsgs.size(); x++)
        {
            if(x == n) continue;
            
            EdgeMessage em = (EdgeMessage) incomingMsgs.get(x);
            if(em.getVariableType() != NodeType.PATH_ACTIVE)
            {
                throw new AlgorithmException("node of type "
                                             + em.getVariableType()
                                             + " sent message to an OR factor node. ERROR");
            }
            msgs[y] = em.v2f();
            y++;
        }
        
        StateSet ss = StateSet.PATH_ACTIVE;
        ProbTable pt = new ProbTable(ss);
        
        double[] probs = new double[ss.size()];
        
        double maxFix = 0;
        for(int x=0; x < msgs.length; x++)
        {
            double tmp = maximizeFixed(x, msgs);
            if(tmp > maxFix)
            {
                maxFix = tmp;
            }
        }
        
        probs[ss.getIndex(State.ZERO)] = Math.max(maxFix,
                                                  maximize(msgs, epOR));
        
        probs[ss.getIndex(State.ONE)] = maximize(msgs, 1);
        
        pt.init(probs);
        
        return pt;
        
    }


    private double maximizeFixed(int fixed, ProbTable[] messages)
    {
        double m = 1;
        
        for(int x=0, n=messages.length; x < n; x++)
        {
            ProbTable pt = messages[x];
            
            if(x == fixed)
            {
                m *= pt.prob(State.ONE);
            }
            else
            {
                m *= pt.max();
            }
        }

        return m;
    }
    
    private double maximize(ProbTable[] messages, double weight)
    {
        double m = weight;

        if(weight <= 0)
        {
            m = 1;
        }

        for(int x=0, n=messages.length; x < n; x++)
        {
            ProbTable pt = messages[x];
            m *= pt.max();
        }

        return m;
    }


}
