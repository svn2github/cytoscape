package fgraph;

import fgraph.util.ObjectIntMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import java.util.logging.Logger;

//import cytoscape.data.CachedExpressionData;
import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;

import cytoscape.util.GinyFactory;

import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntObjectHashMap;

/**
 * A graph of protein-protein and protein-DNA interactions.
 * Also, pvalues or probabilities associated with those edges.
 * Also, knockout expression data.
 */
public class InteractionGraph
{
    private static Logger logger = Logger.getLogger(FactorGraph.class.getName());

    private double PVAL_THRESH = 0.8;

    private int _edgeCount;
    private int _nodeCount;
    
    private RootGraph _graph;
    private OpenIntObjectHashMap _node2name;
    private OpenIntObjectHashMap _edge2type;
    
    private OpenIntIntHashMap _edge2pd; // used to efficiently calculate isProteinDNA

    private static final int UNDIRECTED = 1;
    private static final int DIR_S2T = 2; // edge is directed src -> target
    private static final int DIR_T2S = 3; // edge is directed target -> src

    private List _activeEdges;

    // submodels
    private List _submodels;

    /* map the name of a node in the sif file to an index in the RootGraph
     */
    private ObjectIntMap _name2node; 

    private ExpressionData _expressionData;

    // paths found on this interaction graph using the _expressionData
    private PathResult _paths;
    
    // map edge index to pval (if p-d edge) or probability (if p-p edge).
    private OpenIntDoubleHashMap _edgePvalMap;


    
    InteractionGraph(int nodeCount, int edgeCount)
    {
        _nodeCount = nodeCount;
        _edgeCount = edgeCount;
        
        _graph = GinyFactory.createRootGraph(nodeCount, edgeCount);

        // calling ensureCapacity results in better performance
        _graph.ensureCapacity(nodeCount, edgeCount);
        

        // Map each node index to a node name
        _node2name = new OpenIntObjectHashMap(nodeCount);
        _name2node = new ObjectIntMap(nodeCount);

        _edge2type = new OpenIntObjectHashMap( edgeCount );
        _edge2pd = new OpenIntIntHashMap( edgeCount );

        _edgePvalMap = new OpenIntDoubleHashMap();
    }

    int numEdges()
    {
        return _graph.getEdgeCount();
    }

    int numNodes()
    {
        return _graph.getNodeCount();
    }

    
    OpenIntDoubleHashMap getEdgePvalMap()
    {
        return _edgePvalMap;
    }
    
    int[] createNodes(int num)
    {
        return _graph.createNodes(num);
    }
    
    void addNodeName(int index, String nodeName)
    {
        _node2name.put(index, nodeName);
        _name2node.put(nodeName, index);
    }
    
    public RootGraph getRootGraph()
    {
        return _graph;
    }

    public boolean containsNode(String name)
    {
        return _name2node.containsKey(name);
    }
    
    public int name2Node(String name)
    {
        return _name2node.get(name);
    }

    public String node2Name(int node)
    {
        return (String) _node2name.get(node);
    }


    String edgeName(int edgeIndex)
    {
        int src = _graph.getEdgeSourceIndex(edgeIndex);
        int tgt = _graph.getEdgeTargetIndex(edgeIndex);
        String type = (String) _edge2type.get(edgeIndex);

        return edgeName(src, tgt, type);
    }
    
