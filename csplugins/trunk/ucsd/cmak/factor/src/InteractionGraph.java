import java.io.FileNotFoundException;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import cytoscape.data.readers.InteractionsReader;
import cytoscape.data.Interaction;
//import cytoscape.data.CachedExpressionData;
import cytoscape.data.ExpressionData;
import cytoscape.data.mRNAMeasurement;
import cytoscape.data.readers.TextFileReader;
import cytoscape.data.servers.BioDataServer;
import cytoscape.data.GraphObjAttributes;

import cytoscape.util.GinyFactory;

import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntDoubleHashMap;
import cern.colt.map.OpenIntObjectHashMap;

/**
 * A graph of protein-protein, protein-DNA, and genetic interactions
 */
public class InteractionGraph
{
    private double PVAL_THRESH = 0.8;
    
    private RootGraph _graph;
    private BioDataServer _bds;
    private OpenIntObjectHashMap _node2name;
    private OpenIntObjectHashMap _edge2type;
    
    private OpenIntIntHashMap _edge2pd; // used to efficiently calculate isProteinDNA

    private static final int TRUE = 1;
    private static final int FALSE = 2;
    
    /* map the name of an edge in the sif file to an index in the RootGraph
     * edgeNames are computed by the edgeName method
     */
    private ObjectIntMap _name2edge;

    /* map the name of a node in the sif file to an index in the RootGraph
     */
    private ObjectIntMap _name2node; 

    private ExpressionData _expressionData;

    // map edge index to pval
    private OpenIntDoubleHashMap _edgePvalMap;

    private static String _defaultBioDataDir = "/cellar/users/cmak/cytoscape/testData/annotation/manifest";

    private static Pattern directedPattern = Pattern.compile("ypd|mms|pd", 
                                                             Pattern.CASE_INSENSITIVE);

    private InteractionGraph(RootGraph g)
    {
        _graph = g;
        _edgePvalMap = new OpenIntDoubleHashMap();
    }

    private InteractionGraph()
    {
        this(GinyFactory.createRootGraph());
    }

