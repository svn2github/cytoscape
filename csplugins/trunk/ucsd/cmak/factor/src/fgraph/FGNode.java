package fgraph;

public abstract class FGNode
{
    private NodeType type;

    public FGNode(NodeType t)
    {
        type = t;
    }

    public boolean isType(NodeType t)
    {
        return t == type;
    }
    
    public NodeType type()
    {
        return type;
    }

    
}
