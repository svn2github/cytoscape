import java.util.List;
import java.util.Arrays;
import java.util.Iterator;

public class VariableNode
{
    protected ProbTable probTable;
    protected NodeType type;
    protected StateSet states;
    protected boolean isFixed;
    protected State fixedState;

    protected double[] defaultProbs;
    
    protected int interactionGraphId1;
    protected int interactionGraphId2;
    
    public static VariableNode createEdge(int nodeIndex)
    {
        return new VariableNode(NodeType.EDGE, StateSet.EDGE, nodeIndex);
    }
    
    public static VariableNode createSign(int nodeIndex)
    {
        return defaultInit(new VariableNode(NodeType.SIGN, StateSet.SIGN, nodeIndex));
    }
    
    public static VariableNode createDirection(int nodeIndex)
    {
        return defaultInit(new VariableNode(NodeType.DIR, StateSet.DIR, nodeIndex));
    }
    
    public static VariableNode createPathActive(int pathNumber)
    {
        return defaultInit(new VariableNode(NodeType.PATH_ACTIVE, StateSet.PATH_ACTIVE, pathNumber));
    }
    
    public static VariableNode createKO(int koNode, int targetNode)
    {
        return new VariableNode(NodeType.KO, StateSet.KO, koNode, targetNode);
    }
    

    private static VariableNode defaultInit(VariableNode v)
    {
        v.initProbs(v.stateSet().defaultProbs());
        return v;
    }

    private VariableNode(NodeType t, StateSet s, int id)
    {
        this(t, s, id, 0);
    }
    /** private constructor to ensure that a node can only
     * be created by one of the factory methods
     */

    private VariableNode(NodeType t, StateSet s, int id1, int id2)
    {
        type = t;
        states = s;
        probTable = new ProbTable(s);
        isFixed = false;
        fixedState = null;
        interactionGraphId1 = id1;
        interactionGraphId2 = id2;
        defaultProbs = null;
    }

    /**
     * @return edgeIndex if this is an edge, dir, or sign var; pathNum if this
     * is a pathActive var; koNodeIndex if this is a KO node
     */
    public int getId()
    {
        return interactionGraphId1;
    }

    /**
     * CLUNKY: Is there a better way to to this???? 8/27/04
     * 
     * @return targetNodeIndex if this is a KO node, or 0 otherwise
     */
    public int getId2()
    {
        return interactionGraphId2;
    }
    
    
    /**
     * Initialize this variable with probabilites.
     * Do nothing if fixState has been called on this variable
     * How to ensure caller does not use the wrong vals?
     * 
     */
    public void initProbs(double[] vals)
    {
        if(!isFixed)
        {
            probTable.init(vals);
        }
    }
    
    
    public void setDefaultProbs(double[] vals)
    {
        if(!isFixed)
        {
            defaultProbs = copy(vals);
            
            probTable.init(vals);
        }
    }

    public double[] copy(double[] array)
    {
        double[] c = new double[array.length];
        System.arraycopy(array, 0, c, 0, array.length);
        return c;
    }
    
    /**
     * What to do if s is not a valid state of this node?
     * ProbTable contain 0 values (because all states will have
     * prob 0).
     */
    public void fixState(State fixed)
    {
        isFixed = true;
        fixedState = fixed;

        double[] p = new double[states.size()];
        for(Iterator it = states.iterator(); it.hasNext();)
        {
            State s = (State) it.next();
            int i = states.getIndex(s);

            if(s == fixed)
            {
                p[i] = 1;
            }
            else
            {
                p[i] = 0;
            }
        }

        probTable.init(p);
    }
    
    /**
     * Multiply all of the messages in the list and update this
     * node's probability table.
     */
    public void maxProduct(List incomingMsgs)
    {
        // do nothing if this probabilites of this node have been fixed
        if(isFixed)
        {
            return;
        }
        
        for(int m=0, N=incomingMsgs.size(); m < N; m++)
        {
            EdgeMessage em = (EdgeMessage) incomingMsgs.get(m);
            if(this.states != em.f2v().stateSet())
            {
                System.err.println("VariableNode.maxProduct called with inconsistent messages: " + em.f2v().stateSet());
                return;
            }
        }

        double[] newProb;
        if(defaultProbs != null)
        {
            newProb = copy(defaultProbs);
        }
        else
        {
            newProb = new double[states.size()];
            Arrays.fill(newProb, 1);
        }
        
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

    public ProbTable getProbs()
    {
        return probTable;
    }

    public boolean isFixed()
    {
        return isFixed;
    }

    public State fixedState()
    {
        return fixedState;
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
