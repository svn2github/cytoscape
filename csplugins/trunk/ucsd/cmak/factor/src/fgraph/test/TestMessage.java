package fgraph.test;

import fgraph.NodeType;
import fgraph.ProbTable;
import fgraph.State;


public class TestMessage
{
    private int index;
    private ProbTable prob;
    private NodeType type;
    private State dir;

    private String from;
    private String to;
    
    public TestMessage(int index, ProbTable prob, NodeType type)
    {
        this.index = index;
        this.prob = prob;
        this.type = type;
    }

    public TestMessage(int index, ProbTable prob, NodeType type, State pm)
    {
        this.index = index;
        this.prob = prob;
        this.type = type;
        this.dir = pm;
    }

    public String getFrom() {return from;}
    public String getTo() {return to;}
    public void setFrom(String s) {from = s;}
    public void setTo(String s) {to = s;}

    
    public int getIndex() {return index;}
    public ProbTable getProbTable() {return prob;}
    public NodeType getType() {return type;}
    public State getDir() {return dir;}

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(" " + index + " ");
        b.append(type);
        b.append(" (" + from + " " + to + ") ");
        if(dir != null)
        {
            b.append(dir);
        }
        b.append(" ");
        b.append(prob.toString());

        return b.toString();
    }
}
