/*
  Copyright (c) 2005, Nerius Landys
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package cytoscape.graph.fixed;

import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;

/**
 * A graph whose topology cannot be modified.
 * Edges and nodes are non-negative integers; a given node and a given edge
 * in a single graph can be the same integer.
 */
public interface FixedGraph
{

  /**
   * Returns an enumeration of all nodes in this graph.  Every node in this
   * graph is a unique non-negative integer.  A given node and a given edge
   * in one graph may be the same integer.
   *
   * @return an enumeration over all nodes in this graph; null is never
   *   returned.
   */
  public IntEnumerator nodes();

  /**
   * Returns an enumeration of all edges in this graph.  Every edge in this
   * graph is a unique non-negative integer.  A given node and a given edge
   * in one graph may be the same integer.
   *
   * @return an enumeration over all edges in this graph; null is never
   *   returned.
   */
  public IntEnumerator edges();

  /**
   * Determines whether or not a node exists in this graph.
   * Returns true if and only if the node specified exists.<p>
   * Note that this method is superfluous in this interface (that is,
   * it could be removed without losing any functionality), because
   * edgesAdjacent(int, boolean, boolean, boolean) can be used to test
   * the presence of a node.  However, because nodeExists(int) does not
   * return a complicated object, its performance may be better
   * than that of edgesAdjacent().
   *
   * @param node the [potentially existing] node in this graph whose existence
   *   we're querying.
   * @return the existence of specified node in this graph.
   */
  public boolean nodeExists(int node);

  /**
   * Determines the existence and directedness of an edge.
   * Returns -1 if specified edge does not exist in this graph,
   * returns 1 if specified edge is directed, and returns 0 if specified edge
   * is undirected.
   *
   * @param edge the edge in this graph whose existence and/or
   *   directedness we're seeking.
   * @return 1 if specified edge is directed, 0 if specified edge is
   *   undirected, and -1 if specified edge does not exist in this graph.
   */
  public byte edgeType(int edge);

  /**
   * Determines the source node of an edge.
   * Returns the source node of specified edge or -1 if specified edge does
   * not exist in this graph.
   *
   * @param edge the edge in this graph whose source node we're seeking.
   * @return the source node of specified edge or -1 if specified edge does
   *   not exist in this graph.
   */
  public int edgeSource(int edge);

  /**
   * Determines the target node of an edge.
   * Returns the target node of specified edge or -1 if specified edge does
   * not exist in this graph.
   *
   * @param edge the edge in this graph whose target node we're seeking.
   * @return the target node of specified edge or -1 if specified edge does
   *   not exist in this graph.
   */
  public int edgeTarget(int edge);

  /**
   * Returns a non-repeating enumeration of edges adjacent to a node.
   * The three boolean input parameters define what is meant by "adjacent
   * edge".  If all three boolean input parameters are false, the returned
   * enumeration will have zero elements.<p>
   * This method returns null if and only if the specified node does not
   * exist in this graph.  Therefore, this method can be used to test
   * the existence of a node in this graph.
   *
   * @param node the node in this graph whose adjacent edges we're seeking.
   * @param outgoing all directed edges whose source is the node specified
   *   are included in the returned enumeration if this value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @param incoming all directed edges whose target is the node specified
   *   are included in the returned enumeration if this value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @param undirected all undirected edges touching the specified node
   *   are included in the returned enumeration if this value is true;
   *   otherwise, not a single such edge is included in the returned
   *   enumeration.
   * @return an enumeration of edges adjacent to the node specified
   *   or null if specified node does not exist in this graph.
   */
  public IntEnumerator edgesAdjacent(int node, boolean outgoing,
                                     boolean incoming, boolean undirected);

  /**
   * Returns a non-repeating iteration of edges connecting two nodes.
   * The three boolean input parameters define what is meant by "connecting
   * edge".  If all three boolean input parameters are false, the returned
   * iteration will have no elements.<p>
   * I'd like to discuss the motivation behind this interface method.
   * I assume that most implementations of this interface will implement
   * this method in terms of edgesAdjacent().  Why, then, is this method
   * necessary?  Because some implementations may choose to optimize the
   * implementation of this method by using a binary search tree or a
   * hashtable, for example.  This method is a hook to provide such
   * optimization.<p>
   * This method returns an IntIterator as opposed to an IntEnumerator
   * so that non-optimized implementations would not be required to
   * pre-compute the number of edges being returned.
   *
   * @param node0 one of the nodes in this graph whose connecting edges
   *   we're seeking.
   * @param node1 one of the nodes in this graph whose connecting edges
   *   we're seeking.
   * @param outgoing all directed edges whose source is node0 and whose
   *   target is node1 are included in the returned iteration if this value
   *   is true; otherwise, not a single such edge is included in the returned
   *   iteration.
   * @param incoming all directed edges whose source is node1 and whose
   *   target is node0 are included in the returned iteration if this value
   *   is true; otherwise, not a single such edge is included in the returned
   *   iteration.
   * @param undirected all undirected edges E such that E's endpoints
   *   are node0 and node1 are included in the returned iteration if this
   *   value is true; otherwise, not a single such edge is incuded in the
   *   returned iteration.
   * @return an iteration of edges connecting node0 with node1 in a fashion
   *   specified by boolean input parameters or null if either of node0 or
   *   node1 does not exist in this graph.
   * @deprecated This method may go away at some point; please use
   *   edgesAdjacent(int, boolean, boolean, boolean) where it is reasonable
   *   to do so.
   * @see #edgesAdjacent(int, boolean, boolean, boolean)
   */
  public IntIterator edgesConnecting(int node0, int node1,
                                     boolean outgoing, boolean incoming,
                                     boolean undirected);

}
