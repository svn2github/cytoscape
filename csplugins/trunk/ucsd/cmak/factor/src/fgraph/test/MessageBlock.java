package fgraph.test;

import fgraph.NodeType;

import java.util.LinkedHashMap;

public class MessageBlock
{
    LinkedHashMap v2f;
    LinkedHashMap f2v;
    NodeType type;

    public MessageBlock()
    {
        v2f = new LinkedHashMap();
        f2v = new LinkedHashMap();
    }

    public LinkedHashMap getV2f()
    {
        return v2f;
    }

    public LinkedHashMap getF2v()
    {
        return f2v;
    }

    public NodeType getType()
    {
        return type;
    }

    public void setType(String s)
    {
        if(s.equalsIgnoreCase(NodeType.PATH_FACTOR.toString()))
        {
            type = NodeType.PATH_FACTOR;
        }
        else if(s.equalsIgnoreCase(NodeType.OR_FACTOR.toString()))
        {
            type = NodeType.OR_FACTOR;
        }
        else
        {
            type = null;
        }
    }
}
