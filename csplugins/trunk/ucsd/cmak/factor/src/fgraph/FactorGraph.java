package fgraph;

import fgraph.util.IntListMap;
import fgraph.util.IntIntListMap;
import fgraph.util.Target2PathMap;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import giny.model.RootGraph;

import cytoscape.util.GinyFactory;
import cytoscape.data.mRNAMeasurement;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * A bipartite graph of Variable and Factor nodes that represents
 * constraints on paths in a physical interaction network that
 * explain knockout effects.
 *
 * <p>
 * Ref: Kschischang, Frey, Loeliger. IEEE Trans Inf Theory. Feb 2001
 * 
 * 
 *  TODO: Separate Max-Product code from factor graph construction code
 *
 * A factor graph is a collection of variable and factor nodes connected
 * by edges.
 *
 *  - The graph provides methods for accessing the nodes and edges.
 *  - The graph maintains an influence count data structure as well as
 *    methods of updating the influence count of all variables.
 *  - The graph maintains the state of each variable: fixed or not
 * 
 */
public class FactorGraph
{
    private static Logger logger = Logger.getLogger(FactorGraph.class.getName());
    private static boolean INFO = logger.isLoggable(Level.INFO);
    private static boolean FINE = logger.isLoggable(Level.FINE);
    private static boolean FINER = logger.isLoggable(Level.FINER);

    
    // used to compute the a priori probabilities of
    // knockout nodes
    private static double koinv = 1e-20;

    // used to compute likelihood ratios
    private static double NUM_REPLICATES = 3;
    private static double lnN = Math.log(NUM_REPLICATES);

    private int _nextNodeIndex;
    private int _nextEdgeIndex;

    // map node index to a VariableNode or FactorNode object
    protected OpenIntObjectHashMap _nodeMap;

    private OpenIntIntHashMap _pathFactor2sigmaMap;
    private OpenIntIntHashMap _factorInfluenceMap; 

    // Adjacency list representation of the FactorGraph
    protected IntIntListMap _var2fac;
    protected IntIntListMap _fac2var;

    // adjaceny list of edge messages
    // maps a factor graph node index (either a factor node or a
    // variable node) to the List of EdgeMessages
    // that connect that node to its neighbors in the factor graph.
    // Used for MaxProduct algorithm.
    protected IntListMap _adjacencyMap;
    
    protected IntArrayList _vars;
    protected IntArrayList _factors;

    protected IntArrayList _sign;
    protected IntArrayList _edge;
    protected IntArrayList _dir;
    protected IntArrayList _ko;
    protected IntArrayList _pathActive;
    protected IntArrayList _orFactor;
    protected IntArrayList _pathFactor;

    
    // map interaction edge index to AnnotatedEdge
    protected OpenIntObjectHashMap _edgeMap;

    // map edge presence variable index to AnnotatedEdge object
    protected OpenIntObjectHashMap _x2aeMap;

    // map edge direction variable index to AnnotatedEdge object
    protected OpenIntObjectHashMap _d2aeMap;
    
    // map edge sign variable index to AnnotatedEdge object
    protected OpenIntObjectHashMap _s2aeMap;
    
    // the InteractionGraph that this FactorGraph is based on
    protected InteractionGraph _ig;

    // the PathResults that this FactorGraph is based on
    protected PathResult _paths;

    // map path number to path-factor node index
    private OpenIntIntHashMap _pathMap;

    
    /**
     * @return a unique node index in this factor graph
     */
    private int getNextNodeIndex()
    {
        return _nextNodeIndex--;
    }

    /**
     * @return a unique edge index in this factor graph
     */
    private int getNextEdgeIndex()
    {
        return _nextEdgeIndex--;
    }

