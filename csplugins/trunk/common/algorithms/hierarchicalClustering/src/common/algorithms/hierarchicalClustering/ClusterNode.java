/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package common.algorithms.hierarchicalClustering;

import javax.swing.tree.DefaultMutableTreeNode;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Comparator;

/**
 * A ClusterNode is a MutableTreeNode.  If it is not a leaf then it has at
 * least two children.  All of its children are equidistant from each other,
 * according to some externally defined, double-valued metric.
 * <br>
 * A ClusterNode supports 3 different views on its children:
 * <ol>
 * <li>The left branch is at index 0, the right branch is at index (getChildCount() - 1)</li>
 * <li>The outside branch is the left branch if <tt>orientation</tt> is LEFT_FACE_OUTSIDE or the right branch if <tt>orientation</tt> is RIGHT_FACE_OUTSIDE or null if <tt>orientation</tt> is UNDETERMINED.  The inside branch is the right branch if the outside branch is the left branch, and vice versa.</li>
 * <li>The first branch is the outside branch if {@link #isOutFirst} returns true, or the inside branch otherwise.
 * </ol>
 * A ClusterNode's &quot;faces&quot; are its outside leaves.  A ClusterNode
 * with UNDETERMINED orientation may faces contributed by both its left and
 * right branches, but a ClusterNode with LEFT_FACE_OUTSIDE orientation should
 * have the same faces as its left branch.  A leaf node has one face, and that
 * is itself.
 * @see #getDistanceBetweenChildren
 * @see #setDistanceBetweenChildren
 */
