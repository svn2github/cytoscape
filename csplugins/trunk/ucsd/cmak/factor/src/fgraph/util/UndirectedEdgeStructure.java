package fgraph.util;

public class UndirectedEdgeStructure extends EdgeStructure
{
    public UndirectedEdgeStructure(int s, int t, int e)
    {
        super(s, t, e);
    }

    boolean isUndirected()
    {
        return true;
    }

}
