import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public class VariableNode
{
    protected ProbTable probTable;
    protected NodeType type;
    protected StateSet states;

    private VariableNode(NodeType t, StateSet s)
    {
        type = t;
        states = s;
        probTable = new ProbTable(s);
    }

    /**
     * How to ensure caller does not use the wrong vals?
     */
    public void initProbs(double[] vals)
    {
        probTable.init(vals);
    }
    
    public static VariableNode createEdge()
    {
        return new VariableNode(NodeType.EDGE, StateSet.EDGE);
    }
    
    public static VariableNode createSign()
    {
        return defaultInit(new VariableNode(NodeType.SIGN, StateSet.SIGN));
    }
    
    public static VariableNode createDirection()
    {
        return defaultInit(new VariableNode(NodeType.DIR, StateSet.DIR));
    }
    
    public static VariableNode createPathActive()
    {
        return defaultInit(new VariableNode(NodeType.PATH_ACTIVE, StateSet.PATH_ACTIVE));
    }
    
    public static VariableNode createKO()
    {
        return new VariableNode(NodeType.KO, StateSet.KO);
    }
    

    private static VariableNode defaultInit(VariableNode v)
    {
        v.initProbs(v.stateSet().defaultProbs());
        return v;
    }

    public ProbTable getProbs()
    {
        return probTable;
    }
    
    /**
     * Multiply all of the messages in the list and update this
     * node's probability table.
     */
    public void maxProduct(List incomingMsgs)
    {
        for(int m=0, N=incomingMsgs.size(); m < N; m++)
        {
            EdgeMessage em = (EdgeMessage) incomingMsgs.get(m);
            if(this.states != em.f2v().stateSet())
            {
                System.err.println("VariableNode.maxProduct called with inconsistent messages: " + em.f2v().stateSet());
                return;
            }
        }

        double[] newProb = new double[this.states.size()];
        Arrays.fill(newProb, 1);
        for(Iterator it = states.iterator(); it.hasNext();)
        {
            State s = (State) it.next();
            int i = states.getIndex(s);
            for(int m=0, N=incomingMsgs.size(); m < N; m++)
            {
                EdgeMessage em = (EdgeMessage) incomingMsgs.get(m);
                newProb[i] *= em.f2v().prob(s);
            }
        }
        
        probTable.init(newProb);
    }



    
    /**
     * @return the probability that this node is in state "s" or 0 if
     * s is an invalid state.
     */
    public double prob(State s)
    {
        return probTable.prob(s);
    }

    public NodeType type()
    {
        return type;
    }

    public StateSet stateSet()
    {
        return states;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(type.toString());
        b.append("\n");
        b.append(probTable.toString());
        
        return b.toString();
    }
}
