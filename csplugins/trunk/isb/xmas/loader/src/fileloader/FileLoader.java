package fileloader;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;
import javax.swing.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.readers.*;
import cytoscape.plugin.*;
import cytoscape.data.*;

import cytoscape.data.attr.*;

import exesto.*;

import giny.model.*;
import ViolinStrings.Strings;

public class FileLoader {


  static Object null_att = new String("null");

  // HEADER RESERVED
  static String NODE_LABEL = "NODENAME";
  static String EDGE_LABEL = "EDGETYPE";
  static String COMMENTS = "^#";
  static String delim = "\t";
  
  // POSITION HEADER
  static String NODE_X = "NODE_X";
  static String NODE_Y = "NODE_Y";
  static String EDGE_BENDS = "EDGE_BENDS";




  static String TYPE_BOOLEAN = "BOOLEAN";
  static String TYPE_INTEGER = "INTEGER";
  static String TYPE_FLOATING_POINT = "FLOATING_POINT";
  static String TYPE_STRING = "STRING";

  public static void saveNetworkToFile ( CyNetwork network,
                                         String file_name ) {
    try {
      File file = new File( file_name );
      BufferedWriter writer = new BufferedWriter(new FileWriter( file ));
      
      Iterator nodes_i = network.nodesIterator();
      Iterator edges_i = network.edgesIterator();

      // Node Section
      writer.write( NODE_LABEL+delim );
      
      // Global Data
      CyAttributes data = Cytoscape.getNodeAttributes();
      String[] atts_i = data.getAttributeNames();

      // Local Data
      CyAttributes local = NetworkAttributes.getNodeAttributes( network );
      String[] local_i = local.getAttributeNames();
      
      // Header

      // write network name
      writer.write( network.getIdentifier() );

      //////////////////
      // Node Attributes

      // write global names
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( atts_i[i]+delim );
      }
     
      // write local names, e.g. "LOCAL:____"
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( "LOCAL:"+local_i[i]+delim );
      }
      writer.newLine();

      // Node Attribute Tags
      
      writer.write( "TAGS"+delim );
      
      // global tags
      for ( int i = 0; i < atts_i.length; i++ ) {
        Set gtags = AttributeTags.getTagsOfAttribute( data, atts_i[i] );
        writer.write( setAsListString( gtags )+delim );
      }
      
      // local tags
      for ( int i = 0; i < local_i.length; i++ ) {
        Set gtags = AttributeTags.getTagsOfAttribute( local, local_i[i] );
        writer.write( setAsListString( gtags )+delim );
      }
      writer.newLine();

      // Node Attribute Types
      
      
      
      //////////////
      // Node Values

