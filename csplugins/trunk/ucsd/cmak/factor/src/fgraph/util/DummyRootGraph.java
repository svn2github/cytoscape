package fgraph.util;

import giny.model.Node;
import giny.model.Edge;
import giny.model.GraphPerspective;
import giny.model.RootGraph;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntObjectHashMap;


import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 *
 */
public class DummyRootGraph implements RootGraph
{

    private int _nextNodeIndex;
    private int _nextEdgeIndex;

    private IntListMap _adjMap;

    // map edge index to EdgeStructure object
    private OpenIntObjectHashMap _edges;
    
    
    public String toString()
    {
        
        StringBuffer b = new StringBuffer();
        b.append("num nodes: " + getNodeCount() + "\n");
        b.append("num edges: " + getEdgeCount() + "\n");

        IntArrayList nodes = _adjMap.keys();        

        for(int x=0; x < nodes.size(); x++)
        {
            int node = nodes.get(x);
            b.append(node);
            b.append(" ");
            
            List l = _adjMap.get(node);
            for(int y=0; y < l.size(); y++)
            {
                b.append(l.get(y));
                b.append(", ");
            }
            b.append("\n");
        }
        return b.toString();
    }

    
    public DummyRootGraph()
    {
        _adjMap = new IntListMap();
        _nextNodeIndex = -1;
        _nextEdgeIndex = -1;
        _edges = new OpenIntObjectHashMap();
    }

    public DummyRootGraph(int nodes, int edges)
    {
        this();
        //ensureCapacity(nodes, edges);
    }
    
    /**
     * Ensure the capacity of the RootGraph.
     */
    public void ensureCapacity( int nodes, int edges )
    {
        //_adjMap.ensureCapacity(nodes);
        //_edges.ensureCapacity(edges);
    }

    /**
     * Returns number of active nodes in this perspective.
     * @return an int value; the number of nodes in this ColtRootGraph.
     */
    public int getNodeCount ()
    {
        return _adjMap.keys().size();
    }
 
    /**
     * Returns number of active edges in this perspective.
     * @return an int value; the number of edges in this ColtRootGraph.
     */
    public int getEdgeCount ()
    {
        return _edges.keys().size();
    }
 
    /**
     * Return an array containing the indices of all Nodes in this RootGraph.
     * <br>
     * The result should be considered final; it <b>must not</b> be modified by
     * the receiver.
     * @return an array containing the indices of all Nodes in this RootGraph.
     */
    public int[] getNodeIndicesArray ()
    {
        return copy(_adjMap.keys());
    }

    private int[] copy(IntArrayList array)
    {
        int[] copy = new int[array.size()];
        System.arraycopy(array.elements(), 0, copy, 0, array.size());

        return copy;
    }
    
    /**
     * Return an array containing the indices of all Edges in this RootGraph.
     * <br>
     * The result should be considered final; it <b>must not</b> be modified by
     * the receiver.
     * @return an array containing the indices of all Edges in this RootGraph.
     */
    public int[] getEdgeIndicesArray ()
    {
        return copy(_edges.keys());
    }
 
    /**
     * Create a new Node in this RootGraph, and return its index.	 This method
     * delegates to {@link #createNode( int[], int[] )} with null arguments.
     * @return the index of the newly created Node.
     */
    public int createNode ()
    {
        int n = _getNextNodeIndex();

        //_adjMap.put(n, new ArrayList());

        return n;
    }
 
    /**
     * Create a new Node in this RootGraph, and return its index.	 The new Node
     * will be a MetaParent to the Nodes and Edges with the given indices, and also
     * to any Nodes incident on the given Edges but omitted from the array.
     * @param node_indices a (possibly null) array of the indices in this
     * RootGraph of the Nodes that will be MetaChildren of the newly created Node.
     * @param edge_indices a (possibly null) array of the indices in this
     * RootGraph of the Edges that will be MetaChildren of the newly created Node.
     * @return the index of the newly created Node.
     */
    public int createNode ( int[] node_indices, int[] edge_indices )
    {
        throw new UnsupportedOperationException();
    }

    private int _getNextNodeIndex()
    {
        return _nextNodeIndex--;
    }

