package fgraph;

public class NodeType
{
    private final String name;

    private NodeType(String n)
    {
        name = n;
    }

    public String toString() { return name; }

    public static final NodeType EDGE = new NodeType("edge_presence");
    public static final NodeType SIGN = new NodeType("edge_sign");
    public static final NodeType DIR = new NodeType("edge_dir");
    public static final NodeType PATH_ACTIVE = new NodeType("path_active");
    public static final NodeType KO = new NodeType("ko");

    public static final NodeType FACTOR = new NodeType("factor");
    public static final NodeType OR_FACTOR = new NodeType("or_factor");
    public static final NodeType PATH_FACTOR = new NodeType("path_factor");
    
}

