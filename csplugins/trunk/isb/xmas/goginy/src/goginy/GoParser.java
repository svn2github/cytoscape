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

  public static RootGraph root;
  public static OpenIntIntHashMap uidGidMap;
  public static OpenIntIntHashMap gidUidMap;
  public static Map gdescGidMap;
  public static OpenIntObjectHashMap gidGdescMap;



  public static void parseOBO ( String file_name ) {

    root =  fing.model.FingRootGraphFactory.instantiateRootGraph();
    uidGidMap = new OpenIntIntHashMap( 5000 );
    gidUidMap = new OpenIntIntHashMap( 5000 );
    gidGdescMap = new OpenIntObjectHashMap( 5000 );
    gdescGidMap = new HashMap();

    

    try {
      BufferedReader in
        = new BufferedReader(new FileReader( file_name ) );
      String oneLine = in.readLine();
      
      boolean in_term = false;
      int current_go_id = 0;
      boolean make = false;
    
      while (oneLine != null  ) {
        if ( oneLine.startsWith( "[Term]" ) ) {
          in_term = true;
        } else if ( oneLine.startsWith( "id: part_of" ) ) {
            continue;
        } else if ( in_term && oneLine.startsWith( "id:" ) ) {
          
          current_go_id = ( new Integer( oneLine.substring( 7, 14 ) ) ).intValue();
        } else if ( in_term && oneLine.startsWith( "name:" ) ) {
          String name = oneLine.substring( 6, oneLine.length() );
          //System.out.println( "name: "+name );
          gidGdescMap.put( current_go_id, name );
          gdescGidMap.put( name, new Integer( current_go_id ) );

        } else if ( in_term && oneLine.startsWith( "namespace:" ) ) {
          String namespace = oneLine.substring( 11, oneLine.length() );
          //System.out.println( "namespace: "+namespace );
          if ( namespace.startsWith("biological_process") ) {
            make = true;
          } else {
            make = false;
          }

        } else if ( in_term && oneLine.startsWith( "is_a:" ) ) {        
          int is_a = ( new Integer( oneLine.substring( 9, 16 ) ) ).intValue();
          //System.out.println( current_go_id+" is a "+is_a );
          // make pairing between the current GID and its Parent
          if ( make ) 
            makePairing( is_a, current_go_id );
        }
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
  public static int makePairing ( int parent_gid, int current_gid ) {
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


  public static void main ( String[] args ) {
    parseOBO( args[0] );
  }


}


