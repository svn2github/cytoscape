package cytoscape;

import cytoscape.giny.*;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.data.readers.*;
import cytoscape.data.servers.BioDataServer;

import giny.util.AbstractLayout;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;


public abstract class Cytoscape {
  
  public static String NETWORK_CREATED = "NETWORK_CREATED";
  public static String ATTRIBUTES_ADDED = "ATTRIBUTES_ADDED";
 

  /**
   * When creating a network, use one of the standard suffixes
   * to have it parsed correctly<BR>
   * <ul>
   * <li> sif -- Simple Interaction File</li>
   * <li> gml -- Graph Markup Languange</li>
   * <li> sbml -- SBML</li>
   * </ul>
   */
  public static int FILE_BY_SUFFIX = 0;
  public static int FILE_GML = 1;
  public static int FILE_SIF = 2;
  public static int FILE_SBML = 3;


  /**
   * The shared RootGraph between all Networks
   */
  protected static CytoscapeRootGraph cytoscapeRootGraph;
  
  /**
   * The NetworkData that stores node info
   */
  // TODO: replace seperate objects with one
  protected static GraphObjAttributes nodeData;

  /**
   * The NetworkData that stores edge info
   */
  // TODO: replace seperate objects with one
  protected static GraphObjAttributes edgeData;

  //TODO: remove, replace with NetworkData
  protected static ExpressionData expressionData;

 

  protected static GraphReader reader = null;

  protected static Object pcsO = new Object();
  protected static SwingPropertyChangeSupport pcs = new SwingPropertyChangeSupport( pcsO );


  protected static Map networkViewMap;
  protected static Map networkMap;

  protected static CytoscapeDesktop defaultDesktop;

  protected static String currentNetworkID;
  protected static String currentNetworkViewID;
  /**
   * A null CyNetwork to give when there is no Current Network
   */
  protected static CyNetwork nullNetwork = getRootGraph().
    createNetwork( new int[] {}, new int[] {} );
  
  /**
   * A null CyNetworkView to give when there is no Current NetworkView
   */
  protected static CyNetworkView nullNetworkView =  new PhoebeNetworkView ( nullNetwork, "null" );


  protected static CytoscapeObj cytoscapeobj;

  public static CytoscapeObj getCytoscapeObj () {
    return cytoscapeobj;
  }

  protected static void setCytoscapeObj ( CytoscapeObj obj ) {
    cytoscapeobj = obj;
  }

  //--------------------//
  // Root Graph Methods
  //--------------------//

  public static SwingPropertyChangeSupport getSwingPropertyChangeSupport () {
    return pcs;
  }

  /** 
   * Return the CytoscapeRootGraph
   */
  public static CytoscapeRootGraph getRootGraph () {
    if ( cytoscapeRootGraph == null ) 
      cytoscapeRootGraph = new CytoscapeRootGraph();
    
    return cytoscapeRootGraph;
  }
   
  /**
   * Ensure the capacity of Cytoscapce. This is to prevent
   * the inefficiency of adding nodes one at a time.
   */
  public static void ensureCapacity ( int nodes, int edges ) {
    getRootGraph().ensureCapacity( nodes, edges );
  }
     
  /**
   * @deprecated
   * WARNING: this should only be used under special circumstances.
   */
  public static void clearCytoscape () {
  
    int[] edges = getRootGraph().getEdgeIndicesArray();
    if ( edges == null ) {
      System.out.println( "Null Edges in clear" );
    } else {
      getRootGraph().removeEdges( getRootGraph().getEdgeIndicesArray() );
    }

    getRootGraph().removeNodes( getRootGraph().getNodeIndicesArray() );
  }


  //--------------------//
  // Network Methods
  //--------------------//

  /**
   * Return the Network that currently has the Focus.
   * Can be different from getCurrentNetworkView
   */
  public static CyNetwork getCurrentNetwork () {
    if ( currentNetworkID == null ) 
      return nullNetwork;
    
    CyNetwork network = ( CyNetwork )getNetworkMap().get( currentNetworkID );
    return network;
  }

  /**
   * Return a List of all available CyNetworks
   */
  public static List getNetworkList () {
    return new ArrayList( getNetworkMap().keySet() );
  }

  /**
   * Return the CyNetworkView that currently has the focus.
   * Can be different from getCurrentNetwork 
   */ 
  public static CyNetworkView getCurrentNetworkView () {
    if ( currentNetworkViewID == null ) 
      return nullNetworkView;

    //    System.out.println( "Cytoscape returning current network view: "+currentNetworkViewID );

    CyNetworkView nview =  ( CyNetworkView )getNetworkViewMap().get( currentNetworkViewID );
    return nview;
  }

  
    