    String edgeName(int src, int tgt, String type)
    {
        StringBuffer b = new StringBuffer();
        b.append(node2Name(src));
        b.append(" (");
        b.append(type);
        b.append(") ");
        b.append(node2Name(tgt));

        return b.toString();
    }

    
    /**
     * @return a label identifying an edge.  The label is:
     * "SourceName"."TargetName"
     */
    public String edgeLabel(int edgeIndex)
    {
        StringBuffer b = new StringBuffer();
        b.append(node2Name(_graph.getEdgeSourceIndex(edgeIndex)));
        b.append(".");
        b.append(node2Name(_graph.getEdgeTargetIndex(edgeIndex)));

        return b.toString();
    }

    
    /**
     * @return true if edge is a protein-DNA edge, false otherwise
     */
    public boolean isProteinDNA(int edgeIndex)
    {
        if(_edge2pd.containsKey(edgeIndex))
        {
            int val = _edge2pd.get(edgeIndex);

            if(val != UNDIRECTED)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        
        return false;
    }

    public State getFixedDir(int edgeIndex)
    {
        if(_edge2pd.containsKey(edgeIndex))
        {
            int val = _edge2pd.get(edgeIndex);

            if(val == DIR_S2T)
            {
                return State.PLUS;
            }
            else if (val == DIR_T2S)
            {
                return State.MINUS;
            }
        }

        System.err.println("getFixedDir called with non-protein-DNA edge: " +
                           edgeIndex);

        return null;
    }

    public void clearEdgeData()
    {
        _edgePvalMap.clear();
    }
    


    public void setProteinDNAThreshold(double pvalue)
    {
        IntArrayList edges = _edgePvalMap.keys();

        double epsilon = pvalue * 0.001;

        IntArrayList toRemove = new IntArrayList();
        
        for(int x=0, N=edges.size(); x < N; x++)
        {
            int eIndex = edges.get(x);
            if(isProteinDNA(eIndex))
            {
                double p = _edgePvalMap.get(eIndex);

                if(pvalue < p - epsilon)
                {
                    toRemove.add(eIndex);
                }
            }
        }
        toRemove.trimToSize();
        
        logger.info("Removing " + toRemove.size() +
                           " protein-DNA edges. pvalue threshold = " + pvalue);
        _graph.removeEdges(toRemove.elements());
    }
    
    /**
     * @param edgeIndex RootGraph index of an edge
     * @return the p-value of the edge if edgeIndex is a protein-DNA edge, or
     * the probability that that the edge is present if it is a protein-protein
     * edge, or 1 if the no value for the edge is known.
     */
    public double getEdgeValue(int edgeIndex)
    {
        if(_edgePvalMap.containsKey(edgeIndex))
        {
            return _edgePvalMap.get(edgeIndex);
        }
        else
        {
            return 1;
        }
    }

    /**
     * Load expression data from the file
     */ 
    public void loadExpressionData(String filename)

    {
        _expressionData = new ExpressionData(filename);
    }

    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public boolean expressionChanges(int koNode, int targetNode)
    {
        if(_expressionData != null)
        {
            mRNAMeasurement m = _expressionData.getMeasurement(node2Name(targetNode), 
                                                               node2Name(koNode));
            if(m != null)
            {
                return m.getSignificance() < PVAL_THRESH;
            }
        }
        
        return false;
    }

    public String[] getConditionNames()
    {
        return _expressionData.getConditionNames();
    }

    
    /**
     * Set the threshold used by expressionChanges to determine whether
     * a knockout causes a target gene's expression to change.
     */
    public void setExpressionPvalThreshold(double d)
    {
        PVAL_THRESH = d;
    }

    public mRNAMeasurement getExpression(int koNode, int targetNode)
    {
        if(_expressionData != null)
        {
            
            mRNAMeasurement m = _expressionData.getMeasurement(node2Name(targetNode), 
                                                               node2Name(koNode));

            return m;
        }
        
        return null;
    }

    
    public void setPaths(PathResult paths)
    {
        _paths = paths;
    }
    

    /**
     * @param s a List of AnnotatedEdge objects
     */
    public void setActiveEdges(List e)
    {
        _activeEdges = e;
    }

    public void setSubmodels(List models)
    {
        _submodels = models;
    }
    

    int[] edges()
    {
        return _graph.getEdgeIndicesArray();
    }
    
    void createEdges(int[] src, int[] tgt, String[] types, boolean directed)
    {
        int[] edges = _graph.createEdges(src, tgt, directed);
        for(int e=0; e < edges.length; e++)
        {
            _edge2type.put(edges[e], types[e]);

            if( InteractionGraphFactory.isDirected(types[e]))
            {
                // direction of pd edge is implied to be source to target
                // in the sif file
                _edge2pd.put(edges[e], DIR_S2T);
            }
            else
            {
                _edge2pd.put(edges[e], UNDIRECTED);
            }
        }

    }

    /**
     * Print this interaction graph
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        for(Iterator it = _graph.edgesIterator(); it.hasNext(); )
        {
            Edge e = (Edge) it.next();

            Node s = (Node) e.getSource();
            Node t = (Node) e.getTarget();

            int sid = s.getRootGraphIndex();
            int tid = t.getRootGraphIndex();

            buf.append("E=" + e.getIdentifier()
                       + " from=" + _node2name.get(sid) 
                       + " [" + sid + "]"
                       + " to=" + _node2name.get(tid)
                       + " [" + tid + "]"
                       + " type=" + _edge2type.get(e.getRootGraphIndex())
                       + "\n");
            
        }

        if(_expressionData != null)
        {
            
            String[] conds = _expressionData.getConditionNames();
            String[] genes = _expressionData.getGeneNames();
            for(int c=0; c < conds.length; c++)
            {
                String cc = conds[c];
                if(!containsNode(cc))
                {
                    continue;
                }
                
                buf.append("Condition: ");
                buf.append(cc);
                buf.append("\n");
                for(int g=0; g < genes.length; g++)
                {
                    String gg = genes[g];
                    if(!containsNode(gg))
                    {
                        continue;
                    }

                    mRNAMeasurement m = getExpression(name2Node(cc), name2Node(gg));
                    buf.append(" gene: ");
                    buf.append(gg);
                    if(m != null)
                    {
                        buf.append(" lr=");
                        buf.append(m.getRatio());
                        buf.append(", pval=");
                        buf.append(m.getSignificance());
                    }
                    if(expressionChanges(name2Node(cc), name2Node(gg)))
                    {
                        buf.append(" isChanged=T");
                    }
                    buf.append("\n");
                }
            }
        }
        return buf.toString();
    }
    

    /*
     * Methods for writing the graph to files.
     * Should these be in another class?
     *
     * @param filter only write submodels that explain at least this many
     * knockout experiments (NOTE: not knockout effects. need to fix?)
     * @param filename the base filename used to write each submodel.
     * @see #writeSubmodel()
     */ 

    public void writeGraphAsSubmodels(String filename, int filter)
        throws IOException
    {
        logger.info("writing submodels explaining >= " + filter
                    + " ko experiments");

        int cnt = 0;
        for(int x=0; x < _submodels.size(); x++)
        {
            Submodel m = (Submodel) _submodels.get(x);
            if(m.getNumExplainedKO() >= filter)
            {
                writeSubmodel(m, filename, cnt);
                cnt++;
            }
        }

        writeAttributes(filename);
    }

    /**
     *
     * @param m the submodel to write
     * @param modelNum the number used to label this submodel
     * @param filename the base filename.  Submodel "m" will be written
     * to a file named "{filename}-{modelNum}.sif"
     *
     * @throws
     */
    private void writeSubmodel(Submodel m, String filename, int modelNum) throws IOException
    {
        StringBuffer b = new StringBuffer(filename);
        b.append("-");
        b.append(modelNum);
        b.append(".sif");

        logger.info("writing submodel: " + m.getId() + " to " + b.toString());
        
        PrintStream out = new PrintStream(new FileOutputStream(b.toString()));
        writeEdges(out, m.getEdges(), 1);
        out.close();
    }


    private void writeEdges(PrintStream out, List edges, int neighborCutoff)
        throws IOException
    {

        for(int x=0; x < edges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) edges.get(x);

            int e = ae.interactionIndex;
            int src = _graph.getEdgeSourceIndex(e);
            int target = _graph.getEdgeTargetIndex(e);

            /*
            int dirSrc = src;

            if(ae.maxDir == State.MINUS)
            {
                dirSrc = target;
            }
            
            if(_graph.getOutDegree(dirSrc) > neighborCutoff)
            {
            */
            
            String type = " x ";
            if(_edge2type.containsKey(e))
            {
                type = (String) _edge2type.get(e);
            }
            
            StringBuffer b = new StringBuffer();
            b.append(node2Name(src));
            b.append(" ");
            b.append(type);
            b.append(" ");
            b.append(node2Name(target));
            
            out.println(b.toString());
        }
        //}
    }
    
