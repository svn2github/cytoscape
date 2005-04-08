package goginy;

// Java Import
import java.util.*;
import java.io.*;

// Violin Strings Import
import ViolinStrings.Strings;

// colt import
import cern.colt.map.*;

import giny.model.*;
import fing.model.*;

/**
 * Read in an OBO file, and create a GINY Graph
 */
public class GoParser {

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


  public void parseOBO ( String file_name ) {

    cp =  fing.model.FingRootGraphFactory.instantiateRootGraph();
    bp =  fing.model.FingRootGraphFactory.instantiateRootGraph();
    mf =  fing.model.FingRootGraphFactory.instantiateRootGraph();

    cp_uid_gid_map = new OpenIntIntHashMap();
    bp_uid_gid_map = new OpenIntIntHashMap();
    mf_uid_gid_map = new OpenIntIntHashMap();
    
    cp_gid_uid_map = new OpenIntIntHashMap();
    bp_gid_uid_map = new OpenIntIntHashMap();
    mf_gid_uid_map = new OpenIntIntHashMap();

    cp_gid_desc_map = new OpenIntObjectHashMap();
    bp_gid_desc_map = new OpenIntObjectHashMap();
    mf_gid_desc_map = new OpenIntObjectHashMap();

    try {
      BufferedReader in
        = new BufferedReader(new FileReader( file_name ) );
      String oneLine = in.readLine();
      
      boolean in_term = false;
      int current_go_id = 0;
      String name = "";
      String namespace = "";


      while (oneLine != null  ) {
        if ( oneLine.startsWith( "[Term]" ) ) {
          in_term = true;
        } else if ( oneLine.startsWith( "[Type" ) ) {
          break;
        }else if ( oneLine.startsWith( "id: part_of" ) ) {
            continue;
        } else if ( in_term && oneLine.startsWith( "id:" ) ) {
          current_go_id = ( new Integer( oneLine.substring( 7, 14 ) ) ).intValue();
        } else if ( in_term && oneLine.startsWith( "name:" ) ) {
          name = oneLine.substring( 6, oneLine.length() );
        } else if ( in_term && oneLine.startsWith( "namespace:" ) ) {
          namespace = oneLine.substring( 11, oneLine.length() );

          if ( namespace.startsWith( "cellular_component" ) ) {
            cp_gid_desc_map.put( current_go_id, name );
          } else if ( namespace.startsWith( "biological_process" ) ) {
            bp_gid_desc_map.put( current_go_id, name ); 
          } else if ( namespace.startsWith( "molecular_function" ) ) {
             mf_gid_desc_map.put( current_go_id, name ); 
          }



        } else if ( in_term && oneLine.startsWith( "is_a:" ) || 
                    oneLine.startsWith( "relationship:" ) ) {

          int is_a;
          if ( oneLine.startsWith( "is_a:" ) )
            is_a = ( new Integer( oneLine.substring( 9, 16 ) ) ).intValue();
          else
            is_a = ( new Integer( oneLine.substring( 25, 32 ) ) ).intValue();

          if ( namespace.startsWith( "cellular_component" ) ) {
            //cp
            makePairing( is_a,
                         current_go_id,
                         cp,
                         cp_uid_gid_map,
                         cp_gid_uid_map );
            
          } else if ( namespace.startsWith( "biological_process" ) ) {
            //bp
            makePairing( is_a,
                         current_go_id,
                         bp,
                         bp_uid_gid_map,
                         bp_gid_uid_map );
            
          } else if ( namespace.startsWith( "molecular_function" ) ) {
            //mf
            makePairing( is_a,
                         current_go_id,
                         mf,
                         mf_uid_gid_map,
                         mf_gid_uid_map );
          }


        } //else if ( in_term && oneLine.startsWith( "relationship:" ) ) {
          //int is_a = ( new Integer( oneLine.substring( 25, 31 ) ) ).intValue();
          //makePairing( is_a, current_go_id );
        //}


        oneLine = in.readLine();
        
      } 
      
      in.close();
      
    } catch ( Exception ex ) {
      System.out.println( "File Read error" );
      ex.printStackTrace();
    }

 
  }

  /**
   * @param parent this is the parent of the current GID
   * @param current this is the current GID
   * @param returns the current UID
   */
  public static int makePairing ( int parent_gid, 
                                  int current_gid, 
                                  RootGraph root,
                                  OpenIntIntHashMap uidGidMap,
                                  OpenIntIntHashMap gidUidMap ) {
    int current_uid;
    int parent_uid;

    // make a new node if the parent does not exist
    if ( !gidUidMap.containsKey( parent_gid ) ) {
      parent_uid = root.createNode();
      gidUidMap.put( parent_gid, parent_uid );
      uidGidMap.put( parent_uid, parent_gid );
    } else {
      parent_uid = gidUidMap.get( parent_gid );
    }

    // make a new node if the current does not exist
    if ( !gidUidMap.containsKey( current_gid ) ) {
      current_uid = root.createNode();
      gidUidMap.put( current_gid, current_uid );
      uidGidMap.put( current_uid, current_gid );
    } else {
      current_uid = gidUidMap.get( current_gid );
    }
   
    int edge_uid;
    // if no edge, create an edge between the two
    //if ( !root.edgeExists( parent_uid, current_uid ) ) {
      edge_uid = root.createEdge( parent_uid, current_uid );
      //}

    return current_uid;
  }


 
}


