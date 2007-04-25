package csplugins.layout.algorithms;

import giny.model.*;
import cern.colt.list.*;
import cern.colt.map.*;
import java.util.*;

public abstract class SGraphPartition
{
  // private constants
  private static final int m_NODE_HAS_NOT_BEEN_SEEN = 0;
  private static final int m_NODE_HAS_BEEN_SEEN     = 1;

  /**
    * Creates a list of partitions of a GraphPerspective.
    *
    * This method returns a list of partitions of the nodes and edges specified
    * by a GraphPerspective. A partition is:
    * For all nodes "x" in a partition, for all nodes "y" in a partition, where
    * "x" != "y", there exists a set of edges that ultimately connects "x"
    * to "y". Or stated simply, a partition is a set of nodes that are
    * connected to each other. The partition() method provides a way
    * to separate all the unconnected set of nodes. This method implements
    * an algorithm that has an order of n time complexity (O(n)), where n is
    * the number of nodes and edges.
    *
    * @param _perspective The GraphPerspective containing all the nodes 
    *                     to partition.
    * @return             A List of int[]'s. The elements of each int[] in
    *                     the List are Root Graph's indicies of the nodes
    *                     in the partition.
    */
  public static List partition(GraphPerspective _perspective)
  {
    // partitions stores the list of partitions
    ArrayList partitions = new ArrayList();

    // define an iterator over all nodes in the graph
    Iterator nodeIter = _perspective.nodesIterator();

    // nodesSeenMap is a hash map where each key in the map is a node index.
    // Each value specifies whether the node has been seen or not.
    OpenIntIntHashMap nodesSeenMap = new OpenIntIntHashMap(_perspective.getNodeCount());

    // Initialize the nodesSeenMap so each node has not been seen.
    while (nodeIter.hasNext())
    {
      int node = ((Node)nodeIter.next()).getRootGraphIndex();
      nodesSeenMap.put(node, m_NODE_HAS_NOT_BEEN_SEEN);
    }

    nodeIter = _perspective.nodesIterator();
    while (nodeIter.hasNext())
    {
      // Get this nodes index from the root graph
      int nodeIndex = ((Node)nodeIter.next()).getRootGraphIndex();

      // If we've seen this node, skip it
      if (nodesSeenMap.get(nodeIndex) == m_NODE_HAS_BEEN_SEEN) continue;

      // We haven't seen this node yet, so start a new partition...

      // partitionList holds a list of node indicies of the partition
      IntArrayList partitionList = new IntArrayList();

      // Mark this node as having been seen
      nodesSeenMap.put(nodeIndex, m_NODE_HAS_BEEN_SEEN);

      // Begin traversing through each connected node to this node
      traverse(_perspective, nodesSeenMap, nodeIndex, partitionList);

      // Trim off any excess elements in the IntArrayList
      partitionList.trimToSize();

      // Get the int[] of partitionList and add it to partitions
      partitions.add(partitionList.elements());

    } // end for (int i = 0; i < nodesArray.length; i++)

    // Sort each partition based on the partition's length
    Object parts[] = partitions.toArray();
    Arrays.sort(parts, new Comparator()
      {
        public int compare(Object o1, Object o2)
        {
          int list1[] = (int[]) o1;
          int list2[] = (int[]) o2;

          return (list2.length - list1.length);
        } // end compare()

        public boolean equals(Object obj)
        {
          return false;
        } // end equals()

      } // end new Comparator()

    ); // end Arrays.sort

    // Return the sorted list of partitions
    return Arrays.asList(parts);
  } // end partition(GraphPerspective _perspective)

  /**
    * This method traverses nodes connected to the specified node.
    * @param _perspective   The GraphPerspective that holds all the nodes.
    * @param _nodesSeenMap  A map that specifies which nodes have been seen.
    * @param _nodeIndex     Index of the node to search for connected nodes.
    * @param _partitionList The int array list that holds all the node indicies
    *                       in the partition.
    */
  private static void traverse(GraphPerspective  _perspective,
                               OpenIntIntHashMap _nodesSeenMap,
                               int _nodeIndex, IntArrayList _partitionList)
  {
    // add this node's index to _partitionList
    _partitionList.add(_nodeIndex);

    // Get the Node object that corresponds to this node's index (_nodeIndex)
    Node currentNode = _perspective.getNode(_nodeIndex);

    // Get a list of edges connected to this node
    int incidentEdges[] = _perspective.getAdjacentEdgeIndicesArray(_nodeIndex,
                                     true, true, true);

    // Iterate through each connected edge
    for (int i = 0; i < incidentEdges.length; i++)
    {
      // This edge's index is determined by incidentEdges[i].

      // incidentEdge holds the Edge object that correponds to this edge's
      // index.
      Edge incidentEdge = _perspective.getEdge(incidentEdges[i]);

      // Determine the node's index that is connected to _nodeIndex
      int incidentNodeIndex;

      if (incidentEdge.getSource() == currentNode)
      {
        incidentNodeIndex = _perspective.getIndex(incidentEdge.getTarget());
      }
      else
      {
        incidentNodeIndex = _perspective.getIndex(incidentEdge.getSource());
      }

      // If we haven't seen the connected note yet...
      if (_nodesSeenMap.get(incidentNodeIndex) == m_NODE_HAS_NOT_BEEN_SEEN)
      {
        // Mark the connected node as having been seen
        _nodesSeenMap.put(incidentNodeIndex, m_NODE_HAS_BEEN_SEEN);

        // Begin traversing through the connected node
        traverse(_perspective, _nodesSeenMap,
                 incidentNodeIndex, _partitionList);

      } // end if (connected node has not been seen)

    } // end for (int i = 0; i < incidentEdges.length; i++)

  } // end traverse

} // end class SGraphPartition
