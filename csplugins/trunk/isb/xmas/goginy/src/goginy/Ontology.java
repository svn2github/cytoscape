package goginy;

// Java Import
import java.util.*;
import java.io.*;
import java.beans.*;

// Violin Strings Import
import ViolinStrings.Strings;

// colt import
import cern.colt.map.*;
import cern.colt.list.*;

import giny.model.*;
import fing.model.*;
import giny.view.*;
import phoebe.*;

import goginy.layout.HierarchicalLayoutListener;
import ViolinStrings.Strings;

public class Ontology {

  RootGraph root;
  GraphPerspective gp;
  OpenIntIntHashMap uidGidMap;
  OpenIntIntHashMap gidUidMap;
  OpenIntObjectHashMap gidGdescMap;
  
  int root_node = 0;

  public PropertyChangeSupport pcs;
  

  public int[] flagged_nodes = new int[] {};

  public Ontology ( RootGraph root, 
                    OpenIntIntHashMap uidGidMap,
                    OpenIntIntHashMap gidUidMap,
                    OpenIntObjectHashMap gidGdescMap ) {

    this.root = root;
    this.uidGidMap = uidGidMap;
    this.gidUidMap = gidUidMap;
    this.gidGdescMap = gidGdescMap;

    pcs = new PropertyChangeSupport( this );

    int[] nodes = root.getNodeIndicesArray();
    
    for ( int i = 0; i < nodes.length && root_node == 0; ++i ) {
           
      if ( isUIDRoot( nodes[i] ) ) {
        root_node = nodes[i];
      }
    }
    gp = getUIDChildrenAsGP( root_node, 0 );
  }

  
  public GraphPerspective getGraphPerspective () {
    return gp;
  }

  public String getDesc ( int gid ) {
    return ( String )gidGdescMap.get( gid );
  }

  public int uidForGID ( int gid ) {
    return gidUidMap.get( gid );
  }

  public int[] getTerms ( int[] selected_nodes ) {
    //take the selected nodes and return the GO indices
    IntArrayList go_terms = new IntArrayList();
    for ( int i = 0; i < selected_nodes.length; ++i ) {
      go_terms.add( uidGidMap.get( selected_nodes[i] ) );
    }
    go_terms.trimToSize();
    return go_terms.elements();
  }

  public void addChildrenOfUID ( int uid[] ) {
    
    for ( int i = 0; i < uid.length; ++i ) {
      
      GraphPerspective new_gp  = getUIDChildrenAsGP( uid[i], 0 );
      gp.restoreNodes( new_gp.getNodeIndicesArray() );
      gp.restoreEdges( new_gp.getEdgeIndicesArray() );

     

    }

    pcs.firePropertyChange( "layout", null, null);

  }

  public void removeChildrenOfUID ( int uid[] ) {
    for ( int i = 0; i < uid.length; ++i ) {
      
      GraphPerspective new_gp  = getUIDChildrenAsGP( uid[i], 0 );
      gp.hideNodes( new_gp.getNodeIndicesArray() );
      gp.restoreNode( uid[i] );
    }
    
    pcs.firePropertyChange( "layout", null, null );
    

  }


  public void showTerms ( int[] go_terms ) {


    // take an array of GO ints and show them
       
    IntArrayList mat = new IntArrayList();
    // iterate through the given go_terms and get the nodes
    for ( int i = 0; i < go_terms.length; ++i ) {
      mat.add(  gidUidMap.get( go_terms[i] ) );
    }
    mat.trimToSize();
    flagged_nodes = mat.elements();

    GraphPerspective new_gp  = getUIDParentsAsGP( mat.elements() );

    IntArrayList inOld_notNew = new IntArrayList();
    IntArrayList inNew_notOld = new IntArrayList();
    
    if ( gp == null ) {
      System.out.println( "GP is null??" );
    }
    
    if ( new_gp == null ) {
      System.out.println ( " NEW GP is null???!!?!?!??!" );
    }

   
    IntArrayList diff = Diff.diff( new IntArrayList( gp.getNodeIndicesArray() ),
                                   new IntArrayList( new_gp.getNodeIndicesArray() ),
                                   inOld_notNew,
                                   inNew_notOld );
    
    gp.restoreNodes( new_gp.getNodeIndicesArray() );
    gp.restoreEdges( new_gp.getEdgeIndicesArray() );
    gp.hideNodes( inOld_notNew.elements() );
    gp.restoreNode( root_node );
   
    
    pcs.firePropertyChange( "layout", null, mat.elements() );
    
  }


  protected void searchFor ( String string ) {

    IntArrayList mat = new IntArrayList();
    int[] nodes = root.getNodeIndicesArray();
    String pattern = "*"+string+"*" ;
    for ( int i = 0; i < nodes.length ; ++i ) {
      if ( Strings.isLike( (String)gidGdescMap.get( uidGidMap.get(nodes[i]) ),
                           pattern ) ) {
        //System.out.println( nodes[i]+"matches "+pattern );
        mat.add( nodes[i] );
      } // else 
//         System.out.println(gidGdescMap.get( uidGidMap.get(nodes[i]) )+" does not matches "+pattern );

    }

    mat.trimToSize();
    flagged_nodes = mat.elements();

    GraphPerspective new_gp  = getUIDParentsAsGP( mat.elements() );

     IntArrayList inOld_notNew = new IntArrayList();
     IntArrayList inNew_notOld = new IntArrayList();
    
     IntArrayList diff = Diff.diff( new IntArrayList( gp.getNodeIndicesArray() ),
                                    new IntArrayList( new_gp.getNodeIndicesArray() ),
                                    inOld_notNew,
                                    inNew_notOld );

    
     gp.restoreNodes( new_gp.getNodeIndicesArray() );
     gp.restoreEdges( new_gp.getEdgeIndicesArray() );
     gp.hideNodes( inOld_notNew.elements() );
     gp.restoreNode( root_node );

     pcs.firePropertyChange( "layout", null, mat.elements() );
    

  }



