package fgraph;

import cern.colt.list.IntArrayList;

import java.util.List;
import java.util.ArrayList;

public class Submodel
{
    private static int ID = 0;

    private int _id;
    private IntArrayList _vars;
    private boolean _isInvariant;
    private int _independentVar;
    private List _edges;
    private int _explainedKO;
    private int _depVars;
    private int[] _activePaths; // paths that are active when this model is created

    public static void resetId()
    {
        ID = 0;
    }
    
    public Submodel()
    {
        _edges = new ArrayList();
        _vars = new IntArrayList();
        _isInvariant = false;
        _independentVar = 0;
        _id = Submodel.ID;
        _explainedKO = 0;
        _depVars = 0;
        
        Submodel.ID++;
    }

    public void setNumExplainedKO(int x)
    {
        _explainedKO = x;
    }
    
    /**
     *
     * @return the number of knockout experiments explained by this model
     */
    public int getNumExplainedKO()
    {
        return _explainedKO;
    }
    
    
    public void setNumDepVars(int x)
    {
        _depVars = x;
    }
    
    /**
     *
     * @return the number of knockout experiments explained by this model
     */
    public int getNumDepVars()
    {
        return _depVars;
    }
    
    
    public int getId() {return _id;}
    
    /**
     * Should this check for uniqueness before
     * adding a new edge?
     * @param
     * @return
     * @throws
     */
    public void addEdge(AnnotatedEdge edge)
    {
        _edges.add(edge);
    }

    public List getEdges()
    {
        return _edges;
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

    /**
     * Running time O(|this.size()| * |m.size()|)
     *
     * @param m another submodel
     * @return true if any of the variables in m are in this submodel,
     *         false otherwise.
     * @throws
     */
    public boolean overlaps(Submodel m)
    {
        IntArrayList mV = m.getVars();

        for(int x=0; x < mV.size(); x++)
        {
            if(this.containsVar(mV.get(x)))
            {
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * Merge the vars from another submodel into this one.
     *
     * @param m another submodel
     * @return
     * @throws
     */
    public void merge(Submodel m)
    {
        if(!m.isInvariant())
        {
            int iv = m.getIndependentVar();
            if(!this.containsVar(iv))
            {
                addVar(iv);
            }

            IntArrayList mV = m.getVars();
            for(int x=0; x < mV.size(); x++)
            {
                int v = mV.get(x);
                if(!this.containsVar(v))
                {
                    addVar(v);
                }
            }
        }
    }

    /*
    public void setActivePaths(int[] activePaths)
    {
        _activePaths = activePaths;
    }

    public int[] getActivePaths()
    {
        return _activePaths;
    }
    */
    /**
     * Find all active paths that are covered by edges in this model.
     *
     * @param activePaths a list of active paths
     * @param paths data structure mapping edges to paths
     *
    public void recordActivePaths(int[] activePaths, PathResults paths)
    {

    }
    */
    
    public String toString()
    {
        return Integer.toString(getId());
    }
}
