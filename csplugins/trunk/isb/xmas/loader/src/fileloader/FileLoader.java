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
      
            
      //Iterator nodes_i = network.nodesIterator();
      //Iterator edges_i = network.edgesIterator();
      int[] nodes_i = network.getNodeIndicesArray();
      int[] edges_i = network.getEdgeIndicesArray();
      

      // Node Section
           
      // Global Data
      CyAttributes data = Cytoscape.getNodeAttributes();
      String[] atts_i = data.getAttributeNames();
      Arrays.sort( atts_i );

      // Local Data
      CyAttributes local = NetworkAttributes.getNodeAttributes( network );
      String[] local_i = local.getAttributeNames();
      Arrays.sort( local_i );

      // Header

      // write network name
      //writer.write( network.getIdentifier() );

      //////////////////
      // Node Attributes

      writer.write( NODE_LABEL+delim );

      // write global names
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( atts_i[i]+delim );
      }
     
      // write local names, e.g. "LOCAL:____"
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( "LOCAL:"+local_i[i]+delim );
      }
      writer.newLine();

      writer.write( "TYPES"+delim );

       // global tags
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( getTypeAsString( data.getType( atts_i[i] ) )+delim );
      }
      
      // local tags
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( getTypeAsString( local.getType( local_i[i] ) )+delim );
      }
      writer.newLine();


      
      //////////////
      // Node Values

      // iterate through nodes
      //while ( nodes_i.hasNext() ) {
      //  GraphObject obj = (GraphObject)nodes_i.next();
      for ( int j = nodes_i.length-1; j >= 0; j-- ) {
        GraphObject obj = (GraphObject)network.getNode( nodes_i[j] );
        writer.write( obj.getIdentifier()+delim );
        
        // Global Data
        for ( int i = 0; i < atts_i.length; i++ ) {
          
          Object value = getPrintableValue( data, 
                                            obj.getIdentifier(),
                                            atts_i[i] );
          if ( value == null )
            value = new String("");

          try {
            writer.write( value.toString()+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }

        // Network Specific Data
        for ( int i = 0; i < local_i.length; i++ ) {
          
          Object value = getPrintableValue( local,
                                            obj.getIdentifier(),
                                            local_i[i] );
          if ( value == null )
            value = new String("");
          try {
            writer.write( value.toString()+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }
        writer.newLine();
      }
    
      ////////////////////
      // Edge Section
      
      // Global Data
      data = Cytoscape.getEdgeAttributes();
      atts_i = data.getAttributeNames();
      Arrays.sort( atts_i );

      // Local Data
      local = NetworkAttributes.getEdgeAttributes( network );
      local_i = local.getAttributeNames();
      Arrays.sort( local_i );

      // Header
 
      writer.write( NODE_LABEL+delim+EDGE_LABEL+delim+NODE_LABEL+delim );

     // write global names
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( atts_i[i]+delim );
      }
     
      // write local names, e.g. "LOCAL:____"
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( "LOCAL:"+local_i[i]+delim );
      }
      writer.newLine();

      // Edge Attribute Types
      
      writer.write( "TYPES"+delim+"TYPE_STRING"+delim+"TYPE_STRING"+delim );

       // global tags
      for ( int i = 0; i < atts_i.length; i++ ) {
        writer.write( getTypeAsString( data.getType( atts_i[i] ) )+delim );
      }
      
      // local tags
      for ( int i = 0; i < local_i.length; i++ ) {
        writer.write( getTypeAsString( local.getType( local_i[i] ) )+delim );
      }
      writer.newLine();

      
      
      // Edge Values
      //while ( edges_i.hasNext() ) {
      //  Edge obj = (Edge)edges_i.next();
      for ( int j = edges_i.length -1; j >= 0; j-- ) {
        Edge obj = network.getEdge( edges_i[j] );
        writer.write( obj.getSource().getIdentifier()+delim
                      +data.getStringAttribute( obj.getIdentifier(), cytoscape.data.Semantics.INTERACTION )+delim
                      +obj.getTarget().getIdentifier()+delim);
        
        // Global Data
        for ( int i = 0; i < atts_i.length; i++ ) {
          
          Object value = getPrintableValue( data, 
                                            obj.getIdentifier(),
                                            atts_i[i] );
          if ( value == null )
            value = new String("");
          try {
            writer.write( value.toString()+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }

        // Network Specific Data
        for ( int i = 0; i < local_i.length; i++ ) {
          
          Object value = getPrintableValue( local,
                                            obj.getIdentifier(),
                                            local_i[i] );
          if ( value == null )
            value = new String("");
          try {
            writer.write( value.toString()+delim );
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

  private static Object getPrintableValue ( CyAttributes data, String id, String att ) {
    byte type = data.getType(att);

    // Integer
    if ( type == CyAttributes.TYPE_INTEGER ) {
      return data.getIntegerAttribute( id, att );
    } 

    // Double
    else if ( type == CyAttributes.TYPE_FLOATING ) {
      return data.getDoubleAttribute( id, att );
    }

    // String
    else if ( type == CyAttributes.TYPE_STRING ) {
      return data.getStringAttribute( id, att );
    }

    // Boolean
    else if ( type == CyAttributes.TYPE_BOOLEAN ) {
      return data.getBooleanAttribute( id, att );
    }

    // List
    else if ( type == CyAttributes.TYPE_SIMPLE_LIST ) {
      return data.getAttributeList( id, att );
    }
    
    // Map
    else if ( type == CyAttributes.TYPE_SIMPLE_MAP ) {
      return data.getAttributeMap( id, att );
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


    // defer net creation until we know that there is no specified name
    CyNetwork net = null;
    CyAttributes global_node_data= null;
    CyAttributes global_edge_data= null;
    CyAttributes local_node_data= null;
    CyAttributes local_edge_data= null;


    Vector titles = null;
    int max_col = 0;
    boolean is_nodes = true;
    boolean init = false;

    Vector types = null; // a list of byte types, if defined
    Vector labels = null; // a lists of lists of the labels an attribute belongs to


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
          types = new Vector( line.length );
          for ( int i = 0; i < line.length; ++i ) {
               titles.add( line[i] );
               types.add( "null" );
          }
          titles.set(0, "Source" );
          titles.set(2, "Target" );
          is_nodes = false;
          labels = null;
        }  

        // NODE HEADER
        else if ( oneLine.startsWith( NODE_LABEL ) && !oneLine.startsWith( NODE_LABEL+delimiter+EDGE_LABEL+delimiter+NODE_LABEL ) ) {
          init = true;
          String[] line = oneLine.split( delimiter );
          // populate the title vector
          titles = new Vector( line.length );
          types = new Vector( line.length );
          for ( int i = 0; i < line.length; ++i ) {
               titles.add( line[i] );
               types.add( "null" );
          }
          labels = null;
          is_nodes = true;
        } //node header 

        // COMMENT
        else if (oneLine.startsWith("#")) {
          // comment
        }  

        // ATTRIBUTE TYPES
        else if ( oneLine.startsWith ("TYPE") ) {
          String[] line = oneLine.split( delimiter );
          // assign types
          for ( int i = 0; i < line.length; ++i ) {
            types.add( line[i] );
          }
        }

        // ATTRIBUTE TAGS
        else if ( oneLine.startsWith( "TAG") ) {
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
          
          GraphObject obj = loadRow( line, titles, types, is_nodes,global_node_data, local_node_data, global_edge_data, local_edge_data );
        
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
                                      Vector types,
                                      boolean is_nodes,
                                      CyAttributes global_node_data,
                                      CyAttributes local_node_data,
                                      CyAttributes global_edge_data,
                                      CyAttributes local_edge_data) {
    return loadRow( row, titles, types, is_nodes, new int[] {}, global_node_data, local_node_data, global_edge_data, local_edge_data );
   }

  public static GraphObject loadRow ( String[] row, 
                                      Vector titles,
                                      Vector types,
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
      
      // NODENAME *must* be in Col[0]
      node = Cytoscape.getCyNode( row[0], true );
 
      if ( node == null ) {
        System.out.println( "NODE STILL NULL!!!!!!!" );
        return null;
      }
      
      if ( restricted_colums.length != 0 ) {
        // load only the columns given
        for ( int i = 0; i < restricted_colums.length; ++i ) {
          loadColumn( node,
                      ( String )types.get( restricted_colums[i] ),
                      ( String )titles.get( restricted_colums[i] ),
                      row[ restricted_colums[i] ],
                      global_node_data,
                      local_node_data);
        }
      } else {
        // load all columns
        for ( int i = 1; i < row.length; ++i ) {
          loadColumn( node,
                      ( String )types.get( i ),
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
                       ( String )types.get( restricted_colums[i] ),
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
                      ( String )types.get( i ),
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
                                     String t,
                                     String title, 
                                     String att,
                                     CyAttributes global,
                                     CyAttributes local ) {
    
    if ( title.equalsIgnoreCase(NODE_LABEL) ) {
      return false;
    } 

    if ( att.equalsIgnoreCase( "null" ) || att.equals( "" ) || att.equalsIgnoreCase( "NaN" ) )
      return false;

    // give precedence to what has been loaded already
    byte set_type = global.getType( title ); 
    byte type = getTypeAsByte(t);
    if ( set_type != CyAttributes.TYPE_UNDEFINED && set_type != type )
      type = set_type;
   
    if ( type == CyAttributes.TYPE_UNDEFINED ) {
      // the type was not specified and no values have been set yet
      type = determineType( att );
    }      

    // get the attribute according to type
    Object attribute = castObjectByType( type, att );
     
    try {
      ////////////////////
      // Load LOCAL Data
      if ( title.startsWith( "LOCAL:") ) {
        title = title.substring(6);
        loadTypedValue( local, type, gob.getIdentifier(), title, attribute );
      }
      
      ////////////////////
      // Load GLOBAL Data
      else {
        loadTypedValue( global, type, gob.getIdentifier(), title, attribute );
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

  private static byte determineType ( String value ) {
    // Test for Double
    try { 
      Object attribute = new Double( value.toString() );
      return CyAttributes.TYPE_FLOATING;
    } catch ( Exception e ) {}
    
    // Test for Boolean
    try { 
      if ( value.toString().equals("true") || 
           value.toString().equals("false") ) {
        return CyAttributes.TYPE_BOOLEAN;
      }
    } catch ( Exception e ) {}
    
    // Test for List
    if ( value.startsWith( "[" ) && value.endsWith( "]" ) )
      return CyAttributes.TYPE_SIMPLE_LIST;
    
    // Test for Map
    if ( value.startsWith( "{" ) && value.endsWith( "}" ) )
      return CyAttributes.TYPE_SIMPLE_MAP;
    
    // Default is String
    return CyAttributes.TYPE_STRING;
      
  }

  private static boolean loadTypedValue ( CyAttributes data,
                                          byte type,
                                          String id,
                                          String title,
                                          Object value ) {
    
    // we should be guarenteed that we have a properly typed value, or else null
    if ( value == null )
      return false;
    
    // Boolean
    if ( type == CyAttributes.TYPE_BOOLEAN )
      data.setAttribute( id, title, (Boolean)value );
    
    // Integer
    else if ( type == CyAttributes.TYPE_INTEGER )
      data.setAttribute( id, title,(Integer)value );
    
    // Double
    else if ( type == CyAttributes.TYPE_FLOATING )
      data.setAttribute( id, title,(Double)value );
    
    // String
    else if ( type == CyAttributes.TYPE_STRING ) 
      data.setAttribute( id, title,(String)value );
    
    // List
    else if ( type == CyAttributes.TYPE_SIMPLE_LIST ) 
      data.setAttributeList( id, title,(List)value );
    
    // Map
    else if ( type == CyAttributes.TYPE_SIMPLE_MAP )
      data.setAttributeMap( id, title,(Map)value );
    
    else
      return false;
    
    return true;
  }


  private static Object castObjectByType ( byte type,
                                           String value ) {

    Object object = null;

    // Boolean
    if ( type == CyAttributes.TYPE_BOOLEAN )
      try {
        object = Boolean.valueOf( value );
      } catch ( Exception e ) {}

    // Integer
    else if ( type == CyAttributes.TYPE_INTEGER )
      try {
        object = Integer.valueOf( value );
      } catch ( Exception e ) {}

    // Double
    else if ( type == CyAttributes.TYPE_FLOATING )
      try {
        object = Double.valueOf( value );
      } catch ( Exception e ) {}

    // String
    else if ( type == CyAttributes.TYPE_STRING ) {
      object = value;
    }

    // List
    // only strings and doubles allowed in Lists
    else if ( type == CyAttributes.TYPE_SIMPLE_LIST ) {
      if ( value.startsWith("[") ) {
        object = formList( value );
      }
    }

    // Map
    // only strings and doubles allowed in Maps
    else if ( type == CyAttributes.TYPE_SIMPLE_MAP ) {
      if ( value.startsWith("[") ) {
        object = formMap( value );
      }
    } else {
    }


    return object;
      
    



  }
  

  
  private static Map formMap ( String value ) {
    //{key=value,key2=value2}

    if ( value.length() < 4 ) 
      return null;
    Map attribute = new HashMap();
    value = value.substring( 1,value.length()-1 );
    String[] delim = value.split( ", " );
    for ( int j = 0; j < delim.length; ++j ) {
      String[] key_val = delim[j].split("=");

      Object val;
      try {
        val = new Double( key_val[1] );
      } catch ( Exception e ) {
        val = key_val[1];
      }

      attribute.put( key_val[0], val );
    }
    
    return attribute;
  }

  private static List formList ( String value ) {
    if ( value.length() < 4 ) 
      return null;
    List attribute = new ArrayList();
    value = value.substring( 1,value.length()-1 );
    String[] delim = value.split( ";" );
    for ( int j = 0; j < delim.length; ++j ) {

      Object val;
      try {
        val = new Double( delim[j] );
      } catch ( Exception e ) {
        val = delim[j];
      }


      attribute.add( val );
    }

    //System.out.println( "LIST MADE: "+attribute );

    return attribute;
  }

  private static String getTypeAsString ( byte type ) {
     
    // Integer
    if ( type == CyAttributes.TYPE_INTEGER ) {
      return "TYPE_INTEGER";
    } 

    // Double
    else if ( type == CyAttributes.TYPE_FLOATING ) {
      return "TYPE_FLOATING";
    }

    // String
    else if ( type == CyAttributes.TYPE_STRING ) {
      return "TYPE_STRING";
    }

    // Boolean
    else if ( type == CyAttributes.TYPE_BOOLEAN ) {
      return "TYPE_BOOLEAN";
    }

    // List
    else if ( type == CyAttributes.TYPE_SIMPLE_LIST ) {
      return "TYPE_SIMPLE_LIST";
    }
    
    // Map
    else if ( type == CyAttributes.TYPE_SIMPLE_MAP ) {
      return "TYPE_SIMPLE_MAP";
    }
    
    else {
      return "";
    }
  }
  
  private static byte getTypeAsByte ( String type ) {
     
    // Integer
    if ( type.equalsIgnoreCase( "TYPE_INTEGER") ||
         type.equalsIgnoreCase( "INTEGER" ) ||
         type.equalsIgnoreCase( "INT" ) ) {
      return CyAttributes.TYPE_INTEGER;
    } 

    // Double
    else if ( type.equalsIgnoreCase( "TYPE_FLOATING") ||
              type.equalsIgnoreCase( "FLOATING" ) ||
              type.equalsIgnoreCase( "FLOATING_POINT" ) ||
              type.equalsIgnoreCase( "DOUBLE" ) ) {
      return CyAttributes.TYPE_FLOATING;
    }

    // String
    else if ( type.equalsIgnoreCase( "TYPE_STRING") ||
              type.equalsIgnoreCase( "STRING") ) {
      return CyAttributes.TYPE_STRING;
    }

    // Boolean
    else if ( type.equalsIgnoreCase( "TYPE_BOOLEAN") ||
              type.equalsIgnoreCase( "BOOLEAN") ||
              type.equalsIgnoreCase( "BOOL") ) {
      return CyAttributes.TYPE_BOOLEAN;
    }

    // List
    else if ( type.equalsIgnoreCase( "TYPE_SIMPLE_LIST") ||
              type.equalsIgnoreCase( "SIMPLE_LIST") ||
              type.equalsIgnoreCase( "LIST") ) {
      return CyAttributes.TYPE_SIMPLE_LIST;
    }
    
    // Map
    else if ( type.equalsIgnoreCase( "TYPE_SIMPLE_MAP") ||
              type.equalsIgnoreCase( "SIMPLE_MAP") ||
              type.equalsIgnoreCase( "MAP") ) {
      return CyAttributes.TYPE_SIMPLE_MAP;
    }
    
    else {
      return CyAttributes.TYPE_UNDEFINED;
    }
  }
  


}