      // iterate through nodes
      while ( nodes_i.hasNext() ) {
        GraphObject obj = (GraphObject)nodes_i.next();
        writer.write( obj.getIdentifier()+delim );
        
        // Global Data
        for ( int i = 0; i < atts_i.length; i++ ) {
          
          String value = getPrintableValue( data, 
                                            obj.getIdentifier(),
                                            atts_i[i] );
          try {
            writer.write( value+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }

        // Network Specific Data
        for ( int i = 0; i < local_i.length; i++ ) {
          
          String value = getPrintableValue( local,
                                            obj.getIdentifier(),
                                            local_i[i] );
          try {
            writer.write( value+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }
        writer.newLine();
      }
    
      ////////////////////
      // Edge Section
      writer.write( NODE_LABEL+delim+EDGE_LABEL+delim+NODE_LABEL+delim );
      
      // Global Data
      data = Cytoscape.getEdgeAttributes();
      atts_i = data.getAttributeNames();

      // Local Data
      local = NetworkAttributes.getEdgeAttributes( network );
      local_i = local.getAttributeNames();

      // Header
      // write global names
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( atts_i[i]+delim );
      }
     
      // write local names, e.g. "LOCAL:____"
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( "LOCAL:"+local_i[i]+delim );
      }
      writer.newLine();

      // Edge Attribute Tags
      writer.write( "TAGS"+delim );
      
      // global tags
      for ( int i = 0; i < atts_i.length; i++ ) {
        Set gtags = AttributeTags.getTagsOfAttribute( data, atts_i[i] );
        writer.write( setAsListString( gtags )+delim );
      }
      
      // local tags
      for ( int i = 0; i < local_i.length; i++ ) {
        Set gtags = AttributeTags.getTagsOfAttribute( local, local_i[i] );
        writer.write( setAsListString( gtags )+delim );
      }
      writer.newLine();
      
      // Edge Values
      while ( edges_i.hasNext() ) {
        Edge obj = (Edge)edges_i.next();
        writer.write( obj.getSource().getIdentifier()+delim
                      +data.getStringAttribute( obj.getIdentifier(), cytoscape.data.Semantics.INTERACTION )+delim
                      +obj.getTarget().getIdentifier()+delim);
        
        // Global Data
        for ( int i = 0; i < atts_i.length; i++ ) {
          
          String value = getPrintableValue( data, 
                                            obj.getIdentifier(),
                                            atts_i[i] );
          try {
            writer.write( value+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }

        // Network Specific Data
        for ( int i = 0; i < local_i.length; i++ ) {
          
          String value = getPrintableValue( local,
                                            obj.getIdentifier(),
                                            local_i[i] );
          try {
            writer.write( value+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }
        writer.newLine();
      }

      
      writer.close();
    } catch ( Exception ex ) {
      System.out.println( "Network Write error" );
      ex.printStackTrace();
    }
  
  }

  private static String getPrintableValue ( CyAttributes data, String id, String att ) {
    byte type = data.getType(att);

    // Integer
    if ( type == CyAttributes.TYPE_INTEGER ) {
      return data.getIntegerAttribute( id, att ).toString();
    } 

    // Double
    else if ( type == CyAttributes.TYPE_FLOATING ) {
      return data.getDoubleAttribute( id, att ).toString();
    }

    // String
    else if ( type == CyAttributes.TYPE_STRING ) {
      return data.getStringAttribute( id, att ).toString();
    }

    // Boolean
    else if ( type == CyAttributes.TYPE_BOOLEAN ) {
      return data.getBooleanAttribute( id, att ).toString();
    }

    // List
    else if ( type == CyAttributes.TYPE_SIMPLE_LIST ) {
      return data.getAttributeList( id, att ).toString();
    }
    
    // Map
    else if ( type == CyAttributes.TYPE_SIMPLE_MAP ) {
      return data.getAttributeMap( id, att ).toString();
    }
    
    else {
      return "";
    }

  }

  private static String setAsListString ( Set set ) {
    String s = "[";
    Iterator i = set.iterator();
    while ( i.hasNext() ) {
      s.concat( i.next().toString()+";" );
    }
    s.concat( "]" );
    return s;
  }


  /**
   * NETWORK
   * 
   * NODENAME ---
   * TAGS
   * TYPE
   */
  public static void loadFileToNetwork ( String file_name,
                                         String delimiter ) {


    // defer net credation until we know that there is no specified name
    CyNetwork net = null;
    CyAttributes global_node_data= null;
    CyAttributes global_edge_data= null;
    CyAttributes local_node_data= null;
    CyAttributes local_edge_data= null;


    Vector titles = null;
    int max_col = 0;
    boolean is_nodes = true;
    boolean init = false;

    List types = new ArrayList(); // a list of byte types, if defined
    List labels = new ArrayList(); // a lists of lists of the labels an attribute belongs to


    List nodes = new ArrayList();
    List edges = new ArrayList();

    try {
      File file = new File( file_name );
      BufferedReader in
        = new BufferedReader(new FileReader( file ) );
      String oneLine = in.readLine();
      int count = 0;
      while (oneLine != null  ) {

        // make sure network made
        if ( oneLine.startsWith( NODE_LABEL ) ) {
          if ( net == null ) {
            net = Cytoscape.createNetwork( file_name, false );
            global_node_data = Cytoscape.getNodeAttributes();
            global_edge_data = Cytoscape.getEdgeAttributes();
            local_node_data = NetworkAttributes.getNodeAttributes( net );
            local_edge_data = NetworkAttributes.getEdgeAttributes( net );
          }
        }
        
        // EDGE HEADER
        if ( oneLine.startsWith( NODE_LABEL+delimiter+EDGE_LABEL+delimiter+NODE_LABEL ) ) {

          init = true;
          String[] line = oneLine.split( delimiter );
          // populate the title vector
          titles = new Vector( line.length );
          for ( int i = 0; i < line.length; ++i ) {
               titles.add( line[i] );
          }
          titles.set(0, "Source" );
          titles.set(2, "Target" );
          is_nodes = false;
        }  

        // NODE HEADER
        else if ( oneLine.startsWith( NODE_LABEL ) && !oneLine.startsWith( NODE_LABEL+delimiter+EDGE_LABEL+delimiter+NODE_LABEL ) ) {
          init = true;
          String[] line = oneLine.split( delimiter );
          // populate the title vector
          titles = new Vector( line.length );
          for ( int i = 0; i < line.length; ++i ) {
               titles.add( line[i] );
          }
          is_nodes = true;
        } //node header 

        // COMMENT
        else if (oneLine.startsWith("#")) {
          // comment
        }  

        // ATTRIBUTE TYPES
        else if ( oneLine.startsWith ("TYPE") ) {
        }

        // ATTRIBUTE LABELS
        else if ( oneLine.startsWith( "LABEL") ) {
        }
        
        // NETWORK NAME
        else if ( oneLine.startsWith ("NETWORK" ) ) {
          String[] line = oneLine.split( delimiter );
          // Set name to the name on the same line as NETWORK
          net = Cytoscape.createNetwork( line[1], false );
          global_node_data = Cytoscape.getNodeAttributes();
          global_edge_data = Cytoscape.getEdgeAttributes();
          local_node_data = NetworkAttributes.getNodeAttributes( net );
          local_edge_data = NetworkAttributes.getEdgeAttributes( net );
        }
        

        // LOAD LINE
        else if ( init ) {
 
 
          // load a row
          String[] line = oneLine.split( delimiter );
          
          GraphObject obj = loadRow( line, titles, is_nodes,global_node_data, local_node_data, global_edge_data, local_edge_data );
          if ( obj != null && is_nodes ) {
            //System.out.println( "New Node: "+obj+" "+obj.getIdentifier() );
            nodes.add( obj );
          } else if ( obj != null ) {
            edges.add( obj );
          }

        } 
        
        // no init
        else {
          // no init
          System.err.println( "FILE LOAD ERROR: NO HEADER FOR: "+file_name );
        }
        
        oneLine = in.readLine();
      }
      
      in.close();
    } catch ( Exception ex ) {
      System.out.println( "File Read error" );
      ex.printStackTrace();
    }
    
    net.restoreNodes( nodes, false );
    net.restoreEdges( edges );

  }


  public static GraphObject loadRow ( String[] row, 
                                      Vector titles, 
                                      boolean is_nodes,
                                      CyAttributes global_node_data,
                                      CyAttributes local_node_data,
                                      CyAttributes global_edge_data,
                                      CyAttributes local_edge_data) {
     return loadRow( row, titles, is_nodes, new int[] {}, global_node_data, local_node_data, global_edge_data, local_edge_data );
   }

  public static GraphObject loadRow ( String[] row, 
                                      Vector titles, 
                                      boolean is_nodes,
                                      int[] restricted_colums,
                                      CyAttributes global_node_data,
                                      CyAttributes local_node_data,
                                      CyAttributes global_edge_data,
                                      CyAttributes local_edge_data) {


    ////////////////////
    // NODES
    if ( is_nodes ) {
      CyNode node = null;
      
      // iterate through until a node is found
      for ( int i = 0; i < row.length; ++i ) {
        node = Cytoscape.getCyNode( row[i], false );
        if ( node != null )
          break;
      }
      if ( node == null ) {
        node = Cytoscape.getCyNode( row[0], true );
        //System.out.println( "NODE NULL: "+row[0] );
      }

      if ( node == null ) {
        System.out.println( "NODE STILL NULL!!!!!!!" );
        return null;
      }
      
      if ( restricted_colums.length != 0 ) {
        // load only the columns given
        for ( int i = 0; i < restricted_colums.length; ++i ) {
          loadColumn( node,
                          ( String )titles.get( restricted_colums[i] ),
                          row[ restricted_colums[i] ],
                          global_node_data,
                          local_node_data);
        }
      } else {
        // load all columns
        for ( int i = 0; i < row.length; ++i ) {
          loadColumn( node,
                      ( String )titles.get( i ),
                      row[i],
                      global_node_data,
                      local_node_data);
        }
      }

      return node;
    } 

    
    ////////////////////
    // EDGES
    else {
   
      // load edges
      CyEdge edge = getEdgeForRow( row );

      if ( restricted_colums.length != 0 ) {
        // load only the columns given
        for ( int i = 3; i < restricted_colums.length; ++i ) {
          loadColumn( edge,
                          ( String )titles.get( restricted_colums[i] ),
                          row[ restricted_colums[i] ],
                          global_edge_data,
                          local_edge_data);
        }
      } else {
        // load all columns
        for ( int i = 3; i < row.length; ++i ) {
          //System.out.println( "Edge: "+edge+" Title: "+titles.get( i )+" value: "+row[i] );
          loadColumn( edge,
                          ( String )titles.get( i ),
                          row[i],
                          global_edge_data,
                          local_edge_data );
        }
      }
      return edge;
    }

  }
    
  public static CyNode getNodeForRow ( String[] row ) {
    CyNode node = null;
      
    // iterate through until a node is found
    for ( int i = 0; i < row.length; ++i ) {
      node = Cytoscape.getCyNode( row[i], false );
      if ( node != null )
        break;
    }
    if ( node == null ) {
      node = Cytoscape.getCyNode( row[0], true );
    }

    return node;

  }


  public static CyEdge getEdgeForRow ( String[] row ) {

    if ( row.length < 3 ) 
      return null;

    CyNode source;
    CyNode target;

    // source and target must be defined as row[0] and row[2]
    // the Edge Semantics.INTERACTION must be row[1]

    source = Cytoscape.getCyNode( row[0], false );
    if ( source == null ) 
      source = Cytoscape.getCyNode( row[0], true );

    target = Cytoscape.getCyNode( row[2], false );
    if ( target == null ) 
      target = Cytoscape.getCyNode( row[2], true );

    CyEdge edge = Cytoscape.getCyEdge( source, 
                                       target, 
                                       cytoscape.data.Semantics.INTERACTION,
                                       row[1],
                                       false );
    if ( edge == null )
      return Cytoscape.getCyEdge( source, 
                                       target, 
                                       cytoscape.data.Semantics.INTERACTION,
                                       row[1],
                                       true );

    return edge;
  }

  public static boolean loadColumn ( GraphObject gob,
                                     String title, 
                                     String att,
                                     CyAttributes global,
                                     CyAttributes local ) {


    // figure out what the Attibute is
    Object attribute;
    try { 
      attribute = new Double( att );
    } catch ( Exception e ) {
      attribute = att;
      // not a number, leave as string
    }
    try {
      if ( !attribute.equals( null_att ) ) {

        ////////////////////
        // Load LOCAL Data
        if ( title.startsWith( "LOCAL:") ) {
          title = title.substring(6);
          
          // Load List
          if ( att.startsWith("[") ) {
            attribute = formList( att );
            if ( attribute == null )
              return true;
            
            local.setAttributeList( gob.getIdentifier(), title, (List)attribute );
            return true;
          } // load list

          // Load Hash
          else if ( att.startsWith("{") ) {
            attribute = formMap( att );
            if ( attribute == null )
              return true;
            
            local.setAttributeMap( gob.getIdentifier(), title, (Map)attribute );
            return true;
          } 
          
          // load single
          else {
            if ( attribute instanceof Double ) {
              local.setAttribute( gob.getIdentifier(), title, (Double)attribute );
            } else {
              local.setAttribute( gob.getIdentifier(), title, (String)attribute );
            }
          }
        } // end load LOCAL data
        
        ////////////////////
        // Load GLOBAL Data
        else {
          
          // Load List
          if ( att.startsWith("[") ) {
            attribute = formList( att );
            if ( attribute == null )
              return true;
            
            global.setAttributeList( gob.getIdentifier(), title, (List)attribute );
                       return true;
          } 

          // Load Hash
          else if ( att.startsWith("{") ) {
            attribute = formMap( att );
            if ( attribute == null )
              return true;
            
            global.setAttributeMap( gob.getIdentifier(), title, (Map)attribute );
            return true;
          } 
          
          // load single
          else {
            if ( attribute instanceof Double ) {
              global.setAttribute( gob.getIdentifier(), title, (Double)attribute );
            } else {
              global.setAttribute( gob.getIdentifier(), title, (String)attribute );
            }
          }
        } // global load
      }
    }  catch ( Exception ex ) {
      ex.printStackTrace();
      System.out.print( "Error Loading node: "+gob.getIdentifier() );
      System.out.print( " attribute: "+title );
      System.out.println( " attribute value: "+attribute );
      return false;
    }
    return true;
  }


  
  public static Map formMap ( String value ) {
    //{key=value,key2=value2}

    if ( value.length() < 4 ) 
      return null;
    Map attribute = new HashMap();
    value = value.substring( 1,value.length()-1 );
    String[] delim = value.split( ", " );
    for ( int j = 0; j < delim.length; ++j ) {
      String[] key_val = delim[j].split("=");
      attribute.put( key_val[0], key_val[1] );
    }
    
    return attribute;
  }
  public static List formList ( String value ) {
    if ( value.length() < 4 ) 
      return null;
    List attribute = new ArrayList();
    value = value.substring( 1,value.length()-1 );
    String[] delim = value.split( ";" );
    for ( int j = 0; j < delim.length; ++j ) {
      attribute.add( delim[j] );
    }

    //System.out.println( "LIST MADE: "+attribute );

    return attribute;
  }


    



}
