package fgraph;

import fgraph.util.ObjectIntMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.FileOutputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.util.logging.Logger;

import cytoscape.data.mRNAMeasurement;
import cytoscape.util.GinyFactory;

//import fing.model.FingRootGraphFactory;

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

    private double PVAL_THRESH = 1;

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

    private ExpressionDataIF _expressionData;

    // paths found on this interaction graph using the _expressionData
    private PathResult _paths;
    
    // map edge index to pval (if p-d edge) or probability (if p-p edge).
    private OpenIntDoubleHashMap _edgePvalMap;

    InteractionGraph(int nodeCount, int edgeCount)
    {
        _nodeCount = nodeCount;
        _edgeCount = edgeCount;
        
        _graph = GinyFactory.createRootGraph(nodeCount, edgeCount);
        //_graph = FingRootGraphFactory.instantiateRootGraph();
        
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

    /**
     *
     * @param
     * @return an array of the indices of edges that go from source
     *         to target (includes both directed and undirected edges).
     */
    public int[] getEdgeIndicesArray(int source, int target)
    {
        return _graph.getEdgeIndicesArray(source, target, true);
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

    /**
     * @param edgeIndex the root graph index of an edge in the
     * interaction graph.
     * @return a String of the form: source-node (edge-type) target-node
     */
    String edgeName(int edgeIndex)
    {
        int src = _graph.getEdgeSourceIndex(edgeIndex);
        int tgt = _graph.getEdgeTargetIndex(edgeIndex);
        String type = (String) _edge2type.get(edgeIndex);

        return edgeName(src, tgt, type);
    }
    
    /**
     * Return the edge name for an annotated edge.
     * The order of the nodes will be consistent with the
     * EdgeDirection. ie. If the maxDir of the annotated edge
     * is PLUS then the edge name will be:
     * source-node (type) target-node.
     * <p>
     * If the maxDir is MINUS then the edge name will be:
     * target-node (type) source-node
     * 
     * @param ae an edge
     * @return a String that uniquely identifies the input edge
     */
    String edgeName(AnnotatedEdge ae)
    {
        int edgeIndex = ae.interactionIndex;
        int src = 0;
        int tgt = 0;
        
        if(ae.maxDir == State.MINUS)
        {
            src = _graph.getEdgeTargetIndex(edgeIndex);
            tgt = _graph.getEdgeSourceIndex(edgeIndex);
        }
        else
        {
            src = _graph.getEdgeSourceIndex(edgeIndex);
            tgt = _graph.getEdgeTargetIndex(edgeIndex);
        }

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

    public void setExpressionData(ExpressionDataIF data)
    {
        _expressionData = data;
    }
    
    /**
     * @return true if knocking out "koNode" causes the expression of
     * "targetNode" to change.  Use PVAL_THRESH as a cutoff.
     */
    public boolean expressionChanges(int koNode, int targetNode)
    {
        if(_expressionData != null)
        {
            return _expressionData.isPvalueBelowThreshold(node2Name(koNode),
                                                          node2Name(targetNode));
        }

        return false;
    }


    /**
     */
    public double getExprPval(int koNode, int targetNode)
    {
        if(_expressionData != null)
        {
            return _expressionData.getPvalue(node2Name(koNode),
                                             node2Name(targetNode));
        }

        return 1;
    }

    public mRNAMeasurement getExpression(int koNode, int targetNode)
    {
        if(_expressionData != null)
        {
            return _expressionData.getExpression(node2Name(koNode),
                                                 node2Name(targetNode));
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

    /**
     * Create edges in this interaction graph
     */
    void createEdges(int[] src, int[] tgt, String[] types, boolean directed)
    {
        // create the edges in the RootGraph
        int[] edges = _graph.createEdges(src, tgt, directed);

        // update internal data structures
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

        /*
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
        */
        
        return buf.toString();
    }
    

    /*
     * Methods for writing the graph to files.
     * Should these be in another class?
     *
     * @param filter only write submodels that explain at least this many
     * knockout experiments (NOTE: not knockout effects. need to fix?)
     * @param filename the base filename used to write each submodel.
     *
     * @return a List of File objects.  Each File corresponds to a submodel.
     * 
     * @see #writeSubmodel()
     */ 

    public SubmodelOutputFiles writeGraphAsSubmodels(String filename, int filter)
        throws IOException
    {
        logger.info("writing submodels explaining >= " + filter
                    + " ko experiments");

        SubmodelOutputFiles output = new SubmodelOutputFiles();
        Set edges = new HashSet();
        int cnt = 0;
        for(int x=0; x < _submodels.size(); x++)
        {
            Submodel m = (Submodel) _submodels.get(x);
            if(m.getNumExplainedKO() >= filter)
            {
                edges.addAll(m.getEdges());
                File f = writeSubmodel(m, filename, cnt);
                output.addModel(f);
                cnt++;
            }
        }

        writeAttributes(edges, filename, output);

        return output;
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
    private File writeSubmodel(Submodel m, String filename, int modelNum)
        throws IOException
    {
        StringBuffer b = new StringBuffer(filename);
        b.append("-");
        b.append(modelNum);
        b.append(".sif");

        File file = new File(b.toString());
        
        logger.info("writing submodel: " + m.getId() + " to " + b.toString());
        
        PrintStream out = new PrintStream(new FileOutputStream(file));
        writeEdges(out, m.getEdges(), 1);
        out.close();

        return file;
    }


    private void writeEdges(PrintStream out, List edges, int neighborCutoff)
        throws IOException
    {

        for(int x=0; x < edges.size(); x++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) edges.get(x);

            int e = ae.interactionIndex;

            int src;
            int target;
            
            if(ae.maxDir == State.PLUS)
            {
                src = _graph.getEdgeSourceIndex(e);
                target = _graph.getEdgeTargetIndex(e);
            }
            else if (ae.maxDir == State.MINUS)
            {
                src = _graph.getEdgeTargetIndex(e);
                target = _graph.getEdgeSourceIndex(e);
            }
            else
            {
                throw new IOException("Unexpected edge direction state: "
                                      + ae.maxDir);
            }
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
        SubmodelOutputFiles output = new SubmodelOutputFiles();
        File model = new File(filename + ".sif");
        PrintStream out = new PrintStream(new FileOutputStream(model));
        writeEdges(out, _activeEdges, 0);
        out.close();
        output.addModel(model);

        Set edges = new HashSet(_activeEdges);
        
        writeAttributes(edges, filename, output);
    }

    /**
     *
     *
     * @param edges The edges to write attributes for.
     * A Set of AnnotatedEdge objects.
     * @param filename the base filename to use.  This method will append the
     * appropriate extensions.  Edge directions will be written to a file
     * filename_dir.eda.  Edge Signs will be written to: filename_sign.eda.
     * Node types will be written to: filename_type.noa.
     * @param output data structure to keep track of the Files that
     * are created.
     */
    private void writeAttributes(Set edges, String filename,
                                 SubmodelOutputFiles output)
        throws IOException
    {
        File f = new File(filename + "_dir.eda");
        PrintStream out = new PrintStream(new FileOutputStream(f));
        writeEdgeDir(edges, out);
        out.close();
        output.setEdgeDir(f);

        File f2 = new File(filename + "_sign.eda");
        out = new PrintStream(new FileOutputStream(f2));
        writeEdgeSign(edges, out);
        out.close();
        output.setEdgeSign(f2);

        File f3 = new File(filename + "_path.eda");
        out = new PrintStream(new FileOutputStream(f3));
        writeEdgePath(edges, out);
        out.close();
        output.setEdgePath(f3);

        File f4 = new File(filename + "_model.eda");
        out = new PrintStream(new FileOutputStream(f4));
        writeEdgeModel(edges, out);
        out.close();
        output.setEdgeModel(f4);

        
        File f5 = new File(filename + "_type.noa");
        out = new PrintStream(new FileOutputStream(f5));
        writeNodeTypes(out);
        out.close();
        output.setNodeType(f5);

        /*
        out = new PrintStream(new FileOutputStream(filename + "_ncount.noa"));
        writeNodeNeighborCount(out);
        out.close();
        */
    }

    
    private void writeEdgeDir(Set edges, PrintStream out) throws IOException
    {
        out.println("EdgeDirection (class=java.lang.String)");
        for(Iterator it = edges.iterator(); it.hasNext(); )
        {
            AnnotatedEdge ae = (AnnotatedEdge) it.next();
            StringBuffer b = new StringBuffer(edgeName(ae));
            b.append(" = ");
            b.append(ae.maxDir);
            
            out.println(b.toString());
        }
        
    }
    
    private void writeEdgeSign(Set edges, PrintStream out)
    {
        out.println("EdgeSign (class=java.lang.String)");
        for(Iterator it = edges.iterator(); it.hasNext(); )
        {
            AnnotatedEdge ae = (AnnotatedEdge) it.next();
            StringBuffer b = new StringBuffer(edgeName(ae));
            b.append(" = ");
            b.append(ae.maxSign);
            
            out.println(b.toString());
        }
    }

    private void writeEdgeModel(Set edges, PrintStream out)
    {
        out.println("EdgeModel (class=java.lang.String)");
        for(Iterator it = edges.iterator(); it.hasNext(); )
        {
            AnnotatedEdge ae = (AnnotatedEdge) it.next();
            StringBuffer b = new StringBuffer(edgeName(ae));
            b.append(" = ");
            b.append(list2String(ae.submodels));
            
            out.println(b.toString());
        }
        
    }

    private void writeEdgePath(Set edges, PrintStream out)
    {
        out.println("EdgePath (class=java.lang.String)");
        for(Iterator it = edges.iterator(); it.hasNext(); )
        {
            AnnotatedEdge ae = (AnnotatedEdge) it.next();
            StringBuffer b = new StringBuffer(edgeName(ae));
            b.append(" = ");
            b.append(array2String(ae.paths));
            
            out.println(b.toString());
        }
        
    }

    private String array2String(int[] a)
    {
        StringBuffer b = new StringBuffer();
        for(int x=0; x < a.length; x++)
        {
            b.append(a[x]);
            if(x < a.length - 1)
            {
                b.append(", ");
            }
        }

        return b.toString();
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
            StringBuffer b = new StringBuffer(edgeName(ae));
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