  public static CytoscapeDesktop getDesktop() {
    if ( defaultDesktop == null ) {
      //System.out.println( " cytoscape.Cytoscape requested Desktop: "+defaultDesktop );
      defaultDesktop = new CytoscapeDesktop( getCytoscapeObj().getConfiguration().getViewType() );
    }
    return defaultDesktop;
  }

  /**
   * @deprecated
   */
  public static void setCurrentNetwork ( String id) {
    if ( networkMap.containsKey( id ) )
      currentNetworkID = id;
  }

  /**
   * @deprecated
   */
  public static void setCurrentNetworkView ( String id) {
    if ( networkViewMap.containsKey( id ) )
      currentNetworkViewID = id;
  }


  
  protected static Map getNetworkMap () {
    if ( networkMap == null ) {
      networkMap = new HashMap();
    }
    return networkMap;
  }

  protected static Map getNetworkViewMap () {
    if ( networkViewMap == null ) {
      networkViewMap = new HashMap();
    }
    return networkViewMap;
  }


 

  protected static void addNetwork ( CyNetwork network ) {

    // System.out.println( "CyNetwork Added: "+network.getIdentifier() );

    getNetworkMap().put( network.getIdentifier(), network );
   
    
    firePropertyChange( NETWORK_CREATED,
                        null,
                        network.getIdentifier() );


    createNetworkView( network );
  }

  /**
   * Creates a new, empty Network. 
   */
  public static CyNetwork createNetwork () {
    CyNetwork network =  getRootGraph().createNetwork( new int[] {}, new int[] {} );
    addNetwork( network );
    return network;
  }
  
 

  /**
   * Creates a new Network 
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   */
  public static CyNetwork createNetwork ( int[] nodes, int[] edges ) {
    CyNetwork network = getRootGraph().createNetwork( nodes, edges );
    addNetwork( network );
    return network;
  }
 
