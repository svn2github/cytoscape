package csplugins.metabolic;

import cytoscape.*;
import cytoscape.giny.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.beans.*;
import java.util.*;

import javax.swing.JMenuItem;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.*;
import phoebe.*;
import phoebe.util.*;
import giny.model.*;
import giny.view.*;

import cern.colt.list.*;

/**
 * A Reaction is_a CyNode, and is simply the Meta-Parent of all associated Nodes that are invloved in teh Reaction.
 */
public class Reaction 
  extends 
    CyNode {

  /**A List of the Nodes that are Reactants in this Reaction*/
  IntArrayList reactants;
  /**A List of the Nodes that are Products in this Reaction*/
  IntArrayList products;
  /**A List of the Nodes that are Modifiers in this Reaction*/
  IntArrayList modifiers;

   public Reaction ( int root_graph_index, 
                     RootGraph root_graph ) {
     this( root_graph_index, root_graph, 
           new int[] {},
           new int[] {},
           new int[] {});
   }

  /**
   * A new Reaction will become the MetaParent of all reactants, products, modifiers, and the edges that connect them to the reaction.
   */
  public Reaction ( int root_graph_index, 
                    RootGraph root_graph,
                    int[] reactants,
                    int[] products,
                    int[] modifiers ) {
    super( root_graph_index, root_graph );
    this.reactants = new IntArrayList( reactants );
    this.products = new IntArrayList( products );
    this.modifiers = new IntArrayList( modifiers );

    // add reactants as meta children
    for ( int i = 0; i < reactants.length; i++ ) {
      addReactant( reactants[i] );
    }
    // add products as meta children
    for ( int i = 0; i < products.length; i++ ) {
      addProduct( products[i] );
    }
    // add modifiers as meta children
    for ( int i = 0; i < modifiers.length; i++ ) {
      addModifier( modifiers[i] );
    }
  }

  //////////////////////////////
  // Addition/ Removal

  public void addReactant ( int reactant ) {
    reactants.add( reactant );
    getRootGraph().addNodeMetaChild( getRootGraphIndex(), reactant );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), reactant, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  reactant, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().addEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
      
  }

  public void removeReactant ( int reactant ) {
    reactants.delete( reactants.indexOf( reactant ) );
    getRootGraph().removeNodeMetaChild( getRootGraphIndex(), reactant );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), reactant, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  reactant, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().removeEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
  }

  public void addProduct ( int product ) {
    products.add( product );
    getRootGraph().addNodeMetaChild( getRootGraphIndex(), product );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), product, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  product, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().addEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
      
  }

  public void removeProduct ( int product ) {
    products.delete( products.indexOf( product ) );
    getRootGraph().removeNodeMetaChild( getRootGraphIndex(), product );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), product, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  product, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().removeEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
  }

  public void addModifier ( int modifier ) {
    modifiers.add( modifier );
    getRootGraph().addNodeMetaChild( getRootGraphIndex(), modifier );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), modifier, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  modifier, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().addEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
      
  }

  public void removeModifier ( int modifier ) {
    modifiers.delete( modifiers.indexOf( modifier ) );
    getRootGraph().removeNodeMetaChild( getRootGraphIndex(), modifier );
    int[] eb = getRootGraph().getEdgeIndicesArray( getRootGraphIndex(), modifier, true );
    if ( eb == null || eb.length < 1 ) {
      // get edges the other way
      eb = getRootGraph().getEdgeIndicesArray(  modifier, getRootGraphIndex(), true );
    }
    
    for ( int i = 0; i < eb.length; ++i ) {
      getRootGraph().removeEdgeMetaChild( getRootGraphIndex(), eb[i] );
    }
  }

  //////////////////////////////
  // getReactionComponents

  public int[] getModifierIndices () {
    modifiers.trimToSize();
    return modifiers.elements();
  }

  public List getModifierList () {
    ArrayList list = new ArrayList( modifiers.size() );
    for ( int i = 0; i < modifiers.size(); ++i ) {
      list.add( Cytoscape.getRootGraph().getNode( modifiers.get( i ) ) );
    }
    return list;
  }

  public int[] getReactantIndices () {
    reactants.trimToSize();
    return reactants.elements();
  }

  public List getReactantList () {
    ArrayList list = new ArrayList( reactants.size() );
    for ( int i = 0; i < reactants.size(); ++i ) {
      list.add( Cytoscape.getRootGraph().getNode( reactants.get( i ) ) );
    }
    return list;
  }

  public int[] getProductIndices () {
    products.trimToSize();
    return products.elements();
  }

  public List getProductList () {
    ArrayList list = new ArrayList( products.size() );
    for ( int i = 0; i < products.size(); ++i ) {
      list.add( Cytoscape.getRootGraph().getNode( products.get( i ) ) );
    }
    return list;
  }

	
}
