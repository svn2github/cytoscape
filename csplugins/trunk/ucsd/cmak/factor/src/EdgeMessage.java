
public class EdgeMessage
{
    protected ProbTable v2f;
    protected ProbTable f2v;
    protected VariableNode v;
    protected FactorNode f;

    protected State dir;
    
    public EdgeMessage(VariableNode vn, FactorNode fn)
    {
        this.v = vn;
        this.f = fn;
    }


    public EdgeMessage(VariableNode vn, FactorNode fn, State dir)
    {
        this.v = vn;
        this.f = fn;
        this.dir = dir;
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
    
    
    public VariableNode getVariable()
    {
        return v;
    }
        
    public FactorNode getFactor()
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
