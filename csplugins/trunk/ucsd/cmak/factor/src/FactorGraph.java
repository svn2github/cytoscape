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
public class FactorGraph
{
    // used to compute the a priori probabilities of
    // knockout nodes
    private static double koinv = 1e-20;

    // used to compute likelihood ratios
    private static double NUM_REPLICATES = 3;
    private static double lnN = Math.log(NUM_REPLICATES);

    // the FactorGraph
    protected RootGraph _g;
    
    // map node index to a VariableNode or FactorNode object
    protected OpenIntObjectHashMap _nodeMap;

    // map path number to path-factor node index
    private OpenIntIntHashMap _pathMap; 

    private IntArrayList _pathActive;
    private IntArrayList _vars;
    private IntArrayList _factors;
    
    // map interaction edge index to AnnotatedEdge
    protected OpenIntObjectHashMap _edgeMap;
    

    // the InteractionGraph that this FactorGraph is based on
    protected InteractionGraph _ig;

    // the PathResults that this FactorGraph is based on
    private PathResult _paths;
    
    // adjaceny list of edge messages
    // maps a factor graph node index to the List of EdgeMessages
    // that connect that node to its neighbors in the factor graph.
    // Used for MaxProduct algorithm.
    private IntListMap _adjacencyMap;


    /**
     * So the PrintableFactorGraph subclass can use
     * a different type of underlying graph
     */
    protected RootGraph _newRootGraph()
    {
        return new DummyRootGraph();
    }


    protected FactorGraph(InteractionGraph ig, PathResult pathResults)
    {
        _ig = ig;
        _paths = pathResults;
        _g = _newRootGraph();
        
        IntListMap edge2path = pathResults.getEdge2PathMap();

        int nC = 0;
        int nN = 0; // number of nodes
        int nE = 0; // number of edges
        
        IntArrayList kos = pathResults.getKOs();
        IntArrayList edges = edge2path.keys();

        // number of nodes in factor graph =
        //     2 * #knockouts
        //   + 2*number-of-paths 
        //   + 3 * number of unique edges used on paths
        nN = 2 * kos.size() + 2 * pathResults.getPathCount() + 3 * edges.size();
        
        // number of edges: 3 edges connect path-factor node to sigma, k, and OR
        // 3 edges connect path-factor to edge, dir, sign variables
        // 
        // lower bound = 3 * pathcount + 3 * 1 * pathCount (1 edge)
        // upper bound = 3 * pathcount + 3 * 5 * pathCount (5 edge)
        //
        // use 4 edges per path = (3 + 12 = 15) * pathcount
        nE = 12 * pathResults.getPathCount();

        System.out.println("Initializing factor graph. " + nN + " nodes, " +
                           nE + " estimated edges.");
        
        _g.ensureCapacity(nN, nE);

        _nodeMap = new OpenIntObjectHashMap(nN);
        _pathMap = new OpenIntIntHashMap(pathResults.getPathCount());
        _edgeMap = new OpenIntObjectHashMap(edges.size());
        _adjacencyMap = new IntListMap(nN);
            
        _pathActive = new IntArrayList(pathResults.getPathCount());
        _factors = new IntArrayList(kos.size() + pathResults.getPathCount());
        _vars = new IntArrayList(nN - kos.size() - pathResults.getPathCount());
    }

    public static FactorGraph create(InteractionGraph ig, PathResult pathResults)
    {
        FactorGraph fg = new FactorGraph(ig, pathResults);
        fg.buildGraph(ig, pathResults);
        return fg;
    }
    
