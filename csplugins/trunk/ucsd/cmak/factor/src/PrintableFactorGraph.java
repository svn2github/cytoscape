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

public class PrintableFactorGraph extends FactorGraph
{
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

    protected PrintableFactorGraph(InteractionGraph ig, PathResult pathResults)
    {
        super(ig, pathResults);
        
        IntArrayList kos = pathResults.getKOs();
        IntArrayList edges = pathResults.getEdge2PathMap().keys();

        // for Cytoscape
        _edge = new IntArrayList(edges.size());
        _sign = new IntArrayList(edges.size());
        _dir = new IntArrayList(edges.size());
        _ko = new IntArrayList(kos.size());
        _orFactor = new IntArrayList(kos.size());
        _pathActive = new IntArrayList(pathResults.getPathCount());
        _pathFactor = new IntArrayList(pathResults.getPathCount());

        int nN = 2 * kos.size() + 2 * pathResults.getPathCount() + 3 * edges.size();
        _nodeLabel = new OpenIntObjectHashMap(nN);

    }

    public static PrintableFactorGraph createPrintable(InteractionGraph ig,
                                                       PathResult pathResults)
    {
        PrintableFactorGraph fg = new PrintableFactorGraph(ig, pathResults);
        fg.buildGraph(ig, pathResults);
        return fg;
    }
    
    protected RootGraph _newRootGraph()
    {
        //return GinyFactory.createRootGraph();
        
        return new AdjacencyListRootGraph();
    }

    protected int createOR(int koNodeIndex, int targetNodeIndex)
    {
        int node = super.createOR(koNodeIndex, targetNodeIndex);
        _nodeLabel.put(node, makeKOLabel(koNodeIndex, targetNodeIndex));
        _orFactor.add(node);
        return node;
    }
    
    protected int createPathFactor(int pathNumber)
    {
        int node = super.createPathFactor(pathNumber);
        _nodeLabel.put(node, String.valueOf(pathNumber));
        _pathFactor.add(node);
        return node;
    }
    
    protected int createEdge(int interactionEdgeIndex)
    {
        int node = super.createEdge(interactionEdgeIndex);

        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _edge.add(node);
        return node;
    }

    protected int createSign(int interactionEdgeIndex)
    {
        int node = super.createSign(interactionEdgeIndex);
        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _sign.add(node);
        return node;
    }

    protected int createDir(int interactionEdgeIndex)
    {
        int node = super.createDir(interactionEdgeIndex);
        _nodeLabel.put(node, _ig.edgeLabel(interactionEdgeIndex));
        _dir.add(node);
        return node;
    }

    protected int createPathActive(int pathNumber)
    {
        int node = super.createPathActive(pathNumber);

        _nodeLabel.put(node, String.valueOf(pathNumber));
        _pathActive.add(node);
        
        return node;
    }

    protected int createKO(int koNodeIndex, int targetNodeIndex)
    {
        int node = super.createKO(koNodeIndex, targetNodeIndex);
        _nodeLabel.put(node, makeKOLabel(koNodeIndex, targetNodeIndex));
        _ko.add(node);
        return node;
    }

    
    private String makeKOLabel(int koNodeIndex, int targetNodeIndex)
    {
        StringBuffer b = new StringBuffer();
        b.append(_ig.node2Name(koNodeIndex));
        b.append(".");
        b.append(_ig.node2Name(targetNodeIndex));
        return b.toString();
    }

    
    public void printMaxConfig()
    {
        ObjectArrayList aes = _edgeMap.values();
          
        for(int n=0, N =aes.size(); n < N; n++)
        {
            AnnotatedEdge ae = (AnnotatedEdge) aes.get(n);

            if(ae.maxState == State.ONE)
            {
                System.out.println("edge active: " + ae);
            }
            else
            {
                System.out.println("edge inactive: " + ae);
            }
        }
     
        for(int s=0, N=_pathActive.size(); s < N; s++)
        {
            VariableNode vn = (VariableNode) _nodeMap.get(_pathActive.get(s));
            ProbTable pt = vn.getProbs();

            if(pt.hasUniqueMax() && (pt.maxState() == State.ONE))
            {
                System.out.println("path " + vn.getId() + " is active P=" +
                                   pt.prob(State.ONE));
            }
            else
            {
                System.out.println("path " + vn.getId() + " inactive " + pt);
            }
        }

        for(int s=0, N=_ko.size(); s < N; s++)
        {
            VariableNode vn = (VariableNode) _nodeMap.get(_ko.get(s));
            ProbTable pt = vn.getProbs();

            if(pt.hasUniqueMax() && (pt.maxState() != State.ZERO))
            {
                System.out.println("ko " + _nodeLabel.get(_ko.get(s))
                                   + " is active " + pt.maxState());
            }
            else
            {
                System.out.println("ko " + _nodeLabel.get(_ko.get(s))
                                   + " is inactive " + pt);
            }
        }
    }


    
    /**
     * Write this factor graph as a Cytoscape "sif" file, and
     * write the node attributes.
     */
    public void writeSif(String filename) throws IOException
    {
        CyUtil.writeSif(_g, filename + "_facgraph.sif");

        writeNodeAttr(filename + "_facgraph");
    }

    /**
     * Write the node attributes of this factor graph in a Cytoscape
     * readable format.  Create NodeType, InteractionLabel, and NodeProbs
     * attributes
     */
    private void writeNodeAttr(String filename) throws IOException
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
    void writeNodeProbs(PrintStream out)
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
    private void writeNodeType(PrintStream out)
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
    private void writeNodeLabels(PrintStream out)
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
}
