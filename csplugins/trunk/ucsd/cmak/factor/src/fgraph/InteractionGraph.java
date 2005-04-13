package fgraph;

import fgraph.util.ObjectIntMap;
import fgraph.util.Target2PathMap;

import netan.BioGraph;
import netan.EdgeType;

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
import java.util.Arrays;

import java.util.logging.Logger;

import cytoscape.data.mRNAMeasurement;

import giny.model.Node;
import giny.model.Edge;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.bitvector.BitMatrix;

/**
 * A graph of protein-protein and protein-DNA interactions.
 * Also, pvalues or probabilities associated with those edges.
 * Also, knockout expression data.
 */
public class InteractionGraph
{
    private static Logger logger = Logger.getLogger(FactorGraph.class.getName());

    private BioGraph _bioGraph;
    
    private List _activeEdges;

    // submodels
    private List _submodels;

    private ExpressionDataIF _expressionData;

    // paths found on this interaction graph using the _expressionData
    private PathResult _paths;
    
    // map edge index to pval (if p-d edge) or probability (if p-p edge).
    private OpenIntDoubleHashMap _edgePvalMap;


    
    InteractionGraph(BioGraph g)
    {
        
        _bioGraph = g;
        _edgePvalMap = new OpenIntDoubleHashMap();
    }

    
    OpenIntDoubleHashMap getEdgePvalMap()
    {
        return _edgePvalMap;
    }
    
    public BioGraph getBioGraph()
    {
        return _bioGraph;
    }
    
    /**
     *
     * @param
     * @return an array of the indices of edges that go from source
     *         to target (includes both directed and undirected edges).
     */
    public int[] getEdgeIndicesArray(int source, int target)
    {
        //return _graph.getEdgeIndicesArray(source, target, true);
        return _bioGraph.edgesConnecting(source, target);
    }
    
    public boolean containsNode(String name)
    {
        return _bioGraph.nodeNames().contains(name);
    }
    
    public int name2Node(String name)
    {
        //return _name2node.get(name);
        return _bioGraph.getNodeId(name);
    }

    public String node2Name(int node)
    {
        return _bioGraph.getNodeName(node);
    }

    /**
     * @param edgeIndex the root graph index of an edge in the
     * interaction graph.
     * @return a String of the form: source-node (edge-type) target-node
     */
    String edgeName(int edgeIndex)
    {
        int src = _bioGraph.getEdgeSourceIndex(edgeIndex);
        int tgt = _bioGraph.getEdgeTargetIndex(edgeIndex);
        String type = _bioGraph.getEdgeType(edgeIndex);

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
            src = _bioGraph.getEdgeTargetIndex(edgeIndex);
            tgt = _bioGraph.getEdgeSourceIndex(edgeIndex);
        }
        else
        {
            src = _bioGraph.getEdgeSourceIndex(edgeIndex);
            tgt = _bioGraph.getEdgeTargetIndex(edgeIndex);
        }

        String type = _bioGraph.getEdgeType(edgeIndex);