    /**
     * Create a FactorGraph from an InteractionGraph and the
     * results of finding paths in that InteractionGraph.
     *
     * @param pathResults a DFSPath results object
     * @param ig an interaction graph.  The methods setExpressionData
     * and setEdgeData must have been called on the interaction graph.
     */
    protected void buildGraph(InteractionGraph ig, PathResult pathResults)
    {
        IntListMap edge2path = pathResults.getEdge2PathMap();
        IntArrayList kos = pathResults.getKOs();
        IntArrayList edges = edge2path.keys();

        int cN = 0;
        int cE = 0;
        
        for(int x=0, N = kos.size(); x < N; x++)
        {
            int knockedOut =  kos.get(x);
            Target2PathMap target2path = pathResults.getTarget2PathMap(knockedOut);

            IntArrayList targets = target2path.keys();

            for(int i=0, Nt = targets.size(); i < Nt; i++)
            {
                int t = targets.get(i);
                int ko = createKO(knockedOut, t);
                int or = createOR(knockedOut, t);

                IntArrayList paths = (IntArrayList) target2path.get(t);

                cN += 2;

                for(int j=0, Np =paths.size(); j < Np; j++)
                {
                    int path = paths.get(j);
                    int pa = createPathActive(path);
                    int pf = createPathFactor(path);

                    cN += 2;
                    cE += 3;
                    
                    connect(pa, pf);
                    connect(ko, pf);
                    connect(pa, or);
                }
            }
        }
        
        System.out.println("processed ko, OR, path nodes. cN=" + cN + ", cE=" + cE);
        
        for(int i=0, Ne = edges.size(); i < Ne; i++)
        {
            int edge = edges.get(i);

            AnnotatedEdge aEdge = createAnnotatedEdge(edge);
            cN += 3;
            
            List pathIntervals = edge2path.get(edge);
            
            for(int j=0, Np =pathIntervals.size(); j < Np; j++)
            {
                PathResult.Interval paths = (PathResult.Interval) pathIntervals.get(j);
                for(int k=paths.getStart(), m=paths.getEnd(); k < m; k++)
                {
                    int pf = getPathFactorIndex(k);
                    connect(aEdge.fgIndex, pf);
                    connect(aEdge.dirIndex, pf, paths.getDir());
                    connect(aEdge.signIndex, pf);                

                    cE += 3;
                }
            }
        }

        System.out.println("processed edges. total cN=" + cN + ", cE=" + cE);
    }

    /**
     * @return the RootGraph index of the PathFactorNode that corresponds to
     * pathNumber or 0 if pathNumber is not a known path.
     */
    private int getPathFactorIndex(int pathNumber)
    {
        return _pathMap.get(pathNumber);
    }

    /**
     * Create an undirected edge between a variable node and factor node.
     * This version of connect is called if varNode is a Direction variable node.
     *
     * @param dir the State (PLUS|MINUS) that the direction variable must take
     * for it to be consistent with a specific path.
     * 
     * @return the RootGraph index of the edge
     */
    private int connect(int varNode, int facNode, State dir)
    {
        EdgeMessage em = new EdgeMessage((VariableNode) _nodeMap.get(varNode),
                                         (FactorNode) _nodeMap.get(facNode),
                                         dir);
        _adjacencyMap.add(varNode, em);
        _adjacencyMap.add(facNode, em);
        
        return _g.createEdge(varNode, facNode, false);
    }
    
