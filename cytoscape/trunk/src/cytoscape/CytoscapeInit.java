package cytoscape;

import java.util.ArrayList;
import java.io.File;
import cytoscape.data.readers.TextHttpReader;
import ViolinStrings.Strings;

/**
 * Cytoscape Init is responsible for starting Cytoscape in a way that makes sense.
 * 
 * The two main modes of running Cytoscape are either in "headless" mode or in "script" mode. This class will use the command-line options to figure out which mode is desired, and run things accordingly.
 *
 * The order for doing things will be the following:
 * 1. deterimine script mode, or headless mode
 * 2. get options from properties files
 * 3. get options from command line ( these overwrite properties )
 * 4. Load all Networks
 * 5. Load all Data
 * 6. Load all Plugins
 * 7. Initialize all plugins, in order if specified.
 * 8. Start Desktop/ Print Output exit.
 */

public class CytoscapeInit {

  // variables available to the command line
  String bioDataServer;
  boolean noCanonicalization;
  ArrayList expressionFiles = new ArrayList();
  boolean inExpression = false;
  ArrayList graphFiles = new ArrayList();
  boolean inGraph = false;
  ArrayList edgeAttributes = new ArrayList();
  boolean inEdge = false;
  ArrayList nodeAttributes = new ArrayList();
  boolean inNode = false;
  ArrayList pluginFiles = new ArrayList();
  boolean inPlugin = false;
  String species;
  
  boolean helpRequested;

  /**
   * Parsing for the command line.
   *<ol>
   * <li> b : BioDataServer : BDS [manifest file]| location of BioDataServer manifest file
   * <li> c : noCanonicalization  | suppress automatic canonicalization
   * <li> e : expression [file name] | expression 
   * <li> g : graph [graph files] | gml, sif, cyf files
   * <li> h : help | shows help and exits
   * <li> i : interaction [interaction files] | sif files
   * <li> j : edgeAttributes [attribute files] | old-style edge attribute files
   * <li> n : nodeAttributes [attributes files] | old-style node attribute files
   * <li> s : species [species name] | the name of a species for wchi there is information
  */
  public void parseCommandLine ( String[] args ) {
    System.out.println( "Parsing command line" );

    for( int i = 0; i < args.length; ++i ) {
      System.out.println( i+") "+args[i] );
    }


    int i = 0;
    System.out.println( i +" "+args+" "+args.length );
    while ( i < args.length ) {

      System.out.println( i+". "+args[i] );

      // help
      if ( Strings.isLike( args[i], "-h",0, true ) ||
           Strings.isLike( args[i], "--h",0, true ) ||
           Strings.isLike( args[i], "-help",0, true ) ||
           Strings.isLike( args[i], "--help",0, true ) ) {
        helpRequested = true;
        return;
      }


      // BioDataServer
      else if ( Strings.isLike( args[i], "-b",0, true ) ||
           Strings.isLike( args[i], "-bioDataServer",0, true ) ||
           Strings.isLike( args[i], "-BDS",0, true ) ) {
        i++;
        if ( badArgs(args, i ) ) 
          return;

        System.out.println( "BDS: "+args[i] );
        bioDataServer = args[i];
        i++;
      } 
      
      // species
      else if ( Strings.isLike( args[i], "-s", 0, true ) ||
                Strings.isLike( args[i], "-species", 0, true ) ) {
        i++;
        if ( badArgs(args, i ) ) 
          return;
        System.out.println( "Species set to: "+args[i] );
        species = args[i];
        i++;
      }

      // noCanonicalization
      else if ( Strings.isLike( args[i], "-c", 0, true ) ||
                Strings.isLike( args[i], "-noCanonicalization", 0, true ) ) {
        i++;
        System.out.println( "there will be no canonicalization" );
        noCanonicalization = true;
      }
           
      // graph files
      else if ( Strings.isLike( args[i], "-g", 0, true ) ||
                Strings.isLike( args[i], "-i", 0, true ) ||
                Strings.isLike( args[i], "-graph", 0, true ) ||
                Strings.isLike( args[i], "-interaction", 0, true ) ) {
        resetFalse();
        inGraph = true;
        i++;
        if ( badArgs(args, i ) ) 
          return;

        System.out.println( "Adding "+args[i]+" to the Graph files" );
        graphFiles.add( args[i] );
        i++;
      }
     

      // expression files
      else if ( Strings.isLike( args[i], "-e", 0, true ) ||
                Strings.isLike( args[i], "-expression", 0, true ) ) {
        resetFalse();
        i++;
        if ( badArgs(args, i ) ) 
          return;

        inExpression = true;
        System.out.println( "Adding "+args[i]+" to the expression data list");
        expressionFiles.add( args[i] );
        i++;
      }
     

      // node attributes
      else if ( Strings.isLike( args[i], "-n", 0, true ) ||
                Strings.isLike( args[i], "-nodeAttributes", 0, true ) ) {
        resetFalse();
        inNode = true;
        i++;
        if ( badArgs(args, i ) ) 
          return;

        System.out.println( "Adding "+args[i]+" to the Nodeattributes" );
        nodeAttributes.add( args[i] );
        i++;
      }
     

      // edge attributes
      else if ( Strings.isLike( args[i], "-j", 0, true ) ||
                Strings.isLike( args[i], "-edgeAttributes", 0, true ) ) {
        resetFalse();
        inEdge = true;
        i++;
        if ( badArgs(args, i ) ) 
          return;

        System.out.println( "Adding "+args[i]+" to the Edgeattributes" );
        edgeAttributes.add( args[i] );
        i++;
      }
     
      // plugins
      else if ( Strings.isLike( args[i], "-p", 0, true ) ||
                Strings.isLike( args[i], "-plugin", 0, true ) ||
                Strings.isLike( args[i], "--JLW", 0, true ) ||
                Strings.isLike( args[i], "--JLD", 0, true ) ||
                Strings.isLike( args[i], "--JLL", 0, true ) ) {
        resetFalse();
        inPlugin = true;
        i++;
        if ( badArgs(args, i ) ) 
          return;
        parsePluginArgs( args[i] );
        i++;
      } 

      
      //////////////////////////////
      // Continuation Catches

      else if ( inNode ) {
        System.out.println( "Adding "+args[i]+" to the Nodeattributes" );
        nodeAttributes.add( args[i] );
        i++;
      }
      else if ( inEdge ) {
        System.out.println( "Adding "+args[i]+" to the Edgeattributes" );
        edgeAttributes.add( args[i] );
        i++;
      }
      else if ( inExpression ) {
         System.out.println( "Adding "+args[i]+" to the expression data list");
        expressionFiles.add( args[i] );
        i++;
      }
      else if ( inGraph ) {
        System.out.println( "Adding "+args[i]+" to the Graph files" );
        graphFiles.add( args[i] );
        i++;
      } else if ( inPlugin ) {
        parsePluginArgs( args[i] );
        i++;
      }

      // no matches, exit and request help
      else {
        System.err.println( "nothing matches, call for help" );
        helpRequested = true;
        return;
      }

    }// end args for loop
  }