    private int _getNextEdgeIndex()
    {
        int e = _nextEdgeIndex--;
        return e;
    }

    
    /**
     * Create <tt>new_node_count</tt> new Nodes in this RootGraph, and return
     * their indices.
     * @param new_node_count the number of new nodes to create.
     * @return an array of size new_node_count containing the indices of the
     * newly created Nodes.
     */
    public int[] createNodes ( int new_node_count )
    {
        int[] nodes = new int[new_node_count];
        
        for(int x=0; x < new_node_count; x++)
        {
            nodes[x] = createNode();
        }

        return nodes;
    }
    
    private boolean nodeExists(int n)
    {
        return _adjMap.containsKey(n);
    }


    private boolean edgeExists(int n)
    {
        return _edges.containsKey(n);
    }

    
    private void assertNodeExists(int n, String message)
    {
        if(!nodeExists(n))
        {
            throw new IllegalArgumentException("ASSERT NODE EXISTS FAILED. "
                                               + message + ": " + n);
        }
    }


    private void assertEdgeExists(int e, String message)
    {
        if(!edgeExists(e))
        {
            throw new IllegalArgumentException("ASSERT EDGE EXISTS FAILED. "
                                               + message + ": " + e);
        }
    }

    
    protected EdgeStructure createEdgeStructure(int source, int target,
                                                int edge,
                                                boolean directed)
    {
        if(directed)
        {
            return new EdgeStructure(source, target, edge);
        }
        else
        {
            return new UndirectedEdgeStructure(source, target, edge);
        }
    }
    
    /**
     * Create a directed Edge from the Node with the given <tt>source_index</tt>
     * to the Node with the given <tt>target_index</tt>, and return the new
     * Edge's index.  This method delegates to {@link #createEdge( int, int,
     * boolean )} with a true <tt>directed</tt> argument, except when the source
     * and target indices are the same (self-edges are never directed).
     * @param source_index the index in this RootGraph of the source of the new
     * directed Edge
     * @param target_index the index in this RootGraph of the target of the new
     * directed Edge
     * @return the index of the newly created Edge.
     */
    public int createEdge ( int source_index, int target_index )
    {
        return createEdge(source_index, target_index, true);
    }
 
    /**
     * Create an Edge from the Node with the given <tt>source_index</tt> to the
     * Node with the given <tt>target_index</tt>, and return the new Edge's
     * index.  The newly created Edge will be directed iff the boolean argument
     * is true.
     * @param source_index the index in this RootGraph of the source of the new
     * Edge
     * @param target_index the index in this RootGraph of the target of the new
     * Edge
     * @param directed The new Edge will be directed iff this argument is true.
     * @return the index of the newly created Edge.
     */
    public int createEdge (int source_index,
                           int target_index,
                           boolean directed
                           )
    {
        /*
        assertNodeExists(source_index, "createEdge:source node");
        assertNodeExists(target_index, "createEdge:target node");

        List s = _adjMap.get(source_index);
        */
        int e = _getNextEdgeIndex();
        /*
        EdgeStructure es = createEdgeStructure(source_index, target_index,
                                               e, directed);
        _edges.put(e, es);
        
        s.add( es );

        if(!directed)
        {
            List t = _adjMap.get(target_index);
            t.add( es );
        }
        */
        return e;
    }

    /**
     * Create some Edges.	 The indices of the sources and the targets are given
     * in two arrays, which must be of equal length, and the indices of the
     * created edges are returned in an array of the same length.	 The newly
     * created Edges will be directed iff the boolean argument is true.
     * @param source_indices an array of the indices of the source Nodes for the
     * new Edges.
     * @param target_indices an array of length <tt>source_indices.length</tt> of
     * the indices of the target Nodes for the new Edges.
     * @param directed The new Edges will be directed iff this argument is true.
     * @return an array of length <tt>source_indices.length</tt> of the indices
     * of the newly created Edges.
     */
    public int[] createEdges (int[] source_indices, int[] target_indices,
                              boolean directed
                              )
    {
        if(source_indices.length != target_indices.length)
        {
            throw new IllegalArgumentException ("createEdges([],[]): arrays not the same length");
        }

        int[] edges = new int[source_indices.length];
        
        for(int x=0; x < source_indices.length; x++)
        {
            edges[x] = createEdge(source_indices[x], target_indices[x], directed);
        }

        return edges;
    }
 