    /**
     * Protected constructor.  A FactorGraph should always be created using
     * the factory method.
     */
    protected FactorGraph(InteractionGraph ig, PathResult pathResults)
    {
        _ig = ig;
        _paths = pathResults;

        _nextNodeIndex = -1;
        _nextEdgeIndex = -1;

        //_g = _newRootGraph();
        
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
        int nV = 0;
        int nF = 0;
        int nEdges = edges.size();
        int nPaths = pathResults.getPathCount();
        
        nV = kos.size() + nPaths + 3 * nEdges;
        nF = kos.size() + nPaths;
        nN = nV + nF;
        
        // number of edges: 3 edges connect path-factor node to sigma, k, and OR
        // 3 edges connect path-factor to edge, dir, sign variables
        // 
        // lower bound = 3 * pathcount + 3 * 1 * pathCount (1 edge)
        // upper bound = 3 * pathcount + 3 * 5 * pathCount (5 edge)
        //
        // use 4 edges per path = (3 + 12 = 15) * pathcount
        nE = 12 * nPaths;

        logger.info("Initializing factor graph. "
                    + nV + " vars, "
                    + nF + " factors, "
                    + nN + " nodes, "
                    + nE + " estimated edges.");
        
        _nodeMap = new OpenIntObjectHashMap(nN);
        _pathMap = new OpenIntIntHashMap(nPaths);
        _pathFactor2sigmaMap = new OpenIntIntHashMap(nPaths);
        _factorInfluenceMap = new OpenIntIntHashMap(nPaths);

        _edgeMap = new OpenIntObjectHashMap(nEdges);
        _x2aeMap = new OpenIntObjectHashMap(nEdges);
        _d2aeMap = new OpenIntObjectHashMap(nEdges);
        _s2aeMap = new OpenIntObjectHashMap(nEdges);

        _adjacencyMap = new IntListMap(nN);
        
        _factors = new IntArrayList(nF);
        _vars = new IntArrayList(nV);
        
        _var2fac = new IntIntListMap(nV);
        _fac2var = new IntIntListMap(nF);
        
        _edge = new IntArrayList(nEdges);
        _sign = new IntArrayList(nEdges);
        _dir = new IntArrayList(nEdges);
        _ko = new IntArrayList(kos.size());
        _pathActive = new IntArrayList(nPaths);
        
        _orFactor = new IntArrayList(kos.size());
        _pathFactor = new IntArrayList(nPaths);

    }

        
    /*******************************************************
     *
     *  Methods for creating the factor graph
     *
     *******************************************************/


    /**
     * Factory method for creating a FactorGraph from an InteractionGraph
     * and a set of Paths.
     *
     * @param i the interaction graph
     * @param pathResults candidate paths that explain knockout effects
     * @return a FactorGraph
     */
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
                    int pf = createPathFactor(path, pa);

                    cN += 2;
                    
                    connect(pa, pf);
                    connect(ko, pf);
                    connect(pa, or);