    public RootGraph getRootGraph()
    {
        return _graph;
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
     * @return true if edge is a protein-DNA edge, false otherwise
     */
    public boolean isProteinDNA(int edge)
    {
        if(_edge2pd.containsKey(edge))
        {
            int val = _edge2pd.get(edge);

            if(val == TRUE)
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
    
    /**
     * Load an edge attributes file and store the data in
     * a data structure that maps edge index to a p value
     * 
     * @param filename the file containing "EdgeProbs" edge attributes
     */
    public void loadEdgeData(String filename)
        throws FileNotFoundException, IllegalArgumentException, NumberFormatException
    {
          GraphObjAttributes edgeData = new GraphObjAttributes();
          edgeData.readAttributesFromFile( filename );
          Map m = edgeData.getAttribute("EdgeProbs");
          _edgePvalMap.ensureCapacity(m.size());

          for(Iterator it = m.entrySet().iterator(); it.hasNext();)
          {
              Map.Entry e = (Map.Entry) it.next();

              //System.out.print("edge key=" + e.getKey() + " val=" +  e.getValue());
              int i = _name2edge.get(e.getKey());

              //System.out.println(" mapped to index: " + i);

              _edgePvalMap.put(i, ((Double) e.getValue()).doubleValue());
          }
          
    }

    
    /**
     * @param edgeIndex RootGraph index of an edge
     * @return the pval of that edge or 1 if the pvalue is not known
     */
    public double getPvalue(int edgeIndex)
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


    /**
     * Factory method to create and InteractionGraph from a .sif file
     * Note: does not throw FileNotFoundException if filename is not found
     * @param filename The name of the file. 
     */
    public static InteractionGraph createFromSif(String filename)
        throws Exception
    {
        String rawText = "";
        //BioDataServer bds = new BioDataServer(_defaultBioDataDir);
        List interactions = new ArrayList();

        TextFileReader reader = new TextFileReader (filename);
        
        int len = reader.read ();
        
        if(len > 0)
        {
            rawText = reader.getText ();
        }

        String delimiter = " ";
        if (rawText.indexOf ("\t") >= 0)
        {
            delimiter = "\t";
        }
        StringTokenizer strtok = new StringTokenizer (rawText, "\n");
        
        while (strtok.hasMoreElements ()) {
            String newLine = (String) strtok.nextElement ();
            interactions.add (new Interaction(newLine, delimiter));
        }

        InteractionGraph g = new InteractionGraph();
        g.createRootGraphFromInteractions(interactions, false);

        return g;

    }
    
    protected String canonicalizeName(String name)
    {
        return name;
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
     * Edges labelled "ypd" or "mms" are directed
     * Edges labeled "pp" are undirected
     */
    private void createRootGraphFromInteractions(List interactions, 
                                                 boolean canonicalize)
    {
        //figure out how many nodes and edges we need before we create the graph;
        //this improves performance for large graphs
        Set nodeNameSet = new HashSet();
        int edgeCount = 0;

        int selfEdges = 0;
        int numDirected = 0;
        int numUndirected = 0;
        String type;

        // find all unique node names
        for (int i=0; i<interactions.size(); i++) 
        {
            Interaction interaction = (Interaction) interactions.get(i);
            String sourceName = interaction.getSource();
            
            if(canonicalize) sourceName = canonicalizeName (sourceName);
            
            nodeNameSet.add(sourceName); //does nothing if already there

            String [] targets = interaction.getTargets ();
            for (int t=0; t < targets.length; t++) 
            {
                String targetNodeName = targets[t];
                if(canonicalize) targetNodeName = canonicalizeName (targetNodeName);
                nodeNameSet.add(targetNodeName); //does nothing if already there
                edgeCount++;

                type = interaction.getType();
                // ypd and mms are directed edges from ChIP-CHIP experiments
                if(sourceName.equals(targetNodeName))
                {
                    selfEdges++;
                }
                else if( isDirected(type) )
                {
                    numDirected++;
                }
                else
                {
                    numUndirected++;
                }


            }
        }

        // Create the RootGraph
        int nodeCount = nodeNameSet.size();
        _graph = GinyFactory.createRootGraph(nodeCount, edgeCount);
        // calling ensureCapacity results in better performance
        _graph.ensureCapacity(nodeCount, edgeCount);

        // Create the nodes
        int[] nIndices = _graph.createNodes(nodeCount);
        
        // Map each node index to a node name
        _node2name = new OpenIntObjectHashMap(nodeCount);
        _name2node = new ObjectIntMap(nodeCount);

        int i=0;
        for(Iterator si = nodeNameSet.iterator(); si.hasNext();)
        {
            String nodeName = (String)si.next();
        
            _node2name.put(nIndices[i], nodeName);
            _name2node.put(nodeName, nIndices[i]);
            i++;
        }
        
        /* 
         * for efficiency, create all of the edges at once
         * after source and target node index arrays
         * have been created
         *
         * needs to be modified for creating directed (ypd)
         * vs undirected edges
         *
         * loop over the interactions
         * record edge source and target node indices
         *
         */
        
        String targetNodeName = null;
        String sourceName = null;

        int sourceIndex = 0;
        int targetIndex = 0;

        Interaction interaction = null;

        /*
        System.out.println("interactions=" + interactions.size());
        System.out.println("edges=" + edgeCount);
        System.out.println("selfEdges=" + selfEdges);
        */

        int[] Dsrc = new int[numDirected];
        int[] Dtgt = new int[numDirected];

        int[] Usrc = new int[numUndirected + selfEdges];
        int[] Utgt = new int[numUndirected + selfEdges];
        
        int[] self = new int[selfEdges];
        
        String[] directedTypes = new String[numDirected];
        String[] selfTypes = new String[selfEdges];
        String[] undirectedTypes = new String[numUndirected + selfEdges];

        int selfCt = 0;
        int undirCt = 0;
        int dirCt = 0;
        int x=0;
        for (x=0; x < interactions.size(); x++) 
        {
            interaction = (Interaction) interactions.get(x);

            if(canonicalize) 
            {
                sourceName = canonicalizeName (interaction.getSource ());
            }
            else
            {
                sourceName = interaction.getSource();
            }

            sourceIndex = _name2node.get(sourceName);

            String [] targets = interaction.getTargets ();
            for (int t=0; t < targets.length; t++) 
            {
                if(canonicalize)
                    targetNodeName = canonicalizeName (targets [t]);
                else
                    targetNodeName = targets[t];

                targetIndex = _name2node.get(targetNodeName);

                type = interaction.getType();
                // record edge endpoints
                if(sourceIndex == targetIndex)
                {
                    undirectedTypes[undirCt] = type;
                    Usrc[undirCt] = sourceIndex;
                    Utgt[undirCt] = targetIndex;
                    undirCt++;
                    
                    /*selfTypes[selfCt] = interaction.getType();
                    self[selfCt]  = sourceIndex;
                    selfCt++;
                    */
                }
                else if( isDirected(type) )
                {
                    directedTypes[dirCt] = type;
                    Dsrc[dirCt] = sourceIndex;
                    Dtgt[dirCt] = targetIndex;
                    dirCt++;
                }
                else
                {
                    undirectedTypes[undirCt] = type;
                    Usrc[undirCt] = sourceIndex;
                    Utgt[undirCt] = targetIndex;
                    undirCt++;
                }
            }
        }

        /*
        System.out.println("x=" + x);
        System.out.println("selfCt=" + selfCt);
        System.out.println("undirCt=" + undirCt);
        */

        _edge2type = new OpenIntObjectHashMap( edgeCount );
        _edge2pd = new OpenIntIntHashMap( edgeCount );
        _name2edge = new ObjectIntMap( edgeCount );

        //createEdges(self, self, selfTypes, false);
        createEdges(Usrc, Utgt, undirectedTypes, false);
        createEdges(Dsrc, Dtgt, directedTypes, true);

        
    } // createRootGraphFromInteractionData

    private void createEdges(int[] src, int[] tgt, String[] types, boolean directed)
    {
        int[] edges = _graph.createEdges(src, tgt, directed);
        for(int e=0; e < edges.length; e++)
        {
            _edge2type.put(edges[e], types[e]);
            _name2edge.put(edgeName(src[e], tgt[e], types[e]), edges[e]);

            if( isDirected(types[e]))
            {
                _edge2pd.put(edges[e], TRUE);
            }
            else
            {
                _edge2pd.put(edges[e], FALSE);
            }
            /*
            System.out.println("Saving edge name: " + edgeName(src[e], tgt[e], types[e])
                               + " " + edges[e]);
            */
        }

    }

    private String edgeName(int src, int tgt, String type)
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
     * @return true if an edge of type "type" is directed, false otherwise
     */
    private boolean isDirected(String type)
    {
        return directedPattern.matcher(type).matches();
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

        String[] conds = _expressionData.getConditionNames();
        String[] genes = _expressionData.getGeneNames();
        for(int c=0; c < conds.length; c++)
        {
            String cc = conds[c];
            buf.append("Condition: ");
            buf.append(cc);
            buf.append("\n");
            for(int g=0; g < genes.length; g++)
            {
                String gg = genes[g];
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

        return buf.toString();
    }
}