    public int[] neighborsArray(int nodeIndex)
    {
        throw new UnsupportedOperationException();
        
        /*
        assertNodeExists(nodeIndex);
        List l = _adjMap.get(nodeIndex);

        int[] neb = new int[l.size()];

        for(int x=0; x < l.size(); x++)
        {
            neb[x] = ((EdgeStructure) l.get(x)).getTarget();
        }

        return neb;
        */
    }
 
 
    /**
     * Determine if there are any Edges between the two Nodes with the given
     * indices.
     * @return true iff the Nodes with the given indices are neighbors.
     */
    public boolean isNeighbor ( int a_node_index, int another_node_index )
    {
        assertNodeExists(a_node_index, "isNeighbor:a_node_index");
        assertNodeExists(another_node_index, "isNeighbor:another_node_index");

        List n1 = _adjMap.get(a_node_index);
        List n2 = _adjMap.get(another_node_index);

        // sorted adjacency lists would be faster...
        // this is O( n1.size() + n2.size() )
        
        for(int x=0; x < n1.size(); x++)
        {
            if( another_node_index == ((EdgeStructure) n1.get(x)).getTarget())
            {
                return true;
            }
        }

        for(int x=0; x < n2.size(); x++)
        {
            if( a_node_index == ((EdgeStructure) n2.get(x)).getTarget())
            {
                return true;
            }
        }

        
        return false;
    }
 
 
    /**
     * Determine if there are any Edges from the Node with the first of the given
     * indices to the Node with the second of the given indices.
     * @param from_node_index the index of the Node to find edges from.
     * @param to_node_index the index of the Node to find edges to.
     * @return true iff there is at least one Edge with source index
     * <tt>from_node_index</tt> and target index <tt>to_node_index</tt>.
     */
    public boolean edgeExists ( int from_node_index, int to_node_index )
    {
        assertNodeExists(from_node_index, "edgeExists: from_node_index");
        assertNodeExists(to_node_index, "edgeExists: to_node_index");

        List n1 = _adjMap.get(from_node_index);

        // sorted adjacency lists would be faster...
        // this is O( n1.size() )
        
        for(int x=0; x < n1.size(); x++)
        {
            if( to_node_index == ((EdgeStructure) n1.get(x)).getTarget())
            {
                return true;
            }
        }
        return false;
    }
  
    /**
     * Count the number of edges from the Node with index <tt>from_index</tt> to
     * the Node with index <tt>to_index</tt> (where this.getIndex( to_node ) ==
     * to_index).	 Note that if count_undirected_edges is false, any Edge
     * <tt><i>e</i></tt> such that <tt><i>e</i>.isDirected() == false</tt> will
     * not be included in the count.
     * @param from_node_index the index of the Node to count edges from.
     * @param to_node_index the index of the Node to find edges to.
     * @param count_undirected_edges Undirected edges will be included in the
     * count iff count_undirected_edges is true.
     * @return the number of Edges from the Node with index <tt>from_index</tt>
     * to the Node with index <tt>to_index</tt>.
     */
    public int getEdgeCount (int from_node_index,
                             int to_node_index,
                             boolean count_undirected_edges
                             )
    {
        assertNodeExists(from_node_index, "getEdgeCount: from_node_index");
        assertNodeExists(to_node_index, "getEdgeCount: to_node_index");

        List n1 = _adjMap.get(from_node_index);

        // sorted adjacency lists would be faster...
        // this is O( n1.size() )

        int ct = 0;
        
        for(int x=0; x < n1.size(); x++)
        {
            EdgeStructure es = (EdgeStructure) n1.get(x);

            if(! (!count_undirected_edges && es.isUndirected()))
            {
                if( to_node_index == es.getTarget())
                {
                    ct += 1;
                }
            }
        }

        return ct;
    }

    /**
     * Returns all Adjacent Edges to the given node.
     *
     * @param node_index the index of the node
     * @param include_undirected_edges should we include undirected edges, 
     *				     if true will also return self-edges
     * @param incoming_edges Include incoming edges
     * @param outgoing_edges Include outgoing edges
     */
    public int[] getAdjacentEdgeIndicesArray (int node_index,
                                              boolean include_undirected_edges,
                                              boolean include_incoming_edges,
                                              boolean include_outgoing_edges)
    {
        assertNodeExists(node_index, "getAdjacentEdgeIndiciesArray:node_index");
        
        if(! (include_undirected_edges &&
              !include_incoming_edges &&
              include_outgoing_edges))
        {
            throw new UnsupportedOperationException("incoming edges not supported");
        }
        
        List n1 = _adjMap.get(node_index);

        //IntArrayList edges = new IntArrayList(n1.size());
        int[] edges = new int[n1.size()];
        
        for(int x=0; x < n1.size(); x++)
        {
            edges[x] = ((EdgeStructure) n1.get(x)).getEdgeIndex();
        }

        return edges;
    }

    
    /**
     * Retrieve the index of the Node that is the source of the Edge with the
     * given index.  Note that if the edge is undirected, the edge also connects
     * the target to the source.
     * @param edge_index the index in this RootGraph of the Edge
     * @return the index in this RootGraph of the Edge's source Node
     */
    public int getEdgeSourceIndex ( int edge_index )
    {
        assertEdgeExists(edge_index, "getEdgeSourceIndex: " + edge_index);
        
        EdgeStructure es = (EdgeStructure) _edges.get(edge_index);

        return es.getSource();
    }
 