        return edgeName(src, tgt, type);
    }
    
    String edgeName(int src, int tgt, String type)
    {
        StringBuffer b = new StringBuffer();
        b.append(_bioGraph.getNodeName(src));
        b.append(" (");
        b.append(type);
        b.append(") ");
        b.append(_bioGraph.getNodeName(tgt));

        return b.toString();
    }

    
    /**
     * @return a label identifying an edge.  The label is:
     * "SourceName"."TargetName"
     */
    public String edgeLabel(int edgeIndex)
    {
        StringBuffer b = new StringBuffer();
        b.append(node2Name(_bioGraph.getEdgeSourceIndex(edgeIndex)));
        b.append(".");
        b.append(node2Name(_bioGraph.getEdgeTargetIndex(edgeIndex)));

        return b.toString();
    }

    
    /**
     * @return true if edge is a protein-DNA edge, false otherwise
     */
    public boolean isProteinDNA(int edgeIndex)
    {
        return _bioGraph.getEdgeType(edgeIndex).equals(EdgeType.PD);
    }

    public State getFixedDir(int edgeIndex)
    {
        if(isProteinDNA(edgeIndex))
        {
            return State.PLUS;
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

        _bioGraph.removeEdges(toRemove.elements());
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
     * "targetNode" to change.
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
        return _bioGraph.edges();
    }

    
    int[] getKOIndices()
    {
        String[] conds = _expressionData.getConditionNames();
        
        IntArrayList ko = new IntArrayList(conds.length);
        
        for(int x=0; x < conds.length; x++)
        {
            if(containsNode(conds[x]))
            {
                int i = name2Node(conds[x]); 
                ko.add(i);
            }
        }
        ko.trimToSize();

        return ko.elements();
    }

       

    /*
    private void augmentSubmodel(Submodel model)
    {
        int[] nodes = _bioGraph.nodes();
        int[] kos = getKOIndices();
        
        // find diff exp genes
        BitMatrix affected = new BitMatrix(nodes.length, nodes.length);
        affected.clear();
        Set uniqueAffected = new HashSet();


        int[] koIndex = new int[kos.length];
        for(int x=0; x < nodes.length; x++)
        {
            for(int y=0; y < kos.length; y++)
            {
                if(nodes[x] == kos[y])
                {
                    koIndex[y] = x;
                    logger.info("KO: " + kos[y] + " " + y +
                                "=" + x);
                }
            }
        }
        
        int num=0;
        for(int n = 0; n < nodes.length; n++)
        {
            int node = nodes[n];
            for(int x=0; x < kos.length; x++)
            {
                if(expressionChanges(kos[x], node))
                {
                    num++;
                    affected.put(koIndex[x], n, true);
                    uniqueAffected.add(new Integer(node));
                }
            }
        }

        logger.info("Found " + num + " ko/gene pairs");
        logger.info("Found " + uniqueAffected.size() + " unique genes");
    }
    */
    

    /**
     * Print this interaction graph
     */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        for(Iterator it = _bioGraph.edgesIterator(); it.hasNext(); )
        {
            Edge e = (Edge) it.next();

            Node s = (Node) e.getSource();
            Node t = (Node) e.getTarget();

            int sid = s.getRootGraphIndex();
            int tid = t.getRootGraphIndex();

            buf.append("E=" + e.getIdentifier()
                       + " from=" + _bioGraph.getNodeName(sid) 
                       + " [" + sid + "]"
                       + " to=" + _bioGraph.getNodeName(tid)
                       + " [" + tid + "]"
                       + " type=" + _bioGraph.getEdgeType(e.getRootGraphIndex())
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

            //augmentSubmodel(m);
            
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
                src = _bioGraph.getEdgeSourceIndex(e);
                target = _bioGraph.getEdgeTargetIndex(e);
            }
            else if (ae.maxDir == State.MINUS)
            {
                src = _bioGraph.getEdgeTargetIndex(e);
                target = _bioGraph.getEdgeSourceIndex(e);
            }
            else
            {
                throw new IOException("Unexpected edge direction state: "
                                      + ae.maxDir);
            }
            
            String type = _bioGraph.getEdgeType(e);
            
            StringBuffer b = new StringBuffer();
            b.append(node2Name(src));
            b.append(" ");
            b.append(type);
            b.append(" ");
            b.append(node2Name(target));
            
            out.println(b.toString());
        }

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

        int[] nodes = _bioGraph.nodes();

        for(int n=0, N =nodes.length; n < N; n++)
        {
            out.println(nodes[n] + " = " + _bioGraph.getNodeName(nodes[n]));
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
            int ko = nodes.get(n);
            out.println(node2Name(ko) + " = KO");

            IntArrayList tmap = _paths.getTarget2PathMap(ko).keys();

            for(int t=0; t < tmap.size(); t++)
            {
                out.println(node2Name(tmap.get(t)) + " = Buffered");
            }
        }


        
    }
}
