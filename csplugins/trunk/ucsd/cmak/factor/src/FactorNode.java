import java.util.List;

public abstract class FactorNode extends FGNode
{
    public FactorNode()
    {
        super(NodeType.FACTOR);
    }
    
    public abstract ProbTable maxProduct(List incomingMsgs, int n, VariableNode target)
        throws AlgorithmException;
}
