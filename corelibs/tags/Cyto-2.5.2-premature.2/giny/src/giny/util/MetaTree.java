package giny.util;

import giny.model.*;
import java.util.*;
import cern.colt.list.*;
import cern.colt.map.*;

/**
 * A MetaTree provides a view onto the MetaEdges of a 
 * GraphPerspective.  A MetaTree is defined by a GraphPerspective and 
 * a root node.
 *
 * Note that not all of the nodes in a GraphPerspective will be accesable 
 * via the MetaTree.  It is possible to have a forest of MetaTrees for 
 * one GraphPerspective.
 *
 * Remember that a Tree is essentially a directed acyclic graph.  This class 
 * has methods to check this, and greedy methods that will enforce it.
 */
/** @deprecated Yell loudly (cytoscape-discuss@googlegroups.com) if you want to keep this.
    If we do keep it, it will move somewhere else.  If no one yells, it'll be removed 10/2007 */
public class MetaTree {

  /**
   * This is the Root node of this Tree.
   */
  private int root;

  /**
   * This is the perspective that this tree is a view on.
   */
  private GraphPerspective perspective;

  /**
   * This Hash will hold
  private 
  */


}
