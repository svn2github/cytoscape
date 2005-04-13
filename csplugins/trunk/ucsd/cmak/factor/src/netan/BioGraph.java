package netan;

import netan.parse.SifLexer;
import netan.parse.SifParser;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;

import java.util.logging.Logger;

import java.io.IOException;
import java.io.FileInputStream;

import cern.colt.map.OpenIntIntHashMap;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;

import fgraph.BadInputException;
import fgraph.util.ObjectIntMap;
import fgraph.util.SetMap;

import fing.model.FingRootGraphFactory;

import giny.model.RootGraph;

import antlr.ANTLRException;

/**
 * A biological interaction graph.
 * <p>
 * Basically, provide methods to create a graph from a SIF file
 * and map names to nodes.
 * <p>
 * Enforce the constraint that only one type of each edge can
 * connect two nodes.  So for nodes A and B.
 * if (A pp B), (B pp A) will not be created.
 * if (A pd B), (B pd A) is ok.
 * if (A pd B), another instance of (A pd B) will not be created. 
 */
public class BioGraph
{
    private static Logger logger = Logger.getLogger(BioGraph.class.getName());

    private RootGraph _g;
    private OpenIntObjectHashMap _nodeId2name;
    private ObjectIntMap _name2nodeId;

    
    private static final int UNDIRECTED = 1;
    private static final int DIR_S2T = 2; // edge is directed src -> target
    private static final int DIR_T2S = 3; // edge is directed target -> src

    // map each edge index to the edge type
    private OpenIntObjectHashMap _edge2type;

    // used to efficiently calculate isProteinDNA
    private OpenIntIntHashMap _edge2pd; 

    // map a String edge type to a Set of edge ids of that type.
    private SetMap _edgeType;
    
    public BioGraph(String sifFile)
        throws IOException, BadInputException
    {
        _g = FingRootGraphFactory.instantiateRootGraph();
        _nodeId2name = new OpenIntObjectHashMap();
        _name2nodeId = new ObjectIntMap();
        _edgeType = new SetMap();

        _edge2type = new OpenIntObjectHashMap();
        _edge2pd = new OpenIntIntHashMap();
        
        
        loadSif(sifFile);
    }

    public Set nodeNames()
    {
        return _name2nodeId.keySet();
    }

    public int getNodeCount()
    {
        return _g.getNodeCount();
    }

    public int getEdgeCount()
    {
        return _g.getEdgeCount();
    }

    public int getEdgeCount(String edgeType)
    {
        if(_edgeType.containsKey(edgeType))
        {
            return _edgeType.get(edgeType).size();
        }

        return 0;

    }

    
    /*
    public int[] incomingEdges(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentEdges(type, _name2nodeId.get(node),
                                 false, true, false);
        }
        else
        {
            return new int[0];
        }
    }

    public int[] outgoingEdges(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentEdges(type, _name2nodeId.get(node),
                                 false, false, true);
        }
        else
        {
            return new int[0];
        }

    }

    public int[] undirectedEdges(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentEdges(type, _name2nodeId.get(node),
                                 true, false, false);
        }
        else
        {
            return new int[0];
        }

    }
    */

