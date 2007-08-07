package cytoscape.init;

import cytoscape.CytoscapeInit;
import java.io.*;
import java.util.*;
/**
 * Read the variuos properties files, and remember where they came from, and
 * in whic order they were read.
 */
public class CyPropertiesReader {

  Properties cytoscapeProps;
  String propertiesLocation;
  
  public CyPropertiesReader () {
  }

  /**
   * @return the Properties loaded
   */
  public Properties getProperties () {
    return cytoscapeProps;
  }

  /**
   * @return the location of the Properties loaded
   */
  public String getPropertiesLocation () {
    return propertiesLocation;
  }

  /**
   * When reading properties, only *one* cytoscape.props/vizmap.props file 
   * will actually be read, and any modifications made to it, will be saved back to that 
   * same file.
   * 
   * The order is:
   * 1. specified location
   * 2. `pwd`/cytoscape.props
   * 3. CYTOSCAPE_HOME/cytoscape.props
   * 4. ~/.cytoscape/cytoscape.props
   */
  public void readProperties ( String specified_props_file_location )   {

    boolean propsFound = false;
    cytoscapeProps = new Properties();
    //1. Try the specified location for a props file
    if ( specified_props_file_location != null ) { 
      try {
        File file = new File( specified_props_file_location );
        cytoscapeProps.load( new FileInputStream( file ) );
        propertiesLocation = specified_props_file_location;
        propsFound = true;
      } catch ( Exception e ) {
        // error
        propsFound = false;
      }
    }

    //2. Try the current working directory
    if ( !propsFound ) {
      try {
        File file = new File( System.getProperty ("user.dir"), "cytoscape.props" );
        cytoscapeProps.load( new FileInputStream( file ) );
        propertiesLocation = file.toString();
        propsFound = true;
      } catch ( Exception e ) {
        // error
        propsFound = false;
      }
    }
     
    //3. Try CYTOSCAPE_HOME
    if ( !propsFound ) {
      try {
        File file = new File( System.getProperty ("CYTOSCAPE_HOME"), "cytoscape.props" );
        cytoscapeProps.load( new FileInputStream( file ) );
        propertiesLocation = file.toString();
        propsFound = true;
      } catch ( Exception e ) {
        // error
        propsFound = false;
      }
    }

    //4. Try ~/.cytoscape
    if ( !propsFound ) {
      try {
        File file = CytoscapeInit.getConfigFile( "cytoscape.props" );
        cytoscapeProps.load( new FileInputStream( file ) );
        propertiesLocation = file.toString();
        propsFound = true;
      } catch ( Exception e ) {
        // error
        propsFound = false;
      }
    }

    System.out.println( "Cytoscapeprops found: "+propertiesLocation );

    if ( cytoscapeProps == null ) {
      cytoscapeProps = new Properties();
    }


  } // readProperties
 
  protected void saveProperties () {
    
     // if ( defaultSpeciesName != null )
   //      props.setProperty("defaultSpeciesName", defaultSpeciesName );
//     if ( viewType != null )
//       props.setProperty("viewType", viewType );
//     if ( viewThreshold != null )
//       props.setProperty("viewThreshold", viewThreshold.toString() );
//     if ( bioDataDirectory != null )
//       props.setProperty( "bioDataDirectory", bioDataDirectory );
//     if ( defaultVisualStyle != null )
//       props.setProperty( "defaultVisualStyle", Cytoscape.getDesktop().getVizMapManager().getVisualStyle().getName() );
//     props.setProperty( "currentDirectory", Cytoscape.getCytoscapeObj().getCurrentDirectory().getAbsolutePath() );

//     try {
//       File file = Cytoscape.getCytoscapeObj().getConfigFile( "cytoscape.props" );

//       FileOutputStream output = new FileOutputStream( file );
//       props.store( output, "Cytoscape Property File" );

//     } catch ( Exception ex ) {
//       System.out.println( "Cytoscape.Props Write error" );
//       ex.printStackTrace();
//     }
  }



}