  /**
   * @param go_id a GO id of the form: GO:0000034
   */
  public String descriptionForID ( String go_id ) {
    return ( String )gidGdescMap.get( Integer.parseInt( go_id.substring( 4 ) ) );
  }

  /**
   * @param go_id a GO id that is stripped of the "GO:"
   */
  public String descriptionForID ( int id ) {
    return ( String )gidGdescMap.get( id );
  }

  protected String descriptionForUID ( int uid ) {
    return ( String )gidGdescMap.get( uidGidMap.get(uid) );
  }

  protected boolean isUIDLeaf ( int uid ) {
    if ( root.getAdjacentEdgeIndicesArray( uid, false, false, true ).length == 0 )
      return true;
    return false;
  }

  protected boolean isUIDRoot ( int uid ) {
    if ( root.getAdjacentEdgeIndicesArray( uid, false, true, false ).length == 0 )
      return true;
    return false;
  }

  protected int[] getUIDParents ( int uid ) {
    
    IntArrayList nodes = new IntArrayList();
    IntArrayList edges = new IntArrayList();

    boolean cont = true;
    while ( cont ) {
      cont = addParents( uid, nodes, edges );
    }
    nodes.trimToSize();
    edges.trimToSize();
    
    return nodes.elements();

  }

  protected GraphPerspective getUIDParentsAsGP ( int uid[] ) {
    IntArrayList nodes = new IntArrayList();
    IntArrayList edges = new IntArrayList();

    for ( int i = 0; i < uid.length; ++i ) {
      nodes.add( uid[i] );

      boolean cont = true;
      while ( cont ) {
        cont = addParents( uid[i], nodes, edges );
      }

    }

    //System.out.println ( "Nodes: "+nodes.size() );
    //System.out.println ( "Edges: "+edges.size() );

    nodes.trimToSize();
    edges.trimToSize();
 
    //System.out.println( "Nodes: "+nodes.elements() );
    //System.out.println( "Edges: "+edges.elements() );
    return root.createGraphPerspective( nodes.elements(), 
                                        edges.elements());
  }
  
 
  protected int[] getUIDChildren ( int uid ) {
    IntArrayList nodes = new IntArrayList();
    IntArrayList edges = new IntArrayList();

    boolean cont = true;
    while ( cont ) {
      cont = addChildren( uid, nodes, edges );
    }
    nodes.trimToSize();
    edges.trimToSize();
    
    return nodes.elements();

  }

  protected GraphPerspective getUIDChildrenAsGP ( int uid, int depth ) {
    IntArrayList nodes = new IntArrayList();
    IntArrayList edges = new IntArrayList();

    nodes.add( uid );

    boolean cont = true;
    while ( cont ) {
      cont = addChildren( uid, nodes, edges, 0, depth );
    }
    nodes.trimToSize();
    edges.trimToSize();
    
    return root.createGraphPerspective( nodes.elements(), 
                                        edges.elements()  );
  }
      
  private boolean addParents ( int child, 
                               IntArrayList nodes, 
                               IntArrayList edges ) {
    return addParents( child, nodes, edges, 0, 0 );
  }

  private boolean addParents ( int child, 
                               IntArrayList nodes, 
                               IntArrayList edges,
                               int start,
                               int end ) {
    
    if ( end != 0 && start > end ) {
      return false;
    }
    start++;

    int[] incoming_edges = root.
      getAdjacentEdgeIndicesArray(child, 
                                  false, 
                                  true, 
                                  false);

    if ( incoming_edges == null )
      return false;

    // add these edges
    for ( int i = 0; i < incoming_edges.length; ++i )
      if ( !edges.contains( incoming_edges[i] ) )
        edges.add( incoming_edges[i] );
    
    // continue if there were no more parents
    if ( incoming_edges.length == 0 )
      return false;

    // add the parents
    for ( int i = 0; i < incoming_edges.length; ++i ) {
      int parent = root.getEdgeSourceIndex( incoming_edges[i] );
      if ( !nodes.contains( parent ) ) {
        nodes.add( parent );
        boolean cont = true;
        while ( cont ) {
          cont = addParents( parent, nodes, edges, start, end );
        }
      }
    }
    return false;
  }

                               
  private boolean addChildren ( int parent, 
                                IntArrayList nodes, 
                                IntArrayList edges ) {
    return addParents( parent, nodes, edges, 0, 0 );
  }

  private boolean addChildren ( int parent, 
                                IntArrayList nodes, 
                                IntArrayList edges,
                                int start,
                                int end ) {
    if ( start != 0 && start > end ) {
      return false;
    }
    start++;

    int[] outgoing_edges = root.
      getAdjacentEdgeIndicesArray(parent, 
                                  false, 
                                  false, 
                                  true);

    if ( outgoing_edges == null )
      return false;

    // add these edges
    for ( int i = 0; i < outgoing_edges.length; ++i )
      if ( !edges.contains( outgoing_edges[i] ) )
        edges.add( outgoing_edges[i] );
    
    // continue if there were no more children
    if ( outgoing_edges.length == 0 ) {
      return false;
    }
      // add the parents
    for ( int i = 0; i < outgoing_edges.length; ++i ) {
      int child = root.getEdgeTargetIndex( outgoing_edges[i] );
      if ( !nodes.contains( child ) ) {
        nodes.add( child );
        boolean cont = true;
        while ( cont ) {
          cont = addChildren( child, nodes, edges, start, end );
        }
      }
    }
    return false;
  }
  




}