    public Set incomingNeighbors(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentNodes(type, _name2nodeId.get(node),
                                 false, true, false);
        }
        else
        {
            return Collections.EMPTY_SET;
        }
    }

    public Set outgoingNeighbors(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentNodes(type, _name2nodeId.get(node),
                                 false, false, true);
        }
        else
        {
            return Collections.EMPTY_SET;
        }

    }

    public Set undirectedNeighbors(String type, String node)
    {
        if(_name2nodeId.containsKey(node))
        {
            return adjacentNodes(type, _name2nodeId.get(node),
                                 true, false, false);
        }
        else
        {
            return Collections.EMPTY_SET;
        }

    }

    
    public int[] getAdjacentEdges(int node,
                                  boolean undirected,
                                  boolean incoming,
                                  boolean outgoing)
    {
        return _g.getAdjacentEdgeIndicesArray(node,
                                              undirected,
                                              incoming,
                                              outgoing);
    }

    
    private int[] adjacentEdges(String type, int node,
                               boolean undirected,
                               boolean incoming,
                               boolean outgoing)
    {
        Set edges = _edgeType.get(type);

        int[] in = _g.getAdjacentEdgeIndicesArray(node,
                                                  undirected,
                                                  incoming,
                                                  outgoing);
        IntArrayList ok = new IntArrayList();
        
        for(int x=0; x < in.length; x++)
        {
            int e = in[x];
            if(edges.contains(new Integer(e)))
            {
                ok.add(e);
            }
        }
        ok.trimToSize();
        return ok.elements();
    }

    private Set adjacentNodes(String type, int node,
                              boolean undirected,
                              boolean incoming,
                              boolean outgoing)
    {
        Set edges = _edgeType.get(type);

        int[] in = _g.getAdjacentEdgeIndicesArray(node,
                                                  undirected,
                                                  incoming,
                                                  outgoing);
        IntArrayList ok = new IntArrayList();

        Set adjacentNodes = new HashSet();
        
        for(int x=0; x < in.length; x++)
        {
            int e = in[x];
            if(edges.contains(new Integer(e)))
            {
                int target = getRelativeTarget(e, node);
                adjacentNodes.add(_nodeId2name.get(target));
            }
        }
        return adjacentNodes;
    }


    /**
     * Efficiently calculate the other endpoint of the edge that is not
     * equal to "source"
     * 
     * @param edgeLabel the edge
     * @param source the source node
     * @return the other endpoint of the edge that is not "source"
     */
    private int getRelativeTarget(int edge, int source)
    {
        int s = _g.getEdgeSourceIndex(edge);
        int t = _g.getEdgeTargetIndex(edge);

        /* better performance, Nerius suggestion
         * (Note: ^ == bitwise XOR)
         *
         * Claim: Given a set of integers S = { a, b } and an integer
         *        c which is an element of S, then S \ { c } == (c ^ a) ^ b.
         *
         * Proof:
         * 
         * Suppose c == a, then (a ^ a) = 0 and (0 ^ b) = b
         *     and we return b. OK.
         *     
         * Suppose c == b, consider the four cases that can occur
         * when comparing bits in a and b.
         *  case 1 (a=0, b=0): b^a = 0, 0 ^ b = 0 = a.  OK
         *  case 2 (a=1, b=1): b^a = 0, 0 ^ b = 1 = a.  OK
         *  case 3 (a=1, b=0): b^a = 1, 1 ^ b = 1 = a.  OK
         *  case 4 (a=0, b=1): b^a = 1, 1 ^ b = 0 = a.  OK
         *     
         */
        return ((source ^ s) ^ t);
    }

    
    public boolean edgeExists(String source, String target, String type)
    {
        int s = _name2nodeId.get(source);
        int t = _name2nodeId.get(target);

        return edgeExists(s, t, type);
    }
    
    
    private void loadSif(String sifFile) throws IOException, BadInputException
    {
        try
        {
            SifLexer lexer = new SifLexer(new FileInputStream(sifFile));
            SifParser parser = new SifParser(lexer);

            // Expect a list of 3-element String arrays
            // a[0]: node
            // a[1]: type
            // a[2]: node

            List data = parser.parse();
            
            logger.info("Parsed " + data.size() + " data records from "
                        + sifFile);
                        
            for(int x=0; x < data.size(); x++)
            {
                String[] d = (String[]) data.get(x);

                int s = getNodeId(d[0]);
                int t = getNodeId(d[2]);
                String type = d[1];
                
                if(!edgeExists(s, t, type))
                {
                    int e = _g.createEdge(s, t, EdgeType.isDirected(type));
                    _edgeType.put(type, new Integer(e));

                    _edge2type.put(e, type);
                    if(EdgeType.isDirected(type))
                    {
                        _edge2pd.put(e, DIR_S2T);
                    }
                    else
                    {
                        _edge2pd.put(e, UNDIRECTED);
                    }
                }
                
            }
        }
        catch(ANTLRException e)
        {
            throw new BadInputException(sifFile, e);
        }

        logger.info("Done loading sif: numNodes=" + getNodeCount()
                    + " numEdges=" + getNodeCount());
    }


    /**
     *
     *
     * @param
     * @return true if an edge of type "type" exists between
     * s and t
     * @throws
     */
    private boolean edgeExists(int s, int t, String type)
    {
        if(_edgeType.containsKey(type))
        {
            Set edges = _edgeType.get(type);
            
            int[] conn = edgesConnecting(s, t);
            for(int x=0; x < conn.length; x++)
            {
                int e = conn[x];
                if(edges.contains(new Integer(e)))
                {
                    if(EdgeType.isDirected(type))
                    {
                        if(_g.getEdgeSourceIndex(e) == s &&
                           _g.getEdgeTargetIndex(e) == t)
                        {
                            return true;
                        }
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public boolean edgeExists(int s, int t)
    {
        return _g.edgeExists(s, t);
    }
    
    public int[] edgesConnecting(int s, int t)
    {
        int[] edges = _g.getEdgeIndicesArray(s, t, true);

        if(edges == null)
        {
            edges = new int[0];
        }

        return edges;
    }

    
    /**
     * Get the id of the node associated with "nodeName".
     * If no node exists, create one and return the node id.
     *
     * @param nodeName the name of the node
     * @return the node id, newly created if necessary
     */
    public int getNodeId(String nodeName)
    {
        if(_name2nodeId.containsKey(nodeName))
        {
            return _name2nodeId.get(nodeName);
        }

        int id = _g.createNode();
        _nodeId2name.put(id, nodeName);
        _name2nodeId.put(nodeName, id);

        return id;
    }

    public String getNodeName(int id)
    {
        if(_nodeId2name.containsKey(id))
        {
            return (String) _nodeId2name.get(id);
        }

        return "";
    }

    public int getEdgeSourceIndex(int edgeIndex)
    {
        return _g.getEdgeSourceIndex(edgeIndex);
    }

    public int getEdgeTargetIndex(int edgeIndex)
    {
        return _g.getEdgeTargetIndex(edgeIndex);
    }

    public String getEdgeType(int edgeIndex)
    {
        if(_edge2type.containsKey(edgeIndex))
        {
            return (String) _edge2type.get(edgeIndex);
        }

        return "";
    }

    public int[] edges()
    {
        return _g.getEdgeIndicesArray();
    }

    public int[] nodes()
    {
        return _g.getNodeIndicesArray();
    }

    public Iterator edgesIterator()
    {
        return _g.edgesIterator();
    }

    
    public void removeEdges(int[] edges)
    {
        _g.removeEdges(edges);
    }
}

