import cern.colt.list.IntArrayList;

public class Submodel
{
    private IntArrayList _vars;
    private boolean _isInvariant;
    private int _independentVar;
    
    public Submodel()
    {
        _vars = new IntArrayList();
        _isInvariant = false;
        _independentVar = 0;
    }

    public int size()
    {
        return _vars.size();
    }
    
    public void addVar(int n)
    {
        _vars.add(n);
    }

    public IntArrayList getVars()
    {
        return _vars;
    }

    public boolean isInvariant()
    {
        return _isInvariant;
    }

    public void setInvariant(boolean b)
    {
        _isInvariant = b;
    }

    public int getIndependentVar()
    {
        return _independentVar;
    }

    public void setIndependentVar(int node)
    {
        _independentVar = node;
        this.addVar(node);
    }

}
