package fileloader;

import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.swing.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.readers.*;
import cytoscape.plugin.*;
import cytoscape.data.*;

import giny.model.*;

public class FileLoader {


  public static void loadCytoscape ( String in_file) {
    try {
                  
      FileInputStream fis = new FileInputStream(in_file);

      ///////////////
      // nodes.txt

      // first get nodes.txt
      ZipInputStream zis = new  ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while( (entry = zis.getNextEntry()) != null) {
        System.out.println("Extracting: " +entry.getName());
                    
        if ( entry.getName().equals( "nodes.txt") ) {
                    
          System.out.println("Extracting Only Nodes: " +entry);
          // nodes.txt found
          StringBuffer entry_buffer = new StringBuffer();
          byte[] buf = new byte[1024];
          int len;
          while ( (len = zis.read(buf) ) > 0) {
            if ( len < 1024 ) {
              byte[] last = new byte[len];
              for ( int i = 0; i < len; ++i ) {
                last[i] = buf[i];
              }
              entry_buffer.append( new String( last ) );
            } else 
              entry_buffer.append( new String( buf ) );
          }

          // use SpreadSheet loading stuff to load
                    
          String s = entry_buffer.toString();
          String[] sa = s.split( "\n" );

          // get titles
          Vector titles = new Vector();
          String[] ta = sa[0].split(";");
          for ( int i = 0; i < ta.length; ++i )
            titles.add( ta[i] );
                    

          // load nodes by row
          for ( int i = 1; i < sa.length; ++i ) {
            System.out.println( i+ ".) "+sa[i] );
            FileLoader.loadRow( sa[i].split(";"), titles, true ); 
          }
                    
                    
          break;
        }
      }
      zis.close();
                  
    } catch ( Exception e ) {
      e.printStackTrace();
    }


    try {
      ///////////////
      // edges.txt
      FileInputStream fis = new FileInputStream("/users/xmas/CSBI/cytoscape/temp/outfile.zip");

      // first get edges.txt
      ZipInputStream zis = new  ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while( (entry = zis.getNextEntry()) != null) {
        //System.out.println("Extracting: " +entry);
                    
        if ( entry.getName().equals("edges.txt") ) {
                     
          System.out.println("Extracting Only Edges: " +entry);
          // edges.txt found
          StringBuffer entry_buffer = new StringBuffer();
          byte[] buf = new byte[1024];
          int len;
          while ( (len = zis.read(buf) ) > 0) {
            if ( len < 1024 ) {
              byte[] last = new byte[len];
              for ( int i = 0; i < len; ++i ) {
                last[i] = buf[i];
              }
              entry_buffer.append( new String( last ) );
            } else 
              entry_buffer.append( new String( buf ) );
          }

          // use SpreadSheet loading stuff to load
          String s = entry_buffer.toString();
          String[] sa = s.split( "\n" );

          // get titles
          Vector titles = new Vector();
          String[] ta = sa[0].split(";");
          for ( int i = 0; i < ta.length; ++i )
            titles.add( ta[i] );
                    

          // load nodes by row
          for ( int i = 1; i < sa.length; ++i ) {
            System.out.println( i+ ".) "+sa[i] );
            FileLoader.loadRow( sa[i].split(";"), titles, false ); 
          }
                                                          
          break;
        }
      }
      zis.close();
                  
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    try {
      ///////////////
      // networks..
              
      FileInputStream fis = new FileInputStream("/users/xmas/CSBI/cytoscape/temp/outfile.zip");

      ZipInputStream zis = new  ZipInputStream(new BufferedInputStream(fis));
      ZipEntry entry;
      while((entry = zis.getNextEntry()) != null) {
                    
                    
        if ( entry.getName() == "edges.txt" || entry.getName() == "nodes.txt" ) {
          continue;
        }

        System.out.println("Extracting Network: " +entry);
                    

        StringBuffer entry_buffer = new StringBuffer();
        byte[] buf = new byte[1024];
        int len;
        while ( (len = zis.read(buf) ) > 0) {
          if ( len < 1024 ) {
            byte[] last = new byte[len];
            for ( int i = 0; i < len; ++i ) {
              last[i] = buf[i];
            }
            entry_buffer.append( new String( last ) );
          } else 
            entry_buffer.append( new String( buf ) );
                      
        }

        GraphReader reader = null;
        if ( entry.getName().endsWith( "gml" ) ) {
          // gml file found
          // pass the whole string to the GMLTree for reading
          reader = new GMLReader( entry_buffer.toString(), true );
          // have the GraphReader read the given file
                      
        } else if ( entry.getName().endsWith( "sif" ) ) {
          // create the sif file reader
          reader = new InteractionsReader( Cytoscape.getCytoscapeObj().getBioDataServer(), 
                                           Semantics.getDefaultSpecies(Cytoscape.getCurrentNetwork(), Cytoscape.getCytoscapeObj() ),
                                           entry_buffer.toString(), 
                                           true);
                      
        } else {
          continue;
        }

        try {
          reader.read();
        } catch ( Exception e ) {
          System.err.println( "Loader plugin unable to load entry: "+entry );
          e.printStackTrace();
          continue;
        }



        // get the RootGraph indices of the nodes and
        // edges that were just created
        int[] nodes = reader.getNodeIndicesArray();
        int[] edges = reader.getEdgeIndicesArray();
                    
        if ( nodes == null ) {
          System.err.println( "reader returned null nodes" );
        }
                    
        if ( edges == null ) {
          System.err.println( "reader returned null edges" );
        }
                    
        // Create a new cytoscape.data.CyNetwork from these nodes and edges
        CyNetwork network = Cytoscape.createNetwork( nodes, edges, entry.getName() );
                    
        if (  entry.getName().endsWith( "gml" ) ) {
          network.putClientData( "GML", reader );
                       
          System.out.println( "LNV: "+Cytoscape.getNetworkView( network.getIdentifier() ) );
                    
          if ( Cytoscape.getNetworkView( network.getIdentifier() ) != null ) {
            reader.layout( Cytoscape.getNetworkView( network.getIdentifier() ) );
          }
        }
                                  

      }
      zis.close();
                  
    } catch ( Exception e ) {
      e.printStackTrace();
    }
  }

  public static void saveCytoscape ( String outFilename ) {
                  
    try {
      String lineSep = System.getProperty("line.separator");
      ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(outFilename));

      //////////////
      // NODES.TXT

      // first save all of the nodes and their attributes as a tab delimited text file
      zip.putNextEntry( new ZipEntry("nodes.txt") );
      List nodes_list = Cytoscape.getRootGraph().nodesList();
      String[] node_attributes = Cytoscape.getNodeAttributesList();
                
      StringBuffer buffer = new StringBuffer();
      for ( int i = 0; i < node_attributes.length; ++i ) {
        buffer.append( node_attributes[i]+";" );
      }
      buffer.append( lineSep );
      byte[] bytes = buffer.toString().getBytes();
      zip.write( bytes, 0, bytes.length );

      for ( Iterator it = nodes_list.iterator(); it.hasNext(); ) {
        CyNode node = ( CyNode )it.next();
        buffer = new StringBuffer();
        for ( int i = 0; i < node_attributes.length; ++i ) {
          buffer.append( Cytoscape.getNodeAttributeValue( node, node_attributes[i] )+";" );
        }
        buffer.append( lineSep );
        bytes = buffer.toString().getBytes();
        zip.write( bytes, 0, bytes.length );
      }
      zip.closeEntry();

      //////////////
      // EDGES.TXT
                
      // save all of the edges as aif file
      zip.putNextEntry( new ZipEntry("edges.txt") );
      List edges_list = Cytoscape.getRootGraph().edgesList();
      String[] edge_attributes = Cytoscape.getEdgeAttributesList();
                
      buffer = new StringBuffer();
      buffer.append( "Source;Interaction;Target;" );
      for ( int i = 0; i < edge_attributes.length; ++i ) {
        buffer.append( edge_attributes[i]+";" );
      }
      buffer.append( lineSep );
      bytes = buffer.toString().getBytes();
      zip.write( bytes, 0, bytes.length );

      for ( Iterator it = edges_list.iterator(); it.hasNext(); ) {
        CyEdge edge = ( CyEdge )it.next();
        buffer = new StringBuffer();
        buffer.append( edge.getSourceNode().getIdentifier()+";" );
        buffer.append( Cytoscape.getEdgeAttributeValue( edge, Semantics.INTERACTION )+";" );
        buffer.append( edge.getTargetNode().getIdentifier()+";" );

        for ( int i = 0; i < edge_attributes.length; ++i ) {
          buffer.append( Cytoscape.getEdgeAttributeValue( edge, edge_attributes[i] )+";" );
        }
        buffer.append( lineSep );
        bytes = buffer.toString().getBytes();
        zip.write( bytes, 0, bytes.length );
      }
      zip.closeEntry();


      ///////////////
      // Networks 

      // save each Network with a view as GML file,
      // and save each Network without a view as a SIF file
                
      Set networks = Cytoscape.getNetworkSet();
      for ( Iterator iter = networks.iterator(); iter.hasNext(); ) {
        String id = ( String )iter.next();
        CyNetwork network = Cytoscape.getNetwork( id );
        if ( Cytoscape.getNetworkView( id ) == null ) {
          // create SIF file from the network
          zip.putNextEntry( new ZipEntry(network.getIdentifier()+". "+network.getTitle()+".sif" ));
                   
          Set connected_nodes = new HashSet();
          List edge_list = network.edgesList();
          for ( Iterator e_iter = edge_list.iterator(); e_iter.hasNext(); ) {

            Edge edge = ( Edge )e_iter.next();
            StringBuffer sb = new StringBuffer ();
            String source_name = ( String )network.getNodeAttributeValue( edge.getSource(),
                                                                          Semantics.CANONICAL_NAME );
            String target_name = ( String )network.getNodeAttributeValue( edge.getTarget(),
                                                                          Semantics.CANONICAL_NAME );
            String interaction_name = ( String )network.getEdgeAttributeValue( edge, 
                                                                               Semantics.INTERACTION );
            sb.append( source_name +" ");
            sb.append( interaction_name+" ");
            sb.append( target_name);
            sb.append(lineSep);
            connected_nodes.add( edge.getTarget() );
            connected_nodes.add( edge.getSource() );

            bytes = sb.toString().getBytes();
            zip.write( bytes, 0, bytes.length );
          }

          List node_list = network.nodesList();
          for ( Iterator n_iter = node_list.iterator(); n_iter.hasNext(); ) {
            Node node = ( Node )n_iter.next();
            if ( !connected_nodes.contains( node ) ) {
              bytes = ( ( String )network.getNodeAttributeValue( node, Semantics.CANONICAL_NAME ) ).getBytes();
              zip.write(  bytes, 0, bytes.length );
            }
          }
        } else {
          // view exits, create GML from the network view
          zip.putNextEntry( new ZipEntry(network.getTitle()+".gml") );
          GMLTree result = new GMLTree( Cytoscape.getNetworkView(id) );
          bytes = result.toString().getBytes();
          zip.write( bytes, 0, bytes.length );
        }
        zip.closeEntry();
      }

      ///////////////
      // OTHER FILES WILL GO HERE
                
      // network inheritance tree
      // filters
      // properties

      zip.close();

    } catch ( Exception e ) {
      e.printStackTrace();
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
