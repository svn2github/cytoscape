package cytoscape;

import cytoscape.giny.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.ExpressionData;
import cytoscape.data.readers.*;
import cytoscape.data.servers.BioDataServer;

import giny.util.AbstractLayout;

import java.util.Map;
import java.util.HashMap;

public abstract class Cytoscape {
  
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

  protected static Map storedLayouts = new HashMap();;

  protected static GraphReader reader = null;


  //--------------------//
  // Root Graph Methods
  //--------------------//

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
   */
  public static CyNetwork getCurrentNetwork () {
    return null;
  }

  /**
   * Return a list of all available Networks
   */
  public static CyNetwork[] getNetworkList () {
    return null;
  }

  /**
    * Return the Inheritance Tree of Networks.
    * currently not implemented
    */
   public static javax.swing.JTree getNetworkTree () {
     return null;
   }
                                                 

  /**
   * Creates a new, empty Network. 
   */
  public static CyNetwork createNetwork () {
    return getRootGraph().createNetwork( new int[] {}, new int[] {} );
  }
  
 

  /**
   * Creates a new Network 
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   */
  public static CyNetwork createNetwork ( int[] nodes, int[] edges ) {
    return getRootGraph().createNetwork( nodes, edges );
  }
 
  /**
   * Creates a new Network 
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   */
  public static CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges ) {
    return getRootGraph().createNetwork( nodes, edges );
  }

  /**
   * Creates a new Network, that inherits from the given ParentNetwork
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   * @param param the parent of the this Network
   */
  public static CyNetwork createNetwork ( int[] nodes, int[] edges, CyNetwork parent ) {
    return getRootGraph().createNetwork( nodes, edges );
  }
 
  /**
   * Creates a new Network, that inherits from the given ParentNetwork
   * @param nodes the indeces of nodes
   * @param edges the indeces of edges
   * @param param the parent of the this Network
   */
  public static  CyNetwork createNetwork ( giny.model.Node[] nodes, giny.model.Edge[] edges, CyNetwork parent ) {
    return getRootGraph().createNetwork( nodes, edges );
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


}