  private void parsePluginArgs ( String arg ) {
    
     File plugin_file = new File( arg );

        // test for directory
        if ( plugin_file.isDirectory() ) {
          // load a directory of plugins
          String[] fileList = plugin_file.list();
          String slashString="";
          if( !(plugin_file.getPath().endsWith("/") ) ) slashString="/";

          for(int j = 0; j < fileList.length; j++) {
            if(!(fileList[j].endsWith(".jar"))) continue;
            String jarString = plugin_file.getPath() + slashString + fileList[j];
            // jar file found, add to list of plugins to load.
            pluginFiles.add( jarString );
            System.out.println( "Plugin in directory added: "+jarString );
          }
        }

        // test for jar file
        else if ( plugin_file.toString().endsWith( ".jar" ) ) {
          // add single jar file
          pluginFiles.add( pluginFiles.toString() );
          System.out.println( "Adding single jar: "+plugin_file );
        } 
        
        // not a jar or directory, assume it is a manifest
        else {
          try {
            TextHttpReader reader = new TextHttpReader(plugin_file.toString());
            reader.read();
            String text = reader.getText();
            String lineSep = System.getProperty("line.separator");
            String[] allLines = text.split(lineSep);
            for (int j=0; j < allLines.length; j++) {
              String pluginLoc = allLines[j];
              
              if ( pluginLoc.endsWith( ".jar" ) ) {
                System.out.println( "Pluign found from manifest file: "+pluginLoc );
                pluginFiles.add( pluginLoc );
              }
            }
          } catch ( Exception exp ) {
            System.err.println( "error reading manifest file"+plugin_file );
          }
        }

  }


  /**
   * If the next argument is either null, or a flag, then this was a bad set of arguments
   * and we should just exit and print help.
   * @param args the command line arguments
   * @param i the counter;
   */
  private boolean badArgs ( String[] args, int i ) {
    
    // test to see if we are at then end of the arg list
    if ( args.length == i ) {
      System.out.println( "Too few arguments" );
      helpRequested = true;
      return true;
    } 
    // test to see if next is a flag
    else if ( args[i].startsWith( "-" ) ) {
      System.out.println( "Flag where there should be a value" );
      helpRequested = true;
      return true;
    } 
    return false;
  }



  private void resetFalse () {
    inGraph = false;
    inPlugin = false;
    inExpression = false;
    inNode = false;
    inEdge = false;
  }

} 