    public void writeGraph(String filename) throws IOException
    {
        PrintStream out = new PrintStream(new FileOutputStream(filename + ".sif"));
        writeEdges(out, _activeEdges, 0);
        out.close();

        writeAttributes(filename);
    }

    private void writeAttributes(String filename) throws IOException
    {
        PrintStream out = new PrintStream(new FileOutputStream(filename + "_dir.eda"));
        writeEdgeDir(out);
        out.close();

        out = new PrintStream(new FileOutputStream(filename + "_sign.eda"));
        writeEdgeSign(out);
        out.close();

        out = new PrintStream(new FileOutputStream(filename + "_model.eda"));
        writeEdgeModel(out);
        out.close();
        
        out = new PrintStream(new FileOutputStream(filename + "_type.noa"));
        writeNodeTypes(out);
        out.close();

        /*
        out = new PrintStream(new FileOutputStream(filename + "_ncount.noa"));
        writeNodeNeighborCount(out);
        out.close();
        */
    }

    
    private void writeEdgeDir(PrintStream out) throws IOException
    {
        out.println("EdgeDirection (class=java.lang.String)");
        for(int x=0; x < _activeEdges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) _activeEdges.get(x);
            StringBuffer b = new StringBuffer(edgeName(ae.interactionIndex));
            b.append(" = ");
            b.append(ae.maxDir);
            
            out.println(b.toString());
        }
        
    }
    
    private void writeEdgeSign(PrintStream out)
    {
        out.println("EdgeSign (class=java.lang.String)");
        for(int x=0; x < _activeEdges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) _activeEdges.get(x);
            StringBuffer b = new StringBuffer(edgeName(ae.interactionIndex));
            b.append(" = ");
            b.append(ae.maxSign);
            
            out.println(b.toString());
        }
    }

    private void writeEdgeModel(PrintStream out)
    {
        out.println("EdgeModel (class=java.lang.String)");
        for(int x=0; x < _activeEdges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) _activeEdges.get(x);
            StringBuffer b = new StringBuffer(edgeName(ae.interactionIndex));
            b.append(" = ");
            b.append(list2String(ae.submodels));
            
            out.println(b.toString());
        }
        
    }

    private String list2String(IntArrayList l)
    {
        StringBuffer b = new StringBuffer();
        for(int x=0; x < l.size(); x++)
        {
            b.append(l.getQuick(x));
            if(x < l.size() - 1)
            {
                b.append(", ");
            }
        }

        return b.toString();
    }
    
    private void writeNodeLabels(PrintStream out)
    {
        out.println("NodeName (class=java.lang.String)");
        
        IntArrayList nodes = _node2name.keys();

        for(int n=0, N =nodes.size(); n < N; n++)
        {
            out.println(nodes.get(n) + " = " + _node2name.get(nodes.get(n)));
        }       
    }

    /*
    private void writeNodeNeighborCount(PrintStream out)
    {
        //out.println("NeighborCount (class=java.lang.Integer)");
        out.println("NeighborCount (class=java.lang.Integer)");

        for(int x=0; x < _activeEdges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) _activeEdges.get(x);
            StringBuffer b = new StringBuffer(edgeName(ae.interactionIndex));
            b.append(" = ");
            b.append(ae.maxDir);
            
            out.println(b.toString());
        }

        
        IntArrayList nodes = _node2name.keys();

        for(int n=0, N =nodes.size(); n < N; n++)
        {
            int node = nodes.get(n);
            out.println(node2Name(node) + " = " + _graph.getOutDegree(node));
        }       
    }
    */
    
    private void writeNodeTypes(PrintStream out)
    {
        out.println("NodeType (class=java.lang.String)");
        
        IntArrayList nodes = _paths.getKOs();

        for(int n=0, N =nodes.size(); n < N; n++)
        {
            out.println(node2Name(nodes.get(n)) + " = KO");
        }       
    }





}
