public class EdgeStructure
{
    protected int source;
    protected int target;
    protected int edgeIndex;
    
    public EdgeStructure(int s, int t, int e)
    {
        source = s;
        target = t;
        edgeIndex = e;
    }

    boolean isUndirected()
    {
        return false;
    }

    int getSource()
    {
        return source;
    }
    
    int getTarget()
    {
        return target;
    }

    
    int getEdgeIndex()
    {
        return edgeIndex;
    }

    public String toString()
    {
        StringBuffer b = new StringBuffer("[");
        b.append(source);
        b.append(".");
        b.append(target);
        b.append(", ");
        b.append(edgeIndex);
        b.append("]");
        
        return b.toString();
    }
}
