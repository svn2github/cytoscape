public class TestMessage
{
    private int index;
    private ProbTable prob;
    private NodeType type;
    private State dir;
    
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

    
    public int getIndex() {return index;}
    public ProbTable getProbTable() {return prob;}
    public NodeType getType() {return type;}
    public State getDir() {return dir;}

    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append(type);
        b.append(" [" + index + "] ");
        if(dir != null)
        {
            b.append(dir);
        }
        b.append(" ");
        b.append(prob.toString());

        return b.toString();
    }
}
