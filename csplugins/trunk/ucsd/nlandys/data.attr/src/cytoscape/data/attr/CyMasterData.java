package cytoscape.data.attr;

/**
 * This API is one that is used internally by Cytoscape's implementation.
 * Functionality here includes the ability to register a node's or edge's
 * name.  It is Cytoscape's job to keep the state of this data object
 * in sync with other data objects that Cytoscape uses - for example,
 * the master RootGraph object.<p>
 * Mappings between data structures defined in this package and persistent
 * file storage formats are outside of the scope of the capabilities this
 * package provides; "attribute file" reading and writing sits at an orthogonal
 * abstraction layer, and it is Cytoscape's job to glue the file reading and
 * writing modules with the modules in this package.
 */
public interface CyMasterData
{

  /**
   * Cytoscape needs to maintain a consistent state between nodes registered
   * by data "registries" of this pacakge and other data structures that
   * Cytoscape uses, such as a RootGraph.<p>
   * Let me state a scenario describing how I envision that Cytoscape is
   * implemented.  Cytoscape has the ability to load files which specify a
   * graph topology (nodes and edges connecting nodes).  These files are
   * written by a human.  The syntax in such a file needs a mechanism
   * by which to refer to a node, because a reference to a node needs to
   * exist in order to be able to specify an edge having a node as an
   * endpoint.  For example, the file's syntax, loosely stated, is:
   * <blockquote><pre>
   * create node "foo".
   * create node "bar".
   * create edge "baz" whose target is "foo" and whose source is "bar".
   * </pre></blockquote>
   * Likewise, a file defining attributes will have information such as
   * the following:
   * <blockquote><pre>
   * for node "foo", assign "color" to be "green".
   * for node "bar", assign "color" to be "blue".
   * </pre></blockquote>
   * Therefore, we need some notion of human-readable, persistent (meaning
   * not varying from day-to-day usage of Cytoscape) identifier for a node.
   * The nodeName parameter used in this method is exactly trying to
   * satisfy this need.  The goal of this method is to glue together RootGraph
   * indices of nodes (nodeInx) with human-specified names of nodes
   * (nodeName).  Note that RootGraph indices can be chosen differently
   * from runtime to runtime by the RootGraph engine.  Therefore, it does
   * not make sense to remember the information specified in this method
   * in a persistent manner - this information should be assembled and torn
   * down with every session of Cytoscape.
   * @param nodeInx an "index" of node to register; in all cases that I can
   *   think of the value specified here will be the RootGraph index of a
   *   newly created node.
   * @param nodeName a human-readable node name ("canonical name") that is
   *   presumably defined by the user of Cytoscape; it is this identifier of
   *   a node that a user uses to refer to a node, for example in defining
   *   files specifying the topology of a network or in defining node
   *   attribute values.
   */
  public void registerNode(int nodeInx, String nodeName);

}
