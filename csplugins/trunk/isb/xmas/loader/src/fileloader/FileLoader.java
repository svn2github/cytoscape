package fileloader;

import javax.swing.*;
import cytoscape.*;
import java.io.*;

import java.util.*;

import cytoscape.*;
import cytoscape.data.Semantics;
public class FileLoader {

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

    try {
      File file = new File( file_name );
      BufferedReader in
        = new BufferedReader(new FileReader( file ) );
      String oneLine = in.readLine();
      int count = 0;
      while (oneLine != null  ) {
         
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

  public static boolean loadRow ( String[] row, Vector titles, boolean is_nodes ) {

    if ( is_nodes ) {
      CyNode node = null;
      
      // iterate through until a node is found
      for ( int i = 0; i < row.length; ++i ) {
        node = Cytoscape.getCyNode( row[i], false );
        if ( node != null )
          break;
      }
      if ( node != null )
        System.out.println( "NOde exists: "+node.getIdentifier() );
      // if no node is found, create one with the first column name
      if ( node == null )
        node = Cytoscape.getCyNode( row[0], true );

      System.out.println( "Loading data for: "+node.getIdentifier() );


      
      // now place the attributes
      for ( int i = 0; i < row.length; ++i ) {
        Object attribute;
        try {
          attribute = new Double( row[i] );
        } catch ( Exception e ) {
          // not a number, leave as string
          attribute = row[i];
        }
        try {
          Cytoscape.setNodeAttributeValue( node, (String)titles.get( i ), attribute );
        } catch ( Exception ex ) {
          ex.printStackTrace();
          System.out.print( "Error Loading node: "+node.getIdentifier() );
          System.out.print( " attribute: "+(String)titles.get( i ) );
          System.out.println( " attribute value: "+attribute );

        }
        }
      return true;
    } else {
      CyNode source = Cytoscape.getCyNode( row[0], true );
      CyNode target = Cytoscape.getCyNode( row[2], true );
      CyEdge edge = Cytoscape.getCyEdge( source, target, Semantics.INTERACTION, row[1], true );
      for ( int i = 0; i < row.length; ++i ) {
        Object attribute;
        try {
          attribute = new Double( row[i] );
        } catch ( Exception e ) {
          // not a number, leave as string
          attribute = row[i];
        }
        try {
          Cytoscape.setEdgeAttributeValue( edge, (String)titles.get( i ), attribute );
        } catch ( Exception E ) {
          Cytoscape.setEdgeAttributeValue( edge, (String)titles.get( i ), attribute.toString() );
        }
      }
      return true;
      
    }

  }
    

    



}
