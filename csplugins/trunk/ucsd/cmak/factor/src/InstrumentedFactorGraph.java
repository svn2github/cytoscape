import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import giny.model.RootGraph;

import cytoscape.util.GinyFactory;
import cytoscape.data.mRNAMeasurement;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * A bipartite graph of Variable and Factor nodes that represents
 * constraints on paths in a physical interaction network that
 * explain knockout effects.
 *
 * <p>
 * Ref: Kschischang, Frey, Loeliger. IEEE Trans Inf Theory. Feb 2001
 */
public class InstrumentedFactorGraph extends PrintableFactorGraph
{
    private int nConstVar;

    private OpenIntObjectHashMap _maxMap;

    private String _outFile;

    private OpenIntIntHashMap _changeCount;
    
    protected InstrumentedFactorGraph(InteractionGraph ig, PathResult pathResults,
                                      String outputFile)
    {
        super(ig, pathResults);
        _outFile = outputFile;
        _maxMap = new OpenIntObjectHashMap();
        _changeCount = new OpenIntIntHashMap();
    }

    public static FactorGraph create(InteractionGraph ig, PathResult pathResults,
                                     String outputFile)
    {
        FactorGraph fg = new InstrumentedFactorGraph(ig, pathResults, outputFile);
        fg.buildGraph(ig, pathResults);

        return fg;
    }


    /**
     * Run the max product algorithm.
     *
     * FIX: termination condition. decompose degenerate networks.
     */
    public void runMaxProduct()  throws AlgorithmException
    {
        _vars.trimToSize();
        _factors.trimToSize();
        
        int[] v = _vars.elements();
        int[] f = _factors.elements();

        initVarMax();
        
        initVar2Factor(v);

        int N = (30 * _paths.getMaxPathLength());
        
        int[] constVar = new int[N];
        int[] constMax = new int[N];
        int[] diffMax = new int[N];
        
        boolean terminate = false;

        Set constantVars = new HashSet();
        _changeCount.clear();
        _changeCount.ensureCapacity(_vars.size());
        
        for(int x=0; x < N; x++)
        {
            nConstVar = 0;
                              
            computeFactor2Var(f);
            terminate = computeVar2Factor(v);
            
            Set a = countVarMax();
            
            constVar[x] = nConstVar;
            constMax[x] = a.size();
            diffMax[x] = a.size() - intersection(a, constantVars).size();

            constantVars = a;
            
            System.out.println("mp loop: " + x + " constVar=" + constVar[x]
                               + " constMax=" + constMax[x]
                               + " diffMax=" + diffMax[x] + " / " + _vars.size());
            
        }

        IntArrayList aa = _changeCount.keys();
        System.out.println(aa.size() + " vars change one or more times");
        System.out.println(_vars.size() - aa.size() + " vars never change");

        int[] counts = new int[N + 1];

        for(int x=0; x < aa.size(); x++)
        {
            counts[_changeCount.get(aa.get(x))] += 1;
        }
        System.out.println("Change counts");
        for(int x=0; x < counts.length; x++)
        {
            
            System.out.println("  " + x + " " + counts[x]);
        }
        
        updateEdgeAnnotation();

        printData(counts, _outFile + "_cnt.pf");
        printData(constVar, _outFile + "_var.pf");
        printData(constMax, _outFile + "_max.pf");
        printData(diffMax, _outFile + "_diff.pf");
    }

    private Set intersection(Set a, Set b)
    {
        Set intersect = new HashSet();

        for(Iterator it = a.iterator(); it.hasNext();)
        {
            Object o = it.next();
            if(b.contains(o))
            {
                intersect.add(o);
            }
        }

        return intersect;
    }
    
    
    private void printData(int[] data, String fname)
    {
        try
        {
            PrintStream out = new PrintStream(new FileOutputStream(fname));
            for(int x=0; x < data.length; x++)
            {
                out.print(x);
                out.print(", ");
                out.println(data[x]);
                
            }
            out.close();
        }
        catch(IOException e)
        {
            System.err.println("Error writing to: " + fname);
            e.printStackTrace();
        }

    }
    
    private boolean terminate()
    {
        return false;
    }
    
    protected boolean computeVar2Factor(int[] nodes)
    {
        boolean noChange = true;
        
        for(int x=0, N=nodes.length; x < N; x++)
        {
            int n = nodes[x];
            VariableNode vn = (VariableNode) _nodeMap.get(n);

            List messages = _adjacencyMap.get(n);
            boolean nodeConstant = vn.maxProduct(messages, true);
            noChange = nodeConstant & noChange;

            if(nodeConstant)
            {
                nConstVar++;
            }
        }

        return noChange;
    }

    public void initVarMax()
    {
        _maxMap.clear();
        _maxMap.ensureCapacity(_vars.size());

        for(int v=0; v < _vars.size(); v++)
        {
            VariableNode vn = (VariableNode) _nodeMap.get(_vars.get(v));
            ProbTable pt = vn.getProbs();

            if(!pt.hasUniqueMax())
            {
                _maxMap.put(_vars.get(v), null);
            }
            else
            {
                _maxMap.put(_vars.get(v), pt.maxState());
            }
        }
    }

    /**
     * Return the set of vars (Integers) that are constant
     */
    public Set countVarMax()
    {
        Set constant = new HashSet();
        
        for(int x=0; x < _vars.size(); x++)
        {
            int v = _vars.get(x);
            VariableNode vn = (VariableNode) _nodeMap.get(v);
            ProbTable pt = vn.getProbs();

            Object oldMax = _maxMap.get(v);
            if(pt.hasUniqueMax())
            {
                // var is constant
                if(oldMax == pt.maxState())
                {
                    constant.add(new Integer(v));
                }
                else
                {
                    countChange(v);
                    _maxMap.put(v, pt.maxState());
                }
            }
            else 
            {
                // var had max state, now does not
                if(oldMax != null)
                {
                    countChange(v);
                    _maxMap.put(v, null);
                }
            }
        }

        return constant;
    }


    private void countChange(int v)
    {
        if(_changeCount.containsKey(v))
        {
            //System.out.println(" incrementing " + v + " " + _changeCount.get(v));
            _changeCount.put(v, _changeCount.get(v) + 1);
        }
        else
        {
            _changeCount.put(v, 1);
        }
    }
    
    private String getType(int var)
    {
        StringBuffer b = new StringBuffer();
        
        if(_nodeMap.containsKey(var))
        {
            VariableNode node = (VariableNode) _nodeMap.get(var);
            b.append(node.type());
        }

        return b.toString();
    }
}
