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
    
}

