package cytoscape;

import java.util.ArrayList;
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
  boolean helpRequested; // though this should be handled via CyMain
  ArrayList edgeAttributes = new ArrayList();
  boolean inEdge = false;
  ArrayList nodeAttributes = new ArrayList();
  boolean inNode = false;
  String species;
  
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

    int i = 0;
    System.out.println( i +" "+args+" "+args.length );
    while ( i < args.length ) {
    //for ( int i = 0; i < args.length; ) {
      System.out.println( i+". "+args[i] );
      // BioDataServer
      if ( Strings.isLike( args[i], "-b",0, true ) ||
           Strings.isLike( args[i], "-bioDataServer",0, true ) ||
           Strings.isLike( args[i], "-BDS",0, true ) ) {
        i++;
        System.out.println( "BDS: "+args[i] );
        bioDataServer = args[i];
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
        System.out.println( "Adding "+args[i]+" to the Graph files" );
        graphFiles.add( args[i] );
        i++;
      }
     

      // expression files
      else if ( Strings.isLike( args[i], "-e", 0, true ) ||
                Strings.isLike( args[i], "-expression", 0, true ) ) {
        resetFalse();
        i++;
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
        System.out.println( "Adding "+args[i]+" to the Edgeattributes" );
        edgeAttributes.add( args[i] );
        i++;
      }
     
      
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
      }

    }// end args for loop
  }

  private void resetFalse () {
    inGraph = false;
    inExpression = false;
    inNode = false;
    inEdge = false;
  }

} 