    /**
     * Create an undirected edge between a variable node and factor node
     * @return the RootGraph index of the edge
     */
    private int connect(int varNode, int facNode)
    {
        EdgeMessage em = new EdgeMessage((VariableNode) _nodeMap.get(varNode),
                                         (FactorNode) _nodeMap.get(facNode));
        _adjacencyMap.add(varNode, em);
        _adjacencyMap.add(facNode, em);
        
        return _g.createEdge(varNode, facNode, false);
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private AnnotatedEdge createAnnotatedEdge(int interactionEdgeIndex)
    {
        AnnotatedEdge ae = new AnnotatedEdge(interactionEdgeIndex);

        ae.fgIndex = createEdge(interactionEdgeIndex);
        ae.dirIndex = createDir(interactionEdgeIndex);
        ae.signIndex = createSign(interactionEdgeIndex);
        ae.label = _ig.edgeLabel(interactionEdgeIndex);
        
        _edgeMap.put(interactionEdgeIndex, ae);

        return ae;
    }

    /**
     * Create an edge variable node in the factor graph and initialize
     * its a priori probability using pvalues (if the edge is protein-DNA)
     * or probabilities (if the edge is protein-protein).
     * 
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createEdge(int interactionEdgeIndex)
    {
        int node = _g.createNode();

        _vars.add(node);

        VariableNode vn = VariableNode.createEdge(interactionEdgeIndex);
        _nodeMap.put(node, vn);

        StateSet ss = vn.stateSet();
        double[] prob = new double[ss.size()];

        double val = _ig.getEdgeValue(interactionEdgeIndex);

        if(_ig.isProteinDNA(interactionEdgeIndex))
        {
            prob[ss.getIndex(State.ZERO)] = 1;
            prob[ss.getIndex(State.ONE)] = likelihoodRatio(val);
        }
        else
        {
            prob[ss.getIndex(State.ZERO)] = 1 - val;
            prob[ss.getIndex(State.ONE)] = val;
        }

        vn.setDefaultProbs(prob);

        return node;
    }


    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createSign(int interactionEdgeIndex)
    {
        int node = _g.createNode();

        _vars.add(node);
        _nodeMap.put(node, VariableNode.createSign(interactionEdgeIndex));
        return node;
    }

    /**
     * If the edge is a protein-DNA edge, fix its direction.
     * 
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createDir(int interactionEdgeIndex)
    {
        int node = _g.createNode();

        _vars.add(node);
        
        VariableNode vn = VariableNode.createDirection(interactionEdgeIndex);

        if(_ig.isProteinDNA(interactionEdgeIndex))
        {
            vn.fixState(_ig.getFixedDir(interactionEdgeIndex));
        }
        
        _nodeMap.put(node, vn);
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createPathActive(int pathNumber)
    {
        int node = _g.createNode();

        _pathActive.add(node);
        _vars.add(node);
        _nodeMap.put(node, VariableNode.createPathActive(pathNumber));
        return node;
    }


    /**
     * Create a knockout variable node in the factor graph and initialize
     * its probabilities using its expression pvalue and logratio.
     * 
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createKO(int koNodeIndex, int targetNodeIndex)
    {
        int node = _g.createNode();

        _vars.add(node);
        
        VariableNode vn = VariableNode.createKO(koNodeIndex, targetNodeIndex);
        _nodeMap.put(node, vn);

        mRNAMeasurement m = _ig.getExpression(koNodeIndex, targetNodeIndex);
        if(m == null)
        {
            System.err.println("null mRNAMeasurement for " + koNodeIndex + ", " 
                               + targetNodeIndex);
            return node;
        }

        StateSet ss = vn.stateSet();
        double[] prob = new double[ss.size()];
        prob[ss.getIndex(State.ZERO)] = 1;

        if(m.getRatio() > 0)
        {
            prob[ss.getIndex(State.PLUS)] = likelihoodRatio(m.getSignificance());;
            prob[ss.getIndex(State.MINUS)] = koinv;
        }
        else
        {
            prob[ss.getIndex(State.PLUS)] = koinv;
            prob[ss.getIndex(State.MINUS)] = likelihoodRatio(m.getSignificance());
        }
        vn.setDefaultProbs(prob);

        return node;
    }

    private double likelihoodRatio(double pval)
    {
        return Math.exp(0.5* (ChiSquaredDistribution.inverseCDFMinus1(pval) - lnN));
    }
    
    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createOR(int koNodeIndex, int targetNodeIndex)
    {
        int node = _g.createNode();

        _factors.add(node);
        _nodeMap.put(node, OrFactorNode.getInstance());
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createPathFactor(int pathNumber)
    {
        int node = _g.createNode();

        _factors.add(node);
        _nodeMap.put(node, CachedPathFactorNode.getInstance());
        _pathMap.put(pathNumber, node);

        return node;
    }

    /**
     * @return if nodeIndex refers to a variable node, return the max state
     * of the node if it has a unique max.  Return null of nodeIndex is a
     * factor node or if the variable does not have a unique max.
     */ 
    private State maxState(int nodeIndex)
    {
        Object o = _nodeMap.get(nodeIndex);

        if(o instanceof VariableNode)
        {
            ProbTable pt = ((VariableNode) o).getProbs();

            if(pt.hasUniqueMax())
            {
                return pt.maxState();
            }
        }
        
        return null;
    }

    /**
     * Update the InteractionGraph associated with this factor graph
     * with the edges that are active.  Active edges are determined by calling
     * runMaxProduct, or if runMaxProduct has not been called, active
     * edges will be determinied by their a priori probabilities.
     * <p>
     * An edge is active if its max state is ONE and it is in at least
     * one active path.
     */
    public void updateInteractionGraph()
    {
        int[] activePaths = getActivePaths();

        ObjectArrayList aes = _edgeMap.values();
        
        List activeEdges = new ArrayList();
        
        for(int n=0, N =aes.size(); n < N; n++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) aes.get(n);

            if((ae.maxState == State.ONE)
               &&  _paths.isEdgeOnPath(ae.interactionIndex,
                                       activePaths))
            {
                activeEdges.add(ae);
            }
        }
        
        _ig.setActiveEdges(activeEdges);
    }