    /**
     * Retrieve the index of the Node that is the target of the Edge with the
     * given index.  Note that if the edge is undirected, the edge also connects
     * the target to the source.
     * @param edge_index the index in this RootGraph of the Edge
     * @return the index in this RootGraph of the Edge's target Node
     */
    public int getEdgeTargetIndex ( int edge_index )
    {
        assertEdgeExists(edge_index,  "getEdgeTargetIndex: " + edge_index);
        
        EdgeStructure es = (EdgeStructure) _edges.get(edge_index);

        return es.getTarget();
    }
 
 

    /**
     * Return an array of the indices of all Edges from the Node with the first
     * given index to the Node with the second given index.
     * @param from_node_index the index of the Node to return edges from.
     * @param to_node_index the index of the Node to return edges to.
     * @param include_undirected_edges Undirected edges will be included in the
     * array iff include_undirected_edges is true.
     * @return an array of the Edges from the Node corresponding to
     * <tt>from_node_index</tt> to the Node corresponding to
     * <tt>to_node_index</tt>, or null if none exist.
     */
    public int[] getEdgeIndicesArray (int from_node_index,
                                      int to_node_index,
                                      boolean include_undirected_edges
                                      )
    {
        throw new UnsupportedOperationException();
    }


 
    /**
     * Remove the Node with the given index (and all of that Node's incident
     * Edges) from this RootGraph and all of its GraphPerspectives.  This delegates
     * to {@link #removeNodes( index[] )}.
     * @param node_index The index in this RootGraph of the Node to remove.
     * @return The index of the removed Node, or 0 if the given index is 0 or if
     * the node was already removed.
     */
    public int removeNode ( int node_index ) {  throw new UnsupportedOperationException();}

    
    /**
     * Remove the Nodes with the given indices (and all of those Nodes' incident
     * Edges) from this RootGraph and all of its GraphPerspectives.  This clears
     * the row and column of the coltNodeData matrix at each of the given
     * indices and then calls {@link #hideAllIncidentEdges( Node[], Edge[][],
     * ChangeEvent )} with those nodes that are not already
     * removed, and then, after firing the event, returns an array of equal length
     * to the one given, in which each corresponding position is either the same
     * as in the argument array or is 0, indicating that the node with that index
     * was already removed.
     * @param node_indices An int array of the indices in this RootGraph of the
     * Nodes to remove.
     * @return An int array of equal length to the argument array, and with equal
     * values except at positions that in the input array contain indices
     * corresponding to Nodes that were already removed; at these positions the
     * result array will contain the value 0.
     */
    public int[] removeNodes ( int[] node_indices ) {  throw new UnsupportedOperationException();}
 

 
    /**
     * Remove the Edge with the given index from this RootGraph and all of its
     * GraphPerspectives.	 This delegates to {@link #removeEdges( index[] )}.
     * @param edge_index The index in this RootGraph of the Edge to remove.
     * @return The index of the removed Edge, or 0 if the given index is 0 or if
     * the edge has already been removed.
     */
    public int removeEdge ( int edge_index ) {  throw new UnsupportedOperationException();}

 
    /**
     * Remove the Edges with the given indices from this RootGraph and all of its
     * GraphPerspectives.	 This returns an array of equal length to the one
     * given, in which each corresponding position is either the same as in the
     * argument array or is 0, indicating that the edge with that index was
     * already removed.
     * @param edge_indices An int array of the indices in this RootGraph of the
     * Edges to remove.
     * @return An int array of equal length to the argument array, and with equal
     * values except at positions that in the input array contain indices
     * corresponding to Edges that were already removed; at these positions the
     * result array will contain the value 0.
     */
    public int[] removeEdges ( int[] edge_indices ) {  throw new UnsupportedOperationException();}
 

    
    public int getIndex ( Edge edge ) {  throw new UnsupportedOperationException();}
    /**
     * Retrieve the directedness of the Edge with the
     * given index.  Note that if the edge is undirected, the edge also connects
     * the target to the source.
     * @param edge_index the index in this RootGraph of the Edge
     * @return true iff the edge is directed
     */
    public boolean isEdgeDirected ( int edge_index ) {  throw new UnsupportedOperationException();}