  /**
   * Creates a new Network 
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   */
  public static CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) {
    CyNetwork network = getRootGraph().createNetwork( nodes, edges );
    addNetwork( network );
    return network;
  }

  /**
   * Creates a new Network, that inherits from the given ParentNetwork
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   * @param param the parent of the this Network
   */
  public static CyNetwork createNetwork ( int[] nodes, int[] edges, CyNetwork parent ) {
    CyNetwork network = getRootGraph().createNetwork( nodes, edges );
    addNetwork( network );
    return network;
  }
 
  /**
   * Creates a new Network, that inherits from the given ParentNetwork
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   * @param param the parent of the this Network
   */
  public static  CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges, CyNetwork parent ) {
    CyNetwork network = getRootGraph().createNetwork( nodes, edges );
    addNetwork( network );
    return network;
  }



  /**
   * Creates a cytoscape.data.CyNetwork from a file. The file type is determined by 
   * the suffice of the file
   * <ul>
   * <li> sif -- Simple Interaction File</li>
   * <li> gml -- Graph Markup Languange</li>
   * <li> sbml -- SBML</li>
   * </ul>
   * @param location the location of the file
   */
  public static  CyNetwork createNetwork ( String location ) {
    return createNetwork( location, FILE_BY_SUFFIX, false, null, null );
  }

  /**
   * Creates a cytoscape.data.CyNetwork from a file.  The passed variable determines the
   * type of file, i.e. GML, SIF, SBML, etc.
   *
   * @param location the location of the file
   * @param file_type the type of file GML, SIF, SBML, etc.
   * @param canonicalize this will set the preferred display name to what is
   *                     on the server.
   * @param biodataserver provides the name conversion service
   * @param species  the species used by the BioDataServer
   */
  public static CyNetwork createNetwork ( String location,
                                          int file_type,
                                          boolean canonicalize, 
                                          BioDataServer biodataserver, 
                                          String species ) {
    // return null for a null file
    if ( location == null )  
      return null;

    

    //set the reader according to what file type was passed.
    if ( file_type == FILE_SIF 
         || ( file_type == FILE_BY_SUFFIX && location.endsWith( "sif" ) ) ) {
      reader = new InteractionsReader( biodataserver, 
                                       species,
                                       location );
    } else if ( file_type == FILE_GML
                || ( file_type == FILE_BY_SUFFIX && location.endsWith( "gml" ) ) ) {
      reader = new GMLReader( location );
    } else {
      // TODO: come up with a really good way of supporting arbitrary 
      // file types via plugin support.
      System.err.println( "File Type not Supported, sorry" );
      return Cytoscape.createNetwork();
    }

    // have the GraphReader read the give file
    try {
      reader.read();
    } catch ( Exception e ) {
      System.err.println( "Cytoscape: Error Reading Graph File: "+location+"\n--------------------\n" );
      e.printStackTrace();
      return null;
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
    return createNetwork( nodes, edges );
  }

  /**
   * @deprecated
   */
  public static GraphReader getLastGraphReaderForDoingLayout () {
    return reader;
  }


  //--------------------//
  // Network Data Methods
  //--------------------//
  
  /**
   * @deprecated
   * This should not be used by any user-code
   */
  public static GraphObjAttributes getNodeNetworkData () {
    if ( nodeData == null ) 
      nodeData = new GraphObjAttributes();
    return nodeData;
  }

  /**
   * @deprecated
   * This should not be used by any user-code
   */
  public static GraphObjAttributes getEdgeNetworkData () {
    if ( edgeData == null ) 
      edgeData = new GraphObjAttributes();
    return edgeData;
  }

  /**
   * Load Expression Data
   */
  public static void loadExpressionData ( String filename ) {
   //  try {
//       expressionData = new ExpressionData( filename );
//     } catch (Exception e) {
//       System.err.println( "Unable to Load Expression Data" );
//     }
  
//      if (validLoad) {
//         String callerID = "LoadExpressionMatrixAction.actionPerformed";
//         networkView.getNetwork().beginActivity(callerID);
//         networkView.getNetwork().setExpressionData(newData);
//         // rather than depend on the configuration file,
//         // depend on the ExpFileChooser's checkbox.
//         //if(config.getWhetherToCopyExpToAttribs()) {
//         if(chooser.getWhetherToCopyExpToAttribs()) {
//           newData.copyToAttribs(networkView.getNetwork().getNodeAttributes());
//           //graph appearances may depend on expression data attributes
//           networkView.redrawGraph(false, true);
//         }
//         networkView.getNetwork().endActivity(callerID);
//         //display a description of the data in a dialog
//         String expDescript = newData.getDescription();
//         String title = "Load Expression Data";
//         JOptionPane.showMessageDialog(networkView.getMainFrame(),
//                                       expDescript, title,
//                                       JOptionPane.PLAIN_MESSAGE);
//       } else {
//         //show an error message in a dialog
//         String errString = "Unable to load expression data from "
//           + expDataFilename;
//         String title = "Load Expression Data";
//         JOptionPane.showMessageDialog(networkView.getMainFrame(),
//                                       errString, title,
//                                       JOptionPane.ERROR_MESSAGE);
//       }

  }

  /**
   * Loads Node and Edge attribute data into Cytoscape from the given
   * file locations. Currently, the only supported attribute types are
   * of the type "name = value".
   *
   * @param nodeAttrLocations  an array of node attribute file locations. May be null.
   * @param edgeAttrLocations  an array of edge attribute file locations. May be null.
   * @param canonicalize  convert to the preffered name on the biodataserver
   * @param bioDataServer  provides the name conversion service
   * @param species  the species to use with the bioDataServer's
   */
  public static void loadAttributes ( String[] nodeAttrLocations,
                                      String[] edgeAttrLocations,
                                      boolean canonicalize,
                                      BioDataServer bioDataServer, 
                                      String species ) {
    
    // check to see if there are Node Attributes passed
    if ( nodeAttrLocations != null ) {
	    
	    for ( int i = 0 ; i < nodeAttrLocations.length; ++i ) {
        try {
          nodeData.readAttributesFromFile( bioDataServer, 
                                           species,
                                           nodeAttrLocations[i],
                                           canonicalize );
        } catch (Exception e) {
          System.err.println( "Error loading attributes into NodeData" );
          // e.printStackTrace();
        }
	    }
    }
    
    // Check to see if there are Edge Attributes Passed
    if (edgeAttrLocations != null) {

	    for ( int  j = 0 ; j < edgeAttrLocations.length; ++j ) {
        try {
          edgeData.readAttributesFromFile( edgeAttrLocations[j] );
        } catch (Exception e) {
          System.err.println( "Error loading attributes into EdgeData" );
          //e.printStackTrace();
        }
	    }
    }

    firePropertyChange( ATTRIBUTES_ADDED,
                        null,
                        null );

  }

  /**
   * Loads Node and Edge attribute data into Cytoscape from the given
   * file locations. Currently, the only supported attribute types are
   * of the type "name = value".
   *
   * @param nodeAttrLocations  an array of node attribute file locations. May be null.
   * @param edgeAttrLocations  an array of edge attribute file locations. May be null.
   */
  public static void loadAttributes( String[] nodeAttrLocations,
                                     String[] edgeAttrLocations ) {
    loadAttributes( nodeAttrLocations, edgeAttrLocations, false, null, null );
  }
  

      
  /**
   * Constructs a network using information from a CyProject argument that
   * contains information on the location of the graph file, any node/edge
   * attribute files, and a possible expression data file.
   * If the data server argument is non-null and the project requests
   * canonicalization, the data server will be used for name resolution
   * given the names in the graph/attributes files.
   *
   * @see CyProject
   */
  public static CyNetwork createNetworkFromProject( CyProject project,
                                                    BioDataServer bioDataServer) {
    if (project == null) {return null;}
    
    boolean canonicalize = project.getCanonicalize();
    String species = project.getDefaultSpeciesName();
    CyNetwork network = null;
    if (project.getInteractionsFilename() != null) {
	    //read graph from interaction data
	    String filename = project.getInteractionsFilename();
	    network = createNetwork( filename, 
                               Cytoscape.FILE_SIF,
                               canonicalize,
                               bioDataServer, 
                               species); 
    }
    else if (project.getGeometryFilename() != null) {
	    //read a GML file
	    String filename = project.getGeometryFilename();
	    network = createNetwork( filename,
                               Cytoscape.FILE_GML,
                               false,
                               null, 
                               null);

    }

    if (network == null) {//no graph specified, or unable to read
	    //create a default network
	    network = createNetwork();
    }
	
    //load attributes files
    String[] nodeAttributeFilenames = project.getNodeAttributeFilenames();
    String[] edgeAttributeFilenames = project.getEdgeAttributeFilenames();
    loadAttributes( nodeAttributeFilenames, 
                    edgeAttributeFilenames,
                    canonicalize, 
                    bioDataServer, 
                    species);
	
    //load expression data
    ExpressionData expData = null;
    if (project.getExpressionFilename() != null) {
	    expData = new ExpressionData( project.getExpressionFilename() );
	    network.setExpressionData(expData);
    }
	
    return network;
  }

  /**
   * A BioDataServer should be loadable from a file systems file 
   * or from a URL.
   */
  public static void loadBioDataServer ( String location ) {
   //  try {
//       bioDataServer = new BioDataServer (bioDataDirectory);
//       getCytoscapeObj().setBioDataServer(bioDataServer);
//     } catch ( Exception e ) {
//       String es = "cannot create new biodata server at " + bioDataDirectory;
//       getCytoscapeObj().getLogger().warning(es);
//       return;
//     }
  }

  //------------------------------//
  // CyNetworkView Creation Methods
  //------------------------------//
  
  /**
   * Creates a CyNetworkView, but doesn't do anything with it.
   * Ifnn's you want to use it @link {CytoscapeDesktop}
   * @param network the network to create a view of
  */
  public static CyNetworkView createNetworkView ( CyNetwork network ) {
     return createNetworkView( network, network.getIdentifier() );
  }
  
  /**
   * Creates a CyNetworkView, but doesn't do anything with it.
   * Ifnn's you want to use it @link {CytoscapeDesktop}
   * @param network the network to create a view of
   */
  //TODO: title
  public static CyNetworkView createNetworkView ( CyNetwork network, String title ) {
     
    CyNetworkView view = new PhoebeNetworkView ( network, title );
    getNetworkViewMap().put( network.getIdentifier(), view );

    // System.out.println( "Just Created a PhoebeNetworkView: "+network.getIdentifier()+
    //                     " and it should be in the networkViewMap: "+
    //                     getNetworkViewMap().get( network.getIdentifier() ) );


     firePropertyChange( cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED,
                         null,
                         view );
     return view;
  }
  


  public static CyNetworkView getNetworkView ( String network_id ) {
    // System.out.println( "Getting Network View for: "+network_id );
    return ( CyNetworkView )getNetworkViewMap().get( network_id );
  }





  protected static void firePropertyChange ( String property_type,
                                             Object old_value,
                                             Object new_value ) {

    PropertyChangeEvent e = new PropertyChangeEvent( pcsO, property_type, old_value, new_value );
    // System.out.println( "Cytoscape FIRING : "+property_type );

    getSwingPropertyChangeSupport().firePropertyChange( e );
  }

}