    int[] getActivePaths()
    {
        IntArrayList l = new IntArrayList();
        
        for(int s=0, N=_pathActive.size(); s < N; s++)
        {
            VariableNode vn = (VariableNode) _nodeMap.get(_pathActive.get(s));
            ProbTable pt = vn.getProbs();

            if(pt.hasUniqueMax() && (pt.maxState() == State.ONE))
            {
                l.add(vn.getId());
            }
        }
        l.trimToSize();
        
        return l.elements();
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

        initVar2Factor(v);
        computeFactor2Var(f);
        computeVar2Factor(v);

        for(int x=0; x < 2; x++)
        {
            computeFactor2Var(f);
            computeVar2Factor(v);
        }

        updateEdgeAnnotation();
    }

    private void updateEdgeAnnotation()
    {
        ObjectArrayList aes = _edgeMap.values();

        for(int x=0; x < aes.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) aes.get(x);

            ae.maxState = maxState(ae.fgIndex);
            ae.maxDir = maxState(ae.dirIndex);
            ae.maxSign = maxState(ae.signIndex);
        }
    }
    
    private void computeVar2Factor(int[] nodes)
    {
        for(int x=0, N=nodes.length; x < N; x++)
        {
            int n = nodes[x];
            VariableNode vn = (VariableNode) _nodeMap.get(n);

            List messages = _adjacencyMap.get(n);
            vn.maxProduct(messages);

            ProbTable pt = vn.getProbs();
            
            for(int m=0, M=messages.size(); m < M; m++)
            {
                EdgeMessage em = (EdgeMessage) messages.get(m);
                em.v2f(pt);
            }
        }
    }
    
    private void computeFactor2Var(int[] nodes) throws AlgorithmException
    {
        for(int x=0, N=nodes.length; x < N; x++)
        {
            int n = nodes[x];
            FactorNode fn = (FactorNode) _nodeMap.get(n);

            List messages = _adjacencyMap.get(n);
            for(int m=0, M=messages.size(); m < M; m++)
            {
                EdgeMessage em = (EdgeMessage) messages.get(m);

                em.f2v(fn.maxProduct(messages, m, em.getVariable()));
            }
        }
    }
    
    private void initVar2Factor(int[] nodes)
    {
        for(int x=0, N=nodes.length; x < N; x++)
        {
            int n = nodes[x];
            VariableNode vn = (VariableNode) _nodeMap.get(n);
            ProbTable pt = vn.getProbs();
            
            List messages = _adjacencyMap.get(n);
            for(int m=0, M=messages.size(); m < M; m++)
            {
                EdgeMessage em = (EdgeMessage) messages.get(m);
                em.v2f(pt);
            }
        }
    }

    /**
     * For debugging, print the adjacency list map containing
     * the most recent set of messages passed in the factor graph.
     */
    public String printAdj()
    {
        StringBuffer b = new StringBuffer();
        IntArrayList keys = _adjacencyMap.keys();
        for(int x=0; x < keys.size(); x++)
        {
            int k = keys.get(x);
            b.append(k);
            b.append("  {\n");

            List l = _adjacencyMap.get(k);
            for(int m=0; m < l.size(); m++)
            {
                EdgeMessage em = (EdgeMessage) l.get(m);
                b.append("    v2f ");
                b.append(em.v2f());
                b.append("\n");
                b.append("    f2v ");
                b.append(em.f2v());
                b.append("\n");
            }
            b.append("  }\n");
        }

        return b.toString();
    }

    public String toString()
    {
        return CyUtil.toString(_g);
    }

}
