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

    /**
     * Check if a node of a specific type should be added to this model.
     *
     * @param t the type
     * @return true if t is NodeType.SIGN || NodeType.DIR || NodeType.KO,
     *         false otherwise
     */
    public boolean acceptsType(NodeType t)
    {
        return ((t == NodeType.SIGN) ||
                (t == NodeType.DIR) ||
                (t == NodeType.KO));
    }

    /**
     * Linear time search.  Does this need to be more efficient?
     *
     * @param var the variable
     * @return true if var is in this submodel, false otherwise
     */
    public boolean containsVar(int var)
    {
        for(int x=0; x < _vars.size(); x++)
        {
            if(_vars.getQuick(x) == var)
            {
                return true;
            }
        }
        return false;
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

    /**
     *
     * @return true if this is the invariant submodel
     */
    public boolean isInvariant()
    {
        return _isInvariant;
    }

    /**
     * Set if this is the invariant submodel.
     * This model cannot be invariant and have a nonzero
     * independent variable.
     *
     * @param true if this is invariant, false otherwise
     */
    public void setInvariant(boolean b)
    {
        if(b && _independentVar != 0)
        {
            return;
        }
        
        _isInvariant = b;
    }

    public int getIndependentVar()
    {
        return _independentVar;
    }

    /**
     * Set the variable that was manually fixed (the independent var)
     * that generated this submodel.  Setting an independent var
     * automatically means that this model cannot be the invariant
     * model.
     * 
     * @param
     * @return
     * @throws
     */
    public void setIndependentVar(int node)
    {
        _independentVar = node;
        setInvariant(false);
        this.addVar(node);
    }

}
