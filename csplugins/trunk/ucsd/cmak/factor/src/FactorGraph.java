import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;

import giny.model.RootGraph;

import cytoscape.util.GinyFactory;
import cytoscape.data.mRNAMeasurement;

import java.util.List;
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
    private RootGraph _g;
    
    // map node index to a VariableNode or FactorNode object
    private OpenIntObjectHashMap _nodeMap;

    // map path number to path-factor node index
    private OpenIntIntHashMap _pathMap; 

    // Lists of node indicies.
    // Used to create the "NodeType" Cytoscape node attribute file.
    private IntArrayList _edge;
    private IntArrayList _sign;
    private IntArrayList _dir;
    private IntArrayList _ko;
    private IntArrayList _pathActive;

    private IntArrayList _orFactor;
    private IntArrayList _pathFactor;

    // map node index to label used for Cytoscape visualization
    private OpenIntObjectHashMap _nodeLabel;

    // the InteractionGraph that this FactorGraph is based on
    private InteractionGraph _ig;

    // adjaceny list of edge messages
    private IntListMap _adjacencyMap;
    
    /**
     * Create a FactorGraph from an InteractionGraph and the
     * results of finding paths in that InteractionGraph.
     *
     * @param pathResults a DFSPath results object
     * @param ig an interaction graph.  The methods setExpressionData
     * and setEdgeData must have been called on the interaction graph.
     */
    public FactorGraph(InteractionGraph ig, PathResult pathResults)
    {
        _ig = ig;
        _g = GinyFactory.createRootGraph();

        OpenIntObjectHashMap edge2path = pathResults.getEdge2PathMap();

        _nodeMap = new OpenIntObjectHashMap();
        _pathMap = new OpenIntIntHashMap();
        _adjacencyMap = new IntListMap();
        
        // for Cytoscape
        _edge = new IntArrayList();
        _sign = new IntArrayList();
        _dir = new IntArrayList();
        _ko = new IntArrayList();
        _pathActive = new IntArrayList();
        _orFactor = new IntArrayList();
        _pathFactor = new IntArrayList();
        _nodeLabel = new OpenIntObjectHashMap();

        IntArrayList kos = pathResults.getKOs();
        for(int x=0, N = kos.size(); x < N; x++)
        {
            int knockedOut =  kos.get(x);
            OpenIntObjectHashMap target2path = pathResults.getTarget2PathMap(knockedOut);

            //System.out.println("### processing ko: " + knockedOut);
            IntArrayList targets = target2path.keys();
            for(int i=0, Nt = targets.size(); i < Nt; i++)
            {
                int t = targets.get(i);
                //  System.out.println("### processing target: " + t);
                int ko = createKO(knockedOut, t);
                int or = createOR(knockedOut, t);
                
                IntArrayList paths = (IntArrayList) target2path.get(t);
                
                for(int j=0, Np =paths.size(); j < Np; j++)
                {
                    int path = paths.get(j);
                    int pa = createPathActive(path);
                    int pf = createPathFactor(path);
                    //  System.out.println("### processing pa: " + pa + " pf:" + pf);
                    connect(pa, pf);
                    connect(ko, pf);
                    connect(pa, or);
                }
            }
        }
        
        IntArrayList edges = edge2path.keys();
        for(int i=0, Ne = edges.size(); i < Ne; i++)
        {
            int edge = edges.get(i);
            
            int e = createEdge(edge);
            int d = createDir(edge);
            int s = createSign(edge);
            
            List pathIntervals = (List) edge2path.get(edge);
            
            for(int j=0, Np =pathIntervals.size(); j < Np; j++)
            {
                PathResult.Interval paths = (PathResult.Interval) pathIntervals.get(j);
                for(int k=paths.getStart(), m=paths.getEnd(); k < m; k++)
                {
                    int pf = getPathFactorIndex(k);
                    connect(e, pf);
                    connect(d, pf, paths.getDir());
                    connect(s, pf);                
                }
            }
        }
    }

    /**
     * @return the RootGraph index of the PathFactorNode that corresponse to
     * pathNumber or 0 is pathNumber is not a known path.
     */
    private int getPathFactorIndex(int pathNumber)
    {
        return _pathMap.get(pathNumber);
    }

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
     * Create an undirected edge between node1 and node2
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
    private int createEdge(int interactionEdgeIndex)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _edge.add(node);

        VariableNode vn = VariableNode.createEdge();
        _nodeMap.put(node, vn);

        double pval = _ig.getPvalue(interactionEdgeIndex);

        StateSet ss = vn.stateSet();
        double[] prob = new double[ss.size()];
        prob[ss.getIndex(State.ZERO)] = 1;
        prob[ss.getIndex(State.ONE)] = likelihoodRatio(pval);

        vn.initProbs(prob);

        return node;
    }


    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createSign(int interactionEdgeIndex)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _sign.add(node);
        _nodeMap.put(node, VariableNode.createSign());
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createDir(int interactionEdgeIndex)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _dir.add(node);
        _nodeMap.put(node, VariableNode.createDirection());
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createPathActive(int pathNumber)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, String.valueOf(pathNumber));
        _pathActive.add(node);
        _nodeMap.put(node, VariableNode.createPathActive());
        return node;
    }


    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createKO(int koNodeIndex, int targetNodeIndex)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, makeKOLabel(koNodeIndex, targetNodeIndex));

        _ko.add(node);

        VariableNode vn = VariableNode.createKO();
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
        vn.initProbs(prob);

        return node;
    }

    private double likelihoodRatio(double pval)
    {
        return Math.exp(0.5* (ChiSquaredDistribution.inverseCDFMinus1(pval) - lnN));
    }

    private String makeKOLabel(int koNodeIndex, int targetNodeIndex)
    {
        StringBuffer b = new StringBuffer();
        b.append(_ig.node2Name(koNodeIndex));
        b.append(".");
        b.append(_ig.node2Name(targetNodeIndex));
        return b.toString();
    }
    
    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createOR(int koNodeIndex, int targetNodeIndex)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, makeKOLabel(koNodeIndex, targetNodeIndex));
        _orFactor.add(node);
        _nodeMap.put(node, OrFactorNode.getInstance());
        return node;
    }

    /**
     * @return the newly created index of the node in this FactorGraph's
     * RootGraph
     */
    private int createPathFactor(int pathNumber)
    {
        int node = _g.createNode();
        _nodeLabel.put(node, String.valueOf(pathNumber));
        _pathFactor.add(node);
        _nodeMap.put(node, PathFactorNode.getInstance());
        _pathMap.put(pathNumber, node);

        return node;
    }

    public void runMaxProduct()  throws AlgorithmException
    {
        initVar2Factor();
        computeFactor2Var();
        computeVarMaxMarginal();

        for(int x=0; x < 10; x++)
        {
            computeFactor2Var();
            computeVarMaxMarginal();
        }
    }

    private void computeVarMaxMarginal()
    {
        _varLoop(_edge);
        _varLoop(_sign);
        _varLoop(_dir);
        _varLoop(_pathActive);
        _varLoop(_ko);
    }
    
    private void _varLoop(IntArrayList nodes)
    {
        for(int x=0, N=nodes.size(); x < N; x++)
        {
            int n = nodes.get(x);
            VariableNode vn = (VariableNode) _nodeMap.get(n);
            ProbTable pt = vn.getProbs();
            
            List messages = _adjacencyMap.get(n);
            vn.maxProduct(messages);

            for(int m=0, M=messages.size(); m < M; m++)
            {
                EdgeMessage em = (EdgeMessage) messages.get(m);
                em.v2f(pt);
            }
        }
    }
    
    private void computeFactor2Var() throws AlgorithmException
    {
        _factorLoop(_pathFactor);
        _factorLoop(_orFactor);
    }

    private void _factorLoop(IntArrayList nodes) throws AlgorithmException
    {
        for(int x=0, N=nodes.size(); x < N; x++)
        {
            int n = nodes.get(x);
            FactorNode fn = (FactorNode) _nodeMap.get(n);

            List messages = _adjacencyMap.get(n);
            for(int m=0, M=messages.size(); m < M; m++)
            {
                EdgeMessage em = (EdgeMessage) messages.get(m);

                em.f2v(fn.maxProduct(messages, m, em.getVariable()));
            }
        }
    }
    
    private void initVar2Factor()
    {
        _initLoop(_edge);
        _initLoop(_dir);
        _initLoop(_sign);
        _initLoop(_pathActive);
        _initLoop(_ko);
    }
    
    private void _initLoop(IntArrayList nodes)
    {
        for(int x=0, N=nodes.size(); x < N; x++)
        {
            int n = nodes.get(x);
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
    
    /**
     * Write this factor graph as a Cytoscape "sif" file
     */
    public void writeSif(String filename) throws IOException
    {
        CyUtil.writeSif(_g, filename);
    }

    /**
     * Write the node attributes of this factor graph in a Cytoscape
     * readable format.  Create NodeType, InteractionLabel, and NodeProbs
     * attributes
     */
    public void writeNodeAttr(String filename) throws IOException
    {
        PrintStream out = new PrintStream(new FileOutputStream(filename + "_type.noa"));
        writeNodeType(out);
        out.close();
        
        out = new PrintStream(new FileOutputStream(filename + "_label.noa"));
        writeNodeLabels(out);
        out.close();

        out = new PrintStream(new FileOutputStream(filename + "_prob.noa"));
        writeNodeProbs(out);
        out.close();
    }

    /**
     * Write the probabilities of the variable nodes in the factor graph
     */
    public void writeNodeProbs(PrintStream out)
    {
        out.println("NodeProbs (class=java.lang.String)");
        
        IntArrayList nodes = _nodeMap.keys();

        for(int n=0, N =nodes.size(); n < N; n++)
        {
            Object node = _nodeMap.get(nodes.get(n));
            if(node instanceof VariableNode)
            {
                VariableNode vn = (VariableNode) node;

                StringBuffer sb = new StringBuffer();
                sb.append(nodes.get(n));
                sb.append(" = [");
                for(Iterator it = vn.stateSet().iterator(); it.hasNext();)
                {
                    State s = (State) it.next();
                    sb.append(s);
                    sb.append("=");
                    sb.append(vn.prob(s));
                    if(it.hasNext())
                        sb.append(",");
                }
                sb.append("]");
                out.println(sb.toString());
            }
        }       
    }

    /**
     * Write the type of each node in the factor graph
     */
    public void writeNodeType(PrintStream out)
    {
        out.println("NodeType (class=java.lang.String)");
        writeNodes(out, _edge, "edge");
        writeNodes(out, _sign, "sign");
        writeNodes(out, _dir, "dir");
        writeNodes(out, _ko, "ko");
        writeNodes(out, _pathActive, "pathActive");
        writeNodes(out, _orFactor, "OrFactor");
        writeNodes(out, _pathFactor, "pathFactor");
    }

    /**
     * Write a label for each node in the factor graph.
     * Edge, direction and sign nodes are labeled with their
     * corresponding edge in the physical interaction graph.
     * <p>
     * PathActive variable nodes and PathFactor nodes are labeled
     * with a path number.
     * <p>
     * Knockout variable and knockoutOR factor nodes are labeled with
     * the name of the gene that is affected by a knockout.
     */
    public void writeNodeLabels(PrintStream out)
    {
        out.println("InteractionLabel (class=java.lang.String)");
        
        IntArrayList nodes = _nodeLabel.keys();

        for(int n=0, N =nodes.size(); n < N; n++)
        {
            out.println(nodes.get(n) + " = " + _nodeLabel.get(nodes.get(n)));
        }       
    }

    /**
     * Helper method for writeNodeType()
     */
    private void writeNodes(PrintStream out, IntArrayList nodes, String category)
    {
        for(int x=0, N=nodes.size(); x < N; x++)
        {
            out.println(nodes.get(x) + " = " + category);
        }
    }

    public String toString()
    {
        return CyUtil.toString(_g);
    }
}