    /**
     *
     * Unimplemented methods of the interface
     * /
    
     /**
     * Return the Node with the given index in this RootGraph.  All indices are
     * <= -1.  Some indices may correspond to no node, but no index may
     * correspond to multiple nodes.  The index of a Node will not change for the
     * lifetime of the Node and its RootGraph.
     * @param node_index the index in this RootGraph of to find a corresponding
     * Node for.
     * @return the Node with the given index in this RootGraph, or null if the
     * index is 0 or if there is no Node with the given index.
     */
    // TODO: Add to interface
    public Node getNode ( int node_index ) {  throw new UnsupportedOperationException();}
 
    /**
     * Return the Edge with the given index in this RootGraph.  All indices are
     * <= -1.  Some indices may correspond to no edge, but no index may
     * correspond to multiple edges.  The index of a Edge will not change for the
     * lifetime of the Edge and its RootGraph.
     * @param edge_index the index in this RootGraph of to find a corresponding
     * Edge for.
     * @return the Edge with the given index in this RootGraph, or null if the
     * index is 0 or if there is no Edge with the given index.
     */
    // TODO: Add to interface
    public Edge getEdge ( int edge_index ) {  throw new UnsupportedOperationException();}


    /**
     * @return an Iterator over the Nodes in this graph.
     */
    public Iterator nodesIterator (){  throw new UnsupportedOperationException();}
 
    /**
     * @return an Iterator over the Edges in this graph.
     */
    public Iterator edgesIterator ()
    {	 throw new UnsupportedOperationException();}

    public boolean isNeighbor ( Node a_node, Node another_node ) {  throw new UnsupportedOperationException();}

 
    public List edgesList (int from_node_index,
                           int to_node_index,
                           boolean include_undirected_edges
                           ) {  throw new UnsupportedOperationException();}

    public int getEdgeCount ( Node from,
                              Node to,
                              boolean count_undirected_edges
                              ) {	 throw new UnsupportedOperationException();}

    public boolean edgeExists ( Node from, Node to ) {  throw new UnsupportedOperationException();}

    public List edgesList ( Node from, Node to ) {  throw new UnsupportedOperationException();}

    public boolean containsNode ( Node node ) {  throw new UnsupportedOperationException();}
 
    public boolean containsEdge ( Edge edge ) {  throw new UnsupportedOperationException();}
 
    public List neighborsList ( Node node ) {  throw new UnsupportedOperationException();}


    public List nodesList ()
    {	 throw new UnsupportedOperationException();}
 
    public List edgesList () {  throw new UnsupportedOperationException();}
    public Node removeNode ( Node node ) {  throw new UnsupportedOperationException();}
    public List removeNodes ( List nodes ) {  throw new UnsupportedOperationException();}
 
    public int createNode ( Node[] nodes, Edge[] edges ) {  throw new UnsupportedOperationException();}
 
    public int createNode ( GraphPerspective perspective ) {  throw new UnsupportedOperationException();}

    public Edge removeEdge ( Edge edge ) {  throw new UnsupportedOperationException();}
    
    public List removeEdges ( List edges ) {  throw new UnsupportedOperationException();}

    public int createEdge ( Node source, Node target ) {	throw new UnsupportedOperationException();}
 
    public int createEdge ( Node source, Node target, boolean directed ) {  throw new UnsupportedOperationException();}
 
    
    public int getInDegree ( Node node ) {  throw new UnsupportedOperationException();}
 

    public int getInDegree ( int node_index ) {  throw new UnsupportedOperationException();}
 

    public int getInDegree ( Node node, boolean count_undirected_edges ) {  throw new UnsupportedOperationException();}
 

    public int getInDegree ( int node_index, boolean count_undirected_edges ) {  throw new UnsupportedOperationException();}