public class ClusterNode
  extends DefaultMutableTreeNode {

  // ClusterNode orientations:
  public static final int UNDETERMINED = -1;
  public static final int LEFT_BRANCH_OUTSIDE = 0;
  public static final int RIGHT_BRANCH_OUTSIDE = 1;

  // Branch types:
  public static final int LEFT = 0;
  public static final int RIGHT = 1;
  public static final int OUTSIDE = 2;
  public static final int INSIDE = 3;
  public static final int FIRST = 4;
  public static final int LAST = 5;

  public static final Comparator
    ASCENDING_DISTANCE_BETWEEN_CHILDREN_COMPARATOR =
    new AscendingDistanceBetweenChildrenComparator();

  protected double distanceBetweenChildren = 0.0;
  protected int orientation;
  protected NodeFaces leftFaces;
  protected NodeFaces rightFaces;
  protected boolean outFirst;

  public ClusterNode ( Object node_peer ) {
    super();
    setUserObject( node_peer );
    initializeClusterNode();
  }

  public ClusterNode ( Object node_peer, boolean is_leaf ) {
    super();
    setUserObject( node_peer );
    setAllowsChildren( !is_leaf );
    initializeClusterNode();
  }

  protected void initializeClusterNode () {
    orientation = UNDETERMINED;
    leftFaces = null;
    rightFaces = null;
    outFirst = true;
  }

  public void setDistanceBetweenChildren ( double new_distance ) {
    distanceBetweenChildren = new_distance;
  }

  public double getDistanceBetweenChildren () {
    return distanceBetweenChildren;
  }

  public void setOrientation ( int new_orientation ) {
    orientation = new_orientation;
  }

  public int getOrientation () {
    return orientation;
  }

  public void setOutFirst ( boolean new_out_first ) {
    outFirst = new_out_first;
  }

  public boolean isOutFirst () {
    return outFirst;
  }

  public ClusterNode getBranch ( int branch_type ) {
    switch( branch_type ) {
    case LEFT:
      return getLeftBranch();
    case RIGHT:
      return getRightBranch();
    case OUTSIDE:
      return getOutsideBranch();
    case INSIDE:
      return getInsideBranch();
    case FIRST:
      return getFirstBranch();
    case LAST:
      return getLastBranch();
    default:
      throw new IllegalArgumentException( "Unknown branch type: " + branch_type );
    }
  } // getBranch( int )

  public ClusterNode getLeftBranch () {
    if( isLeaf() ) {
      return null;
    } else {
      return ( ClusterNode )getChildAt( 0 );
    }
  } // getLeftBranch()

  public ClusterNode getRightBranch () {
    if( isLeaf() ) {
      return null;
    } else {
      int child_count = getChildCount();
      return ( ClusterNode )getChildAt( child_count - 1 );
    }
  } // getRightBranch()

  public ClusterNode getOutsideBranch () {
    if( orientation == LEFT_BRANCH_OUTSIDE ) {
      return getLeftBranch();
    } else if( orientation == RIGHT_BRANCH_OUTSIDE ) {
      return getRightBranch();
    } else { // UNDETERMINED.  Just return the left one.
      return getLeftBranch();
    }
  } // getOutsideBranch()

  public ClusterNode getInsideBranch () {
    if( orientation == LEFT_BRANCH_OUTSIDE ) {
      return getRightBranch();
    } else if( orientation == RIGHT_BRANCH_OUTSIDE ) {
      return getLeftBranch();
    } else { // UNDETERMINED.  Just return the right one.
      return getRightBranch();
    }
  } // getInsideBranch()

  public ClusterNode getFirstBranch () {
    if( isOutFirst() ) {
      return getOutsideBranch();
    } else {
      return getInsideBranch();
    }
  } // getFirstBranch()

  public ClusterNode getLastBranch () {
    if( isOutFirst() ) {
      return getInsideBranch();
    } else {
      return getOutsideBranch();
    }
  } // getLastBranch()

  public NodeFaces getFaces ( int branch_type ) {
    switch( branch_type ) {
    case LEFT:
      return getLeftFaces();
    case RIGHT:
      return getRightFaces();
    case OUTSIDE:
      return getOutsideFaces();
    case INSIDE:
      return getInsideFaces();
    case FIRST:
      return getFirstFaces();
    case LAST:
      return getLastFaces();
    default:
      throw new IllegalArgumentException( "Unknown branch type: " + branch_type );
    }
  } // getFaces( int )

  public NodeFaces getLeftFaces () {
    if( leftFaces == null ) {
      leftFaces = new NodeFaces( LEFT );
    }
    return leftFaces;
  } // getLeftFaces()

  public NodeFaces getRightFaces () {
    if( rightFaces == null ) {
      rightFaces = new NodeFaces( RIGHT );
    }
    return rightFaces;
  } // getRightFaces()

  public NodeFaces getOutsideFaces () {
    if( orientation == LEFT_BRANCH_OUTSIDE ) {
      return getLeftFaces();
    } else if( orientation == RIGHT_BRANCH_OUTSIDE ) {
      return getRightFaces();
    } else {
      return null;
    }
  } // getOutsideFaces()

  public NodeFaces getInsideFaces () {
    if( orientation == LEFT_BRANCH_OUTSIDE ) {
      return getRightFaces();
    } else if( orientation == RIGHT_BRANCH_OUTSIDE ) {
      return getLeftFaces();
    } else {
      return null;
    }
  } // getInsideFaces()

  public NodeFaces getFirstFaces () {
    if( isOutFirst() ) {
      return getOutsideFaces();
    } else {
      return getInsideFaces();
    }
  } // getFirstFaces()

  public NodeFaces getLastFaces () {
    if( isOutFirst() ) {
      return getInsideFaces();
    } else {
      return getOutsideFaces();
    }
  } // getLastFaces()

  /**
   * By default a ClusterNode is equal to any DefaultMutableTreeNode with the
   * same userObject (by equals).  This may not be the desired behavior for
   * your circumstance, so heads up.
   */
  public boolean equals ( Object other_object ) {
    return ( ( ( other_object instanceof DefaultMutableTreeNode ) &&
               ( ( ( ( DefaultMutableTreeNode )other_object ).getUserObject() ==
                   getUserObject() ) ||
                 ( ( getUserObject() != null ) &&
                   getUserObject().equals( ( ( DefaultMutableTreeNode )other_object ).getUserObject() ) ) ) ) ||
             ( ( other_object == getUserObject() ) ||
               ( ( getUserObject() != null ) &&
                 getUserObject().equals( other_object ) ) ) );
  } // equals( Object )

  public Iterator leafIterator () {
    // TODO: Modify to handle possible non-binariness of the tree.
    return new Iterator() {
        boolean started = false;
        /** parent is used while navigating the tree. */
        ClusterNode parent;
        /** node is used while navigating the tree. */
        ClusterNode node;
        /** nextNode will be returned by the call to next() */
        ClusterNode nextNode;

        public boolean hasNext () {
          if( !started ) {
            node = ClusterNode.this;
            started = true;
          }
          if( nextNode != null ) {
            return true;
          }
          if( node == null ) {
            return false;
          }
          while( ( node != null ) && !node.isLeaf() ) {
            // Descend first branches until we find a leaf.
            parent = node;
            node = ( ClusterNode )parent.getFirstBranch();
            // Since node is in the first branch of its parent, out goes first.
            node.setOutFirst( true );
          }
          // Done.
          if( node == null ) {
            return false;
          }
          // Found a leaf.  This is what will be returned by next().
          nextNode = node;
          // Now prepare for the next time around.  If node is the last branch
          // in its parent, we have to ascend and keep ascending until it is
          // the first branch in its parent.
          if( parent == null ) {
            // TODO: HERE I AM.  What is going on here?
            System.err.println("ClusterNode.leafIterator got to parent==null (?)");
            System.out.flush();
            node = null;
            return true;
          }
          while( parent.getLastBranch() == node ) {
            node = parent;
            parent = ( ClusterNode )node.getParent();
            if( ( parent == null ) || ( node == ClusterNode.this ) ) { // then node is root, and we stop.
              node = null;
              return true;
            }
          }
          // And then set node to be the last branch.
          node = ( ClusterNode )parent.getLastBranch();
          // Since node is in the last branch of its parent, out goes first.
          node.setOutFirst( false );
          return true;
        } // hasNext()

        public Object next () {
          if( ( nextNode == null ) && !hasNext() ) {
            throw new NoSuchElementException();
          }
          ClusterNode r = nextNode;
          nextNode = null;
          return r;
        } // next()

        public void remove () {
          throw new UnsupportedOperationException();
        } // remove()
      }; // anonymous Iterator
  } // leafIterator()

  /**
   * @return a new Set of ClusterNodes, each of which is a root in the forest
   * generated by setting the forest floor to the given distanceBetweenChildren
   * value.
   */
  public Set rootSet (double distance_threshold){
    Set root_set = new HashSet();
    rootSet(this,distance_threshold,root_set);
    return root_set;
  }//rootSet

  /**
   * Depth first search to find the set of roots that have distance <= distance_threshold.
   * The search is terminated early at a  node's subtree if it meets the above condition.
   */
  public static void rootSet (ClusterNode root,
                              double distance_threshold, 
                              Set root_set){
    if(root_set == null){
      root_set = new HashSet(); 
    }
    if(root == null){return;}
    if(!Double.isNaN(root.getDistanceBetweenChildren()) && 
       root.getDistanceBetweenChildren() <= distance_threshold){
      root_set.add(root);
      return;
    }else{
      int numChildren = root.getChildCount();
      for(int i = 0; i < numChildren; i++){
        rootSet((ClusterNode)root.getChildAt(i),distance_threshold,root_set);
      }//for i
    }//else
    
  }//rootSet

  public static void joinSet ( ClusterNode root,
                               int join_number,
                               Set join_set){
    if(join_set == null){
      join_set = new HashSet();
    }
    if(root == null){return;}
    
    

  }//joinSet

  public class NodeFaces {

    protected int branchType;
    // The branch represented by this NodeFaces, or null if this ClusterNode is
    // oriented such that this NodeFaces is on the inside.
    protected ClusterNode outsideBranch;
  
    public NodeFaces ( int branch_type ) {
      if( ( branch_type != LEFT ) && ( branch_type != RIGHT ) ) {
        throw new IllegalArgumentException( "The branchType of a NodeFaces may be either LEFT or RIGHT, not " 
                                            + branch_type + "." );
      }
      branchType = branch_type;
      // Here's a good time to point out that the orientation is fixed if
      // there's not >= 2 children.
      if( ClusterNode.this.isLeaf() ||
          ( ClusterNode.this.getChildCount() == 1 ) ) {
        orientation = LEFT_BRANCH_OUTSIDE;
      }
      // outsideBranch is null if this NodeFaces represents the inside branch.
      if( ClusterNode.this.isLeaf() ) {
        outsideBranch = ClusterNode.this;
      } else if( ( branchType == LEFT ) &&
                 ( orientation != RIGHT_BRANCH_OUTSIDE ) ) {
        outsideBranch = getLeftBranch();
      } else if( ( branchType == RIGHT ) &&
                 ( orientation != LEFT_BRANCH_OUTSIDE ) ) {
        outsideBranch = getRightBranch();
      }
    }

    public int size () {
      if( ClusterNode.this.isLeaf() ||
          ( ClusterNode.this.getChildCount() == 1 ) ) {
        if( branchType == LEFT ) {
          return 1;
        } else {
          return 0;
        }
      }
      // Check again that we're not on the inside.
      if( ( outsideBranch != null ) &&
          ( ( ( branchType == LEFT ) &&
              ( orientation == RIGHT_BRANCH_OUTSIDE ) ) ||
            ( ( branchType == RIGHT ) &&
              ( orientation == LEFT_BRANCH_OUTSIDE ) ) )
          ) {
        outsideBranch = null;
      }
      if( outsideBranch == null ) {
        return 0;
      }
      return ( outsideBranch.getLeftFaces().size() +
               outsideBranch.getRightFaces().size() );
    } // size()
  
    public ClusterNode get ( int index ) {
      if( ClusterNode.this.isLeaf() ) {
        if( ( index == 0 ) && ( branchType == LEFT ) ) {
          return ClusterNode.this;
        } else {
          throw new NoSuchElementException( "\"" + ClusterNode.this + "\"." + ( ( branchType == LEFT ) ? "leftFace" : "rightFace" ) + ".get( " + index + " ): ClusterNode is a leaf and either this is the not the first face or the index is out of range." );
        }
      }
      if( ClusterNode.this.getChildCount() == 1 ) {
        if( ( index == 0 ) && ( branchType == LEFT ) ) {
          return ( ClusterNode )ClusterNode.this.getChildAt( 0 );
        } else {
          throw new NoSuchElementException( "\"" + ClusterNode.this + "\"." + ( ( branchType == LEFT ) ? "leftFace" : "rightFace" ) + ".get( " + index + " ): ClusterNode has one child and either this is not the first face or the index is out of range." );
        }
      }
      // Check again that we're not on the inside.
      if( ( outsideBranch != null ) &&
          ( ( ( branchType == LEFT ) &&
              ( orientation == RIGHT_BRANCH_OUTSIDE ) ) ||
            ( ( branchType == RIGHT ) &&
              ( orientation == LEFT_BRANCH_OUTSIDE ) ) )
          ) {
        outsideBranch = null;
      }
      if( outsideBranch == null ) {
        throw new NoSuchElementException( "\"" + ClusterNode.this + "\"." + ( ( branchType == LEFT ) ? "leftFace" : "rightFace" ) + ".get( " + index + " ): outsideBranch is null.  Orientation is " + ( ( orientation == LEFT_BRANCH_OUTSIDE ) ? "LEFT_BRANCH_OUTSIDE" : ( ( orientation == RIGHT_BRANCH_OUTSIDE ) ? "RIGHT_BRANCH_OUTSIDE" : "UNDETERMINED" ) ) + "." );
      }
      if( index < outsideBranch.getLeftFaces().size() ) {
        return outsideBranch.getLeftFaces().get( index );
      } else {
        return
          outsideBranch.getRightFaces().get(
                                            index -
                                            outsideBranch.getLeftFaces().size()
                                            );
      }
    } // get( int )
  
    public boolean contains ( ClusterNode query_node ) {
      if( ClusterNode.this.isLeaf() ) {
        if( ( branchType == LEFT ) &&
            ( query_node == ClusterNode.this ) ) {
          return true;
        } else {
          return false;
        }
      }
      if( ClusterNode.this.getChildCount() == 1 ) {
        if( ( branchType == LEFT ) &&
            ( query_node.getParent() == ClusterNode.this ) ) {
          return true;
        } else {
          return false;
        }
      }
      // Check again that we're not on the inside.
      if( ( outsideBranch != null ) &&
          ( ( ( branchType == LEFT ) &&
              ( orientation == RIGHT_BRANCH_OUTSIDE ) ) ||
            ( ( branchType == RIGHT ) &&
              ( orientation == LEFT_BRANCH_OUTSIDE ) ) )
          ) {
        outsideBranch = null;
      }
      if( outsideBranch == null ) {
        return false;
      }
      return ( outsideBranch.getLeftFaces().contains( query_node ) ||
               outsideBranch.getRightFaces().contains( query_node ) );
    } // contains( ClusterNode )
  
    public void select (
                        ClusterNode source,
                        Set nodes_to_select_as_couple_members
                        ) {
      if( outsideBranch == null ) {
        throw new IllegalStateException( "\"" + ClusterNode.this 
                                         + "\"." + ( ( branchType == LEFT ) ? "leftFace" : "rightFace" ) 
                                         + ": Unable to select( " + source + ", " + nodes_to_select_as_couple_members 
                                         + " ) because the orientation of this ClusterNode, " + ClusterNode.this 
                                         + " has already been determined to be " 
                                         + ( ( orientation == LEFT_BRANCH_OUTSIDE ) ? "LEFT_BRANCH_OUTSIDE" : 
                                             ( ( orientation == RIGHT_BRANCH_OUTSIDE ) ? "RIGHT_BRANCH_OUTSIDE" 
                                               : "UNDETERMINED" ) ) + "." );
      }
      if( outsideBranch.orientation != UNDETERMINED ) {
        // TODO: Check that the nodes_to_select_as_couple_members 
        // is the same as the outside faces, and throw this exception if it is not:
        // throw new IllegalStateException( "\"" + ClusterNode.this + "\"." 
        //+ ( ( branchType == LEFT ) ? "leftFace" : "rightFace" ) 
        //+ ": Unable to select( " 
        //+ source + ", " + nodes_to_select_as_couple_members + " ) because the orientation of the outsideBranch, " 
        //+ outsideBranch 
        //+ " has already been determined to be " 
        //+ ( ( orientation == LEFT_BRANCH_OUTSIDE ) ? "LEFT_BRANCH_OUTSIDE" : "RIGHT_BRANCH_OUTSIDE" ) + "." );
        return;
      }
      // Find the subsets that include our relevant child's left and right
      // faces.
      HashSet left_subset = new HashSet();
      HashSet right_subset = new HashSet();
      Iterator ntsacm_iterator =
        nodes_to_select_as_couple_members.iterator();
      ClusterNode candidate_node;
      while( ntsacm_iterator.hasNext() ) {
        candidate_node = ( ClusterNode )ntsacm_iterator.next();
        if( outsideBranch.getLeftFaces().contains( candidate_node ) ) {
          left_subset.add( candidate_node );
        } else if( outsideBranch.getRightFaces().contains( candidate_node ) ) {
          right_subset.add( candidate_node );
        }
      }
  
      if( left_subset.isEmpty() && right_subset.isEmpty() ) {
        throw new RuntimeException( "ASSERTION FAILED: Both subsets are empty." );
      } else if( left_subset.isEmpty() ) {
        if( source == ClusterNode.this ) {
          // The left subset doesn't have any of the selected nodes, so it
          // gets to go on the outside where it won't get in the way.
          outsideBranch.orientation = LEFT_BRANCH_OUTSIDE;
        } else {
          // If the source node is above this node then we're already on
          // the inside and we need to expose the subset with the goods.
          outsideBranch.orientation = RIGHT_BRANCH_OUTSIDE;
        }
        outsideBranch.getRightFaces().select( source, right_subset );
      } else if( right_subset.isEmpty() ) {
        if( source == ClusterNode.this ) {
          // The right subset doesn't have any of the selected nodes, so it
          // gets to go on the outside where it won't get in the way.
          outsideBranch.orientation = RIGHT_BRANCH_OUTSIDE;
        } else {
          // If the source node is above this node then we're already on
          // the inside and we need to expose the subset with the goods.
          outsideBranch.orientation = LEFT_BRANCH_OUTSIDE;
        }
        outsideBranch.getLeftFaces().select( source, left_subset );
      } else {
        // There's selections in both children.  We'll have to leave our
        // orientation undetermined for now.
        outsideBranch.getLeftFaces().select( source, left_subset );
        outsideBranch.getRightFaces().select( source, right_subset );
      }
    } // select(..)
  
  } // inner class NodeFaces

  /**
   * An ascending distanceBetweenChildren ClusterNode comparator.  ClusterNodes
   * that are not equal by == will never be considered equal by this
   * comparator, but ClusterNodes with identical distanceBetweenChildren values
   * may be arbitrarily and inconsistently compared.
   */
  public static class AscendingDistanceBetweenChildrenComparator
    implements Comparator {
    public int compare ( Object object1, Object object2 ) {
      if( object1 == object2 ) {
        return 0;
      }
      double difference =
        ( ( ( ClusterNode )object1 ).getDistanceBetweenChildren() -
          ( ( ClusterNode )object2 ).getDistanceBetweenChildren() );
      if( difference < 0 ) {
        return -1;
      } else if( difference > 0 ) {
        return 1;
      } else {
        // Since they are not the same object, we can't return 0.
        int name1_hash_code = object1.toString().hashCode();
        int name2_hash_code = object2.toString().hashCode();
        if( name1_hash_code != name2_hash_code ) {
          return ( name1_hash_code - name2_hash_code );
        }
        // Well this is a pickle, isn't it.  Let's just return -1 and hope for
        // the best.
        // TODO: HACK.  Dehackify.
        return -1;
      }
    } // compare( Object, Object )
  } // static inner class AscendingDistanceBetweenChildrenComparator

} // class ClusterNode
