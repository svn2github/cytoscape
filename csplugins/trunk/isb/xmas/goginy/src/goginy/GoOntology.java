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
  public RootGraph cp;
  public RootGraph bp;
  public RootGraph mf;

  // This converts a RootGraphIndex to a GO id
  public OpenIntIntHashMap cp_uid_gid_map;
  public OpenIntIntHashMap bp_uid_gid_map;
  public OpenIntIntHashMap mf_uid_gid_map;
   

  // This converts a GO id to a RootGraphIndex
  public OpenIntIntHashMap cp_gid_uid_map;
  public OpenIntIntHashMap bp_gid_uid_map;
  public OpenIntIntHashMap mf_gid_uid_map;

  // Get a Description given a GO id
  public OpenIntObjectHashMap cp_gid_desc_map;
  public OpenIntObjectHashMap bp_gid_desc_map;
  public OpenIntObjectHashMap mf_gid_desc_map;
  

  public Ontology cp_ontology;
  public Ontology bp_ontology;
  public Ontology mf_ontology;
  
  public GoGinyView cp_view;
  public GoGinyView bp_view;
  public GoGinyView mf_view;


  public GoOntology ( String obo ) {

    GoParser parser = new GoParser();
    parser.parseOBO( obo );
    
    
    cp = parser.cp;
    bp = parser.bp;
    mf = parser.mf;

    cp_uid_gid_map = parser.cp_uid_gid_map;
    bp_uid_gid_map = parser.bp_uid_gid_map;
    mf_uid_gid_map = parser.mf_uid_gid_map;
    
    cp_gid_uid_map = parser.cp_gid_uid_map;
    bp_gid_uid_map = parser.bp_gid_uid_map;
    mf_gid_uid_map = parser.mf_gid_uid_map;

    cp_gid_desc_map = parser.cp_gid_desc_map;
    bp_gid_desc_map = parser.bp_gid_desc_map;
    mf_gid_desc_map = parser.mf_gid_desc_map;
  
    
    System.out.println( "Cellular_Component" );
    cp_ontology = new Ontology( cp,
                                cp_uid_gid_map,
                                cp_gid_uid_map,
                                cp_gid_desc_map );
    cp_view = new GoGinyView( cp_ontology );

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
  
    JFrame frame = new JFrame( "GoGiny" );
    JTabbedPane tab = new JTabbedPane();
    frame.getContentPane().add( tab );
    frame.setSize( 800, 400 );

    tab.addTab(  "Cellular_Component" ,cp_view.getComponent() );
    tab.addTab(  "Biological_Process" ,bp_view.getComponent() );
    tab.addTab(  "Molecular_Function" ,mf_view.getComponent() );

    frame.layout();
    frame.setVisible( true );
    
    

  }
  
  public static void main ( String[] args ) {
    GoOntology go = new GoOntology( args[0] );
  }


  
  



}