    public int getOutDegree ( Node node ) {  throw new UnsupportedOperationException();}
 

    public int getOutDegree ( int node_index ) {	throw new UnsupportedOperationException();}
 

    public int getOutDegree ( Node node, boolean count_undirected_edges ) {  throw new UnsupportedOperationException();}
 

    public int getOutDegree ( int node_index, boolean count_undirected_edges ) {	throw new UnsupportedOperationException();}
 
    public int getDegree ( Node node ) {	throw new UnsupportedOperationException();}
 

    public int getDegree ( int node_index ) {  throw new UnsupportedOperationException();}
 
    public int getIndex ( Node node ) {  throw new UnsupportedOperationException();}
 
    
    public GraphPerspective createGraphPerspective (
                                                    Node[] nodes,
                                                    Edge[] edges
                                                    )
    {
	throw new UnsupportedOperationException();
    }
  
    public GraphPerspective createGraphPerspective (
                                                    int[] node_indices,
                                                    int[] edge_indices
                                                    )
    {
	throw new UnsupportedOperationException();
    }


    public boolean addMetaChild ( Node parent, Node child )
    {
	throw new UnsupportedOperationException();
    }

    public boolean addNodeMetaChild ( int parent_index, int child_node_index )
    {
	throw new UnsupportedOperationException();
    }

    public boolean isMetaParent ( Node child, Node parent )
    {
	throw new UnsupportedOperationException();
    }

    public boolean isNodeMetaParent ( int child_node_index, int parent_index )
    {
	throw new UnsupportedOperationException();
    }

    public List metaParentsList ( Node node )
    {
	throw new UnsupportedOperationException();
    }

    public List nodeMetaParentsList ( int node_index )
    {
	throw new UnsupportedOperationException();
    }
 
    public int[] getNodeMetaParentIndicesArray ( int node_index )
    {
	throw new UnsupportedOperationException();
    }
 
    public boolean isMetaChild ( Node parent, Node child )
    {
	throw new UnsupportedOperationException();
    }
 
    public boolean isNodeMetaChild ( int parent_index, int child_node_index )
    {
	throw new UnsupportedOperationException();
    }

    public boolean isNodeMetaChild ( int parent_index, int child_node_index, boolean recursive)
    {
	throw new UnsupportedOperationException();
    }

    public List nodeMetaChildrenList ( Node node )
    {
	throw new UnsupportedOperationException();
    }

    public List nodeMetaChildrenList ( int parent_index )
    {
	throw new UnsupportedOperationException();
    }

    public int[] getNodeMetaChildIndicesArray ( int node_index )
    {
	throw new UnsupportedOperationException();
    }

    public int[] getNodeMetaChildIndicesArray ( int node_index, boolean recursive )
    {
	throw new UnsupportedOperationException();
    }

    public int[] getChildlessMetaDescendants ( int node_index )
    {
	throw new UnsupportedOperationException();
    }

    public boolean addMetaChild ( Node parent, Edge child )
    {
	throw new UnsupportedOperationException();
    }

    public boolean addEdgeMetaChild ( int parent_index, int child_edge_index )
    {
	throw new UnsupportedOperationException();
    }

    public boolean isMetaParent ( Edge child, Node parent )
    {
	throw new UnsupportedOperationException();
    }

    public boolean isEdgeMetaParent ( int child_edge_index, int parent_index )
    {
	throw new UnsupportedOperationException();
    }

    public List metaParentsList ( Edge edge )
    {
	throw new UnsupportedOperationException();
    }

    public List edgeMetaParentsList ( int edge_index )
    {
	throw new UnsupportedOperationException();
    }

    public int[] getEdgeMetaParentIndicesArray ( int edge_index )
    {
	throw new UnsupportedOperationException();
    }

 
    public boolean isMetaChild ( Node parent, Edge child )
    {
	throw new UnsupportedOperationException();
    }

 
    public boolean isEdgeMetaChild ( int parent_index, int child_edge_index )
    {
	throw new UnsupportedOperationException();
    }


    public List edgeMetaChildrenList ( Node node )
    {
	throw new UnsupportedOperationException();
    }

    public List edgeMetaChildrenList ( int node_index )
    {
	throw new UnsupportedOperationException();
    }

 
    public int[] getEdgeMetaChildIndicesArray ( int node_index )
    {
	throw new UnsupportedOperationException();
    }

    
}  

