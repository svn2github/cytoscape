package bool;

import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;
import luna.LunaRootGraph;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class FactorGraph
{
    // A giny.model.RootGraph
    private RootGraph _g;

    /* Map Integer node identifiers (returned by the giny implementation)
     * to NodeAttribute objects.
     *
     * A variable node's attributes consist of a table of
     * states and associated probability values.
     *
     * A function node's attributes consist of a table of
     * variable states (each set of states is named a
     * configuration) and the joint probability of each
     * configuration.
     */ 
    private Map _nodeAtt;

    /**
     * Map edge identifiers to the most recent message passed along
     * that edge.  Integer -> Message
     *
     */
    private Map _msgMap;

    public static void main(String[] args)
        throws AlgorithmException
    {
        try
        {
            RootGraph f = new LunaRootGraph();
            FactorGraph graph = new FactorGraph(f);
            
            FunctionTable fst = new FunctionTable(2, State.BOOLEAN_SET);
            fst.setProb(0, .5);
            fst.setProb(1, .5);
            fst.setProb(2, .9);
            fst.setProb(3, .1);

            FunctionTable frt = new FunctionTable(2, State.BOOLEAN_SET);
            frt.setProb(0, .8);
            frt.setProb(1, .2);
            frt.setProb(2, .2);
            frt.setProb(3, .8);

            FunctionTable fwt = new FunctionTable(3, State.BOOLEAN_SET);
            fwt.setProb(7, .2475);
            fwt.setProb(6, .0025);
            fwt.setProb(5, .225);
            fwt.setProb(4, .025);
            fwt.setProb(3, .225);
            fwt.setProb(2, .025);
            fwt.setProb(1, 0);
            fwt.setProb(0, .25);

            
            int night = graph.createNode(new BooleanAttributes(.6, .4));
            int fs = graph.createNode(fst);
            int sprinkler = graph.createNode(new BooleanAttributes(.5, .5));
            
            int cloudy = graph.createNode(new BooleanAttributes(.5, .5));
            int fr = graph.createNode(frt);
            int rain = graph.createNode(new BooleanAttributes(.5, .5));
            
            int fw = graph.createNode(fwt);
            int wet = graph.createNode(new BooleanAttributes(.5, .5));
            
            graph.createDirectedEdges(night, fs);
            fst.setNode2Var(night, 0);

            graph.createDirectedEdges(fs, sprinkler);
            fst.setNode2Var(sprinkler, 1);

            graph.createDirectedEdges(cloudy, fr);
            frt.setNode2Var(cloudy, 0);

            graph.createDirectedEdges(fr, rain);
            frt.setNode2Var(rain, 1);

            graph.createDirectedEdges(sprinkler, fw);
            fwt.setNode2Var(sprinkler, 0);

            graph.createDirectedEdges(rain, fw);
            fwt.setNode2Var(rain, 1);

            graph.createDirectedEdges(fw, wet);
            fwt.setNode2Var(wet, 2);
      
            //graph.sumProduct();
      
            if(args.length > 0)
            {
                graph.writeSif(args[0]);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public FactorGraph()
    {
        this(new LunaRootGraph());
    }

    
    public FactorGraph(RootGraph graph)
    {
        _g = graph;
        _nodeAtt = new HashMap();
        _msgMap = new HashMap();
    }

    public Message getMessage(int sourceNode, int targetNode)
    {
        int[] edges = _g.getEdgeIndicesArray(sourceNode, targetNode, true);
        if(edges != null && edges.length > 0)
        {
            Integer eId = new Integer(edges[0]);
            if(_msgMap.containsKey(eId))
            {
                return (Message) _msgMap.get(eId);
            }
        }

        return null;
    }

    //public void sumProduct(int[] measurementNodes)
    public void sumProduct() throws AlgorithmException
    {
        _msgMap.clear();
        int[] nodes = _g.getNodeIndicesArray();
        
        int iterations = 0;
        int edgeCount = _g.getEdgeCount();
        while(_msgMap.size() < edgeCount)
        {
            if(iterations > edgeCount)
            {
                break;
            }
            iterations++;

            //            for(int n = 0; n < nodes.length; n++)
            for(int n = nodes.length - 1; n >= 0; n--)
            {
                int nId = nodes[n];
                NodeAttributes atts = (NodeAttributes) _nodeAtt.get(new Integer(nId));

                // Since all edges in the graph are bi-directional 
                // (represented by 2 directed edges) the InDegree should
                // equal the OutDegree.
                int numNeighbors = _g.getOutDegree(nId);
                
                // params:
                //   1. source node identifier
                //   2. get undirected edges?
                //   3. get incoming edges?
                //   4. get outgoing edges?
                int[] inEdges = _g.getAdjacentEdgeIndicesArray(nId, false, true, false);

                //printEdges(inEdges);

                if(inEdges == null)
                {
                    System.out.println("no inEdges for " + nId);
                    break;
                }

                System.out.println("sumProduct: node=" + nId
                                   + " incident=" + atts.numIncidentMessages()
                                   + " numNeighbors=" + numNeighbors);
                
                /** Message passing algorithm
                 *
                 * 
                 *
                 */
                if(atts.numIncidentMessages() ==  numNeighbors - 1)
                {
                    int outEdge = chooseOutEdge(nId, inEdges);
                    sendMessage(nId, inEdges, outEdge);
                }
                else if (atts.numIncidentMessages() == numNeighbors)
                {
                    int[] outEdges = _g.getAdjacentEdgeIndicesArray(nId, false, false, true);
                    for(int oe=0; oe < outEdges.length; oe++)
                    {
                        sendMessage(nId, inEdges, outEdges[oe]);
                    }
                }
            }
        }
    }

    /**
     * @return the index of the edge directed from sourceIndex to its
     * neighbor which has not sent a message to node sourceIndex.
     */
    private int chooseOutEdge(int sourceIndex, int[] inEdges)
        throws AlgorithmException
    {
        int[] oEdges = null;
        for(int e = 0; e < inEdges.length; e++)
        {
            Integer i = new Integer(inEdges[e]);
            if(!_msgMap.containsKey(i))
            {
                oEdges = _g.getEdgeIndicesArray(sourceIndex, 
                                                _g.getEdgeSourceIndex(inEdges[e]), 
                                                false);
                /*
                if(oEdges == null)
                {
                    System.out.println("no out found from " + sourceIndex
                                       + " to " + _g.getEdgeSourceIndex(inEdges[e]));
                    break;

                }
                System.out.println("found " + oEdges.length 
                                   + " from " + sourceIndex
                                   + " to " + _g.getEdgeSourceIndex(inEdges[e]));
                */
                break;
            }
        }
        
        if(oEdges != null && oEdges.length > 0)
        {
            // need to fix this if there are other edges.
            return oEdges[0];
        }
        else
        {
            throw new AlgorithmException("chooseOutEdge called but node " 
                                         + sourceIndex 
                                         + " has no neighbors that have not sent messages");
        }
        
    }
    
    private void sendMessage(int sourceIndex, int[] incidentEdges, int outEdge)
    {
        System.out.println("### sendMessage: atNode " + sourceIndex
                           + " on edgeId" + outEdge
                           + " from " + _g.getEdgeSourceIndex(outEdge)
                           + " to " + _g.getEdgeTargetIndex(outEdge));

        Integer outInt = new Integer(outEdge);
        if(_msgMap.containsKey(outInt))
        {
            return;
        }

        NodeAttributes att = (NodeAttributes) _nodeAtt.get(new Integer(sourceIndex));

        List msgs = new ArrayList();
        
        for(int x=0; x < incidentEdges.length; x++)
        {
            Integer id = new Integer(incidentEdges[x]);
            if(_msgMap.containsKey(id))
            {
                
                msgs.add(_msgMap.get(id));
            }
        }

        Message m =  att.sumProduct(msgs, _g.getEdgeTargetIndex(outEdge));

        _msgMap.put(outInt, m);
        
        incrementIncident(sourceIndex, outEdge);
    }

    private void incrementIncident(int sourceNodeId, int edgeId)
    {

        int n1 = _g.getEdgeSourceIndex(edgeId);
        int n2 = _g.getEdgeTargetIndex(edgeId);

        if(sourceNodeId == n1)
        {
            System.out.println("incrementing messages for " + n2);
            ((NodeAttributes) _nodeAtt.get(new Integer(n2))).incrementMessages();
        }
        else if (sourceNodeId == n2)
        {
            System.out.println("incrementing messages for " + n1);
            ((NodeAttributes) _nodeAtt.get(new Integer(n1))).incrementMessages();
        }
        else
        {
            // error?
        }
    }

    /**
     * @param atts The attributes of the node to create
     * @return The identifier of the new node (according to 
     * GINY, it should be a negative int).
     */
    public int createNode(NodeAttributes atts)
    {
        int n = _g.createNode();
        atts.setId(n);
        _nodeAtt.put(new Integer(n), atts);
        return n;
    }

    public void createDirectedEdges(int n1, int n2)
    {
        _g.createEdge(n1, n2, true);
        _g.createEdge(n2, n1, true);
    }

    /**
     * Write the RootGraph to a Cytoscape .sif file.
     */
    public void writeSif(String filename) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        System.out.println("Printing E=" + _g.getEdgeCount() 
                           + " N=" + _g.getNodeCount());

        for(Iterator it = _g.edgesIterator(); it.hasNext(); )
        {
            Edge e = (Edge) it.next();

            Node s = (Node) e.getSource();
            Node t = (Node) e.getTarget();

            System.out.println("Writing E=" + e.getIdentifier()
                               + " from=" + s.getIdentifier() + " to=" 
                               + t.getIdentifier());
            out.println(s.getIdentifier() + " x " + t.getIdentifier());
        }
        
        out.close();
   }

    private void printEdges(int[] edges)
    {
        if(edges == null)
        {
            System.out.println("printEdges called with null edges");
        }
        for(int e=0; e < edges.length; e++)
        {
            System.out.println("printEdge: " + edges[e]
                               + " from " + _g.getEdgeSourceIndex(edges[e])
                               + " to " + _g.getEdgeTargetIndex(edges[e])
                               + " directed=" + _g.isEdgeDirected(edges[e]));
        }
    }
}