                    cE += 3;
                }
            }
        }
        
        logger.info("processed ko, OR, path nodes. cN=" + cN + ", cE=" + cE);
        
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

                    // update the number of undertermined vars connected
                    // to the factor node.
                    // 
                    // protein-DNA edges have a fixed direction so only the
                    // edge presence and sign vars count.
                    int curInfluenceCnt = _factorInfluenceMap.get(pf);
                    if(_ig.isProteinDNA(edge))
                    {
                        _factorInfluenceMap.put(pf, curInfluenceCnt + 2);
                    }
                    else
                    {
                        _factorInfluenceMap.put(pf, curInfluenceCnt + 3);
                    }

                }
            }
        }

        logger.info("processed edges. total cN=" + cN + ", cE=" + cE);

        //printInfluenceCnt();
        
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
        EdgeMessage em;
        if(dir != null)
        {
            em = new EdgeMessage(getVarNode(varNode).type(),
                                 varNode, facNode, dir);
        }
        else
        {
            em = new EdgeMessage(getVarNode(varNode).type(),
                                 varNode, facNode);
        }
        
        _adjacencyMap.add(varNode, em);
        _adjacencyMap.add(facNode, em);

        _var2fac.add(varNode, facNode);
        _fac2var.add(facNode, varNode);
        
        return getNextEdgeIndex();
        //return _g.createEdge(varNode, facNode, false);
    }
    
    /**
     * Create an undirected edge between a variable node and factor node
     * @return the RootGraph index of the edge
     */
    private int connect(int varNode, int facNode)
    {
        return connect(varNode, facNode, null);
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
        _x2aeMap.put(ae.fgIndex, ae);
        _d2aeMap.put(ae.dirIndex, ae);
        _s2aeMap.put(ae.signIndex, ae);

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
        //int node = _g.createNode();
        int node = getNextNodeIndex();

        _vars.add(node);
        _edge.add(node);
        
        VariableNode vn = VariableNode.createEdge(interactionEdgeIndex);
        _nodeMap.put(node, vn);

        StateSet ss = vn.stateSet();
        double[] prob = new double[ss.size()];

        double val = _ig.getEdgeValue(interactionEdgeIndex);

        // for yeang data output from generate_subnetworks.c
        // all edge attributes are already likelihood ratios
        //prob[ss.getIndex(State.ZERO)] = 1;
        //prob[ss.getIndex(State.ONE)] = val;
        
        // for Ideker lab data
        // protein-DNA edges are reported as pvalues
        if(_ig.isProteinDNA(interactionEdgeIndex))
        {
            prob[ss.getIndex(State.ZERO)] = 1;
            prob[ss.getIndex(State.ONE)] = likelihoodRatio(val);
        }
        // protein-protein edges are probabilities
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
        int node = getNextNodeIndex();
        _sign.add(node);
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
        int node = getNextNodeIndex();

        _vars.add(node);
        _dir.add(node);
        
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
        int node = getNextNodeIndex();

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
        int node = getNextNodeIndex();

        _ko.add(node);
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
        int node = getNextNodeIndex();
        _orFactor.add(node);
        _factors.add(node);
        _nodeMap.put(node, OrFactorNode.getInstance());
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    protected int createPathFactor(int pathNumber, int sigmaNode)
    {
        int node = getNextNodeIndex();

        _pathFactor.add(node);
        _factors.add(node);
        _nodeMap.put(node, CachedPathFactorNode.getInstance());
        _pathMap.put(pathNumber, node);
        _pathFactor2sigmaMap.put(node, sigmaNode);
        _factorInfluenceMap.put(node, 0);

        return node;
    }


    
    /*******************************************************
     *
     *  Methods for accessing the variables and factor nodes
     *  of the graph.
     *
     *******************************************************/

    
    InteractionGraph getInteractionGraph()
    {
        return _ig;
    }

    /*
     * @return the RootGraph index of the PathFactorNode that corresponds to
     * pathNumber or 0 if pathNumber is not a known path.
     */
    private int getPathFactorIndex(int pathNumber)
    {
        return _pathMap.get(pathNumber);
    }

    
    int[] getActivePaths()
    {
        IntArrayList l = new IntArrayList();
        
        for(int s=0, N=_pathActive.size(); s < N; s++)
        {
            VariableNode vn = getVarNode(_pathActive.get(s));
            ProbTable pt = vn.getProbs();

            if(pt.hasUniqueMax() && (pt.maxState() == State.ONE))
            {
                l.add(vn.getId());
            }
        }
        l.trimToSize();
        
        return l.elements();
    }

    PathResult getPaths()
    {
        return _paths;
    }

    IntArrayList getVars()
    {
        return _vars;
    }

    IntArrayList getFactors()
    {
        return _factors;
    }
    
    IntArrayList getSigns()
    {
        return _sign;
    }

    IntArrayList getEdges()
    {
        return _edge;
    }
    
    IntArrayList getDirs()
    {
        return _dir;
    }
    
    IntArrayList getKos()
    {
        return _ko;
    }
    
    IntArrayList getPathActives()
    {
        return _pathActive;
    }

    IntArrayList getOrFactors()
    {
        return _orFactor;
    }
    
    IntArrayList getPathFactor()
    {
        return _pathFactor;
    }


    NodeType _getNodeType(int node)
    {
        FGNode o = (FGNode) _nodeMap.get(node);
        return o.type();
    }


    VariableNode getVarNode(int varIndex)
    {
        VariableNode vn = (VariableNode) _nodeMap.get(varIndex);

        return vn;
    }

    FactorNode getFactorNode(int facIndex)
    {
        FactorNode vn = (FactorNode) _nodeMap.get(facIndex);

        return vn;
    }

    VariableNode getSigmaForFactor(int facIndex)
    {
        if(_pathFactor2sigmaMap.containsKey(facIndex))
        {
            int s = _pathFactor2sigmaMap.get(facIndex);

            return getVarNode(s);
        }

        return null;
    }

    IntArrayList getAdjacentVars(int facnode)
    {
        return _fac2var.get(facnode);
    }

    IntArrayList getAdjacentFacs(int varnode)
    {
        return _var2fac.get(varnode);
    }

    List getAdjacentMessages(int node)
    {
        return _adjacencyMap.get(node);
    }
    
    /**
     *
     *
     * @param node index of a node in the factor graph
     * @return if node is an edge presence, sign or direction variable, then
     * return the index of the edge in the interaction graph that corresponds
     * to the variable. Return 0 otherwise.
     */
    int nodeIndex2InteractionIndex(int node)
    {
        FGNode fgn = (FGNode) _nodeMap.get(node);

        if(fgn.isType(NodeType.EDGE))
        {
            return ((AnnotatedEdge) _x2aeMap.get(node)).interactionIndex;
        }
        else if(fgn.isType(NodeType.SIGN))
        {
            return ((AnnotatedEdge)_s2aeMap.get(node)).interactionIndex;
        }
        else if(fgn.isType(NodeType.DIR))
        {
            return ((AnnotatedEdge)_d2aeMap.get(node)).interactionIndex;
        }

        return 0;
    }

    

    /*******************************************************
     *
     *  Methods for querying the state of the factor graph
     *
     *******************************************************/

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


    private boolean isPathFixed(int facNode)
    {
        int cnt = _factorInfluenceMap.get(facNode);

        return (cnt == 0);
    }

    private boolean isPathActive(int facNode)
    {
        VariableNode sigma =  getSigmaForFactor(facNode);
        return (sigma.getProbs().maxState() == State.ONE);
    }

    
    /**
     * Check if a knockout is explained by at least one path.
     *
     * @param koNode the knockout node
     * @return true if at least one explanatory path for koNode is
     * active and all of the edges, signs and dirs along the path
     * are fixed.
     */
    boolean isExplained(int koNode)
    {
        IntArrayList facs = _var2fac.get(koNode);

        //logger.finer("   isExplained called on: " + koNode);
        
        // check all paths that could explain this knockout
        for(int x=0, Nf=facs.size(); x < Nf; x++)
        {
            int facNode = facs.get(x);
            FactorNode fn = getFactorNode(facNode);

            if(fn.isType(NodeType.PATH_FACTOR))
            {
                if(FINER)
                {
                    logger.finer("   checking PATH_FACTOR: " + facNode
                                + " isFixed=" + isPathFixed(facNode)
                                + " isActive=" + isPathActive(facNode));
                }
                
                if(isPathFixed(facNode) && isPathActive(facNode))
                {
                    return true;
                }
            }
        }

        return false;
    }


        
    /*******************************************************
     *
     *  Methods for accessing influence count information
     *
     *******************************************************/

        
    /**
     * @param nodes the list of variable nodes
     * @return the index of the node in "nodes" that is
     * not yet fixed, and is separated by 1 factor node
     * to the most undetermined variables.
     * <p>
     * The index will be less than zero unless all of the
     * nodes are already fixed.  If multiple non-fixed nodes
     * are connected to the same number of undetermined
     * variables, then return the one that appears last
     * in the list.
     */
    int chooseMaxConnode(IntArrayList nodes)
    {
        int maxInfluence = 0;
        int maxVar = 0;

        int i = 0;
        for(int x=0; x < nodes.size(); x++)
        {
            i = 0;
            int v = nodes.get(x);

            if(!getVarNode(v).isFixed())
            {
                IntArrayList facs = _var2fac.get(v);
                
                for(int f=0; f < facs.size(); f++)
                {
                    i += _factorInfluenceMap.get(facs.get(f));
                }
                
                if( i >= maxInfluence)
                {
                    maxInfluence = i;
                    maxVar = v;
                }
            }
        }

        if(FINE)
        {
            logger.fine("max influence = " + maxInfluence
                        + " for node " + maxVar);
        }
        
        return maxVar;
    }

    
    /**
     * Record dependencies that occur as a result of fixing
     * the independent variable
     * 1. find factor nodes that contain the indepVar and the depVar
     * 2. keep factor node only if all of the vars connected to it are
     *    either invariant, previously manually fixed, or newly determined.
     * 3. update the dependency graph.  
     *    The dependent node depends on each variable connected to the
     *    factor node.
     *
     * <p>
     * ASSUMPTION: all invariant, independent, and dependent vars have
     * been marked as being fixed before addInferredVars is called.
     *    
     * @param indepVar the degerate variable that was manually fixed.
     *                 Called the independent variable.
     * @param depVar the dependent variable
     * @param node a variable that was uniquely determined by the last
     *             run of the max product algorith,
     */
    void addInferredVars(Submodel model,
                         int indepVar, int depVar)
    {
        /*
        logger.finer("addInferredVars: indep-var:" + indepVar
                     + ", dep-var:" + depVar);
        */
        
        /**
         * 1. Identify path-factor nodes that are connected to
         *    depVar (ie. paths that contain depVar)
         *
         * 2. If the factor is connected to indepVar.
         *    (ie. path also contains indepVar).
         *
         * 3. If the path is invariant and none of its edges are in
         *    another model, add all of the signs, dirs, and KO var
         *    for the path to the model.
         */

        IntArrayList facs = _var2fac.get(depVar);
        for(int x=0, Nf=facs.size(); x < Nf; x++)
        {
            int fac = facs.get(x);

            IntArrayList varsToAdd = getInfluencingVars(model, fac,
                                                        indepVar, depVar);

            for(int y=0; y < varsToAdd.size(); y++)
            {
                int v = varsToAdd.getQuick(y);

                if(FINE)
                {
                    logger.fine("adding var: " + v
                                + " from path " + fac
                                + " to model "
                                + model.getId());
                }
                
                model.addVar(v);
            }
        }
    }

    /**
     * Check if the path constrained by facNode:
     *   1) contains indepVar
     *   2) all x,d,s,ko, vars are fixed
     *
     *
     * Return all d,s, ko vars except indepVar and depVar.
     * 
     * @param
     * @return the indexes of all d,s, ko vars excluding indepVar, depVar,
     *         and any var that is already in the model.
     */
    private IntArrayList getInfluencingVars(Submodel model, int facNode,
                                            int indepVar, int depVar)
    {
        IntArrayList vars = _fac2var.get(facNode);

        IntArrayList newVars = new IntArrayList(vars.size());
        boolean containsIV = false;
        boolean containsDV = false;
        boolean isFixed = isPathFixed(facNode);
        boolean isActive = isPathActive(facNode);

        if(FINE)
        {
            logger.fine("getInfluencingVars for: " + facNode
                        + " isFixed=" + isFixed
                        + " isActive=" + isActive
                        + " #vars=" + vars.size()
                        + " IV=" + indepVar
                        + " DV=" + depVar);
        }

        if(isFixed && isActive)
        {
            for(int x=0, N=vars.size(); x < N; x++)
            {
                int v = vars.get(x);
                VariableNode vn = getVarNode(v);
                
                if(v == indepVar)
                {
                    containsIV = true;
                }
                else if (v == depVar)
                {
                    containsDV = true;
                }
                else if(model.acceptsType(vn.type()) &&
                        !model.containsVar(v))
                {
                    newVars.add(v);
                }
                
            }
        }
        
        if (!containsIV || !containsDV)
        {
            if(FINE)
            {
                logger.fine("  contains Indep Var=" + containsIV
                            + "contains Dep Var=" + containsDV);
            }
            newVars.clear();
        }

        return newVars;
        
    }



    /**
     * Decrement the influence count of "varNode" by 1.
     * The influence count of a variable is the number of
     * non-fixed node that are 1 factor-node away from
     * "varNode"
     *
     * @param varNode the variable node
     */
    private void decrementInfluenceCount(int varNode)
    {
        if(FINER)
        {
            logger.finer("decrementing influence of: " + varNode);
        }
        
        IntArrayList facs = _var2fac.get(varNode);

        for(int x=0, N=facs.size(); x < N; x++)
        {
            int f = facs.get(x);
            if(_factorInfluenceMap.containsKey(f))
            {
                int cnt = _factorInfluenceMap.get(f);

                _factorInfluenceMap.put(f, cnt - 1);
            }
        }
    }

    
    /*******************************************************
     *
     *  Methods for maintaining the state of the factor
     *  graph's variable nodes
     *
     *******************************************************/
    
    /**
     * Fix "var" to a specific state.  Choose the state randomly
     *
     * @param var the root graph index of the variable to fix
     */
    void fixVar(int var)
    {
        if(_nodeMap.containsKey(var))
        {
            VariableNode vn = getVarNode(var);
            
            if(vn.isFixed())
            {
                return;
            }
            
            ProbTable pt = vn.getProbs();
            if(pt.hasUniqueMax())
            {
                if(vn.isType(NodeType.EDGE) ||
                   vn.isType(NodeType.DIR) ||
                   vn.isType(NodeType.SIGN))
                {
                    fixEdgeSignDir(vn, pt.maxState(), var);
                }
                else
                {
                    vn.fixState(pt.maxState());
                }
            }
            else
            {
                if(vn.isType(NodeType.PATH_ACTIVE))
                {
                    vn.fixState(State.ZERO);
                    if(INFO)
                    {
                        logger.info("fixing path active for path "
                                    + vn.getId()
                                    + " to " + vn.fixedState());
                    }
                }
                else if (vn.isType(NodeType.EDGE))
                {
                    fixEdgeSignDir(vn, State.ZERO, var);

                    if(INFO)
                    {
                        logger.info("fixing edge value for "
                                           + _ig.edgeName(vn.getId())
                                           + " to " + vn.fixedState());
                    }
                }
                else if (vn.isType(NodeType.SIGN))
                {
                    fixEdgeSignDir(vn, State.PLUS, var);

                    if(INFO)
                    {
                        logger.info("fixing sign for "
                                    + _ig.edgeName(vn.getId())
                                    + " to " + vn.fixedState());
                    }
                }
                else if (vn.isType(NodeType.DIR))
                {
                    fixEdgeSignDir(vn, State.PLUS, var);
                    if(INFO)
                    {
                        logger.info("fixing dir for "
                                    + _ig.edgeName(vn.getId())
                                    + " to " + vn.fixedState());
                    }
                }
                else if(vn.isType(NodeType.KO))
                {
                    if(INFO)
                    {
                        logger.info("fixing ko for "
                                    + _ig.node2Name(vn.getId())
                                    + " - "
                                    + _ig.node2Name(vn.getId2()));
                    }
                    
                    // if P(+1) == P(-1) -> choose +1
                    if(Math.abs(pt.prob(State.PLUS) - pt.prob(State.MINUS)) > 1e-20)
                    {
                        vn.fixState(State.PLUS);
                    }
                    // if P(0) == P(-1) -> choose -1
                    else if (Math.abs(pt.prob(State.ZERO) - pt.prob(State.MINUS)) > 1e-20)
                    {
                        vn.fixState(State.MINUS);
                    }
                    // last case: P(0) == P(+1) -> choose +1
                    else
                    {
                        vn.fixState(State.PLUS);
                    }
                }
                else
                {
                    // pick a state at random
                    StateSet ss = vn.stateSet();
                    Iterator it = ss.iterator();
                    if(it.hasNext())
                    {
                        vn.fixState((State) it.next());
                    }
                }
                
            }
        }
    }
    
    void fixEdgeSignDir(VariableNode vn, State maxState, int nodeIndex)
    {
        vn.fixState(maxState);
        decrementInfluenceCount(nodeIndex);
    }

    
    /*******************************************************
     *
     *  Methods for annotating physical interaction edges
     *  using the state of the factor graph's variables.
     *
     *******************************************************/


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
        _ig.setActiveEdges(updateEdgeAnnotation());
    }


    
    /**
     * Examine the factor graph variables and add physical
     * interactions edges to each model.
     * <p>
     * For each sign variable in a model, add the corresponding physical
     * edge to the model if the edge is on an active path.
     *
     * @param models a list of Submodels
     */    
    void annotateSubmodelEdges(List models)
    {
        for(int x=0; x < models.size(); x++)
        {
            boolean isDistinct = true;
            Submodel m = (Submodel) models.get(x);

            IntArrayList vars = m.getVars();

            for(int y=0; y < vars.size(); y++)
            {
                int v = vars.get(y);
                if(getVarNode(v).isType(NodeType.SIGN))
                {
                    AnnotatedEdge ae = (AnnotatedEdge)_s2aeMap.get(v);
                    if(ae.active)
                    {
                        m.addEdge(ae);
                        ae.addSubmodel(m.getId());
                    }
                }
            }
        }
    }

    
    /**
     * Set the max state, direction, and sign for each edge
     *
     */
    List updateEdgeAnnotation()
    {
        int[] activePaths = getActivePaths();

        ObjectArrayList aes = _edgeMap.values();

        List activeEdges = new ArrayList();
        
        for(int x=0; x < aes.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) aes.get(x);

            ae.maxState = maxState(ae.fgIndex);
            ae.maxDir = maxState(ae.dirIndex);
            ae.maxSign = maxState(ae.signIndex);

            /*
            if(ae.maxState != null &&
               ae.maxDir != null &&
               ae.maxSign != null)
            {
                //                System.out.println("setting invariant to true for: " + ae.interactionIndex);
                ae.invariant = true;
            }
            */

            int[] paths = _paths.pathsThroughEdge(ae.interactionIndex,
                                                  activePaths);
            if((ae.maxState == State.ONE)
               && (paths.length > 0)
               && ae.maxSign != null)
            {
                ae.active = true;
                ae.paths = paths;
                activeEdges.add(ae);
            }
        }

        logger.info("updateEdgeAnnotation with "
                    + activePaths.length + " active paths resulted in "
                    + activeEdges.size() + " active edges");
        
        return activeEdges;
    }


    /*******************************************************
     *
     *  Printing methods
     *
     *******************************************************/
    
    /**
     * For debugging, print the adjacency list map containing
     * the most recent set of messages passed in the factor graph.
     */
    public String printAdj()
    {
        return printAdjOfType(null);
    }

    /**
     * @param type only print nodes of type "type",
     * or print messages for all nodes if type is null.
     */
    public String printAdjOfType(NodeType type)
    {
        StringBuffer b = new StringBuffer();
        IntArrayList keys = _adjacencyMap.keys();
        for(int x=0; x < keys.size(); x++)
        {
            int node = keys.get(x);
            
            FGNode fgn = (FGNode) _nodeMap.get(node);
            
            // continue only if the node is the right type
            if((type == null) || !fgn.isType(type))
            {
                b.append(printNodeMessages(node, fgn));
            }
        }
        return b.toString();
    }

    

    /**
     * Print the most recently passed messages incoming and outgoing
     * of a specific node.
     *
     * @param node the node to print

     * @return the messages in text format.
     */
    public String printNodeMessages(int node, FGNode o)
    {
        boolean isVar = false;
        StringBuffer b = new StringBuffer();

        b.append(node);
        b.append(" ");
        b.append(o.type());
        b.append(" ");
        
        if(! (o.isType(NodeType.OR_FACTOR) || o.isType(NodeType.PATH_FACTOR)))
        {
            isVar = true;
        }

        b.append(" { \n");
        
        
        List l = _adjacencyMap.get(node);
        if(isVar)
        {
            for(int m=0; m < l.size(); m++)
            {
                EdgeMessage em = (EdgeMessage) l.get(m);
                
                if(m==0)
                {
                    b.append("    v2f (");
                    b.append(em.getVariableIndex());
                    b.append(" ");
                    b.append(em.getFactorIndex());
                    b.append(") ");
                    b.append(em.v2f());
                    b.append("\n");
                }
                
                b.append("    f2v (");
                b.append(em.getFactorIndex());
                b.append(" ");
                b.append(em.getVariableIndex());
                b.append(") f=");
                b.append(_getNodeType(em.getFactorIndex()));
                b.append(" ");
                b.append(em.f2v());
                b.append("\n");
            }
	    }
        else
        {
            for(int m=0; m < l.size(); m++)
            {
                EdgeMessage em = (EdgeMessage) l.get(m);
                
                
                b.append("    f2v (");
                b.append(em.getFactorIndex());
                b.append(" ");
                b.append(em.getVariableIndex());
                b.append(") " + m + " t");
                b.append(type2String(_getNodeType(em.getVariableIndex())));
                b.append(" ");
                b.append(em.f2v());
                b.append("\n");
            }
            
            for(int m=0; m < l.size(); m++)
            {
                EdgeMessage em = (EdgeMessage) l.get(m);
                
                NodeType t = _getNodeType(em.getVariableIndex());
                
                b.append("    v2f (");
                b.append(em.getVariableIndex());
                b.append(" ");
                b.append(em.getFactorIndex());
                b.append(") " + m + " ");
                b.append(type2String(t));
                b.append(" ");
                b.append(em.v2f());
                if(t == NodeType.DIR)
                {
                    b.append(" ");
                    b.append(em.getDir());
                }
                b.append("\n");
            }
        }
        b.append("  }\n");
        
        return b.toString();
    }

    private String type2String(NodeType t)
    {
        if(t == NodeType.EDGE)
        {
            return "x";
        }
        else if(t == NodeType.DIR)
        {
            return "d";
        }
        else if(t == NodeType.SIGN)
        {
            return "s";
        }
        else if(t == NodeType.PATH_ACTIVE)
        {
            return "p";
        }
        else if(t == NodeType.KO)
        {
            return "k";
        }
        return "?";
    }
    
    public String toString()
    {
        return "";
        //return CyUtil.toString(_g);
    }

    public String toString(Submodel model)
    {
        IntArrayList vars = model.getVars();
        StringBuffer b = new StringBuffer();

        b.append(model.getId());
        b.append(" isInvariant=");
        b.append(model.isInvariant());

        int iv = model.getIndependentVar();
        if(!model.isInvariant())
        {
            b.append(" indepVar=");
            b.append(iv );
            b.append("   ");
            b.append(getVarNode(iv));
        }

        b.append("\n");

        b.append("### variables\n");
        for(int x=0; x < vars.size(); x++)
        {
            int v = vars.get(x);

            if(v != iv)
            {
                b.append("   ");
                b.append(getVarNode(v));
                b.append("\n");
            }
        }

        b.append("### edges\n");
        List edges = model.getEdges();
        for(int x=0; x < edges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) edges.get(x);

            b.append("   ");
            b.append(ae.interactionIndex);
            b.append(" s=");
            b.append(ae.maxSign);
            b.append(" d=");
            b.append(ae.maxDir);
            b.append("\n");
        }

        
        return b.toString();
    }


    private void printInfluenceCnt()
    {
        IntArrayList facs = _factorInfluenceMap.keys();
        for(int x=0; x < facs.size(); x++)
        {
            logger.info("ic[" + facs.get(x) + "] = "
                        + _factorInfluenceMap.get(facs.get(x)));
        }
    }
}
