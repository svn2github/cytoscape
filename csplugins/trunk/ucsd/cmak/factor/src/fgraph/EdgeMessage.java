package fgraph;


public class EdgeMessage
{
    protected ProbTable v2f;
    protected ProbTable f2v;
    protected NodeType varNode;
    //protected FactorNode f;

    protected int v;
    protected int f;
    
    protected State dir;
    
    public EdgeMessage(NodeType varNode, int vn, int fn)
    {
        this.varNode = varNode;
        this.v = vn;
        this.f = fn;
    }


    public EdgeMessage(NodeType varNode, int vn, int fn, State dir)
    {
        this.varNode = varNode;
        this.v = vn;
        this.f = fn;
        this.dir = dir;
    }

    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        b.append("(" + v + " " + f + ") ");
        b.append("{");
        b.append("f2v=");// + f2v.stateSet() + " ");
        b.append(f2v.toString());
        b.append(" v2f=");// + v2f.stateSet() + " ");
        b.append(v2f.toString());
        b.append(" " + varNode + " ");
        if(dir != null)
        {
            b.append(dir);
        }
        b.append("}");
        
        return b.toString();
    }
    

    /**
     * @return If this message connects a direction node to a path factor node,
     * then return the direction of information flow implied by the knockout
     * effect.  State.PLUS == source->target.  State.MINUS == the reverse.
     * Otherwise return null.
     */
    public State getDir()
    {
        return dir;
    }
    
    
    public NodeType getVariableType()
    {
        return varNode;
    }
    /*    
    public FactorNode getFactor()
    {
        return f;
    }
    */
    
    public int getVariableIndex()
    {
        return v;
    }
        
    public int getFactorIndex()
    {
        return f;
    }

    
    public void v2f(ProbTable p)
    {
        v2f = p;
    }

    public void f2v(ProbTable p)
    {
        f2v = p;
    }

    public ProbTable v2f()
    {
        return v2f;
    }

    public ProbTable f2v()
    {
        return f2v;
    }
}
