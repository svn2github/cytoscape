package fgraph;

import java.util.List;

public abstract class FactorNode extends FGNode
{
    public FactorNode()
    {
        super(NodeType.FACTOR);
    }

    public FactorNode(NodeType type)
    {
        super(type);
    }
    
    public abstract ProbTable maxProduct(List incomingMsgs, int n)
        throws AlgorithmException;
}
