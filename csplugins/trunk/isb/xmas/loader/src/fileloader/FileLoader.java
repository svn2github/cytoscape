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

import giny.model.*;
import ViolinStrings.Strings;

public class FileLoader {


  static Object null_att = new String("null");

  static String NODE_LABEL = "NODENAME";
  static String EDGE_LABEL = "EDGENAME";
  static String COMMENTS = "^#";
  static String delim = "\t";

  public static void saveNetworkToFile ( CyNetwork network,
                                         String file_name ) {
    try {
      File file = new File( file_name );
      BufferedWriter writer = new BufferedWriter(new FileWriter( file ));
      
      Iterator nodes_i = network.nodesIterator();
      Iterator edges_i = network.edgesIterator();

      // Node Section
      writer.write( NODE_LABEL+delim );
      
      CytoscapeData data = Cytoscape.getNodeNetworkData();
      CountedIterator atts_i = data.getDefindedAttributes();
      while (atts_i.hasNext() ) {
        writer.write( atts_i.next()+delim );
      }
      writer.newLine();
      
      while ( nodes_i.hasNext() ) {
        GraphObject obj = (GraphObject)nodes_i.next();
        writer.write( obj.getIdentifier()+delim );
        atts_i = data.getDefindedAttributes();
        while ( atts_i.hasNext() ) {
          // TODO
          Object value = data.getAttributeValueList( obj.getIdentifier(),
                                                     atts_i.next() );
          try {
            writer.write( value+delim );
          } catch ( Exception ex ) {
            System.out.println( "Error with Network output" );
            writer.write( delim );
          }
        }
        writer.newLine();
      }
    
      // Edge Section
      writer.write( NODE_LABEL+delim+EDGE_LABEL+delim+NODE_LABEL+delim );
      
      data = Cytoscape.getEdgeNetworkData();
      atts_i = data.getDefindedAttributes();
      while (atts_i.hasNext() ) {
        writer.write( atts_i.next()+delim );
      }
      writer.newLine();
      
      while ( edges_i.hasNext() ) {
        Edge obj = (Edge)edges_i.next();
        writer.write( obj.getSource().getIdentifier()+delim
                      +obj.getIdentifier()+delim
                      +obj.getTarget().getIdentifier()+delim);
        
        atts_i = data.getDefindedAttributes();
        while ( atts_i.hasNext() ) {
          // TODO
          Object value = data.getAttributeValueList( obj.getIdentifier(),
                                                     atts_i.next() );
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



  public static void saveAttributesToFile ( String file_name,
                                            boolean is_nodes,
                                            String[] attributes ) {


  }



  public static void loadFileToAttributes ( String file_name,
                                            boolean is_nodes,
                                            boolean is_delimted,
                                            String delimiter ) {
    
    Vector titles = null;
    int max_col = 0;

    Pattern p_node = Pattern.compile( NODE_LABEL );
    Pattern p_edge = Pattern.compile( EDGE_LABEL );
    Pattern p_comment = Pattern.compile( COMMENTS );
    

    try {
      File file = new File( file_name );
      BufferedReader in
        = new BufferedReader(new FileReader( file ) );
      String oneLine = in.readLine();
      int count = 0;
      while (oneLine != null  ) {

        Matcher m_node = p_node.matcher( oneLine );
        Matcher m_edge = p_edge.matcher( oneLine );
        Matcher m_comment = p_comment.matcher( oneLine );
         
        if ( m_comment.matches() ) {
          // comment
          System.out.println( "Comment line: "+oneLine );

        } else if ( m_node.matches() && !m_edge.matches() ) {
          // node header
          System.out.println( "Node Header:" +oneLine );

        } else if ( m_node.matches() && m_edge.matches() ) {
          // edge header
          System.out.println( "Edge Header:" +oneLine );
        }



        if (oneLine.startsWith("#")) {
          // comment
        } else {
          // read nodes in
          String[] line = oneLine.split( delimiter );
          if ( titles == null ) {
            // populate the title vector
            titles = new Vector( line.length );
            for ( int i = 0; i < line.length; ++i ) {
              titles.add( line[i] );
            }
          } else {
            // load a row
            if ( !loadRow( line, titles, is_nodes ) )
              System.out.println( "error loading: "+oneLine );
          }
        }        
        oneLine = in.readLine();
      }
      
      in.close();
    } catch ( Exception ex ) {
      System.out.println( "File Read error" );
      ex.printStackTrace();
    }
    
  }


  public static boolean loadRow ( String[] row, 
                                   Vector titles, 
                                   boolean is_nodes ) {
     return loadRow( row, titles, is_nodes, new int[] {} );
   }

  public static boolean loadRow ( String[] row, 
                                  Vector titles, 
                                  boolean is_nodes,
                                  int[] restricted_colums ) {


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
      }
      
      if ( restricted_colums.length != 0 ) {
        // load only the columns given
        for ( int i = 0; i < restricted_colums.length; ++i ) {
          loadNodeColumn( node,
                          ( String )titles.get( restricted_colums[i] ),
                          row[ restricted_colums[i] ] );
        }
      } else {
        // load all columns
        for ( int i = 0; i < row.length; ++i ) {
          loadNodeColumn( node,
                          ( String )titles.get( i ),
                          row[i] );
        }
      }

      return true;
    } 

    
    ////////////////////
    // EDGES
    else {
   
      // load edges
      CyEdge edge = getEdgeForRow( row );

      if ( restricted_colums.length != 0 ) {
        // load only the columns given
        for ( int i = 0; i < restricted_colums.length; ++i ) {
          loadEdgeColumn( edge,
                          ( String )titles.get( restricted_colums[i] ),
                          row[ restricted_colums[i] ] );
        }
      } else {
        // load all columns
        for ( int i = 0; i < row.length; ++i ) {
          loadEdgeColumn( edge,
                          ( String )titles.get( i ),
                          row[i] );
        }
      }
      return true;
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
                                       row[2],
                                       false );
    if ( edge == null )
      return Cytoscape.getCyEdge( source, 
                                       target, 
                                       cytoscape.data.Semantics.INTERACTION,
                                       row[2],
                                       true );

    return edge;
  }

  public static boolean loadNodeColumn ( CyNode node,
                                         String title, 
                                         String att ) {

    //System.out.println( "Load: "+node+" title: "+title+ ": "+att );
    

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
        if ( attribute instanceof String ) {
          if ( att.startsWith("[") ) {
            attribute = formList( att );
            if ( attribute == null )
              return true;

            for ( Iterator i = ( ( List )attribute).iterator(); i.hasNext(); ) {
              //System.out.println( "List: "+node );
              Cytoscape.getNodeNetworkData().addAttributeListValue( node.getIdentifier(),
                                                                    title,
                                                                    i.next() );
            }


            return true;
          }
        }

        // set value
        //System.out.println( "Loading: "+node.getIdentifier()+" "+title+ " "+attribute );
        Cytoscape.setNodeAttributeValue( node, title, attribute );
      }
    }  catch ( Exception ex ) {
      ex.printStackTrace();
      System.out.print( "Error Loading node: "+node.getIdentifier() );
      System.out.print( " attribute: "+title );
      System.out.println( " attribute value: "+attribute );
      return false;
    }
    return true;
  }


  
  public static boolean loadEdgeColumn ( CyEdge edge,
                                         String title, 
                                         String att ) {

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
        if ( attribute instanceof String ) {
          if ( att.startsWith("[") ) {
            attribute = formList( att );
            if ( attribute == null )
              return false;
            for ( Iterator i = ( ( List )attribute).iterator(); i.hasNext(); ) {
              Cytoscape.getEdgeNetworkData().addAttributeListValue( edge.getIdentifier(),
                                                                    title,
                                                                    i.next() );
            }


            return true;
          }
        }

        // set value
        //System.out.println( "Loading: "+edge.getIdentifier()+" "+title+ " "+attribute );
        Cytoscape.setEdgeAttributeValue( edge, title, attribute );
      }
    }  catch ( Exception ex ) {
      ex.printStackTrace();
      System.out.print( "Error Loading edge: "+edge.getIdentifier() );
      System.out.print( " attribute: "+title );
      System.out.println( " attribute value: "+attribute );
      return false;
    }
    return true;
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
