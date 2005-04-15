package goginy;

// Java Import
import java.util.*;
import java.io.*;
import javax.swing.*;

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


public class GoOntology {

  // The Root Graphs store the ontology
  public RootGraph cc;
  public RootGraph bp;
  public RootGraph mf;

  // This converts a RootGraphIndex to a GO id
  public OpenIntIntHashMap cc_uid_gid_map;
  public OpenIntIntHashMap bp_uid_gid_map;
  public OpenIntIntHashMap mf_uid_gid_map;
   

  // This converts a GO id to a RootGraphIndex
  public OpenIntIntHashMap cc_gid_uid_map;
  public OpenIntIntHashMap bp_gid_uid_map;
  public OpenIntIntHashMap mf_gid_uid_map;

  // Get a Description given a GO id
  public OpenIntObjectHashMap cc_gid_desc_map;
  public OpenIntObjectHashMap bp_gid_desc_map;
  public OpenIntObjectHashMap mf_gid_desc_map;
  

  public Ontology cc_ontology;
  public Ontology bp_ontology;
  public Ontology mf_ontology;
  
  public GoGinyView cc_view;
  public GoGinyView bp_view;
  public GoGinyView mf_view;

  JFrame frame;
  String obo;

  public GoOntology ( String obo ) {

    this.obo = obo;
  }

  
  public void show () {
    if ( frame == null ) {
     
      GoParser parser = new GoParser();
      parser.parseOBO( obo );
        
      cc = parser.cc;
      bp = parser.bp;
      mf = parser.mf;

      cc_uid_gid_map = parser.cc_uid_gid_map;
      bp_uid_gid_map = parser.bp_uid_gid_map;
      mf_uid_gid_map = parser.mf_uid_gid_map;
    
      cc_gid_uid_map = parser.cc_gid_uid_map;
      bp_gid_uid_map = parser.bp_gid_uid_map;
      mf_gid_uid_map = parser.mf_gid_uid_map;
      
      cc_gid_desc_map = parser.cc_gid_desc_map;
      bp_gid_desc_map = parser.bp_gid_desc_map;
      mf_gid_desc_map = parser.mf_gid_desc_map;
  
      System.out.println( "Cellular_Component" );
      cc_ontology = new Ontology( cc,
                                  cc_uid_gid_map,
                                  cc_gid_uid_map,
                                  cc_gid_desc_map );
      cc_view = new GoGinyView( cc_ontology );

      System.out.println( "Biological_Process" );
      bp_ontology = new Ontology( bp,
                                  bp_uid_gid_map,
                                  bp_gid_uid_map,
                                  bp_gid_desc_map );
      bp_view = new GoGinyView( bp_ontology );
      
      System.out.println( "Molecular_Function" );
      mf_ontology = new Ontology( mf,
                                  mf_uid_gid_map,
                                  mf_gid_uid_map,
                                  mf_gid_desc_map );
      mf_view = new GoGinyView( mf_ontology );
      
      frame = new JFrame( "GoGiny" );
      JTabbedPane tab = new JTabbedPane();
      frame.getContentPane().add( tab );
      frame.setSize( 800, 400 );

      tab.addTab(  "Cellular_Component" ,cc_view.getComponent() );
      tab.addTab(  "Biological_Process" ,bp_view.getComponent() );
      tab.addTab(  "Molecular_Function" ,mf_view.getComponent() );
      
      frame.layout();
    }

    frame.setVisible( true );
    
  }



  public Ontology getMF () {
    return mf_ontology;
  }

  public Ontology getBP () {
    return bp_ontology;
  }

  public Ontology getCC () {
    return cc_ontology;
  }
  
  public GoGinyView getMFView () {
    return mf_view;
  }

  public GoGinyView getBPView () {
    return bp_view;
  }

  public GoGinyView getCCView () {
    return cc_view;
  }

  
  public static void main ( String[] args ) {
    GoOntology go = new GoOntology( args[0] );
  }


  
  



}
